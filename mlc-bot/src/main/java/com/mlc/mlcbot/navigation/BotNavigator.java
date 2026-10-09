// Standalone adaptation of Citizens2 AStarNavigationStrategy's incremental planner and waypoint follower.
// Licensed under OSL-3.0; see META-INF/mlc-bot/CITIZENS-LICENSE.txt.
package com.mlc.mlcbot.navigation;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/** Bounded main-thread A*: loaded chunks only, one-block jumps and up to three-block drops. */
public final class BotNavigator {
    private static final int BUDGET_PER_TICK = 96;
    private static final int MAX_VISITED = 2048;
    private static final int RANGE = 24;
    private static final int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    private final ArrayDeque<Cell> path = new ArrayDeque<>();
    private Search search;
    private Cell destination;
    private long nextRepath;
    private final Terrain terrain;

    public BotNavigator() { terrain = null; }

    BotNavigator(Terrain terrain) { this.terrain = terrain; }

    interface Terrain {
        boolean canStand(int x, int y, int z);
        boolean passable(int x, int y, int z);
    }

    private static Terrain worldTerrain(World world) {
        return new Terrain() {
            public boolean canStand(int x, int y, int z) { return standable(world, new Cell(x, y, z)); }
            public boolean passable(int x, int y, int z) {
                return y >= world.getMinHeight() && y < world.getMaxHeight()
                        && world.isChunkLoaded(x >> 4, z >> 4) && world.getBlockAt(x, y, z).isPassable();
            }
        };
    }

    public Vector direction(Location at, Location target, long tick) {
        Cell start = Cell.at(at), goal = Cell.at(target);
        Terrain source = terrain == null ? worldTerrain(at.getWorld()) : terrain;
        if (tick >= nextRepath && (destination == null || destination.distanceSquared(goal) > 4
                || search == null && path.isEmpty())) {
            destination = goal;
            search = new Search(source, start, goal);
            path.clear();
            nextRepath = tick + 10;
        }
        if (search != null) {
            if (search.tick()) {
                path.addAll(search.result());
                search = null;
            }
        }
        while (!path.isEmpty()) {
            Cell waypoint = path.peek();
            if (!source.canStand(waypoint.x, waypoint.y, waypoint.z)) {
                stop();
                return new Vector();
            }
            double dx = waypoint.x + 0.5 - at.getX(), dz = waypoint.z + 0.5 - at.getZ();
            if (dx * dx + dz * dz < 0.2 && Math.abs(waypoint.y - at.getY()) < 0.6) {
                path.remove();
                continue;
            }
            Vector direction = new Vector(dx, 0, dz);
            return direction.lengthSquared() < 0.001 ? new Vector() : direction.normalize();
        }
        return new Vector();
    }

    public boolean shouldJump(Location at) {
        return !path.isEmpty() && path.peek().y > at.getY() + 0.4;
    }

    public void stop() {
        search = null;
        path.clear();
        destination = null;
    }

    public static boolean canStand(Location at) {
        return standable(at.getWorld(), Cell.at(at));
    }

    private static boolean standable(World world, Cell cell) {
        if (cell.y <= world.getMinHeight() || cell.y + 1 >= world.getMaxHeight()
                || !world.isChunkLoaded(cell.x >> 4, cell.z >> 4)) return false;
        Block feet = world.getBlockAt(cell.x, cell.y, cell.z);
        Block head = feet.getRelative(0, 1, 0), floor = feet.getRelative(0, -1, 0);
        return feet.isPassable() && head.isPassable() && floor.getType().isSolid()
                && !hazard(feet.getType()) && !hazard(head.getType()) && !hazard(floor.getType())
                && !floor.getCollisionShape().getBoundingBoxes().isEmpty();
    }

    private static boolean hazard(Material type) {
        return switch (type) {
            case LAVA, FIRE, SOUL_FIRE, CACTUS, MAGMA_BLOCK, SWEET_BERRY_BUSH,
                    POWDER_SNOW, CAMPFIRE, SOUL_CAMPFIRE, WITHER_ROSE -> true;
            default -> false;
        };
    }

    private record Cell(int x, int y, int z) {
        static Cell at(Location at) { return new Cell(at.getBlockX(), at.getBlockY(), at.getBlockZ()); }
        int distanceSquared(Cell other) {
            int dx = x - other.x, dy = y - other.y, dz = z - other.z;
            return dx * dx + dy * dy + dz * dz;
        }
        double heuristic(Cell goal) { return Math.hypot(x - goal.x, z - goal.z) + Math.abs(y - goal.y); }
    }

    private record Entry(Cell cell, double cost, double estimate) { }

    private static final class Search {
        final Terrain terrain;
        final Cell start, goal;
        final PriorityQueue<Entry> open = new PriorityQueue<>(Comparator.comparingDouble(Entry::estimate));
        final Map<Cell, Double> costs = new HashMap<>();
        final Map<Cell, Cell> parents = new HashMap<>();
        final Set<Cell> closed = new HashSet<>();
        Cell best;

        Search(Terrain terrain, Cell start, Cell goal) {
            this.terrain = terrain;
            this.start = start;
            this.goal = goal;
            best = start;
            costs.put(start, 0.0);
            open.add(new Entry(start, 0, start.heuristic(goal)));
        }

        boolean tick() {
            for (int step = 0; step < BUDGET_PER_TICK; step++) {
                if (open.isEmpty() || closed.size() >= MAX_VISITED) return true;
                Entry current = open.remove();
                Cell cell = current.cell;
                if (!closed.add(cell)) continue;
                if (cell.heuristic(goal) < best.heuristic(goal)) best = cell;
                if (cell.distanceSquared(goal) <= 1) { best = cell; return true; }
                for (int[] offset : DIRECTIONS) {
                    for (int dy : new int[]{0, 1, -1, -2, -3}) {
                        Cell next = new Cell(cell.x + offset[0], cell.y + dy, cell.z + offset[1]);
                        if (Math.abs(next.x - start.x) > RANGE || Math.abs(next.z - start.z) > RANGE
                                || !terrain.canStand(next.x, next.y, next.z)) continue;
                        // Ascending requires room above our head; descending must have a clear vertical column.
                        if (dy > 0 && !terrain.passable(cell.x, cell.y + 2, cell.z)) continue;
                        boolean clear = true;
                        for (int y = next.y + 2; y <= cell.y + 1; y++) {
                            if (!terrain.passable(next.x, y, next.z)) { clear = false; break; }
                        }
                        if (!clear) continue;
                        double cost = current.cost + 1 + Math.abs(dy) * 0.5;
                        if (cost < costs.getOrDefault(next, Double.POSITIVE_INFINITY)) {
                            costs.put(next, cost);
                            parents.put(next, cell);
                            open.add(new Entry(next, cost, cost + next.heuristic(goal)));
                        }
                        break;
                    }
                }
            }
            return false;
        }

        ArrayDeque<Cell> result() {
            ArrayDeque<Cell> result = new ArrayDeque<>();
            for (Cell cell = best; cell != null && !cell.equals(start); cell = parents.get(cell)) {
                result.addFirst(cell);
            }
            return result;
        }
    }
}

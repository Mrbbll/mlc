package com.mlc.mlcdomain.hocks.bluemap;
import com.mlc.mlcdomain.dataManager.Databasemanager;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.ShapeMarker;
import de.bluecolored.bluemap.api.math.Color;
import de.bluecolored.bluemap.api.math.Shape;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Objects;
import java.util.Optional;

import static com.mlc.mlcdomain.Mlcdomain.instance;

public class Bluemapapi {
    public static void init(){

        // Directly getting the api (wrapped in an Optional)
        //直接获取api（包装在Optional中）
        Optional<BlueMapAPI> optionalApi = BlueMapAPI.getInstance();

        // Directly using the API if it is enabled
        BlueMapAPI.getInstance().ifPresent(api -> {
            instance.getLogger().info("BlueMapAPI已挂钩 !");
            //code executed when the api is enabled (skipped if the api is not enabled)
        });


        // Using a listener to do something as soon as the API is available
        BlueMapAPI.onEnable(api -> {
            // 创建领地标记集
            createMarkerSet(api);
            // 创建领地标记
            // 遍历所有领地，创建标记
            Objects.requireNonNull(Databasemanager.getDomainDataList()).forEach(domainData -> {
                createDomainMarker(
                        domainData.getDomain(),
                        domainData.getPlayerName(),
                        Bukkit.getWorld(domainData.getWorld()),
                        domainData.getX(),
                        domainData.getZ(),
                        domainData.getLevel()
                );
            });

            //code executed when the api got enabled
        });

        BlueMapAPI.onDisable(api -> {
            //code executed right before the api gets disabled
        });
    }
    /**
     * 创建领地标记集
     */
    private static void createMarkerSet(BlueMapAPI api) {
        // 为每个世界创建标记集
        api.getWorlds().forEach(world -> {
            MarkerSet markerSet = MarkerSet.builder()
                    .label("玩家领地")
                    .defaultHidden(false)
                    .build();
            world.getMaps().forEach(map -> {
                map.getMarkerSets().put("玩家领地", markerSet);
            });
        });


    }

    /**
     * 创建领地标记
     */
    public static void createDomainMarker(String domainName, String ownerName, World world, int chunkX, int chunkZ, int level) {
        BlueMapAPI.getInstance().ifPresent(api -> {
            try {
                // 获取对应的BlueMap世界
                Optional<BlueMapWorld> blueMapWorld = api.getWorld(world.getName());
                if (blueMapWorld.isEmpty()) return;

                // 计算区块边界坐标
                int minX = chunkX * 16;
                int minZ = chunkZ * 16;
                int maxX = minX + 16;
                int maxZ = minZ + 16;

                // 创建形状（矩形区域）
                Shape shape = Shape.createRect(minX, minZ, maxX, maxZ);

                // 根据领地等级设置颜色
                Color color = switch (level) {
                    case 0 -> new Color(0, 255, 0,0.2f);
                    case 1 -> new Color(0, 255, 203,0.2f);
                    case 2 -> new Color(251, 0, 255,0.2f);
                    case 3 -> new Color(255, 60, 0,0.2f);
                    default -> new Color(0, 255, 0,0.2f);
                };

                // 创建标记ID（使用世界名和区块坐标确保唯一性）
                String markerId = "domain_" + world.getName() + "_" + chunkX + "_" + chunkZ;

                // 创建形状标记
                ShapeMarker marker = ShapeMarker.builder()
                        .label(domainName + " - " + ownerName)
                        .detail("领地所有者: " + ownerName + " 名称：" + domainName + " 权限等级: " + level + " 区块坐标: " + chunkX + ", " + chunkZ)
                        .shape(shape, 64) // 高度设为64（地面）
                        .lineColor(color)
                        .fillColor(color)
                        .lineWidth(2)
                        .lineWidth(1)
                        .depthTestEnabled(false)
                        .build();

                // 添加到标记集
                blueMapWorld.get().getMaps().forEach(map -> {
                    MarkerSet markerSet = map.getMarkerSets().get("玩家领地");
                    if (markerSet != null) {
                        markerSet.getMarkers().put(markerId, marker);
                    }
                });

//                instance.getLogger().info("已为领地 " + domainName + " 创建BlueMap标记");

            } catch (Exception e) {
                instance.getLogger().warning("创建BlueMap标记失败: " + e.getMessage());
            }
        });
    }
    public static void removeDomainMarker(String domainName, World world, int chunkX, int chunkZ) {
        BlueMapAPI.getInstance().ifPresent(api -> {
            try {
                // 获取对应的BlueMap世界
                Optional<BlueMapWorld> blueMapWorld = api.getWorld(world.getName());
                if (blueMapWorld.isEmpty()) return;

                // 创建标记ID（使用世界名和区块坐标确保唯一性）
                String markerId = "domain_" + world.getName() + "_" + chunkX + "_" + chunkZ;

                // 从所有地图中移除标记
                blueMapWorld.get().getMaps().forEach(map -> {
                    MarkerSet markerSet = map.getMarkerSets().get("玩家领地");
                    if (markerSet != null) {
                        markerSet.getMarkers().remove(markerId);
                    }
                });

                instance.getLogger().info("已移除BlueMap标记: " + markerId);

            } catch (Exception e) {
                instance.getLogger().warning("移除BlueMap标记失败: " + e.getMessage());
            }
        });
    }
    // 更新地图上的领地标记
    public static void updateDomainMarker(String domainName, String ownerName, World world, int chunkX, int chunkZ, int level) {
        removeDomainMarker(domainName, world, chunkX, chunkZ);
        createDomainMarker(domainName, ownerName, world, chunkX, chunkZ, level);
    }

}


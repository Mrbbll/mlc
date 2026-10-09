// Adapted from Citizens2; licensed under OSL-3.0. See META-INF/mlc-bot/CITIZENS-LICENSE.txt.
package com.mlc.mlcbot.nms;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class EmptyPacketListener extends ServerGamePacketListenerImpl {
    public EmptyPacketListener(MinecraftServer minecraftServer, Connection networkManager, ServerPlayer entityPlayer,
            CommonListenerCookie clc) {
        super(minecraftServer, networkManager, entityPlayer, clc);
    }

    @Override
    public void resumeFlushing() {
    }

    @Override
    public void tick() {
        // Folia ticks this (no-op)
    }

    @Override
    public void send(Packet<?> packet) {
    }
}

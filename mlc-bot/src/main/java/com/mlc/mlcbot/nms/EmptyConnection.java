// Adapted from Citizens2 v26_3_R1/network/EmptyConnection.java (OSL-3.0).
package com.mlc.mlcbot.nms;

import java.net.SocketAddress;
import java.lang.reflect.Field;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

public final class EmptyConnection extends Connection {
    public EmptyConnection() {
        super(PacketFlow.CLIENTBOUND);
        channel = new EmptyChannel(null);
        address = new SocketAddress() { private static final long serialVersionUID = 1L; };
    }

    @Override public void flushChannel() { }
    @Override public boolean isConnected() { return true; }
    @Override public void send(Packet<?> packet) { }
    @Override public void send(Packet<?> packet, ChannelFutureListener listener) { }
    @Override public void send(Packet<?> packet, ChannelFutureListener listener, boolean flush) { }

    @Override
    public void setListenerForServerboundHandshake(PacketListener listener) {
        // Citizens uses the same two setters; keep them local instead of importing its NMS singleton.
        setField(Connection.class, this, "packetListener", listener);
        setField(Connection.class, this, "disconnectListener", null);
    }

    static void setField(Class<?> declaringClass, Object instance, String name, Object value) {
        try {
            Field field = declaringClass.getDeclaredField(name);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unsupported Paper 26.3 NMS field: " + name, e);
        }
    }
}

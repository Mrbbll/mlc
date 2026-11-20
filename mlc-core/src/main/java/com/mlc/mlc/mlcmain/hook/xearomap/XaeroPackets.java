package com.mlc.mlc.mlcmain.hook.xearomap;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Xaero v3 payload format, adapted from XaeroTracker (see META-INF/licenses/XaeroTracker.txt).
 */
final class XaeroPackets {
    private static final byte HANDSHAKE_TYPE = 1;
    private static final int NETWORK_VERSION = 3;
    static final byte[] HANDSHAKE = intPacket(HANDSHAKE_TYPE, NETWORK_VERSION);
    static final byte[] RESET = {3};

    private XaeroPackets() {
    }

    static boolean isHandshake(byte[] payload) {
        return payload.length >= HANDSHAKE.length
                && payload[0] == HANDSHAKE_TYPE
                && ByteBuffer.wrap(payload, 1, Integer.BYTES).getInt() == NETWORK_VERSION;
    }

    static byte[] levelId(int levelId) {
        return intPacket((byte) 0, levelId);
    }

    private static byte[] intPacket(byte type, int value) {
        return ByteBuffer.allocate(1 + Integer.BYTES).put(type).putInt(value).array();
    }

    // A null position encodes removal. The network NBT root is unnamed.
    static byte[] marker(UUID id, Position position) {
        var bytes = new ByteArrayOutputStream(96);
        try (var data = new DataOutputStream(bytes)) {
            data.writeByte(2);
            data.writeByte(10);
            data.writeByte(1);
            data.writeUTF("r");
            data.writeBoolean(position == null);
            data.writeByte(11);
            data.writeUTF("i");
            data.writeInt(4);
            data.writeLong(id.getMostSignificantBits());
            data.writeLong(id.getLeastSignificantBits());
            if (position != null) {
                writeCoordinate(data, "x", position.x());
                writeCoordinate(data, "y", position.y());
                writeCoordinate(data, "z", position.z());
                data.writeByte(8);
                data.writeUTF("d");
                data.writeUTF(position.dimension());
            }
            data.writeByte(0);
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not encode Xaero player marker", exception);
        }
        return bytes.toByteArray();
    }

    private static void writeCoordinate(DataOutputStream data, String name, double coordinate) throws IOException {
        data.writeByte(6);
        data.writeUTF(name);
        data.writeDouble(coordinate);
    }

    record Position(double x, double y, double z, String dimension) {
    }
}

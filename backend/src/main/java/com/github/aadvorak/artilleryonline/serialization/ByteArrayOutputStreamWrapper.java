package com.github.aadvorak.artilleryonline.serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ByteArrayOutputStreamWrapper {

    private static final boolean FLOAT_PRECISION = true;

    private final ByteArrayOutputStream stream;

    public ByteArrayOutputStreamWrapper() {
        stream = new ByteArrayOutputStream();
    }

    public void writeBoolean(boolean value) {
        stream.write(value? 1 : 0);
    }

    public void writeInt(int value) {
        try {
            stream.write(intToBytes(value));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeLong(long value) {
        try {
            stream.write(longToBytes(value));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeFloat(float value) {
        try {
            stream.write(floatToBytes(value));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeDouble(double value) {
        if (FLOAT_PRECISION) {
            writeFloat((float) value);
            return;
        }
        try {
            stream.write(doubleToBytes(value));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeString(String value) {
        var bytes = value.getBytes(StandardCharsets.UTF_8);
        writeInt(bytes.length);
        stream.writeBytes(bytes);
    }

    public void writeSerializable(CompactSerializable serializable) {
        writeNullable(serializable, this::writeSerializableValue);
    }

    public void writeCollectionOfSerializable(Collection<? extends CompactSerializable> collection) {
        writeCollection(collection, this::writeSerializableValue);
    }

    public void writeIntegerMapOfSerializable(Map<Integer, ? extends CompactSerializable> map) {
        writeMapOfSerializable(map, this::writeInt);
    }

    public void writeStringMapOfSerializable(Map<String, ? extends CompactSerializable> map) {
        writeMapOfSerializable(map, this::writeString);
    }

    public <K> void writeMapOfSerializable(Map<K, ? extends CompactSerializable> map, Consumer<K> keyWriter) {
        writeMap(map, keyWriter, this::writeSerializableValue);
    }

    public <K, V> void writeMap(Map<K, V> map, Consumer<K> keyWriter, Consumer<V> valueWriter) {
        writeNullable(map, value -> {
            var entrySet = value.entrySet().stream()
                    .filter(e -> e.getKey() != null && e.getValue() != null)
                    .collect(Collectors.toSet());
            writeInt(entrySet.size());
            for (var entry : entrySet) {
                keyWriter.accept(entry.getKey());
                valueWriter.accept(entry.getValue());
            }
        });
    }

    public <V> void writeCollection(Collection<V> collection, Consumer<V> valueWriter) {
        writeNullable(collection, value -> {
            var filteredNotNull = value.stream()
                    .filter(Objects::nonNull)
                    .toList();
            writeInt(filteredNotNull.size());
            for (var e : filteredNotNull) {
                valueWriter.accept(e);
            }
        });
    }

    public <V> void writeNullable(V value, Consumer<V> valueWriter) {
        writeNullFlag(value);
        if (value != null) {
            valueWriter.accept(value);
        }
    }

    public byte[] toByteArray() {
        return stream.toByteArray();
    }

    public void writeSerializableValue(CompactSerializable serializable) {
        serializable.writeToStream(this);
    }

    private void writeNullFlag(Object obj) {
        if (obj == null) {
            stream.write(0);
        } else {
            stream.write(1);
        }
    }

    private byte[] doubleToBytes(double value) {
        long longBits = Double.doubleToLongBits(value);
        return longToBytes(longBits);
    }

    private byte[] floatToBytes(float value) {
        int intBits = Float.floatToIntBits(value);
        return intToBytes(intBits);
    }

    private byte[] longToBytes(long value) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (value >> 56);
        bytes[1] = (byte) (value >> 48);
        bytes[2] = (byte) (value >> 40);
        bytes[3] = (byte) (value >> 32);
        bytes[4] = (byte) (value >> 24);
        bytes[5] = (byte) (value >> 16);
        bytes[6] = (byte) (value >> 8);
        bytes[7] = (byte) (value);
        return bytes;
    }

    private byte[] intToBytes(int value) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (value >> 24);
        bytes[1] = (byte) (value >> 16);
        bytes[2] = (byte) (value >> 8);
        bytes[3] = (byte) (value);
        return bytes;
    }
}

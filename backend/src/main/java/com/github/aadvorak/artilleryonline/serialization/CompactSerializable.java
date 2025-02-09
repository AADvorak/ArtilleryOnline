package com.github.aadvorak.artilleryonline.serialization;

public interface CompactSerializable {
    void writeToStream(ByteArrayOutputStreamWrapper stream);

    default byte[] serialize() {
        var stream = new ByteArrayOutputStreamWrapper();
        writeToStream(stream);
        return stream.toByteArray();
    }
}

package io.github.cyrilschumacher.data.codec;

import java.nio.ByteBuffer;

final class WordByteBuffer {

    private static final int WORD_SIZE = 2;

    private final ByteBuffer buffer;

    WordByteBuffer(final byte[] value) {
        buffer = ByteBuffer.wrap(value);
    }

    byte[] getWord() {
        final byte[] value = new byte[WORD_SIZE];
        buffer.get(value);

        return value;
    }

}

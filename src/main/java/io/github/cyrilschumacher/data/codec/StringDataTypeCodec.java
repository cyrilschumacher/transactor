package io.github.cyrilschumacher.data.codec;

import java.nio.charset.Charset;

/**
 * Data type codec for the type: {@link String}.
 */
public final class StringDataTypeCodec implements DataTypeCodec<String> {

    @Override
    public String decode(final byte[] value, final Charset charset) {
        return new String(value, charset);
    }

    @Override
    public byte[] encode(final String value, final Charset charset) {
        return value.getBytes(charset);
    }

}

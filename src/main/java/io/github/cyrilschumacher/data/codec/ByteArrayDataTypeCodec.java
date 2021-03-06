package io.github.cyrilschumacher.data.codec;

import java.nio.charset.Charset;

/**
 * Data type codec for the type: <code>byte[]</code>.
 * <p>
 * This class is a base class: it only returns the value received as a parameter.
 */
public class ByteArrayDataTypeCodec implements DataTypeCodec<byte[]> {

    @Override
    public byte[] decode(final byte[] value, final Charset charset) {
        return value;
    }

    @Override
    public byte[] encode(final byte[] value, final Charset charset) {
        return value;
    }

}

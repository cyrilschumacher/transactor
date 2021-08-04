package io.github.cyrilschumacher.data.codec;

import java.nio.charset.Charset;

public interface DataTypeCodec<T> {

    T decode(byte[] value, Charset charset);
    byte[] encode(T value, Charset charset);

}

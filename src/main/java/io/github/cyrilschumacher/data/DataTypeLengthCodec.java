package io.github.cyrilschumacher.data;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public interface DataTypeLengthCodec {

    byte[] decode(ByteBuffer buffer, DataElement dataElement, Charset charset);

    byte[] encode(byte[] value, DataElement dataElement, Charset charset);

}

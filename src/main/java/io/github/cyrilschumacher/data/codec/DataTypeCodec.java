package io.github.cyrilschumacher.data.codec;

import java.nio.charset.Charset;


/**
 * Codec for {@link io.github.cyrilschumacher.data.DataElement}.
 * <p>
 * This interface defines methods to encode and decode a value of a type <code>T</code> that will be used during the
 * building or the parsing of the message.
 *
 * @param <T> the value type to encode and decode.
 */
public interface DataTypeCodec<T> {

    /**
     * Decodes a binary value (according a given charset) into an object.
     * <p>
     * This method takes 2 parameters. The first parameter defines the value to be decoded and the second parameter
     * defines the encoding that can be used to decode the value.
     * <p>
     * The method will return a value respecting the generic type defined in the class itself.
     *
     * @param value   a binary value to decode.
     * @param charset a charset.
     * @return the decoded value.
     */
    T decode(byte[] value, Charset charset);

    /**
     * Encodes a value (according a given charset) into a binary value.
     * <p>
     * This method takes 2 parameters. The first parameter defines the value to be encoded and the second parameter
     * defines the encoding that can be used to encode the value.
     * <p>
     * The method will return a binary value, which is a representation of the value provided in parameter.
     *
     * @param value   an object to encode.
     * @param charset a charset.
     * @return the encoded value.
     */
    byte[] encode(T value, Charset charset);

}

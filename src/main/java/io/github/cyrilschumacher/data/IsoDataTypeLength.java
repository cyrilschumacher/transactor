package io.github.cyrilschumacher.data;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum IsoDataTypeLength implements DataTypeLength {

    FIXED(new FixedAdapter()),
    VARIABLE(new VariableAdapter());

    private static final String ALPHA_SPECIAL_PADDING = "0";
    private static final byte BINARY_PADDING = 0;
    private static final String NUMERIC_PADDING = "0";

    private final DataTypeLength adapter;

    IsoDataTypeLength(final DataTypeLength adapter) {
        this.adapter = adapter;
    }

    public byte[] decode(final ByteBuffer value, final DataElement dataElement, final Charset charset) {
        return adapter.decode(value, dataElement, charset);
    }

    public byte[] encode(final byte[] value, final DataElement dataElement, final Charset charset) {
        return adapter.encode(value, dataElement, charset);
    }

    private static class FixedAdapter implements DataTypeLength {

        private static byte[] createPadding(final String character, final int count, final Charset charset) {
            final String padding = character.repeat(count);
            return padding.getBytes(charset);
        }

        public byte[] decode(final ByteBuffer buffer, final DataElement dataElement, final Charset charset) {
            final int length = dataElement.getLength();
            final byte[] decodedValue = new byte[length];
            buffer.get(decodedValue);

            return decodedValue;
        }

        public byte[] encode(final byte[] value, final DataElement dataElement, final Charset charset) {
            final int maximumLength = dataElement.getLength();
            final DataType[] types = dataElement.getTypes();

            final List<DataType> typeList = List.of(types);
            final byte[] padding = createPadding(typeList, value.length, maximumLength, charset);

            return ByteBuffer.allocate(maximumLength).put(padding).put(value).array();
        }

        private byte[] createPadding(final List<DataType> typeList, final int length, final int maximumLength, final Charset charset) {
            final int missingLength = maximumLength - length;
            if (typeList.contains(DataType.BINARY)) {
                final byte[] leftPadding = new byte[missingLength];
                Arrays.fill(leftPadding, BINARY_PADDING);

                return leftPadding;
            } else if (typeList.contains(DataType.ALPHA) || typeList.contains(DataType.SPECIAL)) {
                return createPadding(ALPHA_SPECIAL_PADDING, missingLength, charset);
            } else if (typeList.contains(DataType.NUMERIC)) {
                return createPadding(NUMERIC_PADDING, missingLength, charset);
            }

            return new byte[0];
        }

    }

    private static class VariableAdapter implements DataTypeLength {

        private static byte[] createEncodedLength(final byte[] value, final int length, final Charset charset) {
            final int expectedDigits = getDigits(length);
            if (value.length > length) {
                throw new IllegalElementLengthException(value.length, length);
            }

            final String encodedLengthIndicator = String.format("%0" + expectedDigits + "d", value.length);
            return encodedLengthIndicator.getBytes(charset);
        }

        private static int getDigits(final int length) {
            return (int) Math.log10(length) + 1;
        }

        public byte[] decode(final ByteBuffer buffer, final DataElement dataElement, final Charset charset) {
            final int length = dataElement.getLength();
            final int digits = getDigits(length);
            final byte[] lengthIndicatorByteArray = new byte[digits];
            buffer.get(lengthIndicatorByteArray);

            final String lengthIndicatorString = new String(lengthIndicatorByteArray, charset);
            final int decodedLengthIndicator = Integer.parseInt(lengthIndicatorString, 10);
            final byte[] decodedValue = new byte[decodedLengthIndicator];
            buffer.get(decodedValue);

            return decodedValue;
        }

        public byte[] encode(final byte[] value, final DataElement dataElement, final Charset charset) {
            final int length = dataElement.getLength();
            final byte[] encodedLength = createEncodedLength(value, length, charset);
            final int capacity = encodedLength.length + value.length;

            return ByteBuffer.allocate(capacity).put(encodedLength).put(value).array();
        }

    }

    public static class IllegalElementLengthException extends RuntimeException {

        private final int current;
        private final int expected;

        IllegalElementLengthException(final int current, final int expected) {
            super("The size of the element (" + current + ") is greater than the expected size (" + expected + ").");

            this.current = current;
            this.expected = expected;
        }

        public int getCurrent() {
            return current;
        }

        public int getExpected() {
            return expected;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            } else if ((o == null) || (getClass() != o.getClass())) {
                return false;
            }

            final IllegalElementLengthException exception = (IllegalElementLengthException) o;
            return current == exception.current && expected == exception.expected;
        }

        @Override
        public int hashCode() {
            return Objects.hash(current, expected);
        }

    }

}























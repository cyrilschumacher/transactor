package io.github.cyrilschumacher;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Bitmap {

    private static final int BITMAP_LENGTH = 16;
    private static final int HEXADECIMAL_RADIX = 16;
    private static final int MAXIMUM_BINARY_RANGE = 4;
    private static final int MAXIMUM_DATA_ELEMENTS_PER_BITMAP = 64;

    private final Set<Integer> dataFields;
    private final int range;

    private Bitmap(final Set<Integer> dataFields, final int range) {
        this.dataFields = Set.copyOf(dataFields);
        this.range = range;
    }

    static Builder builder() {
        return new Builder();
    }

    static Bitmap parse(final byte[] data) {
        final Set<Integer> dataFields = new HashSet<>();
        for (int index = 0; index < data.length; index++) {
            final byte bytePart = data[index];
            final Set<Integer> dataFieldsToAdd = parse(index * 2, bytePart, 8);

            dataFields.addAll(dataFieldsToAdd);
        }

        final int range = computeRange(data.length * 2);
        return new Bitmap(dataFields, range);
    }

    static Bitmap parse(final String data) {
        final Set<Integer> dataFields = new HashSet<>();

        for (int dataPartIndex = 0; dataPartIndex < data.length(); dataPartIndex++) {
            final char hexBytePart = data.charAt(dataPartIndex);
            final byte bytePart = (byte) Character.digit(hexBytePart, HEXADECIMAL_RADIX);
            final Set<Integer> dataFieldsToAdd = parse(dataPartIndex, bytePart, 4);

            dataFields.addAll(dataFieldsToAdd);
        }

        final int range = computeRange(data.length());
        return new Bitmap(dataFields, range);
    }

    private static byte[] compactByteArray(final byte[] value) {
        final byte[] buffer = new byte[value.length / 2];
        for (int i = 0; i < value.length; i++) {
            final int index = i / 2;
            buffer[index] = (i % 2 == 0) ? value[i] : (byte) (buffer[index] << 4 | value[i]);
        }

        return buffer;
    }

    private static int computeRange(final double length) {
        final double range = length / (double) BITMAP_LENGTH;
        return (int) Math.ceil(range);
    }

    private static boolean isBitSet(final int bitPosition, final int bytePart) {
        final int initialBits = (int) Math.pow(2, bitPosition);
        return (bytePart & initialBits) == initialBits;
    }

    private static Set<Integer> parse(final int dataPartIndex, final byte bytePart, final int maximumBitNumber) {
        return IntStream.range(0, maximumBitNumber)
                .boxed()
                .filter(bitIndex -> isBitSet(bitIndex, bytePart))
                .map(bitIndex -> toField(bitIndex, dataPartIndex, maximumBitNumber))
                .collect(Collectors.toUnmodifiableSet());
    }

    private static int toField(final int bitIndex, final int dataPartIndex, final int maximumBitNumber) {
        final int position = (maximumBitNumber - bitIndex);
        return (dataPartIndex * 4) + position;
    }

    public Set<Integer> getDataFields() {
        return dataFields;
    }

    public int getRange() {
        return range;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else  if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        final Bitmap bitmap = (Bitmap) o;
        return range == bitmap.range && Objects.equals(dataFields, bitmap.dataFields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataFields, range);
    }

    @Override
    public String toString() {
        return "Bitmap{" +
                "dataFields=" + dataFields.stream().sorted().collect(Collectors.toUnmodifiableList()) +
                ", range=" + range +
                '}';
    }

    byte[] toByteArray() {
        assertRange();

        final byte[] bitmap = createBitmap();
        return compactByteArray(bitmap);
    }

    private void assertRange() {
        final int highestDataField = Collections.max(dataFields);
        final int expectedRange = (int) Math.ceil((double) highestDataField / (double) MAXIMUM_DATA_ELEMENTS_PER_BITMAP);
        if (expectedRange > range) {
            throw new InsufficientRangeException(highestDataField);
        }
    }

    private byte[] createBitmap() {
        final int size = range * BITMAP_LENGTH;
        final byte[] bitmap = new byte[size];

        for (int dataField : dataFields) {
            final int positionInBitmap = (dataField - 1) / MAXIMUM_BINARY_RANGE;
            final int positionInByte = ((positionInBitmap + 1) * MAXIMUM_BINARY_RANGE) - dataField;

            bitmap[positionInBitmap] ^= (byte) Math.pow(2, positionInByte);
        }

        return bitmap;
    }

    static class Builder {

        private static final int MINIMUM_RANGE = 1;

        private final Set<Integer> dataFields;
        private int range = MINIMUM_RANGE;

        private Builder() {
            this.dataFields = new HashSet<>();
        }

        Builder add(final int dataField) {
            dataFields.add(dataField);
            return this;
        }

        Builder add(final int... dataFields) {
            final Set<Integer> dataFieldsToAdd = Arrays.stream(dataFields).boxed().collect(Collectors.toUnmodifiableSet());
            return add(dataFieldsToAdd);
        }

        Builder add(final Set<Integer> dataFieldsToAdd) {
            dataFields.addAll(dataFieldsToAdd);
            return this;
        }

        Builder withRange(final int range) {
            if (range < MINIMUM_RANGE) {
                throw new IllegalArgumentException("The range number must be greater than " + MINIMUM_RANGE + ".");
            }

            this.range = range;
            return this;
        }

        Bitmap build() {
            return new Bitmap(dataFields, range);
        }

    }

    public static class InsufficientRangeException extends RuntimeException {

        private final int dataField;

        public InsufficientRangeException(final int dataField) {
            super("Insufficient range: data field \"" + dataField + "\" cannot be supported.");
            this.dataField = dataField;
        }

        public int getDataField() {
            return dataField;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            } else if ((o == null) || (getClass() != o.getClass())) {
                return false;
            }

            final InsufficientRangeException that = (InsufficientRangeException) o;
            return dataField == that.dataField;
        }

        @Override
        public int hashCode() {
            return Objects.hash(dataField);
        }

        @Override
        public String toString() {
            return "RangeOverflowException{" +
                    "dataField=" + dataField +
                    '}';
        }
    }

}
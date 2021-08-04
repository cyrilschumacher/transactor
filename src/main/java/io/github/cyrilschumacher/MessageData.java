package io.github.cyrilschumacher;

import io.github.cyrilschumacher.data.DataElement;
import io.github.cyrilschumacher.data.DataTypeLength;
import io.github.cyrilschumacher.data.codec.DataTypeCodec;
import io.github.cyrilschumacher.data.codec.DataTypeCodecRegistry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

class MessageData<T extends Enum<T> & DataElement> {

    private final DataTypeCodecRegistry dataTypeCodecRegistry;
    private final Map<T, byte[]> elements;
    private final Charset charset;

    private MessageData(final Map<T, byte[]> elements, final Charset charset, final DataTypeCodecRegistry dataTypeCodecRegistry) {
        this.elements = Collections.unmodifiableMap(elements);
        this.dataTypeCodecRegistry = dataTypeCodecRegistry;
        this.charset = charset;
    }

    static <T extends Enum<T> & DataElement> Builder<T> builder() {
        return new Builder<>();
    }

    static <T extends Enum<T> & DataElement> MessageData<T> parse(final List<T> dataElements, final byte[] data) {
        return parse(dataElements, data, StandardCharsets.US_ASCII, new DataTypeCodecRegistry());
    }

    static <T extends Enum<T> & DataElement> MessageData<T> parse(final List<T> dataElements, final byte[] data, final Charset charset) {
        return parse(dataElements, data, charset, new DataTypeCodecRegistry());
    }

    static <T extends Enum<T> & DataElement> MessageData<T> parse(final List<T> dataElements, final byte[] data, final Charset charset, final DataTypeCodecRegistry dataTypeCodecRegistry) {
        final ByteBuffer buffer = ByteBuffer.wrap(data);
        return parse(dataElements, buffer, charset, dataTypeCodecRegistry);
    }

    private static <T extends Enum<T> & DataElement> MessageData<T> parse(final List<T> dataElements, final ByteBuffer buffer, final Charset charset, final DataTypeCodecRegistry dataTypeCodecRegistry) {
        final Map<T, byte[]> elements = dataElements.stream()
                .map(dataElement -> Map.entry(dataElement, parse(dataElement, buffer)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (value1, value2) -> value1, LinkedHashMap::new));

        return new MessageData<>(elements, charset, dataTypeCodecRegistry);
    }

    private static <T extends Enum<T> & DataElement> byte[] parse(final T dataElement, final ByteBuffer buffer) {
        final DataTypeLength typeLength = dataElement.getTypeLength();
        return typeLength.decode(buffer, dataElement, StandardCharsets.US_ASCII);
    }

    public byte[] get(final T dataElement) {
        return elements.get(dataElement);
    }

    public <V> V get(final T dataElement, final Class<V> klass) {
        final DataTypeCodec<V> dataTypeCodec = dataTypeCodecRegistry.forClass(klass);
        final byte[] value = elements.get(dataElement);

        return dataTypeCodec.decode(value, charset);
    }

    byte[] toByteArray() throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (Map.Entry<T, byte[]> element : elements.entrySet()) {
            final T key = element.getKey();
            final byte[] value = element.getValue();

            final DataTypeLength dataTypeLength = key.getTypeLength();
            final byte[] encodedValue = dataTypeLength.encode(value, key, charset);
            buffer.write(encodedValue);
        }

        return buffer.toByteArray();
    }

    @Override
    public String toString() {
        return "Message{" +
                "elements=" + elements.keySet() +
                '}';
    }

    static class Builder<T extends Enum<T> & DataElement> {

        private Charset charset = StandardCharsets.US_ASCII;
        private DataTypeCodecRegistry dataTypeCodecRegistry = new DataTypeCodecRegistry();

        private final SortedMap<T, Object> elements;

        private Builder() {
            this.elements = new TreeMap<>();
        }

        <V> Builder<T> add(final T element, final V value) {
            this.elements.put(element, value);
            return this;
        }

        Builder<T> withCharset(final Charset charset) {
            this.charset = Objects.requireNonNull(charset);
            return this;
        }

        Builder<T> withDataTypeCodecRegistry(final DataTypeCodecRegistry dataTypeCodecRegistry) {
            this.dataTypeCodecRegistry = dataTypeCodecRegistry;
            return this;
        }

        MessageData<T> build() {
            final Map<T, byte[]> elements = this.elements.keySet().stream()
                    .map(element -> build(element, this.elements.get(element)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (value1, value2) -> value1, LinkedHashMap::new));

            return new MessageData<>(elements, charset, dataTypeCodecRegistry);
        }

        @SuppressWarnings("unchecked")
        private Map.Entry<T, byte[]> build(final T dataElement, final Object value) {
            final Class<?> valueType = value.getClass();
            return Optional.of(dataTypeCodecRegistry)
                    .map(registry -> (DataTypeCodec<Object>) registry.forClass(valueType))
                    .map(registry -> registry.encode(value, charset))
                    .map(encodedValue -> Map.entry(dataElement, encodedValue))
                    .orElseThrow(() -> new UnknownTypeException(dataElement, valueType));
        }

    }

    public static class UnknownTypeException extends RuntimeException {

        private final DataElement element;
        private final Class<?> klass;

        private UnknownTypeException(final DataElement element, final Class<?> klass) {
            super(klass + " not supported for element.");
            this.element = element;
            this.klass = klass;
        }

        public DataElement getElement() {
            return element;
        }

        public Class<?> getKlass() {
            return klass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if ((o == null) || (getClass() != o.getClass())) {
                return false;
            }

            final UnknownTypeException that = (UnknownTypeException) o;
            return Objects.equals(element, that.element) && Objects.equals(klass, that.klass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(element, klass);
        }

        @Override
        public String toString() {
            return "UnknownTypeException{" +
                    "element=" + element +
                    ", klass=" + klass +
                    '}';
        }
    }

}

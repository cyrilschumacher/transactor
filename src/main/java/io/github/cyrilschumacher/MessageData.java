package io.github.cyrilschumacher;

import io.github.cyrilschumacher.data.DataElement;
import io.github.cyrilschumacher.data.DataTypeLengthCodec;
import io.github.cyrilschumacher.data.codec.DataTypeCodec;
import io.github.cyrilschumacher.data.codec.DataTypeCodecRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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

    private static final Logger LOGGER = LogManager.getLogger(MessageData.class);

    private final DataTypeCodecRegistry dataTypeCodecRegistry;
    private final Map<T, byte[]> dataElements;
    private final Charset charset;

    private MessageData(final Map<T, byte[]> dataElements, final Charset charset, final DataTypeCodecRegistry dataTypeCodecRegistry) {
        this.dataElements = Collections.unmodifiableMap(dataElements);
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
                .peek(dataElement -> LOGGER.debug("Parse element: {} ({})", dataElement.getField(), dataElement.getDescription()))
                .map(dataElement -> Map.entry(dataElement, parse(dataElement, buffer)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (value1, value2) -> value1, LinkedHashMap::new));

        return new MessageData<>(elements, charset, dataTypeCodecRegistry);
    }

    private static <T extends Enum<T> & DataElement> byte[] parse(final T dataElement, final ByteBuffer buffer) {
        final DataTypeLengthCodec typeLength = dataElement.getTypeLength();
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        final MessageData<?> that = (MessageData<?>) o;
        return Objects.equals(charset, that.charset)
                && dataElements.entrySet().stream()
                .filter(element -> that.dataElements.containsKey(element.getKey()))
                .map(element -> new byte[][]{element.getValue(), that.dataElements.get(element.getKey())})
                .allMatch(element -> Arrays.equals(element[0], element[1]));
    }

    @Override
    public int hashCode() {
        int result = charset.hashCode();
        result *= 31 + dataElements.keySet().hashCode();
        result *= 31 + dataElements.values().stream().map(value -> 31 + Arrays.hashCode(value)).collect(Collectors.toList()).hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "MessageData{" +
                "elements=" + dataElements.keySet() +
                ", charset=" + charset +
                '}';
    }

    byte[] toByteArray() throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (Map.Entry<T, byte[]> element : dataElements.entrySet()) {
            final T key = element.getKey();
            final byte[] value = element.getValue();

            final DataTypeLengthCodec dataTypeLengthCodec = key.getTypeLength();
            final byte[] encodedValue = dataTypeLengthCodec.encode(value, key, charset);
            buffer.write(encodedValue);
        }

        return buffer.toByteArray();
    }

    static class Builder<T extends Enum<T> & DataElement> {

        private final SortedMap<T, Object> elements;
        private Charset charset = StandardCharsets.US_ASCII;
        private DataTypeCodecRegistry dataTypeCodecRegistry = new DataTypeCodecRegistry();

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
            LOGGER.debug("Build element: {} ({})", dataElement.getField(), dataElement.getDescription());

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
            super("\"" + klass.getName() + "\" not supported for element.");
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
        public boolean equals(final Object o) {
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

package io.github.cyrilschumacher;

import io.github.cyrilschumacher.data.DataElement;
import io.github.cyrilschumacher.data.codec.DataTypeCodecRegistry;
import io.github.cyrilschumacher.data.dump.DumpWriter;
import io.github.cyrilschumacher.data.dump.TransactorDumpWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Builder and parser for ISO 8583 message.
 * <p>
 * The class cannot be created directly. Instead, the class provides several static methods to build or parse an
 * ISO 8583 message. An instance of this class represents an ISO 8583 message and offers the possibility to
 * get the message in binary format by calling the {@link Transactor#toByteArray()} method or to get various
 * information about the message (bitmap, elements).
 *
 * <ul><li>Build message:</li></ul>
 * <p>
 * This class proposes static methods: {@link Transactor#builder(int, Charset)},
 * {@link Transactor#builder(int, Charset, DataTypeCodecRegistry)} returning an instance of an internal builder of
 * this class. This builder offers methods for adding elements based on the generic type <code>T</code> of the class.
 * After adding the elements, an instance of the {@link Transactor} class is returned.
 *
 * <ul><li>Parse message:</li></ul>
 * <p>
 * This class proposes static methods to parse an ISO 8583 message: {@link Transactor#parse(byte[], Class, Charset)},
 * {@link Transactor#parse(byte[], Class, Charset, DataTypeCodecRegistry)} returning an instance of the
 * {@link Transactor} class.
 *
 * @param <T> the type of data elements in the message.
 */
public class Transactor<T extends Enum<T> & DataElement> {

    private static final int BITMAP_LENGTH = 8;
    private static final int HEXADECIMAL_RADIX = 16;
    private static final int MESSAGE_TYPE_INDICATOR_LENGTH = 4;

    private final Bitmap bitmap;
    private final MessageData<T> messageData;
    private final int messageTypeIndicator;
    private final Charset charset;

    private Transactor(final int messageTypeIndicator, final Bitmap bitmap, final MessageData<T> messageData, final Charset charset) {
        this.messageTypeIndicator = messageTypeIndicator;
        this.bitmap = bitmap;
        this.messageData = messageData;
        this.charset = charset;
    }

    public static <T extends Enum<T> & DataElement> Builder<T> builder(final int messageTypeIndicator, final Charset charset) {
        return builder(messageTypeIndicator, charset, new DataTypeCodecRegistry());
    }

    public static <T extends Enum<T> & DataElement> Builder<T> builder(final int messageTypeIndicator, final Charset charset, final DataTypeCodecRegistry dataTypeCodecRegistry) {
        return new Builder<>(messageTypeIndicator, dataTypeCodecRegistry, charset);
    }

    public static <T extends Enum<T> & DataElement> Transactor<T> parse(final byte[] data, final Class<T> klass, final Charset charset) {
        return parse(data, klass, charset, new DataTypeCodecRegistry());
    }

    public static <T extends Enum<T> & DataElement> Transactor<T> parse(final byte[] data, final Class<T> klass, final Charset charset, final DataTypeCodecRegistry dataTypeCodecRegistry) {
        final ByteBuffer buffer = ByteBuffer.wrap(data);

        final int messageTypeIndicator = readMessageTypeIndicator(buffer, charset);
        final Bitmap bitmap = readBitmap(buffer);
        final MessageData<T> messageData = readMessage(buffer, klass, bitmap, charset, dataTypeCodecRegistry);

        return new Transactor<>(messageTypeIndicator, bitmap, messageData, charset);
    }

    private static <T extends Enum<T> & DataElement> MessageData<T> readMessage(final ByteBuffer buffer, final Class<T> klass, final Bitmap bitmap, final Charset charset, final DataTypeCodecRegistry dataTypeCodecRegistry) {
        final int length = buffer.remaining();
        final byte[] data = new byte[length];
        buffer.get(data);

        final List<T> dataElements = Arrays.stream(klass.getEnumConstants()).sorted().filter(dataElement -> bitmap.getDataFields().contains(dataElement.getField())).collect(Collectors.toUnmodifiableList());
        return MessageData.parse(dataElements, data, charset, dataTypeCodecRegistry);
    }

    private static Bitmap readBitmap(final ByteBuffer buffer) {
        final byte[] data = new byte[BITMAP_LENGTH];
        buffer.get(data);

        return Bitmap.parse(data);
    }

    private static int readMessageTypeIndicator(final ByteBuffer buffer, final Charset charset) {
        final byte[] data = new byte[MESSAGE_TYPE_INDICATOR_LENGTH];
        buffer.get(data);

        final String messageTypeIndicator = new String(data, charset);
        return Integer.parseInt(messageTypeIndicator, HEXADECIMAL_RADIX);
    }

    /**
     * @param writer the writer used to write the dump.
     * @see #dump(DumpWriter)
     */
    public void dump(final Writer writer) {
        final DumpWriter<T> dumpWriter = new TransactorDumpWriter<>(writer);
        dump(dumpWriter);
    }

    /**
     * Create a dump summarizing the message.
     *
     * @param dumpWriter the dump writer.
     * @see DumpWriter
     */
    public void dump(final DumpWriter<T> dumpWriter) {
        try {
            dumpWriter.printHeader();
            dumpWriter.printMessageHeader(messageTypeIndicator, bitmap, charset);
            dumpWriter.printDataElements(messageData, charset);
            dumpWriter.printFooter();
        } catch (IOException e) {
            throw new IllegalStateException("An error occurred during the dump generation.", e);
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Charset getCharset() {
        return charset;
    }

    public MessageData<T> getMessageData() {
        return messageData;
    }

    public int getMessageTypeIndicator() {
        return messageTypeIndicator;
    }

    public byte[] toByteArray() throws IOException {
        final byte[] messageTypeIndicatorByteArray = generateMessageTypeIndicator(messageTypeIndicator, charset);
        final byte[] bitmap = this.bitmap.toByteArray();
        final byte[] message = this.messageData.toByteArray();

        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        buffer.write(messageTypeIndicatorByteArray);
        buffer.write(bitmap);
        buffer.write(message);

        return buffer.toByteArray();
    }

    @Override
    public String toString() {
        return "Transactor{" +
                "bitmap=" + bitmap +
                ", messageData=" + messageData +
                ", messageTypeIndicator=" + messageTypeIndicator +
                ", charset=" + charset +
                '}';
    }

    private byte[] generateMessageTypeIndicator(final int messageTypeIndicator, final Charset charset) {
        final String messageTypeIndicatorString = Integer.toHexString(messageTypeIndicator);
        final byte[] encodedValue = messageTypeIndicatorString.getBytes(charset);

        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).put(encodedValue).array();
    }

    public static class Builder<T extends Enum<T> & DataElement> {

        private static final int SECONDARY_BITMAP_RANGE = 2;
        private static final int TERTIARY_BITMAP_RANGE = 3;

        private final Bitmap.Builder bitmapBuilder;
        private final MessageData.Builder<T> messageBuilder;
        private final int messageTypeIndicator;
        private final Charset charset;

        private Builder(final int messageTypeIndicator, final DataTypeCodecRegistry dataTypeCodecRegistry, final Charset charset) {
            this(
                    messageTypeIndicator,
                    Bitmap.builder(),
                    MessageData.<T>builder().withCharset(charset).withDataTypeCodecRegistry(dataTypeCodecRegistry),
                    charset
            );
        }

        private Builder(final int messageTypeIndicator, final Bitmap.Builder bitmapBuilder, final MessageData.Builder<T> messageBuilder, final Charset charset) {
            this.messageTypeIndicator = messageTypeIndicator;
            this.bitmapBuilder = bitmapBuilder;
            this.messageBuilder = messageBuilder;
            this.charset = charset;
        }

        public <V> Builder<T> add(final T element, final V value) {
            final int dataField = element.getField();
            bitmapBuilder.add(dataField);
            messageBuilder.add(element, value);

            return this;
        }

        public <V> Builder<T> hasSecondaryBitmap() {
            bitmapBuilder.withRange(SECONDARY_BITMAP_RANGE);
            return this;
        }

        public <V> Builder<T> hasTertiaryBitmap() {
            bitmapBuilder.withRange(TERTIARY_BITMAP_RANGE);
            return this;
        }

        public Transactor<T> build() {
            final Bitmap bitmap = bitmapBuilder.build();
            final MessageData<T> messageData = messageBuilder.build();

            return new Transactor<>(messageTypeIndicator, bitmap, messageData, charset);
        }

    }

}

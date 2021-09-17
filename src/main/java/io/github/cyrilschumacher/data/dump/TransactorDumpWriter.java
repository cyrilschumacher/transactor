package io.github.cyrilschumacher.data.dump;

import io.github.cyrilschumacher.Bitmap;
import io.github.cyrilschumacher.MessageData;
import io.github.cyrilschumacher.data.DataElement;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Dump writer for {@link io.github.cyrilschumacher.Transactor}.
 * <p>
 * This class writes a dump to a given write as a constructor parameter: {@link #writer}.
 *
 * <ul>
 *     <li><b>Header</b>: print an ASCII separator indicating the start of the dump;</li>
 *     <li><b>Message header</b>: print the message type indicator and the bitmap (in hexadecimal format);</li>
 *     <li>
 *         <b>Data elements</b>: print each element with its number, its label and its value (in hexadecimal format)
 *         if not sensitive (otherwise, the value is hidden by dots);
 *     </li>
 *     <li><b>Footer</b>: print a separator indicating the end of the dump;</li>
 * </ul>
 *
 * @param <T> the type of data elements in the message.
 */
public class TransactorDumpWriter<T extends Enum<T> & DataElement> implements DumpWriter<T> {

    private static final String BORDER = "*";
    private static final int PADDING = 1;
    private static final int DEFAULT_WIDTH = 80;

    private final int width;
    private final PrintWriter writer;

    /**
     * Constructor.
     * <p>
     * Constructs a dump with lines not exceeding the constant: {@link #DEFAULT_WIDTH}.
     *
     * @param writer the writer used to write the dump.
     * @see #TransactorDumpWriter(Writer, int)
     */
    public TransactorDumpWriter(final Writer writer) {
        this(writer, DEFAULT_WIDTH);
    }

    /**
     * Constructor.
     *
     * @param writer the writer used to write the dump.
     * @param width  the total number of characters on each line.
     */
    public TransactorDumpWriter(final Writer writer, final int width) {
        this.width = width;
        this.writer = new PrintWriter(writer);
    }

    private static String createBanner(final String title, final int width) {
        final int length = title.length();

        final double missingCharacters = width - length - 2;
        final int missingSeparatorLeft = (int) Math.floor(missingCharacters / 2.0);
        final int missingSeparatorRight = (int) Math.ceil(missingCharacters / 2.0);

        final String missingLeftSpaces = BORDER.repeat(missingSeparatorLeft);
        final String missingRightSpaces = BORDER.repeat(missingSeparatorRight);

        return String.format("%s %S %s", missingLeftSpaces, title, missingRightSpaces);
    }

    private static String createSection(final String title) {
        return "\t**** " + title + " Section:";
    }

    private static String formatDataElementValue(final DataElement dataElement, final byte[] value) {
        final DataElement.SensitiveFlag sensitiveFlag = dataElement.getSensitiveFlag();

        final StringBuilder buffer = new StringBuilder();
        for (byte b : value) {
            final String valueString = (sensitiveFlag == DataElement.SensitiveFlag.NON_SENSITIVE) ? String.format("%02X ", b) : ".. ";
            buffer.append(valueString);
        }

        return buffer.toString();
    }

    private static String getBitmap(final Bitmap bitmap) {
        final StringBuilder buffer = new StringBuilder();
        for (byte dataElement : bitmap.toByteArray()) {
            final String value = String.format("%02X ", dataElement);
            buffer.append(value);
        }

        return buffer.toString();
    }

    @Override
    public void close() {
        writer.close();
    }

    @Override
    public void printDataElements(final MessageData<T> messageData, final Charset charset) throws IOException {
        final String section = createSection("Data Elements");
        writer.println(section);
        writer.println();

        final Sheet dataElementsSheet = new Sheet(width, PADDING);

        final Sheet.Column fieldColumn = dataElementsSheet.createColumn((int) ((width / 100F) * 5F));
        final Sheet.Column descriptionColumn = dataElementsSheet.createColumn((int) ((width / 100F) * 47F));
        final Sheet.Column valueColumn = dataElementsSheet.createColumn((int) ((width / 100F) * 45F));

        for (MessageData.Element<T> element : messageData) {
            T dataElement = element.getDataElement();
            byte[] value = element.getValue();

            final int field = dataElement.getField();

            final String fieldString = String.format("%03d", field);
            final String description = dataElement.getDescription();
            final String valueString = formatDataElementValue(dataElement, value);

            dataElementsSheet.createRow().addColumn(fieldColumn, fieldString).addColumn(descriptionColumn, description).addColumn(valueColumn, valueString);
        }

        dataElementsSheet.write(writer);
    }

    @Override
    public void printFooter() {
        final String banner = createBanner("End ISO 8583 dump message", width);
        writer.println();
        writer.println(banner);
    }

    @Override
    public void printHeader() {
        final String banner = createBanner("Start ISO 8583 dump message", width);
        writer.println(banner);
        writer.println();
    }

    @Override
    public void printMessageHeader(int messageTypeIndicator, Bitmap bitmap, Charset charset) throws IOException {
        final String section = createSection("Message");
        writer.println(section);
        writer.println();

        final String messageTypeIndicatorString = String.format("0x%04x", messageTypeIndicator);
        final String bitmapString = getBitmap(bitmap);

        final Sheet messageHeaderSheet = new Sheet(width, PADDING);

        final Sheet.Column labelColumn = messageHeaderSheet.createColumn(23);
        messageHeaderSheet.createColumn(1, ":");
        final Sheet.Column valueColumn = messageHeaderSheet.createColumn();

        final Sheet.Row messageTypeIndicatorRow = messageHeaderSheet.createRow();
        messageTypeIndicatorRow.addColumn(labelColumn, "Message Type Indicator");
        messageTypeIndicatorRow.addColumn(valueColumn, messageTypeIndicatorString);

        final Sheet.Row bitmapRow = messageHeaderSheet.createRow();
        bitmapRow.addColumn(labelColumn, "Bitmap");
        bitmapRow.addColumn(valueColumn, bitmapString);

        messageHeaderSheet.write(writer);
    }

}

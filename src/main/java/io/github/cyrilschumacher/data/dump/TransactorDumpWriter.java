package io.github.cyrilschumacher.data.dump;

import io.github.cyrilschumacher.Bitmap;
import io.github.cyrilschumacher.MessageData;
import io.github.cyrilschumacher.data.DataElement;

import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Dump writer for {@link io.github.cyrilschumacher.Transactor}.
 * <p>
 * This class writes a dump to a given write as a constructor parameter: {@link #printWriter}.
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
    private static final int DEFAULT_MAXIMUM_ROW_LENGTH = 80;

    private final int maximumRowLength;
    private final PrintWriter printWriter;

    /**
     * Constructor.
     * <p>
     * Constructs a dump with lines not exceeding the constant: {@link #DEFAULT_MAXIMUM_ROW_LENGTH}.
     *
     * @param writer the writer used to write the dump.
     * @see #TransactorDumpWriter(Writer, int)
     */
    public TransactorDumpWriter(final Writer writer) {
        this(writer, DEFAULT_MAXIMUM_ROW_LENGTH);
    }

    /**
     * Constructor.
     *
     * @param writer the writer used to write the dump.
     * @param maximumRowLength  the total number of characters on each line.
     */
    public TransactorDumpWriter(final Writer writer, final int maximumRowLength) {
        this.maximumRowLength = maximumRowLength;
        this.printWriter = new PrintWriter(writer);
    }

    private static String createBanner(final String title, final int maximumRowLength) {
        final int length = title.length();

        final double missingCharacters = maximumRowLength - length - 2;
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
        printWriter.close();
    }

    @Override
    public void printDataElements(final MessageData<T> messageData, final Charset charset) {
        final String section = createSection("Data Elements");
        printWriter.println(section);
        printWriter.println();

        final Sheet dataElementsSheet = new Sheet(maximumRowLength, PADDING);

        final Sheet.ColumnDefinition fieldColumn = dataElementsSheet.createColumnDefinition((int) ((maximumRowLength / 100F) * 5F));
        final Sheet.ColumnDefinition descriptionColumn = dataElementsSheet.createColumnDefinition((int) ((maximumRowLength / 100F) * 47F));
        final Sheet.ColumnDefinition valueColumn = dataElementsSheet.createColumnDefinition((int) ((maximumRowLength / 100F) * 45F));

        for (MessageData.Element<T> element : messageData) {
            final T dataElement = element.getDataElement();
            final byte[] value = element.getValue();

            final int field = dataElement.getField();

            final String fieldString = String.format("%03d", field);
            final String description = dataElement.getDescription();
            final String valueString = formatDataElementValue(dataElement, value);

            dataElementsSheet.createRow().addColumn(fieldColumn, fieldString).addColumn(descriptionColumn, description).addColumn(valueColumn, valueString);
        }

        dataElementsSheet.write(printWriter);
    }

    @Override
    public void printFooter() {
        final String banner = createBanner("End ISO 8583 dump message", maximumRowLength);
        printWriter.println();
        printWriter.println(banner);
    }

    @Override
    public void printHeader() {
        final String banner = createBanner("Start ISO 8583 dump message", maximumRowLength);
        printWriter.println(banner);
        printWriter.println();
    }

    @Override
    public void printMessageHeader(int messageTypeIndicator, Bitmap bitmap, Charset charset) {
        final String section = createSection("Message");
        printWriter.println(section);
        printWriter.println();

        final String messageTypeIndicatorString = String.format("0x%04x", messageTypeIndicator);
        final String bitmapString = getBitmap(bitmap);

        final Sheet messageHeaderSheet = new Sheet(maximumRowLength, PADDING);

        final Sheet.ColumnDefinition labelColumn = messageHeaderSheet.createColumnDefinition(23);
        messageHeaderSheet.createColumnDefinition(1, ":");
        final Sheet.ColumnDefinition valueColumn = messageHeaderSheet.createColumnDefinition();

        final Sheet.Row messageTypeIndicatorRow = messageHeaderSheet.createRow();
        messageTypeIndicatorRow.addColumn(labelColumn, "Message Type Indicator");
        messageTypeIndicatorRow.addColumn(valueColumn, messageTypeIndicatorString);

        final Sheet.Row bitmapRow = messageHeaderSheet.createRow();
        bitmapRow.addColumn(labelColumn, "Bitmap");
        bitmapRow.addColumn(valueColumn, bitmapString);

        messageHeaderSheet.write(printWriter);
        printWriter.println();
    }

}

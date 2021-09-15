package io.github.cyrilschumacher;

import io.github.cyrilschumacher.data.DataElement;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

public class TransactorDumpWriter<T extends Enum<T> & DataElement> implements DumpWriter<T> {

    private static final String BORDER = "*";
    private static final int PADDING = 1;
    private static final int DEFAULT_WIDTH = 80;

    private final int width;
    private final PrintWriter writer;

    public TransactorDumpWriter(final Writer writer) {
        this(writer, DEFAULT_WIDTH);
    }

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

        final Map<T, byte[]> dataElements = messageData.get();
        for (Map.Entry<T, byte[]> entry : dataElements.entrySet()) {
            T dataElement = entry.getKey();
            byte[] value = entry.getValue();

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

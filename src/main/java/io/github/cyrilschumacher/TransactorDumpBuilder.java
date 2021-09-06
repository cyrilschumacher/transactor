package io.github.cyrilschumacher;

import io.github.cyrilschumacher.data.DataElement;

import java.nio.charset.Charset;
import java.util.Set;
import java.util.StringJoiner;

public class TransactorDumpBuilder<T extends Enum<T> & DataElement> implements DumpBuilder<T> {

    private static final String BORDER = "*";
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final int PADDING = 1;
    private static final int DEFAULT_WIDTH = 80;

    private final StringJoiner buffer;
    private final int width;

    public TransactorDumpBuilder() {
        this(new StringJoiner(LINE_SEPARATOR));
    }

    private TransactorDumpBuilder(final StringJoiner buffer) {
        this(buffer, DEFAULT_WIDTH);
    }

    private TransactorDumpBuilder(final StringJoiner buffer, final int width) {
        this.buffer = buffer;
        this.width = width;
    }

    private static String createBanner(final int width, final String title) {
        final int length = title.length();

        final double missingCharacters = width - length - 2;
        final int missingSeparatorLeft = (int) Math.floor(missingCharacters / 2.0);
        final int missingSeparatorRight = (int) Math.ceil(missingCharacters / 2.0);

        final String missingLeftSpaces = BORDER.repeat(missingSeparatorLeft);
        final String missingRightSpaces = BORDER.repeat(missingSeparatorRight);

        return String.format("%s %S %s", missingLeftSpaces, title, missingRightSpaces);
    }

    private static String createSection(final String name) {
        return String.format("%s\t> %S %s", LINE_SEPARATOR, name, LINE_SEPARATOR);
    }

    private static String formatValue(final DataElement dataElement, final byte[] value) {
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
    public TransactorDumpBuilder<T> withHeader(final int messageTypeIndicator, final Bitmap bitmap, final Charset charset) {
        final String banner = createBanner(width, "Start ISO 8583 dump message");
        final String headerTitle = createSection("Header");
        final String dataElementsTitle = createSection("Data elements");
        final String header = createHeader(messageTypeIndicator, bitmap);

        buffer.add(banner);
        buffer.add(headerTitle);
        buffer.add(header);
        buffer.add(dataElementsTitle);

        return this;
    }

    @Override
    public TransactorDumpBuilder<T> withBody(final MessageData<T> messageData, final Charset charset) {
        final Sheet sheet = new Sheet(width, PADDING);
        final Sheet.Column fieldColumn = sheet.createColumn(3);
        final Sheet.Column descriptionColumn = sheet.createColumn(37);
        final Sheet.Column valueColumn = sheet.createColumn(35);

        messageData.get().forEach((dataElement, value) -> {
            final int field = dataElement.getField();

            final String fieldString = String.format("%03d", field);
            final String description = dataElement.getDescription();
            final String valueString = formatValue(dataElement, value);

            sheet.createRow().addColumn(fieldColumn, fieldString).addColumn(descriptionColumn, description).addColumn(valueColumn, valueString);
        });

        final String body = sheet.build();
        buffer.add(body);

        return this;
    }

    @Override
    public TransactorDumpBuilder<T> withFooter() {
        final String footer = LINE_SEPARATOR + createBanner(width, "End ISO 8583 dump message");
        buffer.add(footer);

        return this;
    }

    @Override
    public String build() {
        return buffer.toString();
    }

    private String createHeader(final int messageTypeIndicator, final Bitmap bitmap) {
        final Set<Integer> dataFields = bitmap.getDataFields();
        final int dataFieldsSize = dataFields.size();

        final String messageTypeIndicatorString = String.format("0x%04x", messageTypeIndicator);
        final String bitmapString = getBitmap(bitmap);
        final String dataElements = Integer.toString(dataFieldsSize);

        final Sheet sheet = new Sheet(width, PADDING);
        final Sheet.Column labelColumn = sheet.createColumn(25);
        sheet.createColumn(1, ":");
        final Sheet.Column valueColumn = sheet.createColumn();

        final Sheet.Row messageTypeIndicatorRow = sheet.createRow();
        messageTypeIndicatorRow.addColumn(labelColumn, "Message Type Indicator");
        messageTypeIndicatorRow.addColumn(valueColumn, messageTypeIndicatorString);

        final Sheet.Row bitmapRow = sheet.createRow();
        bitmapRow.addColumn(labelColumn, "Bitmap");
        bitmapRow.addColumn(valueColumn, bitmapString);

        final Sheet.Row dataElementsRow = sheet.createRow();
        dataElementsRow.addColumn(labelColumn, "Number of data elements");
        dataElementsRow.addColumn(valueColumn, dataElements);

        return sheet.build();
    }

}

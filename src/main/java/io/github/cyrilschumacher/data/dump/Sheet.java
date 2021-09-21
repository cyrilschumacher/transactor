package io.github.cyrilschumacher.data.dump;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Sheet {

    private static final String EMPTY_COLUMN_VALUE = "";
    private static final int INITIAL_POSITION = 0;

    private final List<Column> columns;
    private final int maxWidth;
    private final int padding;
    private final List<Row> rows;

    Sheet(final int maximumWidth, final int padding) {
        this.maxWidth = maximumWidth;
        this.padding = padding;
        this.rows = new ArrayList<>();
        this.columns = new ArrayList<>();
    }

    Column createColumn() {
        final int totalColumnsWidth = columns.stream().map(column -> column.width).reduce(0, Integer::sum);
        final int width = maxWidth - totalColumnsWidth - padding;

        return createColumn(width);
    }

    Column createColumn(final int width) {
        return createColumn(width, EMPTY_COLUMN_VALUE);
    }

    Column createColumn(final int width, final String defaultValue) {
        assertColumnWidth(width);

        final int size = columns.size();
        final Column lastColumn = columns.isEmpty() ? null : columns.get(size - 1);

        return createColumn(width, defaultValue, lastColumn);
    }

    Row createRow() {
        final Row row = new Row(columns, maxWidth);
        rows.add(row);

        return row;
    }

    void write(final PrintWriter writer) {
        for (Row row : rows) {
            row.write(writer);
        }
    }

    private void assertColumnWidth(final int width) {
        final int totalColumnsWidth = columns.stream().map(column -> column.width).reduce(0, Integer::sum);

        final int widthWithPadding = width + padding;
        final int remainingWidth = maxWidth - totalColumnsWidth;
        if (remainingWidth < widthWithPadding) {
            throw new IllegalArgumentException("The length of the columns exceeds the maximum length (" + maxWidth + ").");
        }
    }

    private Column createColumn(final int width, final String defaultValue, final Column previousColumn) {
        final int position = previousColumn != null ? previousColumn.getPosition() + previousColumn.getWidth() : INITIAL_POSITION;
        final int columnWidth = width + padding;

        final Column columnModel = new Column(columnWidth, position, padding, defaultValue);
        columns.add(columnModel);

        return columnModel;
    }

    protected static class Row {

        private final Map<Column, String> columns;
        private final int maxWidth;

        protected Row(final List<Column> columns, final int maxWidth) {
            this.columns = columns.stream().collect(Collectors.toMap(column -> column, Column::getDefaultValue));
            this.maxWidth = maxWidth;
        }

        private static String appendColumn(final Column column, final String row, final String value) {
            final int width = column.getWidth();
            final int startIndex = column.getPosition();
            final int endIndex = startIndex + width;

            final String previousColumns = row.substring(0, startIndex);
            final String nextColumns = row.substring(endIndex);

            return previousColumns + value + nextColumns;
        }

        protected Row addColumn(final Column column, final String value) {
            final boolean exists = columns.containsKey(column);
            if (!exists) {
                throw new IllegalArgumentException("The column is not specified in the sheet.");
            }

            columns.replace(column, value);
            return this;
        }

        protected void write(final PrintWriter writer) {
            final List<String> rows = new ArrayList<>();

            for (Map.Entry<Column, String> entry : columns.entrySet()) {
                final Column column = entry.getKey();
                final String initialValue = entry.getValue();

                final List<String> columns = column.format(initialValue);
                for (int size = columns.size(), index = 0; index < size; index++) {
                    final int columnsSize = columns.size();
                    final int rowsSize = rows.size();

                    if (columnsSize > rowsSize) {
                        final String row = " ".repeat(maxWidth);
                        rows.add(row);
                    }

                    final String value = columns.get(index);

                    String row = rows.get(index);
                    row = appendColumn(column, row, value);

                    rows.set(index, row);
                }
            }

            for (String row : rows) {
                writer.println(row);
            }
        }

    }

    protected static class Column {

        private final String defaultValue;
        private final int padding;
        private final int position;
        private final int width;

        private Column(final int width, final int position, final int padding, final String defaultValue) {
            this.defaultValue = defaultValue;
            this.padding = padding;
            this.position = position;
            this.width = width;
        }

        protected String getDefaultValue() {
            return defaultValue;
        }

        protected int getPosition() {
            return position;
        }

        protected int getWidth() {
            return width;
        }

        protected List<String> format(final String value) {
            final List<String> columns = new ArrayList<>();

            final String[] words = value.split(" ");
            final int maximumLength = width - padding;

            final StringBuilder buffer = new StringBuilder();
            final Iterator<String> iterator = List.of(words).iterator();
            while (iterator.hasNext()) {
                final String word = iterator.next();
                buffer.append(word).append(' ');

                if (((buffer.length() + 1) >= maximumLength) || !iterator.hasNext()) {
                    final String column = buffer.toString();
                    final String trimmedColumn = column.trim();
                    columns.add(trimmedColumn);
                    buffer.setLength(0);
                }
            }

            return format(columns);
        }

        private List<String> format(final List<String> value) {
            final List<String> columns = new ArrayList<>();

            for (String word : value) {
                final StringBuilder buffer = new StringBuilder();
                for (int length = word.length(), index = 0; index < length; index++) {
                    final int bufferLength = buffer.length();

                    final char c = word.charAt(index);
                    if (!((bufferLength == 0) && (c == ' '))) {
                        buffer.append(c);
                    }

                    if ((bufferLength == (width - padding)) || (index == (length - 1))) {
                        final String column = fillValue(buffer);
                        columns.add(column);
                        buffer.setLength(0);
                    }
                }
            }

            return List.copyOf(columns);
        }

        private String fillValue(final StringBuilder valueBuilder) {
            final String value = valueBuilder.toString();
            return fillValue(value);
        }

        private String fillValue(final String value) {
            final int length = value.length();
            final int remainingSpaces = width - length;

            final String missingSpaces = " ".repeat(remainingSpaces);
            return value + missingSpaces;
        }

    }

}

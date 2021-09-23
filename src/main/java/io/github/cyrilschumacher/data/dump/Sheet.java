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

    private final List<ColumnDefinition> columnDefinitions;
    private final int maximumRowLength;
    private final int padding;
    private final List<Row> rows;

    Sheet(final int maximumRowLength, final int padding) {
        this.maximumRowLength = maximumRowLength;
        this.padding = padding;
        this.rows = new ArrayList<>();
        this.columnDefinitions = new ArrayList<>();
    }

    ColumnDefinition createColumnDefinition() {
        final int totalColumnsLength = columnDefinitions.stream().map(column -> column.maximumLength).reduce(0, Integer::sum);
        final int length = maximumRowLength - totalColumnsLength - padding;

        return createColumnDefinition(length);
    }

    ColumnDefinition createColumnDefinition(final int maximumLength) {
        return createColumnDefinition(maximumLength, EMPTY_COLUMN_VALUE);
    }

    ColumnDefinition createColumnDefinition(final int maximumLength, final String defaultValue) {
        assertColumnLength(maximumLength);

        final int size = columnDefinitions.size();
        final ColumnDefinition lastColumn = columnDefinitions.isEmpty() ? null : columnDefinitions.get(size - 1);

        return createColumnDefinition(maximumLength, defaultValue, lastColumn);
    }

    Row createRow() {
        final Row row = new Row(columnDefinitions, maximumRowLength);
        rows.add(row);

        return row;
    }

    void write(final PrintWriter printWriter) {
        rows.stream().map(Row::format).forEach(printWriter::println);
    }

    private void assertColumnLength(final int length) {
        final int totalColumnsLength = columnDefinitions.stream().map(column -> column.maximumLength).reduce(0, Integer::sum);

        final int columnLength = length + padding;
        final int remainingLength = maximumRowLength - totalColumnsLength;
        if (remainingLength < columnLength) {
            throw new IllegalArgumentException("The length of the columns exceeds the maximum length (" + maximumRowLength + ").");
        }
    }

    private ColumnDefinition createColumnDefinition(final int length, final String defaultValue, final ColumnDefinition previousColumn) {
        final int position = previousColumn != null ? previousColumn.getPosition() + previousColumn.getMaximumLength() : INITIAL_POSITION;
        final int maximumLength = length + padding;

        final ColumnDefinition columnDefinition = new ColumnDefinition(maximumLength, position, padding, defaultValue);
        columnDefinitions.add(columnDefinition);

        return columnDefinition;
    }

    protected static class Row {

        private final Map<ColumnDefinition, String> columns;
        private final int maximumRowLength;

        protected Row(final List<ColumnDefinition> columnDefinitions, final int maximumRowLength) {
            this.columns = columnDefinitions.stream().collect(Collectors.toMap(column -> column, ColumnDefinition::getDefaultValue));
            this.maximumRowLength = maximumRowLength;
        }

        private static String appendColumn(final ColumnDefinition columnDefinition, final String row, final String value) {
            final int maximumLength = columnDefinition.getMaximumLength();
            final int startIndex = columnDefinition.getPosition();
            final int endIndex = startIndex + maximumLength;

            final String previousColumns = row.substring(0, startIndex);
            final String nextColumns = row.substring(endIndex);

            return previousColumns + value + nextColumns;
        }

        protected Row addColumn(final ColumnDefinition columnDefinition, final String value) {
            final boolean exists = columns.containsKey(columnDefinition);
            if (!exists) {
                throw new IllegalArgumentException("The column is not specified in the sheet.");
            }

            columns.replace(columnDefinition, value);
            return this;
        }

        protected List<String> format() {
            final List<String> rows = new ArrayList<>();

            for (Map.Entry<ColumnDefinition, String> entry : columns.entrySet()) {
                final ColumnDefinition columnDefinition = entry.getKey();
                final String columnValue = entry.getValue();

                final List<String> formattedColumns = columnDefinition.format(columnValue);
                for (int size = formattedColumns.size(), index = 0; index < size; index++) {
                    final int columnsSize = formattedColumns.size();
                    final int rowsSize = rows.size();
                    if (columnsSize > rowsSize) {
                        final String row = " ".repeat(maximumRowLength);
                        rows.add(row);
                    }

                    final String formattedColumn = formattedColumns.get(index);

                    final String oldRow = rows.get(index);
                    final String newRow = appendColumn(columnDefinition, oldRow, formattedColumn);

                    rows.set(index, newRow);
                }
            }

            return List.copyOf(rows);
        }

    }

    protected static class ColumnDefinition {

        private static final String WORD_SEPARATOR = " ";

        private final String defaultValue;
        private final int padding;
        private final int position;
        private final int maximumLength;

        private ColumnDefinition(final int maximumLength, final int position, final int padding, final String defaultValue) {
            this.defaultValue = defaultValue;
            this.padding = padding;
            this.position = position;
            this.maximumLength = maximumLength;
        }

        protected String getDefaultValue() {
            return defaultValue;
        }

        protected int getPosition() {
            return position;
        }

        protected int getMaximumLength() {
            return maximumLength;
        }

        private static Iterator<String> createWords(final String value) {
            final String[] words = value.split(WORD_SEPARATOR);
            final List<String> wordList = List.of(words);

            return wordList.iterator();
        }

        protected List<String> format(final String value) {
            final List<String> columns = new ArrayList<>();
            final StringBuilder buffer = new StringBuilder();

            final int maximumLength = this.maximumLength - padding;

            final Iterator<String> iterator = createWords(value);
            while (iterator.hasNext()) {
                final String word = iterator.next();
                buffer.append(word).append(WORD_SEPARATOR);

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

                    if ((bufferLength == (maximumLength - padding)) || (index == (length - 1))) {
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
            final int remainingSpaces = maximumLength - length;

            final String missingSpaces = " ".repeat(remainingSpaces);
            return value + missingSpaces;
        }

    }

}

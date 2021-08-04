package io.github.cyrilschumacher.data.codec;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Data type codec for the type: {@link YearMonth}.
 */
public final class YearMonthDataTypeCodec implements DataTypeCodec<YearMonth> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyMM");

    private final DateTimeFormatter dateTimeFormatter;

    public YearMonthDataTypeCodec() {
        this(FORMATTER);
    }

    private YearMonthDataTypeCodec(final DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public YearMonth decode(final byte[] value, final Charset charset) {
        final ByteBuffer buffer = ByteBuffer.wrap(value);
        return decode(buffer, charset);
    }

    @Override
    public byte[] encode(final YearMonth value, final Charset charset) {
        final String formattedValue = value.format(dateTimeFormatter);
        return formattedValue.getBytes(charset);
    }

    private YearMonth decode(final ByteBuffer buffer, final Charset charset) {
        final byte[] yearMonthByteArray = new byte[4];
        buffer.get(yearMonthByteArray);

        final String yearMonthString = new String(yearMonthByteArray, charset);
        return YearMonth.parse(yearMonthString, dateTimeFormatter);
    }

}

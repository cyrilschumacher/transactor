package io.github.cyrilschumacher.data.codec;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Data type codec for the type: {@link LocalDateTime}.
 * <p>
 * The codec handles the {@link LocalDateTime} type with the following format: <code>yyMMddHHmmss</code>.
 */
public final class LocalDateTimeDataTypeCodec implements DataTypeCodec<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmss");

    private final DateTimeFormatter dateTimeFormatter;

    public LocalDateTimeDataTypeCodec() {
        this(FORMATTER);
    }

    public LocalDateTimeDataTypeCodec(final DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public LocalDateTime decode(final byte[] value, final Charset charset) {
        final WordByteBuffer buffer = new WordByteBuffer(value);
        return decode(buffer, charset);
    }

    @Override
    public byte[] encode(final LocalDateTime value, final Charset charset) {
        final String formattedValue = value.format(dateTimeFormatter);
        return formattedValue.getBytes(charset);
    }

    private LocalDateTime decode(final WordByteBuffer buffer, final Charset charset) {
        final byte[] month = buffer.getWord();
        final byte[] dayOfMonth = buffer.getWord();
        final byte[] hour = buffer.getWord();
        final byte[] minute = buffer.getWord();
        final byte[] second = buffer.getWord();

        return decode(month, dayOfMonth, hour, minute, second, charset);
    }

    private LocalDateTime decode(final byte[] month, final byte[] dayOfMonth, final byte[] hour, final byte[] minute, final byte[] second, final Charset charset) {
        final String monthString = new String(month, charset);
        final String dayOfMonthString = new String(dayOfMonth, charset);
        final String hourString = new String(hour, charset);
        final String minuteString = new String(minute, charset);
        final String secondString = new String(second, charset);

        return decode(monthString, dayOfMonthString, hourString, minuteString, secondString);
    }

    private LocalDateTime decode(final String month, final String dayOfMonth, final String hour, final String minute, final String second) {
        final int monthInteger = Integer.parseInt(month);
        final int dayOfMonthInteger = Integer.parseInt(dayOfMonth);
        final int hourInteger = Integer.parseInt(hour);
        final int minuteInteger = Integer.parseInt(minute);
        final int secondInteger = Integer.parseInt(second);

        return LocalDateTime.now()
                .withMonth(monthInteger)
                .withDayOfMonth(dayOfMonthInteger)
                .withHour(hourInteger)
                .withMinute(minuteInteger)
                .withSecond(secondInteger)
                .withNano(0);
    }

}

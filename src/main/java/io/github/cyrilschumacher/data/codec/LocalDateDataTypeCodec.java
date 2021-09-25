package io.github.cyrilschumacher.data.codec;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Data type codec for the type: {@link LocalDate}.
 * <p>
 * The codec handles the {@link LocalDate} type with the following format: <code>MMdd</code>.
 */
public final class LocalDateDataTypeCodec implements DataTypeCodec<LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMdd");

    private final DateTimeFormatter dateTimeFormatter;

    public LocalDateDataTypeCodec() {
        this(FORMATTER);
    }

    public LocalDateDataTypeCodec(final DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public LocalDate decode(final byte[] value, final Charset charset) {
        final WordByteBuffer buffer = new WordByteBuffer(value);
        return decode(buffer, charset);
    }

    @Override
    public byte[] encode(final LocalDate value, final Charset charset) {
        final String formattedValue = value.format(dateTimeFormatter);
        return formattedValue.getBytes(charset);
    }

    private LocalDate decode(final WordByteBuffer buffer, final Charset charset) {
        final byte[] month = buffer.getWord();
        final byte[] dayOfMonth = buffer.getWord();

        return decode(month, dayOfMonth, charset);
    }

    private LocalDate decode(final byte[] month, final byte[] dayOfMonth, final Charset charset) {
        final String monthString = new String(month, charset);
        final String dayOfMonthString = new String(dayOfMonth, charset);

        return decode(monthString, dayOfMonthString);
    }

    private LocalDate decode(final String month, final String dayOfMonth) {
        final int dayOfMonthInteger = Integer.parseInt(dayOfMonth);
        final int monthInteger = Integer.parseInt(month);

        return LocalDate.now().withMonth(monthInteger).withDayOfMonth(dayOfMonthInteger);
    }

}

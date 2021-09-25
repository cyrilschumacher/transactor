package io.github.cyrilschumacher.data.codec;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Data type codec for the type: {@link LocalTime}.
 * <p>
 * The codec handles the {@link LocalTime} type with the following format: <code>HHmmss</code>.
 */
public final class LocalTimeDataTypeCodec implements DataTypeCodec<LocalTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HHmmss");

    @Override
    public LocalTime decode(final byte[] value, final Charset charset) {
        final WordByteBuffer buffer = new WordByteBuffer(value);
        return decode(buffer, charset);
    }

    @Override
    public byte[] encode(final LocalTime value, final Charset charset) {
        final String formattedValue = value.format(FORMATTER);
        return formattedValue.getBytes(charset);
    }

    private LocalTime decode(final WordByteBuffer buffer, final Charset charset) {
        final byte[] hour = buffer.getWord();
        final byte[] minute = buffer.getWord();
        final byte[] second = buffer.getWord();

        return decode(hour, minute, second, charset);
    }

    private LocalTime decode(final byte[] hour, final byte[] minute, final byte[] second, final Charset charset) {
        final String hourString = new String(hour, charset);
        final String minuteString = new String(minute, charset);
        final String secondString = new String(second, charset);

        return decode(hourString, minuteString, secondString);
    }

    private LocalTime decode(final String hour, final String minute, final String second) {
        final int hourInteger = Integer.parseInt(hour);
        final int minuteInteger = Integer.parseInt(minute);
        final int secondInteger = Integer.parseInt(second);

        return LocalTime.of(hourInteger, minuteInteger, secondInteger);
    }

}

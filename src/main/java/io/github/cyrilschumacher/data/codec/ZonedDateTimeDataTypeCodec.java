package io.github.cyrilschumacher.data.codec;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Data type codec for the type: {@link ZonedDateTime}.
 */
public final class ZonedDateTimeDataTypeCodec implements DataTypeCodec<ZonedDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMddHHmmss");
    private static final DateFormat FOUR_DIGITS_DATE_FORMAT = new SimpleDateFormat("yyyy");
    private static final DateFormat TWO_DIGITS_DATE_FORMAT = new SimpleDateFormat("yy");
    private static final ZoneId ZONE_ID = ZoneId.of("UTC");

    private final DateTimeFormatter dateTimeFormatter;
    private final ZoneId zoneId;

    public ZonedDateTimeDataTypeCodec() {
        this(ZONE_ID, FORMATTER);
    }

    public ZonedDateTimeDataTypeCodec(final ZoneId zoneId) {
        this(zoneId, FORMATTER);
    }

    public ZonedDateTimeDataTypeCodec(final DateTimeFormatter dateTimeFormatter) {
        this(ZONE_ID, dateTimeFormatter);
    }

    private ZonedDateTimeDataTypeCodec(final ZoneId zoneId, final DateTimeFormatter dateTimeFormatter) {
        this.zoneId = zoneId;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    private static int getYear(final String year) {
        try {
            final Date date = TWO_DIGITS_DATE_FORMAT.parse(year);
            return getYear(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Unable to read the year.");
        }
    }

    private static int getYear(final Date date) {
        final String fourDigitsDate = FOUR_DIGITS_DATE_FORMAT.format(date);
        return Integer.parseInt(fourDigitsDate);
    }

    @Override
    public ZonedDateTime decode(final byte[] value, final Charset charset) {
        final WordByteBuffer buffer = new WordByteBuffer(value);
        return decode(buffer, charset);
    }

    @Override
    public byte[] encode(final ZonedDateTime value, final Charset charset) {
        final String formattedValue = value.format(dateTimeFormatter);
        return formattedValue.getBytes(charset);
    }

    private ZonedDateTime decode(final WordByteBuffer buffer, final Charset charset) {
        final byte[] year = buffer.getWord();
        final byte[] month = buffer.getWord();
        final byte[] dayOfMonth = buffer.getWord();
        final byte[] hour = buffer.getWord();
        final byte[] minute = buffer.getWord();
        final byte[] second = buffer.getWord();

        return decode(year, month, dayOfMonth, hour, minute, second, charset);
    }

    private ZonedDateTime decode(final byte[] year, final byte[] month, final byte[] dayOfMonth, final byte[] hour, final byte[] minute, final byte[] second, final Charset charset) {
        final String yearString = new String(year, charset);
        final String monthString = new String(month, charset);
        final String dayOfMonthString = new String(dayOfMonth, charset);
        final String hourString = new String(hour, charset);
        final String minuteString = new String(minute, charset);
        final String secondString = new String(second, charset);

        return decode(yearString, monthString, dayOfMonthString, hourString, minuteString, secondString);
    }

    private ZonedDateTime decode(final String year, final String month, final String dayOfMonth, final String hour, final String minute, final String second) {
        final int yearInteger = getYear(year);
        final int monthInteger = Integer.parseInt(month);
        final int dayOfMonthInteger = Integer.parseInt(dayOfMonth);
        final int hourInteger = Integer.parseInt(hour);
        final int minuteInteger = Integer.parseInt(minute);
        final int secondInteger = Integer.parseInt(second);

        return ZonedDateTime.of(yearInteger, monthInteger, dayOfMonthInteger, hourInteger, minuteInteger, secondInteger, 0, zoneId);
    }

}

package io.github.cyrilschumacher.data.codec;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry for {@link DataTypeCodec}.
 * <p>
 * The registry provides, by default, a codec for each of the following classes:
 * <ul>
 *     <li>{@link ByteArrayDataTypeCodec} for <code>byte[]</code>;</li>
 *     <li>{@link LocalDateDataTypeCodec} for {@link LocalDate};</li>
 *     <li>{@link LocalDateTimeDataTypeCodec} for {@link LocalDateTime};</li>
 *     <li>{@link LocalTimeDataTypeCodec} for {@link LocalTime};</li>
 *     <li>{@link StringDataTypeCodec} for {@link String};</li>
 *     <li>{@link YearMonthDataTypeCodec} for {@link YearMonth};</li>
 *     <li>{@link ZonedDateTimeDataTypeCodec} for {@link ZonedDateTime};</li>
 * </ul>
 * <p>
 * These codecs can be replaced with the method: {@link #register(Class, DataTypeCodec)}.
 */
public class DataTypeCodecRegistry {

    private final Map<Class<?>, DataTypeCodec<?>> dataTypeCodecs;

    private static final Map<Class<?>, DataTypeCodec<?>> DEFAULT_DATA_TYPE_CODEC = Map.of(
            byte[].class, new ByteArrayDataTypeCodec(),
            LocalDate.class, new LocalDateDataTypeCodec(),
            LocalDateTime.class, new LocalDateTimeDataTypeCodec(),
            LocalTime.class, new LocalTimeDataTypeCodec(),
            String.class, new StringDataTypeCodec(),
            YearMonth.class, new YearMonthDataTypeCodec(),
            ZonedDateTime.class, new ZonedDateTimeDataTypeCodec()
    );

    public DataTypeCodecRegistry() {
        this(DEFAULT_DATA_TYPE_CODEC);
    }

    public DataTypeCodecRegistry(final Map<Class<?>, DataTypeCodec<?>> dataTypeCodecMap) {
        this.dataTypeCodecs = new HashMap<>(dataTypeCodecMap);
    }

    public DataTypeCodecRegistry register(final Class<?> klass, final DataTypeCodec<?> dataTypeCodec) {
        this.dataTypeCodecs.put(klass, dataTypeCodec);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> DataTypeCodec<T> forClass(final Class<T> klass) {
        return (DataTypeCodec<T>) dataTypeCodecs.get(klass);
    }

}

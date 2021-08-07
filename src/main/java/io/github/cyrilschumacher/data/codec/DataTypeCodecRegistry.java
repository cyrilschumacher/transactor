package io.github.cyrilschumacher.data.codec;

import java.time.*;
import java.util.HashMap;
import java.util.Map;

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

    public DataTypeCodecRegistry registry(final Class<?> klass, final DataTypeCodec<?> dataTypeCodec) {
        this.dataTypeCodecs.put(klass, dataTypeCodec);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> DataTypeCodec<T> forClass(final Class<T> klass) {
        return (DataTypeCodec<T>) dataTypeCodecs.get(klass);
    }

}

package io.github.cyrilschumacher.data.codec;

import java.time.*;
import java.util.Map;

public class DataTypeCodecRegistry {

    private final Map<Class<?>, DataTypeCodec<?>> dataTypeCodecMap;

    public DataTypeCodecRegistry() {
        this.dataTypeCodecMap = Map.of(
                byte[].class, new ByteArrayDataTypeCodec(),
                LocalDate.class, new LocalDateDataTypeCodec(),
                LocalDateTime.class, new LocalDateTimeDataTypeCodec(),
                LocalTime.class, new LocalTimeDataTypeCodec(),
                String.class, new StringDataTypeCodec(),
                YearMonth.class, new YearMonthDataTypeCodec(),
                ZonedDateTime.class, new ZonedDateTimeDataTypeCodec()
        );
    }

    public DataTypeCodecRegistry registry(final Class<?> klass, final DataTypeCodec<?> dataTypeCodec) {
        this.dataTypeCodecMap.put(klass, dataTypeCodec);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> DataTypeCodec<T> forClass(final Class<T> klass) {
        return (DataTypeCodec<T>) dataTypeCodecMap.get(klass);
    }

}

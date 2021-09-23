package io.github.cyrilschumacher.data.codec;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;

/**
 * Unit tests for the class: {@link DataTypeCodecRegistry}.
 */
@DisplayName("Unit tests for `DataTypeCodecRegistry` class.")
class DataTypeCodecRegistryTest {

    @Test
    @DisplayName("Should not return a codec for an unsupported class type.")
    void shouldNotReturnCodecForUnsupportedClassType() {
        // Given
        var dataTypeCodecRegistry = new DataTypeCodecRegistry();

        // When
        var dataTypeCodec = dataTypeCodecRegistry.forClass(Object.class);

        // Then
        MatcherAssert.assertThat(dataTypeCodec, nullValue());
    }

    @Test
    @DisplayName("Should return a custom codec if registered.")
    void shouldReturnCustomCodecIfRegistered() {
        // Given
        var dataTypeCodecMock = Mockito.mock(DataTypeCodec.class);

        var dataTypeCodecRegistry = new DataTypeCodecRegistry();
        dataTypeCodecRegistry.register(Object.class, dataTypeCodecMock);

        // When
        var dataTypeCodec = dataTypeCodecRegistry.forClass(Object.class);

        // Then
        MatcherAssert.assertThat(dataTypeCodec, equalTo(dataTypeCodecMock));
    }

    @ParameterizedTest(name = "Should return a codec: {1} for a class: {0}.")
    @MethodSource("provideClasses")
    @DisplayName("Should return a codec for a specific class.")
    void shouldReturnData(Class<?> klass, Class<?> expectedKlass) {
        // Given
        var dataTypeCodecRegistry = new DataTypeCodecRegistry();

        // When
        var dataTypeCodec = dataTypeCodecRegistry.forClass(klass);

        // Then
        MatcherAssert.assertThat(dataTypeCodec, instanceOf(expectedKlass));
    }

    private static Stream<Arguments> provideClasses() {
        return Stream.of(
                Arguments.of(byte[].class, ByteArrayDataTypeCodec.class),
                Arguments.of(LocalDate.class, LocalDateDataTypeCodec.class),
                Arguments.of(LocalDateTime.class, LocalDateTimeDataTypeCodec.class),
                Arguments.of(LocalTime.class, LocalTimeDataTypeCodec.class),
                Arguments.of(String.class, StringDataTypeCodec.class),
                Arguments.of(YearMonth.class, YearMonthDataTypeCodec.class),
                Arguments.of(ZonedDateTime.class, ZonedDateTimeDataTypeCodec.class)
        );
    }

}
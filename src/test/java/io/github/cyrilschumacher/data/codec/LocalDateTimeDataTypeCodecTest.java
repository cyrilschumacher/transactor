package io.github.cyrilschumacher.data.codec;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.equalTo;

/**
 * Unit tests for the class: {@link LocalDateTimeDataTypeCodec}.
 */
@DisplayName("Unit tests for `LocalDateTimeDataTypeCodec` class.")
class LocalDateTimeDataTypeCodecTest {

    private final LocalDateTimeDataTypeCodec localDateTimeDataTypeCodec = new LocalDateTimeDataTypeCodec();

    @Test
    @DisplayName("Should decode a value.")
    void shouldDecodeValue() {
        // Given
        var value = "0715180347".getBytes(StandardCharsets.US_ASCII);

        // When
        var decodedValue = localDateTimeDataTypeCodec.decode(value, StandardCharsets.US_ASCII);

        // Then
        var expectedDecodedValue = LocalDateTime.of(2021, 7, 15, 18, 3, 47);
        MatcherAssert.assertThat(decodedValue, equalTo(expectedDecodedValue));
    }

    @Test
    @DisplayName("Should encode a value.")
    void shouldEncodeValue() {
        // Given
        var value = LocalDateTime.of(2021, 7, 15, 18, 3, 47);

        // When
        var encodedValue = localDateTimeDataTypeCodec.encode(value, StandardCharsets.US_ASCII);

        // Then
        var encodedEncodedValueString = new String(encodedValue, StandardCharsets.US_ASCII);
        MatcherAssert.assertThat(encodedEncodedValueString, equalTo("210715180347"));
    }

}
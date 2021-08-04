package io.github.cyrilschumacher.data.codec;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.hamcrest.Matchers.equalTo;

/**
 * Unit tests for the class: {@link LocalDateDataTypeCodec}.
 */
@DisplayName("Unit tests for `LocalDateDataTypeCodec` class.")
class LocalDateDataTypeCodecTest {

    private final LocalDateDataTypeCodec localDateDataTypeCodec = new LocalDateDataTypeCodec();

    @Test
    @DisplayName("Should decode a value.")
    void shouldDecodeValue() {
        // Given
        var value = "0715".getBytes(StandardCharsets.US_ASCII);

        // When
        var decodedValue = localDateDataTypeCodec.decode(value, StandardCharsets.US_ASCII);

        // Then
        var expectedDecodedValue = LocalDate.of(2021, 7, 15);
        MatcherAssert.assertThat(decodedValue, equalTo(expectedDecodedValue));
    }

    @Test
    @DisplayName("Should encode a value.")
    void shouldEncodeValue() {
        // Given
        var value = LocalDate.of(2021, 7, 15);

        // When
        var encodedValue = localDateDataTypeCodec.encode(value, StandardCharsets.US_ASCII);

        // Then
        var encodedEncodedValueString = new String(encodedValue, StandardCharsets.US_ASCII);
        MatcherAssert.assertThat(encodedEncodedValueString, equalTo("0715"));
    }

}
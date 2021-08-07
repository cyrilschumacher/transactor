package io.github.cyrilschumacher.data.codec;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

import static org.hamcrest.Matchers.equalTo;

/**
 * Unit tests for the class: {@link LocalTimeDataTypeCodec}.
 */
@DisplayName("Unit tests for `LocalTimeDataTypeCodec` class.")
class LocalTimeDataTypeCodecTest {

    private final LocalTimeDataTypeCodec localTimeDataTypeCodec = new LocalTimeDataTypeCodec();

    @Test
    @DisplayName("Should decode a value.")
    void shouldDecodeValue() {
        // Given
        var value = "170321".getBytes(StandardCharsets.US_ASCII);

        // When
        var decodedValue = localTimeDataTypeCodec.decode(value, StandardCharsets.US_ASCII);

        // Then
        var expectedDecodedValue = LocalTime.of(17, 3, 21);
        MatcherAssert.assertThat(decodedValue, equalTo(expectedDecodedValue));
    }

    @Test
    @DisplayName("Should encode a value.")
    void shouldEncodeValue() {
        // Given
        var value = LocalTime.of(17, 3, 21);

        // When
        var encodedValue = localTimeDataTypeCodec.encode(value, StandardCharsets.US_ASCII);

        // Then
        var encodedEncodedValueString = new String(encodedValue, StandardCharsets.US_ASCII);
        MatcherAssert.assertThat(encodedEncodedValueString, equalTo("170321"));
    }

}
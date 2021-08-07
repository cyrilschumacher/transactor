package io.github.cyrilschumacher.data.codec;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.hamcrest.Matchers.equalTo;

/**
 * Unit tests for the class: {@link ZonedDateTimeDataTypeCodec}.
 */
@DisplayName("Unit tests for `ZonedDateTimeDataTypeCodec` class.")
class ZonedDateTimeDataTypeCodecTest {

    private final ZonedDateTimeDataTypeCodec zonedDateTimeDataTypeCodec = new ZonedDateTimeDataTypeCodec();

    @Test
    @DisplayName("Should decode a value.")
    void shouldDecodeValue() {
        // Given
        var value = "210715173414".getBytes(StandardCharsets.US_ASCII);

        // When
        var decodedValue = zonedDateTimeDataTypeCodec.decode(value, StandardCharsets.US_ASCII);

        // Then
        var zoneId = ZoneId.of("UTC");
        var expectedDecodedValue = ZonedDateTime.of(2021, 7, 15, 17, 34, 14, 0, zoneId);

        MatcherAssert.assertThat(decodedValue, equalTo(expectedDecodedValue));
    }

    @Test
    @DisplayName("Should encode a value.")
    void shouldEncodeValue() {
        // Given
        var zoneId = ZoneId.of("UTC");
        var value = ZonedDateTime.of(2021, 7, 15, 17, 34, 14, 325, zoneId);

        // When
        var encodedValue = zonedDateTimeDataTypeCodec.encode(value, StandardCharsets.US_ASCII);

        // Then
        var encodedEncodedValueString = new String(encodedValue, StandardCharsets.US_ASCII);
        MatcherAssert.assertThat(encodedEncodedValueString, equalTo("0715173414"));
    }

}
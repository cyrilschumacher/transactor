package io.github.cyrilschumacher.data.codec;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.YearMonth;

import static org.hamcrest.Matchers.equalTo;

/**
 * Unit tests for the class: {@link YearMonthDataTypeCodec}.
 */
@DisplayName("Unit tests for `YearMonthDataTypeCodec` class.")
class YearMonthDataTypeCodecTest {

    private final YearMonthDataTypeCodec yearMonthDataTypeCodec = new YearMonthDataTypeCodec();

    @Test
    @DisplayName("Should decode a value.")
    void shouldDecodeValue() {
        // Given
        var value = "2107".getBytes(StandardCharsets.US_ASCII);

        // When
        var decodedValue = yearMonthDataTypeCodec.decode(value, StandardCharsets.US_ASCII);

        // Then
        var expectedDecodedValue = YearMonth.of(2021, 7);
        MatcherAssert.assertThat(decodedValue, equalTo(expectedDecodedValue));
    }

    @Test
    @DisplayName("Should encode a value.")
    void shouldEncodeValue() {
        // Given
        var value = YearMonth.of(2021, 7);

        // When
        var encodedValue = yearMonthDataTypeCodec.encode(value, StandardCharsets.US_ASCII);

        // Then
        var encodedEncodedValueString = new String(encodedValue, StandardCharsets.US_ASCII);
        MatcherAssert.assertThat(encodedEncodedValueString, equalTo("2107"));
    }

}
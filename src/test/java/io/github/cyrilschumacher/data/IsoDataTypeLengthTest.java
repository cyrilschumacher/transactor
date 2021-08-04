package io.github.cyrilschumacher.data;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

/**
 * Unit tests for the class: {@link IsoDataTypeLength}.
 */
@DisplayName("Unit tests for `DefaultDataTypeLength` class.")
class IsoDataTypeLengthTest {

    @Nested
    @DisplayName("For a fixed length element.")
    class ForFixedLengthElement {

        @Nested
        @DisplayName("When the element is decoded.")
        class WhenElementIsDecoded {

            @Test
            @DisplayName("Should encode a value.")
            void shouldEncodeValue() {
                // Given
                var dataElementMock = Mockito.mock(DataElement.class);

                var value = "4035502197074574".getBytes(StandardCharsets.US_ASCII);
                var types = new DataType[]{DataType.ALPHA};

                Mockito.when(dataElementMock.getLength()).thenReturn(16);
                Mockito.when(dataElementMock.getTypes()).thenReturn(types);

                // When
                var encodedValue = IsoDataTypeLength.FIXED.encode(value, dataElementMock, StandardCharsets.US_ASCII);

                // Then
                var encodedValueString = new String(encodedValue, StandardCharsets.US_ASCII);
                MatcherAssert.assertThat(encodedValueString, equalTo("4035502197074574"));
            }

            @Test
            @DisplayName("Should decode a value.")
            void shouldDecodeValue() {
                // Given
                var dataElementMock = Mockito.mock(DataElement.class);

                var value = "4035502197074574".getBytes(StandardCharsets.US_ASCII);
                var buffer = ByteBuffer.wrap(value);

                Mockito.when(dataElementMock.getLength()).thenReturn(16);

                // When
                var decodedValue = IsoDataTypeLength.FIXED.decode(buffer, dataElementMock, StandardCharsets.US_ASCII);

                // Then
                var decodedValueString = new String(decodedValue, StandardCharsets.US_ASCII);
                MatcherAssert.assertThat(decodedValueString, equalTo("4035502197074574"));
            }

            @Test
            @DisplayName("Should encode a value with an inaccurate length.")
            void shouldDecodeValueWithInaccurateLength() {
                // Given
                var dataElementMock = Mockito.mock(DataElement.class);

                var value = "4035502197074574".getBytes(StandardCharsets.US_ASCII);
                var buffer = ByteBuffer.wrap(value);

                Mockito.when(dataElementMock.getLength()).thenReturn(10);

                // When
                var decodedValue = IsoDataTypeLength.FIXED.decode(buffer, dataElementMock, StandardCharsets.US_ASCII);

                // Then
                var decodedValueString = new String(decodedValue, StandardCharsets.US_ASCII);
                MatcherAssert.assertThat(decodedValueString, equalTo("4035502197"));
            }

            @Test
            @DisplayName("Should throw an exception for a length too long.")
            void shouldThrowExceptionForLengthTooLong() {
                // Given
                var dataElementMock = Mockito.mock(DataElement.class);

                var value = "0164035502197074574".getBytes(StandardCharsets.US_ASCII);
                var buffer = ByteBuffer.wrap(value);

                Mockito.when(dataElementMock.getLength()).thenReturn(32);

                // When
                var exception = Assertions.assertThrows(BufferUnderflowException.class, () -> IsoDataTypeLength.FIXED.decode(buffer, dataElementMock, StandardCharsets.US_ASCII));

                // Then
                MatcherAssert.assertThat(exception.getMessage(), nullValue());
                MatcherAssert.assertThat(exception.getCause(), nullValue());
            }

        }

    }

    @Nested
    @DisplayName("For a variable length element.")
    class ForVariableLengthElement {

        @Nested
        @DisplayName("When the element is decoded.")
        class WhenElementIsDecoded {

            @Test
            @DisplayName("Should encode a value with length on two digits.")
            void shouldDecodeValueWithLengthOnTwoDigitsAsLength() {
                // Given
                var dataElementMock = Mockito.mock(DataElement.class);

                var variableDataTypeLength = IsoDataTypeLength.VARIABLE;
                var value = "164035502197074574".getBytes(StandardCharsets.US_ASCII);
                var buffer = ByteBuffer.wrap(value);

                Mockito.when(dataElementMock.getLength()).thenReturn(99);

                // When
                var decodedValue = variableDataTypeLength.decode(buffer, dataElementMock, StandardCharsets.US_ASCII);

                // Then
                var decodedValueString = new String(decodedValue, StandardCharsets.US_ASCII);
                MatcherAssert.assertThat(decodedValueString, equalTo("4035502197074574"));
            }

            @Test
            @DisplayName("Should encode a value with length on three digits.")
            void shouldDecodeValueWithLengthOnThreeDigits() {
                // Given
                var dataElementMock = Mockito.mock(DataElement.class);

                var variableDataTypeLength = IsoDataTypeLength.VARIABLE;
                var value = "0164035502197074574".getBytes(StandardCharsets.US_ASCII);
                var buffer = ByteBuffer.wrap(value);

                Mockito.when(dataElementMock.getLength()).thenReturn(999);

                // When
                var decodedValue = variableDataTypeLength.decode(buffer, dataElementMock, StandardCharsets.US_ASCII);

                // Then
                var decodedValueString = new String(decodedValue, StandardCharsets.US_ASCII);
                MatcherAssert.assertThat(decodedValueString, equalTo("4035502197074574"));
            }

        }

        @Nested
        @DisplayName("When the element is encoded.")
        class WhenElementIsEncoded {

            @Test
            @DisplayName("Should encode a value with length on two digits.")
            void shouldEncodeValueWithLengthOnTwoDigitsAsLength() {
                // Given
                var dataElementMock = Mockito.mock(DataElement.class);

                var variableDataTypeLength = IsoDataTypeLength.VARIABLE;
                var value = "4035502197074574".getBytes(StandardCharsets.US_ASCII);

                Mockito.when(dataElementMock.getLength()).thenReturn(99);

                // When
                var encodedValue = variableDataTypeLength.encode(value, dataElementMock, StandardCharsets.US_ASCII);

                // Then
                var encodedValueString = new String(encodedValue, StandardCharsets.US_ASCII);
                MatcherAssert.assertThat(encodedValueString, equalTo("164035502197074574"));
            }

            @Test
            @DisplayName("Should encode a value with length on three digits.")
            void shouldEncodeValueWithLengthOnThreeDigits() {
                // Given
                var dataElementMock = Mockito.mock(DataElement.class);

                var variableDataTypeLength = IsoDataTypeLength.VARIABLE;
                var value = "4035502197074574".getBytes(StandardCharsets.US_ASCII);

                Mockito.when(dataElementMock.getLength()).thenReturn(999);

                // When
                var encodedValue = variableDataTypeLength.encode(value, dataElementMock, StandardCharsets.US_ASCII);

                // Then
                var encodedValueString = new String(encodedValue, StandardCharsets.US_ASCII);
                MatcherAssert.assertThat(encodedValueString, equalTo("0164035502197074574"));
            }

            @Test
            @DisplayName("Should throw an exception for an exceeded length.")
            void shouldThrowExceptionForExceededLength() {
                // Given
                var dataElementMock = Mockito.mock(DataElement.class);

                var variableDataTypeLength = IsoDataTypeLength.VARIABLE;
                var value = "4035502197074574".getBytes(StandardCharsets.US_ASCII);

                Mockito.when(dataElementMock.getLength()).thenReturn(9);

                // When
                var exception = Assertions.assertThrows(IsoDataTypeLength.IllegalElementLengthException.class, () -> variableDataTypeLength.encode(value, dataElementMock, StandardCharsets.US_ASCII));

                // Then
                var expectedException = new IsoDataTypeLength.IllegalElementLengthException(16, 9);
                MatcherAssert.assertThat(exception, equalTo(expectedException));
            }

        }

    }

}
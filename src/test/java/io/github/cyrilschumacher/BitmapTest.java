package io.github.cyrilschumacher;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;

/**
 * Unit tests for the class: {@link Bitmap}.
 */
@DisplayName("Unit tests for `Bitmap` class.")
class BitmapTest {

    @Test
    @DisplayName("Should return a `String` representation.")
    void shouldReturnStringRepresentation() {
        // Given
        var builder = Bitmap.builder().add(2, 3, 4, 5).range(2);

        // When
        var bitmap = builder.build();

        // Then
        MatcherAssert.assertThat(bitmap, hasToString("Bitmap{dataFields=[2, 3, 4, 5], range=2}"));
    }

    @Nested
    @DisplayName("When a bitmap is built.")
    class WhenBitmapIsBuilt {

        @Test
        @DisplayName("Should throw an exception for an invalid range.")
        void shouldThrowExceptionForInvalidRange() {
            // Given
            var builder = Bitmap.builder().add(65);

            // When
            var exception = Assertions.assertThrows(IllegalArgumentException.class, () -> builder.range(0));

            // Then
            MatcherAssert.assertThat(exception.getMessage(), equalTo("The range number must be greater than 1."));
            MatcherAssert.assertThat(exception.getCause(), is(nullValue()));
        }

        @Test
        @DisplayName("Should build a bitmap.")
        void shouldBuildBitmap() {
            // Given
            var bitmap = Bitmap.builder().add(3, 4, 28, 39, 45, 51, 54, 64).build();

            // When
            var value = bitmap.toByteArray();

            // Then
            var expectedBitmap = new byte[]{0x30, 0x0, 0x0, 0x10, 0x2, 0x8, 0x24, 0x1};

            MatcherAssert.assertThat(value, equalTo(expectedBitmap));
            MatcherAssert.assertThat(bitmap.getDataFields(), containsInAnyOrder(3, 4, 28, 39, 45, 51, 54, 64));
            MatcherAssert.assertThat(bitmap.getRange(), equalTo(1));
        }

        @Test
        @DisplayName("Should build a bitmap, with only first bit.")
        void shouldBuildBitmapWithOnlyFirstBit() {
            // Given
            var bitmap = Bitmap.builder().add(3, 4).build();

            // When
            var value = bitmap.toByteArray();

            // Then
            var expectedBitmap = new byte[]{0x30, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};

            MatcherAssert.assertThat(value, equalTo(expectedBitmap));
            MatcherAssert.assertThat(bitmap.getDataFields(), containsInAnyOrder(3, 4));
            MatcherAssert.assertThat(bitmap.getRange(), equalTo(1));
        }

        @Test
        @DisplayName("Should build a bitmap, with only second bit.")
        void shouldBuildBitmapWithOnlySecondBit() {
            // Given
            var bitmap = Bitmap.builder().add(5, 6, 7, 8).build();

            // When
            var value = bitmap.toByteArray();

            // Then
            var expectedBitmap = new byte[]{0xF, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};

            MatcherAssert.assertThat(value, equalTo(expectedBitmap));
            MatcherAssert.assertThat(bitmap.getDataFields(), containsInAnyOrder(5, 6, 7, 8));
            MatcherAssert.assertThat(bitmap.getRange(), equalTo(1));
        }

        @Test
        @DisplayName("Should build a bitmap with all fields.")
        void shouldBuildWithAllFields() {
            // Given
            var dataFields = IntStream.range(1, 65).boxed().collect(Collectors.toUnmodifiableSet());
            var bitmap = Bitmap.builder().add(dataFields).build();

            // Then
            var value = bitmap.toByteArray();

            // Then
            var expectedBitmap = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

            MatcherAssert.assertThat(value, equalTo(expectedBitmap));
            MatcherAssert.assertThat(bitmap.getDataFields(), equalTo(dataFields));
            MatcherAssert.assertThat(bitmap.getRange(), equalTo(1));
        }

        @Test
        @DisplayName("Should build a secondary bitmap.")
        void shouldBuildSecondaryBitmap() {
            // Given
            var bitmap = Bitmap.builder().add(65).range(2).build();

            // When
            var value = bitmap.toByteArray();

            // Then
            var expectedBitmap = new byte[]{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, (byte) 0x80, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};

            MatcherAssert.assertThat(value, equalTo(expectedBitmap));
            MatcherAssert.assertThat(bitmap.getDataFields(), containsInAnyOrder(65));
            MatcherAssert.assertThat(bitmap.getRange(), equalTo(2));
        }

    }

    @Nested
    @DisplayName("When a bitmap is parsed.")
    class WhenBitmapIsParsed {

        @Nested
        @DisplayName("For an ASCII.")
        class ForAscii {

            @Test
            @DisplayName("Should parse a first bit.")
            void shouldParseFirstBit() {
                // When
                var bitmap = Bitmap.parse("3000000000000000");

                // Then
                MatcherAssert.assertThat(bitmap.getDataFields(), containsInAnyOrder(3, 4));
                MatcherAssert.assertThat(bitmap.getRange(), equalTo(1));
            }

            @Test
            @DisplayName("Should parse a second bit.")
            void shouldParseSecondBit() {
                // When
                var bitmap = Bitmap.parse("0F00000000000000");

                // Then
                MatcherAssert.assertThat(bitmap.getDataFields(), containsInAnyOrder(5, 6, 7, 8));
                MatcherAssert.assertThat(bitmap.getRange(), equalTo(1));
            }

            @Test
            @DisplayName("Should parse all bits.")
            void shouldParseAllBits() {
                // When
                var bitmap = Bitmap.parse("3000001002082401");

                // Then
                MatcherAssert.assertThat(bitmap.getDataFields(), containsInAnyOrder(3, 4, 28, 39, 45, 51, 54, 64));
                MatcherAssert.assertThat(bitmap.getRange(), equalTo(1));
            }

            @Test
            @DisplayName("Should parse a bitmap with all fields..")
            void shouldParseBitmapWithAllFields() {
                // When
                var bitmap = Bitmap.parse("FFFFFFFFFFFFFFFF");

                // Then
                var expected = IntStream.range(1, 65).boxed().collect(Collectors.toUnmodifiableSet());

                MatcherAssert.assertThat(bitmap.getDataFields(), equalTo(expected));
                MatcherAssert.assertThat(bitmap.getRange(), equalTo(1));
            }

            @Test
            @DisplayName("Should parse a secondary bitmap.")
            void shouldParseSecondaryBitmap() {
                // When
                var bitmap = Bitmap.parse("0F000000000000000F00000000000000");

                // Then
                MatcherAssert.assertThat(bitmap.getDataFields(), containsInAnyOrder(5, 6, 7, 8, 69, 70, 71, 72));
                MatcherAssert.assertThat(bitmap.getRange(), equalTo(2));
            }

        }

        @Nested
        @DisplayName("For a binary.")
        class ForBinary {

            @Test
            @DisplayName("Should parse a first bit.")
            void shouldParseFirstBit() {
                // Given
                var bitmapToParse = new byte[]{0x30, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};

                // When
                var bitmap = Bitmap.parse(bitmapToParse);

                // Then
                MatcherAssert.assertThat(bitmap.getDataFields(), containsInAnyOrder(3, 4));
                MatcherAssert.assertThat(bitmap.getRange(), equalTo(1));
            }

            @Test
            @DisplayName("Should parse a second bit.")
            void shouldParseSecondBit() {
                // Given
                var bitmapToParse = new byte[]{0x0F, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};

                // When
                var bitmap = Bitmap.parse(bitmapToParse);

                // Then
                MatcherAssert.assertThat(bitmap.getDataFields(), containsInAnyOrder(5, 6, 7, 8));
                MatcherAssert.assertThat(bitmap.getRange(), equalTo(1));
            }

            @Test
            @DisplayName("Should parse all bits.")
            void shouldParseAllBits() {
                // Given
                var bitmapToParse = new byte[]{0x72, 0x34, 0x5, 0x41, 0x8, (byte) 0xE0, (byte) 0x90, 0x0};

                // When
                var bitmap = Bitmap.parse(bitmapToParse);

                // Then
                MatcherAssert.assertThat(bitmap.getDataFields(), containsInAnyOrder(2, 3, 4, 7, 11, 12, 14, 22, 24, 26, 32, 37, 41, 42, 43, 49, 52));
                MatcherAssert.assertThat(bitmap.getRange(), equalTo(1));
            }

            @Test
            @DisplayName("Should parse a bitmap with all fields.")
            void shouldParseBitmapWithAllFields() {
                // Given
                var bitmapToParse = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

                // When
                var bitmap = Bitmap.parse(bitmapToParse);

                // Then
                var expected = IntStream.range(1, 65).boxed().collect(Collectors.toUnmodifiableSet());
                MatcherAssert.assertThat(bitmap.getDataFields(), equalTo(expected));
                MatcherAssert.assertThat(bitmap.getRange(), equalTo(1));
            }

            @Test
            @DisplayName("Should parse a secondary bitmap.")
            void shouldParseSecondaryBitmap() {
                // Given
                var bitmapToParse = new byte[]{0xF, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0xF, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};

                // When
                var bitmap = Bitmap.parse(bitmapToParse);

                // Then
                MatcherAssert.assertThat(bitmap.getDataFields(), containsInAnyOrder(5, 6, 7, 8, 69, 70, 71, 72));
                MatcherAssert.assertThat(bitmap.getRange(), equalTo(2));
            }

        }

    }

}
package io.github.cyrilschumacher;

import jakarta.xml.bind.DatatypeConverter;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

/**
 * Unit tests for the class: {@link MessageData}.
 */
@DisplayName("Unit tests for `Message` class.")
class MessageDataTest {

    @Test
    @DisplayName("Should return a `String` representation.")
    void shouldReturnStringRepresentation() {
        // Given
        var builder = MessageData.<DummyDataElement>builder().add(DummyDataElement.PROCESSING_CODE, "40").add(DummyDataElement.AMOUNT, "1024");

        // When
        var message = builder.build();

        // Then
        MatcherAssert.assertThat(message, hasToString("MessageData{elements=[PROCESSING_CODE, AMOUNT], charset=US-ASCII}"));
    }

    @Nested
    @DisplayName("When the object is compared.")
    class WhenObjectIsCompared {

        @Test
        @DisplayName("Should return true if it is the same object.")
        @SuppressWarnings({"EqualsWithItself", "ConstantConditions"})
        void shouldReturnTrueForSameObject() {
            // Given
            var dateExpiration = YearMonth.of(2020, 10);
            var personalIdentificationData = new byte[]{(byte) 0xA8, (byte) 0xEC, (byte) 0xBC, 0x46, (byte) 0xAD, (byte) 0xEF, (byte) 0xD3, (byte) 0x8F};

            var messageData = MessageData.<DummyDataElement>builder().add(DummyDataElement.AMOUNT, "1200").add(DummyDataElement.DATE_EXPIRATION, dateExpiration).add(DummyDataElement.PERSONAL_IDENTIFICATION_DATA, personalIdentificationData).build();

            // When
            var isEqual = messageData.equals(messageData);

            // Then
            MatcherAssert.assertThat(isEqual, is(true));
        }

        @Test
        @DisplayName("Should return true if it is the same content, but not the same object.")
        void shouldReturnTrueForSameContentButNotSameObject() {
            // Given
            var dateExpiration = YearMonth.of(2020, 10);
            var personalIdentificationData = new byte[]{(byte) 0xA8, (byte) 0xEC, (byte) 0xBC, 0x46, (byte) 0xAD, (byte) 0xEF, (byte) 0xD3, (byte) 0x8F};

            var messageData1 = MessageData.<DummyDataElement>builder().add(DummyDataElement.AMOUNT, "1200").add(DummyDataElement.DATE_EXPIRATION, dateExpiration).add(DummyDataElement.PERSONAL_IDENTIFICATION_DATA, personalIdentificationData).build();
            var messageData2 = MessageData.<DummyDataElement>builder().add(DummyDataElement.AMOUNT, "1200").add(DummyDataElement.DATE_EXPIRATION, dateExpiration).add(DummyDataElement.PERSONAL_IDENTIFICATION_DATA, personalIdentificationData).build();

            // When
            var isEqual = messageData2.equals(messageData1);

            // Then
            MatcherAssert.assertThat(isEqual, is(true));
        }

        @Test
        @DisplayName("Should return false if it is not the same content.")
        void shouldReturnFalseForDifferentContent() {
            // Given
            var dateExpiration = YearMonth.of(2020, 10);
            var personalIdentificationData = new byte[]{(byte) 0xA8, (byte) 0xEC, (byte) 0xBC, 0x46, (byte) 0xAD, (byte) 0xEF, (byte) 0xD3, (byte) 0x8F};

            var messageData1 = MessageData.<DummyDataElement>builder().add(DummyDataElement.AMOUNT, "1200").add(DummyDataElement.DATE_EXPIRATION, dateExpiration).add(DummyDataElement.PERSONAL_IDENTIFICATION_DATA, personalIdentificationData).build();
            var messageData2 = MessageData.<DummyDataElement>builder().add(DummyDataElement.AMOUNT, "1201").add(DummyDataElement.DATE_EXPIRATION, dateExpiration).add(DummyDataElement.PERSONAL_IDENTIFICATION_DATA, personalIdentificationData).build();

            // When
            var isEqual = messageData2.equals(messageData1);

            // Then
            MatcherAssert.assertThat(isEqual, is(false));
        }

        @Test
        @DisplayName("Should return false if is not the expected type.")
        void shouldReturnFalseForDifferentClassType() {
            // Given
            var messageData = MessageData.<DummyDataElement>builder().add(DummyDataElement.AMOUNT, "1200").build();
            var object = new Object();

            // When
            var isEqual = messageData.equals(object);

            // Then
            MatcherAssert.assertThat(isEqual, is(false));
        }

        @Test
        @DisplayName("Should return different hash code for different data field.")
        void shouldReturnDifferentHashCodeForDifferentDataField() {
            // Given
            var messageData1 = MessageData.<DummyDataElement>builder().add(DummyDataElement.AMOUNT, "1200").build();
            var messageData2 = MessageData.<DummyDataElement>builder().add(DummyDataElement.AMOUNT, "1201").build();

            // When
            var hashCode1 = messageData1.hashCode();
            var hashCode2 = messageData2.hashCode();

            // Then
            MatcherAssert.assertThat(hashCode2, not(equalTo(hashCode1)));
        }

        @Test
        @DisplayName("Should return same hash code for same data field.")
        void shouldReturnSameHashCodeForSameField() {
            // Given
            var messageData1 = MessageData.<DummyDataElement>builder().add(DummyDataElement.AMOUNT, "1200").build();
            var messageData2 = MessageData.<DummyDataElement>builder().add(DummyDataElement.AMOUNT, "1200").build();

            // When
            var hashCode1 = messageData1.hashCode();
            var hashCode2 = messageData2.hashCode();

            // Then
            MatcherAssert.assertThat(hashCode2, equalTo(hashCode1));
        }

        @Test
        @DisplayName("Should return same hash code for same object.")
        void shouldReturnSameHashCodeForForSameObject() {
            // Given
            var messageData = MessageData.<DummyDataElement>builder().add(DummyDataElement.AMOUNT, "1200").build();

            // When
            var hashCode = messageData.hashCode();

            // Then
            MatcherAssert.assertThat(hashCode, equalTo(hashCode));
        }

    }

    @Nested
    @DisplayName("When a message is created.")
    class WhenMessageDataIsCreated {

        @Test
        @DisplayName("Should create a message.")
        void shouldBuildMessage() throws IOException {
            // Given
            var dateExpiration = YearMonth.of(2020, 10);
            var dateTimeLocalTransaction = LocalDateTime.of(2021, 8, 2, 15, 22, 21);
            var personalIdentificationData = new byte[]{(byte) 0xA8, (byte) 0xEC, (byte) 0xBC, 0x46, (byte) 0xAD, (byte) 0xEF, (byte) 0xD3, (byte) 0x8F};
            var transmissionDateTime = ZonedDateTime.of(2021, 8, 2, 15, 22, 21, 0, ZoneOffset.UTC);

            var messageBuilder = MessageData.<DummyDataElement>builder()
                    .add(DummyDataElement.PRIMARY_ACCOUNT_NUMBER, "4552119306245327")
                    .add(DummyDataElement.PROCESSING_CODE, "40")
                    .add(DummyDataElement.AMOUNT, "124")
                    .add(DummyDataElement.TRANSMISSION_DATE_TIME, transmissionDateTime)
                    .add(DummyDataElement.SYSTEM_TRACE_AUDIT_NUMBER, "101")
                    .add(DummyDataElement.DATE_TIME_LOCAL_TRANSACTION, dateTimeLocalTransaction)
                    .add(DummyDataElement.DATE_EXPIRATION, dateExpiration)
                    .add(DummyDataElement.POINT_OF_SERVICE_DATA_CODE, "V69999000000")
                    .add(DummyDataElement.FUNCTION_CODE, "101")
                    .add(DummyDataElement.CARD_ACCEPTOR_BUSINESS_CODE, "1520")
                    .add(DummyDataElement.ACQUIRER_INSTITUTION_ID_CODE, "456654120087")
                    .add(DummyDataElement.RETRIEVAL_REFERENCE_NUMBER, "099920700619")
                    .add(DummyDataElement.CARD_ACCEPTOR_TERMINAL_IDENTIFICATION, "43214321")
                    .add(DummyDataElement.CARD_ACCEPTOR_ID_CODE, "455490051556622")
                    .add(DummyDataElement.CARD_ACCEPTOR_NAME_LOCATION, "Welch & Cie\\Geraldine Lane\\New York\\11590")
                    .add(DummyDataElement.CURRENCY_CODE, "978")
                    .add(DummyDataElement.PERSONAL_IDENTIFICATION_DATA, personalIdentificationData)
                    .build();

            // When
            var actual = messageBuilder.toByteArray();

            // Then
            var expectedValue = DatatypeConverter.parseHexBinary("31363435353231313933303632343533323730303030343030303030303030303031323430383032313532323231303030313031323130383032313532323231323031305636393939393030303030303130313135323031323435363635343132303038373039393932303730303631393433323134333231343535343930303531353536363232343157656C63682026204369655C476572616C64696E65204C616E655C4E657720596F726B5C3131353930393738A8ECBC46ADEFD38F");
            MatcherAssert.assertThat(actual, equalTo(expectedValue));
        }

        @Test
        @DisplayName("Should throw an exception for an unsupported data type.")
        void shouldThrowExceptionForUnsupportedDataType() {
            // Given
            var messageBuilder = MessageData.<DummyDataElement>builder().add(DummyDataElement.PRIMARY_ACCOUNT_NUMBER, new Object());

            // When
            var exception = Assertions.assertThrows(MessageData.UnknownTypeException.class, messageBuilder::build);

            // Then
            MatcherAssert.assertThat(exception.getMessage(), equalTo("\"java.lang.Object\" not supported for element."));
            MatcherAssert.assertThat(exception.getKlass(), equalTo(Object.class));
            MatcherAssert.assertThat(exception.getCause(), nullValue());
        }

    }

    @Nested
    @DisplayName("When a message is parsed.")
    class WhenMessageDataIsParsed {

        @Test
        @DisplayName("Should parse a message.")
        void shouldParseMessage() {
            // Given
            var data = DatatypeConverter.parseHexBinary("31363435353231313933303632343533323730303030343030303030303030303031323430383032313532323231303030313031323130383032313532323231323031305636393939393030303030303130313135323031323435363635343132303038373039393932303730303631393433323134333231343535343930303531353536363232343157656C63682026204369655C476572616C64696E65204C616E655C4E657720596F726B5C3131353930393738A8ECBC46ADEFD38F");
            var fields = List.of(
                    DummyDataElement.PRIMARY_ACCOUNT_NUMBER,
                    DummyDataElement.PROCESSING_CODE,
                    DummyDataElement.AMOUNT,
                    DummyDataElement.TRANSMISSION_DATE_TIME,
                    DummyDataElement.SYSTEM_TRACE_AUDIT_NUMBER,
                    DummyDataElement.DATE_TIME_LOCAL_TRANSACTION,
                    DummyDataElement.DATE_EXPIRATION,
                    DummyDataElement.POINT_OF_SERVICE_DATA_CODE,
                    DummyDataElement.FUNCTION_CODE,
                    DummyDataElement.CARD_ACCEPTOR_BUSINESS_CODE,
                    DummyDataElement.ACQUIRER_INSTITUTION_ID_CODE,
                    DummyDataElement.RETRIEVAL_REFERENCE_NUMBER,
                    DummyDataElement.CARD_ACCEPTOR_TERMINAL_IDENTIFICATION,
                    DummyDataElement.CARD_ACCEPTOR_ID_CODE,
                    DummyDataElement.CARD_ACCEPTOR_NAME_LOCATION,
                    DummyDataElement.CURRENCY_CODE,
                    DummyDataElement.PERSONAL_IDENTIFICATION_DATA
            );

            // When
            var messageData = MessageData.parse(fields, data);

            // Then
            var expectedPersonalIdentificationData = new byte[]{(byte) 0xA8, (byte) 0xEC, (byte) 0xBC, 0x46, (byte) 0xAD, (byte) 0xEF, (byte) 0xD3, (byte) 0x8F};
            var expectedDateExpiration = YearMonth.of(2020, 10);

            MatcherAssert.assertThat(messageData.get(DummyDataElement.PRIMARY_ACCOUNT_NUMBER, String.class), equalTo("4552119306245327"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.PROCESSING_CODE, String.class), equalTo("000040"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.AMOUNT, String.class), equalTo("000000000124"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.TRANSMISSION_DATE_TIME, String.class), equalTo("0802152221"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.SYSTEM_TRACE_AUDIT_NUMBER, String.class), equalTo("000101"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.DATE_TIME_LOCAL_TRANSACTION, String.class), equalTo("210802152221"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.DATE_EXPIRATION, YearMonth.class), equalTo(expectedDateExpiration));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.POINT_OF_SERVICE_DATA_CODE, String.class), equalTo("V69999000000"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.FUNCTION_CODE, String.class), equalTo("101"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.CARD_ACCEPTOR_BUSINESS_CODE, String.class), equalTo("1520"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.ACQUIRER_INSTITUTION_ID_CODE, String.class), equalTo("456654120087"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.RETRIEVAL_REFERENCE_NUMBER, String.class), equalTo("099920700619"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.CARD_ACCEPTOR_TERMINAL_IDENTIFICATION, String.class), equalTo("43214321"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.CARD_ACCEPTOR_ID_CODE, String.class), equalTo("455490051556622"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.CARD_ACCEPTOR_NAME_LOCATION, String.class), equalTo("Welch & Cie\\Geraldine Lane\\New York\\11590"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.CURRENCY_CODE, String.class), equalTo("978"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.PERSONAL_IDENTIFICATION_DATA), equalTo(expectedPersonalIdentificationData));
        }

    }

}
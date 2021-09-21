package io.github.cyrilschumacher;

import jakarta.xml.bind.DatatypeConverter;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

/**
 * Unit tests for the class: {@link Transactor}.
 */
@DisplayName("Unit tests for `Transactor` class.")
class TransactorTest {

    @Test
    @DisplayName("Should return a dump.")
    void shouldReturnDump() {
        // Given
        var data = DatatypeConverter.parseHexBinary("313130307234054108E0900031363435353231313933303632343533323730303030343030303030303030303031323430383032313532323231303030313031323130383032313532323231323031305636393939393030303030303130313135323031323435363635343132303038373039393932303730303631393433323134333231343535343930303531353536363232343157656C63682026204369655C476572616C64696E65204C616E655C4E657720596F726B5C3131353930393738A8ECBC46ADEFD38F");
        var transactor = Transactor.parse(data, DummyDataElement.class, StandardCharsets.US_ASCII);

        var stringWriterMock = Mockito.mock(StringWriter.class);

        // When
        transactor.dump(stringWriterMock);

        // Then
        Mockito.verify(stringWriterMock).write("Message Type Indicator  : 0x1100                                                ", 0, 80);
        Mockito.verify(stringWriterMock).write("Bitmap                  : 72 34 05 41 08 E0 90 00                               ", 0, 80);

        Mockito.verify(stringWriterMock).write("002  Primary Account Number                .. .. .. .. .. .. .. .. .. .. .. ..  ", 0, 80);
        Mockito.verify(stringWriterMock).write("003  Processing Code                       30 30 30 30 34 30                    ", 0, 80);
        Mockito.verify(stringWriterMock).write("004  Amount                                30 30 30 30 30 30 30 30 30 31 32 34  ", 0, 80);
        Mockito.verify(stringWriterMock).write("007  Transmission Date and Time            30 38 30 32 31 35 32 32 32 31        ", 0, 80);
        Mockito.verify(stringWriterMock).write("011  System Trace Audit Number             30 30 30 31 30 31                    ", 0, 80);
        Mockito.verify(stringWriterMock).write("012  Date and time, Local Transaction      32 31 30 38 30 32 31 35 32 32 32 31  ", 0, 80);
        Mockito.verify(stringWriterMock).write("014  Date, expiration                      32 30 31 30                          ", 0, 80);
        Mockito.verify(stringWriterMock).write("022  Point of Service Data Code            56 36 39 39 39 39 30 30 30 30 30 30  ", 0, 80);
        Mockito.verify(stringWriterMock).write("024  Function Code                         31 30 31                             ", 0, 80);
        Mockito.verify(stringWriterMock).write("026  Card Acceptor Business Code           31 35 32 30                          ", 0, 80);
        Mockito.verify(stringWriterMock).write("032  Acquirer Institution Id Code          34 35 36 36 35 34 31 32 30 30 38 37  ", 0, 80);
        Mockito.verify(stringWriterMock).write("037  Retrieval Reference Number            30 39 39 39 32 30 37 30 30 36 31 39  ", 0, 80);
        Mockito.verify(stringWriterMock).write("041  Card Acceptor Terminal Identification 34 33 32 31 34 33 32 31              ", 0, 80);
        Mockito.verify(stringWriterMock).write("042  Card Acceptor ID code                 34 35 35 34 39 30 30 35 31 35 35 36  ", 0, 80);
        Mockito.verify(stringWriterMock).write("043  Card Acceptor Name/Location           .. .. .. .. .. .. .. .. .. .. .. ..  ", 0, 80);
        Mockito.verify(stringWriterMock).write("049  Currency Code Transaction             39 37 38                             ", 0, 80);
        Mockito.verify(stringWriterMock).write("052  Personal Identification Data          .. .. .. .. .. .. .. ..              ", 0, 80);
    }

    @Nested
    @DisplayName("When a message is created.")
    class WhenMessageIsCreated {

        @Test
        @DisplayName("Should create a message.")
        void shouldCreateMessage() throws IOException {
            // Given
            var transmissionDateTime = ZonedDateTime.of(2021, 8, 2, 15, 22, 21, 0, ZoneOffset.UTC);
            var dateTimeLocalTransaction = LocalDateTime.of(2021, 8, 2, 15, 22, 21);
            var personalIdentificationData = new byte[]{(byte) 0xA8, (byte) 0xEC, (byte) 0xBC, 0x46, (byte) 0xAD, (byte) 0xEF, (byte) 0xD3, (byte) 0x8F};

            var transactor = Transactor.<DummyDataElement>builder(0x1100, StandardCharsets.US_ASCII)
                    .add(DummyDataElement.PRIMARY_ACCOUNT_NUMBER, "4552119306245327")
                    .add(DummyDataElement.PROCESSING_CODE, "40")
                    .add(DummyDataElement.AMOUNT, "124")
                    .add(DummyDataElement.TRANSMISSION_DATE_TIME, transmissionDateTime)
                    .add(DummyDataElement.SYSTEM_TRACE_AUDIT_NUMBER, "101")
                    .add(DummyDataElement.DATE_TIME_LOCAL_TRANSACTION, dateTimeLocalTransaction)
                    .add(DummyDataElement.DATE_EXPIRATION, YearMonth.of(2020, 10))
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
            var message = transactor.toByteArray();

            // Then
            var expectedData = DatatypeConverter.parseHexBinary("313130307234054108E0900031363435353231313933303632343533323730303030343030303030303030303031323430383032313532323231303030313031323130383032313532323231323031305636393939393030303030303130313135323031323435363635343132303038373039393932303730303631393433323134333231343535343930303531353536363232343157656C63682026204369655C476572616C64696E65204C616E655C4E657720596F726B5C3131353930393738A8ECBC46ADEFD38F");
            MatcherAssert.assertThat(message, equalTo(expectedData));
        }

        @Test
        @DisplayName("Should create an empty message.")
        void shouldCreateEmptyMessage() throws IOException {
            // Given
            var transactor = Transactor.<DummyDataElement>builder(0x1100, StandardCharsets.US_ASCII).build();

            // When
            var message = transactor.toByteArray();

            // Then
            var expectedData = DatatypeConverter.parseHexBinary("313130300000000000000000");
            MatcherAssert.assertThat(message, equalTo(expectedData));
        }

    }

    @Nested
    @DisplayName("When the message is parsed.")
    class WhenMessageIsParsed {

        @Test
        @DisplayName("Should parse a message.")
        void shouldParseMessage() {
            // Given
            var data = DatatypeConverter.parseHexBinary("313130307234054108E0900031363435353231313933303632343533323730303030343030303030303030303031323430383032313532323231303030313031323130383032313532323231323031305636393939393030303030303130313135323031323435363635343132303038373039393932303730303631393433323134333231343535343930303531353536363232343157656C63682026204369655C476572616C64696E65204C616E655C4E657720596F726B5C3131353930393738A8ECBC46ADEFD38F");

            // When
            var transactor = Transactor.parse(data, DummyDataElement.class, StandardCharsets.US_ASCII);

            // Then
            var expectedPersonalIdentificationData = new byte[]{(byte) 0xA8, (byte) 0xEC, (byte) 0xBC, 0x46, (byte) 0xAD, (byte) 0xEF, (byte) 0xD3, (byte) 0x8F};

            MatcherAssert.assertThat(transactor.getMessageTypeIndicator(), equalTo(0x1100));

            var bitmap = transactor.getBitmap();
            MatcherAssert.assertThat(bitmap.getDataFields(), containsInAnyOrder(2, 3, 4, 7, 11, 12, 14, 22, 24, 26, 32, 37, 41, 42, 43, 49, 52));
            MatcherAssert.assertThat(bitmap.getRange(), equalTo(1));

            var messageData = transactor.getMessageData();
            MatcherAssert.assertThat(messageData.get(DummyDataElement.PRIMARY_ACCOUNT_NUMBER, String.class), equalTo("4552119306245327"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.PROCESSING_CODE, String.class), equalTo("000040"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.AMOUNT, String.class), equalTo("000000000124"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.TRANSMISSION_DATE_TIME, String.class), equalTo("0802152221"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.SYSTEM_TRACE_AUDIT_NUMBER, String.class), equalTo("000101"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.DATE_TIME_LOCAL_TRANSACTION, String.class), equalTo("210802152221"));
            MatcherAssert.assertThat(messageData.get(DummyDataElement.DATE_EXPIRATION, String.class), equalTo("2010"));
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

        @Test
        @DisplayName("Should parse an empty message")
        void shouldParseEmptyMessage() {
            // Given
            var data = DatatypeConverter.parseHexBinary("313130300000000000000000");

            // When
            var transactor = Transactor.parse(data, DummyDataElement.class, StandardCharsets.US_ASCII);

            // Then
            MatcherAssert.assertThat(transactor.getMessageTypeIndicator(), equalTo(0x1100));

            var bitmap = transactor.getBitmap();
            MatcherAssert.assertThat(bitmap.getDataFields(), empty());
            MatcherAssert.assertThat(bitmap.getRange(), equalTo(1));

        }

    }

}
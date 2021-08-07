package io.github.cyrilschumacher.data;

public enum IsoDataElement implements DataElement {

    PRIMARY_ACCOUNT_NUMBER(2, "Primary Account Number", 19, IsoDataTypeLengthCodec.VARIABLE, DataType.NUMERIC),
    PROCESSING_CODE(3, "Processing Code", 6, DataType.NUMERIC),
    AMOUNT(4, "Amount Transaction", 12, DataType.NUMERIC),
    AMOUNT_SETTLEMENT(5, "Amount, settlement", 12, DataType.NUMERIC),
    AMOUNT_CARDHOLDER_BILLING(6, "Amount, cardholder billing ", 12, DataType.NUMERIC),
    TRANSMISSION_DATE_TIME(7, "Transmission date & time", 10, DataType.NUMERIC),
    AMOUNT_CARDHOLDER_BILLING_FEE(8, "Amount, cardholder billing fee", 8, DataType.NUMERIC),
    CONVERSATION_RATE_SETTLEMENT(9, "Conversion rate, settlement", 8, DataType.NUMERIC),
    CONVERSATION_RATE_CARDHOLDER_BILLING(10, "Conversion rate, cardholder billing", 8, DataType.NUMERIC),
    SYSTEM_TRACE_AUDIT_NUMBER(11, "System trace audit number", 6, DataType.NUMERIC),
    LOCAL_TRANSACTION_TIME(12, "Local transaction time", 6, DataType.NUMERIC),
    LOCAL_TRANSACTION_DATE(13, "Local transaction date", 4, DataType.NUMERIC),
    EXPIRATION_DATE(14, "Expiration date", 4, DataType.NUMERIC),
    SETTLEMENT_DATE(15, "Settlement date", 4, DataType.NUMERIC),
    CURRENCY_CONVERSION_DATE(16, "Currency conversion date", 4, DataType.NUMERIC),
    CAPTURE_DATE(17, "Capture date", 4, DataType.NUMERIC),
    MERCHANT_TYPE(18, "Merchant type", 4, DataType.NUMERIC),
    ACQUIRING_INSTITUTION(19, "Acquiring institution", 3, DataType.NUMERIC),
    EXTENDED_PRIMARY_ACCOUNT_NUMBER(20, "PAN extended", 3, DataType.NUMERIC),
    FORWARDING_INSTITUTION(21, "Forwarding institution", 3, DataType.NUMERIC),
    POINT_OF_SERVICE_ENTRY_MODE(22, "Point of service entry mode", 3, DataType.NUMERIC),
    APPLICATION_PRIMARY_ACCOUNT_NUMBER_SEQUENCE_NUMBER(23, "Application PAN sequence number", 3, DataType.NUMERIC),
    FUNCTION_CODE(24, "Function code", 3, DataType.NUMERIC),
    POINT_OF_SERVICE_CONDITION_CODE(25, "Point of service condition code", 2, DataType.NUMERIC),
    POINT_OF_SERVICE_CAPTURE_CODE(26, "Point of service capture code", 2, DataType.NUMERIC),
    AUTHORIZING_IDENTIFICATION_RESPONSE_LENGTH(27, "Authorizing identification response length", 1, DataType.NUMERIC),
    AMOUNT_TRANSACTION_FEE(28, "Amount, transaction fee", 8, DataType.NUMERIC),
    AMOUNT_SETTLEMENT_FEE(29, "Amount, settlement fee", 8, DataType.NUMERIC),
    AMOUNT_TRANSACTION_PROCESSING_FEE(30, "Amount, transaction processing fee", 8, DataType.NUMERIC),
    AMOUNT_SETTLEMENT_PROCESSING_FEE(31, "Amount, settlement processing fee", 8, DataType.NUMERIC),
    ACQUIRING_INSTITUTION_IDENTIFICATION_CODE(32, "Acquiring institution identification code", 11, IsoDataTypeLengthCodec.VARIABLE, DataType.NUMERIC),
    FORWARDING_INSTITUTION_IDENTIFICATION_CODE(33, "Forwarding institution identification code", 11, IsoDataTypeLengthCodec.VARIABLE, DataType.NUMERIC),
    PRIMARY_ACCOUNT_NUMBER_EXTENDED(34, "Primary account number, extended", 11, IsoDataTypeLengthCodec.VARIABLE, DataType.NUMERIC, DataType.SPECIAL),
    TRACK_2_DATA(35, "Track 2 data", 37, IsoDataTypeLengthCodec.VARIABLE, DataType.NUMERIC, DataType.ALPHA, DataType.SPECIAL),
    TRACK_3_DATA(36, "Track 3 data", 107, IsoDataTypeLengthCodec.VARIABLE, DataType.NUMERIC),
    RETRIEVAL_REFERENCE_NUMBER(37, "Retrieval reference number", 12, DataType.NUMERIC, DataType.ALPHA),
    AUTHORIZATION_IDENTIFICATION_RESPONSE(38, "Authorization identification response", 6, DataType.ALPHA, DataType.NUMERIC),
    RESPONSE_CODE(39, "Response code", 2, DataType.ALPHA, DataType.NUMERIC),
    SERVICE_RESTRICTION_CODE(40, "Service restriction code", 3, DataType.ALPHA, DataType.NUMERIC),
    CARD_ACCEPTOR_TERMINAL_IDENTIFICATION(41, "Card acceptor terminal identification", 8, DataType.NUMERIC, DataType.ALPHA, DataType.SPECIAL),
    CARD_ACCEPTOR_IDENTIFICATION_CODE(42, "Card acceptor identification code", 15, DataType.NUMERIC, DataType.ALPHA, DataType.SPECIAL),
    CARD_ACCEPTOR_NAME_LOCATION(43, "Card acceptor name/location", 40, DataType.NUMERIC, DataType.ALPHA, DataType.SPECIAL),
    ADDITIONAL_RESPONSE_CODE(44, "Additional response data", 25, IsoDataTypeLengthCodec.VARIABLE, DataType.ALPHA, DataType.NUMERIC),
    TRACK_1_DATA(45, "Track 1 data", 76, IsoDataTypeLengthCodec.VARIABLE, DataType.ALPHA, DataType.NUMERIC),
    ADDITIONAL_DATA(46, "Additional data", 999, IsoDataTypeLengthCodec.VARIABLE, DataType.ALPHA, DataType.NUMERIC),
    ADDITIONAL_DATA_NATIONAL(47, "Additional data (national)", 999, IsoDataTypeLengthCodec.VARIABLE, DataType.ALPHA, DataType.NUMERIC),
    ADDITIONAL_DATA_PRIVATE(48, "Additional data (private)", 999, IsoDataTypeLengthCodec.VARIABLE, DataType.ALPHA, DataType.NUMERIC),
    CURRENCY_CODE_TRANSACTION(49, "Currency code, transaction", 3, DataType.ALPHA, DataType.NUMERIC),
    CURRENCY_CODE_SETTLEMENT(50, "Currency code, settlement", 3, DataType.ALPHA, DataType.NUMERIC),
    CURRENCY_CODE_CARDHOLDER_BILLING(51, "Currency code, cardholder billing", 3, DataType.ALPHA, DataType.NUMERIC),
    PERSONAL_IDENTIFICATION_NUMBER_DATA(52, "Personal identification number data", 64, DataType.ALPHA, DataType.NUMERIC),
    SECURITY_RELATED_CONTROL_INFORMATION(53, "Security related control information", 16, DataType.NUMERIC),
    ADDITIONAL_AMOUNTS(54, "Additional amounts", 120, IsoDataTypeLengthCodec.VARIABLE, DataType.ALPHA, DataType.NUMERIC),
    ICC_DATA(55, "ICC data", 999, IsoDataTypeLengthCodec.VARIABLE, DataType.ALPHA, DataType.NUMERIC, DataType.SPECIAL),
    MESSAGE_AUTHENTICATION_CODE(64, "Message authentication code", 64, DataType.ALPHA, DataType.NUMERIC, DataType.SPECIAL);

    private final int dataField;
    private final String description;
    private final int length;
    private final DataTypeLengthCodec dataTypeLengthCodec;
    private final DataType[] dataTypes;

    IsoDataElement(final int dataField, final String description, final int length, final DataType... types) {
        this(dataField, description, length, IsoDataTypeLengthCodec.FIXED, types);
    }

    IsoDataElement(final int dataField, final String description, final int length, final DataTypeLengthCodec dataTypeLengthCodec, final DataType... dataTypes) {
        this.dataField = dataField;
        this.description = description;
        this.length = length;
        this.dataTypeLengthCodec = dataTypeLengthCodec;
        this.dataTypes = dataTypes;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getField() {
        return dataField;
    }

    @Override
    public DataTypeLengthCodec getTypeLength() {
        return dataTypeLengthCodec;
    }

    @Override
    public int getMaximumLength() {
        return length;
    }

    @Override
    public DataType[] getTypes() {
        return dataTypes;
    }

}

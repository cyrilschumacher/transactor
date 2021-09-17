package io.github.cyrilschumacher;

import io.github.cyrilschumacher.data.DataElement;
import io.github.cyrilschumacher.data.DataType;
import io.github.cyrilschumacher.data.DataTypeLengthCodec;
import io.github.cyrilschumacher.data.IsoDataTypeLengthCodec;

enum DummyDataElement implements DataElement {

    PRIMARY_ACCOUNT_NUMBER(2, "Primary Account Number", 19, SensitiveFlag.SENSITIVE, IsoDataTypeLengthCodec.VARIABLE, DataType.NUMERIC),
    PROCESSING_CODE(3, "Processing Code", 6, SensitiveFlag.NON_SENSITIVE, IsoDataTypeLengthCodec.FIXED, DataType.NUMERIC),
    AMOUNT(4, "Amount", 12, SensitiveFlag.NON_SENSITIVE, IsoDataTypeLengthCodec.FIXED, DataType.NUMERIC),
    TRANSMISSION_DATE_TIME(7, "Transmission Date and Time", 10, SensitiveFlag.NON_SENSITIVE, IsoDataTypeLengthCodec.FIXED, DataType.NUMERIC),
    SYSTEM_TRACE_AUDIT_NUMBER(11, "System Trace Audit Number", 6, SensitiveFlag.NON_SENSITIVE, IsoDataTypeLengthCodec.FIXED, DataType.NUMERIC),
    DATE_TIME_LOCAL_TRANSACTION(12, "Date and time, Local Transaction", 12, SensitiveFlag.NON_SENSITIVE, IsoDataTypeLengthCodec.FIXED, DataType.NUMERIC),
    DATE_EXPIRATION(14, "Date, expiration", 4, SensitiveFlag.NON_SENSITIVE, IsoDataTypeLengthCodec.FIXED, DataType.NUMERIC),
    POINT_OF_SERVICE_DATA_CODE(22, "Point of Service Data Code", 12, SensitiveFlag.NON_SENSITIVE, IsoDataTypeLengthCodec.FIXED, DataType.ALPHA),
    FUNCTION_CODE(24, "Function Code", 3, SensitiveFlag.NON_SENSITIVE, IsoDataTypeLengthCodec.FIXED, DataType.NUMERIC),
    CARD_ACCEPTOR_BUSINESS_CODE(26, "Card Acceptor Business Code", 4, SensitiveFlag.NON_SENSITIVE, IsoDataTypeLengthCodec.FIXED, DataType.NUMERIC),
    ACQUIRER_INSTITUTION_ID_CODE(32, "Acquirer Institution Id Code", 99, SensitiveFlag.NON_SENSITIVE, IsoDataTypeLengthCodec.VARIABLE, DataType.NUMERIC),
    RETRIEVAL_REFERENCE_NUMBER(37, "Retrieval Reference Number", 12, SensitiveFlag.NON_SENSITIVE, IsoDataTypeLengthCodec.FIXED, DataType.ALPHA),
    CARD_ACCEPTOR_TERMINAL_IDENTIFICATION(41, "Card Acceptor Terminal Identification", 8, SensitiveFlag.NON_SENSITIVE, IsoDataTypeLengthCodec.FIXED, DataType.NUMERIC),
    CARD_ACCEPTOR_ID_CODE(42, "Card Acceptor ID code", 15, SensitiveFlag.NON_SENSITIVE, IsoDataTypeLengthCodec.FIXED, DataType.ALPHA),
    CARD_ACCEPTOR_NAME_LOCATION(43, "Card Acceptor Name/Location", 99, SensitiveFlag.SENSITIVE, IsoDataTypeLengthCodec.VARIABLE, DataType.ALPHA, DataType.NUMERIC),
    CURRENCY_CODE(49, "Currency Code Transaction", 3, SensitiveFlag.NON_SENSITIVE, IsoDataTypeLengthCodec.FIXED, DataType.NUMERIC),
    PERSONAL_IDENTIFICATION_DATA(52, "Personal Identification Data", 8, SensitiveFlag.SENSITIVE, IsoDataTypeLengthCodec.FIXED, DataType.BINARY);

    private final int dataField;
    private final String description;
    private final int maximumLength;
    private final DataTypeLengthCodec dataTypeLengthCodec;
    private final DataType[] dataTypes;
    private final SensitiveFlag sensitiveFlag;

    DummyDataElement(final int dataField, final String description, final int length, final SensitiveFlag sensitiveFlag, final DataTypeLengthCodec dataTypeLengthCodec, final DataType... dataTypes) {
        this.dataField = dataField;
        this.description = description;
        this.maximumLength = length;
        this.dataTypeLengthCodec = dataTypeLengthCodec;
        this.dataTypes = dataTypes;
        this.sensitiveFlag = sensitiveFlag;
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
    public DataTypeLengthCodec getTypeLengthCodec() {
        return dataTypeLengthCodec;
    }

    @Override
    public int getMaximumLength() {
        return maximumLength;
    }

    @Override
    public SensitiveFlag getSensitiveFlag() {
        return sensitiveFlag;
    }

    @Override
    public DataType[] getTypes() {
        return dataTypes;
    }

}

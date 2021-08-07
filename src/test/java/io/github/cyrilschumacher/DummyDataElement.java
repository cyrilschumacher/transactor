package io.github.cyrilschumacher;

import io.github.cyrilschumacher.data.DataElement;
import io.github.cyrilschumacher.data.DataType;
import io.github.cyrilschumacher.data.DataTypeLengthCodec;
import io.github.cyrilschumacher.data.IsoDataTypeLengthCodec;

enum DummyDataElement implements DataElement {

    PRIMARY_ACCOUNT_NUMBER(2, "Primary Account Number", 19, IsoDataTypeLengthCodec.VARIABLE, DataType.NUMERIC),
    PROCESSING_CODE(3, "Processing Code", 6, DataType.NUMERIC),
    AMOUNT(4, "Amount", 12, DataType.NUMERIC),
    TRANSMISSION_DATE_TIME(7, "Transmission Date and Time", 10, DataType.NUMERIC),
    SYSTEM_TRACE_AUDIT_NUMBER(11, "System Trace Audit Number", 6, DataType.NUMERIC),
    DATE_TIME_LOCAL_TRANSACTION(12, "Date and time, Local Transaction", 12, DataType.NUMERIC),
    DATE_EXPIRATION(12, "Date, expiration", 4, DataType.NUMERIC),
    POINT_OF_SERVICE_DATA_CODE(22, "Point of Service Data Code", 12, DataType.ALPHA),
    FUNCTION_CODE(24, "Function Code", 3, DataType.NUMERIC),
    CARD_ACCEPTOR_BUSINESS_CODE(26, "Card Acceptor Business Code", 4, DataType.NUMERIC),
    ACQUIRER_INSTITUTION_ID_CODE(32, "Acquirer Institution Id Code", 99, IsoDataTypeLengthCodec.VARIABLE, DataType.NUMERIC),
    RETRIEVAL_REFERENCE_NUMBER(37, "Retrieval Reference Number", 12, DataType.ALPHA),
    CARD_ACCEPTOR_TERMINAL_IDENTIFICATION(41, "Card Acceptor Terminal Identification", 8, DataType.NUMERIC),
    CARD_ACCEPTOR_ID_CODE(42, "Card Acceptor Terminal Identification", 15, DataType.ALPHA),
    CARD_ACCEPTOR_NAME_LOCATION(43, "Card Acceptor Name/Location", 99, IsoDataTypeLengthCodec.VARIABLE, DataType.ALPHA, DataType.NUMERIC),
    CURRENCY_CODE(49, "Currency Code Transaction", 3, DataType.NUMERIC),
    PERSONAL_IDENTIFICATION_DATA(52, "Currency Code Transaction", 8, DataType.BINARY);

    private final int dataField;
    private final String description;
    private final int maximumLength;
    private final DataTypeLengthCodec dataTypeLengthCodec;
    private final DataType[] dataTypes;

    DummyDataElement(final int dataField, final String description, final int length, final DataType... types) {
        this(dataField, description, length, IsoDataTypeLengthCodec.FIXED, types);
    }

    DummyDataElement(final int dataField, final String description, final int length, final DataTypeLengthCodec dataTypeLengthCodec, final DataType... dataTypes) {
        this.dataField = dataField;
        this.description = description;
        this.maximumLength = length;
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
        return maximumLength;
    }

    @Override
    public DataType[] getTypes() {
        return dataTypes;
    }

}

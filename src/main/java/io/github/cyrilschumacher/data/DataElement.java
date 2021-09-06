package io.github.cyrilschumacher.data;

public interface DataElement {

    int getField();

    String getDescription();

    int getMaximumLength();

    SensitiveFlag getSensitiveFlag();

    DataType[] getTypes();

    DataTypeLengthCodec getTypeLengthCodec();

    enum SensitiveFlag {

        NON_SENSITIVE,
        SENSITIVE

    }

}

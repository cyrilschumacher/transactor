package io.github.cyrilschumacher.data;

public interface DataElement {

    int getField();

    DataTypeLengthCodec getTypeLength();

    String getDescription();

    int getMaximumLength();

    DataType[] getTypes();

}

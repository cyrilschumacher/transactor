package io.github.cyrilschumacher.data;

public interface DataElement {

    int getField();

    DataTypeLength getTypeLength();

    String getDescription();

    int getLength();

    DataType[] getTypes();

}

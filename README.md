# transactor

[![Java CI with Maven](https://github.com/cyrilschumacher/transactor/actions/workflows/maven.yml/badge.svg)](https://github.com/cyrilschumacher/transactor/actions/workflows/maven.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/278c72bf99fc4d55bf7b59519b81987b)](https://www.codacy.com/gh/cyrilschumacher/transactor/dashboard?utm_source=github.com&utm_medium=referral&utm_content=cyrilschumacher/transactor&utm_campaign=Badge_Coverage)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/278c72bf99fc4d55bf7b59519b81987b)](https://www.codacy.com/gh/cyrilschumacher/transactor/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=cyrilschumacher/transactor&amp;utm_campaign=Badge_Grade)

> Writing and reading ISO 8583 message with transactor.

**transactor** is a Java library for writing and reading ISO 8583 messages.

## Getting started

The library provides a class that can be qualified as an entry point: the `Transactor` class. This class provides 
two methods to build or parse an ISO 8583 message.

> 📦 By default, the library provides **default classes** such as `IsoDataElement`, `IsoDataTypeLengthCodec`,...

* To parse an ISO 8583 message, the `parse()` method must be used:

```java
var data = new byte[]{ /* ISO 8583 message */ };
var transactor = Transactor.parse(data, IsoDataElement.class, StandardCharsets.US_ASCII);
```

This method takes the ISO 8583 message in binary format, a class type including a definition of the elements that may 
be in the given message and a charset. In return, the method returns an instance of the `Transactor` class which 
provides methods to retrieve the elements, either in binary format or in a particular format.

* To build an ISO 8583 message, the `builder()` method must be used:

```java
var builder = Transactor.<IsoDataElement>builder(0x1100, StandardCharsets.US_ASCII);
var transactor = builder.build();
```

This method takes a message type indicator and a charset. The method will return an instance of a builder of the 
`Transactor` class which will allow adding elements. After adding the elements, an instance of the `Transactor` 
class can be obtained by calling the `build()` method.

The `Transactor` class provides methods for:

  * Get the message type indicator,
  * Get the bitmap,
  * Get the elements
  * And create a binary value representing the ISO 8583 message.

## How does it work

The classes `Bitmap` and `MessageData` are used as support by the `Transactor` class. The `Bitmap` class has the role
of building and analyzing a bitmap and the `MessageData` class is in charge of building and analyzing the data elements.

> ⚡ An instance of these two classes can be **accessed** from the `Transactor` class only.

When building and parsing an ISO 8583 message, the `Transactor` class will create instances of these classes, 
but before that, it will take care of splitting the message. To parse (or build) the elements contained in a message,
classes use an implementation of the `DataElement` interface. This interface defines accessors to get the number, 
description, types and maximum length of a data element as well as a method to encode or decode the size of the element.

The interface can be implemented in the following way by exploiting in part the classes
(`IsoDataTypeLengthCodec`, `DataType`) offered by the library:

```java
enum CustomDataElement implements DataElement {

    PRIMARY_ACCOUNT_NUMBER(2, "Primary Account Number", 19, IsoDataTypeLengthCodec.VARIABLE, DataType.NUMERIC);

    private final int dataField;
    private final String description;
    private final int length;
    private final DataTypeLengthCodec dataTypeLengthCodec;
    private final DataType[] dataTypes;

    CustomDataElement(final int dataField, final String description, final int length, final DataTypeLengthCodec dataTypeLengthCodec, final DataType... dataTypes) {
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
    public int getLength() {
        return length;
    }

    @Override
    public DataType[] getTypes() {
        return dataTypes;
    }

}
```
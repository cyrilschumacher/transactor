package io.github.cyrilschumacher;

import io.github.cyrilschumacher.data.DataElement;

import java.nio.charset.Charset;

public interface DumpBuilder<T extends Enum<T> & DataElement> {

    TransactorDumpBuilder<T> withBody(MessageData<T> messageData, Charset charset);

    TransactorDumpBuilder<T> withFooter();

    TransactorDumpBuilder<T> withHeader(int messageTypeIndicator, Bitmap bitmap, Charset charset);

    String build();

}

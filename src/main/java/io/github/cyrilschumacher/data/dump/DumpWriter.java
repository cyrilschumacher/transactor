package io.github.cyrilschumacher.data.dump;

import io.github.cyrilschumacher.Bitmap;
import io.github.cyrilschumacher.MessageData;
import io.github.cyrilschumacher.data.DataElement;

import java.io.IOException;
import java.nio.charset.Charset;

public interface DumpWriter<T extends Enum<T> & DataElement> extends AutoCloseable {

    void printDataElements(MessageData<T> messageData, Charset charset) throws IOException;

    void printFooter() throws IOException;

    void printHeader() throws IOException;

    void printMessageHeader(int messageTypeIndicator, Bitmap bitmap, Charset charset) throws IOException;

}

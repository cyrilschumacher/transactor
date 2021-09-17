package io.github.cyrilschumacher.data.dump;

import io.github.cyrilschumacher.Bitmap;
import io.github.cyrilschumacher.MessageData;
import io.github.cyrilschumacher.data.DataElement;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Dump writer.
 * <p>
 * This interface defines methods for writing a structured dump. The dump is structured as follows:
 * <ul>
 *     <li>a <b>header</b>, for printing additional data to the dump: date, copyright, etc;</li>
 *     <li>
 *         a <b>message header</b>, for printing information related to the ISO 8583 message:
 *         bitmap, message type indicator;
 *     </li>
 *     <li><b>data elements</b>, for printing detailed information on the data elements;</li>
 *     <li>a <b>footer</b>, for printing additional data to the dump;</li>
 * </ul>
 *
 * @param <T> the type of data elements in the message.
 */
public interface DumpWriter<T extends Enum<T> & DataElement> extends AutoCloseable {

    void printDataElements(MessageData<T> messageData, Charset charset) throws IOException;

    void printFooter() throws IOException;

    void printHeader() throws IOException;

    void printMessageHeader(int messageTypeIndicator, Bitmap bitmap, Charset charset) throws IOException;

}

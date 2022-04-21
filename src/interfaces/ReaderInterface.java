package interfaces;

import java.io.IOException;

/**
 * A simple interface over a readByte function that reads a single byte from
 * a data stream.
 *
 * Ideally, this can be used, as in the readVarInt and readVarLong functions,
 * as a lambda parameter, or alternatively any method using it can be copied
 *  * and refactored to remove this interface easily.
 */
public interface ReaderInterface {

    /**
     * Reads a single byte from a desired stream and returns it.
     * Which stream this is, should be known implicitly from the implementation
     * context.
     *
     * e.g., if this is implemented with a lambda function, that lambda would
     * specify which stream the byte is read from.
     *
     * @return The byte that was read.
     */
    int readByte() throws IOException;

}
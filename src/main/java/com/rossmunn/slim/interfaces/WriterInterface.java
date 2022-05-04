package com.rossmunn.slim.interfaces;

import java.io.IOException;

/**
 * A simple interface over a writeByte function that writes a single byte to
 * a data sink.
 *
 * Ideally, this can be used, as in the writeVarInt and writeVarLong functions,
 * as a lambda parameter, or alternatively any method using it can be copied
 * and refactored to remove this interface easily.
 */
public interface WriterInterface {
    /**
     * Writes a single byte to the desired sink.
     * Which sink this is, should be known implicitly from the implementation
     * context.
     *
     * e.g., if this is implemented with a lambda function, that lambda would
     * specify which sink the byte is written to.
     *
     * @param value The byte to write.
     */
    void writeByte(byte value) throws IOException;
}


package ru.bedward70.rest.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * InputStream methods
 */
public class InputStreamUtil {

    private static final int DEFAULT_BUFFER_SIZE = 8192;


    private final InputStream inputStream;

    /**
     * Constructor
     * @param inputStream input stream
     */
    public InputStreamUtil(InputStream inputStream) {
        this.inputStream = inputStream;
    }



    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        transferTo(outputStream);
        return outputStream.toByteArray();
    }

    public long  transferTo(OutputStream out) throws IOException {
        Objects.requireNonNull(out, "out");
        long transferred = 0;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int read;
        while ((read = inputStream.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
            out.write(buffer, 0, read);
            transferred += read;
        }
        return transferred;
    }
}

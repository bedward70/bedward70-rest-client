/*
 MIT License https://en.wikipedia.org/wiki/MIT_License

 Copyright (c) 2022, Eduard Balovnev (bedward70)
 All rights reserved.

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package ru.bedward70.rest.client.response.acceptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.function.Supplier;

/**
 * OutputStream implementation of RestResponseAcceptor
 */
public class BytesRestResponseAcceptor implements RestResponseAcceptor<byte[]> {

    /** Mime accept */
    private final String mimeAccept;


    /**
     * Constructor
     *
     * @param mimeAccept mime accept
     */
    public BytesRestResponseAcceptor(final String mimeAccept) {
        this.mimeAccept = mimeAccept;
    }

    @Override
    public void setAcceptProperty(final URLConnection con) {
        con.setRequestProperty(ACCEPT_HEADER_KEY, mimeAccept);
    }

    @Override
    public byte[] readValue(InputStream inputStream, Class<byte[]> responseClazz) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        inputStream.transferTo(os);
        return os.toByteArray();
    }
}

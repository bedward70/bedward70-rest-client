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

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ru.bedward70.rest.client.response.acceptor.RestResponseAcceptor.ACCEPT_HEADER_KEY;

public class OutputStreamRestResponseAcceptorTest {

    @Test
    void setAcceptProperty() {
        // when
        String expected = "application/xml";
        URLConnection con = Mockito.mock(URLConnection.class);

        // do
        new OutputStreamRestResponseAcceptor(expected, null).setAcceptProperty(con);

        // then
        ArgumentCaptor<String> name = ArgumentCaptor.forClass(String.class) ;
        ArgumentCaptor<String> value = ArgumentCaptor.forClass(String.class) ;
        verify(con, times(1)).setRequestProperty(name.capture(), value.capture());
        assertEquals(ACCEPT_HEADER_KEY, name.getValue());
        assertEquals(expected, value.getValue());
    }


    @Test
    void readValue() throws IOException {
        // when
        String expected = "test";
        InputStream inputStream = new ByteArrayInputStream(expected.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamRestResponseAcceptor acceptor = new OutputStreamRestResponseAcceptor(null, () -> outputStream);

        // do
        Boolean result = acceptor.readValue(inputStream, Boolean.class);

        // then
        assertTrue(result);
        assertEquals(expected, new String(outputStream.toByteArray(), StandardCharsets.UTF_8));
    }
}

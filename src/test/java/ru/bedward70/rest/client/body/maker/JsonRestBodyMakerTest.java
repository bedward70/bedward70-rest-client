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
package ru.bedward70.rest.client.body.maker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class JsonRestBodyMakerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonRestBodyMaker maker = new JsonRestBodyMaker(objectMapper);

    @ParameterizedTest
    @MethodSource
    void setContentTypeProperty(
        final Map<String, String> requestBody,
        final Integer expectedTimes,
        final String expectedKey,
        final String expectedValue
    ) {
        // when
        URLConnection con = Mockito.mock(URLConnection.class);

        // do
        maker.setContentTypeProperty(con, requestBody);

        // then
        ArgumentCaptor<String> name = ArgumentCaptor.forClass(String.class) ;
        ArgumentCaptor<String> value = ArgumentCaptor.forClass(String.class) ;
        verify(con, times(expectedTimes)).setRequestProperty(name.capture(), value.capture());
        if (expectedTimes > 0) {
            assertEquals(expectedKey, name.getValue());
            assertEquals(expectedValue, value.getValue());
        }
    }

    private static Stream<Arguments> setContentTypeProperty() {
        return Stream.of(
            Arguments.of((Map<String, String>) null, 0,  null, null),
            Arguments.of(Collections.emptyMap(), 1,  "Content-Type", "application/json")
        );
    }

    @Test
    void writeWithNull() throws IOException {
        // when
        URLConnection con = Mockito.mock(URLConnection.class);

        // do
        maker.write(con, null);

        // then
        verify(con, times(0)).setDoOutput(anyBoolean());
        verify(con, times(0)).getOutputStream();
    }

    @ParameterizedTest
    @MethodSource
    void write(
        final Map<String, String> requestBody
    ) throws IOException {
        // when
        URLConnection con = Mockito.mock(URLConnection.class);
        OutputStream outputStream = Mockito.mock(OutputStream.class);

        doReturn(outputStream).when(con).getOutputStream();

        // do
        maker.write(con, requestBody);

        // then
        verify(con, times(1)).setDoOutput(anyBoolean());
        verify(con, times(1)).getOutputStream();

        ArgumentCaptor<byte[]> bytes = ArgumentCaptor.forClass(byte[].class) ;
        verify(outputStream, times(1)).write(bytes.capture());
        assertEquals(requestBody, objectMapper.readValue(bytes.getValue(), Map.class));
    }

    private static Stream<Arguments> write() {
        Map<String, String> map = new HashMap<>();
        map.put("id", "1");
        map.put("value",  "2");
        return Stream.of(
            Arguments.of(Collections.emptyMap()),
            Arguments.of(map)
        );
    }
}

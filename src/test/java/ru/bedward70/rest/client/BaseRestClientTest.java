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
package ru.bedward70.rest.client;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.bedward70.rest.client.body.maker.RestBodyMaker;
import ru.bedward70.rest.client.exception.ErrorCodeRestClientException;
import ru.bedward70.rest.client.response.acceptor.RestResponseAcceptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BaseRestClientTest {

    @Test
    void test() throws IOException {
        // when
        InputStream inputStream = Mockito.mock(InputStream.class);
        HttpURLConnection con = Mockito.mock(HttpURLConnection.class);
        BaseRestClient client = Mockito.spy(new BaseRestClient("http://localhost"));
        String expected = "result";

        String httpMethod = "PUT";
        String urlSuffix = "/statistic";
        Long requestBody = 1265898L;
        RestBodyMaker<Long> bodyMaker = Mockito.mock(RestBodyMaker.class);
        Class<String> responseClazz = String.class;
        RestResponseAcceptor<String> responseAcceptor = Mockito.mock(RestResponseAcceptor.class);
        String headerName = "name";
        String headerValue = "getting of statistic";
        Map<String, String> headers = new HashMap<>();
        headers.put(headerName, headerValue);
        int responseCode = 201;
        final Integer[] successfulResponseCodes = new Integer[] {responseCode};

        client.setHeader("actual", "actual value");
        client.setHeader("deleted", "deleted value");
        client.setBearerToken("token");
        client.removeHeader("deleted");

        doReturn(con).when(client).getHttpURLConnection(anyString());
        doReturn(inputStream).when(con).getInputStream();
        doReturn(responseCode).when(con).getResponseCode();
        doReturn(expected).when(responseAcceptor).readValue(inputStream, responseClazz);

        // do
        String result = client.execute(
            httpMethod,
            urlSuffix,
            requestBody,
            bodyMaker,
            responseClazz,
            responseAcceptor,
            headers,
            successfulResponseCodes
        );

        // then
        verify(client, times(1)).getHttpURLConnection(urlSuffix);
        verify(con, times(1)).setRequestMethod(httpMethod);
        verify(con, times(1)).setRequestProperty(headerName, headerValue);
        verify(con, times(1)).setRequestProperty("actual", "actual value");
        verify(con, times(1)).setRequestProperty("Authorization", "Bearer token");
        verify(con, times(0)).setRequestProperty("deleted", "deleted value");
        verify(bodyMaker, times(1)).setContentTypeProperty(con, requestBody);
        verify(responseAcceptor, times(1)).setAcceptProperty(con);
        verify(bodyMaker, times(1)).write(con, requestBody);
        verify(con, times(1)).getResponseCode();
        verify(responseAcceptor, times(1)).readValue(inputStream, responseClazz);
        verify(con, times(1)).disconnect();

        assertEquals(expected, result);
    }

    @Test
    void testException() throws IOException {
        // when
        InputStream inputStream = Mockito.mock(InputStream.class);
        HttpURLConnection con = Mockito.mock(HttpURLConnection.class);
        BaseRestClient client = Mockito.spy(new BaseRestClient("http://localhost"));

        int responseCode = 404;
        String responseMessages = "Not Found";
        String response = "test";
        ByteArrayInputStream responseStream = new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8));

        doReturn(con).when(client).getHttpURLConnection(null);
        doReturn(responseCode).when(con).getResponseCode();
        doReturn(responseMessages).when(con).getResponseMessage();
        doReturn(responseStream).when(con).getErrorStream();

        // do
        ErrorCodeRestClientException exception = assertThrows(
            ErrorCodeRestClientException.class,
            () -> client.execute(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Integer[] {}
            )
        );

        verify(con, atLeast(1)).getResponseCode();
        verify(con, times(1)).getResponseMessage();
        verify(con, times(1)).getErrorStream();

        assertEquals(responseCode, exception.getResponseCode());
        assertEquals(responseMessages, exception.getResponseMessage());
        assertEquals(response, new String(exception.getErrorStreamBytes(), StandardCharsets.UTF_8));
    }
}

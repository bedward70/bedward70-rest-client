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

import ru.bedward70.rest.client.body.maker.RestBodyMaker;
import ru.bedward70.rest.client.exception.ErrorCodeRestClientException;
import ru.bedward70.rest.client.exception.ErrorRestClientException;
import ru.bedward70.rest.client.response.acceptor.RestResponseAcceptor;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.nonNull;

/**
 * The base implementation of RestClient interface
 */
public class BaseRestClient implements RestClient {

    /** Default successful HTTP response code */
    public static final int OK_RESPONSE_CODE = 200;

    /** Headers map */
    private final Map<String, String> internalHeaders = new HashMap<>();

    /** Url */
    private final String url;

    /**
     * Constructor
     *
     * @param url url
     */
    public BaseRestClient(final String url) {
        this.url = url;
    }

    @Override
    public HttpURLConnection getHttpURLConnection(String urlSuffix) throws IOException {

        // Generates a request full url
        URL endpointUrl = new URL(url + urlSuffix);
        return (HttpURLConnection) endpointUrl.openConnection();
    }

    @Override
    public <B, R> R execute(
        final String httpMethod,
        final String urlSuffix,
        final B requestBody,
        final RestBodyMaker<B> bodyMaker,
        final Class<R> responseClazz,
        final RestResponseAcceptor<R> responseAcceptor,
        final Map<String, String> headers,
        final Integer... successfulResponseCodes
    ) {
        try {
            HttpURLConnection con = getHttpURLConnection(urlSuffix);
            try {
                // Sets method
                con.setRequestMethod(httpMethod);
                // Sets headers
                Optional.ofNullable(headers)
                    .ifPresent(map -> map.forEach(con::setRequestProperty));
                // Sets internal headers
                internalHeaders.forEach(con::setRequestProperty);

                // Adds Content-Type to headers
                if (nonNull(bodyMaker)) {
                    bodyMaker.setContentTypeProperty(con, requestBody);
                }
                // Adds Accept to headers
                if (nonNull(responseAcceptor)) {
                    responseAcceptor.setAcceptProperty(con);
                }
                // Writes body
                if (nonNull(bodyMaker)) {
                    bodyMaker.write(con, requestBody);
                }

                // Checks response code
                checkResponseCode(con, successfulResponseCodes);

                // Gets response
                return getResponseObject(con, responseClazz, responseAcceptor);
            } finally {
                con.disconnect();
            }
        } catch (IOException e) {
            throw new ErrorRestClientException(e);
        }
    }

    @Override
    public void setBearerToken(final String token) {
        internalHeaders.put("Authorization", "Bearer " + token);
    }

    @Override
    public void setHeader(final String name, final String value) {
        internalHeaders.put(name, value);
    }

    @Override
    public void removeHeader(final String name) {
        internalHeaders.remove(name);
    }

    @Override
    public void removeHeaders() {
        internalHeaders.clear();
    }

    /**
     * Extracts response object
     * @param con HttpURLConnection
     * @param responseClazz response class
     * @param responseAcceptor response acceptor
     * @return response object or null
     *
     * @param <T> generic type
     * @throws IOException IO Exception
     */
    private <T> T getResponseObject(
        final HttpURLConnection con,
        final Class<T> responseClazz,
        final RestResponseAcceptor<T> responseAcceptor
    ) throws IOException {
        T result = null;
        if (nonNull(responseClazz) && nonNull(responseAcceptor)) {
            try (InputStream inputStream = con.getInputStream()) {
                if (nonNull(inputStream)) {
                    result = responseAcceptor.readValue(inputStream, responseClazz);
                }
            }
        }
        return result;
    }

    /**
     * Checks response code
     * @param con HttpURLConnection
     * @param successfulResponseCodes array of successful HTTP codes
     * @throws IOException IO Exception
     */
    private void checkResponseCode(
        final HttpURLConnection con,
        final Integer[] successfulResponseCodes
    ) throws IOException {
        if (!getResponseCodes(successfulResponseCodes).contains(con.getResponseCode())) {
            try (InputStream inputErrorStream = con.getErrorStream()) {
                throw new ErrorCodeRestClientException(
                    con.getResponseCode(),
                    con.getResponseMessage(),
                    nonNull(inputErrorStream) ? null : null
                );
            }
        }
    }

    /**
     * Generates a non-empty list of successful HTTP codes
     * @param successfulResponseCodes array of successful HTTP codes
     * @return a non-empty list of successful HTTP codes
     */
    private static List<Integer> getResponseCodes(
        final Integer[] successfulResponseCodes
    ) {
        return successfulResponseCodes.length == 0
            ? Collections.singletonList(OK_RESPONSE_CODE)
            : Arrays.asList(successfulResponseCodes);
    }
}

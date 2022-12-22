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

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.bedward70.rest.client.body.maker.JsonRestBodyMaker;
import ru.bedward70.rest.client.body.maker.RestBodyMaker;
import ru.bedward70.rest.client.response.acceptor.JsonRestResponseAcceptor;
import ru.bedward70.rest.client.response.acceptor.RestResponseAcceptor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * A JSON Decorator pattern implementation of RestClient interface
 * Adds methods with JSON body maker and JSON response acceptor
 */
public class JsonRestClient implements RestClient {

    /** Wrapped original instance */
    private final RestClient restClient;

    /** Rest body maker */
    private final JsonRestBodyMaker bodyMaker;

    /** Rest response acceptor */
    private final JsonRestResponseAcceptor<?> responseAcceptor;


    /**
     * Constructor
     *
     * @param restClient original instance
     * @param objectMapper object mapper
     */
    public JsonRestClient(
        final RestClient restClient,
        final ObjectMapper objectMapper
    ) {
        this.restClient = restClient;
        this.bodyMaker = new JsonRestBodyMaker(objectMapper);
        this.responseAcceptor = new JsonRestResponseAcceptor<>(objectMapper);
    }

    @Override
    public HttpURLConnection getHttpURLConnection(String urlSuffix) throws IOException {
        return restClient.getHttpURLConnection(urlSuffix);
    }

    /**
     * Executes rest request with out request body
     * @param httpMethod http method
     * @param urlSuffix suffix url
     * @param headers headers
     * @param successfulResponseCodes array of successful HTTP codes
     * @return response object
     *
     * @param <R> generic type
     */
    public <R> R execute(
        final String httpMethod,
        final String urlSuffix,
        final Class<R> responseClazz,
        final Map<String, String> headers,
        final Integer... successfulResponseCodes
    ) {
        return execute(
            httpMethod,
            urlSuffix,
            null,
            null,
            responseClazz,
            responseAcceptor.getGenericInstance(),
            headers,
            successfulResponseCodes
        );
    }

    /**
     * Executes rest request
     * @param httpMethod http method
     * @param urlSuffix suffix url
     * @param requestBody request body
     * @param responseClazz response class
     * @param headers headers
     * @param successfulResponseCodes array of successful HTTP codes
     * @return response object
     *
     * @param <R> generic type
     */
    public <B, R> R execute(
        final String httpMethod,
        final String urlSuffix,
        final B requestBody,
        final Class<R> responseClazz,
        final Map<String, String> headers,
        final Integer... successfulResponseCodes
    ) {
        return execute(
            httpMethod,
            urlSuffix,
            requestBody,
            bodyMaker,
            responseClazz,
            responseAcceptor.getGenericInstance(),
            headers,
            successfulResponseCodes
        );
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
        return restClient.execute(
            httpMethod,
            urlSuffix,
            requestBody,
            bodyMaker,
            responseClazz,
            responseAcceptor,
            headers,
            successfulResponseCodes
        );
    }

    @Override
    public void setBearerToken(String token) {
        restClient.setBearerToken(token);
    }

    @Override
    public void setHeader(String name, String value) {
        restClient.setHeader(name, value);
    }

    @Override
    public void removeHeader(String name) {
        restClient.removeHeader(name);
    }

    @Override
    public void removeHeaders() {
        restClient.removeHeaders();
    }
}

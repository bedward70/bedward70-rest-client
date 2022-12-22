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
import ru.bedward70.rest.client.response.acceptor.RestResponseAcceptor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * RestClient interface
 */
public interface RestClient {

    /**
     * Returns HttpURLConnection
     * @param urlSuffix url suffix
     * @return HttpURLConnection instance
     * @throws IOException IO Exception
     */
    HttpURLConnection getHttpURLConnection(String urlSuffix) throws IOException;

    /**
     * Executes rest request with out request body
     * @param httpMethod http method
     * @param urlSuffix suffix url
     * @param responseClazz response class
     * @param responseAcceptor response acceptor
     * @param headers headers
     * @param successfulResponseCodes array of successful HTTP codes
     * @return response object
     *
     * @param <R> generic type of the response
     */
    default <R> R execute(
        final String httpMethod,
        final String urlSuffix,
        final Class<R> responseClazz,
        final RestResponseAcceptor<R> responseAcceptor,
        final Map<String, String> headers,
        final Integer... successfulResponseCodes
    ) {
        return execute(
            httpMethod,
            urlSuffix,
            null,
            null,
            responseClazz,
            responseAcceptor,
            headers,
            successfulResponseCodes
        );
    }

    /**
     * Executes rest request
     * @param httpMethod http method
     * @param urlSuffix suffix url
     * @param requestBody request body
     * @param bodyMaker body maker
     * @param responseClazz response class
     * @param responseAcceptor response acceptor
     * @param headers headers
     * @param successfulResponseCodes array of successful HTTP codes
     * @return response object
     *
     * @param <B> generic type of the body
     * @param <R> generic type of the response
     */
    <B, R> R execute(
        final String httpMethod,
        final String urlSuffix,
        final B requestBody,
        final RestBodyMaker<B> bodyMaker,
        final Class<R> responseClazz,
        final RestResponseAcceptor<R> responseAcceptor,
        final Map<String, String> headers,
        final Integer... successfulResponseCodes
    );

    /**
     * Adds the "Authorization"-"Bearer " header with the token to headers map
     * @param token the authorized token
     */
    void setBearerToken(final String token);
    /**
     * Sets a header
     * @param name header name
     * @param value header value
     */
    void setHeader(final String name, final String value);

    /**
     * Removes a header
     * @param name header name
     */
    void removeHeader(final String name) ;

    /**
     * Removes all headers
     */
    void removeHeaders();
}

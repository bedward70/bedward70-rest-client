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
package ru.bedward70.rest.client.exception;

import java.util.Optional;
import java.util.function.Function;

public class ErrorCodeRestClientException extends RestClientException {

    /** Response code */
    private final Integer responseCode;
    /** Response message */
    private final String responseMessage;
    /** Error stream bytes */
    private final byte[] errorStreamBytes;

    /**
     * Constructor
     *
     * @param responseCode response Code
     * @param responseMessage response Message
     * @param errorStreamBytes error stream bytes
     */
    public ErrorCodeRestClientException(
        final Integer responseCode,
        final String responseMessage,
        final byte[] errorStreamBytes
    ) {
        super(responseCode + ", " + responseMessage);
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.errorStreamBytes = errorStreamBytes;
    }

    /**
     * @return response code
     */
    public Integer getResponseCode() {
        return responseCode;
    }

    /**
     * @return response message
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * @return error stream bytes
     */
    public byte[] getErrorStreamBytes() {
        return errorStreamBytes;
    }

    /**
     * Returns error object
     * @param function transformation function
     * @return error object or null
     * @param <T> generic type
     */
    public <T> T getErrorObject(Function<byte[], T> function) {
        return Optional.ofNullable(errorStreamBytes).map(function).orElse(null);
    }
}

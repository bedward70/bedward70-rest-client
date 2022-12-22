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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.bedward70.rest.certification.IgnoredCertificate;
import ru.bedward70.rest.client.body.maker.XWwwFormUrlEncodedRestBodyMaker;
import ru.bedward70.rest.client.response.acceptor.StringRestResponseAcceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This demo of connecting to an Apache NiFi instance
 * Set real username and password before using
 * @see <a href="https://nifi.apache.org/docs/nifi-docs/html/getting-started.html">Getting Started with Apache NiFi</a>
 * @see <a href="https://nifi.apache.org/docs/nifi-docs/rest-api/index.html">/nifi-api</a>
 */
@Disabled
public class DemoNifiTest {

    /** Username */
    private static final String USERNAME = "<name>";

    /** Password */
    private static final String PASSWORD = "<password>";

    @Disabled("Disabled: for manual run only with a prepared Apache NiFi instance")
    @Test
    void test() throws IOException {

        // Ignores all validation of certificates
        new IgnoredCertificate().ignore();


        // Default NiFi API url
        String url = "https://localhost:8443/nifi-api";

        // Creates rest client
        BaseRestClient client = new BaseRestClient(url);

        // Gets a token
        String token = getToken(client);
        System.out.println("======================================");
        System.out.println("token = \"" + token + "\"");
        System.out.println("======================================");

        // Sets the token
        client.setBearerToken(token);

        // Creates JSON rest client
        JsonRestClient jsonClient = new JsonRestClient(client, new ObjectMapper());

        // Gets system diagnostics
        Map systemDiagnosticsMap = getSystemDiagnostics(jsonClient, Map.class);
        System.out.println("======================================");
        System.out.println("system-diagnostics (Map) = " + systemDiagnosticsMap);
        System.out.println("======================================");

        // Logouts
        logout(jsonClient);

        // Remotes token
        client.removeHeader("Authorization");

        System.out.println("======================================");
        System.out.println("Successful logout");
        System.out.println("======================================");
    }


    private static String getToken(BaseRestClient client) {

        // Form parameters
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", USERNAME);
        parameters.put("password", PASSWORD);

        return client.execute(
            // Http methods
            "POST",
            // Sub url
            "/access/token",
            // Body object and body maker
            parameters,
            new XWwwFormUrlEncodedRestBodyMaker(),
            // Response instance class and acceptor
            String.class,
            new StringRestResponseAcceptor(),
            // headers
            null,
            // Expected response code
            201
        );
    }

    private static <T> T getSystemDiagnostics(JsonRestClient jsonClient, Class<T> clazz) {
        return jsonClient.execute(
            // Http methods
            "GET",
            // Sub url
            "/system-diagnostics",
            // Response instance class
            clazz,
            // headers
            null
        );
    }

    private static void logout(JsonRestClient jsonClient) {
        jsonClient.execute(
            // Http methods
            "DELETE",
            // Sub url
            "/access/logout",
            // Response instance class
            null,
            // headers
            null
        );
    }
}

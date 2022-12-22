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
package ru.bedward70.rest.certification;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import static java.util.Objects.nonNull;

/**
 * Validate certificates from keystore
 */
public class TrustKeystoreCertificate {

   private final File keystorage;
    private final char[] storepass;
    private final char[] keypass;

    /**
     * Constructor
     *
     * @param keystoreFilename - keystore file with certificates
     * @param keystorePassword - keystore password
     * @param keystorePassword - key password
     */
    public TrustKeystoreCertificate(String keystoreFilename, String keystorePassword, String keystoreKeyPassword) {
        this.keystorage = new File(keystoreFilename);
        this.storepass = keystorePassword.toCharArray();
        this.keypass = nonNull(keystoreKeyPassword) ? keystoreKeyPassword.toCharArray() : this.storepass;
    }


    public void trust() {

        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance("JKS");
            try (InputStream is = new FileInputStream(keystorage)) {
                ks.load(is, storepass);
            }
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, keypass);

            SSLContext sc = SSLContext.getInstance("TLS");
            // initialize a new TMF with our keyStore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX", "SunJSSE");
            tmf.init(ks);

            sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }
}

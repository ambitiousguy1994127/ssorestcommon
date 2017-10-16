package com.idfconnect.ssorest.common.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * <p>SSLUtil class.</p>
 *
 * @author rsand
 * @since 1.4
 */
public class SSLUtil {
    private SSLUtil() {
    }
    
    /**
     * Method getKeyManagers.
     *
     * @param keyStoreType String
     * @param keyStoreFile InputStream
     * @param keyStorePassword char[]
     * @return an array of {@link javax.net.ssl.KeyManager} objects.
     * @throws java.io.IOException if any.
     * @throws java.security.GeneralSecurityException if any.
     * @since 1.4
     */
    public static KeyManager[] getKeyManagers(String keyStoreType, InputStream keyStoreFile, char[] keyStorePassword) throws IOException, GeneralSecurityException {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(keyStoreFile, keyStorePassword);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword);
        return kmf.getKeyManagers();
    }

    /**
     * Method getTrustManagers.
     *
     * @param trustStoreType String
     * @param trustStoreFile InputStream
     * @param trustStorePassword char[]
     * @return an array of {@link javax.net.ssl.TrustManager} objects.
     * @throws java.io.IOException if any.
     * @throws java.security.GeneralSecurityException if any.
     * @since 1.4
     */
    public static TrustManager[] getTrustManagers(String trustStoreType, InputStream trustStoreFile, char[] trustStorePassword) throws IOException, GeneralSecurityException {
        KeyStore trustStore = KeyStore.getInstance(trustStoreType);
        trustStore.load(trustStoreFile, trustStorePassword);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        return tmf.getTrustManagers();
    }
}

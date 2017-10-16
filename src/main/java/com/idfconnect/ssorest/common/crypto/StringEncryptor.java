package com.idfconnect.ssorest.common.crypto;

/**
 * <p>StringEncryptor interface.</p>
 *
 * @author nghia
 */
public interface StringEncryptor {

    /**
     * <p>encrypt.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     * @since 1.3.3
     */
    String encrypt(String message);

    /**
     * <p>decrypt.</p>
     *
     * @param encryptedMessage a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     * @since 1.3.3
     */
    String decrypt(String encryptedMessage);
}

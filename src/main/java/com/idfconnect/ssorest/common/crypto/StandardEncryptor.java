package com.idfconnect.ssorest.common.crypto;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Provider;
import java.security.SecureRandom;

/**
 * <p>StandardEncryptor class.</p>
 *
 * @author nghia
 */
public class StandardEncryptor implements StringEncryptor {

    private final Base64    base64;
    private       String    algorithm     = "PBEWithMD5AndDES";
    private       String    providerName  = null;
    private       Provider  provider      = null;
    private       boolean   initialized   = false;
    private       SecretKey key           = null;
    private       Cipher    encryptCipher = null;
    private       Cipher    decryptCipher = null;
    private       int       nIterations   = 10;
    private       byte[]    salt          = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
    private final int       AES_KEYLENGTH = 128;
    private       byte[]    iv            = new byte[AES_KEYLENGTH / 8];
    private PBEParameterSpec parameterSpec;

    /**
     * <p>Constructor for StandardEncryptor.</p>
     *
     * @since 1.3.3
     */
    public StandardEncryptor() {
        base64 = new Base64();
    }

    /**
     * <p>initialize.</p>
     *
     * @since 1.3.3
     */
    public synchronized void initialize() {
        if (initialized)
            return;
        char[] password = EncryptorConstants.getPassword().toCharArray();
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
        SecretKeyFactory factory;
        try {
            if (this.provider != null) {
                factory = SecretKeyFactory.getInstance(this.algorithm, this.provider);
                this.key = factory.generateSecret(pbeKeySpec);
                this.encryptCipher = Cipher.getInstance(this.algorithm, this.provider);
                this.decryptCipher = Cipher.getInstance(this.algorithm, this.provider);
            } else if (this.providerName != null) {
                factory = SecretKeyFactory.getInstance(this.algorithm, this.providerName);
                this.key = factory.generateSecret(pbeKeySpec);
                this.encryptCipher = Cipher.getInstance(this.algorithm, this.providerName);
                this.decryptCipher = Cipher.getInstance(this.algorithm, this.providerName);
            } else {
                factory = SecretKeyFactory.getInstance(this.algorithm);
                this.key = factory.generateSecret(pbeKeySpec);
                this.encryptCipher = Cipher.getInstance(this.algorithm);
                this.decryptCipher = Cipher.getInstance(this.algorithm);
            }

            SecureRandom prng = new SecureRandom();
            prng.nextBytes(iv);

            parameterSpec = new PBEParameterSpec(salt, this.nIterations, new IvParameterSpec(iv));
        } catch (Throwable throwable) {
            throw new RuntimeException("Unable to initialize StandardEncryptor", throwable);
        } finally {
            cleanPassword(password);
        }

    }

    /** {@inheritDoc} */
    @Override public String encrypt(String message) {
        try {
            if (!initialized) {
                initialize();
            }
            byte[] messageBytes = message.getBytes("UTF-8");
            synchronized (this.encryptCipher) {
                this.encryptCipher.init(Cipher.ENCRYPT_MODE, this.key, parameterSpec);
                byte[] encryptedMessage = this.encryptCipher.doFinal(messageBytes);
                encryptedMessage = this.base64.encode(encryptedMessage);
                return new String(encryptedMessage, "US-ASCII");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not encrypt message", ex);
        }
    }

    /** {@inheritDoc} */
    @Override public String decrypt(String encryptedMessage) {
        try {
            if (!initialized) {
                initialize();
            }
            byte[] encryptedMessageBytes = encryptedMessage.getBytes("US-ASCII");
            encryptedMessageBytes = this.base64.decode(encryptedMessageBytes);
            byte[] message;
            synchronized (this.decryptCipher) {
                this.decryptCipher.init(Cipher.DECRYPT_MODE, this.key, parameterSpec);
                message = this.decryptCipher.doFinal(encryptedMessageBytes);
            }
            return new String(message, "UTF-8");
        } catch (Exception ex) {
            throw new RuntimeException("Could not decrypt message", ex);
        }
    }

    /**
     * <p>Getter for the field <code>algorithm</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 1.3.3
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * <p>Setter for the field <code>algorithm</code>.</p>
     *
     * @param algorithm a {@link java.lang.String} object.
     * @since 1.3.3
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * <p>Getter for the field <code>providerName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 1.3.3
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     * <p>Setter for the field <code>providerName</code>.</p>
     *
     * @param providerName a {@link java.lang.String} object.
     * @since 1.3.3
     */
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    /**
     * <p>Getter for the field <code>provider</code>.</p>
     *
     * @return a {@link java.security.Provider} object.
     * @since 1.3.3
     */
    public Provider getProvider() {
        return provider;
    }

    /**
     * <p>Setter for the field <code>provider</code>.</p>
     *
     * @param provider a {@link java.security.Provider} object.
     * @since 1.3.3
     */
    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    /**
     * <p>Getter for the field <code>nIterations</code>.</p>
     *
     * @return a int.
     * @since 1.3.3
     */
    public int getnIterations() {
        return nIterations;
    }

    /**
     * <p>Setter for the field <code>nIterations</code>.</p>
     *
     * @param nIterations a int.
     * @since 1.3.3
     */
    public void setnIterations(int nIterations) {
        this.nIterations = nIterations;
    }

    private static void cleanPassword(char[] password) {
        if (password != null) {
            synchronized (password) {
                int pwdLength = password.length;

                for (int i = 0; i < pwdLength; ++i) {
                    password[i] = 0;
                }
            }
        }

    }
}

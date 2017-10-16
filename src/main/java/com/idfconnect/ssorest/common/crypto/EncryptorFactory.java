package com.idfconnect.ssorest.common.crypto;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>EncryptorFactory class.</p>
 *
 * @author nghia
 * @since 1.3.3
 */
public class EncryptorFactory {

    /** Constant <code>DEFAULT_ALGORITHM="AES"</code> */
    public final static String DEFAULT_ALGORITHM = "AES";
    private final Map<String, StringEncryptor> encryptorMap;

    private EncryptorFactory() {
        encryptorMap = new HashMap<String, StringEncryptor>();
    }

    private static EncryptorFactory instance = null;

    /**
     * <p>Getter for the field <code>instance</code>.</p>
     *
     * @return a {@link com.idfconnect.ssorest.common.crypto.EncryptorFactory} object.
     */
    public static EncryptorFactory getInstance() {
        if (instance == null) {
            instance = new EncryptorFactory();
        }
        return instance;
    }

    /**
     * <p>getEncryptor.</p>
     *
     * @param algorithm a {@link java.lang.String} object.
     * @return a {@link com.idfconnect.ssorest.common.crypto.StringEncryptor} object.
     */
    public synchronized StringEncryptor getEncryptor(String algorithm) {
        if (encryptorMap.containsKey(algorithm))
            return encryptorMap.get(algorithm);

        if (algorithm == null || algorithm.length() == 0) {
            return getEncryptor(DEFAULT_ALGORITHM);
        }
        StandardEncryptor standardEncryptor = new StandardEncryptor();
        if ("AES".equals(algorithm)) {
            standardEncryptor.setAlgorithm("PBEWithHmacSHA256AndAES_128");
        } else if ("DES".equals(algorithm)) {
            standardEncryptor.setAlgorithm("PBEWithMD5AndDES");
        } else {
            throw new UnsupportedOperationException("Unsupported algorithm " + algorithm);
        }
        encryptorMap.put(algorithm, standardEncryptor);

        return standardEncryptor;
    }

}

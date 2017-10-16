package com.idfconnect.ssorest.common.crypto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>PropertyValueEncryptionUtil class.</p>
 *
 * @author nghia
 */
public class PropertyValueEncryptionUtil {

    private final static Pattern ALG_PATTERN = Pattern.compile("(^\\{AES\\}|\\{DES\\})");

    /**
     * <p>isEncryptedValue.</p>
     *
     * @param value a {@link java.lang.String} object.
     * @return a boolean.
     * @since 1.3.3
     */
    public static boolean isEncryptedValue(String value) {
        if (value == null) {
            return false;
        } else {
            Matcher matcher = ALG_PATTERN.matcher(value);
            return matcher.find();
        }
    }

    private static String[] getEncryptedValueDetails(String value) {
        Matcher matcher = ALG_PATTERN.matcher(value);
        if (matcher.find()) {
            String algo = matcher.group(0);
            String actualEncryptedValue = value.substring(value.indexOf(algo));
            algo = algo.substring(0, algo.length() - 2);
            return new String[] { algo, actualEncryptedValue };

        } else {
            return new String[] { null, value };
        }
    }

    /**
     * <p>
     * getValue.
     * </p>
     *
     * @param value
     *            a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getValue(String value) {
        if (value == null)
            return null;
        String[] details = getEncryptedValueDetails(value);

        if (details[0] == null)
            return details[1];

        return EncryptorFactory.getInstance().getEncryptor(details[0]).decrypt(details[1]);
    }

}

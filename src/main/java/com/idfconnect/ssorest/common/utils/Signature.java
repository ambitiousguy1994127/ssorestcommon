package com.idfconnect.ssorest.common.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * This class defines common routines for generating authentication signatures for AWS requests.
 *
 * @author rsand
 * @since 1.4
 */
public class Signature {
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    
    private static final String UTF8 = StandardCharsets.UTF_8.name();

    /**
     * Computes RFC 2104-compliant HMAC signature.
     *
     * @param data
     *            The data to be signed.
     * @param key
     *            The signing key.
     * @return a {@link java.lang.String} object.
     * @throws java.security.SignatureException
     *            when signature generation fails
     * @since 1.4
     */
    public static String calculateRFC2104HMAC(String data, String key) throws java.security.SignatureException {
        byte[] result;
        try {
            byte[] keyBytes = key.getBytes(UTF8);
            
            // get an hmac_sha1 key from the raw key bytes
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, HMAC_SHA1_ALGORITHM);

            // get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);

            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(data.getBytes(UTF8));

            // base64-encode the hmac
            result = Base64.encodeBase64(rawHmac);

        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        try {
            return new String(result, UTF8);
        } catch (UnsupportedEncodingException ue) {
            throw new SignatureException(ue); // should never happen
        }
    }
}

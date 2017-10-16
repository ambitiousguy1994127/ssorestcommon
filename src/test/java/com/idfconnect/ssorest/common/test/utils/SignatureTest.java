package com.idfconnect.ssorest.common.test.utils;

import static org.junit.Assert.assertEquals;

import java.security.SignatureException;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.idfconnect.ssorest.common.utils.Signature;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SignatureTest {
    Logger        logger       = LoggerFactory.getLogger(getClass());

    // String randomClear = "FImUcfmSRtVVvTTYdPbYtBCWKkhECaOjf568ade1414ff196d46b93b51d21fe1d2365eeba";
    // String randomSigned = "e+lLf1tHFFvNpktmFJiFTcWxWP8=";
    static String secretKey    = "abcd1234";
    static String randomClear  = "AJUqVOxhAZaODAyWFDcZDYkCzCPvxBmx";
    static String randomSigned = "cIOhCPLMh+oJVPt/uIMbOxznnEI=";

    @BeforeClass
    public static void setup() {
        secretKey = System.getProperty("secretKey", secretKey);
        randomClear = System.getProperty("randomClear", randomClear);
        randomSigned = System.getProperty("randomSigned", randomSigned);
    }

    @Test
    public void test1_signatureTest() throws SignatureException {
        String calculatedRandomSigned = Signature.calculateRFC2104HMAC(randomClear, secretKey);
        logger.debug("Calculated value = {}", calculatedRandomSigned);
        assertEquals(randomSigned, calculatedRandomSigned);
    }
}

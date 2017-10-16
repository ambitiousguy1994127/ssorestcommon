package com.idfconnect.ssorest.common.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.idfconnect.ssorest.common.utils.ConfigProperties;
import com.idfconnect.ssorest.common.utils.ConfigPropertiesFactory;

public class ConfigPropertiesTest {
    static final String PREFIX = ConfigPropertiesTest.class.getName() + ".";
    static final String TESTPROPERTY = "someProperty";
    static final String TESTVALUE = "somevalue";
    
    @BeforeClass
    public static void setup() {
        System.setProperty(PREFIX + TESTPROPERTY, TESTVALUE);
    }
    
    @Test
    public void test1() {
        // value from system props
        Map<String,String> map = new HashMap<>();
        ConfigProperties cp = ConfigPropertiesFactory.initFromEverywhere(map, PREFIX);
        assertEquals(TESTVALUE, cp.getProperty(TESTPROPERTY));
        
        // value from system props has precedence over map
        map.put(TESTPROPERTY, TESTVALUE + "1");
        cp = ConfigPropertiesFactory.initFromEverywhere(map, PREFIX);
        assertEquals(TESTVALUE, cp.getProperty(TESTPROPERTY));

        // value from map because prefix is null means no system props
        map.put(TESTPROPERTY, TESTVALUE + "1");
        cp = ConfigPropertiesFactory.initFromEverywhere(map, null);
        assertEquals(TESTVALUE + "1", cp.getProperty(TESTPROPERTY));
    }
    
    @Test
    public void test2() {
        Logger logger = LoggerFactory.getLogger(getClass());
        Map<String,Object> map = new HashMap<>();
        String[] testvalues = new String[] { TESTVALUE + "1", TESTVALUE + "2" };
        map.put(TESTPROPERTY, testvalues);
        ConfigProperties cp = ConfigPropertiesFactory.initFromMap(map);
        logger.debug("CP: {}", cp);
        Object result = cp.getProperty(TESTPROPERTY);
        logger.debug("Results [{}] {}", result.getClass(), result);
        assertTrue(result instanceof List);
        assertEquals(testvalues.length, ((List<?>) result).size());
        assertEquals(TESTVALUE + "1", cp.getPropertyAsSingleValue(TESTPROPERTY));
    }
}

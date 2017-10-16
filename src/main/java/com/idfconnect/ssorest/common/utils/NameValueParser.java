package com.idfconnect.ssorest.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple class for parsing a <em>name=value</em> string
 *
 * @author Richard Sand
 * @since 1.4
 */
public class NameValueParser {
    private String name = null;
    private String value = null;

    /**
     * Convenience method to parse multiple NVPs into a Map
     *
     * @param input a {@link java.lang.String} object.
     * @param delimeter a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     * @since 1.4.2
     */
    public static Map<String,String> parseMultipleNVPs(String input, String delimeter) {
        Map<String,String> results = new HashMap<String,String>();
        String[] tokens = input.split(delimeter);
        for (String token : tokens) {
            NameValueParser nvp = new NameValueParser(token);
            results.put(nvp.getName(), nvp.getValue());
        }
        
        return results;
    }

    /**
     * Creates a NameValueParser
     *
     * @param str an input string of the form <em>name=value</em>
     * @since 1.4.2
     */
    public NameValueParser(String str) {
        // Check for equals sign.
        int index = str.indexOf('=');

        // Split the string at the equals sign.
        if (index > 0) {
            name = str.substring(0, index);
            value = str.substring(index + 1);
        } else { // no equal sign
            name = str;
            value = "";
        }
    }

    /**
     * Gets the name
     *
     * @return String
     * @since 1.4.2
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value
     *
     * @return String
     * @since 1.4.2
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Method toString.
     *
     * @return String
     * @since 1.4.2
     */
    public String toString() {
        return name + "=" + value;
    }
}

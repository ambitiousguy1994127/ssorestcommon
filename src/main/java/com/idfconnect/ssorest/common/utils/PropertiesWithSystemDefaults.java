package com.idfconnect.ssorest.common.utils;

import java.util.Enumeration;
import java.util.Properties;

/**
 * A simple wrapper around java.util.Properties that reads default values the System properties, with a defined prefixed such as <em>test.<em>
 *
 * @author rsand
 * @since 1.4.2
 */
public class PropertiesWithSystemDefaults extends Properties {
    private static final long serialVersionUID = -3747052419622967959L;

    /** Prefix for system properties */
    public static final String SYSTEM_TEST_PROPS_PREFIX = "test.";
    
    /**
     * <p>testProperties.</p>
     *
     * @return a {@link com.idfconnect.ssorest.common.utils.PropertiesWithSystemDefaults} object.
     * @since 3.0
     */
    public static final PropertiesWithSystemDefaults testProperties() {
        return new PropertiesWithSystemDefaults(SYSTEM_TEST_PROPS_PREFIX);
    }
    
    /**
     * <p>createProperties.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @return a {@link com.idfconnect.ssorest.common.utils.PropertiesWithSystemDefaults} object.
     * @since 3.0
     */
    public static final PropertiesWithSystemDefaults createProperties(String prefix) {
        return new PropertiesWithSystemDefaults(prefix);
    }

    private PropertiesWithSystemDefaults() {
        super();
        
    }
    /**
     * <p>Constructor for PropertiesWithSystemDefaults.</p>
     */
    private PropertiesWithSystemDefaults(String prefix) {
        if (!prefix.endsWith("."))
            prefix = prefix + ".";
        Properties newdefaults = new Properties();
        Properties sprops = System.getProperties();
        Enumeration<?> propnames = sprops.propertyNames();
        while (propnames.hasMoreElements()) {
            String propname = (String) propnames.nextElement();
            if (propname.startsWith(prefix))
                newdefaults.setProperty(propname.substring(prefix.length()), (String) sprops.get(propname));
        }
        this.defaults = newdefaults;
    }
}

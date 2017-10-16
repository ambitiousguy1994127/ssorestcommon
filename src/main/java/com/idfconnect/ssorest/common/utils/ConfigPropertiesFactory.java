package com.idfconnect.ssorest.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * ConfigPropertiesFactory class.
 * </p>
 *
 * @author rsand
 * @since 3.0.1
 */
public final class ConfigPropertiesFactory {
    /** Config property constant for use with providing the configuration file name */
    public static final String  CONFIG_FILENAME_PROP          = "configFileName";

    /** Config property constant for use with enabling the file configuration capability */
    public static final String  USE_FILE_CONFIG_PROVIDER_PROP = "useFileConfigProvider";

    private static final String DEFAULT_DELIMITER             = ",";

    private ConfigPropertiesFactory() {
    }

    /**
     * A convenience method to load properties from a java.util.Properties instance. The properties are copied over from this object. Consult the
     * java.util.Properties documentation for a description of the format.
     * <p>
     * The Java properties format does not support multi-valued properties. However, if a delimiter string is specified here (non-null), then any property
     * containing one or more instances of the delimiter will be parsed into a multi-valued property and stored as a List.
     * </p>
     * <p>
     * If a prefix string is specified here (non-null), then only values that begin with that exact string will be parsed into multi-valued properties (the
     * prefix will be stripped off first).
     * </p>
     *
     * @param props
     *            the properties object to load properties from
     * @param delimiter
     *            the delimiter string to use when parsing multi-valued properties (or null to treat all properties as single-valued)
     * @param prefix
     *            a value prefix to search for to identify desired properties
     * @return a {@link com.idfconnect.ssorest.common.utils.ConfigProperties} object.
     * @since 1.4.2
     */
    public static ConfigProperties initFromProperties(Properties props, String delimiter, String prefix) {
        Logger logger = LoggerFactory.getLogger(ConfigPropertiesFactory.class);
        logger.trace("Initializing from properties [delimiter={}][prefix={}]", delimiter, prefix);
        ConfigProperties cp = new ConfigProperties();

        for (String key : props.stringPropertyNames()) {
            String updatedkey = key;
            if (prefix != null) {
                if (!key.startsWith(prefix)) {
                    logger.trace("Skipping key {}", key);
                    continue;
                }
                updatedkey = key.substring(prefix.length());
            }
            String rawValue = props.getProperty(key);
            if (delimiter == null || delimiter.length() == 0 || (rawValue.indexOf(delimiter) < 0)) {
                cp.setProperty(updatedkey, rawValue);
                logger.trace("Loaded [key={}][value={}]", updatedkey, rawValue);
                continue;
            }

            // Parse into a MVP
            StringTokenizer st = new StringTokenizer(rawValue, delimiter);
            List<String> multivalue = new ArrayList<String>();
            while (st.hasMoreTokens())
                multivalue.add(st.nextToken());
            cp.setProperty(updatedkey, multivalue);
            logger.trace("Loaded [key={}][value={}]", updatedkey, multivalue);
        }

        return cp;
    }

    /**
     * This powerful loader first takes in an input map, which *may* be null, and an optional System properties prefix, which may also be null. Then, if
     * <em>prefix</em> is not null, it then loads any System properties that are prefixed by <em>prefix</em>. Next, if there is a property
     * <em>useFileConfigProvider=true</em> and a property <em>configFileName</em>, it will load that file as well. So the order of precedence is
     * <em>File->System—>InputMap</em>
     *
     * @param map
     *            a {@link java.util.Map} object.
     * @param prefix
     *            a {@link java.lang.String} object.
     * @return a {@link com.idfconnect.ssorest.common.utils.ConfigProperties} object.
     */
    public static ConfigProperties initFromEverywhere(Map<? extends Object, ? extends Object> map, String prefix) {
        Logger logger = LoggerFactory.getLogger(ConfigPropertiesFactory.class);

        ConfigProperties configProps = initFromMap(map); // TODO add boolean as to whether prefix should be used here

        // Next load the System properties
        if (prefix != null)
            for (Entry<Object, Object> entry : System.getProperties().entrySet()) {
                String name = (String) entry.getKey();
                if (name.startsWith(prefix)) {
                    String shortname = name.substring(prefix.length());
                    Object old = (String) configProps.setProperty(shortname, (String) entry.getValue());
                    logger.trace("Setting parameter with System property [name={}][systemProperty={}][newValue={}][oldValue={}]", shortname, name, entry.getValue(), old);
                }
            }

        // Finally, load an external properties file, if any
        if (configProps.getPropertyAsBoolean(USE_FILE_CONFIG_PROVIDER_PROP, false)) {
            String filename = configProps.getPropertyAsString(CONFIG_FILENAME_PROP);
            if (filename == null || "".equals(filename)) {
                logger.warn("{} property was set but no property called {} is specified", USE_FILE_CONFIG_PROVIDER_PROP, CONFIG_FILENAME_PROP);
                return configProps;
            }
            File f = new File(filename);
            if (!f.exists() || !f.canRead()) {
                logger.warn("File {} does not exist or cannot be opened", filename);
                return configProps;
            }
            try {
                logger.debug("Loading external properties file {}", filename);
                ConfigProperties fprops = initFromFilename(filename);
                configProps.addProperties(fprops);
            } catch (IOException ioe) {
                logger.error("Caught IOException loading file " + filename, ioe);
            }
        }

        return configProps;
    }

    /**
     * A convenience method to load properties from a Map. This is done by loading a
     *
     * <pre>
     * java.util.Properties
     * </pre>
     *
     * object from the map and then calling <em>initFromProperties(Properties)</em>. We assume the keys are all Strings. Values are all cast to Strings. Arrays
     * are converted to comma-delimited Strings
     *
     * @param map
     *            a {@link java.util.Map} object.
     * @return a {@link com.idfconnect.ssorest.common.utils.ConfigProperties} object.
     */
    public static ConfigProperties initFromMap(Map<? extends Object, ? extends Object> map) {
        return initFromMap(map, DEFAULT_DELIMITER);
    }

    /**
     * A convenience method to load properties from a Map. This is done by loading a
     *
     * <pre>
     * java.util.Properties
     * </pre>
     *
     * object from the map and then calling <em>initFromProperties(Properties)</em>. We assume the keys are all Strings. Values are all cast to Strings. Arrays
     * are converted to delimited Strings
     *
     * @param map
     *            a {@link java.util.Map} object.
     * @param delimiter
     *            a {@link java.lang.String} object.
     * @return a {@link com.idfconnect.ssorest.common.utils.ConfigProperties} object.
     */
    public static ConfigProperties initFromMap(Map<? extends Object, ? extends Object> map, String delimiter) {
        Properties props = new Properties();
        for (Entry<?, ?> entry : map.entrySet()) {
            String key = (String) entry.getKey();
            Object rawValue = entry.getValue();
            if (rawValue == null)
                continue;
            if (rawValue instanceof String) {
                props.setProperty(key, (String) rawValue);
                continue;
            }
            if (!rawValue.getClass().isArray()) {
                props.setProperty(key, rawValue.toString());
                continue;
            }
            Object[] rawArray = (Object[]) rawValue;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rawArray.length; i++) {
                sb.append(rawArray[i]);
                if (i < (rawArray.length - 1))
                    sb.append(delimiter);
            }
            props.setProperty(key, sb.toString());

        }
        return initFromProperties(props, delimiter, null);
    }

    /**
     * Acquires an input stream for the specified path using the classloader, then deleagates to loadFromPropertiesStream(InputStream, String, String).
     *
     * @param path
     *            the path to load properties from
     * @param delimiter
     *            the delimiter string to use when parsing multi-valued properties (or null to treat all properties as single-valued)
     * @param prefix
     *            a value prefix to search for to identify multi-valued properties
     * @return a {@link com.idfconnect.ssorest.common.utils.ConfigProperties} object.
     * @throws java.io.IOException
     *             if any.
     * @since 1.4.2
     */
    public static ConfigProperties initFromResource(String path, String delimiter, String prefix) throws IOException {
        InputStream in = ConfigProperties.class.getResourceAsStream(path);
        if (in == null)
            throw new IOException("Unable to locate stream for file path: " + path);
        try {
            Properties props = new Properties();
            props.load(in);
            return initFromProperties(props, delimiter, prefix);
        } finally {
            in.close();
        }
    }

    /**
     * Initializes by first loading the provided filename as a Properties object
     *
     * @param filename
     *            a {@link java.lang.String} object.
     * @return a {@link com.idfconnect.ssorest.common.utils.ConfigProperties} object.
     * @throws java.io.IOException
     *             if any.
     */
    public static ConfigProperties initFromFilename(String filename) throws IOException {
        FileInputStream is = new FileInputStream(filename);
        Properties props = new Properties();
        props.load(is);
        return initFromProperties(props, null, null);
    }
}

/*
 * Copyright 2013 IDF Connect, Inc.
 * All Rights Reserved.
 */
package com.idfconnect.ssorest.common.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;

import com.idfconnect.ssorest.common.SSORestException;

/**
 * This is a customized map class that is used for storing <code>String</code> -based properties. It is somewhat similar to <code>java.util.Properties</code>,
 * but it uses case-insensitive keys and supports multi-valued properties. A key map is maintained that maps the lower case versions of all property keys to the
 * original case that is actually used to store a property. Using this map, the class allows properties to be stored and retrieved using case-insensitive keys,
 * while maintaining the original-cased keys in its keyset.
 * <p>
 * A property can be mapped to a single value or a List object containing single values. All values are stored internally as <code>String</code> objects. Use
 * <code>getProperty</code> to retrieve a property's value(s) as an <code>Object </code>(either a <code>List</code> or a <code>String</code> will be returned).
 * Use the <code>isMultiProperty</code> method to determine whether a property is a single or multi-valued property. Use <code>getPropertyAsString</code> to
 * retrieve the value of a single-valued property. Use <code>getPropertyAsSingleValue</code> to return a single value for a property. If used on a multi-valued
 * property, this method will return its first value. Use <code>getPropertyAsList</code> to get the value of a multi-valued property. This method will return a
 * list with one element if used on a single-valued property. Convenience methods exist to allow data to be set and retrieved for single-valued properties using
 * non-String data types, as described below.
 * <p>
 * All methods of the form <code>setProperty(String key, <em>type</em> value)</code> are used to set a single-valued property whose value will be the value of
 * <type> converted into a String. For each such method, there is a get property of the form
 * <code>getPropertyAs<em>type</em>(String key, <em>type</em> defaultValue)</code> used to retrieve a single-valued property by converting the String value back
 * into the specified type. If the key can not be found, or if the value can not be converted into the requested type, the value specified in
 * <code>defaultValue</code> will be returned.
 * <p>
 * Additionally, for each type supported (including Strings), a method exists in the form <code>getRequiredPropertyAs<em>type</em>(String key)</code>. These
 * methods are identical to their corresponding methods that do not contain "Required" except that they are used to request properties that are expected to be
 * present; accordingly, they will throw exceptions if the property is not found or contains a value that can not be converted to the requested data type.
 * <p>
 * Note: the <code>getPropertyAsSingleValue</code> method is identical to the <code>getPropertyAsString</code> method except that it returns type Object instead
 * of type <code>String</code>. This method is included for possible future expansion of this class to handle storing non-<code>String</code> properties
 * internally.
 * <p>
 * Methods to load and store to streams are included for backwards compatibility to older versions of <code>ConfigProperties</code>. These methods delegate to
 * <code>Properties</code> instances and do not support the full range of capabilities of this class.
 *
 * @author rsand
 * @since 1.4
 */
public final class ConfigProperties implements Map<String, Object>, Serializable {
    private static final long         serialVersionUID = -5374943004309439412L;

    /** Constant <code>TRUE="true"</code> */
    public static final String        TRUE             = "true";

    /** Constant <code>FALSE="false"</code> */
    public static final String        FALSE            = "false";

    /** Constant <code>DELIMITER="^"</code> */
    public static final String        DELIMITER        = "^";
    private static final String       DOT              = ".";

    // the wrapped map with original-case keys
    ConcurrentHashMap<String, Object> themap;

    // the map of lowercase keys to the original keyname
    ConcurrentHashMap<String, String> keyMap;
   
    String                            delimiter        = DELIMITER;
    String                            prefix           = null;

    /**
     * ConfigProperties constructor comment.
     *
     * @since 1.4.2
     */
    public ConfigProperties() {
        super();
        keyMap = new ConcurrentHashMap<>();
        themap = new ConcurrentHashMap<>();
    }

    /**
     * ConfigProperties constructor comment.
     *
     * @param delimiter a {@link java.lang.String} object.
     * @param prefix a {@link java.lang.String} object.
     * @since 3.0.2
     */
    public ConfigProperties(String delimiter, String prefix) {
        super();
        keyMap = new ConcurrentHashMap<>();
        themap = new ConcurrentHashMap<>();
        this.delimiter = delimiter;
        this.prefix = prefix;
    }

    /**
     * {@inheritDoc}
     *
     * We clone similarly to Hashtable here - referenced to keys and values are retained but other parts are copied
     */
    @Override
    public synchronized Object clone() {
        ConfigProperties cpc = new ConfigProperties();
        cpc.keyMap = new ConcurrentHashMap<String, String>(keyMap);
        cpc.themap = new ConcurrentHashMap<String, Object>(themap);
        return cpc;
    }

    /**
     * Returns the original (fully-cased) key used to store a property.
     * 
     * @param key
     *            a case-insensitive key for which to retrieve the original key
     * @return the original (fully-cased) key used to store a property
     */
    private String getRealKey(String key) {
        // Convert key to lowercase and check the keyMap to retrieve the mixed-case key used to store that case-less key
        String lkey = key.toLowerCase();
        String realkey = keyMap.get(lkey);

        // If there was no key in the map, use the lower case key (we don't store map keys if they are already in lower case)
        if (realkey == null)
            realkey = lkey;

        // Now we should have the key that has actually been used to store this property
        return realkey;
    }

    /**
     * Returns the value for the specified property. This method will return a <code>String</code> for a single-valued property or a <code>List</code> for a
     * multi-valued property. If the specified property is not found, <code>null</code> will be returned.
     *
     * @param key
     *            a case-insensitive key for which to retrieve a property
     * @return the value associated with the specified key
     * @since 1.4.2
     */
    public final Object getProperty(String key) {
        // Property names are accessed in a case-insensitive way but we store keys in their original cases in case we want to retrieve them later. We use the
        // getRealKey() method here to go through the key index to locate the original key used to store the case-neutral version of the specified key.
        String realKey = getRealKey(key);
        if (realKey == null)
            return null;

        // Now we should have the key that has actually been used to store this property
        return themap.get(realKey);
    }

    /**
     * Returns the value for the specified property. This method will return a <code>String</code> for a single-valued property or a <code>List</code> for a
     * multi-valued property. If the specified property is not found, an <code>SSORestException</code> will be thrown.
     *
     * @param key
     *            a case-insensitive key for which to retrieve a required property value
     * @return the value associated with the specified key
     * @throws com.idfconnect.ssorest.common.SSORestException
     *             if no value exists for the specified key
     * @since 1.4.2
     */
    public final Object getRequiredProperty(String key) throws SSORestException {
        Object o = getProperty(key);
        if (o == null)
            throw new SSORestException("Required integer parameter missing or invalid: " + key);
        return o;
    }

    /**
     * Returns the value for the specified property. This method will return a <code>String</code> for a single-valued property or a <code>List</code> for a
     * multi-valued property. If the specified value is not found, <code>defaultValue</code> will be returned.
     *
     * @param key
     *            a case-insensitive key for which to retrieve a property value
     * @param defaultValue
     *            a {@link java.lang.Object} object.
     * @return the value associated with the specified key
     * @since 1.4.2
     */
    public final Object getProperty(String key, Object defaultValue) {
        Object val = getProperty(key);
        if (val == null)
            return defaultValue;
        else
            return val;
    }

    /**
     * Gets a single-valued property object for the specified key. Keys are case-insensitive, so two keys whose lower-case conversions are identical will map to
     * the same value.
     * <p>
     * If the property is not found, the request will be passed to the default property set if one is available. If the property is still not found, null will
     * be returned.
     * <p>
     * If a multi-valued property is found, the first value will be returned (multi-valued properties are stored internally as List objects).
     *
     * @param key
     *            a case-insensitive key for which to retrieve a property value
     * @return the value associated with the specified key
     * @since 1.4.2
     */
    public final Object getPropertyAsSingleValue(String key) {
        Object val = getProperty(key);

        if (val instanceof List) {
            // We found a multi-valued property - return the first value only
            return ((List<?>) val).isEmpty() ? null : ((List<?>) val).get(0);
        } else
            return val;
    }

    /**
     * Gets a single-valued property object for the specified key. Keys are case-insensitive, so two keys whose lower-case conversions are identical will map to
     * the same value.
     * <p>
     * If the property is not found, the request will be passed to the default property set if one is available. If the property is still not found, the
     * specified default value will be returned.
     * <p>
     * If a multi-valued property is found, the first value will be returned (multi-valued properties are stored internally as List objects).
     *
     * @param key
     *            a case-insensitive key for which to retrieve a property value
     * @param defaultValue
     *            a {@link java.lang.Object} object.
     * @return the value associated with the specified key
     * @since 1.4.2
     */
    public final Object getPropertyAsSingleValue(String key, Object defaultValue) {
        Object val = getPropertyAsSingleValue(key);
        return (val == null) ? defaultValue : val;
    }

    /**
     * Returns true if the value for the specified key is multi-valued. Note that multi-valued indicates that zero or more values are stored in a List object
     * associated with the property's key, so it is possible for a multi-valued property to have only a single value.
     * <p>
     * If the property is not found, the request will be passed to the default property set if one is available. If the property is still not found, false will
     * be returned.
     *
     * @param key
     *            a case-insensitive key
     * @return true if the value for the specified key is multi-valued
     * @since 1.4.2
     */
    public final boolean isMultiProperty(String key) {
        String realKey = getRealKey(key);
        Object val = themap.get(realKey);
        if (val == null)
            return false;
        return (val instanceof List<?>);
    }

    /**
     * Gets a multi-valued property object for the specified key. Keys are case-insensitive, so two keys whose lower-case conversions are identical will map to
     * the same value.
     * <p>
     * If the property is not found, the request will be passed to the default property set if one is available. If the property is still not found, null will
     * be returned.
     * <p>
     * If a single-valued property is found, it is wrapped in a List and returned. Note that in this case changes made to the list will not be reflected in the
     * property set since no reference to the List object is itself maintained. Also, if the single-valued property is an empty string, we interpret that as an
     * empty list so the list will contain no elements.
     *
     * @param key
     *            a case-insensitive key for which to retrieve a property value
     * @return the value associated with the specified key converted to a list
     * @since 1.4.2
     */
    @SuppressWarnings("unchecked")
    public final List<String> getPropertyAsList(String key) {
        Object prop = getProperty(key);

        if (prop == null)
            return null;
        else if (prop instanceof List) {
            // We found a multi-valued property - return it
            return (List<String>) prop;
        } else {
            // We found a single-valued property - wrap it in a list and return it
            List<String> mProp = new ArrayList<String>();
            // We leave the list empty if prop is an empty string, otherwise add prop to the list
            if (!(prop instanceof String) || (((String) prop).length() > 0))
                mProp.add((String) prop);
            return mProp;
        }
    }

    /**
     * Gets a multi-valued property object for the specified key. Keys are case-insensitive, so two keys whose lower-case conversions are identical will map to
     * the same value.
     * <p>
     * If the property is not found, the request will be passed to the default property set if one is available. If the property is still not found, the
     * specified default value will be returned.
     * <p>
     * If a single-valued property is found, it is wrapped in a List and returned. Note that in this case changes made to the list will not be reflected in the
     * property set since no reference to the List object is itself maintained.
     *
     * @param key
     *            a case-insensitive key for which to retrieve a property value
     * @param defaultValue
     *            a {@link java.util.List} object.
     * @return the value associated with the specified key converted to a list
     * @since 1.4.2
     */
    public final List<String> getPropertyAsList(String key, List<String> defaultValue) {
        List<String> val = getPropertyAsList(key);
        return (val == null) ? defaultValue : val;
    }

    /**
     * Gets a single-valued property object for the specified key. This method simply delegates to the getPropertyAsSingleValue(String) method, returning null
     * if a non-String value is returned.
     *
     * @param key
     *            a case-insensitive key for which to retrieve a property value
     * @return the value associated with the specified key
     * @since 1.4.2
     */
    public final String getPropertyAsString(String key) {
        Object val = getPropertyAsSingleValue(key);
        return (val instanceof String) ? (String) val : null;
    }

    /**
     * Gets a single-valued property object for the specified key, using first the default property list then the specified default value if the value is not
     * found in this property list.
     *
     * @param key
     *            a case-insensitive key for which to retrieve a property value
     * @param defaultValue
     *            a {@link java.lang.String} object.
     * @return the value associated with the specified key
     * @since 1.4.2
     */
    public final String getPropertyAsString(String key, String defaultValue) {
        String val = getPropertyAsString(key);
        return (val == null) ? defaultValue : val;
    }

    /**
     * Sets a property with a String value or List of only String values. Any other type will cause an IllegalArgumentException to be thrown. It is recommended
     * that for normal circumstances you avoid this method and instead use the various setProperty() methods instead.
     *
     * @param key
     *            the key for the property to store
     * @param value
     *            the value of the property to store
     * @since 1.4.2
     */
    @SuppressWarnings("unchecked")
    public void setPropertyAuto(String key, Object value) {
        if (value instanceof String)
            setProperty(key, (String) value);
        else if (value instanceof List)
            setProperty(key, (List<String>) value);
        else
            throw new IllegalArgumentException("Only a String value or a List of String values may be passed to this method");
    }
    
    /**
     * Adds in the provided additional properties
     *
     * @param newprops a {@link com.idfconnect.ssorest.common.utils.ConfigProperties} object.
     */
    public void addProperties(ConfigProperties newprops) {
        for (Map.Entry<String, Object> entry : newprops.entrySet()) {
            setPropertyAuto(entry.getKey(), entry.getValue());
        };
    }

    /**
     * Sets a property with the specified key and value. Keys are case-insensitive, so setting a value using a key whose lower-case conversion is equivalent to
     * that of a previously used key will overwrite the previous value. Returns the previous value that was overwritten, if any.
     * 
     * @param key
     *            the key for the property to store
     * @param value
     *            the value of the property to store
     * @return the previous value that was overwritten, if any
     */
    private Object setPropertyInternal(String key, Object value) {
        String keylower = key.toLowerCase();
        Object oldkey = keyMap.get(keylower);

        // If key, or anything equivalent to it in a case insensitive
        // comparison, was previously used, we
        // need to remove the old entry first:
        if (oldkey != null)
            remove(oldkey);
        else
            remove(keylower);

        // If the key is not all lowercase, add a keymap entry for it
        // If it is in all lowercase, remove the old mapping if there was one
        if (!key.equals(keylower))
            keyMap.put(keylower, key);
        else
            keyMap.remove(keylower);

        // Now add the property using its original case
        return themap.put(key, value);
    }

    /**
     * Sets a property with the specified key and single value. Keys are case-insensitive, so setting a value using a key whose lower-case conversion is
     * equivalent to that of a previously used key will overwrite the previous value. Returns the previous value that was overwritten, if any.
     *
     * @param key
     *            the key for the property to store
     * @param value
     *            the value of the property to store
     * @return the previous value that was overwritten, if any
     * @since 1.4.2
     */
    public final Object setProperty(String key, String value) {
        return setPropertyInternal(key, value);
    }

    /**
     * Sets a property with the specified key and multiple value. Keys are case-insensitive, so setting a value using a key whose lower-case conversion is
     * equivalent to that of a previously used key will overwrite the previous value. Returns the previous value that was overwritten, if any.
     *
     * @param key
     *            the key for the property to store
     * @param values
     *            the value of the property to store
     * @return the previous value that was overwritten, if any
     * @throws java.lang.IllegalArgumentException
     *             if the list of values contains anything except Strings
     * @since 1.4.2
     */
    public final Object setProperty(String key, List<String> values) {
        if (values != null) {
            for (int i = 0; i < values.size(); i++)
                if (!(values.get(i) instanceof String))
                    throw new IllegalArgumentException("Only a List of String objects may be passed to this method");

            return setPropertyInternal(key, values);
        }
        throw new NullPointerException("Null value passed to setProperty");
    }

    /**
     * Sets a property with the specified key and multiple values. This is a convenience method that converts the array into an ArrayList and is otherwise
     * identical to setProperty(String, List).
     *
     * @param key
     *            the key for the property to store
     * @param values
     *            the value of the property to store
     * @return the previous value that was overwritten, if any
     * @throws java.lang.IllegalArgumentException
     *             if the list of values contains anything except Strings'
     * @since 1.4.2
     */
    public final Object setProperty(String key, Object... values) {
        if (values == null)
            throw new NullPointerException("Null value passed to setProperty");
        ArrayList<String> al = new ArrayList<String>(values.length);
        for (int i = 0; i < values.length; i++)
            al.add((String) values[i]);
        return setProperty(key, al);
    }

    /**
     * Identical to getProperty except this method throws an InvalidParameterException if the requested key is not found and the defaultValue is null.
     *
     * @param key
     *            they key for the property to retrieve
     * @param defaultValue
     *            the default value to return if no value is associated with the specified key
     * @return the value associated with the specified key
     * @throws com.idfconnect.ssorest.common.SSORestException
     *             if the specified key is not associated with a value and if the specified default value is null
     * @since 1.4.2
     */
    public String getRequiredPropertyAsString(String key, String defaultValue) throws SSORestException {
        String val = getPropertyAsString(key, defaultValue);
        if (val == null)
            throw new SSORestException("Required parameter missing: " + key);
        return val;
    }

    /**
     * Identical to getPropertyAsList except this method throws an InvalidParameterException if the requested key is not found.
     *
     * @param key
     *            a case-insensitive key for which to retrieve a property value
     * @param defaultValue
     *            the default value to return if no value is associated with the specified key
     * @return the value associated with the specified key converted to a list
     * @throws com.idfconnect.ssorest.common.SSORestException
     *             if the specified key is not associated with a value and if the specified default value is null
     * @since 1.4.2
     */
    public final List<String> getRequiredPropertyAsList(String key, List<String> defaultValue) throws SSORestException {
        List<String> val = getPropertyAsList(key, defaultValue);
        if (val == null) {
            throw new SSORestException("Required parameter missing: " + key);
        } else
            return val;
    }

    /**
     * Identical to getPropertyAsList except this method throws an InvalidParameterException if the requested key is not found.
     *
     * @param key
     *            a case-insensitive key for which to retrieve a property value
     * @return the value associated with the specified key converted to a list
     * @throws com.idfconnect.ssorest.common.SSORestException
     *             if the specified key is not associated with a value
     * @since 1.4.2
     */
    public final List<String> getRequiredPropertyAsList(String key) throws SSORestException {
        return getRequiredPropertyAsList(key, null);
    }

    /**
     * Identical to getPropertyAsString except this method throws an InvalidParameterException if the requested key is not found.
     *
     * @param key
     *            a case-insensitive key for which to retrieve a property value
     * @return the value associated with the specified key converted to a list
     * @throws com.idfconnect.ssorest.common.SSORestException
     *             if the specified key is not associated with a value
     * @since 1.4.2
     */
    public String getRequiredPropertyAsString(String key) throws SSORestException {
        return getRequiredPropertyAsString(key, null);
    }

    /**
     * Returns the requested property after converting its value to an integer. This method throws an InvalidParameterException if the requested key is not
     * found or if its value can not be converted into an integer.
     *
     * @param key
     *            a case-insensitive key for which to retrieve a property value
     * @return the value associated with the specified key converted to an int
     * @throws com.idfconnect.ssorest.common.SSORestException
     *             if the specified key is not associated with a value
     * @since 1.4.2
     */
    public int getRequiredPropertyAsInt(String key) throws SSORestException {
        String val = getPropertyAsString(key);
        if (val != null)
            try {
                int i = Integer.parseInt(val);
                return i;
            } catch (NumberFormatException e) {
            }

        throw new SSORestException("Required integer parameter missing or invalid: " + key);
    }

    /**
     * Returns the requested property after converting its value to a boolean. This method throws an InvalidParameterException if the requested key is not found
     * or if its value can not be converted into a boolean.
     *
     * @param key
     *            a key which must contain a value which will match a case insensitive comparison to either "true" or "false"
     * @return true if the key's value matches "true", false if it matches "false"
     * @throws com.idfconnect.ssorest.common.SSORestException
     *             if the key does not exist or if its value can not be converted into a boolean
     * @since 1.4.2
     */
    public boolean getRequiredPropertyAsBoolean(String key) throws SSORestException {
        String val = getPropertyAsString(key);
        if (val != null) {
            if (val.toLowerCase().equals(TRUE))
                return true;
            else if (val.toLowerCase().equals(FALSE))
                return false;
        }

        throw new SSORestException("Required true/false parameter missing or invalid: " + key);
    }

    /**
     * Returns the requested property after converting its value to a long. This method throws an InvalidParameterException if the requested key is not found or
     * if its value can not be converted into a long.
     *
     * @param key
     *            a case-insensitive key for which to retrieve a property value
     * @return the value associated with the specified key converted to a long
     * @throws com.idfconnect.ssorest.common.SSORestException
     *             if the specified key is not associated with a value
     * @since 1.4.2
     */
    public long getRequiredPropertyAsLong(String key) throws SSORestException {
        String val = getPropertyAsString(key);
        if (val != null)
            try {
                long i = Long.parseLong(val);
                return i;
            } catch (NumberFormatException e) {
            }

        throw new SSORestException("Required long parameter missing or invalid: " + key);
    }

    /**
     * {@inheritDoc}
     *
     * Overridden to make sure keys entered with this method end up in lower case.
     * @deprecated use the appropriate setPropery() methods instead of this one
     */
    @Deprecated
    @Override
    @SuppressWarnings("unchecked")
    public synchronized Object put(String key, Object value) {
        if (value instanceof String)
            return setProperty(key, (String) value);
        if (value instanceof List)
            return setProperty(key, (List<String>) value);
        throw new IllegalArgumentException("This method must be called with a String key and either a value that is a String or a List containing only Strings");
    }

    /**
     * {@inheritDoc}
     *
     * Overridden to support case-insensitivity of String keys
     */
    @Override
    public synchronized Object remove(Object key) {
        if (key instanceof String) {
            // Get the underlying key used to store this entry
            String realKey = getRealKey((String) key);

            // Remove the keymap entry
            keyMap.remove(((String) key).toLowerCase());

            // Now realkey should contain the key that has actually been used to store this property
            return themap.remove(realKey);
        }
        return themap.remove(key);
    }

    /**
     * <p>
     * getPropertyAsInt.
     * </p>
     *
     * @param key
     *            a {@link java.lang.String} object.
     * @param defaultValue
     *            a int.
     * @return a int.
     * @since 1.4.2
     */
    public final int getPropertyAsInt(String key, int defaultValue) {
        return StringUtil.parseInt(getPropertyAsString(key), defaultValue);
    }

    /**
     * Convenience method that uses getPropertyAsList to retrieve the specified property, then creates an array of integers by converting the values (the same
     * way that getPropertyAsInt does).
     *
     * @param key
     *            a case-insensitive key for which to retrieve a property value
     * @param defaultValue
     *            the default value to return if no value is associated with the specified key
     * @return the value or values of the specified property converted to an int[]
     * @since 1.4.2
     */
    public final int[] getPropertyAsIntArray(String key, int defaultValue) {
        List<String> l = getPropertyAsList(key);
        if (l == null)
            return null;
        else {
            int[] a = new int[l.size()];
            for (int i = 0; i < l.size(); i++)
                a[i] = StringUtil.parseInt(l.get(i), defaultValue);
            return a;
        }
    }

    /**
     * Convenience method that gets a value and converts it to a long.
     *
     * @param key
     *            a case-insensitive key for which to retrieve a property value
     * @param defaultValue
     *            the default value to use if the specified key is not associated with a value
     * @return the value associated with the specified key converted to a long
     * @since 1.4.2
     */
    public final long getPropertyAsLong(String key, long defaultValue) {
        return StringUtil.parseLong(getPropertyAsString(key), defaultValue);
    }

    /**
     * Retrieves the requested value and attempts to convert it into a <code>boolean</code> as follows: the value is compared to <code>trueValue</code> and if
     * it matches <code>true</code> is returned; then the value if compared to <code>falseValue</code> and if it matches <code>false</code> is returned;
     * otherwise <code>defaultValue</code> is returned. The comparisons are case sensitive only if <code>caseSensitive</code> is true.
     *
     * @param key
     *            the key of the property to search for
     * @param trueValue
     *            a non-null String indicating a true value
     * @param falseValue
     *            a non-null String indicating a false value
     * @param caseSensitive
     *            true for case-sensitive comparisons
     * @param defaultValue
     *            the value to return if the key is not found or if the value does not match either value provided
     * @return true if the value matches trueValue, false if the value matches falseValue, otherwise defaultValue
     * @since 1.4.2
     */
    public final boolean getPropertyAsBoolean(String key, String trueValue, String falseValue, boolean caseSensitive, boolean defaultValue) {
        String val = getPropertyAsString(key);
        if (val == null)
            return defaultValue;
        else if (caseSensitive) {
            if (val.equals(trueValue))
                return true;
            else if (val.equals(falseValue))
                return false;
            else
                return defaultValue;
        } else {
            if (val.toLowerCase().equals(trueValue.toLowerCase()))
                return true;
            else if (val.toLowerCase().equals(falseValue.toLowerCase()))
                return false;
            else
                return defaultValue;
        }
    }

    /**
     * Same as <code>getPropertyAsBoolean(key, "true", "false", false, defaultValue)</code> .
     *
     * @param key
     *            a case-insensitive key for which to retrieve a property value
     * @param defaultValue
     *            the default value to use if the specified key is not associated with a value
     * @return the value associated with the specified key converted to a boolean
     * @since 1.4.2
     */
    public final boolean getPropertyAsBoolean(String key, boolean defaultValue) {
        return getPropertyAsBoolean(key, TRUE, FALSE, false, defaultValue);
    }

    /**
     * Sets a property using the specified key and value. The value is converted into the string "true" or "false".
     *
     * @param key
     *            the key to associate a value with
     * @param value
     *            the value to associate with the specified key
     * @return the previous value that was overwritten, if any
     * @since 1.4.2
     */
    public Object setProperty(String key, boolean value) {
        return setProperty(key, value ? TRUE : FALSE);
    }

    /**
     * Sets a property using the specified key and value. The value is converted into a string.
     *
     * @param key
     *            the key to associate a value with
     * @param value
     *            the value to associate with the specified key
     * @return the previous value that was overwritten, if any
     * @since 1.4.2
     */
    public Object setProperty(String key, int value) {
        return setProperty(key, Integer.toString(value));
    }

    /**
     * Sets a property using the specified key and value. The value is converted into a string.
     *
     * @param key
     *            the key to associate a value with
     * @param value
     *            the value to associate with the specified key
     * @return the previous value that was overwritten, if any
     * @since 1.4.2
     */
    public Object setProperty(String key, long value) {
        return setProperty(key, Long.toString(value));
    }

    /**
     * Gets a single- or multi-valued property and converts it into a single String object. Multi-valued properties are concatenated into a delimited String as
     * per the MvaUtil.buildMva(List) method. This is a utility method used by the store() method.
     * 
     * @param key
     *            the key for which to return a single string value
     * @param delimeter
     * @return the value of the specified property converted to a single string object
     */
    @SuppressWarnings("unchecked")
    private String getPropertyAsSingleValueString(String key, String delimeter) {
        String realKey = getRealKey(key);
        Object val = themap.get(realKey);

        if (val == null)
            return null;
        else if (val instanceof String)
            return (String) val;
        else if (val instanceof List) {
            // Quick hack to make delimited string from list
            List<String> values = (List<String>) val;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < values.size(); i++) {
                String s = values.get(i);
                if (s != null)
                    sb.append(s);
                if (i != values.size() - 1)
                    sb.append(delimeter);
            }
            return sb.toString();
        } else
            // Not a String - can't use this
            return null;
    }

    /**
     * Save properties to an OutputStream.
     * <p>
     * This task is delegated to a Properties instance and the properties are copied over to this object. Multi-valued properties are converted to delimited
     * strings.
     * </p>
     *
     * @param out
     *            a {@link java.io.OutputStream} object.
     * @param header
     *            a {@link java.lang.String} object.
     * @throws java.io.IOException
     *             if any.
     * @since 1.4.2
     */
    public synchronized void storePropertiesToStream(OutputStream out, String header) throws IOException {
        storePropertiesToStream(out, header, delimiter);
    }

    /**
     * Save properties to an OutputStream.
     * <p>
     * This task is delegated to a Properties instance and the properties are copied over to this object. Multi-valued properties are converted to delimited
     * strings.
     * </p>
     *
     * @param out
     *            a {@link java.io.OutputStream} object.
     * @param header
     *            a {@link java.lang.String} object.
     * @param delimeter
     *            a {@link java.lang.String} object.
     * @throws java.io.IOException
     *             if any.
     * @since 1.4.2
     */
    public synchronized void storePropertiesToStream(OutputStream out, String header, String delimeter) throws IOException {
        // Create a Properties object to delegate to
        Properties props = new Properties();

        // Copy all of our properties into it
        Iterator<String> iter = keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            String val = getPropertyAsSingleValueString(key, delimeter);
            if (prefix != null)
                key = prefix + DOT;
            if (val != null) {
                LoggerFactory.getLogger(getClass()).trace("Storing [key={}][value={}]", key, val);
                props.setProperty(key, val);
            }
        }

        // Store 'em
        props.store(out, header);
    }

    /**
     * Returns true if the properties set contains a mapping for the specified key. The comparison is case-insensitve. Use this method instead of containsKey to
     * avoid false negatives (because containsKey is not case-insensitive). If this object was constructed with another ConfigProperties object to be used for
     * defaults, that object will not be consulted when checking for the presence of the specified key. This method will only return true if this object alone
     * contains the specified key. This method can therefore be used to determine whether a property value was taken from this object or from the defaults
     * object.
     *
     * @param key
     *            the key to check for
     * @return a boolean.
     * @since 1.4.2
     */
    public boolean containsPropertyKey(String key) {
        return themap.containsKey(getRealKey(key));
    }

    /**
     * Returns a new ConfigProperties object that is a subset of the current instance. The new object will contain all of the keys that start with
     * <code>&lt;tag&gt; + "."</code>.
     *
     * @param tag
     *            a {@link java.lang.String} object.
     * @return a {@link com.idfconnect.ssorest.common.utils.ConfigProperties} object.
     * @since 1.4.2
     */
    public ConfigProperties getSubProperties(final String tag) {
        if (tag == null)
            return null;
        String usetag = tag.toLowerCase() + DOT;
        ConfigProperties sub = new ConfigProperties();
        Enumeration<String> en = themap.keys();
        while (en.hasMoreElements()) {
            String nextkeyraw = en.nextElement();
            String nextkey = nextkeyraw.toLowerCase();
            if (nextkey.startsWith(usetag)) {
                String newkey = nextkeyraw.substring(usetag.length());
                sub.setPropertyInternal(newkey, this.getProperty(nextkeyraw));
            }
        }
        return sub;
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return themap.size();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return themap.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    @Deprecated
    public boolean containsKey(Object key) {
        return themap.containsKey(key);
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsValue(Object value) {
        return themap.containsValue(value);
    }

    /** {@inheritDoc} */
    @Override
    @Deprecated
    public Object get(Object key) {
        return themap.get(key);
    }

    /** {@inheritDoc} */
    @Override
    @Deprecated
    public void putAll(Map<? extends String, ? extends Object> m) {
        for (Entry<? extends String, ? extends Object> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        themap.clear();
        keyMap.clear();
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> keySet() {
        return themap.keySet();
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Object> values() {
        return themap.values();
    }

    /** {@inheritDoc} */
    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return themap.entrySet();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ConfigProperties [");
        builder.append(themap);
        builder.append("]");
        return builder.toString();
    }
}

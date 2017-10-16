package com.idfconnect.ssorest.common.utils;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * A Hashtable extension which uses case-insensitive Strings as keys
 *
 * @author rsand
 * @since 1.4
 */
public class CaseInsensitiveHashtable<V> extends Hashtable<String, V> {

    private static final long       serialVersionUID = 5668481669235529637L;

    // List of the actual keys (case sensitive) used
    private HashMap<String, String> keyConverter     = null;

    /**
     * <p>Constructor for CaseInsensitiveHashtable.</p>
     *
     * @since 1.4.2
     */
    public CaseInsensitiveHashtable() {
        super();
        keyConverter = new HashMap<String, String>();
    }

    /**
     * Constructor for CaseInsensitiveHashtable.
     *
     * @param initialCapacity int
     * @param loadFactor float
     * @since 1.4.2
     */
    public CaseInsensitiveHashtable(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        keyConverter = new HashMap<String, String>(initialCapacity, loadFactor);
    }

    /**
     * Constructor for CaseInsensitiveHashtable.
     *
     * @param initialCapacity int
     * @since 1.4.2
     */
    public CaseInsensitiveHashtable(int initialCapacity) {
        super(initialCapacity);
        keyConverter = new HashMap<String, String>(initialCapacity);
    }

    // TODO: Needs to be converted to use HashMap
    /**
     * Constructor for CaseInsensitiveHashtable.
     *
     * @param t Map<? extends String,? extends V>
     * @since 1.4.2
     */
    public CaseInsensitiveHashtable(Map<? extends String, ? extends V> t) {
        // Note - not initializing the superclass with the argument - because we don't want to add the Map
        // through the superclass - we want to use the subclass functionality
        super();
        keyConverter = new HashMap<String, String>();
        putAll(t);
    }

    /**
     * {@inheritDoc}
     *
     * Method clear.
     */
    @Override
    public synchronized void clear() {
        keyConverter.clear();
        super.clear();
    }

    /**
     * {@inheritDoc}
     *
     * Method clone.
     */
    @Override
    public synchronized Object clone() {
        @SuppressWarnings("unchecked")
        CaseInsensitiveHashtable<V> newHashtable = (CaseInsensitiveHashtable<V>) super.clone();
        HashMap<String, String> newKeyConverter = new HashMap<String, String>(this.keyConverter);
        newHashtable.keyConverter = newKeyConverter;
        return newHashtable;
    }

    /**
     * {@inheritDoc}
     *
     * Method contains.
     */
    @Override
    public synchronized boolean contains(Object value) {
        return super.contains(value);
    }

    /**
     * {@inheritDoc}
     *
     * Method containsKey.
     */
    @Override
    public synchronized boolean containsKey(Object key) {
        return keyConverter.containsKey(((String) key).toLowerCase());
    }

    /**
     * {@inheritDoc}
     *
     * Method containsValue.
     */
    @Override
    public boolean containsValue(Object value) {
        return super.containsValue(value);
    }

    /**
     * {@inheritDoc}
     *
     * Method elements.
     */
    @Override
    public synchronized Enumeration<V> elements() {
        return super.elements();
    }

    /**
     * {@inheritDoc}
     *
     * Method entrySet.
     */
    @Override
    public Set<java.util.Map.Entry<String, V>> entrySet() {
        return super.entrySet();
    }

    /**
     * {@inheritDoc}
     *
     * Method equals.
     */
    @Override
    public synchronized boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof CaseInsensitiveHashtable))
            return false;
        CaseInsensitiveHashtable<?> other = (CaseInsensitiveHashtable<?>) obj;

        // In order to be equal, the following conditions must be met:
        // 1. Same number of keys
        // 2. The lowercase representation of the keys is identical in each object (it's OK
        // if one has Test1 and one has TEST1 as the real key - both have the lowercase key of test1)
        // 3. The get for each similar key (lowercase match) returns the same value.
        if (keyConverter.size() != other.keyConverter.size()) {
            return false;
        }
        if (this.size() != other.size()) {
            return false;
        }

        // Loop through the keys in the keyConverter and see if the other keyConverter contains it.
        // The actual result does not matter - they may not match - as long as the lowercase key is present then
        // the keyConverters are functionally equal
        // if the key is present in the other, then compare the values using equals()
        for (java.util.Map.Entry<String, String> entry : keyConverter.entrySet()) {
            if (!(other.keyConverter.containsKey(entry.getKey()))) {
                return false;
            }
            if (!(this.get(entry.getValue()).equals(other.get(other.keyConverter.get(entry.getKey()))))) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * Method get.
     */
    @Override
    public synchronized V get(Object key) {
        if (key == null || !(key instanceof String))
            return null;
        
        String realKey = keyConverter.get(((String) key).toLowerCase());

        if (realKey == null) {
            return null;
        } else {
            return super.get(realKey);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Method hashCode.
     */
    @Override
    public synchronized int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((keyConverter == null) ? 0 : keyConverter.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * Method isEmpty.
     */
    @Override
    public synchronized boolean isEmpty() {
        return super.isEmpty();
    }

    /**
     * {@inheritDoc}
     *
     * Method keySet.
     */
    @Override
    public Set<String> keySet() {
        return super.keySet();
    }

    /**
     * {@inheritDoc}
     *
     * Method keys.
     */
    @Override
    public synchronized Enumeration<String> keys() {
        return super.keys();
    }

    /**
     * {@inheritDoc}
     *
     * Method put.
     */
    @Override
    public synchronized V put(String key, V value) {
        V oldValue = null;
        String oldKey = null;

        // Check to see if a key already exists for this entry - if so remove it, store the oldKey value
        if (keyConverter.containsKey(key.toLowerCase())) {
            oldKey = keyConverter.remove(key.toLowerCase());

            // Add the new key and value
            keyConverter.put(key.toLowerCase(), key);

            // If the new key is the same, can just replace it
            if (key.equals(oldKey)) {
                oldValue = super.put(key, value);
            } else {
                oldValue = super.remove(oldKey);
                super.put(key, value);
            }
        } else {
            // New entry - just add the key and value
            keyConverter.put(key.toLowerCase(), key);
            super.put(key, value);
        }

        return oldValue;
    }

    /**
     * {@inheritDoc}
     *
     * Method putAll.
     */
    @Override
    public synchronized void putAll(Map<? extends String, ? extends V> t) {
        for (Map.Entry<? extends String, ? extends V> entry : t.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * {@inheritDoc}
     *
     * Method remove.
     */
    @Override
    public synchronized V remove(Object key) {
        if (keyConverter.containsKey(((String) key).toLowerCase())) {
            String oldKey = keyConverter.remove(((String) key).toLowerCase());
            return super.remove(oldKey);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * Method size.
     */
    @Override
    public synchronized int size() {
        return super.size();
    }

    /**
     * {@inheritDoc}
     *
     * Method toString.
     */
    @Override
    public synchronized java.lang.String toString() {
        return super.toString();
    }

    /**
     * {@inheritDoc}
     *
     * Method values.
     */
    @Override
    public Collection<V> values() {
        return super.values();
    }
}

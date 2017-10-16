package com.idfconnect.ssorest.common.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.collect.BiMap;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.HashBiMap;

/**
 * A quick-and-dirty implementation of a sorted BiMap backed by a HashBiMap
 *
 * @author Richard Sand
 * @param <K>
 * @param <V>
 */
public class SortedBiMap<K, V> extends ForwardingMap<K, V> implements BiMap<K, V> {
    HashBiMap<K, V> map;

    /**
     * <p>create.</p>
     *
     * @param <K> a K object.
     * @param <V> a V object.
     * @return a {@link com.idfconnect.ssorest.common.utils.SortedBiMap} object.
     * @since 3.0
     */
    public static <K, V> SortedBiMap<K, V> create() {
        return new SortedBiMap<K, V>();
    }

    /**
     * <p>create.</p>
     *
     * @param expectedSize a int.
     * @param <K> a K object.
     * @param <V> a V object.
     * @return a {@link com.idfconnect.ssorest.common.utils.SortedBiMap} object.
     * @since 3.0
     */
    public static <K, V> SortedBiMap<K, V> create(int expectedSize) {
        return new SortedBiMap<K, V>(expectedSize);
    }

    /**
     * <p>create.</p>
     *
     * @param map a {@link java.util.Map} object.
     * @param <K> a K object.
     * @param <V> a V object.
     * @return a {@link com.idfconnect.ssorest.common.utils.SortedBiMap} object.
     * @since 3.0
     */
    public static <K, V> SortedBiMap<K, V> create(Map<? extends K, ? extends V> map) {
        SortedBiMap<K, V> bimap = create(map.size());
        bimap.putAll(map);
        return bimap;
    }

    private SortedBiMap() {
        map = HashBiMap.create();
    }

    private SortedBiMap(int expectedSize) {
        map = HashBiMap.create(expectedSize);
    }

    /** {@inheritDoc} */
    @Override
    public SortedSet<K> keySet() {
        return new TreeSet<K>(map.keySet());
    }

    /** {@inheritDoc} */
    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        TreeMap<K, V> newmap = new TreeMap<K, V>(map);
        return newmap.entrySet();
    }

    /** {@inheritDoc} */
    @Override
    public V forcePut(K key, V value) {
        return map.forcePut(key, value);
    }

    /** {@inheritDoc} */
    @Override
    public BiMap<V, K> inverse() {
        BiMap<V, K> inverse = HashBiMap.create(map.size());
        Set<K> keys = keySet();
        for (K key : keys)
            inverse.put(map.get(key), key);
        return inverse;
    }

    /** {@inheritDoc} */
    @Override
    public Set<V> values() {
        return new HashSet<V>(new TreeMap<K, V>(map).values());
    }

    /** {@inheritDoc} */
    @Override
    protected Map<K, V> delegate() {
        return map;
    }
}

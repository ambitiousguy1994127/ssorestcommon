package com.idfconnect.ssorest.common.cache;

import java.util.Dictionary;
import java.util.Enumeration;

/**
 * <p>Abstract BaseCache class.</p>
 *
 * @author rsand
 * @since 1.4
 */
public abstract class BaseCache extends Dictionary<Object, Object> {

    /**
     * <p>Constructor for BaseCache.</p>
     *
     * @since 1.4
     */
    public BaseCache() {
    }

    /**
     * Method elements.
     *
     * @return Enumeration<Object>
     * @since 1.4
     */
    public abstract Enumeration<Object> elements();

    /**
     * {@inheritDoc}
     *
     * Method get.
     */
    public abstract Object get(Object obj);

    /**
     * Method isEmpty.
     *
     * @return boolean
     * @since 1.4
     */
    public abstract boolean isEmpty();

    /**
     * Method keys.
     *
     * @return Enumeration<Object>
     * @since 1.4
     */
    public abstract Enumeration<Object> keys();

    /**
     * {@inheritDoc}
     *
     * Method put.
     */
    public abstract Object put(Object obj, Object obj1);

    /**
     * {@inheritDoc}
     *
     * Method remove.
     */
    public abstract Object remove(Object obj);

    /**
     * Method size.
     *
     * @return int
     * @since 1.4
     */
    public abstract int size();

    /**
     * <p>clear.</p>
     *
     * @since 1.4
     */
    public abstract void clear();
}

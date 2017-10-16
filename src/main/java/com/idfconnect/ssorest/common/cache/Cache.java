package com.idfconnect.ssorest.common.cache;

/**
 * <p>Cache interface.</p>
 *
 * @author rsand
 * @since 1.4
 */
public interface Cache {

    /**
     * Method elements.
     *
     * @param key a {@link java.lang.Object} object.
     * @param entry a {@link java.lang.Object} object.
     * @return Enumeration<Object>
     * @since 1.4
     */
    public abstract Object put(Object key, Object entry) ;

    /**
     * <p>dumpKey.</p>
     *
     * @since 1.4
     */
    public abstract void dumpKey();

    /**
     * Method isEmpty.
     *
     * @return boolean
     * @since 1.4
     */
    public abstract boolean isEmpty();

    /**
     * Method put.
     *
     * @since 1.4
     */
    public abstract void removeTail();

    /**
     * Method remove.
     *
     * @param obj Object
     * @return Object
     * @since 1.4
     */
    public abstract Object remove(Object obj);

    /**
     * <p>get.</p>
     *
     * @param key a {@link java.lang.Object} object.
     * @return a {@link java.lang.Object} object.
     * @since 1.4
     */
    public Object get(Object key);

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

    /**
     * <p>flush.</p>
     *
     * @since 1.4
     */
    public abstract void flush();

    /**
     * Method add.
     *
     * @param objKey Object
     * @param objValue Object
     * @since 1.4
     */
    public abstract void add(Object objKey, Object objValue);

    /**
     * Method assertMaxCacheSize.
     *
     * @param maxSize int
     * @since 1.4
     */
    public abstract void assertMaxCacheSize(int maxSize);

    /**
     * Method assertMaxCacheSize.
     *
     * @param maxSize int
     * @param timeOut int
     * @since 1.4
     */
    public abstract void assertMaxCacheSize(int maxSize, int timeOut);

    /**
     * Method toString.
     *
     * @return String
     * @since 1.4
     */
    public abstract String toString();

    /**
     * <p>close.</p>
     *
     * @since 1.4
     */
    public abstract void close();

}

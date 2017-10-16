package com.idfconnect.ssorest.common.cache;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * <p>CacheLocalImpl class.</p>
 *
 * @author rsand
 * @since 1.4
 */
public class CacheLocalImpl implements Cache{
    /**
     */
    static class DLLEntry {
        /**
         * Method toString.
         * @return String
         */
        public String toString() {
            return key + ":" + value;
        }

        DLLEntry next;
        DLLEntry previous;
        Object   value;
        Object   key;
        long     timestamp;

        /**
         * Constructor for DLLEntry.
         * @param obj Object
         * @param obj1 Object
         */
        DLLEntry(Object obj, Object obj1) {
            next = null;
            previous = null;
            value = null;
            key = null;
            key = obj;
            value = obj1;
            timestamp = System.currentTimeMillis();
        }
    }

    int                         maxsize;
    int                         size;
    int                         cacheTimeOut;
    Hashtable<Object, DLLEntry> hash;
    DLLEntry                    head;
    DLLEntry                    tail;

    /**
     * <p>Constructor for CacheLocalImpl.</p>
     *
     * @since 1.4
     */
    public CacheLocalImpl() {
        this(100, Integer.MAX_VALUE);
    }

    /**
     * Constructor for Cache.
     *
     * @param i int
     * @param timeOut int
     * @since 1.4
     */
    public CacheLocalImpl(int i, int timeOut) {
        head = null;
        tail = null;
        maxsize = i;
        size = 0;
        hash = new Hashtable<Object, DLLEntry>(i);
        cacheTimeOut = timeOut;
    }

    /**
     * Method expanseCapacity.
     *
     * @param newSize int
     * @since 1.4
     */
    public synchronized void expanseCapacity(int newSize) {
        if (newSize > maxsize)
            maxsize = newSize;
    }

    /**
     * Method elements.
     *
     * @return Enumeration<Object>
     * @since 1.4
     */
    public synchronized Enumeration<Object> elements() {
        Vector<Object> vector = new Vector<Object>();
        DLLEntry dllentry = head;
        if (dllentry != null)
            do
                vector.addElement(dllentry.value);
            while ((dllentry = dllentry.next) != null);
        return vector.elements();
    }

    /**
     * {@inheritDoc}
     *
     * Method get.
     */
    public  Object get(Object obj) {
        DLLEntry dllentry = hash.get(obj);
        if (dllentry == null)
            return null;

        int interval = (int) (System.currentTimeMillis() - dllentry.timestamp) / 1000;
        if (interval < cacheTimeOut)

            return dllentry.value;
        else {
            // out date, need to remove and return null;
            remove(obj);
            return null;
        }
    }

    /**
     * <p>dumpKey.</p>
     *
     * @since 1.4
     */
    public void dumpKey() {
        System.out.println("hash=" + toString());
    }

    /**
     * Method isEmpty.
     *
     * @return boolean
     * @since 1.4
     */
    public boolean isEmpty() {
        return head == null;
    }

    /**
     * Method keys.
     *
     * @return Enumeration<Object>
     * @since 1.4
     */
    public synchronized Enumeration<Object> keys() {
        return hash.keys();
    }

    /**
     * {@inheritDoc}
     *
     * Method put.
     */
    public Object put(Object obj, Object obj1) {
        // System.out.println("put into cache for: "+obj+"
        // hash="+obj.hashCode()+" =="+obj.toString ());

        DLLEntry dllentry = new DLLEntry(obj, obj1);
        DLLEntry dllentry1 = hash.put(obj, dllentry);
        if (dllentry1 == null) {
            dllentry.next = head;
            if (head != null)
                head.previous = dllentry;
            head = dllentry;
            if (tail == null)
                tail = dllentry;
            if (++size > maxsize) {
                if (remove(tail.key) == null)
                    removeTail();
            }
            // dumpKey();

            return null;
        } else {
            DLLEntry temp = head;
            while (temp != null) {
                if (temp.key.equals(dllentry.key)) {
                    dllentry.next = temp.next;
                    dllentry.previous = temp.previous;
                    temp.next = null;
                    temp.previous = null;
                    if (dllentry.previous == null)
                        head = dllentry;
                    else
                        dllentry.previous.next = dllentry;
                    if (dllentry.next == null)
                        tail = dllentry;
                    else
                        dllentry.next.previous = dllentry;

                    break;
                } else
                    temp = temp.next;
            }
            if (temp != null) {
            } else {
                hash.remove(obj);

            }

            return dllentry1.value;
        }
    }

    /**
     * <p>removeTail.</p>
     *
     * @since 1.4
     */
    public synchronized void removeTail() {

        DLLEntry temp = tail;
        tail = temp.previous;
        if (tail != null)
            tail.next = null;
        else
            head = null;
        temp.previous = null;
        temp.next = null;
        size--;

    }

    /**
     * {@inheritDoc}
     *
     * Method remove.
     */
    public synchronized Object remove(Object obj) {
        DLLEntry dllentry = hash.remove(obj);
        if (dllentry == null)
            return null;
        size--;
        if (dllentry == tail) {
            tail = dllentry.previous;
            if (tail != null)
                tail.next = null;
        }
        if (dllentry == head) {
            head = dllentry.next;
            if (head != null)
                head.previous = null;
        }
        if (dllentry.previous != null)
            dllentry.previous.next = dllentry.next;
        if (dllentry.next != null)
            dllentry.next.previous = dllentry.previous;
        dllentry.next = null;
        dllentry.previous = null;
        return dllentry.value;
    }

    /**
     * Method size.
     *
     * @return int
     * @since 1.4
     */
    public int size() {
        return size;
    }

    /**
     * <p>clear.</p>
     *
     * @since 1.4
     */
    public synchronized void clear() {
        hash.clear();
        head = tail = null;
        size = 0;
    }

    /**
     * <p>flush.</p>
     *
     * @since 1.4
     */
    public synchronized void flush() {
        // System.out.println("cache been flushed...");
        clear();
    }

    /**
     * {@inheritDoc}
     *
     * Method add.
     */
    public synchronized void add(Object objKey, Object objValue) {
        put(objKey, objValue);
    }

    /**
     * {@inheritDoc}
     *
     * Method assertMaxCacheSize.
     */
    public synchronized void assertMaxCacheSize(int maxSize) {
        assertMaxCacheSize(maxSize, Integer.MAX_VALUE);
    }

    /**
     * {@inheritDoc}
     *
     * Method assertMaxCacheSize.
     */
    public synchronized void assertMaxCacheSize(int maxSize, int timeOut) {
        // System.out.println("assert max cache size got called:
        // "+this.getClass().getName());
        if (this.size() != hash.size()) {
            this.maxsize = maxSize;
            clear();
        } else {
            this.maxsize = maxSize;
            while (this.size > maxsize)
                if (remove(tail.key) == null)
                    removeTail();
        }
    }

    /**
     * Method toString.
     *
     * @return String
     * @since 1.4
     */
    public String toString() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append('[');
        DLLEntry dllentry = head;
        if (dllentry != null)
            do
                stringbuffer.append(dllentry + "; ");
            while ((dllentry = dllentry.next) != null);
        stringbuffer.append(']');
        stringbuffer.append("(" + size + ")");
        return stringbuffer.toString();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
    }
}

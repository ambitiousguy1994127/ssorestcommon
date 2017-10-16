package com.idfconnect.ssorest.common.cache;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.idfconnect.ssorest.common.validator.InetAddressValidator;

import redis.clients.jedis.Jedis;

/**
 * <p>
 * CacheJedisImpl class.
 * </p>
 *
 * @author rsand
 * @since 1.4
 */
public class CacheJedisImpl implements Cache {
    // TODO maxsize is unused
    //protected int     maxsize;
    protected int     cacheTimeOut;

    protected Jedis   replica        = null;                               // for read
    protected Jedis   master         = null;                               // for write
    protected Gson    gson           = null;
    List<String>      endPoints;
    protected String  masterEndPoint = null;
    protected String  masterHost     = null;
    protected int     masterPort     = CacheConstant.REDIS_PORT_DEFAULT;
    protected String  slaveHost      = null;
    protected int     slavePort      = CacheConstant.REDIS_PORT_DEFAULT;
    protected boolean slaveRunning   = false;
    protected boolean masterRunning  = false;
    protected boolean isConnected    = false;

    Logger            logger         = LoggerFactory.getLogger(getClass());

    /**
     * <p>
     * isConnected.
     * </p>
     *
     * @return a boolean.
     * @since 1.4
     */
    public boolean isConnected() {
        return isConnected;
    }

    protected int keyType = CacheConstant.CACHE_KEY_TYPE_HASH_CODE;

    /**
     * <p>
     * Constructor for CacheJedisImpl.
     * </p>
     *
     * @param master
     *            a {@link java.lang.String} object.
     * @param endPoints
     *            a {@link java.util.List} object.
     * @since 1.4
     */
    public CacheJedisImpl(String master, List<String> endPoints) {
        this(500, Integer.MAX_VALUE, master, endPoints);

    }

    /**
     * <p>
     * Constructor for CacheJedisImpl.
     * </p>
     *
     * @param master
     *            a {@link java.lang.String} object.
     * @param endPoints
     *            a {@link java.util.List} object.
     * @param keyType
     *            a int.
     * @since 1.4
     */
    public CacheJedisImpl(String master, List<String> endPoints, int keyType) {
        this(500, Integer.MAX_VALUE, master, endPoints);
        this.keyType = keyType;
    }

    /**
     * Constructor for Cache.
     *
     * @param i
     *            int
     * @param timeOut
     *            int
     * @param master
     *            a {@link java.lang.String} object.
     * @param endPoints
     *            a {@link java.util.List} object.
     * @since 1.4
     */
    public CacheJedisImpl(int i, int timeOut, String master, List<String> endPoints) {
        // TODO maxsize
        //maxsize = i;
        cacheTimeOut = timeOut;
        this.endPoints = endPoints;
        this.masterEndPoint = master;
        isConnected = tryEndPoints();

    }

    /** {@inheritDoc} */
    @Override
    public Object put(Object key, Object entry) {
        // TODO set cache key in the entry?
        String keyStr = toJedisKey(key);
        String valueStr = gson.toJson(entry);
        try {
            master.setex(keyStr, cacheTimeOut, valueStr);
        } catch (Exception e) {
            logger.warn("Caught exception putting object into cache", e);
            if (tryEndPoints()) { // if able to reattempt the connection, do it
                logger.warn("Current endpoint is down: {} ", e.getMessage());
            } else { // if not use the local way
                     // TODO localway here
            }
        }
        return entry;
    }

    /** {@inheritDoc} */
    @Override
    public void dumpKey() {
        logger.trace("hash={}", toString());
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return replica.keys("*") == null || replica.keys("*").size() == 0;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void removeTail() {
        // Iterator<Object> iterator = cache.iterator();
        // while (iterator.hasNext()) {
        // Object object = (Object) iterator.next();
        // }
        //
        // int last = ehcache.getKeys().size()-1;
        // ehcache.remove(ehcache.getKeys().get(last));
    }

    /** {@inheritDoc} */
    @Override
    public synchronized Object remove(Object obj) {
        String key = toJedisKey(obj);
        Object value = get(obj);
        master.del(key);
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public Object get(Object key) {
        String keyStr = toJedisKey(key);
        String value = null;
        try {
            value = replica.get(keyStr);
            logger.trace("Retrieved object: {}={} ", keyStr, value);
        } catch (Exception e) {
            logger.warn("Error retrieving value for {}", keyStr, e);
            return null; // TODO fix this!
            // if (tryEndPoints()){ // if able to reattempt the connection, do it
            // value = cache.get(keyStr); // should never failed this time
            // }
            // else{ // if not use the local way
            // //TODO localway here
            // }
        }
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return replica.keys("*").size();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void clear() {
        master.flushAll();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void flush() {
        master.flushAll();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void add(Object objKey, Object objValue) {
        put(objKey, objValue);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void assertMaxCacheSize(int maxSize) {
        assertMaxCacheSize(maxSize, Integer.MAX_VALUE);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void assertMaxCacheSize(int maxSize, int timeOut) {
        // this.maxsize = maxSize;
        // while (ehcache.getSize() > maxsize)
        // removeTail();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        replica.close();
        master.close();
    }

    private boolean tryEndPoints() {
        // attempt connection to each endpoints, break once connected
        for (String endPoint : endPoints) {
            if (endPoint.indexOf(":") < 0)
                endPoint += ":" + CacheConstant.REDIS_PORT_DEFAULT;
            replica = initEndPoint(endPoint);

            try {
                replica.ping();
                // at this point we are connected, so initialize gson as well.
                gson = new Gson();
                String[] hp = endPoint.split(":");
                slaveHost = toRealIP(hp[0]);
                slavePort = Integer.parseInt(hp[1]);
                slaveRunning = true;
                break;
            } catch (Exception e) {
                logger.warn("Not connected to {}, trying another endpoint: ", endPoint, e.getMessage());
            }
        }

        try {
            if (masterEndPoint.indexOf(":") < 0)
                masterEndPoint += ":" + CacheConstant.REDIS_PORT_DEFAULT;
            String[] hp = masterEndPoint.split(":");
            masterHost = toRealIP(hp[0]);
            masterPort = Integer.parseInt(hp[1]);
            if (slaveRunning && masterHost.equals(slaveHost) && masterPort == slavePort) {
                logger.trace("Master and replica are same, standalone for this host");
                promoteReplica();
                return true;
            }
            master = initEndPoint(masterEndPoint);
            master.ping(); // master down, use local end point only, replication stop, no failover
            masterRunning = true;
            if (!slaveRunning) { // standalone mode if slave is not running
                replica = master;
            } else {
                replica.slaveof(masterHost, masterPort);
            }
            return true;
        } catch (Exception e) {
            if (slaveRunning) {
                logger.warn("Either the master down or there is no master, operating in standalone mode now: {}", e.getMessage());
                promoteReplica();
            } else {
                // nothing is running, we need to use local cache now
            }
        }

        // at this point, connection failed, return false;
        return false;
    }

    private void promoteReplica() {
        replica.slaveofNoOne();
        master = replica;
    }

    private Jedis initEndPoint(String endPoint) {
        String host = null;
        int port = CacheConstant.REDIS_PORT_DEFAULT;
        if (endPoint == null || endPoint.length() == 0)
            return null;
        String[] hp = endPoint.split(":");
        host = hp[0];
        if (hp.length > 1) { // port found
            try {
                port = Integer.parseInt(hp[1]);
            } catch (Exception e) {
                // do nothing, error will make default port
            }
        }
        return new Jedis(host, port);
    }

    private String toRealIP(String host) throws UnknownHostException {
        if (host == null)
            return null;
        if (InetAddressValidator.getInstance().isValid(host)) { // if it is a local host
            if (host.equals("127.0.0.1")) {
                return InetAddress.getLocalHost().getHostAddress();
            }
            return host;
        }

        // the case of a host name
        if (host.equals("localhost")) {
            logger.trace("Local host address detected: {}", InetAddress.getLocalHost().getHostAddress());
            return InetAddress.getLocalHost().getHostAddress();
        }
        return InetAddress.getByName(host).getHostAddress();
    }

    private String toJedisKey(Object key) {
        String str = (String) key.toString();
        String theKey = null;
        switch (keyType) {
            case CacheConstant.CACHE_KEY_TYPE_HASH_CODE:
                theKey = str.hashCode() + "";
                break;
            case CacheConstant.CACHE_KEY_TYPE_SHA1:
                theKey = DigestUtils.sha1Hex(str);
                break;
            case CacheConstant.CACHE_KEY_TYPE_BASE64:
                theKey = Base64.encodeBase64String(str.getBytes(StandardCharsets.UTF_8));
                break;
            case CacheConstant.CACHE_KEY_TYPE_STRING:
                theKey = str;
                break;
            default:
                theKey = str.hashCode() + "";
                break;
        }
        return theKey;

    }

    /**
     * <p>isMasterRunning.</p>
     *
     * @return a boolean.
     * @since 3.0
     */
    public boolean isMasterRunning() {
        return masterRunning;
    }
}

package com.idfconnect.ssorest.common.cache;

/**
 * <p>CacheException class.</p>
 *
 * @author rsand
 * @since 1.4
 */
public class CacheException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 7474021426402320668L;

    /**
     * <p>Constructor for CacheException.</p>
     *
     * @since 1.4
     */
    public CacheException() {
        super();
    }

    /**
     * Constructor for CacheException.
     *
     * @param message String
     * @param cause Throwable
     * @since 1.4
     */
    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for CacheException.
     *
     * @param message String
     * @since 1.4
     */
    public CacheException(String message) {
        super(message);
    }

    /**
     * Constructor for CacheException.
     *
     * @param cause Throwable
     * @since 1.4
     */
    public CacheException(Throwable cause) {
        super(cause);
    }
}

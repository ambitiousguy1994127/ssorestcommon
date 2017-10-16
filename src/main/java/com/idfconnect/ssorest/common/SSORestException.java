package com.idfconnect.ssorest.common;

/**
 * Base exception class for exceptions generated from the common tools of SSORest
 *
 * @author Richard Sand
 * @since 1.4
 */
public class SSORestException extends Exception {
    private static final long serialVersionUID = 5191902382771990105L;

    /**
     * Default constructor
     *
     * @since 1.4
     */
    public SSORestException() {
        super();
    }

    /**
     * Constructor for SSORestException.
     *
     * @param message String
     * @since 1.4
     */
    public SSORestException(String message) { // NOPMD by Administrator on 4/25/16 2:11 PM
        super(message);
    }

    /**
     * Constructor for SSORestException.
     *
     * @param t Throwable
     * @since 1.4
     */
    public SSORestException(Throwable t) {
        super(t);
    }

    /**
     * Constructor for SSORestException.
     *
     * @param message String
     * @param t Throwable
     * @since 1.4
     */
    public SSORestException(String message, Throwable t) {
        super(message, t);
    }
}

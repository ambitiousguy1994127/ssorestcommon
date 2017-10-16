package com.idfconnect.ssorest.common.logging.utils;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.event.Level;

import com.idfconnect.ssorest.common.logging.Loggable;

/**
 * <p>LogHelper class.</p>
 *
 * @author rsand
 * @since 3.0
 */
public final class LogHelper {

    private LogHelper() {
    }

    /**
     * <p>log.</p>
     *
     * @param logger a {@link org.slf4j.Logger} object.
     * @param level a {@link org.slf4j.event.Level} object.
     * @param format a {@link java.lang.String} object.
     * @param args a {@link java.lang.Object} object.
     */
    public static void log(Logger logger, Level level, String format, Object... args) {
        switch (level) {
            case ERROR:
                logger.error(format, args);
                break;
            case WARN:
                logger.warn(format, args);
                break;
            case INFO:
                logger.info(format, args);
                break;
            case DEBUG:
                logger.debug(format, args);
                break;
            case TRACE:
                logger.trace(format, args);
                break;
            default:
                break;
        }
    }
    
    /**
     * <p>log.</p>
     *
     * @param logger a {@link org.slf4j.Logger} object.
     * @param marker a {@link org.slf4j.Marker} object.
     * @param level a {@link org.slf4j.event.Level} object.
     * @param format a {@link java.lang.String} object.
     * @param args a {@link java.lang.Object} object.
     */
    public static void log(Logger logger, Marker marker, Level level, String format, Object... args) {
        switch (level) {
            case ERROR:
                logger.error(format, marker, args);
                break;
            case WARN:
                logger.warn(format, marker, args);
                break;
            case INFO:
                logger.info(format, marker, args);
                break;
            case DEBUG:
                logger.debug(format, marker, args);
                break;
            case TRACE:
                logger.trace(format, marker, args);
                break;
            default:
                break;
        }
    }
    
    /**
     * Log a message for the given Loggable
     *
     * @param loggable a {@link com.idfconnect.ssorest.common.logging.Loggable} object.
     * @param method a {@link java.lang.reflect.Method} object.
     * @param format a {@link java.lang.String} object.
     * @param args a {@link java.lang.Object} object.
     */
    public static void log(Loggable loggable, Method method, String format, Object... args) {
        Logger logger = getLogger(loggable, method);
        log(logger, getLevel(loggable), format, args);
    }
    
    /**
     * Log a message for the given Loggable
     *
     * @param loggable a {@link com.idfconnect.ssorest.common.logging.Loggable} object.
     * @param method a {@link java.lang.reflect.Method} object.
     * @param marker a {@link org.slf4j.Marker} object.
     * @param format a {@link java.lang.String} object.
     * @param args a {@link java.lang.Object} object.
     */
    public static void log(Loggable loggable, Method method, Marker marker, String format, Object... args) {
        Logger logger = getLogger(loggable, method);
        log(logger, marker, getLevel(loggable), format, args);
    }

    /**
     * Get the destination logger for this method
     *
     * @param method
     *            The method
     * @param loggable
     *            The Loggable
     * @return The logger that will be used
     */
    public static Logger getLogger(final Loggable loggable, final Method method) {
        final String name = loggable.name();
        
        if (name.isEmpty()) {
            return LoggerFactory.getLogger(method.getDeclaringClass());
        } else {
            return LoggerFactory.getLogger(name);
        }
    }
    
    /**
     * Returns the log level configured for the loggable
     *
     * @param loggable a {@link com.idfconnect.ssorest.common.logging.Loggable} object.
     * @return the level
     */
    public static Level getLevel(Loggable loggable) {
        switch (loggable.level()) {
            case Loggable.ERROR :
                return Level.ERROR;
            case Loggable.WARN :
                return Level.WARN;
            case Loggable.INFO :
                return Level.INFO;
            case Loggable.DEBUG :
                return Level.DEBUG;
            case Loggable.TRACE :
                return Level.TRACE;
            default :
                return null;
        }
    }
    
    /**
     * Retuns true if the logger is enabled for the Loggable's requested level
     *
     * @param loggable a {@link com.idfconnect.ssorest.common.logging.Loggable} object.
     * @param method a {@link java.lang.reflect.Method} object.
     * @return a boolean.
     */
    public static boolean isEnabled(Loggable loggable, Method method) {
        Logger logger = getLogger(loggable, method);
        switch (loggable.level()) {
            case Loggable.ERROR :
                return logger.isErrorEnabled();
            case Loggable.WARN :
                return logger.isWarnEnabled();
            case Loggable.INFO :
                return logger.isInfoEnabled();
            case Loggable.DEBUG :
                return logger.isDebugEnabled();
            case Loggable.TRACE :
                return logger.isTraceEnabled();
            default :
                return false;
        }
    }
    
    /**
     * Retuns true if the logger is enabled for the Loggable's requested level
     *
     * @param loggable a {@link com.idfconnect.ssorest.common.logging.Loggable} object.
     * @param marker a {@link org.slf4j.Marker} object.
     * @param method a {@link java.lang.reflect.Method} object.
     * @return a boolean.
     */
    public static boolean isEnabled(Loggable loggable, Marker marker, Method method) {
        Logger logger = getLogger(loggable, method);
        switch (loggable.level()) {
            case Loggable.ERROR :
                return logger.isErrorEnabled(marker);
            case Loggable.WARN :
                return logger.isWarnEnabled(marker);
            case Loggable.INFO :
                return logger.isInfoEnabled(marker);
            case Loggable.DEBUG :
                return logger.isDebugEnabled(marker);
            case Loggable.TRACE :
                return logger.isTraceEnabled(marker);
            default :
                return false;
        }
    }
}

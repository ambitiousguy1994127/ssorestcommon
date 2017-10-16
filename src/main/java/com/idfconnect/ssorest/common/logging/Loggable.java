package com.idfconnect.ssorest.common.logging;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Makes a method loggable via SLF4J
 *
 * @author rsand
 * @since 3.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Loggable {
    /**
     * TRACE level of logging.
     */
    int TRACE = 4;

    /**
     * DEBUG level of logging.
     */
    int DEBUG = 3;

    /**
     * INFO level of logging.
     */
    int INFO  = 2;

    /**
     * WARN level of logging.
     */
    int WARN  = 1;

    /**
     * ERROR level of logging.
     */
    int ERROR = 0;

    /**
     * Level of logging.
     */
    int level() default Loggable.INFO;

    /**
     * Shall we trim long texts in order to make log lines more readable?
     */
    boolean trim() default true;

    /**
     * Method entry moment should be reported as well (by default only an exit moment is reported).
     */
    boolean prepend() default false;

    /**
     * List of exception types, which should not be logged if thrown.
     *
     * <p>
     * You can also mark some exception types as "always to be ignored", using {@link Loggable.Quiet} annotation.
     */
    Class<? extends Throwable>[] ignore() default {};

    /**
     * Skip logging of result, replacing it with dots
     */
    boolean skipResult() default false;

    /**
     * Skip logging of arguments, replacing them all with dots
     */
    boolean skipArgs() default false;

    /**
     * Add toString() result to log line.
     */
    boolean logThis() default false;

    /**
     * The precision (number of fractional digits) to be used when displaying the measured execution time.
     */
    int precision() default 2;

    /**
     * The name of the logger to be used. If not specified, defaults to the class name of the annotated class or method.
     */
    String name() default "";

    /**
     * Identifies an exception that is never logged by {@link Loggable} if/when being thrown out of an annotated method.
     *
     * <p>
     * Sometimes exceptions are used as flow control instruments (although this may be considered as a bad practice in most casts). In such situations we don't
     * want to flood log console with error messages. One of the options is to use {@link Loggable#ignore()} attribute to list all exception types that should
     * be ignored. However, this {@link Loggable.Quiet} annotation is more convenient when we want to ignore one specific exception type in all situations.
     *
     * @since 0.8
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Quiet {
    }
}

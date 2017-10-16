package com.idfconnect.ssorest.common.logging;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import com.idfconnect.ssorest.common.logging.utils.LogHelper;
import com.idfconnect.ssorest.common.logging.utils.Mnemos;

/**
 * Logs method calls.
 *
 * <p>
 * It is an AspectJ aspect and you are not supposed to use it directly. It is instantiated by AspectJ runtime framework when your code is annotated with
 * {@link com.idfconnect.ssorest.common.logging.Loggable} annotation.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 */
@Aspect
public final class MethodLogger {
    /**
     * <p>Constructor for MethodLogger.</p>
     *
     * @since 3.0
     */
    public MethodLogger() {
    }

    /**
     * Log methods in a class.
     *
     * <p>
     * Try NOT to change the signature of this method, in order to keep it backward compatible.
     *
     * @param point
     *            Joint point
     * @return The result of call
     * @throws java.lang.Throwable
     *             If something goes wrong inside
     * @since 3.0
     */
    @Around(
            "execution(public * (@com.idfconnect.ssorest.common.logging.Loggable *).*(..))"
            + " || execution(protected * (@com.idfconnect.ssorest.common.logging.Loggable *).*(..))"
            + " && !execution(String *.toString())"
            + " && !execution(int *.hashCode())" 
            + " && !execution(boolean *.canEqual(Object))"
            + " && !execution(boolean *.equals(Object))" 
            + " && !cflow(call(com.idfconnect.ssorest.common.logging.MethodLogger.new()))"
            )
    public Object wrapClass(final ProceedingJoinPoint point) throws Throwable {
        final Method method = MethodSignature.class.cast(point.getSignature()).getMethod();
        Object output;
        if (method.isAnnotationPresent(Loggable.class)) {
            output = point.proceed();
        } else {
            output = this.wrap(point, method, method.getDeclaringClass().getAnnotation(Loggable.class));
        }
        return output;
    }

    /**
     * Log individual methods.
     *
     * <p>
     * Try NOT to change the signature of this method, in order to keep it backward compatible.
     *
     * @param point
     *            Joint point
     * @return The result of call
     * @throws java.lang.Throwable
     *             If something goes wrong inside
     * @since 3.0
     */
    @Around(
            "(execution(* *(..)) "
            + "|| initialization(*.new(..)))"
            + " && @annotation(com.idfconnect.ssorest.common.logging.Loggable)"
            )
    public Object wrapMethod(final ProceedingJoinPoint point) throws Throwable {
        final Method method = MethodSignature.class.cast(point.getSignature()).getMethod();
        return this.wrap(point, method, method.getAnnotation(Loggable.class));
    }

    /**
     * Catch exception and re-call the method.
     * 
     * @param point
     *            Joint point
     * @param method
     *            The method
     * @param loggable
     *            The annotation
     * @return The result of call
     * @throws Throwable
     *             If something goes wrong inside
     */
    private Object wrap(final ProceedingJoinPoint point, final Method method, final Loggable loggable) throws Throwable {
        long start = System.nanoTime();
        try {
            if (loggable.prepend() && LogHelper.isEnabled(loggable, method)) {
                LogHelper.log(
                        loggable, 
                        method, 
                        new StringBuilder(
                                Mnemos.toText(
                                        point,
                                        loggable.trim(),
                                        loggable.skipArgs(),
                                        loggable.logThis())
                                )
                        .append("[entered]").toString(),
                        (Object[]) null
                );
            }
            final Object result = point.proceed();
            final long nano = System.nanoTime() - start;
            if (LogHelper.isEnabled(loggable, method)) {
                LogHelper.log(
                        loggable,
                        method,
                        this.message(point, method, loggable, result, nano),
                        (Object[]) null
                );
            }
            return result;
        } catch (final Throwable ex) {
            if (!MethodLogger.contains(loggable.ignore(), ex) && !ex.getClass().isAnnotationPresent(Loggable.Quiet.class)) {
                final StackTraceElement[] traces = ex.getStackTrace();
                final String origin;
                if (traces.length > 0) {
                    final StackTraceElement trace = traces[0];
                    origin = MethodLogger.stackTraceElementOneLine(trace);
                } else {
                    origin = "(unknown)";
                }
                LogHelper.log(
                        loggable,
                        method,
                        new StringBuilder(
                                Mnemos.toText(
                                        point,
                                        loggable.trim(),
                                        loggable.skipArgs(),
                                        loggable.logThis())
                                )
                            .append(Mnemos.toText(ex))
                            .append(" thrown from ")
                            .append(origin)
                            .append(" [")
                            .append(
                                    String.format(
                                            "%1$." + loggable.precision() + "f",
                                            (System.nanoTime() - start) / 1000.0
                                            )
                                        )
                            .append("ms]")
                            .toString()
                            );
            }
            throw ex;
        }
    }

    /**
     * Prepared message for log
     * 
     * @param point
     *            JointPoint to use.
     * @param method
     *            Method for which to log.
     * @param annotation
     *            Loggable annotation.
     * @param result
     *            Method result.
     * @param nano
     *            Method execution time.
     * @return Log message.
     */
    private String message(final ProceedingJoinPoint point, final Method method, final Loggable annotation, final Object result, final long nano) {
        final StringBuilder sb = new StringBuilder();
        sb.append(Mnemos.toText(point, annotation.trim(), annotation.skipArgs(), annotation.logThis()));
        if (!method.getReturnType().equals(Void.TYPE))
            sb.append("[returned ")
                .append(Mnemos.toText(result, annotation.trim(), annotation.skipResult()))
                .append(']');
        else
            sb.append("[exited]");
        
        sb.append("[")
            .append(
                String.format(
                        "%1$." + annotation.precision() + "f",
                        nano / 1000.0
                        )
                    )
            .append("ms]");

        return sb.toString();
    }

    /**
     * Checks whether array of types contains given type.
     * 
     * @param array
     *            Array of them
     * @param exp
     *            The exception to find
     * @return TRUE if it's there
     */
    private static boolean contains(final Class<? extends Throwable>[] array, final Throwable exp) {
        boolean contains = false;
        for (final Class<? extends Throwable> type : array) {
            if (MethodLogger.instanceOf(exp.getClass(), type)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    /**
     * The type is an instance of another type?
     * 
     * @param child
     *            The child type
     * @param parent
     *            Parent type
     * @return TRUE if child is really a child of a parent
     */
    private static boolean instanceOf(final Class<?> child, final Class<?> parent) {
        boolean instance = child.equals(parent) || (child.getSuperclass() != null && MethodLogger.instanceOf(child.getSuperclass(), parent));
        if (!instance) {
            for (final Class<?> iface : child.getInterfaces()) {
                instance = MethodLogger.instanceOf(iface, parent);
                if (instance) {
                    break;
                }
            }
        }
        return instance;
    }

    /**
     * Textualize a stacktrace.
     * 
     * @param trace
     *            One stacktrace element
     * @return The text
     */
    private static String stackTraceElementOneLine(final StackTraceElement trace) {
        StringBuilder sb = new StringBuilder(trace.getClassName())
                .append('#')
                .append(trace.getMethodName())
                .append('[')
                .append(trace.getLineNumber())
                .append(']');
        return sb.toString();
    }
}

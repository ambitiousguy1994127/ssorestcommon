package com.idfconnect.ssorest.common.http;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>FluentHttpServletResponse interface.</p>
 *
 * @author Administrator
 * @since 1.4
 */
public interface FluentHttpServletResponse extends HttpServletResponse {
    /**
     * <p>attribute.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     * @return a {@link com.idfconnect.ssorest.common.http.FluentHttpServletResponse} object.
     * @since 1.4
     */
    FluentHttpServletResponse attribute(String name, String value);
    /**
     * <p>cookie.</p>
     *
     * @param cookie a {@link javax.servlet.http.Cookie} object.
     * @return a {@link com.idfconnect.ssorest.common.http.FluentHttpServletResponse} object.
     * @since 1.4
     */
    FluentHttpServletResponse cookie(Cookie cookie);
    /**
     * <p>dateHeader.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param date a long.
     * @return a {@link com.idfconnect.ssorest.common.http.FluentHttpServletResponse} object.
     * @since 1.4
     */
    FluentHttpServletResponse dateHeader(String name, long date);
    /**
     * <p>header.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     * @return a {@link com.idfconnect.ssorest.common.http.FluentHttpServletResponse} object.
     * @since 1.4
     */
    FluentHttpServletResponse header(String name, String value);
    /**
     * <p>intHeader.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param value a int.
     * @return a {@link com.idfconnect.ssorest.common.http.FluentHttpServletResponse} object.
     * @since 1.4
     */
    FluentHttpServletResponse intHeader(String name, int value);
    /**
     * <p>flush.</p>
     *
     * @return a {@link com.idfconnect.ssorest.common.http.FluentHttpServletResponse} object.
     * @throws java.io.IOException if any.
     * @since 1.4
     */
    FluentHttpServletResponse flush() throws IOException;
    /**
     * <p>doReset.</p>
     *
     * @return a {@link com.idfconnect.ssorest.common.http.FluentHttpServletResponse} object.
     * @since 1.4
     */
    FluentHttpServletResponse doReset();
    /**
     * <p>doResetBuffer.</p>
     *
     * @return a {@link com.idfconnect.ssorest.common.http.FluentHttpServletResponse} object.
     * @since 1.4
     */
    FluentHttpServletResponse doResetBuffer();
    /**
     * <p>error.</p>
     *
     * @param sc a int.
     * @return a {@link com.idfconnect.ssorest.common.http.FluentHttpServletResponse} object.
     * @throws java.io.IOException if any.
     * @since 1.4
     */
    FluentHttpServletResponse error(int sc) throws IOException;
    /**
     * <p>error.</p>
     *
     * @param sc a int.
     * @param message a {@link java.lang.String} object.
     * @return a {@link com.idfconnect.ssorest.common.http.FluentHttpServletResponse} object.
     * @throws java.io.IOException if any.
     * @since 1.4
     */
    FluentHttpServletResponse error(int sc, String message) throws IOException;
    /**
     * <p>redirect.</p>
     *
     * @param url a {@link java.lang.String} object.
     * @return a {@link com.idfconnect.ssorest.common.http.FluentHttpServletResponse} object.
     * @throws java.io.IOException if any.
     * @since 1.4
     */
    FluentHttpServletResponse redirect(String url) throws IOException;
    /**
     * <p>bufferSize.</p>
     *
     * @param size a int.
     * @return a {@link com.idfconnect.ssorest.common.http.FluentHttpServletResponse} object.
     * @since 1.4
     */
    FluentHttpServletResponse bufferSize(int size);
    /**
     * <p>characterEncoding.</p>
     *
     * @param str a {@link java.lang.String} object.
     * @return a {@link com.idfconnect.ssorest.common.http.FluentHttpServletResponse} object.
     * @since 1.4
     */
    FluentHttpServletResponse characterEncoding(String str);
    /**
     * <p>contentLength.</p>
     *
     * @param length a int.
     * @return a {@link com.idfconnect.ssorest.common.http.FluentHttpServletResponse} object.
     * @since 1.4
     */
    FluentHttpServletResponse contentLength(int length);
    /**
     * <p>contentType.</p>
     *
     * @param type a {@link java.lang.String} object.
     * @return a {@link com.idfconnect.ssorest.common.http.FluentHttpServletResponse} object.
     * @since 1.4
     */
    FluentHttpServletResponse contentType(String type);
    /**
     * <p>locale.</p>
     *
     * @param locale a {@link java.util.Locale} object.
     * @return a {@link com.idfconnect.ssorest.common.http.FluentHttpServletResponse} object.
     * @since 1.4
     */
    FluentHttpServletResponse locale(Locale locale);
    /**
     * <p>status.</p>
     *
     * @param sc a int.
     * @return a {@link com.idfconnect.ssorest.common.http.FluentHttpServletResponse} object.
     * @since 1.4
     */
    FluentHttpServletResponse status(int sc);
}

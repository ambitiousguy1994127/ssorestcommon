package com.idfconnect.ssorest.common.http;

import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.idfconnect.ssorest.common.utils.CaseInsensitiveHashtable;

/**
 * An HttpServletRequestWrapper that allows for manipulation of the headers and cookies
 *
 * @author Richard Sand
 * @since 1.4
 */
public class FilterHttpRequestWrapper extends HttpServletRequestWrapper {
    private CopyOnWriteArrayList<Cookie>           cookies       = new CopyOnWriteArrayList<Cookie>();
    private CaseInsensitiveHashtable<List<String>> headers       = null;
    private String                                 remoteUser    = null;
    private Principal                              userPrincipal = null;

    /**
     * <p>Constructor for FilterHttpRequestWrapper.</p>
     *
     * @param request a {@link javax.servlet.http.HttpServletRequest} object.
     * @since 1.4
     */
    public FilterHttpRequestWrapper(HttpServletRequest request) {
        super(request);
        remoteUser = request.getRemoteUser();
        cookies = new CopyOnWriteArrayList<Cookie>();
        headers = new CaseInsensitiveHashtable<List<String>>();
        if (request.getCookies() != null)
            for (Cookie c : request.getCookies())
                cookies.add(c);

        // Headers
        Enumeration<?> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            Enumeration<?> oldvalues = request.getHeaders(name);
            CopyOnWriteArrayList<String> newvalues = new CopyOnWriteArrayList<String>();
            while (oldvalues.hasMoreElements()) {
                String nextvalue = (String) oldvalues.nextElement();
                newvalues.add(nextvalue);
            }
            headers.put(name, newvalues);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Cookie[] getCookies() {
        return cookies.toArray(new Cookie[] {});
    }

    /**
     * <p>Setter for the field <code>remoteUser</code>.</p>
     *
     * @param remoteUser a {@link java.lang.String} object.
     * @since 1.4
     */
    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    /** {@inheritDoc} */
    @Override
    public long getDateHeader(String name) {
        if ((headers.get(name) == null) || (headers.get(name).size() == 0))
            return -1;
        return Long.parseLong(headers.get(name).get(0));
    }

    /** {@inheritDoc} */
    @Override
    public String getHeader(String name) {
        if (headers.get(name) != null && (headers.get(name).size() > 0))
            return headers.get(name).get(0);
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Enumeration<String> getHeaderNames() {
        return headers.keys();
    }

    /** {@inheritDoc} */
    @Override
    public Enumeration<String> getHeaders(String name) {
        if (headers.get(name) == null)
            return null;
        return Collections.enumeration(headers.get(name));
    }

    /** {@inheritDoc} */
    @Override
    public int getIntHeader(String name) {
        if ((headers.get(name) == null) || (headers.get(name).size() == 0))
            return -1;
        return Integer.parseInt(headers.get(name).get(0));
    }

    /**
     * {@inheritDoc}
     *
     * Method getRemoteUser
     */
    @Override
    public String getRemoteUser() {
        return remoteUser;
    }

    /**
     * Adds the provided cookie to the request. Note that if there is a duplicate cookie (same name, case insensitive) it will be overwritten
     *
     * @param c a {@link javax.servlet.http.Cookie} object.
     * @since 1.4
     */
    public void addCookie(Cookie c) {
        List<String> cookieHeaders = headers.get("Cookie");
        if (cookieHeaders == null)
            cookieHeaders = new CopyOnWriteArrayList<String>();
        for (Cookie cc : cookies) {
            if (cc.getName().equalsIgnoreCase(c.getName()))
                cookies.remove(cc);
        }
        cookies.add(c);

        for (String cookieHeader : cookieHeaders)
            if (c.getName().equalsIgnoreCase(cookieHeader.split("=")[0]))
                cookieHeaders.remove(cookieHeader);
        cookieHeaders.add(FriendlyCookie.toHeaderString(c));
        headers.put("Cookie", cookieHeaders);
    }

    /**
     * Adds the provided cookies to the request. Note that if there are duplicate cookies, they will be overwritten
     *
     * @param cookies a {@link java.util.List} object.
     * @since 1.4
     */
    public void addCookies(List<Cookie> cookies) {
        for (Cookie cc : cookies)
            addCookie(cc);
    }

    /**
     * Adds the provided cookies to the request. Note that if there is a duplicate cookie (same name, domain, and path) it will be overwritten
     *
     * @param cookies a {@link javax.servlet.http.Cookie} object.
     * @since 1.4
     */
    public void addCookies(Cookie... cookies) {
        for (Cookie cc : cookies)
            addCookie(cc);
    }

    /**
     * Removes the named header from the request
     *
     * @param name a {@link java.lang.String} object.
     * @return the old (removed) value for the header, if any
     * @since 1.4
     */
    public List<String> removeHeader(String name) {
        return headers.remove(name);
    }

    /**
     * Adds the provided value to the list of values for the named header
     *
     * @param name a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     * @since 1.4
     */
    public List<String> addHeader(String name, String value) {
        List<String> values = headers.get(name);
        if (values == null)
            values = new CopyOnWriteArrayList<String>();
        values.add(value);
        return headers.put(name, values);
    }

    /**
     * <p>Setter for the field <code>cookies</code>.</p>
     *
     * @param cookies a {@link javax.servlet.http.Cookie} object.
     * @since 1.4
     */
    public void setCookies(Cookie... cookies) {
        this.cookies = new CopyOnWriteArrayList<Cookie>();
        for (Cookie c : cookies)
            addCookie(c);
    }

    /**
     * <p>Setter for the field <code>cookies</code>.</p>
     *
     * @param cookies a {@link java.util.List} object.
     * @since 1.4
     */
    public void setCookies(List<Cookie> cookies) {
        this.cookies = new CopyOnWriteArrayList<Cookie>();
        for (Cookie c : cookies)
            addCookie(c);
    }

    /**
     * <p>Setter for the field <code>headers</code>.</p>
     *
     * @param headers a {@link java.util.Hashtable} object.
     * @since 1.4
     */
    public void setHeaders(Hashtable<String, List<String>> headers) {
        this.headers = new CaseInsensitiveHashtable<List<String>>();
        for (Entry<String, List<String>> entry : headers.entrySet()) {
            for (String value : entry.getValue())
                addHeader(entry.getKey(), value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Principal getUserPrincipal() {
        if (userPrincipal != null)
            return userPrincipal;
        return super.getUserPrincipal();
    }
    
    /**
     * <p>Setter for the field <code>userPrincipal</code>.</p>
     *
     * @param userPrincipal a {@link java.security.Principal} object.
     * @since 1.4
     */
    public void setUserPrincipal(Principal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }
}

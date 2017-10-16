package com.idfconnect.ssorest.common.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.LoggerFactory;

import com.idfconnect.ssorest.common.utils.CaseInsensitiveHashtable;

/**
 * A simple HttpServletRequestWrapper that allows the Web Agent to update the request headers, cookies, and remote user. It also takes a zero-argument
 * constructor and is useful for serializing & deserializing request objects
 *
 * @author Richard Sand
 * @since 1.4
 */
public class WebAgentRequestWrapper implements HttpServletRequest, Serializable {
    /** Request attribute name holding requested content */
    public static final String                          CONTENT_REQUEST_ATTRIBUTE           = "content";

    /** Request attribute name holding requested content */
    public static final String                          CONTENT_TIMESTAMP_REQUEST_ATTRIBUTE = "contentTimestamp";
    
    /** Static boolean to tell the setURL method to populate the parameters from the query string */
    public static final boolean PARSE_QUERY_STRING = true;
    
    private static final long                           serialVersionUID                    = -2405609284304716975L;
    private CaseInsensitiveHashtable<List<String>>      updatedHeaders                      = null;
    private String                                      remoteUser                          = null;
    private transient HttpServletRequest                wrappedRequest                      = null;
    private transient ServletContext                    servletContext                      = null;
    private CopyOnWriteArrayList<Cookie>                cookies                             = new CopyOnWriteArrayList<Cookie>();
    private Hashtable<String, Object>                   attributes                          = null;
    private Hashtable<String, List<String>>             parameters                          = null;
    private CopyOnWriteArrayList<Locale>                locales                             = null;
    private String                                      contentType                         = "text/html";
    private int                                         contentLength                       = 0;
    private String                                      characterEncoding                   = StandardCharsets.UTF_8.name();
    private String                                      localAddr                           = "127.0.0.1";
    private String                                      remoteAddr                          = "127.0.0.1";
    private String                                      remoteHost                          = "localhost";
    private int                                         localPort                           = 80;
    private int                                         serverPort                          = 80;
    private int                                         remotePort                          = 23456;
    private String                                      localName                           = "localhost";
    private String                                      serverName                          = "localhost";
    private String                                      protocol                            = "HTTP/1.1";
    private String                                      scheme                              = "http";
    private String                                      contextPath                         = "";
    private String                                      authType                            = null;
    private String                                      method                              = "GET";
    private transient WebAgentRequestWrapperInputStream is                                  = null;
    private String                                      requestURI                          = null;
    private String                                      servletPath                         = "";
    private String                                      queryString                         = null;
    private byte[]                                      body                                = null;

    /**
     */
    private static class WebAgentRequestWrapperInputStream extends ServletInputStream {
        InputStream is;

        /**
         * Constructor for WebAgentRequestWrapperInputStream.
         * 
         * @param content
         *            byte[]
         * @throws UnsupportedEncodingException
         */
        public WebAgentRequestWrapperInputStream(byte[] content) throws UnsupportedEncodingException {
            super();
            is = new ByteArrayInputStream(content);
        }

        /**
         * Method read.
         * 
         * @return int
         * @throws IOException
         */
        @Override
        public int read() throws IOException {
            return is.read();
        }

        /**
         * Method close.
         * 
         * @see java.io.Closeable#close()
         */
        @Override
        public void close() {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        /**
         * Method available.
         * 
         * @return int
         * @throws IOException
         */
        @Override
        public int available() throws IOException {
            return is.available();
        }

        /**
         * Method mark.
         * 
         * @param readlimit
         *            int
         */
        @Override
        public synchronized void mark(int readlimit) {
            is.mark(readlimit);
        }

        /**
         * Method markSupported.
         * 
         * @return boolean
         */
        @Override
        public boolean markSupported() {
            return is.markSupported();
        }

        /**
         * Method read.
         * 
         * @param b
         *            byte[]
         * @param off
         *            int
         * @param len
         *            int
         * @return int
         * @throws IOException
         */
        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return is.read(b, off, len);
        }

        /**
         * Method read.
         * 
         * @param b
         *            byte[]
         * @return int
         * @throws IOException
         */
        @Override
        public int read(byte[] b) throws IOException {
            return is.read(b);
        }

        /**
         * Method reset.
         * 
         * @throws IOException
         */
        @Override
        public synchronized void reset() throws IOException {
            is.reset();
        }

        /**
         * Method skip.
         * 
         * @param n
         *            long
         * @return long
         * @throws IOException
         */
        @Override
        public long skip(long n) throws IOException {
            return is.skip(n);
        }
    }

    /**
     * Method setRequestBasicAuth.
     *
     * @param req
     *            WebAgentRequestWrapper
     * @param name
     *            String
     * @param password
     *            String
     * @throws java.io.UnsupportedEncodingException
     *             if any.
     * @since 1.4.2
     */
    public static final void setRequestBasicAuth(WebAgentRequestWrapper req, String name, String password) throws UnsupportedEncodingException {
        String authString = name + ":" + password;
        byte[] authEncBytes = Base64.encodeBase64(authString.getBytes(req.getCharacterEncoding()));
        String authStringEnc = new String(authEncBytes, req.getCharacterEncoding());
        req.setAuthType(BASIC_AUTH);
        req.addHeader("Authorization", "Basic " + authStringEnc);
    }

    /**
     * Simple utility method that adds the query string to the uri if it is non-null/non-blank
     *
     * @param uri
     *            a {@link java.lang.String} object.
     * @param queryString
     *            a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     * @since 3.0.1
     */
    public static final String getRequestUriWithQueryString(String uri, String queryString) {
        if (queryString == null || queryString.length() == 0)
            return uri;
        return uri + '?' + queryString;
    }

    /**
     * <p>
     * setRequestPluginIdentity.
     * </p>
     *
     * @param req
     *            a {@link com.idfconnect.ssorest.common.http.WebAgentRequestWrapper} object.
     * @param pluginID
     *            a {@link java.lang.String} object.
     * @param randomText
     *            a {@link java.lang.String} object.
     * @param randomTextSigned
     *            a {@link java.lang.String} object.
     * @throws java.io.UnsupportedEncodingException
     *             if any.
     * @since 1.4.2
     */
    public static final void setRequestPluginIdentity(WebAgentRequestWrapper req, String pluginID, String randomText, String randomTextSigned) throws UnsupportedEncodingException {
        req.setAttribute("pluginID", pluginID);
        if (randomText != null) {
            req.setAttribute("randomText", randomText);
            req.setAttribute("randomTextSigned", URLEncoder.encode(randomTextSigned, StandardCharsets.UTF_8.name())); // remember to URL encode
        }
    }

    /**
     * <p>
     * setRequestPluginIdentity.
     * </p>
     *
     * @param req
     *            a {@link com.idfconnect.ssorest.common.http.WebAgentRequestWrapper} object.
     * @param pluginID
     *            a {@link java.lang.String} object.
     * @param gatewayToken
     *            a {@link java.lang.String} object.
     * @since 1.4.2
     */
    public static final void setRequestPluginIdentity(WebAgentRequestWrapper req, String pluginID, String gatewayToken) {
        if (pluginID != null)
            req.setAttribute("pluginID", pluginID);
        if (gatewayToken != null)
            req.setAttribute("gatewayToken", gatewayToken);
    }

    /**
     * <p>
     * Constructor for WebAgentRequestWrapper.
     * </p>
     *
     * @since 1.4.2
     */
    public WebAgentRequestWrapper() {
        attributes = new Hashtable<String, Object>();
        parameters = new Hashtable<String, List<String>>();
        locales = new CopyOnWriteArrayList<Locale>();
        cookies = new CopyOnWriteArrayList<Cookie>();
        updatedHeaders = new CaseInsensitiveHashtable<List<String>>();
    }

    /**
     * Constructor for WebAgentRequestWrapper.
     *
     * @param url
     *            String
     * @throws java.net.MalformedURLException
     *             if any.
     * @throws java.net.UnknownHostException
     *             if any.
     * @since 1.4.2
     */
    public WebAgentRequestWrapper(String url) throws MalformedURLException, UnknownHostException {
        this(new URL(url));
    }

    /**
     * Constructor for WebAgentRequestWrapper.
     *
     * @param url
     *            String
     * @param parseQueryString
     *            set to <code>true</code> to populate the parameters from the query string
     * @throws java.net.MalformedURLException
     *             if any.
     * @throws java.net.UnknownHostException
     *             if any.
     * @since 1.4.2
     */
    public WebAgentRequestWrapper(String url, boolean parseQueryString) throws MalformedURLException, UnknownHostException {
        this(new URL(url), parseQueryString);
    }

    /**
     * Constructor for WebAgentRequestWrapper.
     *
     * @param url
     *            URL
     * @throws java.net.UnknownHostException
     *             if any.
     * @since 1.4.2
     */
    public WebAgentRequestWrapper(URL url) throws UnknownHostException {
        this();
        setURL(url);
    }

    /**
     * Constructor for WebAgentRequestWrapper.
     *
     * @param url
     *            URL
     * @param parseQueryString
     *            set to <code>true</code> to populate the parameters from the query string
     * @throws java.net.UnknownHostException
     *             if any.
     * @since 1.5.6
     */
    public WebAgentRequestWrapper(URL url, boolean parseQueryString) throws UnknownHostException {
        this();
        setURL(url, parseQueryString);
    }

    /**
     * Constructor for WebAgentRequestWrapper.
     *
     * @param request
     *            HttpServletRequest
     * @since 1.4.2
     */
    public WebAgentRequestWrapper(HttpServletRequest request) {
        super();
        setWrappedRequest(request);
    }

    /**
     * Loads the headers and cookies from the provided request object into the wrapper
     *
     * @param req
     *            a {@link javax.servlet.http.HttpServletRequest} object.
     * @since 1.4.2
     */
    protected void loadHeadersAndCookies(HttpServletRequest req) {
        // Headers
        updatedHeaders = new CaseInsensitiveHashtable<List<String>>();
        Enumeration<?> names = req.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            Enumeration<String> values = req.getHeaders(name);
            CopyOnWriteArrayList<String> v = new CopyOnWriteArrayList<String>();
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                v.add(value);
            }
            updatedHeaders.put(name, v);
        }

        // Cookies
        cookies = new CopyOnWriteArrayList<Cookie>();
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies())
                cookies.add(c);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Method getHeader.
     */
    public String getHeader(String name) {
        List<String> values = updatedHeaders.get(name);
        if (values == null)
            return null;
        return values.get(0);
    }

    /**
     * Method getHeaderNames.
     *
     * @return Enumeration<String>
     * @since 1.4.2
     */
    public Enumeration<String> getHeaderNames() {
        return updatedHeaders.keys();
    }

    /**
     * {@inheritDoc}
     *
     * Method getHeaders.
     */
    public Enumeration<String> getHeaders(String name) {
        List<String> values = updatedHeaders.get(name);
        if (values == null)
            return null;
        return Collections.enumeration(values);
    }

    /**
     * Method getRemoteUser.
     *
     * @return String
     * @since 1.4.2
     */
    public String getRemoteUser() {
        if (remoteUser != null)
            return remoteUser;
        if (wrappedRequest != null)
            return wrappedRequest.getRemoteUser();
        return null;
    }

    /**
     * Sets the remote user variable for the request. Note that if method is never called, the wrapper will delegate calls to getRemoteUser to the wrapped
     * request.
     *
     * @param remoteUser
     *            the remoteUser to set
     * @since 1.4.2
     */
    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    /**
     * Returns true if the named header is contained within the request
     *
     * @param name
     *            a {@link java.lang.String} object.
     * @return boolean
     * @since 1.4.2
     */
    public boolean containsHeader(String name) {
        return updatedHeaders.containsKey(name);
    }

    /**
     * Adds the provided value to the list of values for the named header
     *
     * @param name
     *            a {@link java.lang.String} object.
     * @param value
     *            a {@link java.lang.String} object.
     * @return List<String>
     * @since 1.4.2
     */
    public List<String> addHeader(String name, String value) {
        List<String> values = updatedHeaders.get(name);
        if (values == null)
            values = new CopyOnWriteArrayList<String>();
        values.add(value);
        return updatedHeaders.put(name, values);
    }

    /**
     * Adds the provided value to the list of values for the named parameter
     *
     * @param name
     *            a {@link java.lang.String} object.
     * @param value
     *            a {@link java.lang.String} object.
     * @return List<String>
     * @since 1.4.2
     */
    public List<String> addParameter(String name, String value) {
        List<String> values = parameters.get(name);
        if (values == null)
            values = new CopyOnWriteArrayList<String>();
        values.add(value);
        return parameters.put(name, values);
    }

    /**
     * Adds the provided cookie to the request. Note that if there is a duplicate cookie (same name, case insensitive) it will be overwritten
     *
     * @param c
     *            a {@link javax.servlet.http.Cookie} object.
     * @since 1.4.2
     */
    public void addCookie(Cookie c) {
        List<String> cookieHeaders = updatedHeaders.get("COOKIE");
        if (cookieHeaders == null)
            cookieHeaders = new CopyOnWriteArrayList<String>();
        if (cookies != null) {
            Iterator<Cookie> iterator = cookies.iterator();
            while (iterator.hasNext()) {
                Cookie cc = iterator.next();
                String cookieName = cc.getName();
                if (cookieName == null) {
                    cookies.remove(cc); /// remove this in order not to cause NPE!
                    continue;
                }

                if (cookieName.equalsIgnoreCase(c.getName()))
                    cookies.remove(cc);
            }

            List<String> toRemove = null;
            for (String cookieHeader : cookieHeaders) {
                if (c.getName().equalsIgnoreCase(cookieHeader.split("=")[0])) {
                    if (toRemove == null)
                        toRemove = new ArrayList<String>();
                    toRemove.add(cookieHeader);
                }
            }
            if (toRemove != null)
                cookieHeaders.removeAll(toRemove);

        } else
            cookies = new CopyOnWriteArrayList<Cookie>();

        cookies.add(c);
        cookieHeaders.add(FriendlyCookie.toHeaderString(c));
        updatedHeaders.put("COOKIE", cookieHeaders);
    }

    /**
     * Adds the provided cookies to the request. Note that if there are duplicate cookies, they will be overwritten
     *
     * @param cookies
     *            List<Cookie>
     * @since 1.4.2
     */
    public void addCookies(Collection<Cookie> cookies) {
        for (Cookie cc : cookies)
            addCookie(cc);
    }

    /**
     * Adds the provided cookies to the request. Note that if there is a duplicate cookie (same name, domain, and path) it will be overwritten
     *
     * @param cookieArray
     *            Cookie[]
     * @since 1.4.2
     */
    public void addCookies(Cookie[] cookieArray) {
        for (Cookie cc : cookieArray)
            addCookie(cc);
    }

    /**
     * Removes the named header from the request
     *
     * @param name
     *            a {@link java.lang.String} object.
     * @return the old (removed) value for the header, if any
     * @since 1.4.2
     */
    public List<String> removeHeader(String name) {
        return updatedHeaders.remove(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WebAgentRequestWrapper [");
        if (updatedHeaders != null) {
            builder.append("headers=");
            builder.append(updatedHeaders);
            builder.append(", ");
        }
        if (getRemoteUser() != null) {
            builder.append("getRemoteUser()=");
            builder.append(getRemoteUser());
            builder.append(", ");
        }
        if (getAuthType() != null) {
            builder.append("getAuthType()=");
            builder.append(getAuthType());
            builder.append(", ");
        }
        if (getCookies() != null) {
            builder.append("getCookies()=");
            builder.append(Arrays.toString(getCookies()));
            builder.append(", ");
        }
        if (getMethod() != null) {
            builder.append("getMethod()=");
            builder.append(getMethod());
            builder.append(", ");
        }
        if (getPathInfo() != null) {
            builder.append("getPathInfo()=");
            builder.append(getPathInfo());
            builder.append(", ");
        }
        if (getPathTranslated() != null) {
            builder.append("getPathTranslated()=");
            builder.append(getPathTranslated());
            builder.append(", ");
        }
        if (getContextPath() != null) {
            builder.append("getContextPath()=");
            builder.append(getContextPath());
            builder.append(", ");
        }
        if (getQueryString() != null) {
            builder.append("getQueryString()=");
            builder.append(getQueryString());
            builder.append(", ");
        }
        if (getUserPrincipal() != null) {
            builder.append("getUserPrincipal()=");
            builder.append(getUserPrincipal());
            builder.append(", ");
        }
        if (getRequestedSessionId() != null) {
            builder.append("getRequestedSessionId()=");
            builder.append(getRequestedSessionId());
            builder.append(", ");
        }
        if (getRequestURI() != null) {
            builder.append("getRequestURI()=");
            builder.append(getRequestURI());
            builder.append(", ");
        }
        if (getRequestURL() != null) {
            builder.append("getRequestURL()=");
            builder.append(getRequestURL());
            builder.append(", ");
        }
        if (getServletPath() != null) {
            builder.append("getServletPath()=");
            builder.append(getServletPath());
            builder.append(", ");
        }
        if (getAttributeNames() != null) {
            builder.append("getAttributesNames()=[");
            Enumeration<String> en = getAttributeNames();
            while (en.hasMoreElements()) {
                builder.append(en.nextElement());
                if (en.hasMoreElements())
                    builder.append(',');
            }
            builder.append("], ");
        }
        if (getCharacterEncoding() != null) {
            builder.append("getCharacterEncoding()=");
            builder.append(getCharacterEncoding());
            builder.append(", ");
        }
        builder.append("getContentLength()=");
        builder.append(getContentLength());
        builder.append(", ");
        if (getContentType() != null) {
            builder.append("getContentType()=");
            builder.append(getContentType());
            builder.append(", ");
        }
        if (getParameterMap() != null) {
            builder.append("getParameterMap()=");
            builder.append(getParameterString());
            builder.append(", ");
        }
        if (getParameterNames() != null) {
            builder.append("getParameterNames()=");
            builder.append(getParameterNames());
            builder.append(", ");
        }
        if (getProtocol() != null) {
            builder.append("getProtocol()=");
            builder.append(getProtocol());
            builder.append(", ");
        }
        if (getScheme() != null) {
            builder.append("getScheme()=");
            builder.append(getScheme());
            builder.append(", ");
        }
        if (getServerName() != null) {
            builder.append("getServerName()=");
            builder.append(getServerName());
            builder.append(", ");
        }
        builder.append("getServerPort()=");
        builder.append(getServerPort());
        builder.append(", ");
        if (getRemoteAddr() != null) {
            builder.append("getRemoteAddr()=");
            builder.append(getRemoteAddr());
            builder.append(", ");
        }
        if (getRemoteHost() != null) {
            builder.append("getRemoteHost()=");
            builder.append(getRemoteHost());
            builder.append(", ");
        }
        if (getLocale() != null) {
            builder.append("getLocale()=");
            builder.append(getLocale());
            builder.append(", ");
        }
        if (getLocales() != null) {
            builder.append("getLocales()=");
            builder.append(getLocales());
            builder.append(", ");
        }
        builder.append("isSecure()=");
        builder.append(isSecure());
        builder.append(", isRequestedSessionIdValid()=");
        builder.append(isRequestedSessionIdValid());
        builder.append(", isRequestedSessionIdFromURL()=");
        builder.append(isRequestedSessionIdFromURL());
        builder.append(", isRequestedSessionIdFromCookie()=");
        builder.append(isRequestedSessionIdFromCookie());
        builder.append(", getRemotePort()=");
        builder.append(getRemotePort());
        builder.append(", ");
        if (getLocalName() != null) {
            builder.append("getLocalName()=");
            builder.append(getLocalName());
            builder.append(", ");
        }
        if (getLocalAddr() != null) {
            builder.append("getLocalAddr()=");
            builder.append(getLocalAddr());
            builder.append(", ");
        }
        builder.append("getLocalPort()=");
        builder.append(getLocalPort());
        builder.append(']');
        return builder.toString();
    }

    /**
     * <p>
     * Getter for the field <code>updatedHeaders</code>.
     * </p>
     *
     * @return the updatedHeaders
     * @since 1.4.2
     */
    public Hashtable<String, List<String>> getUpdatedHeaders() {
        return updatedHeaders;
    }

    /**
     * {@inheritDoc}
     *
     * Method getAttribute.
     */
    public Object getAttribute(String name) {
        if (wrappedRequest != null)
            return wrappedRequest.getAttribute(name);
        return attributes.get(name);
    }

    /**
     * Method getAttributeNames.
     *
     * @return Enumeration<String>
     * @since 1.4.2
     */
    public Enumeration<String> getAttributeNames() {
        if (wrappedRequest != null)
            return wrappedRequest.getAttributeNames();
        return attributes.keys();
    }

    /**
     * Method getCharacterEncoding.
     *
     * @return String
     * @since 1.4.2
     */
    public String getCharacterEncoding() {
        if (wrappedRequest != null)
            return wrappedRequest.getCharacterEncoding();
        return characterEncoding;
    }

    /**
     * {@inheritDoc}
     *
     * Method setCharacterEncoding.
     */
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        if (wrappedRequest != null)
            wrappedRequest.setCharacterEncoding(env);
        this.characterEncoding = env;

    }

    /**
     * Method getContentLength.
     *
     * @return int
     * @since 1.4.2
     */
    public int getContentLength() {
        if (wrappedRequest != null)
            return wrappedRequest.getContentLength();
        if (body != null)
            return body.length;
        return contentLength;
    }

    /**
     * Method getContentType.
     *
     * @return String
     * @since 1.4.2
     */
    public String getContentType() {
        if (wrappedRequest != null)
            return wrappedRequest.getContentType();
        return contentType;
    }

    /**
     * Method getInputStream.
     *
     * @return ServletInputStream
     * @throws java.io.IOException
     *             if any.
     * @since 1.4.2
     */
    public ServletInputStream getInputStream() throws IOException {
        if (wrappedRequest != null)
            return wrappedRequest.getInputStream();
        if (is == null && body != null)
            is = new WebAgentRequestWrapperInputStream(body);
        return is;
    }

    /**
     * {@inheritDoc}
     *
     * Method getParameter.
     */
    public String getParameter(String name) {
        if (wrappedRequest != null)
            return wrappedRequest.getParameter(name);
        List<String> values = parameters.get(name);
        if (values != null && values.size() > 0)
            return values.get(0);
        return null;
    }

    /**
     * Method getParameterNames.
     *
     * @return Enumeration<String>
     * @since 1.4.2
     */
    public Enumeration<String> getParameterNames() {
        if (wrappedRequest != null)
            return wrappedRequest.getParameterNames();
        return parameters.keys();
    }

    /**
     * {@inheritDoc}
     *
     * Method getParameterValues.
     */
    public String[] getParameterValues(String name) {
        if (wrappedRequest != null)
            return wrappedRequest.getParameterValues(name);
        List<String> values = parameters.get(name);
        if (values == null)
            return null;
        return values.toArray(new String[] {});
    }

    /**
     * <p>
     * getParameterString.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     * @since 1.4.2
     */
    private String getParameterString() {
        Map<String, String[]> map = getParameterMap();
        Set<Entry<String, String[]>> entries = map.entrySet();
        StringBuffer sb = new StringBuffer();
        for (Entry<String, String[]> entry : entries) {
            String name = entry.getKey();
            if (("password").equalsIgnoreCase(name)) // not show the password
                continue;
            String[] values = entry.getValue();
            sb.append(name + "=[");
            for (String value : values) {
                sb.append(value);
                sb.append(",");
            }
            sb.append("],");
            if (("username").equalsIgnoreCase(name)) {
                try {
                    byte[] bytes = values[0].getBytes(getCharacterEncoding());
                    StringBuffer sbb = new StringBuffer();
                    for (byte b : bytes) {
                        sbb.append(b + ",");
                    }
                    sb.append("namebyte=[" + sbb.toString() + "],");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();

    }

    /**
     * Method getParameterMap.
     *
     * @return a {@link java.util.Map} object.
     * @since 1.4.2
     */
    public Map<String, String[]> getParameterMap() {
        if (wrappedRequest != null)
            return wrappedRequest.getParameterMap();
        Map<String, String[]> retval = new HashMap<String, String[]>();
        for (String name : parameters.keySet())
            retval.put(name, getParameterValues(name));
        return retval;
    }

    /**
     * Method getProtocol.
     *
     * @return String
     * @since 1.4.2
     */
    public String getProtocol() {
        if (wrappedRequest != null)
            return wrappedRequest.getProtocol();
        return protocol;
    }

    /**
     * Method getScheme.
     *
     * @return String
     * @since 1.4.2
     */
    public String getScheme() {
        if (wrappedRequest != null)
            return wrappedRequest.getScheme();
        return scheme;
    }

    /**
     * Method getServerName.
     *
     * @return String
     * @since 1.4.2
     */
    public String getServerName() {
        if (wrappedRequest != null)
            return wrappedRequest.getServerName();
        return serverName;
    }

    /**
     * Method getServerPort.
     *
     * @return int
     * @since 1.4.2
     */
    public int getServerPort() {
        if (wrappedRequest != null)
            return wrappedRequest.getServerPort();
        return serverPort;
    }

    /**
     * Method getReader.
     *
     * @return BufferedReader
     * @throws java.io.IOException
     *             if any.
     * @since 1.4.2
     */
    public BufferedReader getReader() throws IOException {
        if (wrappedRequest != null)
            return wrappedRequest.getReader();
        if (is != null) {
            return new BufferedReader(new InputStreamReader(is, getCharacterEncoding()));
        }
        return null;
    }

    /**
     * Method getRemoteAddr.
     *
     * @return String
     * @since 1.4.2
     */
    public String getRemoteAddr() {
        if (wrappedRequest != null)
            return wrappedRequest.getRemoteAddr();
        return remoteAddr;
    }

    /**
     * Method getRemoteHost.
     *
     * @return String
     * @since 1.4.2
     */
    public String getRemoteHost() {
        if (wrappedRequest != null)
            return wrappedRequest.getRemoteHost();
        return remoteHost;
    }

    /**
     * {@inheritDoc}
     *
     * Method setAttribute.
     */
    public void setAttribute(String name, Object o) {
        if (wrappedRequest != null)
            wrappedRequest.setAttribute(name, o);
        else {
            if (o == null)
                removeAttribute(name);
            else
                attributes.put(name, o);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Method removeAttribute.
     */
    public void removeAttribute(String name) {
        if (wrappedRequest != null)
            wrappedRequest.removeAttribute(name);
        else
            attributes.remove(name);
    }

    /**
     * Method getLocale.
     *
     * @return Locale
     * @since 1.4.2
     */
    public Locale getLocale() {
        if (wrappedRequest != null)
            return wrappedRequest.getLocale();
        if (locales == null)
            return null;
        return locales.get(0);
    }

    /**
     * Method getLocales.
     *
     * @return Enumeration<Locale>
     * @since 1.4.2
     */
    public Enumeration<Locale> getLocales() {
        if (wrappedRequest != null)
            return wrappedRequest.getLocales();
        if (locales == null)
            return null;
        return Collections.enumeration(locales);
    }

    /**
     * Method isSecure.
     *
     * @return boolean
     * @since 1.4.2
     */
    public boolean isSecure() {
        if (wrappedRequest != null)
            return wrappedRequest.isSecure();
        if (scheme == null)
            return false;
        return scheme.endsWith("s");
    }

    /**
     * {@inheritDoc}
     *
     * Method getRequestDispatcher.
     */
    public RequestDispatcher getRequestDispatcher(String path) {
        if (wrappedRequest != null)
            return wrappedRequest.getRequestDispatcher(path);
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * Method getRealPath.
     */
    @Deprecated
    public String getRealPath(String path) {
        if (wrappedRequest != null)
            return wrappedRequest.getRealPath(path);
        return null;
    }

    /**
     * Method getRemotePort.
     *
     * @return int
     * @since 1.4.2
     */
    public int getRemotePort() {
        if (wrappedRequest != null)
            return wrappedRequest.getRemotePort();
        return remotePort;
    }

    /**
     * Method getLocalName.
     *
     * @return String
     * @since 1.4.2
     */
    public String getLocalName() {
        if (wrappedRequest != null)
            return wrappedRequest.getLocalName();
        return localName;
    }

    /**
     * Method getLocalAddr.
     *
     * @return String
     * @since 1.4.2
     */
    public String getLocalAddr() {
        if (wrappedRequest != null)
            return wrappedRequest.getLocalAddr();
        return localAddr;
    }

    /**
     * Method getLocalPort.
     *
     * @return int
     * @since 1.4.2
     */
    public int getLocalPort() {
        if (wrappedRequest != null)
            return wrappedRequest.getLocalPort();
        return localPort;
    }

    /**
     * Method getAuthType.
     *
     * @return String
     * @since 1.4.2
     */
    public String getAuthType() {
        if (wrappedRequest != null)
            return wrappedRequest.getAuthType();
        return authType;
    }

    /**
     * Method getCookies.
     *
     * @return an array of {@link javax.servlet.http.Cookie} objects.
     * @since 1.4.2
     */
    public Cookie[] getCookies() {
        return cookies.toArray(new Cookie[] {});
    }

    /**
     * {@inheritDoc}
     *
     * Method getDateHeader.
     */
    public long getDateHeader(String name) {
        // if (wrappedRequest != null)
        // return wrappedRequest.getDateHeader(name);
        List<String> v = updatedHeaders.get(name);
        if ((v == null) || (v.size() == 0))
            return -1;
        return Long.parseLong(v.get(0));
    }

    /**
     * {@inheritDoc}
     *
     * Method getIntHeader.
     */
    public int getIntHeader(String name) {
        // if (wrappedRequest != null)
        // return wrappedRequest.getIntHeader(name);
        List<String> v = updatedHeaders.get(name);
        if ((v == null) || (v.size() == 0))
            return -1;
        return Integer.parseInt(v.get(0));
    }

    /**
     * Method getMethod.
     *
     * @return String
     * @since 1.4.2
     */
    public String getMethod() {
        if (wrappedRequest != null)
            return wrappedRequest.getMethod();
        return method;
    }

    /**
     * Method getPathInfo.
     *
     * @return String
     * @since 1.4.2
     */
    public String getPathInfo() {
        if (wrappedRequest != null)
            return wrappedRequest.getPathInfo();
        return null;
    }

    /**
     * Method getPathTranslated.
     *
     * @return String
     * @since 1.4.2
     */
    public String getPathTranslated() {
        if (wrappedRequest != null)
            return wrappedRequest.getPathTranslated();
        return null;
    }

    /**
     * Method getContextPath.
     *
     * @return String
     * @since 1.4.2
     */
    public String getContextPath() {
        if (wrappedRequest != null)
            return wrappedRequest.getContextPath();
        return contextPath;
    }

    /**
     * Method getQueryString.
     *
     * @return String
     * @since 1.4.2
     */
    public String getQueryString() {
        if (wrappedRequest != null)
            return wrappedRequest.getQueryString();
        return queryString;
    }

    /**
     * {@inheritDoc}
     *
     * Method isUserInRole.
     */
    public boolean isUserInRole(String role) {
        if (wrappedRequest != null)
            return wrappedRequest.isUserInRole(role);
        return false;
    }

    /**
     * Method getUserPrincipal.
     *
     * @return Principal
     * @since 1.4.2
     */
    public Principal getUserPrincipal() {
        if (wrappedRequest != null)
            return wrappedRequest.getUserPrincipal();
        return null;
    }

    /**
     * Method getRequestedSessionId.
     *
     * @return String
     * @since 1.4.2
     */
    public String getRequestedSessionId() {
        if (wrappedRequest != null)
            return wrappedRequest.getRequestedSessionId();
        return null;
    }

    /**
     * Method getRequestURI.
     *
     * @return String
     * @since 1.4.2
     */
    public String getRequestURI() {
        if (wrappedRequest != null)
            return wrappedRequest.getRequestURI();
        return requestURI;
    }

    /**
     * Method getRequestURL.
     *
     * @return StringBuffer
     * @since 1.4.2
     */
    public StringBuffer getRequestURL() {
        if (wrappedRequest != null)
            return wrappedRequest.getRequestURL();
        StringBuffer sb = new StringBuffer();
        sb.append(getScheme());
        sb.append("://");
        sb.append(getServerName());
        if (getServerPort() > 0) {
            sb.append(':');
            sb.append(getServerPort());
        }
        if (getRequestURI() != null)
            sb.append(getRequestURI());
        return sb;
    }

    /**
     * Method getServletPath.
     *
     * @return String
     * @since 1.4.2
     */
    public String getServletPath() {
        if (wrappedRequest != null)
            return wrappedRequest.getServletPath();
        return servletPath;
    }

    /**
     * {@inheritDoc}
     *
     * Method getSession.
     */
    public HttpSession getSession(boolean create) {
        if (wrappedRequest != null)
            return wrappedRequest.getSession(create);
        return null;
    }

    /**
     * Method getSession.
     *
     * @return HttpSession
     * @since 1.4.2
     */
    public HttpSession getSession() {
        if (wrappedRequest != null)
            return wrappedRequest.getSession();
        return null;
    }

    /**
     * Method isRequestedSessionIdValid.
     *
     * @return boolean
     * @since 1.4.2
     */
    public boolean isRequestedSessionIdValid() {
        if (wrappedRequest != null)
            return wrappedRequest.isRequestedSessionIdValid();
        return false;
    }

    /**
     * Method isRequestedSessionIdFromCookie.
     *
     * @return boolean
     * @since 1.4.2
     */
    public boolean isRequestedSessionIdFromCookie() {
        if (wrappedRequest != null)
            return wrappedRequest.isRequestedSessionIdFromCookie();
        return false;
    }

    /**
     * Method isRequestedSessionIdFromURL.
     *
     * @return boolean
     * @since 1.4.2
     */
    public boolean isRequestedSessionIdFromURL() {
        if (wrappedRequest != null)
            return wrappedRequest.isRequestedSessionIdFromURL();
        return false;
    }

    /**
     * Method isRequestedSessionIdFromUrl.
     *
     * @return boolean
     * @since 1.4.2
     */
    @Deprecated
    public boolean isRequestedSessionIdFromUrl() {
        if (wrappedRequest != null)
            return wrappedRequest.isRequestedSessionIdFromUrl();
        return false;
    }

    /**
     * Method getWrappedRequest.
     *
     * @return HttpServletRequest
     * @since 1.4.2
     */
    public HttpServletRequest getWrappedRequest() {
        return wrappedRequest;
    }

    /**
     * Method setWrappedRequest.
     *
     * @param wrappedRequest
     *            HttpServletRequest
     * @since 1.4.2
     */
    public void setWrappedRequest(HttpServletRequest wrappedRequest) {
        this.wrappedRequest = wrappedRequest;
        loadHeadersAndCookies(wrappedRequest);
    }

    /**
     * Method getAllParameters.
     *
     * @return Hashtable<String,List<String>>
     * @since 1.4.2
     */
    public Hashtable<String, List<String>> getAllParameters() {
        return parameters;
    }

    /**
     * Method setAllParameters.
     *
     * @param parameters
     *            Hashtable<String,List<String>>
     * @since 1.4.2
     */
    public void setAllParameters(Hashtable<String, List<String>> parameters) {
        this.parameters = parameters;
    }

    /**
     * Initializes the request with the provided destination URL
     *
     * @param url
     *            a {@link java.net.URL} object.
     * @throws java.net.UnknownHostException
     *             if any.
     * @since 1.4.2
     */
    public void setURL(URL url) throws UnknownHostException {
        setURL(url, false);
    }
    
    /**
     * Initializes the request with the provided destination URL
     *
     * @param url
     *            a {@link java.net.URL} object.
     * @param parseQueryString
     *            set to <code>true</code> to populate the parameters from the query string
     * @throws java.net.UnknownHostException
     *             if any.
     * @since 1.5.6
     */
    public void setURL(URL url, boolean parseQueryString) throws UnknownHostException {
        localAddr = InetAddress.getLocalHost().getHostAddress();
        localName = InetAddress.getLocalHost().getCanonicalHostName();
        serverName = url.getHost();
        serverPort = url.getPort();
        if (serverPort == -1)
            serverPort = url.getDefaultPort();
        locales.add(Locale.getDefault());
        requestURI = url.getPath();
        queryString = url.getQuery();
        scheme = url.getProtocol();

        if (parseQueryString && queryString != null) {
            StringTokenizer st = new StringTokenizer(queryString, "&");
            while (st.hasMoreTokens()) {
                String token;
                try {
                    token = URLDecoder.decode(st.nextToken(), StandardCharsets.UTF_8.name());
                    int idx = token.indexOf('=');
                    if (idx < 1)
                        continue;
                    String name = token.substring(0, idx);
                    String value = "";
                    if (token.length() > idx)
                        value = token.substring(idx + 1);
                    addParameter(name, value);
                } catch (UnsupportedEncodingException e) {
                    LoggerFactory.getLogger(getClass()).debug("Could not get encoding to URLDecode: " + e.toString(), e); // should never happen
                }
            }
        }
    }

    /**
     * Method getAllAttributes.
     *
     * @return Hashtable<String,Object>
     * @since 1.4.2
     */
    public Hashtable<String, Object> getAllAttributes() {
        return attributes;
    }

    /**
     * Method getAllHeaders.
     *
     * @return Hashtable<String,List<String>>
     * @since 1.4.2
     */
    public Hashtable<String, List<String>> getAllHeaders() {
        return updatedHeaders;
    }

    /**
     * Method setAllCookies.
     *
     * @param cookies
     *            List<Cookie>
     * @since 1.4.2
     */
    public void setAllCookies(Collection<Cookie> cookies) {
        this.cookies = new CopyOnWriteArrayList<Cookie>(cookies);
    }

    /**
     * Method setAllCookies.
     *
     * @param cookies
     *            Cookie[]
     * @since 1.4.2
     */
    public void setAllCookies(Cookie[] cookies) {
        this.cookies = new CopyOnWriteArrayList<Cookie>(cookies);
    }

    /**
     * Method setAllHeaders.
     *
     * @param updatedHeaders
     *            CaseInsensitiveHashtable<List<String>>
     * @since 1.4.2
     */
    public void setAllHeaders(CaseInsensitiveHashtable<List<String>> updatedHeaders) {
        this.updatedHeaders = updatedHeaders;
    }

    /**
     * Method setAllLocales.
     *
     * @param locales
     *            CopyOnWriteArrayList<Locale>
     * @since 1.4.2
     */
    public void setAllLocales(CopyOnWriteArrayList<Locale> locales) {
        this.locales = locales;
    }

    /**
     * Method setContentType.
     *
     * @param contentType
     *            String
     * @since 1.4.2
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Method setLocalAddr.
     *
     * @param localAddr
     *            String
     * @since 1.4.2
     */
    public void setLocalAddr(String localAddr) {
        this.localAddr = localAddr;
    }

    /**
     * Method setRemoteAddr.
     *
     * @param remoteAddr
     *            String
     * @since 1.4.2
     */
    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    /**
     * Method setRemoteHost.
     *
     * @param remoteHost
     *            String
     * @since 1.4.2
     */
    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    /**
     * Method setLocalPort.
     *
     * @param localPort
     *            int
     * @since 1.4.2
     */
    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    /**
     * Method setServerPort.
     *
     * @param serverPort
     *            int
     * @since 1.4.2
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Method setRemotePort.
     *
     * @param remotePort
     *            int
     * @since 1.4.2
     */
    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    /**
     * Method setLocalName.
     *
     * @param localName
     *            String
     * @since 1.4.2
     */
    public void setLocalName(String localName) {
        this.localName = localName;
    }

    /**
     * Method setServerName.
     *
     * @param serverName
     *            String
     * @since 1.4.2
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * Method setProtocol.
     *
     * @param protocol
     *            String
     * @since 1.4.2
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Method setScheme.
     *
     * @param scheme
     *            String
     * @since 1.4.2
     */
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    /**
     * Sets the context path. Note that if the class contains a wrapped request, this method has no effect
     *
     * @param contextPath
     *            a {@link java.lang.String} object.
     * @since 1.4.2
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    /**
     * Method setAuthType.
     *
     * @param authType
     *            String
     * @since 1.4.2
     */
    public void setAuthType(String authType) {
        this.authType = authType;
    }

    /**
     * Method setMethod.
     *
     * @param method
     *            String
     * @since 1.4.2
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Method setRequestURI.
     *
     * @param requestURI
     *            String
     * @since 1.4.2
     */
    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    /**
     * Method setServletPath.
     *
     * @param servletPath
     *            String
     * @since 1.4.2
     */
    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    /**
     * Method setQueryString.
     *
     * @param queryString
     *            String
     * @since 1.4.2
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    /**
     * Sets the raw content body for the request object. Note that this has no effect if this object wraps an existing request object
     *
     * @param body
     *            an array of byte.
     * @since 1.4.2
     */
    public void setBody(byte[] body) {
        this.body = body;
    }

    /**
     * {@inheritDoc}
     *
     * Method getAsyncContext.
     */
    @Override
    public AsyncContext getAsyncContext() {
        if (wrappedRequest != null)
            return wrappedRequest.getAsyncContext();
        return null; // TODO Servlet 3.0 wrapper
    }

    /**
     * {@inheritDoc}
     *
     * Method getDispatcherType.
     */
    @Override
    public DispatcherType getDispatcherType() {
        if (wrappedRequest != null)
            return wrappedRequest.getDispatcherType();
        return null; // TODO Servlet 3.0 wrapper
    }

    /**
     * {@inheritDoc}
     *
     * Method getServletContext.
     */
    @Override
    public ServletContext getServletContext() {
        if (wrappedRequest != null)
            return wrappedRequest.getServletContext();
        return servletContext;
    }

    /**
     * {@inheritDoc}
     *
     * Method isAsyncStarted.
     */
    @Override
    public boolean isAsyncStarted() {
        if (wrappedRequest != null)
            return wrappedRequest.isAsyncStarted();
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * Method isAsyncSupported.
     */
    @Override
    public boolean isAsyncSupported() {
        if (wrappedRequest != null)
            return wrappedRequest.isAsyncSupported();
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * Method startAsync.
     */
    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        if (wrappedRequest != null)
            return wrappedRequest.startAsync();
        return null; // TODO Servlet 3.0 wrapper
    }

    /**
     * {@inheritDoc}
     *
     * Method startAsync.
     */
    @Override
    public AsyncContext startAsync(ServletRequest request, ServletResponse response) throws IllegalStateException {
        if (wrappedRequest != null)
            return wrappedRequest.startAsync(request, response);
        return null; // TODO Servlet 3.0 wrapper
    }

    /**
     * {@inheritDoc}
     *
     * Method authenticate.
     */
    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        if (wrappedRequest != null)
            return wrappedRequest.authenticate(response);
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * Method getPart.
     */
    @Override
    public Part getPart(String arg0) throws IOException, ServletException {
        if (wrappedRequest != null)
            return wrappedRequest.getPart(arg0);
        return null; // TODO Servlet 3.0 wrapper
    }

    /**
     * {@inheritDoc}
     *
     * Method getParts.
     */
    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        if (wrappedRequest != null)
            return wrappedRequest.getParts();
        return null; // TODO Servlet 3.0 wrapper
    }

    /**
     * {@inheritDoc}
     *
     * Method login.
     */
    @Override
    public void login(String arg0, String arg1) throws ServletException {
        if (wrappedRequest != null)
            wrappedRequest.login(arg0, arg1);
    }

    /**
     * {@inheritDoc}
     *
     * Method logout.
     */
    @Override
    public void logout() throws ServletException {
        if (wrappedRequest != null)
            wrappedRequest.logout();
    }

    /**
     * Sets the Servlet Context. Note that if the class has a wrapped request, this method has no effect
     *
     * @param sc
     *            a {@link javax.servlet.ServletContext} object.
     * @since 1.4.2
     */
    public void setServletContext(ServletContext sc) {
        this.servletContext = sc;
    }

    /**
     * Method setContentLength.
     *
     * @param contentLength
     *            int
     * @since 1.4.2
     */
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }
}

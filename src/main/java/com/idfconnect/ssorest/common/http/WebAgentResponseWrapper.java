package com.idfconnect.ssorest.common.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * A simple WebAgentResponseWrapper that provides access to fields within the response after they've been set. This class may also be used without a wrapped
 * response object for purposes such as JUnit testing. It provides a fluent interface and a friendly toString implementation
 *
 * @author Richard Sand
 * @since 1.4
 */
public class WebAgentResponseWrapper implements FluentHttpServletResponse {
    /** Experimental status code from RFC-2774 */
    public static final int                               SC_NOT_EXTENDED   = 510;

    private HashMap<String, Vector<String>>               attributes        = new HashMap<String, Vector<String>>();
    private HashMap<String, Vector<String>>               headers           = new HashMap<String, Vector<String>>();
    private List<Cookie>                                  cookies           = new ArrayList<Cookie>();
    private int                                           contentLength     = 0;
    private int                                           status            = 0;
    private String                                        contentType       = "text/html";
    private String                                        characterEncoding = "UTF-8";
    private Locale                                        locale            = new Locale(characterEncoding);
    private transient WebAgentResponseWrapperOutputStream os                = null;
    private HttpServletResponse                           wrappedResponse   = null;

    /**
     * Returns true if the two cookies have the same name, domain, and path
     *
     * @param cookie1 a {@link javax.servlet.http.Cookie} object.
     * @param cookie2 a {@link javax.servlet.http.Cookie} object.
     * @return boolean
     * @since 1.4
     */
    public static final boolean overrides(Cookie cookie1, Cookie cookie2) {
        if (!(cookie1.getName().equalsIgnoreCase(cookie2.getName())))
            return false;

        if ((cookie1.getPath() != null) && !(cookie1.getPath().equalsIgnoreCase(cookie2.getPath())))
            return false;
        if ((cookie1.getPath() == null) && (cookie2.getPath() != null))
            return false;

        if ((cookie1.getDomain() != null) && !(cookie1.getDomain().equalsIgnoreCase(cookie2.getDomain())))
            return false;
        if ((cookie1.getDomain() == null) && (cookie2.getDomain() != null))
            return false;

        return true;
    }

    /**
     */
    private static class WebAgentResponseWrapperOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream baos = null;

        public WebAgentResponseWrapperOutputStream() {
            super();
            baos = new ByteArrayOutputStream();
        }

        /**
         * Method write.
         * 
         * @param b
         *            int
         * @throws IOException
         */
        @Override
        public void write(int b) throws IOException {
            baos.write(b);
        }

        /**
         * Method flush.
         * 
         * @throws IOException
         * @see java.io.Flushable#flush()
         */
        @Override
        public void flush() throws IOException {
            baos.flush();
        }

        /**
         * Method write.
         * 
         * @param b
         *            byte[]
         * @param off
         *            int
         * @param len
         *            int
         * @throws IOException
         */
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            baos.write(b, off, len);
        }

        /**
         * Method write.
         * 
         * @param b
         *            byte[]
         * @throws IOException
         */
        @Override
        public void write(byte[] b) throws IOException {
            baos.write(b);
        }

        /**
         * Method toString.
         * 
         * @return String
         */
        @Override
        public String toString() {
            try {
                baos.flush();
            } catch (IOException e) {
            }
            try {
                return baos.toString(StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                return ""; // should never happen
            }
        }
    }

    /**
     * <p>Constructor for WebAgentResponseWrapper.</p>
     *
     * @since 1.4
     */
    public WebAgentResponseWrapper() {
        os = new WebAgentResponseWrapperOutputStream();
    }

    /**
     * Constructor for WebAgentResponseWrapper.
     *
     * @param response
     *            HttpServletResponse
     * @since 1.4
     */
    public WebAgentResponseWrapper(HttpServletResponse response) {
        this.wrappedResponse = response;
        if (response instanceof WebAgentResponseWrapper) {
            this.status = ((WebAgentResponseWrapper) response).getStatus();
            this.contentLength = ((WebAgentResponseWrapper) response).getContentLength();
            this.cookies = ((WebAgentResponseWrapper) response).getCookies();
            this.headers = ((WebAgentResponseWrapper) response).getHeaders();
        }
    }

    /**
     * Method flushBuffer.
     *
     * @throws java.io.IOException if any.
     * @since 1.4
     */
    public void flushBuffer() throws IOException {
        if (wrappedResponse != null)
            wrappedResponse.flushBuffer();
    }

    /**
     * Returns the output body of the response IFF there is no underlying wrapped object
     *
     * @return String
     * @since 1.4
     */
    public String getBodyContent() {
        if ((wrappedResponse != null) && (wrappedResponse instanceof WebAgentResponseWrapper))
            return ((WebAgentResponseWrapper) wrappedResponse).getBodyContent();
        if (os == null)
            return null;
        return os.toString();
    }

    /**
     * Method getBufferSize.
     *
     * @return int
     * @since 1.4
     */
    public int getBufferSize() {
        if (wrappedResponse != null)
            return wrappedResponse.getBufferSize();
        return 0;
    }

    /**
     * Method getCharacterEncoding.
     *
     * @return String
     * @since 1.4
     */
    public String getCharacterEncoding() {
        if (wrappedResponse != null)
            return wrappedResponse.getCharacterEncoding();
        return characterEncoding;
    }

    /**
     * Method getContentType.
     *
     * @return String
     * @since 1.4
     */
    public String getContentType() {
        return (wrappedResponse != null) ? wrappedResponse.getContentType() : contentType;
    }

    /**
     * Method getLocale.
     *
     * @return Locale
     * @since 1.4
     */
    public Locale getLocale() {
        return (wrappedResponse != null) ? wrappedResponse.getLocale() : locale;
    }

    /**
     * Method getOutputStream.
     *
     * @return ServletOutputStream
     * @throws java.io.IOException if any.
     * @since 1.4
     */
    public ServletOutputStream getOutputStream() throws IOException {
        if (wrappedResponse != null)
            return wrappedResponse.getOutputStream();
        return os;
    }

    /**
     * Method getWriter.
     *
     * @return PrintWriter
     * @throws java.io.IOException if any.
     * @since 1.4
     */
    public PrintWriter getWriter() throws IOException {
        if (wrappedResponse != null)
            return wrappedResponse.getWriter();
        return new PrintWriter(new OutputStreamWriter(os, getCharacterEncoding()), true);
    }

    /**
     * Method isCommitted.
     *
     * @return boolean
     * @since 1.4
     */
    public boolean isCommitted() {
        if (wrappedResponse != null)
            return wrappedResponse.isCommitted();
        return false;
    }

    /**
     * Method reset.
     *
     * @since 1.4
     */
    public void reset() {
        if (wrappedResponse != null)
            wrappedResponse.reset();
        resetBuffer();
        status = 0;
        cookies = new ArrayList<Cookie>();
        headers = new HashMap<String, Vector<String>>();
    }

    /**
     * Method resetBuffer.
     *
     * @since 1.4
     */
    public void resetBuffer() {
        if (wrappedResponse == null)
            os = new WebAgentResponseWrapperOutputStream();
        else
            wrappedResponse.resetBuffer();
    }

    /**
     * {@inheritDoc}
     *
     * Method setBufferSize.
     */
    public void setBufferSize(int size) {
        if (wrappedResponse != null)
            wrappedResponse.setBufferSize(size);
    }

    /**
     * {@inheritDoc}
     *
     * Method setCharacterEncoding.
     */
    public void setCharacterEncoding(String str) {
        if (wrappedResponse != null)
            wrappedResponse.setCharacterEncoding(str);
        this.characterEncoding = str;
        locale = new Locale(str);
    }

    /**
     * {@inheritDoc}
     *
     * Method setContentLength.
     */
    public void setContentLength(int length) {
        if (wrappedResponse != null)
            wrappedResponse.setContentLength(length);
        this.contentLength = length;
    }

    /**
     * {@inheritDoc}
     *
     * Method setContentType.
     */
    public void setContentType(String str) {
        if (wrappedResponse != null)
            wrappedResponse.setContentType(str);
        this.contentType = str;
    }

    /**
     * {@inheritDoc}
     *
     * Method setLocale.
     */
    public void setLocale(Locale locale) {
        if (wrappedResponse != null)
            wrappedResponse.setLocale(locale);
        this.locale = locale;
    }

    /**
     * {@inheritDoc}
     *
     * Method addCookie.
     */
    public void addCookie(Cookie c) {
        if (wrappedResponse != null)
            wrappedResponse.addCookie(c);
        cookies.add(c);
    }

    /**
     * Places the provided cookie into the response. If there is a wrapped response object, this just calls <code>addCookie</code>. Otherwise if there is an
     * existing cookie with the same name, domain, and path, it will be replaced
     *
     * @param cookie a {@link javax.servlet.http.Cookie} object.
     * @since 1.4
     */
    public void setCookie(Cookie cookie) {
        if (wrappedResponse != null)
            wrappedResponse.addCookie(cookie);
        boolean found = false;
        for (Cookie these : cookies) {
            if (overrides(these, cookie)) {
                these.setValue(cookie.getValue());
                found = true;
                break;
            }
        }
        if (!found)
            cookies.add(cookie);
    }

    /**
     * {@inheritDoc}
     *
     * Method addDateHeader.
     */
    public void addDateHeader(String name, long date) {
        if (wrappedResponse != null)
            wrappedResponse.addDateHeader(name, date);
        addHeader(name, "" + date);
    }

    /**
     * {@inheritDoc}
     *
     * Method addHeader.
     */
    public void addHeader(String name, String value) {
        Vector<String> values = headers.get(name);
        if (values == null)
            values = new Vector<String>();
        values.add(value);
        headers.put(name, values);
        if (wrappedResponse != null)
            wrappedResponse.addHeader(name, value);
    }

    /**
     * Method addAttribute.
     *
     * @param name
     *            String
     * @param value
     *            String
     * @since 1.4
     */
    public void addAttribute(String name, String value) {
        Vector<String> values = attributes.get(name);
        if (values == null)
            values = new Vector<String>();
        values.add(value);
        attributes.put(name, values);
    }

    /**
     * {@inheritDoc}
     *
     * Method addIntHeader.
     */
    public void addIntHeader(String name, int value) {
        Vector<String> values = headers.get(name);
        if (values == null)
            values = new Vector<String>();
        values.add(Integer.toString(value));
        headers.put(name, values);
        if (wrappedResponse != null)
            wrappedResponse.addIntHeader(name, value);
    }

    /**
     * {@inheritDoc}
     *
     * Method containsHeader.
     */
    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    /**
     * {@inheritDoc}
     *
     * Method encodeRedirectURL.
     */
    public String encodeRedirectURL(String url) {
        if (wrappedResponse != null)
            return wrappedResponse.encodeRedirectURL(url);
        return url;
    }

    /**
     * {@inheritDoc}
     *
     * Method encodeRedirectUrl.
     */
    @Deprecated
    public String encodeRedirectUrl(String url) {
        if (wrappedResponse != null)
            return wrappedResponse.encodeRedirectUrl(url);
        return url;
    }

    /**
     * {@inheritDoc}
     *
     * Method encodeURL.
     */
    public String encodeURL(String url) {
        if (wrappedResponse != null)
            return wrappedResponse.encodeURL(url);
        return url;
    }

    /**
     * {@inheritDoc}
     *
     * Method encodeUrl.
     */
    @Deprecated
    public String encodeUrl(String url) {
        if (wrappedResponse != null)
            return wrappedResponse.encodeUrl(url);
        return url;
    }

    /**
     * {@inheritDoc}
     *
     * Method sendError.
     */
    public void sendError(int sc) throws IOException {
        if (wrappedResponse != null)
            wrappedResponse.setStatus(sc);
        setStatus(sc);
    }

    /**
     * {@inheritDoc}
     *
     * Method sendError.
     */
    public void sendError(int sc, String msg) throws IOException {
        setStatus(sc);
        if (wrappedResponse != null)
            wrappedResponse.sendError(sc, msg);
        else
            getWriter().println(msg);
    }

    /**
     * {@inheritDoc}
     *
     * Method sendRedirect.
     */
    public void sendRedirect(String url) throws IOException {
        setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        setHeader("Location", url);
        if (wrappedResponse != null)
            wrappedResponse.sendRedirect(url);
    }

    /**
     * {@inheritDoc}
     *
     * Method setDateHeader.
     */
    public void setDateHeader(String name, long date) {
        Vector<String> values = new Vector<String>();
        values.add("" + date);
        headers.put(name, values);
        if (wrappedResponse != null)
            wrappedResponse.setDateHeader(name, date);
    }

    /**
     * {@inheritDoc}
     *
     * Method setHeader.
     */
    public void setHeader(String name, String value) {
        Vector<String> values = new Vector<String>();
        values.add(value);
        headers.put(name, values);
        if (wrappedResponse != null)
            wrappedResponse.setHeader(name, value);
    }

    /**
     * {@inheritDoc}
     *
     * Method setIntHeader.
     */
    public void setIntHeader(String name, int value) {
        setHeader(name, Integer.toString(value));
    }

    /**
     * {@inheritDoc}
     *
     * Method setStatus.
     */
    public void setStatus(int sc) {
        if (wrappedResponse != null)
            wrappedResponse.setStatus(sc);
        this.status = sc;
    }

    /**
     * {@inheritDoc}
     *
     * Method setStatus.
     */
    @Deprecated
    public void setStatus(int sc, String msg) {
        try {
            sendError(sc, msg);
        } catch (IOException e) {
        }
    }

    /**
     * Method getStatus.
     *
     * @return int
     * @since 1.4
     */
    public int getStatus() {
        return status;
    }

    /**
     * {@inheritDoc}
     *
     * Method toString.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append(" [");
        builder.append("status=");
        builder.append(status);
        builder.append(", contentLength=");
        builder.append(contentLength);
        builder.append(", ");
        if (contentType != null) {
            builder.append("contentType=");
            builder.append(contentType);
            builder.append(", ");
        }
        if (characterEncoding != null) {
            builder.append("characterEncoding=");
            builder.append(characterEncoding);
            builder.append(", ");
        }
        if (locale != null) {
            builder.append("locale=");
            builder.append(locale);
            builder.append(", ");
        }
        if (attributes != null) {
            builder.append("attributes=");
            builder.append(attributes);
            builder.append(", ");
        }
        if (headers != null) {
            builder.append("headers=");
            builder.append(headers);
            builder.append(", ");
        }
        if (cookies != null) {
            builder.append("cookies=");
            builder.append(cookies);
            builder.append(", ");
        }
        if (os != null) {
            builder.append("body=");
            builder.append(os);
        }
        builder.append(']');
        return builder.toString();
    }

    /**
     * <p>Getter for the field <code>cookies</code>.</p>
     *
     * @return the cookies
     * @since 1.4
     */
    public List<Cookie> getCookies() {
        return cookies;
    }

    /**
     * <p>Getter for the field <code>cookies</code>.</p>
     *
     * @param name
     *            String
     * @return the cookies
     * @since 1.4
     */
    public List<Cookie> getCookies(String name) {
        ArrayList<Cookie> results = new ArrayList<Cookie>();
        for (Cookie cookie : cookies)
            if (cookie.getName().equalsIgnoreCase(name))
                results.add(cookie);
        return results;
    }

    /**
     * <p>Getter for the field <code>headers</code>.</p>
     *
     * @return the headers
     * @since 1.4
     */
    public HashMap<String, Vector<String>> getHeaders() {
        return headers;
    }

    /**
     * <p>Getter for the field <code>attributes</code>.</p>
     *
     * @return the attributes
     * @since 1.4
     */
    public HashMap<String, Vector<String>> getAttributes() {
        return attributes;
    }

    /**
     * <p>Getter for the field <code>contentLength</code>.</p>
     *
     * @return the contentLength
     * @since 1.4
     */
    public int getContentLength() {
        return contentLength;
    }

    /**
     * {@inheritDoc}
     *
     * Method getHeader.
     */
    @Override
    public String getHeader(String name) {
        Vector<String> values = headers.get(name);
        if (values == null || values.size() == 0)
            return null;
        return values.firstElement();
    }

    /**
     * {@inheritDoc}
     *
     * Method getHeaderNames.
     */
    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    /**
     * {@inheritDoc}
     *
     * Method getHeaders.
     */
    @Override
    public Collection<String> getHeaders(String name) {
        return headers.get(name);
    }

    /**
     * Returns a custom attribute from the object. If no such attribute exists, returns null
     *
     * @param name a {@link java.lang.String} object.
     * @return Object
     * @since 1.4
     */
    public Object getAttribute(String name) {
        return (attributes == null) ? null : attributes.get(name);
    }

    /**
     * Returns the names of the custom attributes set in the object. If no attributes have been set, returns null (NOT an empty object)
     *
     * @return Collection<String>
     * @since 1.4
     */
    public Collection<String> getAttributeNames() {
        return attributes.keySet();
    }

    /**
     * Sets a custom attribute. Returns the replaced value, or null if none existed
     *
     * @param name a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     * @return Vector<String>
     * @since 1.4
     */
    public Vector<String> setAttribute(String name, String value) {
        Vector<String> values = new Vector<String>();
        values.add(value);
        return attributes.put(name, values);
    }

    /**
     * Removes a custom attribute. Returns the replaced value, or null if none existed
     *
     * @param name a {@link java.lang.String} object.
     * @return Vector<String>
     * @since 1.4
     */
    public Vector<String> removeAttribute(String name) {
        return attributes.remove(name);
    }

    /** {@inheritDoc} */
    @Override
    public FluentHttpServletResponse attribute(String name, String value) {
        addAttribute(name, value);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FluentHttpServletResponse cookie(Cookie cookie) {
        addCookie(cookie);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FluentHttpServletResponse dateHeader(String name, long date) {
        addDateHeader(name, date);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FluentHttpServletResponse header(String name, String value) {
        addHeader(name, value);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FluentHttpServletResponse intHeader(String name, int value) {
        addIntHeader(name, value);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FluentHttpServletResponse flush() throws IOException {
        flushBuffer();
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FluentHttpServletResponse doReset() {
        reset();
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FluentHttpServletResponse doResetBuffer() {
        resetBuffer();
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FluentHttpServletResponse error(int sc) throws IOException {
        sendError(sc);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FluentHttpServletResponse error(int sc, String message) throws IOException {
        sendError(sc, message);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FluentHttpServletResponse redirect(String url) throws IOException {
        sendRedirect(url);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FluentHttpServletResponse bufferSize(int size) {
        setBufferSize(size);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FluentHttpServletResponse characterEncoding(String str) {
        setCharacterEncoding(str);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FluentHttpServletResponse contentLength(int length) {
        setContentLength(length);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FluentHttpServletResponse contentType(String type) {
        setContentType(type);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FluentHttpServletResponse locale(Locale locale) {
        setLocale(locale);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FluentHttpServletResponse status(int sc) {
        setStatus(sc);
        return this;
    }
}

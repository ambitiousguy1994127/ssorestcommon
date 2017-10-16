package com.idfconnect.ssorest.common.test.http;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.idfconnect.ssorest.common.http.FriendlyCookie;

/**
 * A simple POJO implementation of HttpServletResponse, mainly for use with JUnit
 * 
 * @author Richard Sand
 * 
 * @version $Revision: 1.0 $
 */
public class TestHttpServletResponse implements HttpServletResponse {
    List<Cookie>                    cookies           = new ArrayList<Cookie>();
    HashMap<String, Vector<String>> headers           = new HashMap<String, Vector<String>>();
    int                             bufferSize        = 0;
    int                             contentLength     = 0;
    int                             status            = 0;
    String                          contentType       = "text/html";
    String                          characterEncoding = "ISO-8859-1";
    Locale                          locale            = new Locale(characterEncoding);
    TestServletOutputStream         os;

    public TestHttpServletResponse() {
        os = new TestServletOutputStream();
    }

    /**
     * Method flushBuffer.
     * @throws IOException
     * @see javax.servlet.ServletResponse#flushBuffer()
     */
    public void flushBuffer() throws IOException {
    }

    /**
     * Method getBufferSize.
     * @return int
     * @see javax.servlet.ServletResponse#getBufferSize()
     */
    public int getBufferSize() {
        return 0;
    }

    /**
     * Method getCharacterEncoding.
     * @return String
     * @see javax.servlet.ServletResponse#getCharacterEncoding()
     */
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    /**
     * Method getContentType.
     * @return String
     * @see javax.servlet.ServletResponse#getContentType()
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Method getLocale.
     * @return Locale
     * @see javax.servlet.ServletResponse#getLocale()
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Method getOutputStream.
     * @return ServletOutputStream
     * @throws IOException
     * @see javax.servlet.ServletResponse#getOutputStream()
     */
    public ServletOutputStream getOutputStream() throws IOException {
        return os;
    }

    /**
     * Method getWriter.
     * @return PrintWriter
     * @throws IOException
     * @see javax.servlet.ServletResponse#getWriter()
     */
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(new OutputStreamWriter(os, getCharacterEncoding()));
    }

    /**
     * Method isCommitted.
     * @return boolean
     * @see javax.servlet.ServletResponse#isCommitted()
     */
    public boolean isCommitted() {
        return false;
    }

    /**
     * Method reset.
     * @see javax.servlet.ServletResponse#reset()
     */
    public void reset() {
        resetBuffer();
        status = 0;
    }

    /**
     * Method resetBuffer.
     * @see javax.servlet.ServletResponse#resetBuffer()
     */
    public void resetBuffer() {
        os = new TestServletOutputStream();
    }

    /**
     * Method setBufferSize.
     * @param size int
     * @see javax.servlet.ServletResponse#setBufferSize(int)
     */
    public void setBufferSize(int size) {

    }

    /**
     * Method setCharacterEncoding.
     * @param str String
     * @see javax.servlet.ServletResponse#setCharacterEncoding(String)
     */
    public void setCharacterEncoding(String str) {
        this.characterEncoding = str;
        locale = new Locale(str);
    }

    /**
     * Method setContentLength.
     * @param length int
     * @see javax.servlet.ServletResponse#setContentLength(int)
     */
    public void setContentLength(int length) {
        this.contentLength = length;
    }

    /**
     * Method setContentType.
     * @param str String
     * @see javax.servlet.ServletResponse#setContentType(String)
     */
    public void setContentType(String str) {
        if (str.contains(";")) {
            contentType = str.substring(0, str.indexOf(';'));
            String str2 = str.substring(str.indexOf(';') + 1).trim();
            int idx = str2.indexOf("charset=") + 8;
            characterEncoding = str2.substring(idx);
        } else
            this.contentType = str;
    }

    /**
     * Method setLocale.
     * @param locale Locale
     * @see javax.servlet.ServletResponse#setLocale(Locale)
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Method addCookie.
     * @param c Cookie
     * @see javax.servlet.http.HttpServletResponse#addCookie(Cookie)
     */
    public void addCookie(Cookie c) {
        cookies.add(new FriendlyCookie(c));
    }

    /**
     * Method addDateHeader.
     * @param name String
     * @param date long
     * @see javax.servlet.http.HttpServletResponse#addDateHeader(String, long)
     */
    public void addDateHeader(String name, long date) {
        addHeader(name, "" + date);
    }

    /**
     * Method addHeader.
     * @param name String
     * @param value String
     * @see javax.servlet.http.HttpServletResponse#addHeader(String, String)
     */
    public void addHeader(String name, String value) {
        Vector<String> values = headers.get(name);
        if (values == null)
            values = new Vector<String>();
        values.add(value);
        headers.put(name, values);
    }

    /**
     * Method addIntHeader.
     * @param name String
     * @param value int
     * @see javax.servlet.http.HttpServletResponse#addIntHeader(String, int)
     */
    public void addIntHeader(String name, int value) {
        Vector<String> values = headers.get(name);
        if (values == null)
            values = new Vector<String>();
        values.add("" + value);
        headers.put(name, values);
    }

    /**
     * Method containsHeader.
     * @param name String
     * @return boolean
     * @see javax.servlet.http.HttpServletResponse#containsHeader(String)
     */
    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    /**
     * Method encodeRedirectURL.
     * @param url String
     * @return String
     * @see javax.servlet.http.HttpServletResponse#encodeRedirectURL(String)
     */
    public String encodeRedirectURL(String url) {
        return url;
    }

    /**
     * Method encodeRedirectUrl.
     * @param url String
     * @return String
     * @see javax.servlet.http.HttpServletResponse#encodeRedirectUrl(String)
     */
    @Deprecated
    public String encodeRedirectUrl(String url) {
        return url;
    }

    /**
     * Method encodeURL.
     * @param url String
     * @return String
     * @see javax.servlet.http.HttpServletResponse#encodeURL(String)
     */
    public String encodeURL(String url) {
        return url;
    }

    /**
     * Method encodeUrl.
     * @param url String
     * @return String
     * @see javax.servlet.http.HttpServletResponse#encodeUrl(String)
     */
    @Deprecated
    public String encodeUrl(String url) {
        return url;
    }

    /**
     * Method sendError.
     * @param sc int
     * @throws IOException
     * @see javax.servlet.http.HttpServletResponse#sendError(int)
     */
    public void sendError(int sc) throws IOException {
        setStatus(sc);
    }

    /**
     * Method sendError.
     * @param sc int
     * @param msg String
     * @throws IOException
     * @see javax.servlet.http.HttpServletResponse#sendError(int, String)
     */
    public void sendError(int sc, String msg) throws IOException {
        setStatus(sc);
        getWriter().println(msg);
    }

    /**
     * Method sendRedirect.
     * @param url String
     * @throws IOException
     * @see javax.servlet.http.HttpServletResponse#sendRedirect(String)
     */
    public void sendRedirect(String url) throws IOException {
        setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        setHeader("Location", url);
    }

    /**
     * Method setDateHeader.
     * @param name String
     * @param date long
     * @see javax.servlet.http.HttpServletResponse#setDateHeader(String, long)
     */
    public void setDateHeader(String name, long date) {
        setHeader(name, "" + date);
    }

    /**
     * Method setHeader.
     * @param name String
     * @param value String
     * @see javax.servlet.http.HttpServletResponse#setHeader(String, String)
     */
    public void setHeader(String name, String value) {
        Vector<String> values = new Vector<String>();
        values.add(value);
        headers.put(name, values);
    }

    /**
     * Method setIntHeader.
     * @param name String
     * @param value int
     * @see javax.servlet.http.HttpServletResponse#setIntHeader(String, int)
     */
    public void setIntHeader(String name, int value) {
        setHeader(name, "" + value);
    }

    /**
     * Method setStatus.
     * @param sc int
     * @see javax.servlet.http.HttpServletResponse#setStatus(int)
     */
    public void setStatus(int sc) {
        this.status = sc;
    }

    /**
     * Method setStatus.
     * @param sc int
     * @param msg String
     * @see javax.servlet.http.HttpServletResponse#setStatus(int, String)
     */
    @Deprecated
    public void setStatus(int sc, String msg) {
        this.status = sc;
        try {
            getWriter().println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method getStatus.
     * @return int
     * @see javax.servlet.http.HttpServletResponse#getStatus()
     */
    public int getStatus() {
        return status;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TestHttpServletResponse [");
        builder.append("status=");
        builder.append(status);
        builder.append(", ");
        builder.append("contentLength=");
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
        if (cookies != null) {
            builder.append("cookies=");
            builder.append(cookies);
            builder.append(", ");
        }
        if (headers != null) {
            builder.append("headers=");
            builder.append(headers);
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
    
     * @return the cookies */
    public List<Cookie> getCookies() {
        return cookies;
    }

    /**
    
     * @param name String
     * @return the cookies */
    public List<Cookie> getCookies(String name) {
        ArrayList<Cookie> results = new ArrayList<Cookie>();
        for (Cookie cookie : cookies)
            if (cookie.getName().equalsIgnoreCase(name))
                results.add(cookie);
        return results;
    }

    /**
    
     * @return the headers */
    public HashMap<String, Vector<String>> getHeaders() {
        return headers;
    }

    /**
    
     * @return the contentLength */
    public int getContentLength() {
        return contentLength;
    }

    /**
     * Method getHeader.
     * @param name String
     * @return String
     * @see javax.servlet.http.HttpServletResponse#getHeader(String)
     */
    @Override
    public String getHeader(String name) {
        List<String> values = headers.get(name);
        if (values == null || values.size() < 1)
        return null;
        return values.get(0);
    }

    /**
     * Method getHeaderNames.
     * @return Collection<String>
     * @see javax.servlet.http.HttpServletResponse#getHeaderNames()
     */
    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    /**
     * Method getHeaders.
     * @param name String
     * @return Collection<String>
     * @see javax.servlet.http.HttpServletResponse#getHeaders(String)
     */
    @Override
    public Collection<String> getHeaders(String name) {
        return headers.get(name);
    }
}

package com.idfconnect.ssorest.common.test.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
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

import com.idfconnect.ssorest.common.http.FriendlyCookie;

/**
 * A simple POJO implementation of HttpServletRequest, mainly for use with JUnit
 * 
 * @author Richard Sand
 * 
 * @version $Revision: 1.0 $
 */
public class TestHttpServletRequest implements HttpServletRequest {
    CopyOnWriteArrayList<Cookie>    cookies           = new CopyOnWriteArrayList<Cookie>();
    HashMap<String, Object>         attributes        = new HashMap<String, Object>();
    Map<String, String[]>           parameters        = new HashMap<String, String[]>();
    HashMap<String, Vector<String>> headers           = new HashMap<String, Vector<String>>();
    Vector<Locale>                  locales           = new Vector<Locale>();
    String                          contentType       = "text/html";
    String                          characterEncoding = "ISO-8859-1";
    String                          localAddr         = "127.0.0.1";
    String                          remoteAddr        = "127.0.0.1";
    String                          remoteHost        = "localhost";
    int                             localPort         = 80;
    int                             serverPort        = 80;
    int                             remotePort        = 23456;
    String                          localName         = "localhost";
    String                          serverName        = "localhost";
    String                          protocol          = "HTTP/1.1";
    String                          scheme            = "http";
    String                          contextPath       = "";
    String                          authType          = null;
    String                          method            = "GET";
    TestServletInputStream          is;
    URL                             testURL           = null;
    String                          requestURI;
    String                          servletPath       = "";
    String                          remoteUser        = null;
    String                          queryString       = null;

    public TestHttpServletRequest() {
        try {
            localAddr = InetAddress.getLocalHost().getHostAddress();
            localName = InetAddress.getLocalHost().getHostName();
            serverName = InetAddress.getLocalHost().getCanonicalHostName();
            locales.add(Locale.getDefault());
        } catch (UnknownHostException e) {
        }
    }

    /**
     * Constructor for TestHttpServletRequest.
     * @param url String
     * @throws MalformedURLException
     * @throws UnknownHostException
     */
    public TestHttpServletRequest(String url) throws MalformedURLException, UnknownHostException {
        this(new URL(url));
    }

    /**
     * Constructor for TestHttpServletRequest.
     * @param url URL
     * @throws UnknownHostException
     */
    public TestHttpServletRequest(URL url) throws UnknownHostException {
        this.testURL = url;
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
    }

    /**
     * Method getAttribute.
     * @param name String
     * @return Object
     * @see javax.servlet.ServletRequest#getAttribute(String)
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * Method getAttributeNames.
     * @return Enumeration<String>
     * @see javax.servlet.ServletRequest#getAttributeNames()
     */
    public Enumeration<String> getAttributeNames() {
        return new Vector<String>(attributes.keySet()).elements();
    }

    /**
     * Method getCharacterEncoding.
     * @return String
     * @see javax.servlet.ServletRequest#getCharacterEncoding()
     */
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    /**
     * Method getContentLength.
     * @return int
     * @see javax.servlet.ServletRequest#getContentLength()
     */
    public int getContentLength() {
        return toString().length();
    }

    /**
     * Method getContentType.
     * @return String
     * @see javax.servlet.ServletRequest#getContentType()
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Method getInputStream.
     * @return ServletInputStream
     * @throws IOException
     * @see javax.servlet.ServletRequest#getInputStream()
     */
    public ServletInputStream getInputStream() throws IOException {
        return new TestServletInputStream(toString());
    }

    /**
     * Method getLocalAddr.
     * @return String
     * @see javax.servlet.ServletRequest#getLocalAddr()
     */
    public String getLocalAddr() {
        return localAddr;
    }

    /**
     * Method getLocalName.
     * @return String
     * @see javax.servlet.ServletRequest#getLocalName()
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * Method getLocalPort.
     * @return int
     * @see javax.servlet.ServletRequest#getLocalPort()
     */
    public int getLocalPort() {
        return localPort;
    }

    /**
     * Method getLocale.
     * @return Locale
     * @see javax.servlet.ServletRequest#getLocale()
     */
    public Locale getLocale() {
        return locales.get(0);
    }

    /**
     * Method getLocales.
     * @return Enumeration<Locale>
     * @see javax.servlet.ServletRequest#getLocales()
     */
    public Enumeration<Locale> getLocales() {
        return locales.elements();
    }

    /**
     * Method getParameter.
     * @param name String
     * @return String
     * @see javax.servlet.ServletRequest#getParameter(String)
     */
    public String getParameter(String name) {
        String[] v = parameters.get(name);
        if (v == null || v.length < 1)
            return null;
        return v[0];
    }

    /**
     * Method getParameterMap.
     * @return Map<String,String[]>
     * @see javax.servlet.ServletRequest#getParameterMap()
     */
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    /**
     * Method getParameterNames.
     * @return Enumeration<String>
     * @see javax.servlet.ServletRequest#getParameterNames()
     */
    public Enumeration<String> getParameterNames() {
        return new Vector<String>(parameters.keySet()).elements();
    }

    /**
     * Method getParameterValues.
     * @param name String
     * @return String[]
     * @see javax.servlet.ServletRequest#getParameterValues(String)
     */
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }

    /**
     * Method getProtocol.
     * @return String
     * @see javax.servlet.ServletRequest#getProtocol()
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Method getReader.
     * @return BufferedReader
     * @throws IOException
     * @see javax.servlet.ServletRequest#getReader()
     */
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(is));
    }

    /**
     * Method getRealPath.
     * @param path String
     * @return String
     * @see javax.servlet.ServletRequest#getRealPath(String)
     */
    public String getRealPath(String path) {
        return null;
    }

    /**
     * Method getRemoteAddr.
     * @return String
     * @see javax.servlet.ServletRequest#getRemoteAddr()
     */
    public String getRemoteAddr() {
        return remoteAddr;
    }

    /**
     * Method getRemoteHost.
     * @return String
     * @see javax.servlet.ServletRequest#getRemoteHost()
     */
    public String getRemoteHost() {
        return remoteHost;
    }

    /**
     * Method getRemotePort.
     * @return int
     * @see javax.servlet.ServletRequest#getRemotePort()
     */
    public int getRemotePort() {
        return remotePort;
    }

    /**
     * Method getRequestDispatcher.
     * @param path String
     * @return RequestDispatcher
     * @see javax.servlet.ServletRequest#getRequestDispatcher(String)
     */
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    /**
     * Method getScheme.
     * @return String
     * @see javax.servlet.ServletRequest#getScheme()
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * Method getServerName.
     * @return String
     * @see javax.servlet.ServletRequest#getServerName()
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Method getServerPort.
     * @return int
     * @see javax.servlet.ServletRequest#getServerPort()
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Method isSecure.
     * @return boolean
     * @see javax.servlet.ServletRequest#isSecure()
     */
    public boolean isSecure() {
        return getScheme().endsWith("s");
    }

    /**
     * Method removeAttribute.
     * @param name String
     * @see javax.servlet.ServletRequest#removeAttribute(String)
     */
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    /**
     * Method setAttribute.
     * @param name String
     * @param value Object
     * @see javax.servlet.ServletRequest#setAttribute(String, Object)
     */
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    /**
     * Method setCharacterEncoding.
     * @param enc String
     * @throws UnsupportedEncodingException
     * @see javax.servlet.ServletRequest#setCharacterEncoding(String)
     */
    public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
        this.characterEncoding = enc;
    }

    /**
     * Method getAuthType.
     * @return String
     * @see javax.servlet.http.HttpServletRequest#getAuthType()
     */
    public String getAuthType() {
        return authType;
    }

    /**
     * Method getContextPath.
     * @return String
     * @see javax.servlet.http.HttpServletRequest#getContextPath()
     */
    public String getContextPath() {
        return contextPath;
    }

    /**
     * Method getCookie.
     * @param name String
     * @return Cookie
     */
    public Cookie getCookie(String name) {
        for (Cookie c : cookies)
            if (c.getName().equalsIgnoreCase(name))
                return c;
        return null;
    }

    /**
     * Method getCookies.
     * @return Cookie[]
     * @see javax.servlet.http.HttpServletRequest#getCookies()
     */
    public synchronized Cookie[] getCookies() {
        return cookies.toArray(new Cookie[] {});
    }

    /**
     * Method getDateHeader.
     * @param name String
     * @return long
     * @see javax.servlet.http.HttpServletRequest#getDateHeader(String)
     */
    public long getDateHeader(String name) {
        String value = getHeader(name);
        if (value == null)
            return -1;
        try {
            DateFormat df = new SimpleDateFormat();
            Date d = df.parse(value);
            return d.getTime();
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not parse date: " + value);
        }
    }

    /**
     * Method getHeader.
     * @param name String
     * @return String
     * @see javax.servlet.http.HttpServletRequest#getHeader(String)
     */
    public String getHeader(String name) {
        Vector<String> v = headers.get(name);
        if (v == null)
            return null;
        return v.get(0);
    }

    /**
     * Method getHeaderNames.
     * @return Enumeration<String>
     * @see javax.servlet.http.HttpServletRequest#getHeaderNames()
     */
    public Enumeration<String> getHeaderNames() {
        return new Vector<String>(headers.keySet()).elements();
    }

    /**
     * Method getHeaders.
     * @param name String
     * @return Enumeration<String>
     * @see javax.servlet.http.HttpServletRequest#getHeaders(String)
     */
    public Enumeration<String> getHeaders(String name) {
        Vector<String> v = headers.get(name);
        if (v == null)
            return null;
        return v.elements();
    }

    /**
     * Method getIntHeader.
     * @param name String
     * @return int
     * @see javax.servlet.http.HttpServletRequest#getIntHeader(String)
     */
    public int getIntHeader(String name) {
        String value = getHeader(name);
        if (value == null)
            return -1;
        return Integer.parseInt(value);
    }

    /**
     * Method getMethod.
     * @return String
     * @see javax.servlet.http.HttpServletRequest#getMethod()
     */
    public String getMethod() {
        return method;
    }

    /**
     * Method getPathInfo.
     * @return String
     * @see javax.servlet.http.HttpServletRequest#getPathInfo()
     */
    public String getPathInfo() {
        return null;
    }

    /**
     * Method getPathTranslated.
     * @return String
     * @see javax.servlet.http.HttpServletRequest#getPathTranslated()
     */
    public String getPathTranslated() {
        return null;
    }

    /**
     * Method getQueryString.
     * @return String
     * @see javax.servlet.http.HttpServletRequest#getQueryString()
     */
    public String getQueryString() {
        return queryString;
    }

    /**
     * Method getRemoteUser.
     * @return String
     * @see javax.servlet.http.HttpServletRequest#getRemoteUser()
     */
    public String getRemoteUser() {
        return remoteUser;
    }

    /**
     * Method getRequestURI.
     * @return String
     * @see javax.servlet.http.HttpServletRequest#getRequestURI()
     */
    public String getRequestURI() {
        return requestURI;
    }

    /**
     * Method getRequestURL.
     * @return StringBuffer
     * @see javax.servlet.http.HttpServletRequest#getRequestURL()
     */
    public StringBuffer getRequestURL() {
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
     * Method getRequestedSessionId.
     * @return String
     * @see javax.servlet.http.HttpServletRequest#getRequestedSessionId()
     */
    public String getRequestedSessionId() {
        return null;
    }

    /**
     * Method getServletPath.
     * @return String
     * @see javax.servlet.http.HttpServletRequest#getServletPath()
     */
    public String getServletPath() {
        return servletPath;
    }

    /**
     * Method getSession.
     * @return HttpSession
     * @see javax.servlet.http.HttpServletRequest#getSession()
     */
    public HttpSession getSession() {
        return null;
    }

    /**
     * Method getSession.
     * @param create boolean
     * @return HttpSession
     * @see javax.servlet.http.HttpServletRequest#getSession(boolean)
     */
    public HttpSession getSession(boolean create) {
        return null;
    }

    /**
     * Method getUserPrincipal.
     * @return Principal
     * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
     */
    public Principal getUserPrincipal() {
        return null;
    }

    /**
     * Method isRequestedSessionIdFromCookie.
     * @return boolean
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromCookie()
     */
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    /**
     * Method isRequestedSessionIdFromURL.
     * @return boolean
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromURL()
     */
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    /**
     * Method isRequestedSessionIdFromUrl.
     * @return boolean
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromUrl()
     */
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    /**
     * Method isRequestedSessionIdValid.
     * @return boolean
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdValid()
     */
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    /**
     * Method isUserInRole.
     * @param arg0 String
     * @return boolean
     * @see javax.servlet.http.HttpServletRequest#isUserInRole(String)
     */
    public boolean isUserInRole(String arg0) {
        return false;
    }

    /**
    
     * @return the testURL */
    public URL getTestURL() {
        return testURL;
    }

    /**
     * @param locales
     *            the locales to set
     */
    public void setLocales(Vector<Locale> locales) {
        this.locales = locales;
    }

    /**
     * @param contentType
     *            the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @param localAddr
     *            the localAddr to set
     */
    public void setLocalAddr(String localAddr) {
        this.localAddr = localAddr;
    }

    /**
     * @param remoteAddr
     *            the remoteAddr to set
     */
    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    /**
     * @param remoteHost
     *            the remoteHost to set
     */
    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    /**
     * @param localPort
     *            the localPort to set
     */
    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    /**
     * @param remotePort
     *            the remotePort to set
     */
    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    /**
     * @param localName
     *            the localName to set
     */
    public void setLocalName(String localName) {
        this.localName = localName;
    }

    /**
     * @param protocol
     *            the protocol to set
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @param scheme
     *            the scheme to set
     */
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    /**
     * @param contextPath
     *            the contextPath to set
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    /**
     * @param authType
     *            the authType to set
     */
    public void setAuthType(String authType) {
        this.authType = authType;
    }

    /**
     * @param method
     *            the method to set
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @param is
     *            the is to set
     */
    public void setInputStream(TestServletInputStream is) {
        this.is = is;
    }

    /**
     * @param requestURI
     *            the requestURI to set
     */
    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    /**
     * @param servletPath
     *            the servletPath to set
     */
    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    /**
     * @param remoteUser
     *            the remoteUser to set
     */
    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    /**
     * @param queryString
     *            the queryString to set
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    /**
     * Method addHeader.
     * @param name String
     * @param value String
     * @return Vector<String>
     */
    public Vector<String> addHeader(String name, String value) {
        Vector<String> values = headers.get(name);
        if (values == null)
            values = new Vector<String>();
        values.add(value);
        return headers.put(name, values);
    }

    /**
     * Method addAttribute.
     * @param name String
     * @param value Object
     * @return Object
     */
    public Object addAttribute(String name, Object value) {
        return attributes.put(name, value);
    }

    /**
     * Method addParameter.
     * @param name String
     * @param value String
     * @return String[]
     */
    public String[] addParameter(String name, String value) {
        String[] str = parameters.get(name);
        ArrayList<String> values = new ArrayList<String>();
        if (str != null)
            for (String next : str)
                values.add(next);
        values.add(value);
        return parameters.put(name, values.toArray(new String[]{}));
    }

    /**
     * Adds the provided cookie to the request. Note that if there is a duplicate cookie (same name, domain, and path) it will be overwritten
     * 
     * @param c
     */
    public synchronized void addCookie(Cookie c) {
        for (Cookie cc : cookies) {
            if (cc.getName().equalsIgnoreCase(c.getName()) && cc.getPath().equalsIgnoreCase(c.getPath()) && cc.getDomain().equalsIgnoreCase(c.getDomain()))
                cookies.remove(cc);
        }
        cookies.add(new FriendlyCookie(c));
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TestHttpServletRequest [cookies=");
        builder.append(cookies);
        builder.append(", attributes=");
        builder.append(attributes);
        builder.append(", parameters=");
        builder.append(parameters);
        builder.append(", headers=");
        builder.append(headers);
        builder.append(", locales=");
        builder.append(locales);
        builder.append(", contentType=");
        builder.append(contentType);
        builder.append(", characterEncoding=");
        builder.append(characterEncoding);
        builder.append(", localAddr=");
        builder.append(localAddr);
        builder.append(", remoteAddr=");
        builder.append(remoteAddr);
        builder.append(", localPort=");
        builder.append(localPort);
        builder.append(", serverPort=");
        builder.append(serverPort);
        builder.append(", remotePort=");
        builder.append(remotePort);
        builder.append(", localName=");
        builder.append(localName);
        builder.append(", serverName=");
        builder.append(serverName);
        builder.append(", protocol=");
        builder.append(protocol);
        builder.append(", scheme=");
        builder.append(scheme);
        builder.append(", contextPath=");
        builder.append(contextPath);
        builder.append(", authType=");
        builder.append(authType);
        builder.append(", method=");
        builder.append(method);
        builder.append(", requestURI=");
        builder.append(requestURI);
        builder.append(", servletPath=");
        builder.append(servletPath);
        builder.append(", remoteUser=");
        builder.append(remoteUser);
        builder.append(", queryString=");
        builder.append(queryString);
        builder.append(']');
        return builder.toString();
    }

    /**
     * Erases the existing cookies and sets a new cookie list
     * 
    
     * @param cookiesArray Cookie[]
     */
    public synchronized void setCookies(Cookie[] cookiesArray) {
        this.cookies = new CopyOnWriteArrayList<Cookie>();
        addCookies(cookiesArray);
    }

    /**
     * Adds the provided cookies to the current cookie list
     * 
    
     * @param cookiesArray Cookie[]
     */
    public synchronized void addCookies(Cookie[] cookiesArray) {
        for (Cookie c : cookiesArray)
            addCookie(c);
    }

    /**
     * Adds the provided cookies to the current cookie list
     * 
    
     * @param newcookies List<Cookie>
     */
    public synchronized void addCookies(List<Cookie> newcookies) {
        for (Cookie c : newcookies)
            addCookie(c);
    }

    /**
     * Erases the existing cookies and sets a new cookie list
     * 
    
     * @param newcookies List<Cookie>
     */
    public synchronized void setCookies(List<Cookie> newcookies) {
        this.cookies = new CopyOnWriteArrayList<Cookie>();
        addCookies(newcookies);
    }

    /**
     * @param attributes
     *            the attributes to set
     */
    public void setAttributes(HashMap<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * @param parameters
     *            the parameters to set
     */
    public void setParameters(Map<String, String[]> parameters) {
        this.parameters = parameters;
    }

    /**
     * @param headers
     *            the headers to set
     */
    public void setHeaders(HashMap<String, Vector<String>> headers) {
        this.headers = headers;
    }

    /**
     * @param testURL
     *            the testURL to set
     */
    public void setTestURL(URL testURL) {
        this.testURL = testURL;
    }

    /**
     * Method setRequestBasicAuth.
     * @param req TestHttpServletRequest
     * @param name String
     * @param password String
     */
    public static final void setRequestBasicAuth(TestHttpServletRequest req, String name, String password) {
        String authString = name + ":" + password;
        byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
        String authStringEnc = new String(authEncBytes);
        req.setAuthType(BASIC_AUTH);
        req.addHeader("Authorization", "Basic " + authStringEnc);
    }

    /**
     * Method getAsyncContext.
     * @return AsyncContext
     * @see javax.servlet.ServletRequest#getAsyncContext()
     */
    @Override
    public AsyncContext getAsyncContext() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Method getDispatcherType.
     * @return DispatcherType
     * @see javax.servlet.ServletRequest#getDispatcherType()
     */
    @Override
    public DispatcherType getDispatcherType() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Method getServletContext.
     * @return ServletContext
     * @see javax.servlet.ServletRequest#getServletContext()
     */
    @Override
    public ServletContext getServletContext() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Method isAsyncStarted.
     * @return boolean
     * @see javax.servlet.ServletRequest#isAsyncStarted()
     */
    @Override
    public boolean isAsyncStarted() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Method isAsyncSupported.
     * @return boolean
     * @see javax.servlet.ServletRequest#isAsyncSupported()
     */
    @Override
    public boolean isAsyncSupported() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Method startAsync.
     * @return AsyncContext
     * @throws IllegalStateException
     * @see javax.servlet.ServletRequest#startAsync()
     */
    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Method startAsync.
     * @param arg0 ServletRequest
     * @param arg1 ServletResponse
     * @return AsyncContext
     * @throws IllegalStateException
     * @see javax.servlet.ServletRequest#startAsync(ServletRequest, ServletResponse)
     */
    @Override
    public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Method authenticate.
     * @param arg0 HttpServletResponse
     * @return boolean
     * @throws IOException
     * @throws ServletException
     * @see javax.servlet.http.HttpServletRequest#authenticate(HttpServletResponse)
     */
    @Override
    public boolean authenticate(HttpServletResponse arg0) throws IOException, ServletException {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Method getPart.
     * @param arg0 String
     * @return Part
     * @throws IOException
     * @throws ServletException
     * @see javax.servlet.http.HttpServletRequest#getPart(String)
     */
    @Override
    public Part getPart(String arg0) throws IOException, ServletException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Method getParts.
     * @return Collection<Part>
     * @throws IOException
     * @throws ServletException
     * @see javax.servlet.http.HttpServletRequest#getParts()
     */
    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Method login.
     * @param arg0 String
     * @param arg1 String
     * @throws ServletException
     * @see javax.servlet.http.HttpServletRequest#login(String, String)
     */
    @Override
    public void login(String arg0, String arg1) throws ServletException {
        // TODO Auto-generated method stub

    }

    /**
     * Method logout.
     * @throws ServletException
     * @see javax.servlet.http.HttpServletRequest#logout()
     */
    @Override
    public void logout() throws ServletException {
        // TODO Auto-generated method stub

    }
}

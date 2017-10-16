package com.idfconnect.ssorest.common.test.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class TestServletContext implements ServletContext {
    private Hashtable<String, Object> attrs;
    private Hashtable<String, String> initParams;
    private String                    contextPath;
    private File                      realPath;
    private Logger                    logger;

    /**
     * Constructor for TestServletContext.
     * @param contextPath String
     * @param realPath File
     */
    public TestServletContext(String contextPath, File realPath) {
        logger = LoggerFactory.getLogger(getClass());
        attrs = new Hashtable<String, Object>();
        initParams = new Hashtable<String, String>();
        this.contextPath = contextPath;
        this.realPath = realPath;
        if (!realPath.isDirectory())
            throw new IllegalArgumentException("Real Path is not a directory: " + realPath);
    }

    /**
     * Method addFilter.
     * @param filterName String
     * @param arg1 String
     * @return Dynamic
     * @see javax.servlet.ServletContext#addFilter(String, String)
     */
    @Override
    public Dynamic addFilter(String filterName, String arg1) {
        return null;
    }

    /**
     * Method addFilter.
     * @param filterName String
     * @param f Filter
     * @return Dynamic
     * @see javax.servlet.ServletContext#addFilter(String, Filter)
     */
    @Override
    public Dynamic addFilter(String filterName, Filter f) {
        return null;
    }

    /**
     * Method addFilter.
     * @param filterName String
     * @param f Class<? extends Filter>
     * @return Dynamic
     */
    @Override
    public Dynamic addFilter(String filterName, Class<? extends Filter> f) {
        return null;
    }

    /**
     * Method addListener.
     * @param className String
     * @see javax.servlet.ServletContext#addListener(String)
     */
    @Override
    public void addListener(String className) {
    }

    /**
     * Method addListener.
     * @param t T
     * @see javax.servlet.ServletContext#addListener(T)
     */
    @Override
    public <T extends EventListener> void addListener(T t) {
    }

    /**
     * Method addListener.
     * @param arg0 Class<? extends EventListener>
     */
    @Override
    public void addListener(Class<? extends EventListener> arg0) {
    }

    /**
     * Method addServlet.
     * @param name String
     * @param arg1 String
     * @return javax.servlet.ServletRegistration.Dynamic
     */
    @Override
    public javax.servlet.ServletRegistration.Dynamic addServlet(String name, String arg1) {
        return null;
    }

    /**
     * Method addServlet.
     * @param servletName String
     * @param servlet Servlet
     * @return javax.servlet.ServletRegistration.Dynamic
     */
    @Override
    public javax.servlet.ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        return null;
    }

    /**
     * Method addServlet.
     * @param servletName String
     * @param servletClass Class<? extends Servlet>
     * @return javax.servlet.ServletRegistration.Dynamic
     */
    @Override
    public javax.servlet.ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return null;
    }

    /**
     * Method createFilter.
     * @param arg0 Class<T>
     * @return T
     * @throws ServletException
     */
    @Override
    public <T extends Filter> T createFilter(Class<T> arg0) throws ServletException {
        return null;
    }

    /**
     * Method createListener.
     * @param arg0 Class<T>
     * @return T
     * @throws ServletException
     */
    @Override
    public <T extends EventListener> T createListener(Class<T> arg0) throws ServletException {
        return null;
    }

    /**
     * Method createServlet.
     * @param arg0 Class<T>
     * @return T
     * @throws ServletException
     */
    @Override
    public <T extends Servlet> T createServlet(Class<T> arg0) throws ServletException {
        return null;
    }

    /**
     * Method declareRoles.
     * @param roles String[]
     * @see javax.servlet.ServletContext#declareRoles(String[])
     */
    @Override
    public void declareRoles(String... roles) {

    }

    /**
     * Method getAttribute.
     * @param name String
     * @return Object
     * @see javax.servlet.ServletContext#getAttribute(String)
     */
    @Override
    public Object getAttribute(String name) {
        return attrs.get(name);
    }

    /**
     * Method getAttributeNames.
     * @return Enumeration<String>
     * @see javax.servlet.ServletContext#getAttributeNames()
     */
    @Override
    public Enumeration<String> getAttributeNames() {
        return attrs.keys();
    }

    /**
     * Method getClassLoader.
     * @return ClassLoader
     * @see javax.servlet.ServletContext#getClassLoader()
     */
    @Override
    public ClassLoader getClassLoader() {
        return getClass().getClassLoader();
    }

    /**
     * Method getContext.
     * @param uri String
     * @return ServletContext
     * @see javax.servlet.ServletContext#getContext(String)
     */
    @Override
    public ServletContext getContext(String uri) {
        return null;
    }

    /**
     * Method getContextPath.
     * @return String
     * @see javax.servlet.ServletContext#getContextPath()
     */
    @Override
    public String getContextPath() {
        return contextPath;
    }

    /**
     * Method getDefaultSessionTrackingModes.
     * @return Set<SessionTrackingMode>
     * @see javax.servlet.ServletContext#getDefaultSessionTrackingModes()
     */
    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return null;
    }

    /**
     * Method getEffectiveMajorVersion.
     * @return int
     * @see javax.servlet.ServletContext#getEffectiveMajorVersion()
     */
    @Override
    public int getEffectiveMajorVersion() {
        return 5;
    }

    /**
     * Method getEffectiveMinorVersion.
     * @return int
     * @see javax.servlet.ServletContext#getEffectiveMinorVersion()
     */
    @Override
    public int getEffectiveMinorVersion() {
        return 2;
    }

    /**
     * Method getEffectiveSessionTrackingModes.
     * @return Set<SessionTrackingMode>
     * @see javax.servlet.ServletContext#getEffectiveSessionTrackingModes()
     */
    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return null;
    }

    /**
     * Method getFilterRegistration.
     * @param name String
     * @return FilterRegistration
     * @see javax.servlet.ServletContext#getFilterRegistration(String)
     */
    @Override
    public FilterRegistration getFilterRegistration(String name) {
        return null;
    }

    /**
     * Method getFilterRegistrations.
     * @return Map<String,? extends FilterRegistration>
     * @see javax.servlet.ServletContext#getFilterRegistrations()
     */
    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return null;
    }

    /**
     * Method getInitParameter.
     * @param name String
     * @return String
     * @see javax.servlet.ServletContext#getInitParameter(String)
     */
    @Override
    public String getInitParameter(String name) {
        return initParams.get(name);
    }

    /**
     * Method getInitParameterNames.
     * @return Enumeration<String>
     * @see javax.servlet.ServletContext#getInitParameterNames()
     */
    @Override
    public Enumeration<String> getInitParameterNames() {
        return initParams.keys();
    }

    /**
     * Method getJspConfigDescriptor.
     * @return JspConfigDescriptor
     * @see javax.servlet.ServletContext#getJspConfigDescriptor()
     */
    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        return null;
    }

    /**
     * Method getMajorVersion.
     * @return int
     * @see javax.servlet.ServletContext#getMajorVersion()
     */
    @Override
    public int getMajorVersion() {
        return 3;
    }

    /**
     * Method getMimeType.
     * @param filename String
     * @return String
     * @see javax.servlet.ServletContext#getMimeType(String)
     */
    @Override
    public String getMimeType(String filename) {
        String str = filename.toLowerCase();
        if (str.endsWith(".txt"))
            return "text/plain";
        if (str.endsWith(".gif"))
            return "image/gif";
        if (str.endsWith(".jpg") || str.endsWith(".jpeg"))
            return "image/jpeg";
        return "text/html";
    }

    /**
     * Method getMinorVersion.
     * @return int
     * @see javax.servlet.ServletContext#getMinorVersion()
     */
    @Override
    public int getMinorVersion() {
        return 0;
    }

    /**
     * Method getNamedDispatcher.
     * @param name String
     * @return RequestDispatcher
     * @see javax.servlet.ServletContext#getNamedDispatcher(String)
     */
    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        return null;
    }

    /**
     * Method getRealPath.
     * @param path String
     * @return String
     * @see javax.servlet.ServletContext#getRealPath(String)
     */
    @Override
    public String getRealPath(String path) {
        if (path.startsWith(contextPath + '/'))
            path = path.substring(contextPath.length() + 1);
        return new File(realPath, path).getAbsolutePath();
    }

    /**
     * Method getRequestDispatcher.
     * @param path String
     * @return RequestDispatcher
     * @see javax.servlet.ServletContext#getRequestDispatcher(String)
     */
    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    /**
     * Method getResource.
     * @param path String
     * @return URL
     * @throws MalformedURLException
     * @see javax.servlet.ServletContext#getResource(String)
     */
    @Override
    public URL getResource(String path) throws MalformedURLException {
        return new URL(getRealPath(path));
    }

    /**
     * Method getResourceAsStream.
     * @param path String
     * @return InputStream
     * @see javax.servlet.ServletContext#getResourceAsStream(String)
     */
    @Override
    public InputStream getResourceAsStream(String path) {
        try {
            return new FileInputStream(getRealPath(path));
        } catch (FileNotFoundException e) {
            logger.error(e.toString());
            return null;
        }
    }

    /**
     * Method getResourcePaths.
     * @param path String
     * @return Set<String>
     * @see javax.servlet.ServletContext#getResourcePaths(String)
     */
    @Override
    public Set<String> getResourcePaths(String path) {
        return null;
    }

    /**
     * Method getServerInfo.
     * @return String
     * @see javax.servlet.ServletContext#getServerInfo()
     */
    @Override
    public String getServerInfo() {
        return getClass().getSimpleName() + "/1.0";
    }

    /**
     * Method getServlet.
     * @param arg0 String
     * @return Servlet
     * @throws ServletException
     * @see javax.servlet.ServletContext#getServlet(String)
     */
    @Override
    @Deprecated
    public Servlet getServlet(String arg0) throws ServletException {
        return null;
    }

    /**
     * Method getServletContextName.
     * @return String
     * @see javax.servlet.ServletContext#getServletContextName()
     */
    @Override
    public String getServletContextName() {
        return getClass().getName();
    }

    /**
     * Method getServletNames.
     * @return Enumeration<String>
     * @see javax.servlet.ServletContext#getServletNames()
     */
    @Override
    @Deprecated
    public Enumeration<String> getServletNames() {
        return null;
    }

    /**
     * Method getServletRegistration.
     * @param servletName String
     * @return ServletRegistration
     * @see javax.servlet.ServletContext#getServletRegistration(String)
     */
    @Override
    public ServletRegistration getServletRegistration(String servletName) {
        return null;
    }

    /**
     * Method getServletRegistrations.
     * @return Map<String,? extends ServletRegistration>
     * @see javax.servlet.ServletContext#getServletRegistrations()
     */
    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return new HashMap<String,ServletRegistration>();
    }

    /**
     * Method getServlets.
     * @return Enumeration<Servlet>
     * @see javax.servlet.ServletContext#getServlets()
     */
    @Override
    @Deprecated
    public Enumeration<Servlet> getServlets() {
        return null;
    }

    /**
     * Method getSessionCookieConfig.
     * @return SessionCookieConfig
     * @see javax.servlet.ServletContext#getSessionCookieConfig()
     */
    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return null;
    }

    /**
     * Method log.
     * @param msg String
     * @see javax.servlet.ServletContext#log(String)
     */
    @Override
    public void log(String msg) {
        logger.info(msg);
    }

    /**
     * Method log.
     * @param arg0 Exception
     * @param arg1 String
     * @see javax.servlet.ServletContext#log(Exception, String)
     */
    @Override
    @Deprecated
    public void log(Exception arg0, String arg1) {

    }

    /**
     * Method log.
     * @param msg String
     * @param t Throwable
     * @see javax.servlet.ServletContext#log(String, Throwable)
     */
    @Override
    public void log(String msg, Throwable t) {
        logger.error(msg, t);
    }

    /**
     * Method removeAttribute.
     * @param name String
     * @see javax.servlet.ServletContext#removeAttribute(String)
     */
    @Override
    public void removeAttribute(String name) {
        attrs.remove(name);
    }

    /**
     * Method setAttribute.
     * @param name String
     * @param value Object
     * @see javax.servlet.ServletContext#setAttribute(String, Object)
     */
    @Override
    public void setAttribute(String name, Object value) {
        attrs.put(name, value);
    }

    /**
     * Method setInitParameter.
     * @param name String
     * @param value String
     * @return boolean
     * @see javax.servlet.ServletContext#setInitParameter(String, String)
     */
    @Override
    public boolean setInitParameter(String name, String value) {
        if (initParams.containsKey(name))
            return false;
        initParams.put(name, value);
        return true;
    }

    /**
     * Method setSessionTrackingModes.
     * @param arg0 Set<SessionTrackingMode>
     */
    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> arg0) {

    }
}

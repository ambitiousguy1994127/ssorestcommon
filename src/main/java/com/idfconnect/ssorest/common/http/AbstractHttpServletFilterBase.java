package com.idfconnect.ssorest.common.http;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.idfconnect.ssorest.common.utils.ConfigProperties;
import com.idfconnect.ssorest.common.utils.ConfigPropertiesFactory;

/**
 * An abstract Servlet Filter implementation which supplies some convenience methods
 *
 * @author Richard Sand
 * @since 1.4
 */
public abstract class AbstractHttpServletFilterBase implements Filter {
    /** Config property constant for use with providing the configuration file name */
    public static final String CONFIG_FILENAME_PROP          = "configFileName";

    /** Config property constant for use with enabling the file configuration capability */
    public static final String USE_FILE_CONFIG_PROVIDER_PROP = "useFileConfigProvider";

    private FilterConfig       config                        = null;
    private ConfigProperties   parameters;
    private Logger             logger                        = LoggerFactory.getLogger(getClass());

    /**
     * <p>
     * Constructor for AbstractHttpServletFilterBase.
     * </p>
     *
     * @since 1.4
     */
    public AbstractHttpServletFilterBase() {
    }

    /**
     * {@inheritDoc}
     *
     * Method doFilter.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws IOException, ServletException {
        if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse))
            doHttpFilter((HttpServletRequest) request, (HttpServletResponse) response, fc);
        else
            throw new ServletException("The request and response objects provided were not HTTP objects");
    }

    /**
     * Overwrite this method to implement a filter with HttpServletRequest and HttpServletResponse objects
     *
     * @param request
     *            a {@link javax.servlet.http.HttpServletRequest} object.
     * @param response
     *            a {@link javax.servlet.http.HttpServletResponse} object.
     * @param fc
     *            a {@link javax.servlet.FilterChain} object.
     * @throws java.io.IOException
     *             if any.
     * @throws javax.servlet.ServletException
     *             if any.
     * @since 1.4
     */
    public abstract void doHttpFilter(HttpServletRequest request, HttpServletResponse response, FilterChain fc) throws IOException, ServletException;

    /**
     * {@inheritDoc}
     *
     * Method init.
     */
    @Override
    public final void init(FilterConfig fc) throws ServletException {
        logger.trace("Inside init()");
        this.config = fc;
        loadParameters();
        initialize();
    }

    /**
     * Order of precedence file->system—>context—>filter
     *
     * @since 3.0
     */
    protected void loadParameters() {
        HashMap<String, String> localparams = new HashMap<String,String>();

        // First load the filter properties - lowest precedence
        Enumeration<String> names = config.getInitParameterNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                String value = config.getInitParameter(name);
                logger.trace("Loaded filter parameter [filterParam={}][value={}]", name, value);
                localparams.put(name, value);
            }
        }

        // Next load the context properties
        ServletContext ctx = config.getServletContext();
        String prefix = getClass().getName() + ".";
        names = config.getServletContext().getInitParameterNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                if (name.startsWith(prefix)) {
                    String value = ctx.getInitParameter(name);
                    String shortname = name.substring(prefix.length());
                    String old = localparams.put(shortname, value);
                    logger.trace("Setting parameter with context parameter [name={}][contextParam={}][newValue={}][oldValue={}]", shortname, name, value, old);
                }
            }
        }
        
        parameters = ConfigPropertiesFactory.initFromEverywhere(localparams, prefix);
    }

    /**
     * This method replaces the Filter interface <code>init</code> method
     *
     * @throws javax.servlet.ServletException
     *             if any.
     * @since 1.4
     */
    protected abstract void initialize() throws ServletException;

    /**
     * Returns the FilterConfig object used to initialize the filter
     *
     * @return FilterConfig
     * @since 1.4
     */
    protected final FilterConfig getFilterConfig() {
        return config;
    }

    /**
     * Method getConfigParameter.
     *
     * @param name
     *            String
     * @return String
     * @since 1.4
     */
    protected final String getConfigParameter(String name) {
        return parameters.getPropertyAsString(name);
    }

    /**
     * Method getConfigParameter.
     *
     * @param name
     *            String
     * @param defaultValue
     *            String
     * @return String
     * @since 1.4
     */
    protected final String getConfigParameter(String name, String defaultValue) {
        if (getConfigParameter(name) != null)
            return getConfigParameter(name);
        return defaultValue;
    }

    /**
     * Returns the request parameter name as an integer. If the parameter is not defined or is not an integer, returns the default value of -1
     *
     * @param name
     *            the case-insensitive parameter name
     * @return int
     * @since 1.4
     */
    protected final int getConfigParameterAsInt(String name) {
        return getConfigParameterAsInt(name, -1);
    }

    /**
     * Returns the request parameter name as an integer. If the parameter is not defined or is not an integer, returns the provided default value
     *
     * @param name
     *            the case-insensitive parameter name
     * @param defaultValue
     *            int
     * @return int
     * @since 1.4
     */
    protected final int getConfigParameterAsInt(String name, int defaultValue) {
        int val = defaultValue;
        String str = getConfigParameter(name);
        try {
            val = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            logger.trace("NumberFormatException {} = {}, returning default {}", name, str, val);
        }
        return val;
    }

    /**
     * Returns the request parameter name as a long. If the parameter is not defined or is not a long, returns the default value of -1
     *
     * @param name
     *            the case-insensitive parameter name
     * @return long
     * @since 1.4
     */
    protected final long getConfigParameterAsLong(String name) {
        return getConfigParameterAsLong(name, -1);
    }

    /**
     * Returns the request parameter name as a long. If the parameter is not defined or is not a long, returns the provided default value
     *
     * @param name
     *            the case-insensitive parameter name
     * @param defaultValue
     *            long
     * @return long
     * @since 1.4
     */
    protected final long getConfigParameterAsLong(String name, long defaultValue) {
        long val = defaultValue;
        String str = getConfigParameter(name);
        try {
            val = Long.parseLong(str);
        } catch (NumberFormatException nfe) {
            logger.trace("NumberFormatException {} = {}, returning default {}", name, str, val);
        }
        return val;
    }

    /**
     * Returns the request parameter name as a boolean. If the parameter is not defined or is not a boolean, returns false
     *
     * @param name
     *            the case-insensitive parameter name
     * @return boolean
     * @since 1.4
     */
    protected final boolean getConfigParameterAsBoolean(String name) {
        return getConfigParameterAsBoolean(name, false);
    }

    /**
     * Returns the request parameter name as a boolean. If the parameter is not defined, returns the provided default value
     *
     * @param name
     *            the case-insensitive parameter name
     * @param defaultValue
     *            boolean
     * @return boolean
     * @since 1.4
     */
    protected final boolean getConfigParameterAsBoolean(String name, boolean defaultValue) {
        boolean val = defaultValue;
        String str = getConfigParameter(name);
        if (str != null)
            val = Boolean.parseBoolean(str);
        return val;
    }

    /**
     * <p>
     * Getter for the field <code>parameters</code>.
     * </p>
     *
     * @return a {@link java.util.HashMap} object.
     * @since 1.4
     */
    public final ConfigProperties getParameters() {
        return parameters;
    }
}

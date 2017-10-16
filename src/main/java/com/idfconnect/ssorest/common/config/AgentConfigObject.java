package com.idfconnect.ssorest.common.config;

/**
 * <p>AgentConfigObject class.</p>
 *
 * @author nghia
 */
public class AgentConfigObject {
    private String acoName;
    private String acoDesc;
    private String agentName;
    private String defaultAgentName;
    private String cookieDomain;
    private String cookieDomainScope;
    private String cookieProvider;
    private String logFileName;
    private String logLevel;

    /**
     * <p>Getter for the field <code>acoName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getAcoName() {
        return acoName;
    }

    /**
     * <p>Setter for the field <code>acoName</code>.</p>
     *
     * @param acoName a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setAcoName(String acoName) {
        this.acoName = acoName;
    }

    /**
     * <p>Getter for the field <code>acoDesc</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getAcoDesc() {
        return acoDesc;
    }

    /**
     * <p>Setter for the field <code>acoDesc</code>.</p>
     *
     * @param acoDesc a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setAcoDesc(String acoDesc) {
        this.acoDesc = acoDesc;
    }

    /**
     * <p>Getter for the field <code>agentName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getAgentName() {
        return agentName;
    }

    /**
     * <p>Setter for the field <code>agentName</code>.</p>
     *
     * @param agentName a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    /**
     * <p>Getter for the field <code>defaultAgentName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getDefaultAgentName() {
        return defaultAgentName;
    }

    /**
     * <p>Setter for the field <code>defaultAgentName</code>.</p>
     *
     * @param defaultAgentName a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setDefaultAgentName(String defaultAgentName) {
        this.defaultAgentName = defaultAgentName;
    }

    /**
     * <p>Getter for the field <code>cookieDomain</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getCookieDomain() {
        return cookieDomain;
    }

    /**
     * <p>Setter for the field <code>cookieDomain</code>.</p>
     *
     * @param cookieDomain a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setCookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    /**
     * <p>Getter for the field <code>cookieDomainScope</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getCookieDomainScope() {
        return cookieDomainScope;
    }

    /**
     * <p>Setter for the field <code>cookieDomainScope</code>.</p>
     *
     * @param cookieDomainScope a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setCookieDomainScope(String cookieDomainScope) {
        this.cookieDomainScope = cookieDomainScope;
    }

    /**
     * <p>Getter for the field <code>cookieProvider</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getCookieProvider() {
        return cookieProvider;
    }

    /**
     * <p>Setter for the field <code>cookieProvider</code>.</p>
     *
     * @param cookieProvider a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setCookieProvider(String cookieProvider) {
        this.cookieProvider = cookieProvider;
    }

    /**
     * <p>Getter for the field <code>logFileName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getLogFileName() {
        return logFileName;
    }

    /**
     * <p>Setter for the field <code>logFileName</code>.</p>
     *
     * @param logFileName a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    /**
     * <p>Getter for the field <code>logLevel</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getLogLevel() {
        return logLevel;
    }

    /**
     * <p>Setter for the field <code>logLevel</code>.</p>
     *
     * @param logLevel a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "[ACO{"
                + "acoName: " + acoName + " ,\n"
                + "acoDec: " + acoDesc + " ,\n"
                + "AgentName: " + agentName + " ,\n"
                + "DefaultAgentName: " + defaultAgentName + " ,\n"
                + "CookieDomain: " + cookieDomain + " ,\n"
                + "CookieDomainScope: " + cookieDomainScope + " ,\n"
                + "CookieProvider: " + cookieProvider + " ,\n"
                + "LogFileName: " + logFileName + " ,\n"
                + "LogLevel: " + logLevel + " \n"
                + "}]";

    }
}

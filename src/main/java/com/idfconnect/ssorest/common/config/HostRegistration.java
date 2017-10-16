package com.idfconnect.ssorest.common.config;

/**
 * <p>HostRegistration class.</p>
 *
 * @author nghia
 */
public class HostRegistration {
    private String serverAddress;
    private String adminAccount;
    private String adminPassword;
    private String trustedHostName;
    private String hco;
    private String fipsMode;
    private String fileName;

    /**
     * <p>Getter for the field <code>serverAddress</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * <p>Setter for the field <code>serverAddress</code>.</p>
     *
     * @param serverAddress a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    /**
     * <p>Getter for the field <code>adminAccount</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getAdminAccount() {
        return adminAccount;
    }

    /**
     * <p>Setter for the field <code>adminAccount</code>.</p>
     *
     * @param adminAccount a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setAdminAccount(String adminAccount) {
        this.adminAccount = adminAccount;
    }

    /**
     * <p>Getter for the field <code>adminPassword</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getAdminPassword() {
        return adminPassword;
    }

    /**
     * <p>Setter for the field <code>adminPassword</code>.</p>
     *
     * @param adminPassword a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    /**
     * <p>Getter for the field <code>trustedHostName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getTrustedHostName() {
        return trustedHostName;
    }

    /**
     * <p>Setter for the field <code>trustedHostName</code>.</p>
     *
     * @param trustedHostName a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setTrustedHostName(String trustedHostName) {
        this.trustedHostName = trustedHostName;
    }

    /**
     * <p>Getter for the field <code>hco</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getHco() {
        return hco;
    }

    /**
     * <p>Setter for the field <code>hco</code>.</p>
     *
     * @param hco a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setHco(String hco) {
        this.hco = hco;
    }

    /**
     * <p>Getter for the field <code>fipsMode</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getFipsMode() {
        return fipsMode;
    }

    /**
     * <p>Setter for the field <code>fipsMode</code>.</p>
     *
     * @param fipsMode a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setFipsMode(String fipsMode) {
        this.fipsMode = fipsMode;
    }

    /**
     * <p>Getter for the field <code>fileName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * <p>Setter for the field <code>fileName</code>.</p>
     *
     * @param fileName a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

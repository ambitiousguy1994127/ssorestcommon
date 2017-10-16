package com.idfconnect.ssorest.common.config;

/**
 * <p>Policies class.</p>
 *
 * @author nghia
 */
public class Policies {
    private String[] domains;
    private String[] directories;

    /**
     * <p>Getter for the field <code>domains</code>.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     * @since 3.0.3
     */
    public String[] getDomains() {
        return domains;
    }

    /**
     * <p>Setter for the field <code>domains</code>.</p>
     *
     * @param domains an array of {@link java.lang.String} objects.
     * @since 3.0.3
     */
    public void setDomains(String[] domains) {
        this.domains = domains;
    }

    /**
     * <p>Getter for the field <code>directories</code>.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     * @since 3.0.3
     */
    public String[] getDirectories() {
        return directories;
    }

    /**
     * <p>Setter for the field <code>directories</code>.</p>
     *
     * @param directories an array of {@link java.lang.String} objects.
     * @since 3.0.3
     */
    public void setDirectories(String[] directories) {
        this.directories = directories;
    }
}

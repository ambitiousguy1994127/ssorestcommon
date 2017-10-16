package com.idfconnect.ssorest.common.config;

/**
 * <p>DomainPolicy class.</p>
 *
 * @author nghia
 */
public class DomainPolicy {
    private String name;
    private String desc;
    private String[] dirs;

    private RealmPolicy realm;
    private PolicyLink  policy;

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>desc</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getDesc() {
        return desc;
    }

    /**
     * <p>Setter for the field <code>desc</code>.</p>
     *
     * @param desc a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * <p>Getter for the field <code>realm</code>.</p>
     *
     * @return a {@link com.idfconnect.ssorest.common.config.RealmPolicy} object.
     * @since 3.0.3
     */
    public RealmPolicy getRealm() {
        return realm;
    }

    /**
     * <p>Setter for the field <code>realm</code>.</p>
     *
     * @param realm a {@link com.idfconnect.ssorest.common.config.RealmPolicy} object.
     * @since 3.0.3
     */
    public void setRealm(RealmPolicy realm) {
        this.realm = realm;
    }

    /**
     * <p>Getter for the field <code>dirs</code>.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public String[] getDirs() {
        return dirs;
    }

    /**
     * <p>Setter for the field <code>dirs</code>.</p>
     *
     * @param dirs an array of {@link java.lang.String} objects.
     */
    public void setDirs(String[] dirs) {
        this.dirs = dirs;
    }

    /**
     * <p>Getter for the field <code>policy</code>.</p>
     *
     * @return a {@link com.idfconnect.ssorest.common.config.PolicyLink} object.
     */
    public PolicyLink getPolicy() {
        return policy;
    }

    /**
     * <p>Setter for the field <code>policy</code>.</p>
     *
     * @param policy a {@link com.idfconnect.ssorest.common.config.PolicyLink} object.
     */
    public void setPolicy(PolicyLink policy) {
        this.policy = policy;
    }
}

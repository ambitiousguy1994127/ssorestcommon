package com.idfconnect.ssorest.common.config;


/**
 * <p>RulePolicy class.</p>
 *
 * @author nghia
 */
public class RulePolicy {

    private String name;
    private String desc;

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
}

package com.idfconnect.ssorest.common.config;


/**
 * <p>RealmPolicy class.</p>
 *
 * @author nghia
 */
public class RealmPolicy {
    private String     name;
    private String     desc;
    private RulePolicy rule;

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
     * <p>Getter for the field <code>rule</code>.</p>
     *
     * @return a {@link com.idfconnect.ssorest.common.config.RulePolicy} object.
     * @since 3.0.3
     */
    public RulePolicy getRule() {
        return rule;
    }

    /**
     * <p>Setter for the field <code>rule</code>.</p>
     *
     * @param rule a {@link com.idfconnect.ssorest.common.config.RulePolicy} object.
     * @since 3.0.3
     */
    public void setRule(RulePolicy rule) {
        this.rule = rule;
    }
}

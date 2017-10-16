package com.idfconnect.ssorest.common.config;

/**
 * <p>AgentInstance class.</p>
 *
 * @author nghia
 */
public class AgentInstance {
    private String name;
    private String desc;
    private String groupName;
    private String groupDesc;

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
     * <p>Getter for the field <code>groupName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * <p>Setter for the field <code>groupName</code>.</p>
     *
     * @param groupName a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
     * <p>Getter for the field <code>groupDesc</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getGroupDesc() {
        return groupDesc;
    }

    /**
     * <p>Setter for the field <code>groupDesc</code>.</p>
     *
     * @param groupDesc a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "[ AgentInstance {"
                + "name: " + name + " ,\n"
                + "desc: " + desc + " ,\n"
                + "groupName: " + groupName + " ,\n"
                + "groupDesc: " + groupDesc + " \n"
                + "}]";

    }
}

package com.idfconnect.ssorest.common.config;


/**
 * <p>FrontendAgent class.</p>
 *
 * @author nghia
 */
public class FrontendAgent {
    private AgentInstance     agentInstance;
    private AgentConfigObject aco;
    private DomainPolicy      domain;

    /**
     * <p>Getter for the field <code>agentInstance</code>.</p>
     *
     * @return a {@link com.idfconnect.ssorest.common.config.AgentInstance} object.
     * @since 3.0.3
     */
    public AgentInstance getAgentInstance() {
        return agentInstance;
    }

    /**
     * <p>Setter for the field <code>agentInstance</code>.</p>
     *
     * @param agentInstance a {@link com.idfconnect.ssorest.common.config.AgentInstance} object.
     * @since 3.0.3
     */
    public void setAgentInstance(AgentInstance agentInstance) {
        this.agentInstance = agentInstance;
    }

    /**
     * <p>Getter for the field <code>aco</code>.</p>
     *
     * @return a {@link com.idfconnect.sm.agent.config.AgentConfigObject} object.
     * @since 3.0.3
     */
    public AgentConfigObject getAco() {
        return aco;
    }

    /**
     * <p>Setter for the field <code>aco</code>.</p>
     *
     * @param aco a {@link com.idfconnect.sm.agent.config.AgentConfigObject} object.
     * @since 3.0.3
     */
    public void setAco(AgentConfigObject aco) {
        this.aco = aco;
    }

    /**
     * <p>Getter for the field <code>domain</code>.</p>
     *
     * @return a {@link com.idfconnect.ssorest.common.config.DomainPolicy} object.
     * @since 3.0.3
     */
    public DomainPolicy getDomain() {
        return domain;
    }

    /**
     * <p>Setter for the field <code>domain</code>.</p>
     *
     * @param domain a {@link com.idfconnect.ssorest.common.config.DomainPolicy} object.
     * @since 3.0.3
     */
    public void setDomain(DomainPolicy domain) {
        this.domain = domain;
    }
}

package com.idfconnect.ssorest.common.config;

import com.idfconnect.ssorest.common.json.JsonPojo;

/**
 * <p>GatewaySetting class.</p>
 *
 * @author nghia
 */
public class GatewaySetting implements JsonPojo{
    private String            webAgentConfFile;
    private String            option;
    private String            gatewayUrl;
    private HostRegistration  hostRegistration;
    private AgentConfigObject aco;
    private AgentInstance     agentInstance;
    private FrontendAgent     frontendAgent;
    private DomainPolicy      domain;
    private Policies          policies;

    /**
     * <p>Getter for the field <code>webAgentConfFile</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getWebAgentConfFile() {
        return webAgentConfFile;
    }

    /**
     * <p>Setter for the field <code>webAgentConfFile</code>.</p>
     *
     * @param webAgentConfFile a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setWebAgentConfFile(String webAgentConfFile) {
        this.webAgentConfFile = webAgentConfFile;
    }

    /**
     * <p>Getter for the field <code>option</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getOption() {
        return option;
    }

    /**
     * <p>Setter for the field <code>option</code>.</p>
     *
     * @param option a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setOption(String option) {
        this.option = option;
    }

    /**
     * <p>Getter for the field <code>gatewayUrl</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getGatewayUrl() {
        return gatewayUrl;
    }

    /**
     * <p>Setter for the field <code>gatewayUrl</code>.</p>
     *
     * @param gatewayUrl a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    /**
     * <p>Getter for the field <code>hostRegistration</code>.</p>
     *
     * @return a {@link com.idfconnect.ssorest.common.config.HostRegistration} object.
     * @since 3.0.3
     */
    public HostRegistration getHostRegistration() {
        return hostRegistration;
    }

    /**
     * <p>Setter for the field <code>hostRegistration</code>.</p>
     *
     * @param hostRegistration a {@link com.idfconnect.ssorest.common.config.HostRegistration} object.
     * @since 3.0.3
     */
    public void setHostRegistration(HostRegistration hostRegistration) {
        this.hostRegistration = hostRegistration;
    }

    /**
     * <p>Getter for the field <code>aco</code>.</p>
     *
     * @return a {@link com.idfconnect.ssorest.common.config.AgentConfigObject} object.
     * @since 3.0.3
     */
    public AgentConfigObject getAco() {
        return aco;
    }

    /**
     * <p>Setter for the field <code>aco</code>.</p>
     *
     * @param aco a {@link com.idfconnect.ssorest.common.config.AgentConfigObject} object.
     * @since 3.0.3
     */
    public void setAco(AgentConfigObject aco) {
        this.aco = aco;
    }

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
     * <p>Getter for the field <code>frontendAgent</code>.</p>
     *
     * @return a {@link com.idfconnect.ssorest.common.config.FrontendAgent} object.
     * @since 3.0.3
     */
    public FrontendAgent getFrontendAgent() {
        return frontendAgent;
    }

    /**
     * <p>Setter for the field <code>frontendAgent</code>.</p>
     *
     * @param frontendAgent a {@link com.idfconnect.ssorest.common.config.FrontendAgent} object.
     * @since 3.0.3
     */
    public void setFrontendAgent(FrontendAgent frontendAgent) {
        this.frontendAgent = frontendAgent;
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

    /**
     * <p>Getter for the field <code>policies</code>.</p>
     *
     * @return a {@link com.idfconnect.ssorest.common.config.Policies} object.
     */
    public Policies getPolicies() {
        return policies;
    }

    /**
     * <p>Setter for the field <code>policies</code>.</p>
     *
     * @param policies a {@link com.idfconnect.ssorest.common.config.Policies} object.
     */
    public void setPolicies(Policies policies) {
        this.policies = policies;
    }
}

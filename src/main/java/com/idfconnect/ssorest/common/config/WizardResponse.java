package com.idfconnect.ssorest.common.config;

import com.idfconnect.ssorest.common.json.JsonPojo;

/**
 * <p>WizardResponse class.</p>
 *
 * @author nghia
 */
public class WizardResponse implements JsonPojo{
    private int            code;
    private String         errorMessage;
    private String         errorDescription;
    private GatewaySetting gatewaySetting;

    /**
     * <p>Getter for the field <code>code</code>.</p>
     *
     * @return a int.
     * @since 3.0.3
     */
    public int getCode() {
        return code;
    }

    /**
     * <p>Setter for the field <code>code</code>.</p>
     *
     * @param code a int.
     * @since 3.0.3
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * <p>Getter for the field <code>errorMessage</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * <p>Setter for the field <code>errorMessage</code>.</p>
     *
     * @param errorMessage a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * <p>Getter for the field <code>errorDescription</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * <p>Setter for the field <code>errorDescription</code>.</p>
     *
     * @param errorDescription a {@link java.lang.String} object.
     * @since 3.0.3
     */
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    /**
     * <p>Getter for the field <code>gatewaySetting</code>.</p>
     *
     * @return a {@link com.idfconnect.ssorest.common.config.GatewaySetting} object.
     * @since 3.0.3
     */
    public GatewaySetting getGatewaySetting() {
        return gatewaySetting;
    }

    /**
     * <p>Setter for the field <code>gatewaySetting</code>.</p>
     *
     * @param gatewaySetting a {@link com.idfconnect.ssorest.common.config.GatewaySetting} object.
     * @since 3.0.3
     */
    public void setGatewaySetting(GatewaySetting gatewaySetting) {
        this.gatewaySetting = gatewaySetting;
    }

}

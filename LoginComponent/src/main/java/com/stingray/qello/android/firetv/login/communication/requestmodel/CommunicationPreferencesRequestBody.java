package com.stingray.qello.android.firetv.login.communication.requestmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommunicationPreferencesRequestBody {
    private String sessionId;
    private String deviceId;
    private String clientId;
    // Jackson renames it to optIn because it's a boolean
    @JsonProperty("isOptIn")
    private Boolean isOptIn;
    private String languageTag;

    public CommunicationPreferencesRequestBody(String sessionId, String deviceId, Boolean isOptIn, String languageTag) {
        this.sessionId = sessionId;
        this.deviceId = deviceId;
        this.isOptIn = isOptIn;
        this.languageTag = languageTag;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getClientId() {
        return clientId;
    }

    public Boolean getIsOptIn() {
        return isOptIn;
    }

    public String getLanguageTag() {
        return languageTag;
    }
}

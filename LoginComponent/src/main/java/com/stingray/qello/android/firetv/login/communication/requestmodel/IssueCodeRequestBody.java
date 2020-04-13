package com.stingray.qello.android.firetv.login.communication.requestmodel;

public class IssueCodeRequestBody {
    private String sessionId;
    private String clientId;
    private String deviceId;
    private String redirectUri;

    public IssueCodeRequestBody(String sessionId, String deviceId) {
        this.sessionId = sessionId;
        this.deviceId = deviceId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}

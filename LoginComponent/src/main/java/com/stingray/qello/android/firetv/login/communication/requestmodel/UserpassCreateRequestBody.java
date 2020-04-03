package com.stingray.qello.android.firetv.login.communication.requestmodel;

public class UserpassCreateRequestBody {
    private String email;
    private String password;
    private String languageTag;
    private String clientId;
    private String deviceId;

    public UserpassCreateRequestBody(String email, String password, String languageTag, String deviceId) {
        this.email = email;
        this.password = password;
        this.languageTag = languageTag;
        this.deviceId = deviceId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getLanguageTag() {
        return languageTag;
    }

    public String getClientId() {
        return clientId;
    }

    public String getDeviceId() {
        return deviceId;
    }
}

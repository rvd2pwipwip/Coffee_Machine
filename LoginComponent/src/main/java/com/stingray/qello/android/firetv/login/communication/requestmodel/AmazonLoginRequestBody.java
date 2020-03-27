package com.stingray.qello.android.firetv.login.communication.requestmodel;

public class AmazonLoginRequestBody {
    private String amazonAccessToken;
    private String languageTag;
    private String clientId;
    private String setAmazonPasswordUri;
    private String deviceId;
    private String campaignId;
    private String utmParameters;

    public AmazonLoginRequestBody(String amazonAccessToken, String languageTag, String deviceId) {
        this.amazonAccessToken = amazonAccessToken;
        this.languageTag = languageTag;
        this.setAmazonPasswordUri = "https://login-test.stingray.com/createPasswordAndLink";
        this.deviceId = deviceId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAmazonAccessToken() {
        return amazonAccessToken;
    }

    public String getLanguageTag() {
        return languageTag;
    }

    public String getClientId() {
        return clientId;
    }

    public String getSetAmazonPasswordUri() {
        return setAmazonPasswordUri;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public String getUtmParameters() {
        return utmParameters;
    }
}

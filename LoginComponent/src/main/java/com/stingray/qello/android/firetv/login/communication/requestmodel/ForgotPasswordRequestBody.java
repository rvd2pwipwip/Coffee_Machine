package com.stingray.qello.android.firetv.login.communication.requestmodel;

public class ForgotPasswordRequestBody {
    private String email;
    private String clientId;
    private String languageTag;
    private String resetPasswordUri;

    public ForgotPasswordRequestBody(String email, String languageTag) {
        this.email = email;
        this.languageTag = languageTag;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setResetPasswordUri(String resetPasswordUri) {
        this.resetPasswordUri = resetPasswordUri;
    }

    public String getEmail() {
        return email;
    }

    public String getClientId() {
        return clientId;
    }

    public String getLanguageTag() {
        return languageTag;
    }

    public String getResetPasswordUri() {
        return resetPasswordUri;
    }

    @Override
    public String toString() {
        return "ForgotPasswordRequestBody{" +
                "email='" + email + '\'' +
                ", clientId='" + clientId + '\'' +
                ", languageTag='" + languageTag + '\'' +
                ", resetPasswordUri='" + resetPasswordUri + '\'' +
                '}';
    }
}

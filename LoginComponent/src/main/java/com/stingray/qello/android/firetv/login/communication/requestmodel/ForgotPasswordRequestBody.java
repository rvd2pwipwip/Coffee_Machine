package com.stingray.qello.android.firetv.login.communication.requestmodel;

public class ForgotPasswordRequestBody {
    private String email;
    private String clientId;
    private String languageTag;
    private String resetPasswordUrl;

    public ForgotPasswordRequestBody(String email, String languageTag) {
        this.email = email;
        this.languageTag = languageTag;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setResetPasswordUrl(String resetPasswordUrl) {
        this.resetPasswordUrl = resetPasswordUrl;
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

    public String getResetPasswordUrl() {
        return resetPasswordUrl;
    }
}

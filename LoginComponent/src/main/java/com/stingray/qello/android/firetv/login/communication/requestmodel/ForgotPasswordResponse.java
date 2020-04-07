package com.stingray.qello.android.firetv.login.communication.requestmodel;

public class ForgotPasswordResponse {
    private Boolean emailExists;
    private Boolean captchaRequired;

    public Boolean getEmailExists() {
        return emailExists;
    }

    public Boolean getCaptchaRequired() {
        return captchaRequired;
    }
}

package com.stingray.qello.android.firetv.login.communication.requestmodel;

import com.stingray.qello.android.firetv.login.communication.model.ULUserInfo;

public class UserpassLoginResponse {
    private String sessionId;
    private ULUserInfo userInfo;

    public String getSessionId() {
        return sessionId;
    }

    public ULUserInfo getUserInfo() {
        return userInfo;
    }
}

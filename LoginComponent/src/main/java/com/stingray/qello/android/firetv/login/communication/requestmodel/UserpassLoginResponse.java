package com.stingray.qello.android.firetv.login.communication.requestmodel;

import com.stingray.qello.android.firetv.login.communication.model.UserInfo;

public class UserpassLoginResponse {
    private String sessionId;
    private UserInfo userInfo;

    public String getSessionId() {
        return sessionId;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}

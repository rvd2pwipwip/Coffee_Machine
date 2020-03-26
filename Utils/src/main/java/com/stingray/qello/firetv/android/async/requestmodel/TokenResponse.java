package com.stingray.qello.firetv.android.async.requestmodel;

public class TokenResponse {
    private String accessToken;
    private String tokenType;
    private String expiresIn;
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}

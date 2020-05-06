package com.stingray.qello.firetv.android.async.requestmodel;

public class TokenResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}

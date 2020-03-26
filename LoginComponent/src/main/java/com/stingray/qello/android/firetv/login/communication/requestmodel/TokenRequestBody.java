package com.stingray.qello.android.firetv.login.communication.requestmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TokenRequestBody {
    @JsonIgnore
    private final static String GRANT_TYPE = "AUTHORIZATION_CODE";

    private String grantType;
    private String code;
    private String redirectUri;
    private String clientId;
    private String refreshToken;

    public TokenRequestBody(String code, String redirectUri, String refreshToken) {
        this.grantType = GRANT_TYPE;
        this.code = code;
        this.redirectUri = redirectUri;
        this.refreshToken = refreshToken;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getCode() {
        return code;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getClientId() {
        return clientId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}

package com.stingray.qello.firetv.android.async.requestmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TokenRequestBody {
    @JsonIgnore
    private final static String GRANT_TYPE_AUTH_CODE = "AUTHORIZATION_CODE";

    @JsonIgnore
    private final static String GRANT_TYPE_REFRESH_TOKEN = "REFRESH_TOKEN";

    private String grantType;
    private String code;
    private String redirectUri;
    private String clientId;
    private String refreshToken;

    public TokenRequestBody(String code) {
        this.grantType = GRANT_TYPE_AUTH_CODE;
        this.code = code;
        this.redirectUri = "123";
    }

    public TokenRequestBody(String refreshToken, String clientId) {
        this.grantType = GRANT_TYPE_REFRESH_TOKEN;
        this.refreshToken = refreshToken;
        this.redirectUri = "123";
        this.clientId = clientId;
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

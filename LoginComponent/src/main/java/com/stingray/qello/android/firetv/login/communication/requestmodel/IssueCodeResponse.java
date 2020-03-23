package com.stingray.qello.android.firetv.login.communication.requestmodel;

public class IssueCodeResponse {
    private String code;
    private String field;
    private String invalidValue;
    private String handleType;

    public String getCode() {
        return code;
    }

    public String getField() {
        return field;
    }

    public String getInvalidValue() {
        return invalidValue;
    }

    public String getHandleType() {
        return handleType;
    }
}

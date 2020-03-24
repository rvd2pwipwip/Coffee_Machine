package com.stingray.qello.firetv.android.model;

public class SvodImage {
    private String type;
    private String url;

    private SvodImage() {

    }

    public SvodImage(String type, String url) {
        this.type = type;
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}

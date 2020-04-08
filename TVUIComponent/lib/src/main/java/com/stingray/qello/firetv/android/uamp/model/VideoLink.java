package com.stingray.qello.firetv.android.uamp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class VideoLink {
    private String videoType;
    private String mediaUri;

    @JsonIgnore
    private Type type;

    public String getVideoType() {
        return videoType;
    }

    public String getMediaUri() {
        return mediaUri;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {PREVIEW, TRAILER, FULL}
}

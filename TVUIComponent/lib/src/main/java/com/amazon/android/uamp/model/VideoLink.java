package com.amazon.android.uamp.model;

public class VideoLink {
    private String videoType;
    private String mediaUri;

    public String getVideoType() {
        return videoType;
    }

    public String getMediaUri() {
        return mediaUri;
    }

    public enum Type {PREVIEW, TRAILER, FULL}
}

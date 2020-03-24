package com.stingray.qello.firetv.android.uamp.model;

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

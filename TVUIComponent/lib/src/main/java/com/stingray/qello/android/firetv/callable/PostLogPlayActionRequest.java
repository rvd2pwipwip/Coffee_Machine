package com.stingray.qello.android.firetv.callable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostLogPlayActionRequest {
    // Specific naming required because doesn't follow snake case convention
    @JsonProperty("content_id")
    private final String contentId;
    @JsonProperty("offset")
    private final String offset;
    @JsonProperty("play_action")
    private final String playAction = "PLAY";
    @JsonProperty("asset_type")
    private final String assetType = "CONCERT";

    public PostLogPlayActionRequest(String contentId, String offset) {
        this.contentId = contentId;
        this.offset = offset;
    }

    public String getContentId() {
        return contentId;
    }

    public String getOffset() {
        return offset;
    }

    public String getPlayAction() {
        return playAction;
    }

    public String getAssetType() {
        return assetType;
    }
}

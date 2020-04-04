package com.stingray.qello.firetv.android.contentbrowser.callable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stingray.qello.firetv.android.async.SvodCallable;
import com.stingray.qello.firetv.android.contentbrowser.callable.model.LikeStatus;
import com.stingray.qello.firetv.android.model.content.constants.AssetType;
import com.stingray.qello.firetv.android.utils.SvodObjectMapperProvider;

public class AddToFavoriteCallable extends SvodCallable<Void> {
    private final static String ENDPOINT = "/v1/profile/favorites";
    private final static String TAG = AddToFavoriteCallable.class.getSimpleName();

    private final ObjectMapper objectMapper = new SvodObjectMapperProvider().get();

    private Request request;

    public AddToFavoriteCallable(String contentId, LikeStatus likeStatus) {
        request = new Request(contentId, likeStatus.name(), AssetType.CONCERT.name());
    }

    @Override
    public Void call() throws JsonProcessingException {
        String url = String.format(ENDPOINT);
        Response response = post(url, objectMapper.writeValueAsString(request));

        if (response.getCode() != 204) {
            throw new RuntimeException(String.format("Failed to update like status from [%s]. Response: [%s]", ENDPOINT, response.getBody()));
        }

        return null;
    }

    public static class Request {
        private final String contentId;
        private final String likeStatus;
        private final String assetType;

        public Request(String contentId, String likeStatus, String assetType) {
            this.contentId = contentId;
            this.likeStatus = likeStatus;
            this.assetType = assetType;
        }

        public String getContentId() {
            return contentId;
        }

        public String getLikeStatus() {
            return likeStatus;
        }

        public String getAssetType() {
            return assetType;
        }
    }
}

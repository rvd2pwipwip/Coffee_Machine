package com.amazon.android.uamp.mediaSession;

import android.util.Log;

import com.amazon.android.uamp.model.VideoLink;
import com.amazon.android.uamp.model.VideoLinks;
import com.amazon.android.utils.NetworkUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class GetVideoLinksCallable implements Callable<Map<VideoLink.Type, String>> {
    private final static String ENDPOINT = "https://svod-stage.api.stingray.com/v1/content/%s/video-links?hevc-compatible=true";
    private static final String TAG = GetVideoLinksCallable.class.getSimpleName();

    private String assetId;
    private ObjectMapper objectMapper;

    public GetVideoLinksCallable(String assetId) {
        this.assetId = assetId;
        this.objectMapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    @Override
    public Map<VideoLink.Type, String> call() {
        try {
            String url = String.format(ENDPOINT, assetId);
            String jsonResponse = NetworkUtils.getDataLocatedAtUrl(url);

            Log.i(TAG, String.format("Received response: %s", jsonResponse));

            List<VideoLink> videoLinks = objectMapper.readValue(jsonResponse, VideoLinks.class).getVideoLinks();

            Map<VideoLink.Type, String> mediaUriByType = new HashMap<>();

            for (VideoLink videoLink: videoLinks) {
                try {
                    VideoLink.Type type = VideoLink.Type.valueOf(videoLink.getVideoType());
                    if (videoLink.getMediaUri() != null) {
                        mediaUriByType.put(type, videoLink.getMediaUri());
                    }
                } catch (IllegalArgumentException e) {
                    Log.w(TAG, String.format("Unrecognized video type [%s]", videoLink.getVideoType()),e);
                }
            }

            return mediaUriByType;

        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to get videoLink from [%s]", ENDPOINT),e);
            return new HashMap<>();
        }
    }
}

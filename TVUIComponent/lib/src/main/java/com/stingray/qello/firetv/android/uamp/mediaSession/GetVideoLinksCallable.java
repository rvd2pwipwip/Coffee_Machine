package com.stingray.qello.firetv.android.uamp.mediaSession;

import android.util.Log;

import com.stingray.qello.firetv.android.async.SvodCallable;
import com.stingray.qello.firetv.android.uamp.model.VideoLink;
import com.stingray.qello.firetv.android.uamp.model.VideoLinks;
import com.stingray.qello.firetv.android.utils.SvodObjectMapperProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetVideoLinksCallable extends SvodCallable<Map<VideoLink.Type, VideoLink>> {
    private final static String ENDPOINT = "/v1/content/%s/video-links?hevc-compatible=true";
    private static final String TAG = GetVideoLinksCallable.class.getSimpleName();

    private String assetId;
    private ObjectMapper objectMapper;

    public GetVideoLinksCallable(String assetId) {
        this.assetId = assetId;
        this.objectMapper = new SvodObjectMapperProvider().get();
    }

    @Override
    public Map<VideoLink.Type, VideoLink> call() {
        try {
            String url = String.format(ENDPOINT, assetId);
            String jsonResponse = get(url);

            List<VideoLink> videoLinks = objectMapper.readValue(jsonResponse, VideoLinks.class).getVideoLinks();

            Map<VideoLink.Type, VideoLink> mediaUriByType = new HashMap<>();

            for (VideoLink videoLink : videoLinks) {
                try {
                    videoLink.setType(VideoLink.Type.valueOf(videoLink.getVideoType()));
                    if (videoLink.getMediaUri() != null) {
                        mediaUriByType.put(videoLink.getType(), videoLink);
                    }
                } catch (IllegalArgumentException e) {
                    Log.w(TAG, String.format("Unrecognized video type [%s]", videoLink.getVideoType()), e);
                }
            }

            return mediaUriByType;

        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to get concert video link from [%s]", ENDPOINT), e);
            return new HashMap<>();
        }
    }
}

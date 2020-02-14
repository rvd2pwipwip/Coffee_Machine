package com.amazon.android.uamp.mediaSession;

import com.amazon.android.uamp.model.VideoLink;
import com.amazon.android.uamp.model.VideoLinks;
import com.amazon.android.utils.NetworkUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetVideoLinksRunnable implements Runnable {
    private final static String ENDPOINT = "https://svod-stage.api.stingray.com/v1/content/%s/video-links?hevc-compatible=true";
    private final static Logger LOG = LoggerFactory.getLogger(GetVideoLinksRunnable.class);

    private String assetId;
    private ObjectMapper objectMapper;

    private Map<VideoLink.Type, String> mediaUriByType;

    public GetVideoLinksRunnable(String assetId) {
        this.assetId = assetId;
        this.objectMapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    public Map<VideoLink.Type, String> getMediaUriByType() {
        return mediaUriByType;
    }

    @Override
    public void run() {
        try {
            String url = String.format(ENDPOINT, assetId);
            String jsonResponse = NetworkUtils.getDataLocatedAtUrl(url);

            LOG.info(String.format("Received response: %s", jsonResponse));

            List<VideoLink> videoLinks = objectMapper.readValue(jsonResponse, VideoLinks.class).getVideoLinks();

            Map<VideoLink.Type, String> mediaUriByType = new HashMap<>();

            for (VideoLink videoLink: videoLinks) {
                try {
                    VideoLink.Type type = VideoLink.Type.valueOf(videoLink.getVideoType());
                    if (videoLink.getMediaUri() != null) {
                        mediaUriByType.put(type, videoLink.getMediaUri());
                    }
                } catch (IllegalArgumentException e) {
                    LOG.warn(String.format("Unrecognized video type [%s]", videoLink.getVideoType()),e);
                }
            }

            this.mediaUriByType = mediaUriByType;

        } catch (Exception e) {
            LOG.error(String.format("Failed to get videoLink from [%s]", ENDPOINT),e);
        }
    }
}

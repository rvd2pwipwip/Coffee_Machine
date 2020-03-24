package com.stingray.qello.firetv.android.uamp.mediaSession;

import com.stingray.qello.firetv.android.uamp.model.VideoLink;

import java.util.Map;

public class VideoLinkSelector {

    public String select(Map<VideoLink.Type, String> mediaUriByType) {
        String mediaUri;

        if (mediaUriByType.get(VideoLink.Type.FULL) != null) {
            mediaUri = mediaUriByType.get(VideoLink.Type.FULL);
        } else if(mediaUriByType.get(VideoLink.Type.TRAILER) != null) {
            mediaUri = mediaUriByType.get(VideoLink.Type.TRAILER);
        } else if(mediaUriByType.get(VideoLink.Type.PREVIEW) != null) {
            mediaUri = mediaUriByType.get(VideoLink.Type.PREVIEW);
        } else {
            mediaUri = "";
        }
        return mediaUri;
    }
}

package com.stingray.qello.firetv.android.uamp.mediaSession;

import com.stingray.qello.firetv.android.uamp.model.VideoLink;

import java.util.Map;

public class VideoLinkSelector {

    public VideoLink select(Map<VideoLink.Type, VideoLink> mediaUriByType) {
        VideoLink videoLink;

        if (mediaUriByType.get(VideoLink.Type.FULL) != null) {
            videoLink = mediaUriByType.get(VideoLink.Type.FULL);
        } else if(mediaUriByType.get(VideoLink.Type.TRAILER) != null) {
            videoLink = mediaUriByType.get(VideoLink.Type.TRAILER);
        } else if(mediaUriByType.get(VideoLink.Type.PREVIEW) != null) {
            videoLink = mediaUriByType.get(VideoLink.Type.PREVIEW);
        } else {
            videoLink = null;
        }
        return videoLink;
    }
}

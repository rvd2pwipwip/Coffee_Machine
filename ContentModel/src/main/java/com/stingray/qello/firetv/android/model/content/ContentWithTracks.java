package com.stingray.qello.firetv.android.model.content;

import java.util.List;

public class ContentWithTracks {
    private final Content content;
    private final List<Track> tracks;

    public ContentWithTracks(Content content, List<Track> tracks) {
        this.content = content;
        this.tracks = tracks;
    }

    public Content getContent() {
        return content;
    }

    public List<Track> getTracks() {
        return tracks;
    }
}

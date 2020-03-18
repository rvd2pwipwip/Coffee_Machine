package com.amazon.android.model.content;

import com.amazon.android.model.content.Content;
import com.amazon.android.model.content.ContentContainer;
import com.amazon.android.model.content.Track;

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

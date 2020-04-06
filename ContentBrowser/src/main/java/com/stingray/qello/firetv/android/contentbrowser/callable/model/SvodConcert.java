package com.stingray.qello.firetv.android.contentbrowser.callable.model;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class SvodConcert {
    ;

    private String id;
    private String title;
    private String artists;
    private String subtitle;
    private String shortDescription;
    private String category;
    private String location;
    private String concertYear;
    private Long duration;
    private List<String> ratings;
    private String fullDescription;
    private Boolean hasPreview;
    private String likeStatus;
    private Long playPosition;

    // For deserialization
    public SvodConcert() { }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtists() {
        return artists;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public String getConcertYear() {
        return concertYear;
    }

    public Long getDuration() {
        return duration;
    }

    public List<String> getRatings() {
        return ratings;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public Boolean getHasPreview() {
        return hasPreview;
    }

    public String getLikeStatus() {
        return likeStatus;
    }

    public boolean isLiked() {
        return likeStatus != null && likeStatus.equalsIgnoreCase("LIKED");
    }

    public Long getPlayPosition() {
        return playPosition;
    }
}

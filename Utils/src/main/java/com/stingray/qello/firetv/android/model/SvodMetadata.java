package com.stingray.qello.firetv.android.model;

import java.util.List;

public class SvodMetadata {
    private String displayName;
    private Boolean showIfEmpty;
    private Boolean hasViewMore;
    private Boolean refreshable;
    private String preset;
    private List<SvodImage> images;

    public SvodMetadata() {
        
    }

    public SvodMetadata(String displayName, Boolean showIfEmpty, Boolean hasViewMore, Boolean refreshable, String preset, List<SvodImage> images) {
        this.displayName = displayName;
        this.showIfEmpty = showIfEmpty;
        this.hasViewMore = hasViewMore;
        this.refreshable = refreshable;
        this.preset = preset;
        this.images = images;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Boolean getShowIfEmpty() {
        return showIfEmpty;
    }

    public Boolean getHasViewMore() {
        return hasViewMore;
    }

    public Boolean getRefreshable() {
        return refreshable;
    }

    public String getPreset() {
        return preset;
    }

    public List<SvodImage> getImages() {
        return images;
    }
}

package com.amazon.android.model.content;

import java.io.Serializable;
import java.util.Objects;

public class Genre implements Serializable {
    private static final long serialVersionUID = 3751326288800793834L;
    private static final String TAG = Genre.class.getSimpleName();

    public final static String M_ID = "mId";
    public final static String M_TITLE = "mTitle";
    public final static String M_CARD_IMAGE_URL = "mCardImageUrl";

    private String mId;
    private String mTitle;
    private String mCardImageUrl;

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getCardImageUrl() {
        return mCardImageUrl;
    }

    public void setCardImageUrl(String mCardImageUrl) {
        this.mCardImageUrl = mCardImageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return Objects.equals(mId, genre.mId) &&
                Objects.equals(mTitle, genre.mTitle) &&
                Objects.equals(mCardImageUrl, genre.mCardImageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mTitle, mCardImageUrl);
    }
}

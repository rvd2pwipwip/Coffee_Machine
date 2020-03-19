package com.amazon.android.model.content;

import java.util.Objects;

public class Track extends Content {
    private static final long serialVersionUID = 3751326288800793834L;
    private static final String TAG = Track.class.getSimpleName();

    public final static String M_PARENT_ID = "mParentId";
    public final static String M_IS_PUBLIC = "mIsPublic";

    private String mParentId;
    private String mIsPublic;

    public String getParentId() {
        return mParentId;
    }

    public void setParentId(String mParentId) {
        this.mParentId = mParentId;
    }

    public String getIsPublic() {
        return mIsPublic;
    }

    public void setIsPublic(String mIsPublic) {
        this.mIsPublic = mIsPublic;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Track track = (Track) o;
        return Objects.equals(mParentId, track.mParentId) &&
                Objects.equals(mIsPublic, track.mIsPublic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mParentId, mIsPublic);
    }
}

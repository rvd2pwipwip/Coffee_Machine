package com.stingray.qello.firetv.android.model.translators;

import android.util.Log;

import com.stingray.qello.firetv.android.model.AModelTranslator;
import com.stingray.qello.firetv.android.model.content.Content;
import com.stingray.qello.firetv.android.model.content.Track;
import com.stingray.qello.firetv.utils.ListUtils;

import java.util.List;

public class TrackTranslator extends AModelTranslator<Track> {

    private static final String TAG = TrackTranslator.class.getSimpleName();

    @Override
    public Track instantiateModel() {

        return new Track();
    }

    @Override
    public boolean setMemberVariable(Track model, String field, Object value) {

        if (model == null || field == null || field.isEmpty()) {
            Log.e(TAG, "Input parameters should not be null and field cannot be empty.");
            return false;
        }
        // This allows for some content to have extra values that others might not have.
        if (value == null) {
            Log.w(TAG, "Value for " + field + " was null so not set for Track, this may be " +
                    "intentional.");
            return true;
        }
        try {
            switch (field) {
                case Content.TITLE_FIELD_NAME:
                    model.setTitle(value.toString());
                    break;
                case Content.DESCRIPTION_FIELD_NAME:
                    model.setDescription(value.toString());
                    break;
                case Content.ID_FIELD_NAME:
                    model.setId(value.toString());
                    break;
                case Content.SUBTITLE_FIELD_NAME:
                    model.setSubtitle(value.toString());
                    break;
                case Content.URL_FIELD_NAME:
                    model.setUrl(value.toString());
                    break;
                case Content.ASSET_TYPE_FIELD_NAME:
                    model.setAssetType(value.toString());
                    break;
                case Content.CARD_IMAGE_URL_FIELD_NAME:
                    model.setCardImageUrl(value.toString());
                    break;
                case Content.BACKGROUND_IMAGE_URL_FIELD_NAME:
                    model.setBackgroundImageUrl(value.toString());
                    break;
                case Content.TAGS_FIELD_NAME:
                    // Expecting value to be a list.
                    model.setTags(value.toString());
                    break;
                case Content.CLOSED_CAPTION_FIELD_NAME:
                    model.setCloseCaptionUrls((List) value);
                    break;
                case Content.RECOMMENDATIONS_FIELD_NAME:
                    // Expecting value to be a list.
                    model.setRecommendations(value.toString());
                    break;
                case Content.AVAILABLE_DATE_FIELD_NAME:
                    model.setAvailableDate(value.toString());
                    break;
                case Content.SUBSCRIPTION_REQUIRED_FIELD_NAME:
                    model.setSubscriptionRequired((boolean) value);
                    break;
                case Content.CHANNEL_ID_FIELD_NAME:
                    model.setChannelId(value.toString());
                    break;
                case Content.DURATION_FIELD_NAME:
                    model.setDuration(Long.parseLong(value.toString()));
                    break;
                case Content.AD_CUE_POINTS_FIELD_NAME:
                    model.setAdCuePoints((List) value);
                    break;
                case Content.STUDIO_FIELD_NAME:
                    model.setStudio(value.toString());
                    break;
                case Content.FORMAT_FIELD_NAME:
                    model.setFormat(value.toString());
                    break;
                case Track.M_PARENT_ID:
                    model.setParentId(value.toString());
                    break;
                case Track.M_IS_PUBLIC:
                    model.setIsPublic(value.toString());
                    break;
                default:
                    model.setExtraValue(field, value);
                    break;
            }
        } catch (ClassCastException e) {
            Log.e(TAG, "Error casting value to the required type for field " + field, e);
            return false;
        } catch (ListUtils.ExpectingJsonArrayException e) {
            Log.e(TAG, "Error creating JSONArray from provided tags string " + value, e);
            return false;
        }
        return true;
    }

    @Override
    public boolean validateModel(Track model) {

        try {
            return !model.getTitle().isEmpty();
        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer found during model validation.", e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return TrackTranslator.class.getSimpleName();
    }
}

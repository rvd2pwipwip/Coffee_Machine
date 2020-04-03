package com.stingray.qello.firetv.segment;

import android.content.Context;

import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.Traits;
import com.segment.analytics.android.integrations.mixpanel.MixpanelIntegration;
import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;
import com.stingray.qello.firetv.android.utils.Preferences;
import com.stingray.qello.firetv.segment.constants.SegmentTrackingConstants;
import com.stingray.qello.firetv.user_tracking.ITracking;

public class SegmentTracking implements ITracking {

    @Override
    public void init(Context context) {
        Analytics analytics = new Analytics.Builder(context, "thKdc3YGs1K6r3DQ2qKaXQNGdrFN3cUt")
                .trackApplicationLifecycleEvents()
                .trackAttributionInformation().collectDeviceId(true).recordScreenViews().use(MixpanelIntegration.FACTORY).build();

        Analytics.setSingletonInstance(analytics);
    }

    @Override
    public void trackPlaybackStarted(Context context, String sessionId, String assetId, long totalLength, long position) {
        Analytics.with(context).track(SegmentTrackingConstants.VIDEO_PLAYBACK_STARTED, new Properties()
                .putValue(SegmentTrackingConstants.SESSION_ID_PROPERTY, sessionId)
                .putValue(SegmentTrackingConstants.CONTENT_ASSET_ID_PROPERTY, assetId)
                .putValue(SegmentTrackingConstants.TOTAL_LENGTH_PROPERTY, String.valueOf(totalLength))
                .putValue(SegmentTrackingConstants.POSITION_PROPERTY, String.valueOf(position))
        );
    }

    @Override
    public void trackPlaybackPaused(Context context, String sessionId, String assetId, long totalLength, long position) {
        Analytics.with(context).track(SegmentTrackingConstants.VIDEO_PLAYBACK_PAUSED, new Properties()
                .putValue(SegmentTrackingConstants.SESSION_ID_PROPERTY, sessionId)
                .putValue(SegmentTrackingConstants.CONTENT_ASSET_ID_PROPERTY, assetId)
                .putValue(SegmentTrackingConstants.TOTAL_LENGTH_PROPERTY, String.valueOf(totalLength))
                .putValue(SegmentTrackingConstants.POSITION_PROPERTY, String.valueOf(position))
        );
    }

    @Override
    public void trackPlaybackInterrupted(Context context, String sessionId, String assetId, long totalLength, long position, String error) {
        Analytics.with(context).track(SegmentTrackingConstants.VIDEO_PLAYBACK_INTERRUPTED, new Properties()
                .putValue(SegmentTrackingConstants.SESSION_ID_PROPERTY, sessionId)
                .putValue(SegmentTrackingConstants.CONTENT_ASSET_ID_PROPERTY, assetId)
                .putValue(SegmentTrackingConstants.TOTAL_LENGTH_PROPERTY, String.valueOf(totalLength))
                .putValue(SegmentTrackingConstants.POSITION_PROPERTY, String.valueOf(position))
                .putValue(SegmentTrackingConstants.METHOD_PROPERTY, error)
        );
    }

    @Override
    public void trackPlaybackCompleted(Context context, String sessionId, String assetId, long totalLength, long position) {
        Analytics.with(context).track(SegmentTrackingConstants.VIDEO_PLAYBACK_COMPLETED, new Properties()
                .putValue(SegmentTrackingConstants.SESSION_ID_PROPERTY, sessionId)
                .putValue(SegmentTrackingConstants.CONTENT_ASSET_ID_PROPERTY, assetId)
                .putValue(SegmentTrackingConstants.TOTAL_LENGTH_PROPERTY, String.valueOf(totalLength))
                .putValue(SegmentTrackingConstants.POSITION_PROPERTY, String.valueOf(position))
        );
    }

    @Override
    public void trackPlaybackResumed(Context context, String sessionId, String assetId, long totalLength, long position) {
        Analytics.with(context).track(SegmentTrackingConstants.VIDEO_PLAYBACK_RESUMED, new Properties()
                .putValue(SegmentTrackingConstants.SESSION_ID_PROPERTY, sessionId)
                .putValue(SegmentTrackingConstants.CONTENT_ASSET_ID_PROPERTY, assetId)
                .putValue(SegmentTrackingConstants.TOTAL_LENGTH_PROPERTY, String.valueOf(totalLength))
                .putValue(SegmentTrackingConstants.POSITION_PROPERTY, String.valueOf(position))
        );
    }

    @Override
    public void trackContentStarted(Context context, String sessionId, String assetId, long totalLength, long position) {
        Analytics.with(context).track(SegmentTrackingConstants.VIDEO_CONTENT_STARTED, new Properties()
                .putValue(SegmentTrackingConstants.SESSION_ID_PROPERTY, sessionId)
                .putValue(SegmentTrackingConstants.CONTENT_ASSET_ID_PROPERTY, assetId)
                .putValue(SegmentTrackingConstants.TOTAL_LENGTH_PROPERTY, String.valueOf(totalLength))
                .putValue(SegmentTrackingConstants.POSITION_PROPERTY, String.valueOf(position))
        );
    }

    @Override
    public void trackContentPlaying(Context context, String sessionId, String assetId, long totalLength, long position) {
        Analytics.with(context).track(SegmentTrackingConstants.VIDEO_CONTENT_PLAYING, new Properties()
                .putValue(SegmentTrackingConstants.SESSION_ID_PROPERTY, sessionId)
                .putValue(SegmentTrackingConstants.CONTENT_ASSET_ID_PROPERTY, assetId)
                .putValue(SegmentTrackingConstants.TOTAL_LENGTH_PROPERTY, String.valueOf(totalLength))
                .putValue(SegmentTrackingConstants.POSITION_PROPERTY, String.valueOf(position))
        );
    }

    @Override
    public void trackContentCompleted(Context context, String sessionId, String assetId, long totalLength, long position) {
        Analytics.with(context).track(SegmentTrackingConstants.VIDEO_CONTENT_COMPLETED, new Properties()
                .putValue(SegmentTrackingConstants.SESSION_ID_PROPERTY, sessionId)
                .putValue(SegmentTrackingConstants.CONTENT_ASSET_ID_PROPERTY, assetId)
                .putValue(SegmentTrackingConstants.TOTAL_LENGTH_PROPERTY, String.valueOf(totalLength))
                .putValue(SegmentTrackingConstants.POSITION_PROPERTY, String.valueOf(position))
        );
    }

    @Override
    public void trackAccountCreation(Context context)
    {
        identifyUser(context);
        String userTrackingId = Preferences.getString(PreferencesConstants.USER_TRACKING_ID);
        Analytics.with(context).alias(userTrackingId);
        Analytics.with(context).track("Account Created", new Properties().putValue("authentication", "Email"));
    }

    @Override
    public void trackAccountLogin(Context context) {
        identifyUser(context);
        Analytics.with(context).track("Account Logged In", new Properties().putValue("authentication", "Email"));
    }

    @Override
    public void screen(Context context, String pageId) {
        Analytics.with(context).screen(pageId);
    }

    private void identifyUser(Context context) {
        String email = Preferences.getString(PreferencesConstants.EMAIL);
        String userTrackingId = Preferences.getString(PreferencesConstants.USER_TRACKING_ID);

        Analytics.with(context).identify(userTrackingId, new Traits().putEmail(email).putName(email), null);
    }
}

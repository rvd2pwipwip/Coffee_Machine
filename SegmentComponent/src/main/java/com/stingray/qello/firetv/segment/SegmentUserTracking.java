package com.stingray.qello.firetv.segment;

import android.content.Context;

import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.android.integrations.mixpanel.MixpanelIntegration;
import com.segment.analytics.Traits;
import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;
import com.stingray.qello.firetv.android.utils.Preferences;
import com.stingray.qello.firetv.user_tracking.IUserTracking;

public class SegmentUserTracking implements IUserTracking {

    @Override
    public void init(Context context) {
        Analytics analytics = new Analytics.Builder(context, "thKdc3YGs1K6r3DQ2qKaXQNGdrFN3cUt")
                .trackApplicationLifecycleEvents()
                .trackAttributionInformation().collectDeviceId(true).recordScreenViews().use(MixpanelIntegration.FACTORY).build();

        Analytics.setSingletonInstance(analytics);
    }

    @Override
    public void trackAccountCreation(Context context)
    {
        identifyUser(context);
        String userTrackingId = Preferences.getString(PreferencesConstants.USER_TRACKING_ID);
        Analytics.with(context).alias(userTrackingId);
        Analytics.with(context).track("Account Created", new Properties().putValue("authentication", "Email"));
    }

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

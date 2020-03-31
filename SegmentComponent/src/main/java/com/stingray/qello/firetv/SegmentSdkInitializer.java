package com.stingray.qello.firetv;

import android.content.Context;

import com.segment.analytics.Analytics;
import com.segment.analytics.android.integrations.mixpanel.MixpanelIntegration;

public class SegmentSdkInitializer {

    public void init(Context context) {
        Analytics analytics = new Analytics.Builder(context, "BLU0BoiWiFCQsLFZxWDqgKAzAggTUNQG")
                .trackApplicationLifecycleEvents()
                .trackAttributionInformation().collectDeviceId(true).recordScreenViews().use(MixpanelIntegration.FACTORY).build();

        Analytics.setSingletonInstance(analytics);

    }
}

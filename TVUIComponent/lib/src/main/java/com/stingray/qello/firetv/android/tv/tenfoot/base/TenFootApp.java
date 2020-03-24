/**
 * Copyright 2015-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.stingray.qello.firetv.android.tv.tenfoot.base;

import com.stingray.qello.firetv.android.configuration.ConfigurationManager;
import com.stingray.qello.firetv.android.contentbrowser.app.ContentBrowserApplication;
import com.stingray.qello.firetv.android.contentbrowser.constants.ConfigurationConstants;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.tv.tenfoot.ui.activities.MainActivity;
import com.stingray.qello.firetv.android.tv.tenfoot.ui.activities.ContentDetailsActivity;
import com.stingray.qello.firetv.android.tv.tenfoot.ui.activities.SplashActivity;

import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.stingray.qello.firetv.android.uamp.ui.PlaybackActivity;
import com.stingray.qello.firetv.analytics.AnalyticsTags;

/**
 * TenFoot Application class.
 */
public class TenFootApp extends ContentBrowserApplication {

    /**
     * Debug TAG.
     */
    private static final String TAG = ContentBrowserApplication.class.getSimpleName();

    @Override
    public void onCreate() {

        super.onCreate();

        try {
            /**
             * Get the default values and set them to Configuration manager.
             */
            ConfigurationManager
                    .getInstance(this)
                    .setBooleanValue(ConfigurationConstants.CONFIG_HIDE_MORE_ACTIONS,
                                     getResources().getBoolean(R.bool.hide_more_options))
                    .setIntegerValue(com.stingray.qello.firetv.android.ui.constants.ConfigurationConstants
                                             .CONFIG_SPINNER_ALPHA_COLOR,
                                     getResources().getInteger(R.integer.spinner_alpha))
                    .setIntegerValue(com.stingray.qello.firetv.android.ui.constants.ConfigurationConstants
                                             .CONFIG_SPINNER_COLOR,
                                     ContextCompat.getColor(this, R.color.spinner_color))
                    .setIntegerValue(ConfigurationConstants.CONFIG_TIME_TO_RELOAD_FEED,
                                     getResources().getInteger(R.integer.time_to_reload_content));
        }
        catch (Resources.NotFoundException exception) {
            Log.e(TAG, "Resources not found", exception);
        }

        // Add analytics constant of embedded activities.
        mAnalyticsManager.addAnalyticsConstantForActivity(SplashActivity.class.getSimpleName(),
                                                          AnalyticsTags.SCREEN_SPLASH)
                         .addAnalyticsConstantForActivity(MainActivity.class
                                                                  .getSimpleName(),
                                                          AnalyticsTags.SCREEN_BROWSE)
                         .addAnalyticsConstantForActivity(ContentDetailsActivity.class
                                                                  .getSimpleName(),
                                                          AnalyticsTags.SCREEN_DETAILS)
                         .addAnalyticsConstantForActivity(PlaybackActivity.class
                                                                  .getSimpleName(),
                                                          AnalyticsTags.SCREEN_PLAYBACK);
    }
}

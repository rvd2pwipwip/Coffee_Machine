/**
 * This file was modified by Amazon:
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
/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.stingray.qello.firetv.android.tv.tenfoot.ui.activities;

import com.stingray.qello.firetv.android.contentbrowser.ContentBrowser;
import com.stingray.qello.firetv.android.model.event.ActionUpdateEvent;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.tv.tenfoot.base.BaseActivity;
import com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.Home.ContentDetailsFragment;
import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;
import com.stingray.qello.firetv.android.utils.Preferences;
import com.stingray.qello.firetv.utils.DateAndTimeHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import android.os.Bundle;
import android.util.Log;

/**
 * Details activity class that loads the LeanbackDetailsFragment class.
 */
public class ContentDetailsActivity extends BaseActivity {

    private static final String TAG = ContentDetailsActivity.class.getSimpleName();

    public static final String SHARED_ELEMENT_NAME = "hero";

    public ContentDetailsFragment mContentDetailsFragment;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "onCreate called.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_details_activity_layout);

        mContentDetailsFragment = (ContentDetailsFragment) getFragmentManager().findFragmentById
                (R.id.content_details_fragment);
    }

    @Override
    public void setRestoreActivityValues() {

        Preferences.setString(PreferencesConstants.LAST_ACTIVITY,
                              ContentBrowser.CONTENT_DETAILS_SCREEN);
        Preferences.setLong(PreferencesConstants.TIME_LAST_SAVED,
                            DateAndTimeHelper.getCurrentDate().getTime());
    }

//    @Subscribe
//    public void onActionListUpdateRequired(ActionUpdateEvent actionUpdateEvent) {
//
//        mContentDetailsFragment.updateActions();
//    }

    @Override
    protected void onStart() {

        super.onStart();
        //EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
//
//        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}

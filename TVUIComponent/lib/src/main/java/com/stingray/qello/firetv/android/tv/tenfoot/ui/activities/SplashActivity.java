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
import com.stingray.qello.firetv.android.contentbrowser.helper.ErrorHelper;
import com.stingray.qello.firetv.android.interfaces.ICancellableLoad;
import com.stingray.qello.firetv.android.utils.ErrorUtils;
import com.stingray.qello.firetv.android.utils.Helpers;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.tv.tenfoot.base.BaseActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * This is a splash activity to load when the app is initializing and loading its content.
 */
public class SplashActivity extends BaseActivity implements ICancellableLoad {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private ImageView mAppLogo;
    private ProgressBar mProgress;
    private boolean isLoadingCancelled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity_layout);
        mAppLogo = (ImageView) findViewById(R.id.main_logo);
        mProgress = (ProgressBar) findViewById(R.id.feed_progress);
        TextView mProgressText = (TextView) findViewById(R.id.feed_loader);

        // Check to see if this activity is not called from the TenFootApp.
        if (!getIntent().hasExtra(ContentBrowser.CONTENT_WILL_UPDATE)) {
            mProgressText.setText(R.string.Global_Loading);
        }
        // If this activity was called from the TenFootApp call activity method.
        else {
            mProgressText.setText(R.string.feed_reloading);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        if (Helpers.DEBUG) {
            Log.d(TAG, "Splash onNewIntent:" + intent);
        }
        this.setIntent(intent);
    }

    @Override
    public void onResume() {

        super.onResume();
        isLoadingCancelled = false;
        if (!getIntent().hasExtra(ContentBrowser.CONTENT_WILL_UPDATE)) {
            Log.d(TAG, "First loading");
            new AsyncTask<Activity, Void, String>() {

                @Override
                protected String doInBackground(Activity... activity) {

                    ContentBrowser contentBrowser = ContentBrowser.getInstance(activity[0]);
                    try {

                        SplashActivity splashAct = (SplashActivity) activity[0];

                        contentBrowser.onAllModulesLoaded();

                        // Notify content browser that the intent is coming from an app launch.
                        splashAct.getIntent().putExtra(ContentBrowser.RESTORE_ACTIVITY, true);
                        contentBrowser.runGlobalRecipes(splashAct, splashAct);
                    }
                    catch (Exception e) {
                        Log.e(TAG, "Failed to put data in cache for recipe ", e);
                    }
                    catch (NoClassDefFoundError error) {
                        Log.e("Did not find class ", error.getMessage());
                        return error.toString();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(String error) {
                    if(error != null && error.contains(NoClassDefFoundError.class.getSimpleName())) {
                        Log.e(TAG, "onPostExecute "+error);
                        showAuthErrorDialog();
                    }
                }
            }.execute(this);

        }
    }

    /**
     * Shows an error dialog
     */
    private void showAuthErrorDialog() {
        ErrorHelper.injectErrorFragment(this, ErrorUtils.ERROR_CATEGORY
                .AUTHENTICATION_SYSTEM_ERROR, (errorDialogFragment, errorButtonType,
                                               errorCategory) -> {
            if (errorButtonType ==
                    ErrorUtils.ERROR_BUTTON_TYPE.EXIT_APP) {
                this.finishAffinity();
            }
        });
    }
    @Override
    public void setRestoreActivityValues() {
        // not restoring state.
    }

    /**
     * Returns if the loading request is cancelled or not.
     * For this class it will never be cancelled.
     *
     * @return True if loading is cancelled
     */
    public boolean isLoadingCancelled() {

        return isLoadingCancelled;
    }

    @Override
    public void onPause() {

        isLoadingCancelled = true;
        super.onPause();
    }



}

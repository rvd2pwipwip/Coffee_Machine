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

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.tv.tenfoot.base.BaseActivity;
import com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.Home.HomeFragment;
import com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.MyQello.MyQelloFragment;
import com.stingray.qello.firetv.android.tv.tenfoot.utils.BrowseHelper;
import com.stingray.qello.firetv.android.utils.Helpers;

/**
 * MainActivity class that loads the ContentBrowseFragment.
 */
public class MainActivity extends BaseActivity {

    private final String TAG = MainActivity.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);
        Helpers.handleActivityEnterFadeTransition(this, 1500);
        FragmentManager fragmentManager = getFragmentManager();

        Fragment homeFragment =fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName());
        if(homeFragment == null) {
            homeFragment = new HomeFragment();
            homeFragment.setArguments(savedInstanceState);
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.main_detail, homeFragment, HomeFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {

        super.onResume();

        reportFullyDrawn();
    }

    @Override
    public void setRestoreActivityValues() {

        BrowseHelper.saveBrowseActivityState(this);
    }


}

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
package com.stingray.qello.android.firetv.login.activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.amazon.android.utils.Helpers;
import com.stingray.qello.android.firetv.login.fragments.AccountCreationFragment;
import com.stingray.qello.android.firetv.login.R;

/**
 * This activity allows users to login with amazon.
 */
public class CreateAccountActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.acount_creation_main);
        Helpers.handleActivityEnterFadeTransition(this, 1500);

        Fragment accountCreationFragment = new AccountCreationFragment();
        accountCreationFragment.setArguments(savedInstanceState);

        getFragmentManager().beginTransaction().add(R.id.main_account_frame, accountCreationFragment, AccountCreationFragment.class.getSimpleName()).commit();
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


}
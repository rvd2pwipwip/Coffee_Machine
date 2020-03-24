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

import com.stingray.qello.firetv.android.adapters.ActionWidgetAdapter;
import com.stingray.qello.firetv.android.contentbrowser.ContentBrowser;
import com.stingray.qello.firetv.android.model.Action;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.Home.HomeFragment;
import com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.Explore.ContentSearchFragment;
import com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.MyQello.MyQelloFragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.v17.leanback.widget.OnChildViewHolderSelectedListener;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * BaseActivity class that handles common actions such as setting the font.
 */
public abstract class BaseActivity extends Activity {

    /**
     * Debug TAG.
     */
    private static final String TAG = BaseActivity.class.getSimpleName();

    // This is the currently selected action.
    private Action mSelectedAction;

    /**
     * Action widget adapter.
     */
    private ActionWidgetAdapter mActionWidgetAdapter;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        // This lets us get global font support.
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.v(TAG, "onActivityResult called with requestCode:" + requestCode +
                " resultCode:" + requestCode + " intent:" + data);
        super.onActivityResult(requestCode, resultCode, data);

        ContentBrowser.getInstance(this)
                      .handleOnActivityResult(requestCode, resultCode, data);
    }

    /**
     * This variable responds to items being selected in the view. It updates the selected action.
     */
    private final OnChildViewHolderSelectedListener mRowSelectedListener =
            new OnChildViewHolderSelectedListener() {
                @Override
                public void onChildViewHolderSelected(RecyclerView parent,
                                                      RecyclerView.ViewHolder view, int position,
                                                      int subposition) {

                    mSelectedAction = mActionWidgetAdapter.getAction(position);

                }
            };

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {

        super.onStart();

        // Get the Action widget container.
        VerticalGridView actionWidgetContainer =
                (VerticalGridView) findViewById(R.id.widget_grid_view);

        if (actionWidgetContainer != null) {

            // Create a new Action Widget Adapter
            mActionWidgetAdapter = new ActionWidgetAdapter(actionWidgetContainer);

            // Set adapter.
            actionWidgetContainer.setAdapter(mActionWidgetAdapter);

            // Add the actions to the widget adapter.
            mActionWidgetAdapter.addActions(ContentBrowser
                                                    .getInstance(this).getWidgetActionsList());

            // Set the selected listener for the child view of the selected listener.
            actionWidgetContainer.setOnChildViewHolderSelectedListener(mRowSelectedListener);
            // Set the on click listener for this widget container.
            actionWidgetContainer.setOnClickListener(
                    v -> this.actionTriggered( mSelectedAction));
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        setRestoreActivityValues();
    }

    /**
     * Use this method to set the
     * {@link com.stingray.qello.firetv.android.ui.constants.PreferencesConstants#LAST_ACTIVITY}
     * and {@link com.stingray.qello.firetv.android.ui.constants.PreferencesConstants#TIME_LAST_SAVED} values in
     * the {@link com.stingray.qello.firetv.android.utils.Preferences} instance. This will allow the activity to be
     * restored when the app launches.
     */
    public abstract void setRestoreActivityValues();

    public void actionTriggered(Action action) {
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch ((int) action.getId()) {
            case ContentBrowser.CONTENT_ACTION_SEARCH:
                Log.d(TAG, "actionTriggered -> CONTENT_ACTION_SEARCH");

                Fragment searchFragment = fragmentManager.findFragmentByTag(ContentSearchFragment.class.getSimpleName());
                if(searchFragment == null) {
                    searchFragment = new ContentSearchFragment();
                }

                fragmentTransaction.replace(R.id.main_detail, searchFragment, ContentSearchFragment.class.getSimpleName());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case ContentBrowser.CONTENT_ACTION_HOME:
                Log.d(TAG, "actionTriggered -> CONTENT_ACTION_HOME");

                Fragment homeFragment = fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName());
                if(homeFragment == null) {
                    homeFragment = new HomeFragment();
                }

                fragmentTransaction.replace(R.id.main_detail, homeFragment, HomeFragment.class.getSimpleName());
                fragmentTransaction.commit();
                break;
            case ContentBrowser.CONTENT_ACTION_MY_QELLO:
                Log.d(TAG, "actionTriggered -> CONTENT_ACTION_MY_QELLO");

                Fragment myQelloFragment = fragmentManager.findFragmentByTag(MyQelloFragment.class.getSimpleName());
                if(myQelloFragment == null) {
                    myQelloFragment = new MyQelloFragment();
                }

                fragmentTransaction.replace(R.id.main_detail, myQelloFragment, MyQelloFragment.class.getSimpleName());
                fragmentTransaction.commit();
                break;
            case ContentBrowser.CONTENT_ACTION_LOGIN_LOGOUT: {
                Log.d(TAG, "actionTriggered -> CONTENT_ACTION_LOGIN_LOGOUT");
                //loginLogoutActionTriggered(activity, action);
            }
            break;
        }
    }
}


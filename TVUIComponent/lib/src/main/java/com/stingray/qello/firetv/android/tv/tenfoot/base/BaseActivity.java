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
import android.view.View;

import com.stingray.qello.firetv.android.adapters.ActionWidgetAdapter;
import com.stingray.qello.firetv.android.contentbrowser.ContentBrowser;
import com.stingray.qello.firetv.android.event.SubscribeNowPopupEvent;
import com.stingray.qello.firetv.android.model.Action;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.Explore.ContentSearchFragment;
import com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.Home.HomeFragment;
import com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.MyQello.MyQelloFragment;
import com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.SubscribeNowFragment;
import com.stingray.qello.firetv.android.utils.Preferences;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.stingray.qello.firetv.android.contentbrowser.ContentBrowser.CONTENT_ACTION_HOME;
import static com.stingray.qello.firetv.android.contentbrowser.ContentBrowser.CONTENT_ACTION_MY_QELLO;
import static com.stingray.qello.firetv.android.contentbrowser.ContentBrowser.CONTENT_ACTION_SEARCH;

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
    private Integer selectedActionPosition = 0;

    private Map<String, Integer> fragmentAndActionIndexMap = new HashMap<>();

    /**
     * Action widget adapter.
     */
    private ActionWidgetAdapter mActionWidgetAdapter;
    private VerticalGridView actionWidgetContainer;

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
        actionWidgetContainer = findViewById(R.id.widget_grid_view);

        if (actionWidgetContainer != null && mActionWidgetAdapter == null) {
            // Create a new Action Widget Adapter
            mActionWidgetAdapter = new ActionWidgetAdapter(actionWidgetContainer);

            // Set adapter.
            actionWidgetContainer.setAdapter(mActionWidgetAdapter);

            // Add the actions to the widget adapter.
            ArrayList<Action> orderedActions = ContentBrowser.getInstance(this).getWidgetActionsList();
            mActionWidgetAdapter.addActions(orderedActions);

            for (int i = 0; i < orderedActions.size(); i++) {
                Action cAction = orderedActions.get(i);
                String fragmentTag = null;

                if (cAction.getId() == CONTENT_ACTION_HOME) {
                    fragmentTag = HomeFragment.class.getSimpleName();
                } else if (cAction.getId() == CONTENT_ACTION_SEARCH) {
                    fragmentTag = ContentSearchFragment.class.getSimpleName();
                } else if (cAction.getId() == CONTENT_ACTION_MY_QELLO) {
                    fragmentTag = MyQelloFragment.class.getSimpleName();
                }

                if (fragmentTag != null) {
                    fragmentAndActionIndexMap.put(fragmentTag, i);
                }
            }

            // Set the selected listener for the child view of the selected listener.
            actionWidgetContainer.setOnChildViewHolderSelectedListener(mRowSelectedListener);
            // Set the on click listener for this widget container.
            actionWidgetContainer.setOnClickListener(v -> this.actionTriggered(mSelectedAction));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        highlightSelectedNav(selectedActionPosition, false);
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

        String fragmentTag = null;

        switch ((int) action.getId()) {
            case ContentBrowser.CONTENT_ACTION_SEARCH:
                fragmentTag = ContentSearchFragment.class.getSimpleName();
                Log.d(TAG, "actionTriggered -> CONTENT_ACTION_SEARCH");

                Fragment searchFragment = fragmentManager.findFragmentByTag(fragmentTag);
                if(searchFragment == null) {
                    searchFragment = new ContentSearchFragment();
                }

                fragmentTransaction.replace(R.id.main_detail, searchFragment, fragmentTag);
                fragmentTransaction.addToBackStack(fragmentTag);
                fragmentTransaction.commit();
                break;
            case CONTENT_ACTION_HOME:
                Log.d(TAG, "actionTriggered -> CONTENT_ACTION_HOME");
                fragmentTag = HomeFragment.class.getSimpleName();

                Fragment homeFragment = fragmentManager.findFragmentByTag(fragmentTag);
                if(homeFragment == null) {
                    homeFragment = new HomeFragment();
                }

                fragmentTransaction.replace(R.id.main_detail, homeFragment, fragmentTag);
                fragmentTransaction.addToBackStack(fragmentTag);
                fragmentTransaction.commit();
                break;
            case ContentBrowser.CONTENT_ACTION_MY_QELLO:
                Log.d(TAG, "actionTriggered -> CONTENT_ACTION_MY_QELLO");
                fragmentTag = MyQelloFragment.class.getSimpleName();

                Fragment myQelloFragment = fragmentManager.findFragmentByTag(fragmentTag);
                if(myQelloFragment == null) {
                    myQelloFragment = new MyQelloFragment();
                }

                fragmentTransaction.replace(R.id.main_detail, myQelloFragment, fragmentTag);
                fragmentTransaction.addToBackStack(fragmentTag);
                fragmentTransaction.commit();
                break;
            case ContentBrowser.CONTENT_ACTION_LOGIN_LOGOUT: {
                Log.d(TAG, "actionTriggered -> CONTENT_ACTION_LOGIN_LOGOUT");
                //loginLogoutActionTriggered(activity, action);
            }
            break;
        }
        if (fragmentTag != null) {
            selectedActionPosition = fragmentAndActionIndexMap.get(fragmentTag);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onSubscribeNowPopupEvent(SubscribeNowPopupEvent subscribeNowPopupEvent) {
        if (!Preferences.getBoolean(com.stingray.qello.firetv.android.ui.constants.PreferencesConstants.HAS_SUBSCRIPTION)) {
            Fragment subscribeNowFragment = getFragmentManager().findFragmentByTag(SubscribeNowFragment.TAG);

            if (subscribeNowFragment == null) {
                // TODO Leo - Subscribe now fragment is always being created? I don't know why but need to persist it somewhere
                subscribeNowFragment = new SubscribeNowFragment();
            }

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(subscribeNowFragment, SubscribeNowFragment.TAG);
            ft.commit();
        }
    }

    private String getCurrentFragmentName(){
        FragmentManager fragmentManager = getFragmentManager();
        int index = fragmentManager.getBackStackEntryCount() - 1;

        if (index == -1) {
            return HomeFragment.class.getSimpleName();
        } else if (index > -1) {
            return fragmentManager.getBackStackEntryAt(index).getName();
        } else {
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String fragmentNameAfterBackPress = getCurrentFragmentName();
        if (fragmentNameAfterBackPress != null) {
            Integer actionIndex = fragmentAndActionIndexMap.get(fragmentNameAfterBackPress);
            highlightSelectedNav(actionIndex, true);
        }
    }

    private void highlightSelectedNav(Integer actionIndex, boolean requestFocus) {
        clearNavHighlight();
        setNavHighlight(actionIndex, requestFocus);
    }

    private void setNavHighlight(Integer actionIndex, boolean requestFocus) {
        if (actionWidgetContainer != null) {
            for (Integer i = 0; i < actionWidgetContainer.getChildCount(); i++) {
                if (i.equals(actionIndex)) {
                    View view = actionWidgetContainer.getChildAt(i);
                    view.setBackground(getResources().getDrawable(R.drawable.navigation_right_border));
                    View buttonView = view.findViewById(R.id.action_button);
                    if (buttonView != null && requestFocus) {
                        buttonView.requestFocus();
                    }
                    selectedActionPosition = actionIndex;
                }
            }
        }
    }

    private void clearNavHighlight() {
        if (actionWidgetContainer != null) {
            for (int i = 0; i < actionWidgetContainer.getChildCount(); i++) {
                actionWidgetContainer.getChildAt(i).setBackground(null);
            }
        }
    }
}


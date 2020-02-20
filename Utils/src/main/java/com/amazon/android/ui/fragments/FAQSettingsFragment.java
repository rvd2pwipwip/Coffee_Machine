package com.amazon.android.ui.fragments;

import android.app.Activity;
import android.app.FragmentManager;

import com.amazon.utils.R;

/**
 * Provides fragment to be shown when "FAQ" settings item is clicked.
 */

//TODO fix by language?
public class FAQSettingsFragment extends RemoteMarkdownFileFragment {
    private static final String TAG = FAQSettingsFragment.class.getSimpleName();

    public void createFragment(final Activity activity,
                               FragmentManager manager) {
        super.createFragment(activity, manager, activity.getString(R.string.faq_settings_fragment_tag), "https://legal.stingray.com/en/qello-faq/markdown" );
    }

    @Override
    public String getTag() {
        return TAG;
    }
}

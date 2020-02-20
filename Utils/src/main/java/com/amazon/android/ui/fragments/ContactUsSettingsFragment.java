package com.amazon.android.ui.fragments;

import android.app.Activity;
import android.app.FragmentManager;

import com.amazon.utils.R;

public class ContactUsSettingsFragment extends FileFragment {

    private static final String TAG = ContactUsSettingsFragment.class.getSimpleName();


    public void createFragment(final Activity activity, FragmentManager manager) {
        super.createFragment(activity, manager, activity.getString(R.string.contact_us_settings_fragment_tag), activity.getString(R.string.contact_us_file));
    }

    @Override
    public String getTag() {
        return TAG;
    }


}

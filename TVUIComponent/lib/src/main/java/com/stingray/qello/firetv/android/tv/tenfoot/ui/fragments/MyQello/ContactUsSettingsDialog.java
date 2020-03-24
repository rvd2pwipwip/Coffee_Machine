package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.MyQello;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.ui.fragments.AppInfoDialog;
import com.stingray.qello.firetv.android.ui.interfaces.SingleViewProvider;

public class ContactUsSettingsDialog extends AppInfoDialog {

    private static final String TAG = ContactUsSettingsDialog.class.getSimpleName();

    public void createFragment(final Activity activity, FragmentManager manager) {
        super.createFragment(activity, manager,"ContactUsSettingsFragment");
    }

    protected SingleViewProvider getSingleViewProvider(Context context) {
       return (context1, inflater, parent) -> {
            return mActivity.getLayoutInflater().inflate(R.layout.contact_layout, parent);
        };
    }


}
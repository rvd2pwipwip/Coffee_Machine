package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.MyQello;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.ui.fragments.AppInfoDialog;
import com.stingray.qello.firetv.android.ui.fragments.RemoteMarkdownFileFragment;
import com.stingray.qello.firetv.android.ui.interfaces.SingleViewProvider;

public class AboutSettingsDialog extends AppInfoDialog {

    private static final String TAG = AboutSettingsDialog.class.getSimpleName();

    public void createFragment(final Activity activity, FragmentManager manager) {
        super.createFragment(activity, manager,"AboutSettingsFragment");
    }

    protected SingleViewProvider getSingleViewProvider(Context context) {

        return (context1, inflater, parent) -> {
            final View view = mActivity.getLayoutInflater().inflate(R.layout.about_layout, parent);

            String termsTag = mActivity.getResources().getString(R.string.terms_settings_fragment_tag);
            String termsUrl = mActivity.getResources().getString(R.string.terms_settings_url);

            String privacyTag = mActivity.getResources().getString(R.string.privacy_settings_fragment_tag);
            String privacyUrl = mActivity.getResources().getString(R.string.privacy_settings_url);

            Button termsButton = view.findViewById(R.id.terms_button);
            termsButton.setOnClickListener(v -> new RemoteMarkdownFileFragment()
                    .createFragment(mActivity, mActivity.getFragmentManager(), termsTag, termsUrl));

            Button privacyButton = view.findViewById(R.id.privacy_button);
            privacyButton.setOnClickListener(v -> new RemoteMarkdownFileFragment()
                    .createFragment(mActivity, mActivity.getFragmentManager(), privacyTag, privacyUrl));

            return view;
        };
    }

}

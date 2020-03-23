package com.amazon.android.tv.tenfoot.ui.fragments.MyQello;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.amazon.android.tv.tenfoot.R;
import com.amazon.android.ui.fragments.AppInfoDialog;
import com.amazon.android.ui.fragments.RemoteMarkdownFileFragment;
import com.amazon.android.ui.interfaces.SingleViewProvider;

public class AboutSettingsDialog extends AppInfoDialog {

    private static final String TAG = AboutSettingsDialog.class.getSimpleName();

    public void createFragment(final Activity activity, FragmentManager manager) {
        super.createFragment(activity, manager,"AboutSettingsFragment");
    }

    protected SingleViewProvider getSingleViewProvider(Context context) {

        return (context1, inflater, parent) -> {
            final View view = mActivity.getLayoutInflater().inflate(R.layout.about_layout, parent);

            Button termsButton = (Button) view.findViewById(R.id.terms_button);
            termsButton.setOnClickListener(v -> new RemoteMarkdownFileFragment()
                    .createFragment(mActivity, mActivity.getFragmentManager(), mActivity.getString(com.amazon.utils.R.string.terms_settings_fragment_tag), "https://legal.stingray.com/en/qello-terms-and-conditions/markdown"));

            Button privacyButton = (Button) view.findViewById(R.id.privacy_button);
            privacyButton.setOnClickListener(v -> new RemoteMarkdownFileFragment()
                    .createFragment(mActivity, mActivity.getFragmentManager(), mActivity.getString(com.amazon.utils.R.string.privacy_settings_fragment_tag), "https://legal.stingray.com/en/privacy-policy/markdown"));

            return view;
        };
    }

}

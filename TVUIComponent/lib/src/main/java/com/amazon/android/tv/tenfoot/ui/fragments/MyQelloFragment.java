package com.amazon.android.tv.tenfoot.ui.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazon.android.adapters.ActionWidgetAdapter;
import com.amazon.android.configuration.ConfigurationManager;
import com.amazon.android.contentbrowser.ContentBrowser;
import com.amazon.android.model.Action;
import com.amazon.android.model.content.Content;
import com.amazon.android.tv.tenfoot.R;
import com.amazon.android.ui.constants.ConfigurationConstants;
import com.amazon.android.ui.fragments.ContactUsSettingsFragment;
import com.amazon.android.ui.fragments.LogoutSettingsFragment;
import com.amazon.android.ui.fragments.TermsSettingsFragment;
import com.amazon.android.ui.utils.BackgroundImageUtils;
import com.amazon.android.utils.GlideHelper;
import com.amazon.android.utils.Helpers;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

/**
 * MainActivity class that loads the ContentBrowseFragment.
 */
public class MyQelloFragment extends Fragment{

    private final String TAG = MyQelloFragment.class.getSimpleName();

    private static final int ACTIVITY_ENTER_TRANSITION_FADE_DURATION = 1500;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Helpers.handleActivityEnterFadeTransition(getActivity(), ACTIVITY_ENTER_TRANSITION_FADE_DURATION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.my_qello_layout, container, false);
        addListenerOnButton(view);
        return view;
    }

    public void addListenerOnButton(View view)  {
        Button contactUsButton = (Button) view.findViewById(R.id.contact_us_button);
        contactUsButton.setOnClickListener(v -> new ContactUsSettingsFragment()
                .createFragment(getActivity(),
                        getActivity().getFragmentManager(),
                        null));

        Button termsButton = (Button) view.findViewById(R.id.terms_button);
        termsButton.setOnClickListener(v -> new TermsSettingsFragment()
                .createFragment(getActivity(),
                        getActivity().getFragmentManager(),
                        null));
    }

}
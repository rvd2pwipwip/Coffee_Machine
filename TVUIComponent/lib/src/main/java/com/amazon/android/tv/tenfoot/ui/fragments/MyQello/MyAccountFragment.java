package com.amazon.android.tv.tenfoot.ui.fragments.MyQello;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.amazon.android.tv.tenfoot.R;
import com.amazon.android.ui.fragments.ContactUsSettingsFragment;
import com.amazon.android.ui.fragments.FAQSettingsFragment;
import com.amazon.android.utils.Helpers;

/**
 * MainActivity class that loads the ContentBrowseFragment.
 */
public class MyAccountFragment extends Fragment{

    private final String TAG = MyAccountFragment.class.getSimpleName();

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
        Button faqButton = (Button) view.findViewById(R.id.faq_button);
        faqButton.setOnClickListener(v -> new FAQSettingsFragment()
                .createFragment(getActivity(), getActivity().getFragmentManager()));

        Button contactUsButton = (Button) view.findViewById(R.id.contact_us_button);
        contactUsButton.setOnClickListener(v -> new ContactUsSettingsFragment()
                .createFragment(getActivity(), getActivity().getFragmentManager()));

        //TODO fix
        Button aboutButton = (Button) view.findViewById(R.id.about_button);
        aboutButton.setOnClickListener(v -> new ContactUsSettingsFragment()
                .createFragment(getActivity(), getActivity().getFragmentManager()));
    }

}
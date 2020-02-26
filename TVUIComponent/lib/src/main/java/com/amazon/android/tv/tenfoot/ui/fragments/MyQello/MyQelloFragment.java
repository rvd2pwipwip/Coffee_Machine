package com.amazon.android.tv.tenfoot.ui.fragments.MyQello;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.amazon.android.tv.tenfoot.R;
import com.amazon.android.utils.Helpers;

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
        Button settingsButton = (Button) view.findViewById(R.id.settings_button);
        settingsButton.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                FragmentManager fragmentManager = this.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Fragment settingsFragment = fragmentManager.findFragmentByTag(SettingsFragment.class.getSimpleName());
                if(settingsFragment == null) {
                    settingsFragment = new SettingsFragment();
                }

                fragmentTransaction.replace(R.id.my_qello_detail, settingsFragment, SettingsFragment.class.getSimpleName());
                fragmentTransaction.commit();
            }
        });

        Button history = (Button) view.findViewById(R.id.history_button);
        history.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                FragmentManager fragmentManager = this.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Fragment historyFragment = fragmentManager.findFragmentByTag(HistoryFragment.class.getSimpleName());
                if(historyFragment == null) {
                    historyFragment = new HistoryFragment();
                }

                fragmentTransaction.replace(R.id.my_qello_detail, historyFragment, HistoryFragment.class.getSimpleName());
                fragmentTransaction.commit();
            }
        });
    }

}
package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.MyQello;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.utils.Helpers;

public class FavoritesFragment extends Fragment {

    private final String TAG = FavoritesFragment.class.getSimpleName();

    private static final int ACTIVITY_ENTER_TRANSITION_FADE_DURATION = 1500;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Helpers.handleActivityEnterFadeTransition(getActivity(), ACTIVITY_ENTER_TRANSITION_FADE_DURATION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorites_layout, container, false);
    }

}
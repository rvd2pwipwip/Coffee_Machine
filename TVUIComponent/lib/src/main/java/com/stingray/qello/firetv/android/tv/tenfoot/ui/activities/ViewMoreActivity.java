package com.stingray.qello.firetv.android.tv.tenfoot.ui.activities;

import android.os.Bundle;
import android.util.Log;

import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.tv.tenfoot.base.BaseActivity;
import com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.Home.ViewMoreFragment;

public class ViewMoreActivity extends BaseActivity {
    private final static String TAG = ViewMoreActivity.class.getSimpleName();
    private ViewMoreFragment viewMoreFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate called.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_more_layout);

        viewMoreFragment = (ViewMoreFragment) getFragmentManager().findFragmentById
                (R.id.view_more_fragment);
    }

    @Override
    public void setRestoreActivityValues() {

    }
}

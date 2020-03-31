package com.stingray.qello.android.firetv.login.activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.stingray.qello.android.firetv.login.R;
import com.stingray.qello.android.firetv.login.fragments.PurchaseFragment;
import com.stingray.qello.firetv.android.utils.Helpers;

public class PurchaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.purchase_main);
        Helpers.handleActivityEnterFadeTransition(this, 1500);

        Fragment purchaseFragment = new PurchaseFragment();
        purchaseFragment.setArguments(savedInstanceState);

        getFragmentManager().beginTransaction().add(R.id.purchase_frame, purchaseFragment, PurchaseFragment.class.getSimpleName()).commit();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reportFullyDrawn();
    }

}

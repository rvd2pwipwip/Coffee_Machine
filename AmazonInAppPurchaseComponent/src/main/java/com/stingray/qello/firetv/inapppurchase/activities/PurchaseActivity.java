package com.stingray.qello.firetv.inapppurchase.activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.stingray.qello.firetv.android.utils.Helpers;
import com.stingray.qello.firetv.inapppurchase.R;
import com.stingray.qello.firetv.inapppurchase.fragment.PurchaseFragment;

public class PurchaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setContentView(R.layout.purchase_main);

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

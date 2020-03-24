package com.stingray.qello.firetv.android.ui.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Display;

import com.stingray.qello.firetv.android.ui.interfaces.SingleViewProvider;

public abstract class AppInfoDialog {

    protected Activity mActivity;

    public void createFragment(final Activity activity, FragmentManager manager, String tag) {
        mActivity = activity;
        final ReadDialogFragment dialog = new ReadDialogFragment();
        dialog.setContentViewProvider(getSingleViewProvider(activity));
        dialog.setArguments(getArguments(activity));
        commitFragment(manager, dialog, tag);
    }

    @NonNull
    protected Bundle getArguments(final Activity activity) {
        final Bundle args = new Bundle();
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        args.putInt(ReadDialogFragment.INTENT_EXTRA_DIALOG_HEIGHT, size.y);
        args.putInt(ReadDialogFragment.INTENT_EXTRA_DIALOG_WIDTH, size.x);
        return args;
    }

    protected void commitFragment(FragmentManager manager, ReadDialogFragment dialog, String fragmentTag) {
        final FragmentTransaction ft = (manager.beginTransaction());
        ft.add(dialog, fragmentTag);
        ft.commit();
    }

    protected abstract SingleViewProvider getSingleViewProvider(Context context);
}

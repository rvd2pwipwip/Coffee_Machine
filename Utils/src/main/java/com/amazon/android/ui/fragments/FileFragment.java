package com.amazon.android.ui.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.amazon.android.ui.interfaces.SingleViewProvider;
import com.amazon.android.utils.Helpers;
import com.amazon.utils.R;

//TODO fix extract common with markdonw file fragement
public abstract class FileFragment {

    private Activity mActivity;

    public abstract String getTag();

    public void createFragment(final Activity activity,  FragmentManager manager, String fragmentTag, String fileName) {

        final ReadDialogFragment dialog = new ReadDialogFragment();
        dialog.setContentViewProvider(getSingleViewProvider(activity, fileName));
        dialog.setArguments(getArguments(activity));
        commitFragment(manager, dialog, fragmentTag);
        mActivity = activity;
    }

    @NonNull
    private Bundle getArguments(final Activity activity) {
        final Bundle args = new Bundle();
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        args.putInt(ReadDialogFragment.INTENT_EXTRA_DIALOG_HEIGHT, size.y);
        args.putInt(ReadDialogFragment.INTENT_EXTRA_DIALOG_WIDTH, size.x);
        return args;
    }

    /*
     * Sets up the view to display the Notice Settings item content.
     */
    private SingleViewProvider getSingleViewProvider(Context context, String fileName) {

        String content = "";
        try {
            content = Helpers.getContentFromFile(context, fileName);
        }
        catch (Exception e) {
            Log.e(getTag(), "could not read " + fileName + " file", e);
        }

        final Spanned spanned = Html.fromHtml(content);

        return (context1, inflater, parent) -> {
            final View result = mActivity.getLayoutInflater().inflate(R.layout.read_dialog_default_layout, parent);
            final TextView mainText = (TextView) result.findViewById(R.id.txt);
            mainText.setText(spanned, TextView.BufferType.SPANNABLE);
            return result;
        };
    }

    private void commitFragment(FragmentManager manager, ReadDialogFragment dialog, String fragmentTag) {
        final FragmentTransaction ft = (manager.beginTransaction());
        ft.add(dialog, fragmentTag);
        ft.commit();
    }
}

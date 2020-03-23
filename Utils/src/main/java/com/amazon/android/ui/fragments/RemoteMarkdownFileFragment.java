package com.amazon.android.ui.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.amazon.android.ui.interfaces.SingleViewProvider;
import com.amazon.android.utils.NetworkUtils;
import com.amazon.utils.R;

import java.io.IOException;

import io.noties.markwon.Markwon;

public class RemoteMarkdownFileFragment {

    private Activity mActivity;

    public void createFragment(final Activity activity,  FragmentManager manager, String fragmentTag, String url) {

        final ReadDialogFragment dialog = new ReadDialogFragment();
        dialog.setContentViewProvider(getSingleViewProvider(activity, url));
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
    private SingleViewProvider getSingleViewProvider(Context context, String url) {

        String content = "";
        try {
            content = new LoadDataTask().execute(url).get();
        }
        catch (Exception e) {
            Log.e("RemoteMarkdownFileFra", "could not read " + url + " data", e);
        }

        final String fcontent = content;

        return (context1, inflater, parent) -> {
            final Markwon markwon = Markwon.create(context);
            final View result = mActivity.getLayoutInflater().inflate(R.layout.read_dialog_default_layout, parent);
            final TextView mainText = (TextView) result.findViewById(R.id.txt);
            Spanned spanned = markwon.toMarkdown(fcontent);
            mainText.setText(spanned, TextView.BufferType.SPANNABLE);
            return result;
        };
    }

    private void commitFragment(FragmentManager manager, ReadDialogFragment dialog, String fragmentTag) {
        final FragmentTransaction ft = (manager.beginTransaction());
        ft.add(dialog, fragmentTag);
        ft.commit();
    }

    private class LoadDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return NetworkUtils.getDataLocatedAtUrl(urls[0]);
            } catch (IOException e) {
                Log.e("RemoteMarkdownFileFrag", "Could not read at URL", e);
            }
            return null;
        }
    }
}

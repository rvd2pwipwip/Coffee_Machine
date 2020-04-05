package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.MyQello;

import android.util.Log;

import com.stingray.qello.firetv.android.contentbrowser.callable.ClearBrowsePageCallable;
import com.stingray.qello.firetv.android.tv.tenfoot.R;

public class HistoryFragment extends BrowsePageFragment {

    @Override
    protected String getBrowsePage() {
        return "HISTORY";
    }

    @Override
    protected int getEmptyMsgDrawable() {
        return R.drawable.empty_history_msg;
    }

    @Override
    protected int getEmptyImageDrawable() {
        return R.drawable.history;
    }

    @Override
    protected String getClearButtonText() {
        return "Clear History";
    }

    @Override
    protected void setActionButtonListener() {
        actionButton1.setOnClickListener(v -> {
            // Clear Favorites
            observableFactory.create(new ClearBrowsePageCallable("history"))
                    .subscribe(voidObject -> {
                        clearContents();
                    }, throwable -> {
                        Log.e(getTag(), "Failed to clear history");
                    });
        });
    }
}

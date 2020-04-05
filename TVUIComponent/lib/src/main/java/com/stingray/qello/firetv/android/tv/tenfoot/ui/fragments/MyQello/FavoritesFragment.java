package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.MyQello;

import android.util.Log;

import com.stingray.qello.firetv.android.contentbrowser.callable.ClearBrowsePageCallable;
import com.stingray.qello.firetv.android.tv.tenfoot.R;

public class FavoritesFragment extends BrowsePageFragment {

    @Override
    protected String getBrowsePage() {
        return "FAVORITE";
    }

    @Override
    protected int getEmptyMsgDrawable() {
        return R.drawable.empty_favorites_msg;
    }

    @Override
    protected int getEmptyImageDrawable() {
        return R.drawable.like;
    }

    @Override
    protected String getClearButtonText() {
        return "Clear Favorites";
    }

    @Override
    protected void setActionButtonListener() {
        actionButton1.setOnClickListener(v -> {
            // Clear Favorites
            observableFactory.create(new ClearBrowsePageCallable("favorites"))
                    .subscribe(voidObject -> {
                        clearContents();
                    }, throwable -> {
                        Log.e(getTag(), "Failed to clear favorites");
                    });
        });
    }
}
/**
 * Copyright 2015-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * A copy of the License is located at
 * <p>
 * http://aws.amazon.com/apache2.0/
 * <p>
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.stingray.qello.firetv.android.tv.tenfoot.presenter;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.stingray.qello.firetv.android.configuration.ConfigurationManager;
import com.stingray.qello.firetv.android.model.content.Content;
import com.stingray.qello.firetv.android.model.content.ContentWithTracks;
import com.stingray.qello.firetv.android.model.content.Track;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.tv.tenfoot.base.TenFootApp;
import com.stingray.qello.firetv.android.ui.constants.ConfigurationConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

/**
 * An {@link Presenter} for rendering the detailed description of an item.
 * The description needs to have a title and subtitle.
 */
public class ContentTrackListPresenter extends Presenter {

    private final static String TAG = ContentTrackListPresenter.class.getSimpleName();

    private Context mContext;

    /**
     * View holder for the details description. It contains title, subtitle, and body text views.
     */
    public static class ViewHolder extends Presenter.ViewHolder {

        private final TextView mTitle;
        private final TextView mBody;
        private final TableLayout mContentTrackListTable;

        public ViewHolder(final View view) {

            super(view);
            mTitle = (TextView) view.findViewById(R.id.details_description_title);
            mBody = (TextView) view.findViewById(R.id.details_description_body);
            mContentTrackListTable = (TableLayout) view.findViewById(R.id.content_track_list_table);
        }

        public TextView getTitle() {

            return mTitle;
        }

        public TextView getBody() {
            return mBody;
        }

        public TableLayout getContentTrackListTable() {
            return mContentTrackListTable;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent) {

        Log.v(TAG, "onCreateViewHolder called.");

        mContext = parent.getContext();

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.details_content_track_list_layout, parent,
                        false);

        return new ViewHolder(view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {

        Log.v(TAG, "onBindViewHolder called.");
        ViewHolder customViewHolder = (ViewHolder) viewHolder;
        onBindDescription(customViewHolder, item);
    }

    private void onBindDescription(ViewHolder viewHolder, Object item) {

        Log.v(TAG, "onBindDescription called.");
        ContentWithTracks contentWithTracks = (ContentWithTracks) item;

        if (contentWithTracks != null) {
            populateViewHolder(viewHolder, contentWithTracks);
        } else {
            Log.e(TAG, "Content is null in onBindDescription");
        }
    }

    private void populateViewHolder(ViewHolder viewHolder, ContentWithTracks contentWithTracks) {
        Content content = contentWithTracks.getContent();

        ConfigurationManager config = ConfigurationManager.getInstance(TenFootApp.getInstance());

        viewHolder.getTitle().setEllipsize(TextUtils.TruncateAt.END);
        viewHolder.getTitle().setSingleLine();

        String title = content.getSubtitle() + " - " + content.getTitle();
        String body = contentWithTracks.getTracks().size() + " tracks";

        viewHolder.getTitle().setText(title);
        CalligraphyUtils.applyFontToTextView(TenFootApp.getInstance(), viewHolder.getTitle(),
                config.getTypefacePath(ConfigurationConstants.LIGHT_FONT));

        viewHolder.getBody().setText(body);

        viewHolder.getContentTrackListTable().removeAllViews();


        for (int i = 0; i < contentWithTracks.getTracks().size(); i++) {
            Track track = contentWithTracks.getTracks().get(i);
            TableLayout tableLayout = viewHolder.getContentTrackListTable();
            View row = LayoutInflater.from(mContext).inflate(R.layout.content_track_list_row_layout, tableLayout, false);
            TextView titleView = row.findViewById(R.id.track_title);
            titleView.setText(track.getTitle());

            TextView trackIndex = row.findViewById(R.id.track_index);
            trackIndex.setText(String.valueOf(i + 1));

            TextView subtitleView = row.findViewById(R.id.track_subtitle);
            subtitleView.setText(track.getSubtitle());

            TextView durationView = row.findViewById(R.id.track_duration);
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.US);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String duration = formatter.format(new Date(track.getDuration()));
            durationView.setText(duration);

            // TODO We'll need to bind the actions later
//            Button addToPlaylistBtn = (Button) row.findViewById(R.id.add_track_to_playlist_button);
//            Button addToFavoritesBtn = (Button) row.findViewById(R.id.add_track_to_favorites);

            tableLayout.addView(row);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder holder) {

        Log.v(TAG, "onViewAttachedToWindow called.");
        ViewHolder customViewHolder = (ViewHolder) holder;
        super.onViewAttachedToWindow(customViewHolder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewDetachedFromWindow(Presenter.ViewHolder holder) {

        Log.v(TAG, "onViewDetachedFromWindow called.");
        super.onViewDetachedFromWindow(holder);
    }

}

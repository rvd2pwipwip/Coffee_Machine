package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.Home;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.stingray.qello.firetv.android.configuration.ConfigurationManager;
import com.stingray.qello.firetv.android.contentbrowser.callable.model.SvodConcert;
import com.stingray.qello.firetv.android.model.SvodImage;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.tv.tenfoot.base.TenFootApp;
import com.stingray.qello.firetv.android.ui.constants.ConfigurationConstants;
import com.stingray.qello.firetv.android.ui.fragments.FullScreenDialogFragment;
import com.stingray.qello.firetv.android.utils.GlideHelper;
import com.stingray.qello.firetv.android.utils.Helpers;
import com.stingray.qello.firetv.android.utils.SvodObjectMapperProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

public class ContentReadMoreFragment extends FullScreenDialogFragment {
    public static final String TAG = ContentReadMoreFragment.class.getSimpleName();

    private static final int DETAIL_THUMB_WIDTH = 280;
    private static final int DETAIL_THUMB_HEIGHT = 367;

    private BackgroundManager mBackgroundManager;
    private DisplayMetrics mMetrics;
    private Map<String, String> imageMap = new HashMap<>();
    private boolean initFailed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_read_more_layout, container);
    }

    @Override
    public final Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        final Window dialogWindow = dialog.getWindow();

        mMetrics = new DisplayMetrics();

        if (dialogWindow != null) {
            dialogWindow.getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
            mBackgroundManager = BackgroundManager.getInstance(getActivity());
        }

        return dialog;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View backButton = view.findViewById(R.id.nav_back_button);
        backButton.setOnClickListener(v -> dismiss());

        SvodConcert svodConcert = null;

        try {
            String svodConcertString = getArguments().getString("svodConcert");
            if (svodConcertString != null) {
                svodConcert = new SvodObjectMapperProvider().get().readValue(svodConcertString, SvodConcert.class);
                databind(view, svodConcert);
            } else {
                Log.e(TAG, "Missing content info in arguments");
                initFailed = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to read current content info", e);
            initFailed = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (initFailed) {
            Toast toast = Toast.makeText(getActivity(), "Unable to open read more page", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 100);
            toast.show();
            dismiss();
        }
    }

    private void databind(View view, SvodConcert svodConcert) {
        for (SvodImage svodImage : svodConcert.getImages()) {
            imageMap.put(svodImage.getType(), svodImage.getUrl());
        }

        View thumbnailPosterView = view.findViewById(R.id.content_read_more_poster);

        TextView title = view.findViewById(R.id.content_read_more_title);
        TextView subtitle = view.findViewById(R.id.content_read_more_subtitle);
        TextView yearRuntime = view.findViewById(R.id.content_read_more_year_runtime);
        TextView fullDescription = view.findViewById(R.id.content_read_more_full_description);

        int width = Helpers.convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = Helpers.convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT);

        SimpleTarget<Bitmap> posterBitmapTarget = new SimpleTarget<Bitmap>(width, height) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Log.d(TAG, "content_details_activity_layout overview card image url ready: " + resource);
                int cornerRadius = getResources().getInteger(R.integer.details_overview_image_corner_radius);

                Bitmap bitmap = Helpers.roundCornerImage(getActivity(), resource, cornerRadius);
                thumbnailPosterView.setBackground(new BitmapDrawable(getActivity().getResources(), bitmap));
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                thumbnailPosterView.setBackground(errorDrawable);
            }
        };

        GlideHelper.loadImageIntoSimpleTargetBitmap(getActivity(),
                imageMap.get("THUMBNAIL_PORTRAIT"),
                new GlideHelper.LoggingListener<>(),
                R.drawable.default_poster,
                posterBitmapTarget);

        if (mBackgroundManager.getDrawable() != null) {
            view.setBackground(mBackgroundManager.getDrawable());
        } else {
            view.setBackground(getResources().getDrawable(R.drawable.background_view_more));
        }

        ConfigurationManager config = ConfigurationManager.getInstance(TenFootApp.getInstance());

        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setSingleLine();
        title.setText(svodConcert.getTitle());

        CalligraphyUtils.applyFontToTextView(TenFootApp.getInstance(), title, config.getTypefacePath(ConfigurationConstants.BOLD_FONT));

        subtitle.setText(svodConcert.getSubtitle());

        String YEAR = "Year: ";
        String RUNTIME = " / Runtime: ";

        DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String duration = formatter.format(new Date(svodConcert.getDuration()));
        String yearRuntimeText = YEAR + svodConcert.getConcertYear() + RUNTIME + duration;

        yearRuntime.setText(yearRuntimeText);

        fullDescription.setText(svodConcert.getFullDescription().trim());
        CalligraphyUtils.applyFontToTextView(TenFootApp.getInstance(), fullDescription, config.getTypefacePath(ConfigurationConstants.LIGHT_FONT));
    }
}

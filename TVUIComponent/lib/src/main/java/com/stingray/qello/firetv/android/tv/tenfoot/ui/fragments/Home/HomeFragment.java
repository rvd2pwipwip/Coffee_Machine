package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.Home;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stingray.qello.firetv.android.configuration.ConfigurationManager;
import com.stingray.qello.firetv.android.model.content.Content;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.ui.constants.ConfigurationConstants;
import com.stingray.qello.firetv.android.ui.utils.BackgroundImageUtils;
import com.stingray.qello.firetv.android.utils.GlideHelper;
import com.stingray.qello.firetv.android.utils.Helpers;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

/**
 * MainActivity class that loads the ContentBrowseFragment.
 */
public class HomeFragment extends Fragment implements ContentBrowseFragment
        .OnBrowseRowListener {

    private final String TAG = HomeFragment.class.getSimpleName();

    private static final int CONTENT_IMAGE_CROSS_FADE_DURATION = 1000;
    private static final int ACTIVITY_ENTER_TRANSITION_FADE_DURATION = 1500;
    private static final int UI_UPDATE_DELAY_IN_MS = 0;

    private TextView mContentArtist;
    private TextView mContentTitle;
    private TextView mContentDescription;
    private ImageView mContentImage;
    private Subscription mContentImageLoadSubscription;

    // View that contains the background
    private View mMainFrame;
    private Drawable mBackgroundWithPreview;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Helpers.handleActivityEnterFadeTransition(getActivity(), ACTIVITY_ENTER_TRANSITION_FADE_DURATION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        final View view = inflater.inflate(R.layout.home_layout, container, false);

        if (view != null) {

            mContentArtist = (TextView) view.findViewById(R.id.content_detail_artist);
            CalligraphyUtils.applyFontToTextView(getActivity(), mContentArtist, ConfigurationManager
                    .getInstance(getActivity()).getTypefacePath(ConfigurationConstants.LIGHT_FONT));

            mContentTitle = (TextView) view.findViewById(R.id.content_detail_title);
            CalligraphyUtils.applyFontToTextView(getActivity(), mContentTitle, ConfigurationManager
                    .getInstance(getActivity()).getTypefacePath(ConfigurationConstants.BOLD_FONT));

            mContentDescription = (TextView) view.findViewById(R.id.content_detail_description);
            CalligraphyUtils.applyFontToTextView(getActivity(), mContentDescription, ConfigurationManager
                    .getInstance(getActivity()).getTypefacePath(ConfigurationConstants.LIGHT_FONT));

            mContentImage = (ImageView) view.findViewById(R.id.content_image);

            mContentImage.setImageURI(Uri.EMPTY);

            // Get display/background size
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point windowSize = new Point();
            display.getSize(windowSize);
            int imageWidth = (int) getResources().getDimension(R.dimen.content_image_width);
            int imageHeight = (int) getResources().getDimension(R.dimen.content_image_height);
            int gradientSize = (int) getResources().getDimension(R.dimen.content_image_gradient_size);
            // Create the background
            Bitmap background =
                    BackgroundImageUtils.createBackgroundWithPreviewWindow(
                            windowSize.x,
                            windowSize.y,
                            imageWidth,
                            imageHeight,
                            gradientSize,
                            ContextCompat.getColor(getActivity(), R.color.browse_background_color));
            mBackgroundWithPreview = new BitmapDrawable(getResources(), background);
            // Set the background
            mMainFrame = view.findViewById(R.id.main_frame);
            mMainFrame.setBackground(mBackgroundWithPreview);

            Fragment contentBrowseFragment = getFragmentManager().findFragmentById(R.id.content_browse_fragment);
            if(contentBrowseFragment == null) {
                contentBrowseFragment = new ContentBrowseFragment();
                contentBrowseFragment.setArguments(savedInstanceState);
            }
            getFragmentManager().beginTransaction().replace(R.id.content_browse, contentBrowseFragment).commit();

        }
        return view;
    }

    /**
     * {@inheritDoc}
     * Called by the browse fragment ({@link ContentBrowseFragment}. Switches the content
     * title, description, and image.
     */
    @Override
    public void onItemSelected(Object item) {

        if (item instanceof Content) {
            Content content = (Content) item;
            callImageLoadSubscription(content.getSubtitle(),
                                      content.getTitle(),
                                      content.getDescription(),
                                      content.getBackgroundImageUrl());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mContentImageLoadSubscription != null) {
            mContentImageLoadSubscription.unsubscribe();
        }
        Log.d(TAG, "Home Fragment destroyed");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Helper method to subscribe the selected item to the observable that will load the content
     * image into the background. It is okay for the background image URL to be null. A null URL
     * will result in showing the default background.
     *
     * @param title       The title to display.
     * @param description The description to display.
     * @param bgImageUrl  The URL of the image to display.
     */
    public void callImageLoadSubscription(String artistName, String title, String description, String bgImageUrl) {

        mContentImageLoadSubscription = Observable
                .timer(UI_UPDATE_DELAY_IN_MS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()) // This is a must for timer.
                .subscribe(c -> {
                    mContentArtist.setText(artistName);
                    mContentTitle.setText(title);
                    mContentDescription.setText(description);
                    GlideHelper.loadImageWithCrossFadeTransition(getActivity().getApplicationContext(),
                            mContentImage,
                            bgImageUrl,
                            CONTENT_IMAGE_CROSS_FADE_DURATION,
                            R.color.browse_background_color);

                    // If there is no image, remove the preview window
                    if (bgImageUrl != null && !bgImageUrl.isEmpty()) {
                        mMainFrame.setBackground(mBackgroundWithPreview);
                    } else {
                        mMainFrame.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable
                                .background_my_qello));
                    }
                });

    }

}

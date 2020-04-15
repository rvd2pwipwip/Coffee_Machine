package android.support.v17.leanback.widget;

import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stingray.qello.firetv.android.configuration.ConfigurationManager;
import com.stingray.qello.firetv.android.contentbrowser.ContentBrowser;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.ui.constants.ConfigurationConstants;

import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

public class ActionButtonPresenter extends Presenter {



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_button_layout,
                parent, false);
        v.requestFocus();
        return new ActionPresenterSelector.ActionViewHolder(v, parent.getLayoutDirection());
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Action action = (Action) item;
        final ActionPresenterSelector.ActionViewHolder vh = (ActionPresenterSelector
                .ActionViewHolder) viewHolder;
        vh.mAction = action;
        vh.mButton.setText(action.getLabel1());
        if(action.getIcon() != null) {
            vh.mButton.setCompoundDrawablesWithIntrinsicBounds(action.getIcon(), null, null, null);
            vh.view.setPaddingRelative(15, 0, 15, 0);
        } else {
            vh.mButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        if(action.getId() == ContentBrowser.CONTENT_ACTION_START_FREE_TRIAL) {
            vh.mButton.setBackground(ContextCompat.getDrawable(vh.view.getContext(), R.drawable.text_input_frame_state_blue));
            vh.mButton.setTextColor(ContextCompat.getColor(vh.view.getContext(), R.color.accent));
            CalligraphyUtils.applyFontToTextView(vh.view.getContext(), vh.mButton, ConfigurationManager
                    .getInstance(vh.view.getContext()).getTypefacePath(ConfigurationConstants.BOLD_FONT));
        }

        vh.view.requestFocus();
        vh.mButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE &&
                        event.getAction() == KeyEvent.ACTION_DOWN) {
                    vh.mButton.performClick();
                }
                return false;
            }
        });
        //  setCommomButtonProperties(vh.mButton);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ((ActionPresenterSelector.ActionViewHolder) viewHolder).mAction = null;
    }
}

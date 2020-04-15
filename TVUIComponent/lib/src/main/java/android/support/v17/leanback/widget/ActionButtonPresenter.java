package android.support.v17.leanback.widget;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stingray.qello.firetv.android.tv.tenfoot.R;

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
        }
        vh.view.requestFocus();
        vh.view.setPaddingRelative(15, 0, 15, 0);
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

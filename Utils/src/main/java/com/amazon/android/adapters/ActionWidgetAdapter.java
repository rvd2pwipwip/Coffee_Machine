package com.amazon.android.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.support.annotation.MainThread;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.amazon.android.model.Action;
import com.amazon.utils.R;

import java.util.ArrayList;

/**
 * This class extends the {@link android.support.v7.widget.RecyclerView.Adapter}
 * It is used to display Actions items.
 */
public class ActionWidgetAdapter extends RecyclerView.Adapter {

    // A list of action items that are presented in the view.
    private final ArrayList<Action> mActionsList;

    private static final int WINDOW_ALIGNMENT_OFFSET_PERCENT = 0;

    // The HorizontalGridView that is used to display the items.
    private VerticalGridView verticalGridView;


    public ActionWidgetAdapter(VerticalGridView inputHorizontalGridView) {
        this(inputHorizontalGridView, new ArrayList<>());
    }

    public ActionWidgetAdapter(VerticalGridView inputVerticalGridView,
                               ArrayList<Action> actions) {

        // Set the vertical grid view to the input view.
        verticalGridView = inputVerticalGridView;

        // Set the vertical grid view alignment.
        verticalGridView.setWindowAlignment(VerticalGridView.WINDOW_ALIGN_BOTH_EDGE);
        verticalGridView.setWindowAlignmentOffsetPercent(WINDOW_ALIGNMENT_OFFSET_PERCENT);

        // Set the adapter of the vertical grid view.
        verticalGridView.setAdapter(this);

        mActionsList = new ArrayList<>();

        // Set the actions.
        if (actions != null) {
            addActions(actions);
        }
    }

    /**
     * This method will add an {@link ArrayList} of actions to the action widget.
     * This method must be called on the main UI thread.
     *
     * @param inputActions An {@link ArrayList} of actions that needs to be added.
     */
    @MainThread
    public void addActions(ArrayList<Action> inputActions) {

        mActionsList.addAll(inputActions);

        setVerticalGridViewSize();

        // Notify the adapter that new items have been added.
        // This call needs to be on the main UI thread.
        notifyDataSetChanged();
    }

    public Action getAction(int position) {
        return mActionsList.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout.
        View contactView = inflater.inflate(R.layout.action_item, parent, false);

        // Return a new holder instance.
        return new ViewHolder(contactView);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param baseHolder The view holder.
     * @param position   The position of the view.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder baseHolder, int position) {

        Action action = mActionsList.get(position);

        // Set item views based on the data model.
        ((ViewHolder) baseHolder).actionButton.setImageResource(action.getIconResourceId());
        ((ViewHolder) baseHolder).actionButton.setTag(action.getName());

        ((ViewHolder) baseHolder).actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verticalGridView.performClick();
            }
        });

        // Set the color changing actions for the items.
        ((ViewHolder) baseHolder).actionButton.setOnFocusChangeListener((v, hasFocus) -> {

            if (hasFocus) {
                int color = v.getContext().getResources().getColor(R.color.search_orb);
                v.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                v.invalidate();
            }
            else {
                v.getBackground().clearColorFilter();
                v.invalidate();
            }
        });
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return int, that represents the number of items in the list.
     */
    @Override
    public int getItemCount() {
        return mActionsList.size();
    }

    private void setVerticalGridViewSize() {

        Resources res = verticalGridView.getContext().getResources();

        ViewGroup.LayoutParams params = verticalGridView.getLayoutParams();

        params.width = res.getDimensionPixelSize(R.dimen.action_widget_width) + res.getDimensionPixelSize(R.dimen.grid_view_left_right_padding);
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        verticalGridView.setLayoutParams(params);
    }

    /**
     * This class describes an item view and metadata about its place within the RecyclerView.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageButton actionButton;

        public ViewHolder(View v) {
            super(v);
            actionButton = (ImageButton) itemView.findViewById(R.id.action_button);
        }
    }
}
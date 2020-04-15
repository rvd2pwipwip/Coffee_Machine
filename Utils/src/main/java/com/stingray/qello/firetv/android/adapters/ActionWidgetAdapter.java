package com.stingray.qello.firetv.android.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.MainThread;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.stingray.qello.firetv.android.model.Action;
import com.stingray.qello.firetv.utils.R;

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

        int margin = verticalGridView.getContext().getResources().getDimensionPixelSize(R.dimen.action_widget_item_margin);
        verticalGridView.setItemMargin(margin);

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

        //setVerticalGridViewSize();

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

        ViewHolder viewHolder = ((ViewHolder) baseHolder);

        // Set item views based on the data model.
        viewHolder.actionButton.setImageResource(action.getIconResourceId());
        viewHolder.actionButton.setTag(action.getName());

        Drawable bg = verticalGridView.getContext().getResources().getDrawable(R.drawable.navigation_left_border);
        if (position == 0) {
            viewHolder.parentView.setBackground(bg);
        }

        viewHolder.actionButton.setOnClickListener(v -> {
            for (int i = 0; i < verticalGridView.getChildCount(); i++) {
                View view = verticalGridView.getChildAt(i);
                view.setBackground(null);
            }
            viewHolder.parentView.setBackground(bg);
            verticalGridView.performClick();
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

        params.width = res.getDimensionPixelSize(R.dimen.action_widget_width);
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        verticalGridView.setLayoutParams(params);
    }

    /**
     * This class describes an item view and metadata about its place within the RecyclerView.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageButton actionButton;
        public final View parentView;

        public ViewHolder(View v) {
            super(v);
            this.parentView = v;
            actionButton = itemView.findViewById(R.id.action_button);
        }
    }
}
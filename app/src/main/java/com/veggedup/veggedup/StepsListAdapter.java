package com.veggedup.veggedup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class StepsListAdapter extends RecyclerView.Adapter<StepsListAdapter.StepsViewHolder> {
    private Context mContext;
    private List<Map<String, String>> mSteps;

    public StepsListAdapter(Context context, List<Map<String, String>> ingredients) {
        this.mContext = context;
        this.mSteps = ingredients;
    }

    @Override
    public StepsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.step_list_item, parent, false);
        return new StepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepsViewHolder holder, int position) {
        String step = mSteps.get(position).get("step");

        holder.stepTextView.setText(step);
    }

    @Override
    public int getItemCount() {
        return mSteps.size();
    }

    class StepsViewHolder extends RecyclerView.ViewHolder {

        TextView stepTextView;

        public StepsViewHolder(View itemView) {
            super(itemView);
            stepTextView = (TextView) itemView.findViewById(R.id.stepTextView);
        }
    }
}

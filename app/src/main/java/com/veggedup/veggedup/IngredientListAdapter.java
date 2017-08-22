package com.veggedup.veggedup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class IngredientListAdapter extends RecyclerView.Adapter<IngredientListAdapter.IngredientViewHolder> {
    private Context mContext;
    private List<Map<String, String>> mIngredients;

    public IngredientListAdapter(Context context, List<Map<String, String>> ingredients) {
        this.mContext = context;
        this.mIngredients = ingredients;
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.ingredient_list_item, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        String qty = mIngredients.get(position).get("qty");
        String uom = mIngredients.get(position).get("uom");
        String name = mIngredients.get(position).get("name");

        holder.qtyTextView.setText(qty);
        holder.uomTextView.setText(uom);
        holder.nameTextView.setText(name);
    }

    @Override
    public int getItemCount() {
        return mIngredients.size();
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {

        TextView qtyTextView;
        TextView uomTextView;
        TextView nameTextView;

        public IngredientViewHolder(View itemView) {
            super(itemView);
            qtyTextView = (TextView) itemView.findViewById(R.id.qtyTextView);
            uomTextView = (TextView) itemView.findViewById(R.id.uomTextView);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
        }
    }
}

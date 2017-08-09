package com.veggedup.veggedup;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.veggedup.veggedup.data.VeggedupContract;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder> {

    // Holds on to the cursor to display the recipes
    private Cursor mCursor;
    private Context mContext;


    final private RecipeListAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface RecipeListAdapterOnClickHandler {
        void onClick(int recipeId);
    }

    /**
     * Constructor using the context and the db cursor
     * @param context the calling context/activity
     * @param cursor the db cursor with recipe data to display
     */
    public RecipeListAdapter(Context context, Cursor cursor, RecipeListAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;
        this.mCursor = cursor;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        // Move the mCursor to the position of the item to be displayed
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null

        // Update the view holder with the information needed to display
        String name = mCursor.getString(mCursor.getColumnIndex(VeggedupContract.Recipe.COLUMN_TITLE));

        String type = mCursor.getString(mCursor.getColumnIndex(VeggedupContract.Recipe.COLUMN_TYPE));

        // Retrieve the id from the cursor and
        long id = mCursor.getLong(mCursor.getColumnIndex(VeggedupContract.Recipe._ID));

        // Display the recipe title
        holder.titleTextView.setText(name);

        holder.typeTextView.setText(type);

        // Set the tag of the itemview in the holder to the id
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    /**
     * Swaps the Cursor currently held in the adapter with a new one
     * and triggers a UI refresh
     *
     * @param newCursor the new cursor that will replace the existing one
     */
    public void swapCursor(Cursor newCursor) {
        // Always close the previous mCursor first
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    /**
     * Inner class to hold the views needed to display a single item in the recycler-view
     */
    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Will display the recipe title
        TextView titleTextView;
        TextView typeTextView;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews
         *
         * @param itemView The View that you inflated in
         *                 {@link RecipeListAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public RecipeViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title_text_view);
            typeTextView = (TextView) itemView.findViewById(R.id.type_text_view);

            itemView.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click. We fetch the date that has been
         * selected, and then call the onClick handler registered with this adapter, passing that
         * date.
         *
         * @param v the View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int recipeId = mCursor.getInt(mCursor.getColumnIndex(VeggedupContract.Recipe.COLUMN_RECIPE_ID));
            mClickHandler.onClick(recipeId);
        }
    }
}
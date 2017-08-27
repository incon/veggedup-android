package com.veggedup.veggedup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class IngredientsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);

        initIngredients();
        RecyclerView ingredientsRecyclerView = (RecyclerView) view.findViewById(R.id.ingredientsRecyclerView);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        IngredientListAdapter mAdapter = new IngredientListAdapter(getContext(), ingredientsList);
        ingredientsRecyclerView.setAdapter(mAdapter);

        return view;
    }

    List<Map<String, String>> ingredientsList = new ArrayList<>();

    private void initIngredients() {
        RecipeDetailActivity activity = (RecipeDetailActivity) getActivity();

        try {
            JSONArray ingredientRoot = new JSONArray(activity.getIngredientsJSON());

            for (int i = 0; i < ingredientRoot.length(); i++) {
                JSONObject ingredientObject = ingredientRoot.getJSONObject(i);
                HashMap<String, String> ingredient = new HashMap<>();
                ingredient.put("qty", ingredientObject.getString("qty"));
                ingredient.put("uom", ingredientObject.getString("uom"));
                ingredient.put("name", ingredientObject.getString("name"));
                ingredientsList.add(ingredient);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

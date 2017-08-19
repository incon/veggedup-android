package com.veggedup.veggedup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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

    public IngredientsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);

        initIngredients();
        ListView listView = (ListView) view.findViewById(R.id.ingredientsList);
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), ingredientsList, R.layout.ingredient_list_item, new String[] {"qty", "uom", "name"}, new int[] {R.id.qtyTextView, R.id.uomTextView, R.id.nameTextView});
        listView.setAdapter(simpleAdapter);


        return view;
    }

    List<Map<String,String>> ingredientsList = new ArrayList<>();
    private void initIngredients(){
        RecipeDetailActivity activity = (RecipeDetailActivity) getActivity();

        try{
            JSONArray ingredientRoot = new JSONArray(activity.getIngredientsJSON());

            for(int i = 0; i<ingredientRoot.length();i++){
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

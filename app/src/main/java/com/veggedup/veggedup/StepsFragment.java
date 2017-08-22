package com.veggedup.veggedup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class StepsFragment extends Fragment {


    public StepsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_steps, container, false);

        initStepsList();
        RecyclerView stepsRecyclerView = (RecyclerView) view.findViewById(R.id.stepsRecyclerView);
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        StepsListAdapter mAdapter = new StepsListAdapter(getContext(), stepsList);
        stepsRecyclerView.setAdapter(mAdapter);

        return view;
    }

    List<Map<String, String>> stepsList = new ArrayList<>();

    private void initStepsList() {
        RecipeDetailActivity activity = (RecipeDetailActivity) getActivity();

        try {
            JSONArray jsonStepsRoot = new JSONArray(activity.getStepsJSON());

            for (int i = 0; i < jsonStepsRoot.length(); i++) {
                String step = jsonStepsRoot.getString(i);
                HashMap<String, String> steps = new HashMap<>();
                steps.put("step", step);
                stepsList.add(steps);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

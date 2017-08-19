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
        ListView listView = (ListView) view.findViewById(R.id.stepsList);
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), stepsList, R.layout.step_list_item, new String[] {"steps"}, new int[] {R.id.stepTextView});
        listView.setAdapter(simpleAdapter);


        return view;
    }

    List<Map<String,String>> stepsList = new ArrayList<>();
    private void initStepsList(){
        RecipeDetailActivity activity = (RecipeDetailActivity) getActivity();

        try{
            JSONArray jsonStepsRoot = new JSONArray(activity.getStepsJSON());

            for(int i = 0; i<jsonStepsRoot.length();i++){
                String step = jsonStepsRoot.getString(i);
                HashMap<String, String> steps = new HashMap<>();
                steps.put("steps", step);
                stepsList.add(steps);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

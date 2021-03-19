package edu.iut.m414.distimate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DistanceQuestionFragment extends Fragment {
    private static final String ARG_CITY_NAME_1 = "firstCityName";
    private static final String ARG_CITY_NAME_2 = "secondCityName";

    public DistanceQuestionFragment() {
        // Required empty public constructor
    }

    public static DistanceQuestionFragment newInstance(String firstCityName, String secondCityName) {
        DistanceQuestionFragment fragment = new DistanceQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CITY_NAME_1, firstCityName);
        args.putString(ARG_CITY_NAME_2, secondCityName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_distance_question, container, false);

        TextView firstCityNameText = rootView.findViewById(R.id.firstCityNameText);
        TextView secondCityNameText = rootView.findViewById(R.id.secondCityNameText);

        if (getArguments() != null) {
            firstCityNameText.setText(getArguments().getString(ARG_CITY_NAME_1));
            secondCityNameText.setText(getArguments().getString(ARG_CITY_NAME_2));
        }

        return rootView;
    }
}
package edu.iut.m414.distimate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DistanceAnswerFragment extends Fragment {
    private static final String ARG_REAL_DISTANCE = "realDistance";
    private static final String ARG_GUESSED_DISTANCE = "guessedDistance";

    public DistanceAnswerFragment() {
        // Required empty public constructor
    }

    public static DistanceAnswerFragment newInstance(long realDistance, long guessedDistance) {
        DistanceAnswerFragment fragment = new DistanceAnswerFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_REAL_DISTANCE, realDistance);
        args.putLong(ARG_GUESSED_DISTANCE, guessedDistance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_distance_answer, container, false);

        TextView realDistanceText = rootView.findViewById(R.id.realDistanceText);
        TextView guessedDistanceText = rootView.findViewById(R.id.guessedDistanceText);

        if (getArguments() != null) {
            realDistanceText.setText(getArguments().getLong(ARG_REAL_DISTANCE) + " km");
            guessedDistanceText.setText(getArguments().getLong(ARG_GUESSED_DISTANCE) + " km");
        }

        return rootView;
    }
}
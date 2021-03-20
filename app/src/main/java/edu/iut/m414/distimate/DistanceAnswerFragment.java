package edu.iut.m414.distimate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.iut.m414.distimate.util.Utilities;

public class DistanceAnswerFragment extends Fragment {
    private static final String ARG_REAL_DISTANCE = "realDistance";
    private static final String ARG_GUESSED_DISTANCE = "guessedDistance";
    private static final String ARG_HAS_GUESS = "hasGuess";
    private boolean hasGuess;

    public DistanceAnswerFragment() {
        // Required empty public constructor
    }

    public static DistanceAnswerFragment newInstance(long realDistance, long guessedDistance, boolean hasGuess) {
        DistanceAnswerFragment fragment = new DistanceAnswerFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_REAL_DISTANCE, realDistance);
        args.putLong(ARG_GUESSED_DISTANCE, guessedDistance);
        args.putBoolean(ARG_HAS_GUESS, hasGuess);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hasGuess = getArguments().getBoolean(ARG_HAS_GUESS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        if (hasGuess) {
            rootView = inflater.inflate(R.layout.fragment_distance_answer_and_guess, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_distance_answer, container, false);
        }

        TextView realDistanceText = rootView.findViewById(R.id.realDistanceText);

        if (getArguments() != null)
            realDistanceText.setText(String.format(
                    getString(R.string.distance),
                    Utilities.formatNumber(getArguments().getLong(ARG_REAL_DISTANCE), getContext())));

        if (hasGuess) {
            TextView guessedDistanceText = rootView.findViewById(R.id.guessedDistanceText);
            if (getArguments() != null)
                guessedDistanceText.setText(String.format(
                        getString(R.string.distance),
                        Utilities.formatNumber(getArguments().getLong(ARG_GUESSED_DISTANCE), getContext())));
        }

        return rootView;
    }
}
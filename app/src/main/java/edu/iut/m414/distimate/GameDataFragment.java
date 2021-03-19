package edu.iut.m414.distimate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import edu.iut.m414.distimate.util.TimeUpListener;

public class GameDataFragment extends Fragment {
    private static final String ARG_COUNTRY_NAME = "countryName";
    private static final String ARG_DURATION = "duration";

    private TimeUpListener timeUpListener;
    private Chronometer gameTimer;
    private TextView currentCountryName;
    private TextView currentScore;

    public GameDataFragment() {
        // Required empty public constructor
    }

    public static GameDataFragment newInstance(String countryName, long duration) {
        GameDataFragment fragment = new GameDataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COUNTRY_NAME, countryName);
        args.putLong(ARG_DURATION, duration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game_data, container, false);

        gameTimer = rootView.findViewById(R.id.gameTimer);

        currentCountryName = rootView.findViewById(R.id.gameCountryNameText);
        currentScore = rootView.findViewById(R.id.gameScoreText);

        if (getArguments() != null) {
            currentCountryName.setText(getArguments().getString(ARG_COUNTRY_NAME));
            initializeTimer(getArguments().getLong(ARG_DURATION));
        }

        return rootView;
    }

    private void initializeTimer(long duration) {
        gameTimer.stop();
        gameTimer.setBase(SystemClock.elapsedRealtime() + (duration * 1000));

        gameTimer.setOnChronometerTickListener(chronometer -> {
            if (SystemClock.elapsedRealtime() >= chronometer.getBase()) {
                timeUpListener.onTimeUp();
            }
            gameTimer.stop();
        });
    }

    public void updateScore(int score) {
        currentScore.setText(String.valueOf(score));
    }

    public void startTimer() {
        gameTimer.start();
    }

    public void setTimeUpListener(TimeUpListener timeUpListener) {
        this.timeUpListener = timeUpListener;
    }
}
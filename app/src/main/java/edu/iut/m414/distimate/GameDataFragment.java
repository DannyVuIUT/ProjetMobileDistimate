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
    private TimeUpListener timeUpListener;
    private Chronometer gameTimer;
    private TextView currentCountryName;
    private TextView currentScore;

    public GameDataFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game_data, container, false);

        gameTimer = rootView.findViewById(R.id.gameTimer);

        currentCountryName = rootView.findViewById(R.id.gameCountryNameText);
        currentScore = rootView.findViewById(R.id.gameScoreText);

        return rootView;
    }

    public void updateScore(int score) {
        currentScore.setText(String.valueOf(score));
    }

    public void updateCountryName(String name) {
        currentCountryName.setText(name);
    }

    public void initializeTimer(long time) {
        gameTimer.stop();
        gameTimer.setBase(SystemClock.elapsedRealtime() + (time * 1000));

        gameTimer.setOnChronometerTickListener(chronometer -> {
            if (SystemClock.elapsedRealtime() >= chronometer.getBase()) {
                timeUpListener.onTimeUp();
            }
            gameTimer.stop();
        });
    }

    public void startTimer() {
        gameTimer.start();
    }

    public void setTimeUpListener(TimeUpListener timeUpListener) {
        this.timeUpListener = timeUpListener;
    }
}
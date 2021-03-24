package edu.iut.m414.distimate;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import edu.iut.m414.distimate.util.TimeUpListener;
import edu.iut.m414.distimate.util.Utilities;

/**
 * Fragment représentant les données globalesde la partie en cours :
 * temps restant, pays de la partie, score actuel.
 */
public class GameDataFragment extends Fragment {
    private static final String ARG_COUNTRY_NAME = "countryName";
    private static final String ARG_DURATION = "duration";

    private TimeUpListener timeUpListener;

    /**
     * -----------------------------------------------------
     *  Widget non vu : le chronomètre.
     *  Il est en mode "countdown" pour servir de minuteur.
     * -----------------------------------------------------
     */
    private Chronometer gameTimer;
    private TextView currentScore;
    private long initialDuration;
    private boolean started;

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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        timeUpListener = (TimeUpListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game_data, container, false);

        gameTimer = rootView.findViewById(R.id.gameTimer);

        TextView currentCountryName = rootView.findViewById(R.id.gameCountryNameText);
        currentScore = rootView.findViewById(R.id.gameScoreText);

        if (getArguments() != null) {
            currentCountryName.setText(getArguments().getString(ARG_COUNTRY_NAME));
            initialDuration = getArguments().getLong(ARG_DURATION);
            initializeTimer(initialDuration);
        }

        return rootView;
    }

    private void initializeTimer(long duration) {
        gameTimer.stop();
        gameTimer.setBase(SystemClock.elapsedRealtime() + duration + 500);
        started = false;

        gameTimer.setOnChronometerTickListener(chronometer -> {
            if (started && SystemClock.elapsedRealtime() >= chronometer.getBase()) {
                timeUpListener.onTimeUp();
                gameTimer.stop();
            }
        });
    }

    public void updateScore(int score) {
        currentScore.setText(Utilities.formatNumber(score, getContext()));
    }

    public void startTimer() {
        initializeTimer(initialDuration);
        started = true;
        gameTimer.start();
    }

    public void decreaseTimer(long duration) {
        gameTimer.setBase(
                Math.max(gameTimer.getBase() - duration, SystemClock.elapsedRealtime()));

        // --------------------------------------------------------------
        // Quand on décrémente la durée restante, une animation faisant
        // Trembler le temps restant, tout en le changeant en rouge,
        // se joue.
        // --------------------------------------------------------------
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(),R.animator.error_shake);
        set.setTarget(gameTimer);
        set.start();
    }
}
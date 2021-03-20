package edu.iut.m414.distimate;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import edu.iut.m414.distimate.util.AnimationEndListener;
import edu.iut.m414.distimate.util.GameLoadingStateListener;
import edu.iut.m414.distimate.util.GameStartListener;

public class GameSetupFragment extends Fragment implements GameLoadingStateListener {
    private GameStartListener startListener;
    private Button startButton;

    public GameSetupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game_setup, container, false);

        startButton = rootView.findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_fade);
                    animation.setAnimationListener(new AnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            startListener.onGameStart();
                        }
                    });
                    startButton.startAnimation(animation);
                });
        startButton.setClickable(false);
        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.startListener = (GameStartListener) getActivity();
    }

    @Override
    public void onGameLoaded() {
        startButton.setClickable(true);
    }
}
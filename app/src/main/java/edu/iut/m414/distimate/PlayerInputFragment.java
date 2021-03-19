package edu.iut.m414.distimate;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.iut.m414.distimate.util.DistanceGuessListener;

public class PlayerInputFragment extends Fragment {
    private static final String TAG = PlayerInputFragment.class.getSimpleName();

    private DistanceGuessListener distanceGuessListener;
    private EditText distanceGuessValueInput;
    private Button distanceGuessConfirmButton;
    private Button distanceGuessSkipButton;

    public PlayerInputFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        distanceGuessListener = (DistanceGuessListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player_input, container, false);

        distanceGuessConfirmButton = rootView.findViewById(R.id.distanceGuessConfirmButton);
        distanceGuessConfirmButton.setOnClickListener(v -> confirmDistanceGuess());

        distanceGuessSkipButton = rootView.findViewById(R.id.distanceGuessSkipButton);
        distanceGuessSkipButton.setOnClickListener(v -> skipDistanceGuess());

        distanceGuessValueInput = rootView.findViewById(R.id.distanceGuessValueInput);
        distanceGuessValueInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Inutilisé
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Inutilisé
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                distanceGuessConfirmButton.setEnabled(s.length() > 0);
            }
        });

        return rootView;
    }

    private void confirmDistanceGuess() {
        try {
            int guessValue = Integer.parseInt(distanceGuessValueInput.getText().toString());
            distanceGuessListener.onDistanceGuess(guessValue);
            clearGuess();
        } catch (NumberFormatException e) {
            Log.e(TAG, "NumberFormatException: " + e.getMessage());
        }
    }

    private void skipDistanceGuess() {
        distanceGuessListener.onSkip();
        clearGuess();
    }

    private void clearGuess() {
        distanceGuessValueInput.setText("");
    }

    public void setInputEnabled(boolean inputEnabled) {
        distanceGuessValueInput.setEnabled(inputEnabled);
        distanceGuessSkipButton.setEnabled(inputEnabled);
    }
}
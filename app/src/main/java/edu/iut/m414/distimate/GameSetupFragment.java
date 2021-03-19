package edu.iut.m414.distimate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.iut.m414.distimate.util.GameLoadingStateListener;

public class GameSetupFragment extends Fragment implements GameLoadingStateListener {
    public GameSetupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game_setup, container, false);

        // TODO : récupérer les textes et boutons pour changer l'état de chargement
        return rootView;
    }

    @Override
    public void onGameLoaded() {
        // TODO : changer l'état des boutons
    }
}
package edu.iut.m414.distimate;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import edu.iut.m414.distimate.data.Country;
import edu.iut.m414.distimate.data.CountryList;
import edu.iut.m414.distimate.data.Game;
import edu.iut.m414.distimate.util.DataManager;
import edu.iut.m414.distimate.util.GameLoadingStateListener;

public class GameActivity extends AppCompatActivity {
    private GameLoadingStateListener gameLoadingStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        if (intent != null) {
            int countryIndex = intent.getIntExtra(DataManager.COUNTRY, 0);
            Country selectedCountry = CountryList.get(countryIndex);
            loadGameDataFragment(getString(selectedCountry.getNameId()));
        }
    }

    private void loadGameDataFragment(String countryName) {
        Fragment gameDataFragment = getSupportFragmentManager().findFragmentById(R.id.gameDataFrame);

        if (gameDataFragment == null) {
            gameDataFragment = GameDataFragment.newInstance(countryName, Game.DURATION);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.gameDataFrame, gameDataFragment)
                    .commit();
        }
    }

    private void loadGameSetupFragment() {
        Fragment middleFragment = getSupportFragmentManager().findFragmentById(R.id.questionAnswerFrame);
        if (middleFragment == null) {
            middleFragment = new GameSetupFragment();
            gameLoadingStateListener = (GameLoadingStateListener)middleFragment;

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.questionAnswerFrame, middleFragment)
                    .commit();
        }
    }

    private void loadPlayerInputFragment() {
        Fragment playerInputFragment = getSupportFragmentManager().findFragmentById(R.id.playerInputFrame);

        if (playerInputFragment == null) {
            playerInputFragment = new PlayerInputFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.playerInputFrame, playerInputFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}

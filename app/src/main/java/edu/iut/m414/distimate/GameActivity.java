package edu.iut.m414.distimate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import edu.iut.m414.distimate.data.Country;
import edu.iut.m414.distimate.data.CountryList;
import edu.iut.m414.distimate.data.DistanceQuestion;
import edu.iut.m414.distimate.data.Game;
import edu.iut.m414.distimate.util.DataManager;
import edu.iut.m414.distimate.util.DistanceGuessListener;
import edu.iut.m414.distimate.util.GameLoadingStateListener;
import edu.iut.m414.distimate.util.GameStartListener;
import edu.iut.m414.distimate.util.VibrationManager;

public class GameActivity extends AppCompatActivity implements GameStartListener, DistanceGuessListener {
    private GameLoadingStateListener gameLoadingStateListener;
    private PlayerInputFragment playerInputFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        if (intent != null) {
            int countryIndex = intent.getIntExtra(DataManager.COUNTRY, 0);
            Country selectedCountry = CountryList.get(countryIndex);
            loadGameDataFragment(getString(selectedCountry.getNameId()));
            loadGameSetupFragment();
            new LoadGameTask().execute(selectedCountry);
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

    private void playNextQuestion() {
        DistanceQuestion nextQuestion = Game.getInstance().nextQuestion();
        if (nextQuestion != null) {
            loadDistanceQuestionFragment(nextQuestion);
        } else {
            // TODO : gérer le fait qu'il n'y ait plus de question
        }
    }

    private void loadDistanceQuestionFragment(DistanceQuestion question) {
        DistanceQuestionFragment distanceQuestionFragment =
                DistanceQuestionFragment.newInstance(question.getFrom(), question.getTo());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.questionAnswerFrame, distanceQuestionFragment)
                .commit();
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
        playerInputFragment = (PlayerInputFragment)getSupportFragmentManager().findFragmentById(R.id.playerInputFrame);

        if (playerInputFragment == null) {
            playerInputFragment = new PlayerInputFragment();
            playerInputFragment.setDistanceGuessListener(this);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.playerInputFrame, playerInputFragment)
                    .commit();
        }
    }

    @Override
    public void onGameStart() {
        loadPlayerInputFragment();
        playNextQuestion();
    }

    @Override
    public void onDistanceGuess(int guess) {
        playNextQuestion();
    }

    @Override
    public void onSkip() {
        playNextQuestion();
        VibrationManager.vibrate(this, 150);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public class LoadGameTask extends AsyncTask<Country,Void,Void> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GameActivity.this);
            progressDialog.setMessage(getString(R.string.loading_questions));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Country... countries) {
            Game.getInstance().initializeGameData(countries[0],getString(R.string.game_locale));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            gameLoadingStateListener.onGameLoaded();
        }

    }

}

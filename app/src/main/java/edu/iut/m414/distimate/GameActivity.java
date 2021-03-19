package edu.iut.m414.distimate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

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
import edu.iut.m414.distimate.util.TimeUpListener;
import edu.iut.m414.distimate.util.VibrationManager;

public class GameActivity extends AppCompatActivity implements GameStartListener, DistanceGuessListener, TimeUpListener {
    private GameLoadingStateListener gameLoadingStateListener;
    private PlayerInputFragment playerInputFragment;
    private GameDataFragment gameDataFragment;
    private boolean gameHasStarted;

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
            gameHasStarted = false;
        }
    }

    private void loadGameDataFragment(String countryName) {
        if (gameDataFragment == null) {
            gameDataFragment = GameDataFragment.newInstance(countryName, Game.DURATION);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.gameDataFrame, gameDataFragment)
                    .commit();
        }
    }

    private void playNextQuestion() {
        if (gameHasStarted) {
            new GetNextQuestionTask().execute();
        } else {
            DistanceQuestion nextQuestion = Game.nextQuestion();
            loadDistanceQuestionFragment(nextQuestion);
            gameHasStarted = true;
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

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.playerInputFrame, playerInputFragment)
                    .commit();
        }
    }

    @Override
    public void onGameStart() {
        gameDataFragment.startTimer();
        loadPlayerInputFragment();
        playNextQuestion();
    }

    @Override
    public void onDistanceGuess(int guess) {
        playerInputFragment.setInputEnabled(false);
        Game.updateScore(guess);
        gameDataFragment.updateScore(Game.getCurrentScore());
        // TODO : AFFICHER LA VRAIE REPONSE ET L'ESTIMATION
        playNextQuestion();
    }

    @Override
    public void onSkip() {
        playerInputFragment.setInputEnabled(false);
        VibrationManager.vibrate(this, 150);
        gameDataFragment.decreaseTimer(Game.PENALTY_DURATION);
        // TODO : AFFICHER LA VRAIE REPONSE
        playNextQuestion();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onTimeUp() {
        Game.notifyStopLoading();
        Intent intent = new Intent(this, GameResultActivity.class);
        intent.putExtra(DataManager.SCORE, Game.getCurrentScore());
        startActivity(intent);
        finish();
    }

    private class LoadGameTask extends AsyncTask<Country,Void,Void> {
        private ProgressDialog progressDialog;
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
            Game.initializeGameData(countries[0],getString(R.string.game_locale));
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

    private class GetNextQuestionTask extends AsyncTask<Void,Void,Void> {
        private String TAG = GetNextQuestionTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... voids) {
            getNextQuestion();
            return null;
        }

        private void getNextQuestion() {
            DistanceQuestion nextQuestion = Game.nextQuestion();
            if (nextQuestion != null) {
                loadDistanceQuestionFragment(nextQuestion);
                runOnUiThread(() -> playerInputFragment.setInputEnabled(true));
            } else if (!Game.allQuestionsHaveLoaded()) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
                getNextQuestion();
            } else {
                // TODO : gérer le fait qu'il n'y ait plus du tout de questions
            }
        }
    }
}

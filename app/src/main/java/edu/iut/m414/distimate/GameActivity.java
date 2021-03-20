package edu.iut.m414.distimate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import edu.iut.m414.distimate.data.Country;
import edu.iut.m414.distimate.data.CountryList;
import edu.iut.m414.distimate.data.DistanceQuestion;
import edu.iut.m414.distimate.data.Game;
import edu.iut.m414.distimate.util.AnimationEndListener;
import edu.iut.m414.distimate.util.DataManager;
import edu.iut.m414.distimate.util.DistanceGuessListener;
import edu.iut.m414.distimate.util.GameLoadingStateListener;
import edu.iut.m414.distimate.util.GameStartListener;
import edu.iut.m414.distimate.util.TimeUpListener;
import edu.iut.m414.distimate.util.Utilities;
import edu.iut.m414.distimate.util.VibrationManager;

public class GameActivity extends AppCompatActivity implements GameStartListener, DistanceGuessListener, TimeUpListener {
    private static final String TAG = GameActivity.class.getSimpleName();
    private GameLoadingStateListener gameLoadingStateListener;
    private PlayerInputFragment playerInputFragment;
    private GameDataFragment gameDataFragment;
    private FrameLayout centerFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        if (intent != null) {
            int countryIndex = intent.getIntExtra(DataManager.KEY_COUNTRY, 0);
            Country selectedCountry = CountryList.get(countryIndex);
            loadGameDataFragment(getString(selectedCountry.getNameId()));
            loadGameSetupFragment();
            new LoadGameTask().execute(selectedCountry);
            centerFrame = findViewById(R.id.centerFrame);
        }
    }

    private void loadGameDataFragment(String countryName) {
        if (gameDataFragment == null) {
            gameDataFragment = GameDataFragment.newInstance(countryName, DataManager.DURATION);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.gameDataFrame, gameDataFragment)
                    .commit();
        }
    }

    private void loadGameSetupFragment() {
        Fragment middleFragment = getSupportFragmentManager().findFragmentById(R.id.centerFrame);
        if (middleFragment == null) {
            middleFragment = new GameSetupFragment();
            gameLoadingStateListener = (GameLoadingStateListener)middleFragment;

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.centerFrame, middleFragment)
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

    private void playNextQuestion() {
        new GetNextQuestionTask().execute();
    }

    private void animateCenterFrameThenExecute(Animation animation, Runnable onAnimationEnd) {
        centerFrame.startAnimation(animation);
        animation.setAnimationListener(new AnimationEndListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                onAnimationEnd.run();
            }
        });
    }

    private void loadNextDistanceQuestionFragment(DistanceQuestion question) {
        Animation slideOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out);
        animateCenterFrameThenExecute(slideOutAnimation, () -> loadDistanceQuestionFragment(question));
    }

    private void loadDistanceQuestionFragment(DistanceQuestion question) {
        DistanceQuestionFragment distanceQuestionFragment =
                DistanceQuestionFragment.newInstance(question.getFrom(), question.getTo());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.centerFrame, distanceQuestionFragment)
                .commit();

        Animation slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        centerFrame.startAnimation(slideInAnimation);
    }

    private void loadDistanceAnswerFragment(long realDistance) {
        Animation slideOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out);
        animateCenterFrameThenExecute(slideOutAnimation,
                () -> {
                    DistanceAnswerFragment distanceAnswerFragment =
                            DistanceAnswerFragment.newInstance(realDistance, -1, false);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.centerFrame, distanceAnswerFragment)
                            .commit();

                    Animation slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in);
                    animateCenterFrameThenExecute(slideInAnimation, () -> {
                        Utilities.waitDelay(DataManager.ANSWER_WAIT_TIME, TAG);
                        playNextQuestion();
                    });
                });
    }

    private void loadDistanceAnswerAndGuessFragment(long realDistance, long guessedDistance) {
        Animation slideOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out);
        animateCenterFrameThenExecute(slideOutAnimation,
                () -> {
                    DistanceAnswerFragment distanceAnswerFragment =
                            DistanceAnswerFragment.newInstance(realDistance, guessedDistance, true);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.centerFrame, distanceAnswerFragment)
                            .commit();

                    Animation slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in);
                    animateCenterFrameThenExecute(slideInAnimation, () -> {
                        Utilities.waitDelay(DataManager.ANSWER_WAIT_TIME, TAG);
                        playNextQuestion();
                    });
                });
    }

    @Override
    public void onGameStart() {
        gameDataFragment.startTimer();
        loadPlayerInputFragment();

        DistanceQuestion nextQuestion = Game.nextQuestion();
        loadDistanceQuestionFragment(nextQuestion);
    }

    @Override
    public void onDistanceGuess(int guess) {
        playerInputFragment.setInputEnabled(false);
        Game.updateScore(guess);
        gameDataFragment.updateScore(Game.getCurrentScore());
        DistanceQuestion currentQuestion = Game.getCurrentQuestion();
        loadDistanceAnswerAndGuessFragment(currentQuestion.getRealDistance(), guess);
    }

    @Override
    public void onSkip() {
        playerInputFragment.setInputEnabled(false);
        VibrationManager.vibrate(this, 150);
        gameDataFragment.decreaseTimer(DataManager.PENALTY_DURATION);
        loadDistanceAnswerFragment(Game.getCurrentQuestion().getRealDistance());
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onTimeUp() {
        playerInputFragment.setInputEnabled(false);
        Game.notifyStopLoading();
        Intent intent = new Intent(this, GameResultActivity.class);
        intent.putExtra(DataManager.KEY_SCORE, Game.getCurrentScore());
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
                loadNextDistanceQuestionFragment(nextQuestion);
                runOnUiThread(() -> playerInputFragment.setInputEnabled(true));
            } else if (!Game.allQuestionsHaveLoaded()) {
                Utilities.waitDelay(250, TAG);
                getNextQuestion();
            } else {
                // TODO : gérer le fait qu'il n'y ait plus du tout de questions
            }
        }
    }
}

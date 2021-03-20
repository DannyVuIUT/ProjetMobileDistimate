package edu.iut.m414.distimate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

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
            gameDataFragment = GameDataFragment.newInstance(countryName, DataManager.GAME_DURATION);
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
            gameLoadingStateListener = (GameLoadingStateListener) middleFragment;

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.centerFrame, middleFragment)
                    .commit();
        }
    }

    private void loadPlayerInputFragment() {
        playerInputFragment = (PlayerInputFragment) getSupportFragmentManager().findFragmentById(R.id.playerInputFrame);

        if (playerInputFragment == null) {
            playerInputFragment = new PlayerInputFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.playerInputFrame, playerInputFragment)
                    .commit();
        }
    }

    private void playNextQuestion(long delay) {
        new GetNextQuestionTask().execute(delay);
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
        animateCenterFrameThenExecute(slideInAnimation,
                () -> playerInputFragment.setInputEnabled(true));
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
                    animateCenterFrameThenExecute(slideInAnimation,
                            () -> playNextQuestion(DataManager.ANSWER_WAIT_TIME));
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
                    animateCenterFrameThenExecute(slideInAnimation,
                            () -> playNextQuestion(DataManager.ANSWER_AND_GUESS_WAIT_TIME));
                });
    }

    @Override
    public void onGameStart() {
        DistanceQuestion nextQuestion = Game.nextQuestion();
        if (nextQuestion == null) {
            Toast.makeText(
                    GameActivity.this,
                    getString(R.string.question_retrieval_failed),
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            gameDataFragment.startTimer();
            loadPlayerInputFragment();
            loadDistanceQuestionFragment(nextQuestion);
        }
    }

    @Override
    public void onDistanceGuess(int guess) {
        playerInputFragment.setInputEnabled(false);
        Game.updateScore(guess);
        gameDataFragment.updateScore(Game.getCurrentScore());
        DistanceQuestion currentQuestion = Game.getCurrentQuestion();
        loadDistanceAnswerAndGuessFragment(currentQuestion.getActualDistance(), guess);
    }

    @Override
    public void onSkip() {
        playerInputFragment.setInputEnabled(false);
        Game.updateScoreSkip();
        gameDataFragment.updateScore(Game.getCurrentScore());
        VibrationManager.vibrate(this, 150);
        gameDataFragment.decreaseTimer(DataManager.PENALTY_DURATION);
        loadDistanceAnswerFragment(Game.getCurrentQuestion().getActualDistance());
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onTimeUp() {
        Game.notifyStopLoading();
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
        Intent intent = new Intent(this, GameResultActivity.class);
        intent.putExtra(DataManager.KEY_SCORE, Game.getCurrentScore());
        startActivity(intent);
        finish();
    }

    private class LoadGameTask extends AsyncTask<Country, Void, Void> {
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
            Game.initializeGameData(countries[0], getString(R.string.game_locale));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            if (Game.requestAreWorking()) {
                gameLoadingStateListener.onGameLoaded();
            } else {
                onGameStart();
            }
        }
    }

    private class GetNextQuestionTask extends AsyncTask<Long, Void, Void> {
        private final String TAG = GetNextQuestionTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Long... longs) {
            Utilities.waitDelay(longs[0], TAG);
            getNextQuestion();
            return null;
        }

        private void getNextQuestion() {
            DistanceQuestion nextQuestion = Game.nextQuestion();
            if (nextQuestion != null) {
                loadNextDistanceQuestionFragment(nextQuestion);
            } else if (!Game.allQuestionsHaveLoaded()) {
                if (Game.requestAreWorking()) {
                    Utilities.waitDelay(250, TAG);
                    getNextQuestion();
                } else {
                    Toast.makeText(
                            GameActivity.this,
                            getString(R.string.question_retrieval_failed),
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(
                        GameActivity.this,
                        getString(R.string.no_question_left),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}

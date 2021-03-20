package edu.iut.m414.distimate.data;

import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.iut.m414.distimate.request.GeoDB;
import edu.iut.m414.distimate.util.DataManager;
import edu.iut.m414.distimate.util.Utilities;

public class Game {
    private static final String TAG = Game.class.getSimpleName();
    private static final Random RNG = new Random();

    private static Country currentCountry;
    private static int currentScore;
    private static int currentQuestionIndex;
    private static final List<DistanceQuestion> questionList;
    private static final boolean[] continueLoading;
    static {
        questionList = Collections.synchronizedList(new ArrayList<>());
        continueLoading = new boolean[] {false};
    }

    private Game() {
        super();
    }

    public static void initializeGameData(Country country, String languageCode) {
        currentScore = 0;
        currentQuestionIndex = -1;
        currentCountry = country;

        synchronized (questionList) {
            questionList.clear();
        }

        continueLoading[0] = true;
        Thread initThread = new Thread(() -> {
            long currentTime = SystemClock.elapsedRealtime();
            for (int i = 0; i < DataManager.MAX_GAME_SIZE && continueLoading[0]; i++) {
                int firstCityNumber = RNG.nextInt(country.getCitiesCount());
                int secondCityNumber;
                do {
                    secondCityNumber = RNG.nextInt(country.getCitiesCount());
                } while (firstCityNumber == secondCityNumber);

                long otherTime = SystemClock.elapsedRealtime();
                City firstCity = GeoDB.requestCity(country.getId(), firstCityNumber, languageCode);
                Log.d(TAG, "GOT FIRST CITY AFTER : " + (SystemClock.elapsedRealtime() - otherTime) + "ms");

                if (!continueLoading[0])
                    break;

                City secondCity = GeoDB.requestCity(country.getId(), secondCityNumber, languageCode);
                Log.d(TAG, "GOT SECOND CITY AFTER : " + (SystemClock.elapsedRealtime() - otherTime) + "ms");

                if (!continueLoading[0])
                    break;

                int distance = GeoDB.requestDistance(firstCity.getId(), secondCity.getId());
                Log.d(TAG, "GOT DISTANCE AFTER : " + (SystemClock.elapsedRealtime() - otherTime) + "ms");

                synchronized (questionList) {
                    questionList.add(
                            new DistanceQuestion(
                                    firstCity.getName(),
                                    secondCity.getName(),
                                    distance));
                }

                Log.d(TAG, "REQUEST ENDED AFTER " + (SystemClock.elapsedRealtime() - currentTime) + "ms");
                Log.d(TAG, String.format("%s -> %s : %d",
                        questionList.get(i).getFrom(),
                        questionList.get(i).getTo(),
                        questionList.get(i).getRealDistance()));
            }
        });
        initThread.setDaemon(true);
        initThread.start();

        Utilities.waitDelay(DataManager.INIT_WAIT_TIME, TAG);
    }

    public static DistanceQuestion nextQuestion() {
        synchronized (questionList) {
            if (currentQuestionIndex + 1 >= questionList.size()) {
                return null;
            } else {
                currentQuestionIndex++;
                return questionList.get(currentQuestionIndex);
            }
        }
    }

    public static DistanceQuestion getCurrentQuestion() {
        synchronized (questionList) {
            if (currentQuestionIndex < questionList.size()) {
                return questionList.get(currentQuestionIndex);
            } else {
                return null;
            }
        }
    }

    public static DistanceQuestion[] getShownQuestions() {
        return questionList.subList(0, currentQuestionIndex + 1).toArray(new DistanceQuestion[0]);
    }

    public static int updateScore(int distanceGuess) {
        int guessScore = computeScore(distanceGuess);
        currentScore += guessScore;
        return guessScore;
    }

    private static int computeScore(int distanceGuess) {
        DistanceQuestion currentQuestion = questionList.get(currentQuestionIndex);
        return Math.abs(currentQuestion.getRealDistance() - distanceGuess);
    }

    public static boolean allQuestionsHaveLoaded() {
        synchronized (questionList) {
            return questionList.size() >= DataManager.MAX_GAME_SIZE;
        }
    }

    public static int getCurrentScore() {
        return currentScore;
    }

    public static void notifyStopLoading() {
        continueLoading[0] = false;
    }
}

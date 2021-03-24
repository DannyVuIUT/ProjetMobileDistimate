package edu.iut.m414.distimate.data;

import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.iut.m414.distimate.request.GeoDB;
import edu.iut.m414.distimate.util.DataManager;

public class Game {
    private static final String TAG = Game.class.getSimpleName();
    private static final Random RNG = new Random();

    private static Country currentCountry;
    private static int currentScore;
    private static int currentQuestionIndex;
    private static final List<DistanceQuestion> questionList;
    private static boolean continueLoading;
    private static boolean requestsWorking;

    static {
        questionList = Collections.synchronizedList(new ArrayList<>());
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

        continueLoading = true;
        requestsWorking = true;

        Thread currentThread = Thread.currentThread();
        Thread initThread = new Thread(() -> {
            for (int i = 0; i < DataManager.MAX_GAME_SIZE && continueLoading; i++) {
                long currentTime = SystemClock.elapsedRealtime();

                int firstCityNumber = RNG.nextInt(country.getCitiesCount());
                int secondCityNumber;
                do {
                    secondCityNumber = RNG.nextInt(country.getCitiesCount());
                } while (firstCityNumber == secondCityNumber);

                // --------------------------------------------------------------
                // Requête à GeoDB pour récupérer une 1re ville
                // --------------------------------------------------------------
                City firstCity = GeoDB.requestCity(country.getId(), firstCityNumber, languageCode);

                if (!continueLoading)
                    break;

                // --------------------------------------------------------------
                // Requête à GeoDB pour récupérer une 2e ville
                // --------------------------------------------------------------
                City secondCity = GeoDB.requestCity(country.getId(), secondCityNumber, languageCode);

                if (!continueLoading)
                    break;

                if (firstCity == null || secondCity == null) {
                    requestsWorking = false;
                    currentThread.interrupt();
                    break;
                }

                // --------------------------------------------------------------
                // Requête à GeoDB pour récupérer la distance entre les 2 villes
                // ---------------------------------------------------------------
                int distance = GeoDB.requestDistance(firstCity.getId(), secondCity.getId());

                synchronized (questionList) {
                    questionList.add(
                            new DistanceQuestion(
                                    firstCity.getName(),
                                    secondCity.getName(),
                                    distance));
                }

                Log.d(TAG, "REQUEST " + (i+1) + " ENDED AFTER " + (SystemClock.elapsedRealtime() - currentTime) + "ms");
                Log.d(TAG, String.format("%s -> %s : %d",
                        questionList.get(i).getFrom(),
                        questionList.get(i).getTo(),
                        questionList.get(i).getActualDistance()));
            }
        });
        initThread.setDaemon(true);
        initThread.start();

        try {
            Thread.sleep(DataManager.INIT_WAIT_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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

    public static void updateScore(int distanceGuess) {
        int guessScore = computeScore(distanceGuess);
        currentScore += guessScore;
    }

    public static void updateScoreSkip() {
        currentScore += DataManager.SKIP_SCORE;
    }

    private static int computeScore(int distanceGuess) {
        DistanceQuestion currentQuestion = questionList.get(currentQuestionIndex);
        int difference = Math.abs(currentQuestion.getActualDistance() - distanceGuess);
        int score = (int) ((((0. + currentCountry.getBaseDistance() - difference)) / currentCountry.getBaseDistance()) * DataManager.MAX_BASE_SCORE);
        return Math.max(0, score);
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
        continueLoading = false;
    }

    public static boolean requestAreWorking() {
        return requestsWorking;
    }
}

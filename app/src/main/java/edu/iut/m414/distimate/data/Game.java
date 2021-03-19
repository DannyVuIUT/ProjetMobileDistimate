package edu.iut.m414.distimate.data;

import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.iut.m414.distimate.request.GeoDB;

public class Game {
    private static final String TAG = Game.class.getSimpleName();
    private static final long INIT_WAIT_TIME = 12000;
    private static final Random RNG = new Random();
    public static final long DURATION = 60;
    private static final int MAX_GAME_SIZE = 15;

    private static Country currentCountry;
    private static int currentScore;
    private static int currentQuestionIndex;
    private static List<DistanceQuestion> questionList;

    private Game() {
        super();
    }

    private static void inititalizeData() {
        if (questionList == null) {
            questionList = Collections.synchronizedList(new ArrayList<>());
        }
    }

    public static void initializeGameData(Country country, String languageCode) {
        inititalizeData();

        currentScore = 0;
        currentQuestionIndex = -1;
        currentCountry = country;
        questionList.clear();

        Thread initThread = new Thread(() -> {
            long currentTime = SystemClock.elapsedRealtime();
            for (int i = 0; i < MAX_GAME_SIZE; i++) {
                int firstCityNumber = RNG.nextInt(country.getCitiesCount());
                int secondCityNumber;
                do {
                    secondCityNumber = RNG.nextInt(country.getCitiesCount());
                } while (firstCityNumber == secondCityNumber);

                long otherTime = SystemClock.elapsedRealtime();
                City firstCity = GeoDB.requestCity(country.getId(), firstCityNumber, languageCode);
                Log.d(TAG, "GOT FIRST CITY AFTER : " + (SystemClock.elapsedRealtime() - otherTime) + "ms");
                City secondCity = GeoDB.requestCity(country.getId(), secondCityNumber, languageCode);
                Log.d(TAG, "GOT SECOND CITY AFTER : " + (SystemClock.elapsedRealtime() - otherTime) + "ms");
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

        try {
            Thread.sleep(INIT_WAIT_TIME);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static DistanceQuestion nextQuestion() {
        inititalizeData();

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
        inititalizeData();

        synchronized (questionList) {
            if (currentQuestionIndex < questionList.size()) {
                return questionList.get(currentQuestionIndex);
            } else {
                return null;
            }
        }
    }

    public static DistanceQuestion[] getShownQuestions() {
        inititalizeData();

        return questionList.subList(0, currentQuestionIndex + 1).toArray(new DistanceQuestion[0]);
    }

    public static int updateScore(int distanceGuess) {
        int guessScore = computeScore(distanceGuess);
        currentScore += guessScore;
        return guessScore;
    }

    private static int computeScore(int distanceGuess) {
        inititalizeData();

        DistanceQuestion currentQuestion = questionList.get(currentQuestionIndex);
        return Math.abs(currentQuestion.getRealDistance() - distanceGuess);
    }

    public static boolean allQuestionsHaveLoaded() {
        inititalizeData();

        synchronized (questionList) {
            return questionList.size() >= MAX_GAME_SIZE;
        }
    }

    public static int getCurrentScore() {
        return currentScore;
    }
}

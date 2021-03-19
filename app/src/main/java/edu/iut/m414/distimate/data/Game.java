package edu.iut.m414.distimate.data;

import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.iut.m414.distimate.request.GeoDB;

public class Game {
    public static final String TAG = Game.class.getSimpleName();
    public static final long DURATION = 60;
    private static final Random RNG = new Random();
    private static final int MAX_GAME_SIZE = 15;

    public static Game instance;

    private List<DistanceQuestion> questionList;
    private Country currentCountry;
    private int currentScore;
    private int currentQuestionIndex;

    public static Game getInstance() {
        if (instance == null)
            instance = new Game();
        return instance;
    }

    private Game() {
        questionList = Collections.synchronizedList(new ArrayList<>());
    }

    public void initializeGameData(Country country, String languageCode) {
        currentScore = 0;
        currentQuestionIndex = -1;
        currentCountry = country;
        questionList.clear();

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
    }

    public DistanceQuestion nextQuestion() {
        synchronized (questionList) {
            if (currentQuestionIndex + 1 >= questionList.size()) {
                return null;
            } else {
                currentQuestionIndex++;
                return questionList.get(currentQuestionIndex);
            }
        }
    }

    public DistanceQuestion getCurrentQuestion() {
        synchronized (questionList) {
            if (currentQuestionIndex < questionList.size()) {
                return questionList.get(currentQuestionIndex);
            } else {
                return null;
            }
        }
    }

    public DistanceQuestion[] getShownQuestions() {
        return questionList.subList(0, currentQuestionIndex + 1).toArray(new DistanceQuestion[0]);
    }

    public int updateScore(int distanceGuess) {
        int guessScore = computeScore(distanceGuess);
        currentScore += guessScore;
        return guessScore;
    }

    private int computeScore(int distanceGuess) {
        DistanceQuestion currentQuestion = questionList.get(currentQuestionIndex);
        return Math.abs(currentQuestion.getRealDistance() - distanceGuess);
    }

    public boolean haveAllQuestionsLoaded() {
        synchronized (questionList) {
            return questionList.size() >= MAX_GAME_SIZE;
        }
    }
}

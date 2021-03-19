package edu.iut.m414.distimate.data;

import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
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
        questionList = new ArrayList<>();
    }

    public void initializeGameData(Country country, String languageCode) {
        currentScore = 0;
        currentQuestionIndex = -1;
        currentCountry = country;
        questionList.clear();

        Thread[] getQuestionThreads = new Thread[MAX_GAME_SIZE];

        for (int i = 0; i < MAX_GAME_SIZE; i++) {
            int firstCityNumber = RNG.nextInt(country.getCitiesCount());
            int secondCityNumber;
            do {
                secondCityNumber = RNG.nextInt(country.getCitiesCount());
            } while (firstCityNumber == secondCityNumber);
            final int finalSecondCityNumber = secondCityNumber;

            getQuestionThreads[i] = new Thread(() -> {
                City firstCity = GeoDB.requestCity(country.getId(), firstCityNumber, languageCode);
                City secondCity = GeoDB.requestCity(country.getId(), finalSecondCityNumber, languageCode);
                int distance = GeoDB.requestDistance(firstCity.getId(), secondCity.getId());

                questionList.add(
                        new DistanceQuestion(
                                firstCity.getName(),
                                secondCity.getName(),
                                distance));
            });
            getQuestionThreads[i].start();
        }

        long currentTime = SystemClock.elapsedRealtime();
        for (Thread t : getQuestionThreads) {
            try {
                t.join();
                Log.d(TAG, "REQUEST ENDED AFTER " + (SystemClock.elapsedRealtime() - currentTime) + "ms");
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        for (DistanceQuestion dq : questionList) {
            Log.d(TAG, String.format("%s -> %s : %d", dq.getFrom(), dq.getTo(), dq.getRealDistance()));
        }
    }

    public DistanceQuestion nextQuestion() {
        if (currentQuestionIndex + 1 >= questionList.size()) {
            return null;
        } else {
            currentQuestionIndex++;
            return questionList.get(currentQuestionIndex);
        }
    }

    public DistanceQuestion getCurrentQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            return questionList.get(currentQuestionIndex);
        } else {
            return null;
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
}

package edu.iut.m414.distimate.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.iut.m414.distimate.request.GeoDB;

public class Game {
    public static final long DURATION = 60;
    private static final Random RNG = new Random();
    private static final int MAX_GAME_SIZE = 15;

    private List<DistanceQuestion> questionList;
    private Country currentCountry;
    private int currentScore;

    public Game() {
        questionList = new ArrayList<>();
    }

    public void initializeGameData(Country country, String languageCode) {
        currentScore = 0;
        currentCountry = country;
        questionList.clear();

        for (int i = 0; i < MAX_GAME_SIZE; i++) {
            int firstCityNumber = RNG.nextInt(country.getCitiesCount());
            int secondCityNumber;
            do {
                secondCityNumber = RNG.nextInt(country.getCitiesCount());
            } while (firstCityNumber == secondCityNumber);

            City firstCity = GeoDB.requestCity(country.getId(), firstCityNumber, languageCode);
            City secondCity = GeoDB.requestCity(country.getId(), secondCityNumber, languageCode);
            int distance = GeoDB.requestDistance(firstCity.getId(), secondCity.getId());

            questionList.add(
                    new DistanceQuestion(
                            firstCity.getName(),
                            secondCity.getName(),
                            distance));
        }
    }

    public DistanceQuestion nextQuestion() {
        if (questionList.isEmpty()) {
            return null;
        } else {
            return questionList.remove(0);
        }
    }

    public int updateScore(DistanceQuestion question, int distanceGuess) {
        int guessScore = computeScore(question, distanceGuess);
        currentScore += guessScore;
        return guessScore;
    }

    private int computeScore(DistanceQuestion question, int distanceGuess) {
        return Math.abs(question.getRealDistance() - distanceGuess);
    }
}

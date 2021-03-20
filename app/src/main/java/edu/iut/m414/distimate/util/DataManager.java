package edu.iut.m414.distimate.util;

public final class DataManager {
    public static final long INIT_WAIT_TIME = 12000;
    public static final long ANSWER_WAIT_TIME = 1250;
    public static final long ANSWER_AND_GUESS_WAIT_TIME = 1750;
    public static final long PENALTY_DURATION = 3000;
    public static final long GAME_DURATION = 60000;
    public static final int MAX_GAME_SIZE = 19;
    public static final int MAX_BASE_SCORE = 1000;
    public static final int SKIP_SCORE = 100;

    public static final String KEY_COUNTRY = "country";
    public static final String KEY_SCORE = "score";

    private DataManager() {
        super();
    }
}

package edu.iut.m414.distimate.util;

import android.content.Context;
import android.view.View;

import java.text.NumberFormat;

public final class DataManager {
    public static final long INIT_WAIT_TIME = 10000;
    public static final long ANSWER_WAIT_TIME = 1500;
    public static final long DURATION = 20000;
    public static final long PENALTY_DURATION = 3000;
    public static final int MAX_GAME_SIZE = 10;

    public static final String KEY_COUNTRY = "country";
    public static final String KEY_SCORE = "score";

    private DataManager() {
        super();
    }
}

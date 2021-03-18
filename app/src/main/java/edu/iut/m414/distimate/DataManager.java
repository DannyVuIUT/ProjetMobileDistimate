package edu.iut.m414.distimate;

import android.content.Context;
import android.view.View;

import java.text.NumberFormat;

public final class DataManager {
    public static final String COUNTRY = "country";

    private DataManager() {
    }

    public static String formatNumber(int number,Context context){
       return NumberFormat.getNumberInstance(context.getResources().getConfiguration().getLocales().get(0)).format(number);
    }
}

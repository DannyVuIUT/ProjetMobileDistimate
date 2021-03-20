package edu.iut.m414.distimate.util;

import android.content.Context;
import android.util.Log;

import java.text.NumberFormat;

public class Utilities {
    public static String formatNumber(long number, Context context) {
        return NumberFormat.getNumberInstance(context.getResources().getConfiguration().getLocales().get(0)).format(number);
    }

    public static void waitDelay(long delay, String interruptErrorTag) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Log.e(interruptErrorTag, e.getMessage());
        }
    }
}

package edu.iut.m414.distimate.util;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibrationManager {
    public static void vibrate(Context context) {
        vibrate(context, 150);
    }
    /**
     * -----------------------------------------------------------------------------
     * Technologie non-vue : la vibration de l'appareil mobile
     * (Testée et fonctionnelle sur un vrai appareil mobile)
     * -----------------------------------------------------------------------------
     * @param context - contexte
     * @param milliseconds - durée de vibration
     */
    public static void vibrate(Context context, int milliseconds) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        //Adaptation aux différentes versions de SDK
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(milliseconds);
        }
    }
}

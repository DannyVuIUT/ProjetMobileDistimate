package edu.iut.m414.distimate.util;

import android.view.animation.Animation;

public abstract class AnimationEndListener implements Animation.AnimationListener {
    @Override
    public void onAnimationStart(Animation animation) {
        // Inutilisé
    }

    @Override
    public abstract void onAnimationEnd(Animation animation);

    @Override
    public void onAnimationRepeat(Animation animation) {
        // Inutilisé
    }
}

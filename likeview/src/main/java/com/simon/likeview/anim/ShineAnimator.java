package com.simon.likeview.anim;

import android.animation.ValueAnimator;
import android.graphics.Canvas;

import com.simon.likeview.interpolator.Ease;
import com.simon.likeview.interpolator.EasingInterpolator;
import com.simon.likeview.view.ShineView;

/**
 * description: shine 动画
 * author: Simon
 * created at 2017/9/12 下午1:28
 */
public class ShineAnimator extends ValueAnimator {

    float MAX_VALUE = 1.5f;
    long ANIM_DURATION = 1500;
    Canvas canvas;

    public ShineAnimator() {
        setFloatValues(1f, MAX_VALUE);
        setDuration(ANIM_DURATION);
        setStartDelay(200);
        setInterpolator(new EasingInterpolator(Ease.QUART_OUT));
    }

    public ShineAnimator(long duration, float max_value, long delay) {
        setFloatValues(1f, max_value);
        setDuration(duration);
        setStartDelay(delay);
        setInterpolator(new EasingInterpolator(Ease.QUART_OUT));
    }

    public void startAnim(final ShineView shineView, final int centerAnimX, final int centerAnimY) {
        start();
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }


}

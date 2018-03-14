/**
 * created by 小卷毛, 2016/11/25
 * Copyright (c) 2016, 416143467@qq.com All Rights Reserved.
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG             #
 * #                                                   #
 */
package com.ruanmeng.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

/**
 * 项目名称：Shared_Marketing
 * 创建人：小卷毛
 * 创建时间：2017-02-19 13:54
 */
public class AnimationHelper {

    /**
     * Android系统本身内置了一些通用的Interpolator(插值器)，
     * AccelerateDecelerateInterpolator   在动画开始与结束的地方速率改变比较慢，在中间的时候加速
     * AccelerateInterpolator             在动画开始的地方速率改变比较慢，然后开始加速
     * AnticipateInterpolator             开始的时候向后然后向前甩
     * AnticipateOvershootInterpolator    开始的时候向后然后向前甩一定值后返回最后的值
     * BounceInterpolator                 动画结束的时候弹起
     * CycleInterpolator                  动画循环播放特定的次数，速率改变沿着正弦曲线
     * DecelerateInterpolator             在动画开始的地方快然后慢
     * LinearInterpolator                 以常量速率改变（匀速）
     * OvershootInterpolator              向前甩一定值后再回到原来位置
     */

    /**
     * 旋转动画，默认300ms
     */
    public static void startRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    /**
     * 旋转动画，设置指定的时间，单位毫秒
     */
    public static void startRotateAnimator(final View target, final float from, final float to, long milliseconds) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(milliseconds);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    /**
     * 数字文本加载动画，默认1000ms
     */
    public static void startIncreaseAnimator(final TextView target, final int to) {
        ValueAnimator animator = ValueAnimator.ofInt(0, to);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                target.setText(String.valueOf(valueAnimator.getAnimatedValue()));
            }
        });
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    /**
     * 数字文本加载动画，默认1000ms
     */
    public static void startIncreaseAnimator(final TextView target, final float to) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, to);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                target.setText(String.format(
                        "%.2f",
                        Double.parseDouble(String.valueOf(valueAnimator.getAnimatedValue())) ));
            }
        });
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    /**
     * 数字文本加载动画，设置指定的时间，单位毫秒
     */
    public static void startIncreaseAnimator(final TextView target, final int to, long milliseconds) {
        ValueAnimator animator = ValueAnimator.ofInt(0, to);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                target.setText(String.valueOf(valueAnimator.getAnimatedValue()));
            }
        });
        animator.setDuration(milliseconds);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    /**
     * 数字文本加载动画，设置指定的时间，单位毫秒
     */
    public static void startIncreaseAnimator(final TextView target, final float to, long milliseconds) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, to);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                target.setText(String.format(
                        "%.2f",
                        Double.parseDouble(String.valueOf(valueAnimator.getAnimatedValue())) ));
            }
        });
        animator.setDuration(milliseconds);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

}

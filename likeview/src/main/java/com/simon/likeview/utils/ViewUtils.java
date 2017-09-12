package com.simon.likeview.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * description: View 的辅助工具类
 * author: Simon
 * created at 2017/9/12 下午1:35
 */

public class ViewUtils {
    private static final String TAG = "ViewUtils";

    /**
     * 私有构造函数
     */
    private ViewUtils() {
        throw new AssertionError("you can't init me!");
    }

    /**
     * 获取View的底部距离
     *
     * @param attachView 目标view
     * @param ac         上下文环境
     * @param isReal     是不是真实高度
     * @return 返回底部高度
     */
    public static int getBottomHeight(View attachView, Activity ac, boolean isReal) {
        //--check value--
        if (attachView == null || ac == null) {
            Log.e(TAG, "attachView或者activity为null! =>getBottomHeight()");
            return -1;
        }
        //--获取在window中的位置--
        int[] location = new int[2];
        attachView.getLocationInWindow(location);
        //--区别全屏和非全屏--
        if (isReal) {
            Rect visibleFrame = new Rect();
            ac.getWindow().getDecorView().getWindowVisibleDisplayFrame(visibleFrame);
            return visibleFrame.height() - location[1];
        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            ac.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            return metrics.heightPixels - location[1];
        }
    }

    /**
     * 将view 从当前activity 的contentView中移除
     *
     * @param activity   上下文环境
     * @param attachView 目标view
     */
    public static void removeView(Activity activity, View attachView) {
        //--check value--
        if (activity == null || attachView == null) {
            Log.e(TAG, "removeView err!");
            return;
        }
        //--获取rootview--
        final ViewGroup rootView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        if (rootView == null) {
            Log.e(TAG, "removeView err!");
            return;
        }
        //--remove view--
        rootView.removeView(attachView);
    }

    public static void attachView(Activity activity, View attachView) {
        //--check value--
        if (activity == null || attachView == null) {
            Log.e(TAG, "attachView err!");
            return;
        }
        //--获取rootview--
        final ViewGroup rootView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        if (rootView == null) {
            Log.e(TAG, "attachView err!");
            return;
        }
        //--check parent if exist remove attachView--
        ViewGroup parent = (ViewGroup) attachView.getParent();
        if (parent != null) {
            parent.removeView(attachView);
        }
        //--attach view--
        int position = rootView.indexOfChild(attachView);
        if (position == -1) { //防止多次添加
            rootView.addView(attachView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    public static int[] getViewLocation(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        return location;
    }

    public static Rect getViewFrame(Activity activity) {
        Rect visibleFrame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(visibleFrame);
        return visibleFrame;
    }

    public static int[] getViewInfo(View view) {
        int[] viewInfo = new int[2];
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        viewInfo[0] = view.getMeasuredWidth();
        viewInfo[1] = view.getMeasuredHeight();
        return viewInfo;
    }


}

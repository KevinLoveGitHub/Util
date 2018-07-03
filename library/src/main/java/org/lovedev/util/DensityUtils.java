package org.lovedev.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * px,dp,sp转化工具类
 */
public class DensityUtils {

    /**
     * 不允许初始化
     */
    private DensityUtils() {
    }

    /**
     * @param context 上下文
     * @param dpVal   dp 值
     * @return px 值
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * @param context 上下文
     * @param spVal   sp 值
     * @return px 值
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }


    /**
     * @param context 上下文
     * @param pxVal   px 值
     * @return dp 值
     */
    public static int px2dp(Context context, float pxVal) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxVal / scale + 0.5f);
    }


    /**
     * @param context 上下文
     * @param pxVal   px 值
     * @return sp 值
     */
    public static int px2sp(Context context, float pxVal) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxVal / fontScale + 0.5);
    }
}


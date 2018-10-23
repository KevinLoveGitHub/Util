package org.lovedev.util;

import android.util.Log;

/**
 * 说明：log 工具类
 * 作者：Kevin
 * 日期：2017/6/13
 */
public class LogUtils {

    private LogUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    // 是否需要打印bug，可以在application的onCreate函数里面初始化
    private static boolean isDebug = true;
    private static final String TAG = "LogUtils";

    // 下面四个是默认tag的函数
    public static void i(String msg) {
        performPrint(Log.INFO, TAG, msg);
    }

    public static void w(String msg){
        performPrint(Log.INFO, TAG, msg);
    }

    public static void d(String msg) {
        performPrint(Log.DEBUG, TAG, msg);
    }

    public static void e(String msg) {
        performPrint(Log.ERROR, TAG, msg);
    }

    public static void v(String msg) {
        performPrint(Log.VERBOSE, TAG, msg);
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        performPrint(Log.INFO, tag, msg);
    }

    public static void w(String tag, String msg){
        performPrint(Log.INFO, tag, msg);
    }

    public static void d(String tag, String msg) {
        performPrint(Log.DEBUG, tag, msg);
    }

    public static void e(String tag, String msg) {
        performPrint(Log.ERROR, tag, msg);
    }

    public static void v(String tag, String msg) {
        performPrint(Log.VERBOSE, tag, msg);
    }


    //执行打印
    private static void performPrint(int level, String tag, String msg) {
        //非Debug版本，则不打印日志
        if (!isDebug) {
            return;
        }
        String threadName = Thread.currentThread().getName();
        String lineIndicator = getLineIndicator();
        StringBuilder stringBuilder = new StringBuilder(msg);

        int maxSize = 500;
        if (msg.length() > maxSize) {
            stringBuilder = new StringBuilder();
            int lines = msg.length() / maxSize;
            for (int i = 0; i < lines; i++) {
                int start = i * maxSize;
                if (i > 0) {
                    start += 1;
                }
                stringBuilder.append(msg.substring(start, (i + 1) * maxSize)).append("\n");
            }

            int size = msg.length() - lines * maxSize;
            if (size > 0) {
                stringBuilder.append(msg.substring(lines * maxSize));
            }
        }
        Log.println(level, tag, String.valueOf(stringBuilder.append(" : ").append(threadName).append("-").append(lineIndicator)));
    }

    private static String getLineIndicator() {
        //3代表方法的调用深度：0-getLineIndicator，1-performPrint，2-print，3-调用该工具类的方法位置
        StackTraceElement element = ((new Exception()).getStackTrace())[3];
        return "(" +
                element.getFileName() + ":" +
                element.getLineNumber() + ")." +
                element.getMethodName();
    }

}

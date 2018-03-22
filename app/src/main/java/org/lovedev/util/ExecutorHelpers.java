package org.lovedev.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Kevin
 * @data 2018/3/19
 */
public class ExecutorHelpers {

    private static final String TAG = "ThreadPoolHelpers";
    private final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private final int KEEP_ALIVE_TIME = 1;

    private final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private final MainThreadExecutor mainThread;
    private final ExecutorService networkIO;
    private final ExecutorService diskIO;

    private static class ThreadPoolHelpersHolder {
        private static final ExecutorHelpers INSTANCE = new ExecutorHelpers();
    }

    public static final ExecutorHelpers getInstance() {
        return ThreadPoolHelpersHolder.INSTANCE;
    }

    private ExecutorHelpers() {
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
        BlockingQueue<Runnable> diskTaskQueue = new LinkedBlockingQueue<Runnable>();

        diskIO = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                diskTaskQueue, new BackgroundThreadFactory(),
                new DefaultRejectedExecutionHandler());

        networkIO = new ThreadPoolExecutor(NUMBER_OF_CORES,

                NUMBER_OF_CORES * 2, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT,

                taskQueue, new BackgroundThreadFactory(), new DefaultRejectedExecutionHandler());


        mainThread = new MainThreadExecutor();

    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    private class BackgroundThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r);
        }
    }

    private class DefaultRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            Log.e(TAG, "Task " + r.toString() + " rejected from " + executor.toString());
        }
    }


    public static MainThreadExecutor getMainThread() {
        return getInstance().mainThread;
    }

    public static ExecutorService getNetworkIO() {
        return getInstance().networkIO;
    }

    public static ExecutorService getDiskIO() {
        return getInstance().diskIO;
    }
}

package org.lovedev.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Kevin
 * @date 2018/3/19
 */
public class ExecutorHelpers {

    private static final String TAG = "ExecutorHelpers";
    private final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private final int CORE_POOL_SIZE = Math.max(2, Math.min(NUMBER_OF_CORES - 1, 4));
    private int MAX_QUEUE_SIZE = NUMBER_OF_CORES * 2 + 1;

    private final MainThreadExecutor mainThread;
    private final ExecutorService networkIO;
    private final ExecutorService diskIO;
    private final ExecutorService newCachedThreadPool;

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
                diskTaskQueue, new CustomThreadFactory("diskIO"));

        networkIO = new ThreadPoolExecutor(CORE_POOL_SIZE,
                MAX_QUEUE_SIZE, 0L, TimeUnit.MILLISECONDS,
                taskQueue, new CustomThreadFactory("networkIO"));

        newCachedThreadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), new CustomThreadFactory("newCachedThreadPool"));

        mainThread = new MainThreadExecutor();
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    public static ExecutorService getNewCachedThreadPool() {
        return getInstance().newCachedThreadPool;
    }

    public static Executor getMainThread() {
        return getInstance().mainThread;
    }

    public static ExecutorService getNetworkIO() {
        return getInstance().networkIO;
    }

    public static ExecutorService getDiskIO() {
        return getInstance().diskIO;
    }

    private class CustomThreadFactory implements ThreadFactory {
        private int counter;
        private String name;
        private List<String> stats;

        public CustomThreadFactory(String name) {
            counter = 1;
            this.name = name;
            stats = new ArrayList<>();
        }

        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            Thread t = new Thread(runnable, name + "-Thread_" + counter);
            counter++;
            stats.add(String.format(Locale.CHINA, "Created thread %d with name %s on %s \n", t.getId(), t.getName(), new Date()));
            return t;
        }

        public String getStats() {
            StringBuilder buffer = new StringBuilder();
            for (String stat : stats) {
                buffer.append(stat);
            }
            return buffer.toString();
        }
    }
}

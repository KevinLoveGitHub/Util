package org.lovedev.util;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadFactory;

public class CustomThreadFactory implements ThreadFactory {
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

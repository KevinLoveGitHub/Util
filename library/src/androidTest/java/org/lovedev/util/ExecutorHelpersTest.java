package org.lovedev.util;

import android.os.SystemClock;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin
 * @data 2018/7/4
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExecutorHelpersTest {
    @Test
    public void networkIOTest() {
        int i = 0;
        while (true) {
            i++;
            SystemClock.sleep(1000);
            final int finalI = i;
            ExecutorHelpers.getNetworkIO().execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(finalI);
                }
            });
        }
    }
}

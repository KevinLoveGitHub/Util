package org.lovedev.util;

import org.junit.Test;

/**
 * @author Kevin
 * @date 2018/7/4
 */
public class ExecutorHelpersTest {
    @Test
    public void networkIOTest() {
        for (int j = 0; j < 20; j++) {
            final int finalJ = j;
            ExecutorHelpers.getNetworkIO().execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(finalJ);
                }
            });
        }

    }
}

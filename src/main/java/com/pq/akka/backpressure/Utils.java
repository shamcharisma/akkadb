package com.pq.akka.backpressure;

import java.util.Date;

public class Utils {
    public static void print(Object s) {
        System.out.printf("%s:%s%n", new Date(), s);
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static long start = System.currentTimeMillis();

    public static Boolean isSlowTime() {
        boolean b = (System.currentTimeMillis() - start) % 30_000 >= 15_000;
        return b;
    }
}

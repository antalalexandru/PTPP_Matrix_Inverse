package com.company;

public class TimeUtils {
    public static double getTimeConsumption(Runnable runnable, int numberOfTries) {
        double totalTime = 0;
        for(int i = 0; i < numberOfTries; i++) {
            long start = System.currentTimeMillis();
            runnable.run();
            long stop = System.currentTimeMillis();
            totalTime += stop - start;
        }
        return totalTime / numberOfTries;
    }
}

package com.company;

public class TimeUtils {
    public static double getTimeConsumption(Runnable runnable, int numberOfTries) {
        long totalTime = 0;
        for(int i = 0; i < numberOfTries; i++) {
            long start = System.currentTimeMillis();
            runnable.run();
            totalTime += System.currentTimeMillis() - start;
        }
        return (1. * totalTime) / numberOfTries;
    }
}

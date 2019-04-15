package com.company;

public class TimeUtils {

    /**
     *
     * @param runnable
     * @param numberOfTrials
     * @return
     */
    public static double getTimeConsumption(Runnable runnable, int numberOfTrials) {
        long totalTime = 0;
        for(int i = 0; i < numberOfTrials; i++) {
            long start = System.currentTimeMillis();
            runnable.run();
            totalTime += System.currentTimeMillis() - start;
        }
        return (1. * totalTime) / numberOfTrials;
    }

}

package com.company;

@SuppressWarnings("WeakerAccess")
public class DoubleUtils {
    public static boolean equals(double a, double b) {
        return Math.abs(a - b) < 0.000001;
    }
}

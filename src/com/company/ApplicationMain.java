package com.company;

import java.io.IOException;
import java.util.Arrays;

public class ApplicationMain {

    public static void main(String[] args) throws MatrixNotInvertibleException, BadArgumentException, IOException, InterruptedException {

        compareAlgorithms();

    }

    private static void testParallelAlgorithm() throws MatrixNotInvertibleException, BadArgumentException {
        for(int i = 0; i < 1000; i++) {
            int matrixSize = 100;
            double[][] matrix = MatrixUtils.generateRandomMatrix(matrixSize, matrixSize);
            double[][] inverse = new ParallelMatrixInverseCalculator(matrix).computeMatrixInverse();
            if( ! MatrixUtils.matrixEquals(MatrixUtils.matrixMultiplication(matrix, inverse), MatrixUtils.getIdentityMatrix(matrixSize))) {
                throw new RuntimeException("Wrong algorithm");
            }
        }
    }

    private static void compareAlgorithms() {

        final int numberOfTrials = 5;

        Arrays.asList(5, 10, 15, 20, 25, 50, 100, 125, 150, 175, 200, 250, 300, 350, 400, 450, 500, 600, 700, 800, 900, 1000, 1250, 1500, 1750, 2000, 2250, 2500, 2750, 3000, 3250, 3500).forEach(matrixSize -> {

            double[][] matrix = MatrixUtils.generateRandomMatrix(matrixSize, matrixSize);

            double sequentialImplementationTime = TimeUtils.getTimeConsumption(() -> {
                try {
                    new SequentialMatrixInverseCalculator(matrix).computeMatrixInverse();
                } catch (MatrixNotInvertibleException e) {
                    e.printStackTrace();
                }
            }, numberOfTrials);

            double parallelImplementationTime = TimeUtils.getTimeConsumption(() -> {
                try {
                    new ParallelMatrixInverseCalculator(matrix).computeMatrixInverse();
                } catch (MatrixNotInvertibleException e) {
                    e.printStackTrace();
                }
            }, numberOfTrials);

            System.out.printf("Matrix size: %d\nSequential: %.4f ms\nParallel: %.4f ms\n===============================================\n", matrixSize, sequentialImplementationTime, parallelImplementationTime);

        });
    }

}

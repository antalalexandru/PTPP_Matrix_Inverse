package com.company;

import java.io.IOException;
import java.util.Arrays;

public class ApplicationMain {

    public static void main(String[] args) throws MatrixNotInvertibleException, BadArgumentException, IOException, InterruptedException {

        compareAlgorithms();

        // testParallelAlgorithm();

    }

    private static void testParallelAlgorithm() throws MatrixNotInvertibleException, BadArgumentException {
        for(int i = 0; i < 1000; i++) {
            int matrixSize = 100;
            double[][] matrix = MatrixUtils.generateRandomMatrix(matrixSize, matrixSize);
            double[][] inverse = MatrixUtils.getMatrixInverseFromExtendedMatrix(new ParallelMatrixInverseCalculator(MatrixUtils.addIdentityMatrix(matrix), 8).computeMatrixInverse());
            if( ! MatrixUtils.matrixEquals(MatrixUtils.matrixMultiplication(matrix, inverse), MatrixUtils.getIdentityMatrix(matrixSize))) {
                throw new RuntimeException("Wrong algorithm");
            }
        }
    }

    private static void compareAlgorithms() {

        final int numberOfTrials = 3;

        Arrays.asList(5, 10, 15, 20, 25, 50, 100, 125, 150, 175, 200, 250, 300, 350, 400, 450, 500, 600, 700, 800, 900, 1000, 1250, 1500, 1750, 2000, 2250, 2500, 2750, 3000, 3250, 3500, 4000, 4500, 5000, 5500, 6000).forEach(matrixSize -> {

            double[][] matrix = MatrixUtils.generateRandomMatrix(matrixSize, matrixSize);

            System.out.println("===============================================");
            System.out.println("Matrix size: " + matrixSize);

            double sequentialImplementationTime = 0;

            for(int i = 0; i < numberOfTrials; i++) {
                double[][] extendedMatrix = MatrixUtils.addIdentityMatrix(matrix);
                long start = System.currentTimeMillis();
                try {
                    new SequentialMatrixInverseCalculator(extendedMatrix).computeMatrixInverse();
                } catch (MatrixNotInvertibleException e) {
                    e.printStackTrace();
                }
                sequentialImplementationTime += System.currentTimeMillis() - start;
            }
            sequentialImplementationTime /= numberOfTrials;

            System.out.printf("sequentialImplementationTime: %.4f\n", sequentialImplementationTime);

            Arrays.asList(4, 8, 16).forEach(numberOfThreads -> {
                double parallelImplementationTime = 0;
                for(int i = 0; i < numberOfTrials; i++) {
                    double[][] extendedMatrix = MatrixUtils.addIdentityMatrix(matrix);
                    long start = System.currentTimeMillis();
                    try {
                        new ParallelMatrixInverseCalculator(extendedMatrix, numberOfThreads).computeMatrixInverse();
                    } catch (MatrixNotInvertibleException e) {
                        e.printStackTrace();
                    }
                    parallelImplementationTime += System.currentTimeMillis() - start;
                }
                parallelImplementationTime /= numberOfTrials;
                System.out.printf("Parallel, %d threads: %.4f\n", numberOfThreads, parallelImplementationTime);

            });

        });
    }

}

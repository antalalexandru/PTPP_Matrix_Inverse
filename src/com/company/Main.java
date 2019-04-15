package com.company;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

public class Main {

    private static void computeInverse( String matrixFile, String inverseFile, int matrixSize ) throws IOException, MatrixNotInvertibleException, BadArgumentException {
        double[][] matrix = MatrixUtils.readMatrixFromFile(matrixFile);
        double[][] inverse = MatrixUtils.getMatrixInverseSecvential(matrix);
        if( ! MatrixUtils.matrixEquals(MatrixUtils.matrixMultiplication(matrix, inverse), MatrixUtils.getIdentityMatrix(matrixSize))) {
            throw new RuntimeException("Wrong algorithm");
        }
        MatrixUtils.writeMatrixToFile(inverseFile, inverse);
    }

    public static void main(String[] args) throws MatrixNotInvertibleException, BadArgumentException, IOException, InterruptedException {

        testAlgorithm();

        /*new ParallelMatrixInverseComputation(MatrixUtils.addIdentityMatrix(new double[][]{
                {5, 7, 4},
                {6, 2, 9},
                {5, 7, 3}
        })).getMatrixInverseParallel();*/

        //String matrixFile = "./src/com/company/resources/matrix.txt";
        //String inverseMatrixFile = "./src/com/company/resources/inverse.txt";

        //int matrixSize = 3;

        //double[][] matrix = MatrixUtils.generateRandomMatrix(matrixSize, matrixSize);
        //MatrixUtils.writeMatrixToFile(matrixFile, matrix);

        //computeInverse(matrixFile, inverseMatrixFile, matrixSize);

        /*Arrays.asList(5, 10, 15, 20, 25, 50, 100, 125, 150, 175, 200, 250, 300, 350, 400, 450, 500, 600, 700, 800, 900, 1000, 1250, 1500, 1750, 2000, 2250, 2500, 2750, 3000, 3250, 3500).forEach(matrixSize -> {
            double[][] matrix = MatrixUtils.generateRandomMatrix(matrixSize, matrixSize);

            int numberOfTries = 2;

            double secventialResult = 0;
            for(int i = 0; i < numberOfTries; i++) {
                double[][] modifiedMatrix = MatrixUtils.addIdentityMatrix(matrix);
                long start = System.currentTimeMillis();
                try {
                    MatrixUtils.getMatrixInverseSecvential(modifiedMatrix);
                } catch (MatrixNotInvertibleException e) {
                    e.printStackTrace();
                }
                secventialResult += System.currentTimeMillis() - start;
            }
            secventialResult /= numberOfTries;


            double parallelResult = 0;
            for(int i = 0; i < numberOfTries; i++) {
                double[][] modifiedMatrix = MatrixUtils.addIdentityMatrix(matrix);
                long start = System.currentTimeMillis();
                try {
                    new ParallelMatrixInverseComputation(modifiedMatrix).getMatrixInverseParallel();
                } catch (MatrixNotInvertibleException | InterruptedException e) {
                    e.printStackTrace();
                }
                parallelResult += System.currentTimeMillis() - start;
            }
            parallelResult /= numberOfTries;


            System.out.printf("Matrix size: %d, duration: sequential: %f ms; parallel: %f\n", matrixSize, secventialResult, parallelResult);

        });*/

    }

    private static void testAlgorithm() throws MatrixNotInvertibleException, BadArgumentException, InterruptedException {
        for(int i = 0; i < 1000; i++) {
            int matrixSize = 10;
            double[][] matrix = MatrixUtils.generateRandomMatrix(matrixSize, matrixSize);
            double[][] modifiedMatrix = MatrixUtils.addIdentityMatrix(matrix);
            double[][] inverse = MatrixUtils.getInverseMatrix(new ParallelMatrixInverseComputation(modifiedMatrix).getMatrixInverseParallel());
            if( ! MatrixUtils.matrixEquals(MatrixUtils.matrixMultiplication(matrix, inverse), MatrixUtils.getIdentityMatrix(matrixSize))) {
                throw new RuntimeException("Wrong algorithm");
            }
        }
    }

}

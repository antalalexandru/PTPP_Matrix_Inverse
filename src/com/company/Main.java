package com.company;

import java.io.IOException;
import java.math.BigDecimal;
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

    public static void main(String[] args) throws MatrixNotInvertibleException, BadArgumentException, IOException {

        testAlgorithm();

        String matrixFile = "./src/com/company/resources/matrix.txt";
        String inverseMatrixFile = "./src/com/company/resources/inverse.txt";

        int matrixSize = 3;

        //double[][] matrix = MatrixUtils.generateRandomMatrix(matrixSize, matrixSize);
        //MatrixUtils.writeMatrixToFile(matrixFile, matrix);

        computeInverse(matrixFile, inverseMatrixFile, matrixSize);

    }

    private static void testAlgorithm() throws MatrixNotInvertibleException, BadArgumentException {
        for(int i = 0; i < 1000; i++) {
            int matrixSize = 100;
            double[][] matrix = MatrixUtils.generateRandomMatrix(matrixSize, matrixSize);
            double[][] inverse = MatrixUtils.getMatrixInverseSecvential(matrix);
            if( ! MatrixUtils.matrixEquals(MatrixUtils.matrixMultiplication(matrix, inverse), MatrixUtils.getIdentityMatrix(matrixSize))) {
                throw new RuntimeException("Wrong algorithm");
            }
        }
    }

}

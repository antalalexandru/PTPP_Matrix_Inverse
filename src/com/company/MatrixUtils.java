package com.company;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class MatrixUtils {

    public static double[][] readMatrixFromFile(final String file) throws IOException {
        try (Scanner scanner = new Scanner(new File(file))) {
            int numberOfLines = scanner.nextInt();
            double[][] elements = new double[numberOfLines][numberOfLines];
            for(int i = 0; i < numberOfLines; i++) {
                for(int j = 0; j < numberOfLines; j++) {
                    elements[i][j] = scanner.nextDouble();
                }
            }
            return elements;
        }
    }

    public static void writeMatrixToFile(final String file, double[][] matrix) throws IOException {
        int numberOfLines = matrix.length;
        int numberOfColumns = matrix[0].length;
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write("" + numberOfLines);
            bufferedWriter.newLine();
            for(int i = 0; i < numberOfLines; i++) {
                String line = "";
                for(int j = 0; j < numberOfColumns; j++) {
                    line = line + matrix[i][j] + " ";
                }
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        }
    }

    private static double[][] addIdentityMatrix(double[][] initialMatrix) {
        int numberOfLines = initialMatrix.length;
        double[][] modifiedMatrix = new double[numberOfLines][2 * numberOfLines];
        for(int i = 0; i < numberOfLines; i++) {
            for(int j = 0; j < numberOfLines; j++) {
                modifiedMatrix[i][j] = initialMatrix[i][j];
                if(i == j) {
                    modifiedMatrix[i][numberOfLines + j] = 1;
                }
            }
        }
        return modifiedMatrix;
    }

    private static double[][] getInverseMatrix(double[][] matrix) {
        int numberOfLines = matrix.length;
        double[][] inverseMatrix = new double[numberOfLines][numberOfLines];

        for(int i = 0; i < numberOfLines; i++) {
            for(int j = 0; j < numberOfLines; j++) {
                inverseMatrix[i][j] = matrix[i][numberOfLines + j];
            }
        }

        return inverseMatrix;
    }

    private static void swapMatrixLines(double[][] matrix, int firstLine, int secondLine) {
        int numberOfColumns = matrix[firstLine].length;
        for(int column = 0; column < numberOfColumns; column++) {
            double aux = matrix[firstLine][column];
            matrix[firstLine][column] = matrix[secondLine][column];
            matrix[secondLine][column] = aux;
        }
    }

    public static double[][] getMatrixInverseSecvential(double[][] matrix) throws MatrixNotInvertibleException {
        double[][] modifiedMatrix = addIdentityMatrix(matrix);
        int numberOfLines = modifiedMatrix.length;
        for(int pivotLine = 0; pivotLine < numberOfLines; pivotLine++) {
            if(DoubleUtils.equals(modifiedMatrix[pivotLine][pivotLine], 0.0)) {
                // find column to swap
                int currentLine = pivotLine;
                while(currentLine < numberOfLines && DoubleUtils.equals(modifiedMatrix[currentLine][pivotLine], 0.0)) {
                    currentLine++;
                }
                if(currentLine == numberOfLines) {
                    throw new MatrixNotInvertibleException();
                }
                swapMatrixLines(modifiedMatrix, pivotLine, currentLine);
            }
            double pivot = modifiedMatrix[pivotLine][pivotLine];
            for(int column = 0; column < 2 * numberOfLines; column++) {
                modifiedMatrix[pivotLine][column] /= pivot;
            }
            for(int line = 0; line < numberOfLines; line++) {
                if(line == pivotLine) {
                    continue;
                }
                for(int column = 0; column < 2 * numberOfLines; column++) {
                    if(column != pivotLine) {
                        modifiedMatrix[line][column] = modifiedMatrix[line][column] - modifiedMatrix[line][pivotLine] * modifiedMatrix[pivotLine][column] / modifiedMatrix[pivotLine][pivotLine];
                    }
                }
                modifiedMatrix[line][pivotLine] = 0;
            }
        }
        return getInverseMatrix(modifiedMatrix);
    }


    public static double[][] matrixMultiplication(double[][] A, double[][] B) throws BadArgumentException {
        int m1ColLength = A[0].length;
        int m2RowLength = B.length;
        if(m1ColLength != m2RowLength)
            throw new BadArgumentException();
        int mRRowLength = A.length;
        int mRColLength = B[0].length;
        double[][] mResult = new double[mRRowLength][mRColLength];
        for(int i = 0; i < mRRowLength; i++) {
            for(int j = 0; j < mRColLength; j++) {
                for(int k = 0; k < m1ColLength; k++) {
                    mResult[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return mResult;
    }

    public static double[][] getIdentityMatrix(int numberOfLines) {
        double[][] matrix = new double[numberOfLines][numberOfLines];
        for(int i = 0; i < numberOfLines; i++) {
            matrix[i][i] = 1;
        }
        return matrix;
    }

    public static double[][] generateRandomMatrix(int numberOfLines, int numberOfColumns) {
        double[][] matrix = new double[numberOfLines][numberOfColumns];
        for(int i = 0; i < numberOfLines; i++) {
            for(int j = 0; j < numberOfColumns; j++) {
                matrix[i][j] = ThreadLocalRandom.current().nextInt(-10, 10);
            }
        }
        return matrix;
    }

    public static boolean matrixEquals(double[][] A, double[][] B) {
        if(A.length != B.length || (A.length > 0 && A[0].length != B[0].length)) {
            return false;
        }
        if(A.length == 0) {
            return true;
        }
        int numberOfLines = A.length, numberOfColumns = A[0].length;
        for(int i = 0; i < numberOfLines; i++) {
            for(int j = 0; j < numberOfColumns; j++) {
                if(!DoubleUtils.equals(A[i][j], B[i][j])) {
                    System.out.println(A[i][j] + " != " + B[i][j] + ", " + i + ", " + j);
                    return false;
                }
            }
        }
        return true;
    }

}

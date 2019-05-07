package com.company;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Usual functions applied to double arrays
 */
@SuppressWarnings({"StringConcatenationInLoop", "WeakerAccess", "ManualArrayCopy"})
public class MatrixUtils {

    /**
     * Read and return matrix from given file
     * @param file path to matrix file
     * @return double[][] matrix
     * @throws IOException I/O exception occured
     */
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

    /**
     * Print matrix to console
     * @param matrix double[][]
     */
    public static void printMatrix(double[][] matrix) {
        for (double[] line : matrix) {
            for (double element : line) {
                System.out.printf("%.4f ", element);
            }
            System.out.println();
        }
    }

    /**
     * Writes matrix element to file
     * @param file file path where matrix will be written
     * @param matrix matrix elements
     * @throws IOException I/O exception occured
     */
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

    /**
     * Extends matrix A by adding identity matrix (I_n) on its right side
     * @param initialMatrix [A], A is typeof double[][]
     * @return [A|I_n], where n = A.length
     */
    public static double[][] addIdentityMatrix(double[][] initialMatrix) {
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

    /**
     * Returns matrix inverse result from extended matrix
     * @param matrix [A|B]
     * @return B
     */
    public static double[][] getMatrixInverseFromExtendedMatrix(double[][] matrix) {
        int numberOfLines = matrix.length;
        double[][] inverseMatrix = new double[numberOfLines][numberOfLines];

        for(int i = 0; i < numberOfLines; i++) {
            for(int j = 0; j < numberOfLines; j++) {
                inverseMatrix[i][j] = matrix[i][numberOfLines + j];
            }
        }

        return inverseMatrix;
    }

    /**
     * Swap lines firstLine and secondLine from given matrix
     * @param matrix
     * @param firstLine
     * @param secondLine
     */
    public static void swapMatrixLines(double[][] matrix, int firstLine, int secondLine) {
        int numberOfColumns = matrix[firstLine].length;
        for(int column = 0; column < numberOfColumns; column++) {
            double aux = matrix[firstLine][column];
            matrix[firstLine][column] = matrix[secondLine][column];
            matrix[secondLine][column] = aux;
        }
    }

    /**
     * Computes matrix multiplication (A * B)
     * @param A first matrix
     * @param B second matrix
     * @return A * B
     * @throws BadArgumentException if the given matrices could not be multiplied (matrix dimensions mismatch)
     */
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

    /**
     * Returns identity matrix of given size
     * @param numberOfLines  matrix number of lines / columns
     * @return I_n, where n = numberOfLines
     */
    public static double[][] getIdentityMatrix(int numberOfLines) {
        double[][] matrix = new double[numberOfLines][numberOfLines];
        for(int i = 0; i < numberOfLines; i++) {
            matrix[i][i] = 1;
        }
        return matrix;
    }

    /**
     *
     * @param numberOfLines
     * @param numberOfColumns
     * @return
     */
    public static double[][] generateRandomMatrix(int numberOfLines, int numberOfColumns) {
        double[][] matrix = new double[numberOfLines][numberOfColumns];
        for(int i = 0; i < numberOfLines; i++) {
            for(int j = 0; j < numberOfColumns; j++) {
                matrix[i][j] = ThreadLocalRandom.current().nextInt(-10, 10);
            }
        }
        return matrix;
    }

    /**
     *
     * @param A
     * @param B
     * @return
     */
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

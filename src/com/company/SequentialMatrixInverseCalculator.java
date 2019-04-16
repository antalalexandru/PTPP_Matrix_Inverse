package com.company;

import static com.company.MatrixUtils.swapMatrixLines;

public class SequentialMatrixInverseCalculator extends MatrixInverseCalculator implements java.io.Serializable {

    public SequentialMatrixInverseCalculator(double[][] matrix) {
        super(matrix);
    }

    /**
     * Sequential implementation
     *
     * @return
     * @throws MatrixNotInvertibleException
     */
    @Override
    @SuppressWarnings("Duplicates")
    public double[][] computeMatrixInverse() throws MatrixNotInvertibleException {
        int numberOfLines = extendedMatrix.length;
        for(int pivotLine = 0; pivotLine < numberOfLines; pivotLine++) {
            // if matrix[line][line] = 0, choose line' where matrix[line'][line] != 0 and swap lines line' with line in matrix
            if(DoubleUtils.equals(extendedMatrix[pivotLine][pivotLine], 0.0)) {
                // find column to swap
                int currentLine = pivotLine;
                while(currentLine < numberOfLines && DoubleUtils.equals(extendedMatrix[currentLine][pivotLine], 0.0)) {
                    currentLine++;
                }
                if(currentLine == numberOfLines) {
                    throw new MatrixNotInvertibleException();
                }
                swapMatrixLines(extendedMatrix, pivotLine, currentLine);
            }
            double pivot = extendedMatrix[pivotLine][pivotLine];
            for(int column = pivotLine; column < numberOfColumns; column++) {
                extendedMatrix[pivotLine][column] /= pivot;
            }
            for(int line = 0; line < numberOfLines; line++) {
                if(line != pivotLine) {
                    double element = extendedMatrix[line][pivotLine] / extendedMatrix[pivotLine][pivotLine];
                    for (int column = pivotLine + 1; column < numberOfColumns; column++) {
                        extendedMatrix[line][column] = extendedMatrix[line][column] - element * extendedMatrix[pivotLine][column];
                    }
                    extendedMatrix[line][pivotLine] = 0;
                }
            }
        }
        return extendedMatrix;
    }
}

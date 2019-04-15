package com.company;

public abstract class MatrixInverseCalculator {

    protected double[][] extendedMatrix;
    protected int numberOfLines;

    public MatrixInverseCalculator(double[][] matrix) {
        this.extendedMatrix = MatrixUtils.addIdentityMatrix(matrix);
        this.numberOfLines = matrix.length;
    }

    /**
     *
     * @return
     * @throws MatrixNotInvertibleException
     */
    public abstract double[][] computeMatrixInverse() throws MatrixNotInvertibleException;

}

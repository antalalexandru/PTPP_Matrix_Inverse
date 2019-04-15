package com.company;

public abstract class MatrixInverseCalculator {

    protected double[][] extendedMatrix;
    protected int numberOfLines;

    public MatrixInverseCalculator(double[][] extendedMatrix) {
        this.extendedMatrix = extendedMatrix;
        this.numberOfLines = extendedMatrix.length;
    }

    /**
     *
     * @return
     * @throws MatrixNotInvertibleException
     */
    public abstract double[][] computeMatrixInverse() throws MatrixNotInvertibleException;

}

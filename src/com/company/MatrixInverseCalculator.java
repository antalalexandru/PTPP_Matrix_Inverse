package com.company;

@SuppressWarnings("WeakerAccess")
public abstract class MatrixInverseCalculator {

    protected volatile double[][] extendedMatrix;
    protected volatile int numberOfLines;
    protected volatile int numberOfColumns;


    public MatrixInverseCalculator(double[][] extendedMatrix) {
        this.extendedMatrix = extendedMatrix;
        this.numberOfLines = extendedMatrix.length;
        this.numberOfColumns = 2 * this.numberOfLines;
    }

    /**
     *
     * @return
     * @throws MatrixNotInvertibleException
     */
    public abstract double[][] computeMatrixInverse() throws MatrixNotInvertibleException;

}

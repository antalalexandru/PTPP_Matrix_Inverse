package com.company;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelMatrixInverseCalculator extends MatrixInverseCalculator implements java.io.Serializable {

    private static int NUMBER_OF_THREADS = 3;

    Thread[] threads = new Thread[NUMBER_OF_THREADS];

    public ParallelMatrixInverseCalculator(double[][] matrix) {
        super(matrix);
    }

    /**
     * Parallel implementation
     *
     * @return
     * @throws MatrixNotInvertibleException
     */
    @Override
    public double[][] computeMatrixInverse() throws MatrixNotInvertibleException {

        for (int pivotLine = 0; pivotLine < numberOfLines; pivotLine++) {

            // if matrix[line][line] = 0, choose line' where matrix[line'][line] != 0 and swap lines line' with line in matrix
            if (DoubleUtils.equals(extendedMatrix[pivotLine][pivotLine], 0.0)) {
                // find line to swap
                int lineToSwap = pivotLine;
                while (lineToSwap < numberOfLines && DoubleUtils.equals(extendedMatrix[lineToSwap][pivotLine], 0.0)) {
                    lineToSwap++;
                }
                if (lineToSwap == numberOfLines) {
                    throw new MatrixNotInvertibleException();
                }
                MatrixUtils.swapMatrixLines(extendedMatrix, pivotLine, lineToSwap);
            }

            double pivot = extendedMatrix[pivotLine][pivotLine];

            for (int column = pivotLine; column < numberOfColumns; column++) {
                extendedMatrix[pivotLine][column] /= pivot;
            }

            int elementsPerThread = numberOfLines / NUMBER_OF_THREADS;
            int remainingElements = numberOfLines % NUMBER_OF_THREADS;

            int startLine = 0;

            for (int workerId = 0; workerId < NUMBER_OF_THREADS; workerId++) {

                int endLine = startLine + elementsPerThread;

                if (remainingElements > 0) {
                    endLine++;
                    remainingElements--;
                }

                threads[workerId] = new Thread(new Worker(pivotLine, startLine, endLine));
                threads[workerId].start();

                startLine = endLine;
            }

            for (int workerId = 0; workerId < NUMBER_OF_THREADS; workerId++) {
                try {
                    threads[workerId].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return extendedMatrix;
    }

    private class Worker implements Runnable {

        private volatile int pivotLine;
        private volatile int startLine;
        private volatile int endLine;

        private Worker(int pivotLine, int startLine, int endLine) {
            this.pivotLine = pivotLine;
            this.startLine = startLine;
            this.endLine = endLine;
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        @SuppressWarnings("Duplicates")
        public void run() {
            for (int currentLine = startLine; currentLine < this.endLine; currentLine++) {
                if (currentLine == pivotLine) {
                    continue;
                }
                double element = extendedMatrix[currentLine][pivotLine] / extendedMatrix[pivotLine][pivotLine];

                for (int column = pivotLine + 1; column < numberOfColumns; column++) {
                    extendedMatrix[currentLine][column] = extendedMatrix[currentLine][column] - element * extendedMatrix[pivotLine][column];
                }
                extendedMatrix[currentLine][pivotLine] = 0;
            }
        }
    }

}

package com.company;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelMatrixInverseCalculator extends MatrixInverseCalculator implements java.io.Serializable {

    private static final int NUMBER_OF_THREADS = 8; // Runtime.getRuntime().availableProcessors();

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

            ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
            Thread[] threads = new Thread[NUMBER_OF_THREADS];

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

            for (int column = 0; column < 2 * extendedMatrix.length; column++) {
                extendedMatrix[pivotLine][column] /= pivot;
            }

            int elementsPerThread = numberOfLines / NUMBER_OF_THREADS;
            int remainingElements = numberOfLines % NUMBER_OF_THREADS;

            int startLine = 0;

            for (int workerLine = 0; workerLine < NUMBER_OF_THREADS; workerLine++) {
                int endLine = startLine + elementsPerThread;
                if (remainingElements > 0) {
                    endLine++;
                    remainingElements--;
                }
                // executorService.submit(new Worker(pivotLine, startLine, endLine));

                threads[workerLine] = new Thread(new Worker(pivotLine, startLine, endLine));
                threads[workerLine].start();

                startLine = endLine;
            }

            for (int workerLine = 0; workerLine < NUMBER_OF_THREADS; workerLine++) {
                try {
                    threads[workerLine].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            /*executorService.shutdown();
            try {
                if (!executorService.awaitTermination(80, TimeUnit.MILLISECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }*/

        }

        return extendedMatrix;
    }

    private class Worker implements Runnable {

        private int pivotLine;
        private int startLine;
        private int endLine;

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
                for (int column = 0; column < 2 * numberOfLines; column++) {
                    if (column != pivotLine) {
                        extendedMatrix[currentLine][column] = extendedMatrix[currentLine][column] - element * extendedMatrix[pivotLine][column];
                    }
                }
                extendedMatrix[currentLine][pivotLine] = 0;
            }
        }
    }

}

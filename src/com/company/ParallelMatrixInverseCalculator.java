package com.company;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Parallel matrix inverse calculator
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ParallelMatrixInverseCalculator extends MatrixInverseCalculator implements Serializable {

    private int numberOfThreads;
    private int pivotLine;

    private AtomicBoolean finishedAlgorithm = new AtomicBoolean(false);

    private CyclicBarrier waitPivotInitializationCyclicBarrier;
    private CyclicBarrier waitThreadsComputationsCyclicBarrier;

    private Set<Thread> threads = new HashSet<>();

    public ParallelMatrixInverseCalculator(double[][] matrix, int numberOfThreads) {
        super(matrix);

        this.numberOfThreads = numberOfThreads;
        this.waitPivotInitializationCyclicBarrier = new CyclicBarrier(numberOfThreads + 1);
        this.waitThreadsComputationsCyclicBarrier = new CyclicBarrier(numberOfThreads + 1);

        init();
    }

    public ParallelMatrixInverseCalculator(double[][] matrix) {
        this(matrix, Runtime.getRuntime().availableProcessors());
    }

    private void init() {
        int elementsPerThread = numberOfLines / numberOfThreads;
        int remainingElements = numberOfLines % numberOfThreads;

        int startLine = 0;

        // threads allocation
        for (int workerId = 0; workerId < numberOfThreads; workerId++) {
            int endLine = startLine + elementsPerThread;
            if (remainingElements > 0) {
                endLine++;
                remainingElements--;
            }
            threads.add(new Thread(new Worker(startLine, endLine)));
            startLine = endLine;
        }
    }

    /**
     * Parallel implementation
     *
     * @return extended matrix [I_n | matrix^-1]
     * @throws MatrixNotInvertibleException if matrix is not invertible
     */
    @Override
    public double[][] computeMatrixInverse() throws MatrixNotInvertibleException {

        for (pivotLine = 0; pivotLine < numberOfLines; pivotLine++) {

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

            if (pivotLine == 0) {
                // start threads
                threads.forEach(Thread::start);
            }

            try {
                waitPivotInitializationCyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }

            if (pivotLine == numberOfLines - 1) {

                // mark algorithm as finished and wait for threads to complete
                finishedAlgorithm.set(true);
                try {
                    waitThreadsComputationsCyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }

                threads.forEach(thread -> {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

            } else {
                try {
                    waitThreadsComputationsCyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }

        }

        return extendedMatrix;
    }

    private class Worker implements Runnable {
        private int startLine;
        private int endLine;

        private Worker(int startLine, int endLine) {
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
        public void run() {
            while (!finishedAlgorithm.get()) {
                try {
                    waitPivotInitializationCyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }

                int currentPivotLine = pivotLine;
                for (int currentLine = startLine; currentLine < endLine; currentLine++) {
                    if (currentLine == currentPivotLine) {
                        continue;
                    }
                    double element = extendedMatrix[currentLine][currentPivotLine] / extendedMatrix[currentPivotLine][currentPivotLine];

                    for (int column = currentPivotLine + 1; column < numberOfColumns; column++) {
                        extendedMatrix[currentLine][column] -= element * extendedMatrix[currentPivotLine][column];
                    }
                    extendedMatrix[currentLine][currentPivotLine] = 0;
                }

                try {
                    waitThreadsComputationsCyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

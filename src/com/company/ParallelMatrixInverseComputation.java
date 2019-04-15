package com.company;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelMatrixInverseComputation {
    private double[][] matrix;

    public ParallelMatrixInverseComputation(double[][] matrix) {
        this.matrix = MatrixUtils.addIdentityMatrix(matrix);
    }

    private class Worker implements Runnable {

        private int pivotLine;
        private int startLine;
        private int endLine;

        public Worker(int pivotLine, int startLine, int endLine) {
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
        public void run() {
            for(int currentLine = startLine; currentLine < this.endLine; currentLine++){
                if (currentLine == pivotLine) {
                    return;
                }
                for (int column = 0; column < 2 * matrix.length; column++) {
                    if (column != pivotLine) {
                        matrix[currentLine][column] = matrix[currentLine][column] - matrix[currentLine][pivotLine] * matrix[pivotLine][column] / matrix[pivotLine][pivotLine];
                    }
                }
            }
        }
    }

    public double[][] getMatrixInverseParallel() throws MatrixNotInvertibleException, InterruptedException {

        int numberOfThreads = 4;

        for(int i = 0; i < matrix.length; i++) {

            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

            if(DoubleUtils.equals(matrix[i][i], 0.0)) {
                // find column to swap
                int currentLine = i;
                while(currentLine < matrix.length && DoubleUtils.equals(matrix[currentLine][i], 0.0)) {
                    currentLine++;
                }
                if(currentLine == matrix.length) {
                    throw new MatrixNotInvertibleException();
                }
                MatrixUtils.swapMatrixLines(matrix, i, currentLine);
            }

            double pivot = matrix[i][i];

            for(int j = 0; j < 2 * matrix.length; j++) {
                matrix[i][j] /= pivot;
            }

            // launch threads
            //Thread[] threads = new Thread[numberOfThreads];
            int elementsPerThread = matrix.length / numberOfThreads;
            for(int workerLine = 0; workerLine < numberOfThreads; workerLine++) {
                int startLine = workerLine * elementsPerThread;
                int endLine = (workerLine + 1) * elementsPerThread;
                if(workerLine == numberOfThreads - 1) {
                    endLine = matrix.length;
                }
                //threads[workerLine] = new Thread(new Worker(i, startLine, endLine));
                //threads[workerLine].start();

                executorService.submit(new Worker(i, startLine, endLine));
            }

            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(800, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }

            /*for(int workerLine = 0; workerLine < numberOfThreads; workerLine++) {
                threads[workerLine].join();
            }*/

            for(int line = 0; line < matrix.length; line++) {
                if(line != i) {
                    matrix[line][i] = 0;
                }
            }

        }

        return MatrixUtils.getInverseMatrix(matrix);
    }

}

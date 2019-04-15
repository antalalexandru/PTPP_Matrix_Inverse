package com.company;

public class IntensiveMatrixInverseClaulator extends MatrixInverseCalculator {

    private Object[] columnMutexes;

    private final static int NUMBER_OF_THREADS = 2;

    public IntensiveMatrixInverseClaulator(double[][] extendedMatrix) {
        super(extendedMatrix);
        columnMutexes = new Object[2 * extendedMatrix.length];
        for(int i = 0; i < 2 * extendedMatrix.length; i++) {
            columnMutexes[i] = new Object();
        }
    }

    /*
    * Suppose there will be no null elements on matrix main diagonal
    * */
    @Override
    public double[][] computeMatrixInverse() throws MatrixNotInvertibleException {

        Thread[] threads = new Thread[NUMBER_OF_THREADS];

        int elementsPerThread = numberOfLines / NUMBER_OF_THREADS;
        int remainingElements = numberOfLines % NUMBER_OF_THREADS;

        int startLine = 0;

        for (int workerLine = 0; workerLine < NUMBER_OF_THREADS; workerLine++) {
            int endLine = startLine + elementsPerThread;
            if (remainingElements > 0) {
                endLine++;
                remainingElements--;
            }

            threads[workerLine] = new Thread(new Worker(startLine, endLine));
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

        return extendedMatrix;
    }

    private class Worker implements Runnable {

        private int startLine;
        private int endLine;

        public Worker(int startLine, int endLine) {
            this.startLine = startLine;
            this.endLine = endLine;
        }

        @Override
        public void run() {

            for(int pivotLine = startLine; pivotLine < endLine; pivotLine++) {

                double pivot = extendedMatrix[pivotLine][pivotLine];

                for(int column = pivotLine; column < extendedMatrix.length * 2; column++) {
                    extendedMatrix[pivotLine][column] /= pivot;
                }

                for(int currentLine = 0; currentLine < extendedMatrix.length; currentLine++) {
                    if(currentLine == pivotLine) {
                        continue;
                    }
                    for(int currentColumn = pivotLine + 1; currentColumn < extendedMatrix.length * 2; currentColumn++) {
                        // double element = extendedMatrix[currentLine][line] / extendedMatrix[line][line];
                        synchronized (columnMutexes[currentColumn]) {
                            extendedMatrix[currentLine][currentColumn] -= extendedMatrix[currentLine][pivotLine] * extendedMatrix[pivotLine][currentColumn] / extendedMatrix[pivotLine][pivotLine];
                        }

                    }
                }

                for(int currentLine = pivotLine + 1; currentLine < extendedMatrix.length; currentLine++) {
                    extendedMatrix[currentLine][pivotLine] = 0;
                }

                System.out.println("Pasul" + pivotLine);
                MatrixUtils.printMatrix(extendedMatrix);

            }
        }
    }

}

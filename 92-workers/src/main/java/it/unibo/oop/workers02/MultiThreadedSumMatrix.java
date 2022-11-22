package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix {

    private final int threadNumbers;

    public MultiThreadedSumMatrix(final int threadNumbers) {
        this.threadNumbers = threadNumbers;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startpos;
        private final int elemNumber;
        private double result;

        Worker(final double[][] matrix, final int startpos, final int elemNumber) {
            super();
            this.matrix = matrix;
            this.startpos = startpos;
            this.elemNumber = elemNumber;
        }

        public void run() {
            System.out.println("Working from position " + startpos + " to position "
                    + (startpos + elemNumber - 1));
            // for (int i = startpos; startpos + elemNumber > matrix.length ? i <
            // matrix.length
            // : i < startpos + elemNumber; i++) {
            // }

            for (int i = startpos; i < matrix.length && i < startpos + elemNumber; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    this.result += this.matrix[i][j];
                }
            }

        }

        public double getResult() {
            return this.result;
        }
    }

    public double sum(double[][] matrix) {
        final int size = matrix.length % threadNumbers + matrix.length / threadNumbers;

        /*
         * final Worker[][] matrixWorkers = new Worker[threadNumbers][threadNumbers];
         * for (int i = 0; i < matrix.length; i += size) {
         * for (int j = i + 1; j < matrix.length; j += size) {
         * // matrixWorkers[i][j] =
         * }
         * }
         */

        final List<Worker> workers = new ArrayList<>(threadNumbers);

        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }

        for (final Worker worker : workers) {
            worker.start();
        }

        double sum = 0;
        /*
         * for (final Worker[] worker : matrixWorkers) {
         * sum += worker.getResult();
         * }
         */
        for (final Worker worker : workers) {
            try {
                worker.join();
                sum += worker.getResult();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                throw new IllegalStateException(e);
            }
        }

        return sum;
    }

}

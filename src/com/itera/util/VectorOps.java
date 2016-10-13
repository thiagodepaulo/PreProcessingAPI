/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class VectorOps {

    public static void add(float[] a, float[] b) {
        int nDim = a.length;
        for (int i = 0; i < nDim; i++) {
            a[i] = a[i] + b[i];
        }
    }

    public static void add(double[] a, double[] b) {
        int nDim = a.length;
        for (int i = 0; i < nDim; i++) {
            a[i] = a[i] + b[i];
        }
    }

    public static double sum(float[] a, float[] b) {
        int nDim = a.length;
        double sum = 0;
        for (int i = 0; i < nDim; i++) {
            sum += a[i] + b[i];
        }
        return sum;
    }

    public static double sum(float[] a) {
        double sum = 0;
        for (float v : a) {
            sum += v;
        }
        return sum;
    }

    public static double sum(double[] a) {
        double sum = 0;
        for (double v : a) {
            sum += v;
        }
        return sum;
    }

    public static double squaredSum(float[] a) {
        double sum = 0;
        for (float v : a) {
            sum += v * v;
        }
        return sum;
    }

    public static double dot(float[] a, float b[]) {
        int nDim = a.length;
        double sum = 0;
        for (int i = 0; i < nDim; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    public static double[] dot(float[] a, float[][] b) {
        int n = b.length;
        int nDim = a.length;
        double[] r = new double[n];
        double sum;
        for (int i = 0; i < n; i++) {
            sum = 0;
            for (int j = 0; j < nDim; j++) {
                sum += a[j] * b[i][j];
            }
            r[i] = sum;
        }
        return r;
    }

    public static float[] mean(List<float[]> a) {
        if (a.isEmpty()) {
            return null;
        }
        int n = a.size();
        int nDim = a.get(0).length;
        float[] mean = new float[nDim];
        for (float[] vec : a) {
            add(mean, vec);
        }
        for (int i = 0; i < nDim; i++) {
            mean[i] /= n;
        }
        return mean;
    }

    public static int argmax(double[] a) {
        double max = a[0];
        int idx_max = 0;
        for (int i = 1; i < a.length; i++) {
            if (a[i] > max) {
                max = a[i];
                idx_max = i;
            }
        }
        return idx_max;
    }

    public static int argmax(int[] a) {
        int max = a[0];
        int idx_max = 0;
        for (int i = 1; i < a.length; i++) {
            if (a[i] > max) {
                max = a[i];
                idx_max = i;
            }
        }
        return idx_max;
    }

    public static double[] normalize(double[] a) {
        double sum = 0;
        double[] r = new double[a.length];
        for (double ai : a) {
            sum += ai;
        }
        for (int i = 0; i < a.length; i++) {
            r[i] = a[i] / sum;
        }
        return r;
    }

    public static int[] argsort(final double[] a, final boolean ascending) {
        Integer[] indexes = new Integer[a.length];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }
        Arrays.sort(indexes, new Comparator<Integer>() {
            @Override
            public int compare(final Integer i1, final Integer i2) {
                return (ascending ? 1 : -1) * Double.compare(a[i1], a[i2]);
            }
        });

        int[] b = new int[indexes.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = indexes[i].intValue();
        }
        return b;
    }

    public static void unitVec(float[] a) {
        int n = a.length;
        float sqrtSum = 0;
        for (int i = 0; i < n; i++) {
            sqrtSum += a[i] * a[i];
        }
        sqrtSum = (float) Math.sqrt(sqrtSum);
        for (int i = 0; i < n; i++) {
            a[i] = a[i] / sqrtSum;
        }
    }

    public static <T> void print(T[] a) {
        for (T t : a) {
            System.out.print(t + " ");
        }
        System.out.println();
    }

    public static void print(float[] a) {
        for (float t : a) {
            System.out.print(t + " ");
        }
        System.out.println();
    }

    public static void print(double[] a) {
        for (double t : a) {
            System.out.print(t + " ");
        }
        System.out.println();
    }

}

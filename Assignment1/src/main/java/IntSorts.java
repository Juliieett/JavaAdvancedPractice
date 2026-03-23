package org.example.sortingbenchmark;

import java.util.Arrays;

public final class IntSorts {
    private IntSorts() {}

    public static void bubbleSort(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < a.length - 1 - i; j++) {
                if (a[j] > a[j + 1]) {
                    int tmp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = tmp;
                    swapped = true;
                }
            }
            if (!swapped) {
                return;
            }
        }
    }

    public static void quickSort(int[] a) {
        if (a.length < 2) {
            return;
        }
        quickSortRange(a, 0, a.length - 1);
    }

    private static void quickSortRange(int[] a, int low, int high) {
        while (low < high) {
            if (high - low <= 16) {
                insertionSort(a, low, high);
                return;
            }

            int pivotIndex = medianOfThreePivotIndex(a, low, high);
            int pivotFinalIndex = partition(a, low, high, pivotIndex);

            // Recurse into smaller side to keep stack bounded.
            if (pivotFinalIndex - low < high - pivotFinalIndex) {
                quickSortRange(a, low, pivotFinalIndex - 1);
                low = pivotFinalIndex + 1;
            } else {
                quickSortRange(a, pivotFinalIndex + 1, high);
                high = pivotFinalIndex - 1;
            }
        }
    }

    private static int partition(int[] a, int low, int high, int pivotIndex) {
        int pivotValue = a[pivotIndex];
        swap(a, pivotIndex, high);

        int i = low;
        for (int j = low; j < high; j++) {
            if (a[j] <= pivotValue) {
                swap(a, i, j);
                i++;
            }
        }
        swap(a, i, high);
        return i;
    }

    // Median-of-three pivot to reduce worst-case on structured inputs.
    private static int medianOfThreePivotIndex(int[] a, int low, int high) {
        int mid = low + ((high - low) >>> 1);
        int x = a[low];
        int y = a[mid];
        int z = a[high];

        if (x <= y) {
            if (y <= z) return mid;
            if (x <= z) return high;
            return low;
        } else {
            if (x <= z) return low;
            if (y <= z) return high;
            return mid;
        }
    }

    private static void insertionSort(int[] a, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= low && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    public static void lsdRadixSortBase256(int[] a) {
        // Base-256 LSD radix sort (4 passes) with signed-int handling via value ^ Integer.MIN_VALUE.
        int n = a.length;
        if (n < 2) {
            return;
        }

        int[] src = a;
        int[] dst = new int[n];

        for (int pass = 0; pass < 4; pass++) {
            int shift = pass * 8;
            int[] count = new int[256];

            for (int i = 0; i < n; i++) {
                int key = src[i] ^ Integer.MIN_VALUE;
                int c = (key >>> shift) & 0xFF;
                count[c]++;
            }

            int sum = 0;
            for (int i = 0; i < 256; i++) {
                int old = count[i];
                count[i] = sum;
                sum += old;
            }

            for (int i = 0; i < n; i++) {
                int val = src[i];
                int key = val ^ Integer.MIN_VALUE;
                int c = (key >>> shift) & 0xFF;
                dst[count[c]++] = val;
            }

            int[] tmp = src;
            src = dst;
            dst = tmp;
        }

        if (src != a) {
            System.arraycopy(src, 0, a, 0, n);
        }
    }

    public static void arraysSort(int[] a) {
        Arrays.sort(a);
    }

    private static void swap(int[] a, int i, int j) {
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }
}


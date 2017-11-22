import java.util.Arrays;

/**
 * Class containing all the sorting algorithms from 61B to date.
 *
 * You may add any number instance variables and instance methods
 * to your Sorting Algorithm classes.
 *
 * You may also override the empty no-argument constructor, but please
 * only use the no-argument constructor for each of the Sorting
 * Algorithms, as that is what will be used for testing.
 *
 * Feel free to use any resources out there to write each sort,
 * including existing implementations on the web or from DSIJ.
 *
 * All implementations except Distribution Sort adopted from Algorithms,
 * a textbook by Kevin Wayne and Bob Sedgewick. Their code does not
 * obey our style conventions.
 */
public class MySortingAlgorithms {

    /**
     * Java's Sorting Algorithm. Java uses Quicksort for ints.
     */
    public static class JavaSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            Arrays.sort(array, 0, k);
        }

        @Override
        public String toString() {
            return "Built-In Sort (uses quicksort for ints)";
        }
    }

    /** Insertion sorts the provided data. */
    public static class InsertionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            for (int i = 1; i < k; i += 1) {
                int temp = array[i];
                int j = i - 1;
                while (j >= 0 && array[j] > temp) {
                    array[j + 1] = array[j];
                    j = j - 1;
                }
                array[j + 1] = temp;
            }
        }

        @Override
        public String toString() {
            return "Insertion Sort";
        }
    }

    /**
     * Selection Sort for small K should be more efficient
     * than for larger K. You do not need to use a heap,
     * though if you want an extra challenge, feel free to
     * implement a heap based selection sort (i.e. heapsort).
     */
    public static class SelectionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            for (int i = 0; i < k - 1; i += 1) {
                int small = i;
                for (int j = i + 1; j < k; j += 1) {
                    if (array[j] < array[small]) {
                        small = j;
                    }
                }
                if (small != i) {
                    swap(array, i, small);
                }
            }
        }

        @Override
        public String toString() {
            return "Selection Sort";
        }
    }

    /** Your mergesort implementation. An iterative merge
      * method is easier to write than a recursive merge method.
      * Note: I'm only talking about the merge operation here,
      * not the entire algorithm, which is easier to do recursively.
      */
    public static class MergeSort implements SortingAlgorithm {
        private int returnList[];
        private int tempList[];

        @Override
        public void sort(int[] array, int k) {
            returnList = array;
            tempList = new int [k];
            mergesort(0, k - 1);
        }

        private void mergesort(int low, int high) {
            if (low < high) {
                mergesort(low, low + (high - low) / 2);
                mergesort(low + (high - low) / 2 + 1, high);
                merge(low, low + (high - low) / 2, high);
            }
        }

        private void merge(int low, int mid, int high) {
            for (int i = low; i <= high; i += 1) {
                tempList[i] = returnList[i];
            }
            int lowCount = low;
            int highCount = mid + 1;
            int temp = low;
            while (lowCount <= mid && highCount <= high) {
                if (tempList[lowCount] <= tempList[highCount]) {
                    returnList[temp] = tempList[lowCount];
                    lowCount += 1;
                } else {
                    returnList[temp] = tempList[highCount];
                    highCount += 1;
                }
                temp += 1;
            }
            while (lowCount <= mid) {
                returnList[temp] = tempList[lowCount];
                lowCount += 1;
                temp += 1;
            }
        }

        @Override
        public String toString() {
            return "Merge Sort";
        }
    }

    /**
     * Your Distribution Sort implementation.
     * You should create a count array that is the
     * same size as the value of the max digit in the array.
     */
    public static class DistributionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            int max = 0;
            for (int i = 0; i < k; i += 1) {
                if (array[i] > max) {
                    max = array[i];
                }
            }
            int count[] = new int[max + 1];
            for (int i = 0; i < k; i += 1) {
                count[array[i]] += 1;
            }
            int index = 0;
            for (int i = 0; i < count.length; i += 1) {
                int temp = count[i];
                for (int j = 0; j < temp; j += 1) {
                    array[index] = i;
                    index += 1;
                }
            }
        }

        @Override
        public String toString() {
            return "Distribution Sort";
        }
    }

    /** Your Heapsort implementation.
     */
    public static class HeapSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            int total = k - 1;
            for (int i = total / 2; i >= 0; i -=1) {
                heapify(array, i, total);
            }
            for (int i = total; i >0; i -= 1) {
                swap(array, 0, i);
                total -= 1;
                heapify(array, 0, total);
            }
        }

        private void heapify(int[] array, int index, int total) {
            int left = index * 2;
            int right = left + 1;
            int temp = index;

            if (left <= total && array[left] > array[temp]) {
                temp = left;
            }
            if (right <= total && array[right] > array[temp]) {
                temp = right;
            }
            if (index != temp) {
                swap(array, index, temp);
                heapify(array, temp, total);
            }
        }

        @Override
        public String toString() {
            return "Heap Sort";
        }
    }

    /** Your Quicksort implementation.
     */
    public static class QuickSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Quicksort";
        }
    }

    /* For radix sorts, treat the integers as strings of x-bit numbers.  For
     * example, if you take x to be 2, then the least significant digit of
     * 25 (= 11001 in binary) would be 1 (01), the next least would be 2 (10)
     * and the third least would be 1.  The rest would be 0.  You can even take
     * x to be 1 and sort one bit at a time.  It might be interesting to see
     * how the times compare for various values of x. */

    /**
     * LSD Sort implementation.
     */
    public static class LSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "LSD Sort";
        }
    }

    /**
     * MSD Sort implementation.
     */
    public static class MSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "MSD Sort";
        }
    }

    /** Exchange A[I] and A[J]. */
    private static void swap(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

}

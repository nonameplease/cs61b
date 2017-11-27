import java.util.Arrays;
import java.util.Comparator;

/** Minimal spanning tree utility.
 *  @author
 */
public class MST {

    /** Given an undirected, weighted, connected graph whose vertices are
     *  numbered 1 to V, and an array E of edges, returns a list of edges
     *  in E that form a minimal spanning tree of the input graph.
     *  Each edge in E is a three-element int array of the form (u, v, w),
     *  where 0 < u < v <= V are vertex numbers, and 0 <= w is the weight
     *  of the edge. The result is an array containing edges from E.
     *  Neither E nor the arrays in it may be modified.  There may be
     *  multiple edges between vertices.  The objects in the returned array
     *  are a subset of those in E (they do not include copies of the
     *  original edges, just the original edges themselves.) */
    public static int[][] mst(int V, int[][] E) {

        return null;  // FIXME
    }

    /** An ordering of edges by weight. */
    private static final Comparator<int[]> EDGE_WEIGHT_COMPARATOR =
        new Comparator<int[]>() {
            @Override
            public int compare(int[] e0, int[] e1) {
                return e0[2] - e1[2];
            }
        };


    /** Your Heapsort implementation.
     */
    public static class HeapSort{
        public void sort(int[] array, int k) {
            int total = k - 1;
            for (int i = total / 2; i >= 0; i -= 1) {
                heapify(array, i, total);
            }
            for (int i = total; i > 0; i -= 1) {
                swap(array, 0, i);
                total -= 1;
                heapify(array, 0, total);
            }
        }

        /**
         * Helper function for HeapSort.
         * @param array Input array.
         * @param index Index.
         * @param total Total number.
         */
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

    /** Exchange A[I] and A[J]. */
    private static void swap(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

}

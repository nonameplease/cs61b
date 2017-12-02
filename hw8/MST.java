import java.util.Comparator;
import java.util.PriorityQueue;

/** Minimal spanning tree utility.
 *  @author Scott Shao
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
        return mstCanPass(V, E);
    }

    /**
     * A older implementation that can pass.
     * @param V Number of vertices.
     * @param E A double array.
     * @return A double array.
     */
    public static int[][] mstCanPass(int V, int[][] E) {
        PriorityQueue<int[]> mst =
                new PriorityQueue<int[]>(EDGE_WEIGHT_COMPARATOR);
        PriorityQueue<int[]> fringe =
                new PriorityQueue<int[]>(EDGE_WEIGHT_COMPARATOR);
        for (int i = 0; i < E.length; i += 1) {
            fringe.offer(E[i]);
        }
        UnionFind uf = new UnionFind(V);
        while (!fringe.isEmpty()) {
            int[] cur = fringe.poll();
            if (!uf.samePartition(cur[0], cur[1])) {
                uf.union(cur[0], cur[1]);
                mst.offer(cur);
            }
        }
        int[][] result = new int[mst.size()] [3];
        for (int i = 0; i < result.length && !mst.isEmpty(); i += 1) {
            int[] cur = mst.poll();
            result[i] = cur;
        }
        return result;
    }

    /** An ordering of edges by weight. */
    private static final Comparator<int[]> EDGE_WEIGHT_COMPARATOR =
        new Comparator<int[]>() {
            @Override
            public int compare(int[] e0, int[] e1) {
                return e0[2] - e1[2];
            }
        };

    /**
     * buggy.
     * @param V number of vertices.
     * @param E edges.
     */
    private void heapMst(int V, int[][] E) {
        int[][] weights = E;
        heapSort(weights);
        int edges = 1;
        int index = 0;
        UnionFind uf = new UnionFind(V);
        while (edges != V) {
            uf.union(weights[index][0], weights[index][1]);
        }
    }

    /**
     * Heapsort tailored for double array.
     * @param arr A double array in this class.
     */
    private void heapSort(int[][] arr) {
        int n = arr.length;

        for (int i = n / 2; i >= 0; i -= 1) {
            heapify(arr, n, i);
        }

        for (int i = n - 1; i >= 0; i -= 1) {
            int[] temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;
            heapify(arr, i, 0);
        }
    }

    /**
     * Heapify the array.
     * @param arr A array.
     * @param n Number of index.
     * @param index Largest.
     */
    private void heapify(int[][] arr, int n, int index) {
        int largest = index;
        int left = 2 * index + 1;
        int right = 2 * index + 2;
        if (left < n && arr[left][2] > arr[largest][2]) {
            largest = left;
        }
        if (right < n && arr[right][2] > arr[largest][2]) {
            largest = right;
        }
        if (largest != index) {
            int[] swap = arr[index];
            arr[index] = arr[largest];
            arr[largest] = swap;
            heapify(arr, n, largest);
        }
    }
}

/* NOTE: The file ArrayUtil.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2 */

/** Array utilities.
 *  @author Scott Shao
 */
class Arrays {
    /* C. */
    /** Returns a new array consisting of the elements of A followed by the
     *  the elements of B. */
    static int[] catenate(int[] A, int[] B) {
        int[] C = new int[A.length + B.length];
        System.arraycopy(A, 0, C, 0, A.length);
        System.arraycopy(B, 0, C, A.length, B.length);
        return C;
    }

    /** Returns the array formed by removing LEN items from A,
     *  beginning with item #START. */
    static int[] remove(int[] A, int start, int len) {
        if (len == 0) {
            return A;
        }
        int[] B = new int[A.length - len];
        System.arraycopy(A, 0, B, 0, start);
        System.arraycopy(A, start + len, B, start, A.length - start - len);
        return B;
    }

    /* E. */
    /** Returns the array of arrays formed by breaking up A into
     *  maximal ascending lists, without reordering.
     *  For example, if A is {1, 3, 7, 5, 4, 6, 9, 10}, then
     *  returns the three-element array
     *  {{1, 3, 7}, {5}, {4, 6, 9, 10}}. */
    static int[][] naturalRuns(int[] A) {
        if (A == null) {
            return null;
        }
        int length = 1;
        int value = A[0];
        for (int i = 0; i < A.length; i++) {
            if (value > A[i]) {
                length += 1;
            }
            value = A[i];
        }
        int[] startingPoint = new int[length];
        int[] duration = new int[length];
        startingPoint[0] = 0;
        value = A[0];
        int temp = 1;
        int temp2 = 0;
        int temp3 = 0;
        for (int i = 0; i < A.length; i++) {
            if (value > A[i]) {
                startingPoint[temp] = i;
                duration[temp - 1] = temp2;
                temp3 += temp2;
                temp += 1;
                temp2 = 1;
            } else {
                temp2 += 1;
            }
            value = A[i];
        }
        duration[length - 1] = A.length - temp3;
        int[][] arrayOfArray = new int[length][];
        for (int i = 0; i < length; i++) {
            System.arraycopy(A, startingPoint[i], arrayOfArray[i], 0, duration[i]);
        }
        return arrayOfArray;
    }
}

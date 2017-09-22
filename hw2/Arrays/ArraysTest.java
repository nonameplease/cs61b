import org.junit.Test;
import static org.junit.Assert.*;

/** A series of tests that test the catenate, remove, and naturalRuns
 * methods in the Arrays class.
 *  @author Scott Shao
 */

public class ArraysTest {
    @Test
    public void testArrays() {
        int[] A = new int[]{1, 2, 3};
        int[] B = new int[]{4, 5, 6};
        int[] C = new int[]{1, 2, 3, 4, 5, 6};
        int[] D = new int[]{};

        assertArrayEquals(C, Arrays.catenate(A, B));
        assertArrayEquals(A, Arrays.catenate(A, D));
        assertArrayEquals(A, Arrays.remove(C, 3, 3));

        int[] E = {1, 3, 7, 5, 4, 6, 9, 10};
        int[][] F = {{1, 3, 7}, {5}, {4, 6, 9, 10}};
        assertArrayEquals(F, Arrays.naturalRuns(E));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }
}

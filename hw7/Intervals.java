import org.junit.Test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Arrays;
import static org.junit.Assert.*;

/** HW #8, Problem 3.
 *  @author Scott Shao
  */
public class Intervals {
    /** Assuming that INTERVALS contains two-element arrays of integers,
     *  <x,y> with x <= y, representing intervals of ints, this returns the
     *  total length covered by the union of the intervals. */
    public static int coveredLength(List<int[]> intervals) {
        ArrayList<int[]> last = new ArrayList<int[]>();
        int size = intervals.size();
        for (int[] i : intervals) {
            last.add(new int[] {i[0], -1});
            last.add(new int[] {i[1], 1});
        }
        Collections.sort(last, new Helper());
        int num = 0;
        int start = 0;
        int total = 0;
        for (int[] i : last) {
            if (num == 0) {
                start = i[0];
            }
            num = num - i[1];
            if (num == 0) {
                total += i[0] - start;
            }
        }
        return total;
    }

    /**
     * Helper class for the coveredLength.
     */
    private static class Helper implements Comparator<int[]> {
        @Override
        public int compare(int[] x, int[] y) {
            if (x[0] == y[0]) {
                return x[1] - y[1];
            } else {
                return x[0] - y[0];
            }
        }
    }

    /** Test intervals. */
    static final int[][] INTERVALS = {
        {19, 30},  {8, 15}, {3, 10}, {6, 12}, {4, 5},
    };
    /** Covered length of INTERVALS. */
    static final int CORRECT = 23;

    /** Performs a basic functionality test on the coveredLength method. */
    @Test
    public void basicTest() {
        assertEquals(CORRECT, coveredLength(Arrays.asList(INTERVALS)));
    }

    /** Runs provided JUnit test. ARGS is ignored. */
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(Intervals.class));
    }

}

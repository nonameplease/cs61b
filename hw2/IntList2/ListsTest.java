import org.junit.Test;
import static org.junit.Assert.*;

/** A series of tests that test the naturalRuns
 * method in the Lists class.
 *  @author Scott Shao
 */

public class ListsTest {
    /** FIXME
     */

    // It might initially seem daunting to try to set up
    // Intlist2 expected.
    //
    // There is an easy way to get the IntList2 that you want in just
    // few lines of code! Make note of the IntList2.list method that
    // takes as input a 2D array.

    @Test
    public void testLists() {
        IntList L = IntList.list(1,3,7,5,4,6,9,10,10,11);
        IntList2 L2 = IntList2.list(IntList.list(1,3,7), IntList.list(5),
                IntList.list(4,6,9,10), IntList.list(10,11));
        assertEquals(L2, Lists.naturalRuns(L));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ListsTest.class));
    }
}

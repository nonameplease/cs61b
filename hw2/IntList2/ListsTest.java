import org.junit.Test;
import static org.junit.Assert.*;

/** A series of tests that test the naturalRuns
 * method in the Lists class.
 *  @author Scott Shao
 */

public class ListsTest {
    @Test
    public void testLists() {
        IntList L = IntList.list(1, 3, 7, 5, 4, 6, 9, 10, 10, 11);
        IntList2 L2 = IntList2.list(IntList.list(1, 3, 7), IntList.list(5),
                IntList.list(4, 6, 9, 10), IntList.list(10, 11));
        assertEquals(L2, Lists.naturalRuns(L));
        IntList2 L3 = IntList2.list();
        assertEquals(L3, Lists.naturalRuns(IntList.list()));
        IntList2 L4 = IntList2.list(IntList.list(3), IntList.list(2),
                IntList.list(1));
        assertEquals(L4, Lists.naturalRuns(IntList.list(3, 2, 1)));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ListsTest.class));
    }
}

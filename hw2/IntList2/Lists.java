/* NOTE: The file Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2, Problem #1. */

/** List problem.
 *  @author Scott Shao
 */
class Lists {
    /** Return the list of lists formed by breaking up L into "natural runs":
     *  that is, maximal strictly ascending sublists, in the same order as
     *  the original.  For example, if L is (1, 3, 7, 5, 4, 6, 9, 10, 10, 11),
     *  then result is the four-item list
     *            ((1, 3, 7), (5), (4, 6, 9, 10), (10, 11)).
     *  Destructive: creates no new IntList items, and may modify the
     *  original list pointed to by L. */
    static IntList2 naturalRuns(IntList L) {
        /* *Replace this body with the solution. */
        if (L == null) {
            return null;
        } else {
            IntList runs = L;
            while (L.tail != null && L.tail.head > L.head) {
                L = L.tail;
            }
            IntList ptr = L;
            IntList2 rest = naturalRuns(ptr.tail);
            ptr.tail = null;
            return new IntList2(runs, rest);
        }
    }
}

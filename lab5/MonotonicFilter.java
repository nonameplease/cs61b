import java.util.Iterator;
import utils.Filter;

/** A kind of Filter that lets all the VALUE elements of its input sequence
 *  that are larger than all the preceding values to go through the
 *  Filter.  So, if its input delivers (1, 2, 3, 3, 2, 1, 5), then it
 *  will produce (1, 2, 3, 5).
 *  @author Scott Shao
 */
class MonotonicFilter<Value extends Comparable<Value>> extends Filter<Value> {

    /** A filter of values from INPUT that delivers a monotonic
     *  subsequence.  */
    MonotonicFilter(Iterator<Value> input) {
        super(input);
    }

    @Override
    protected boolean keep() {
        if (last == null || last.compareTo(_next) < 0) {
            last = _next;
            return true;
        }
        return false;
    }

    /** A private LAST that holds a Value type value.
     */
    private Value last;

}

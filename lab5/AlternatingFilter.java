import java.util.Iterator;
import utils.Filter;

/** A kind of Filter that lets through every other VALUE element of
 *  its input sequence, starting with the first.
 *  @author Scott Shao
 */
class AlternatingFilter<Value> extends Filter<Value> {

    /** A filter of values from INPUT that lets through every other
     *  value. */
    AlternatingFilter(Iterator<Value> input) {
        super(input);
    }

    @Override
    protected boolean keep() {
        discard = !discard;
        return discard;
    }

    /** A boolean DISCARD that tracks alternation.
     */
    private boolean discard = false;

}

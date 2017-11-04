
import java.util.LinkedList;
import java.util.List;

/** A set of String values.
 *  @author Scott Shao
 */
class ECHashStringSet implements StringSet {

    /**
     * Maximum load factor. This could also used to
     * calculate minimum load factor.
     */
    private static final int MAX_LOAD_FACTOR = 5;

    /**
     * The bit operation to get rid of sign bit.
     */
    private static final int REMOVE_TOP_BIT = 0x7fffffff;

    /**
     * The default starting bucket size.
     */
    private static final int STARTING_SIZE = 5;

    /**
     * A LinkedList of LinkedList of String to store buckets.
     */
    private LinkedList<LinkedList<String>> buckets;

    /**
     * The total number of arguments in the ECH.
     */
    private int size;

    /**
     * Constructor with bucket size.
     * @param bucketsSize The size of the bucket.
     */
    public ECHashStringSet(int bucketsSize) {
        buckets = new LinkedList<LinkedList<String>>();
        size = 0;
        for (int i = 0; i < bucketsSize; i += 1) {
            buckets.add(new LinkedList<String>());
        }
    }

    /**
     * Default constructor.
     */
    public ECHashStringSet() {
        this(STARTING_SIZE);
    }

    /**
     * Resize the bucket.
     */
    public void resize() {
        LinkedList<LinkedList<String>> old = buckets;
        buckets = new LinkedList<LinkedList<String>>();
        for (int i = 0; i < old.size() * 5; i += 1) {
            buckets.add(new LinkedList<String>());
        }
        size = 0;
        for (LinkedList<String> sl : old) {
            if (sl != null) {
                for (String s : sl) {
                    this.put(s);
                }
            }
        }
    }

    /**
     * Find current load factor.
     * @return Number of load factor.
     */
    private double load() {
        return ((double) size) / ((double) buckets.size());
    }

    /**
     * Find the corresponding bucket of a potentially
     * negative hashcode.
     * @param hashCode A hashcode, can be negative.
     * @return Bucket number.
     */
    private int findBucketFromHash(int hashCode) {
        int bucketNum = buckets.size();
        int hashCode2 = hashCode & REMOVE_TOP_BIT;
        int whichBucket = hashCode2 % bucketNum;
        return whichBucket;
    }

    /**
     * Adds the string S to the string set. If it is already present in the
     * set, do nothing.
     * @param s A String.
     */
    public void put(String s) {
        if (s != null) {
            if (load() > MAX_LOAD_FACTOR) {
                resize();
            }
            int key = findBucketFromHash(s.hashCode());
            if (buckets.get(key) == null) {
                buckets.add(key, new LinkedList<String>());
            }
            buckets.get(key).add(s);
            size += 1;
        }
    }

    /**
     * Returns true iff S is in the string set.
     * @param s A string argument.
     * @return true iff S is in the string set.
     */
    public boolean contains(String s) {
        if (s != null) {
            int key = findBucketFromHash(s.hashCode());
            if (buckets.get(key) == null) {
                return false;
            } else {
                return buckets.get(key).contains(s);
            }
        } else {
            return false;
        }
    }

    /**
     * Return a list of all members of this set in ascending order.
     * @return a list of all members of this set in ascending order.
     */
    public List<String> asList() {
        List<String> returnValue = new LinkedList<String>();
        for (LinkedList<String> sl : buckets) {
            for (String s : sl) {
                returnValue.add(s);
            }
        }
        return returnValue;
    }
}

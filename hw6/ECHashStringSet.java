
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
     * A boolean to keep track if resizing is in progress.
     */
    private boolean resizing = false;

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
     * @param newCount New bucket number.
     */
    public void resize(int newCount) {
        ECHashStringSet newechss = new ECHashStringSet(newCount);
        newechss.resizing = true;
        for (int i = 0; i < buckets.size(); i += 1) {
            for (String s : buckets.get(i)) {
                newechss.put(s);
            }
        }
        buckets = newechss.buckets;
        newechss.resizing = false;
    }

    /**
     * Adds the string S to the string set. If it is already present in the
     * set, do nothing.
     * @param s A String.
     */
    public void put(String s) {
        size += 1;
        int bucketNum = buckets.size();
        if (size != 1 && size > bucketNum * MAX_LOAD_FACTOR && !resizing) {
            resize(bucketNum * 5);
        } else if (size != 1
                && size < bucketNum / MAX_LOAD_FACTOR && !resizing) {
            resize(bucketNum / 5);
        }

        int hashCode = s.hashCode() & REMOVE_TOP_BIT;
        int whichBucket = hashCode % bucketNum;
        LinkedList<String> temp = buckets.get(whichBucket);
        if (!temp.contains(s)) {
            temp.add(s);
        }
    }

    /**
     * Returns true iff S is in the string set.
     * @param s A string argument.
     * @return true iff S is in the string set.
     */
    public boolean contains(String s) {
        int bucketNum = buckets.size();
        int hashCode = s.hashCode() & REMOVE_TOP_BIT;
        int whichBucket = hashCode % bucketNum;
        LinkedList<String> temp = buckets.get(whichBucket);
        //return temp.contains(s);
        return this.contains(s);
    }

    /**
     * Return a list of all members of this set in ascending order.
     * @return a list of all members of this set in ascending order.
     */
    public List<String> asList() {
        List<String> returnValue = new LinkedList<String>();
        for (int i = 0; i < buckets.size(); i += 1) {
            for (String s : buckets.get(i)) {
                returnValue.add(s);
            }
        }
        return returnValue;
    }
}

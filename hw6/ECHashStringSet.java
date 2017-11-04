// REPLACE THIS STUB WITH THE CORRECT SOLUTION.
// The current contents of this file are merely to allow things to compile 
// out of the box. It bears scant relation to a proper solution (for one thing,
// a hash table should not be a SortedStringSet.)

import java.util.LinkedList;
import java.util.List;

/** A set of String values.
 *  @author
 */
class ECHashStringSet implements StringSet {
    private static final int MAX_LOAD_FACTOR = 5;
    private static final double MIN_LOAD_FACTOR = 0.2;
    private static final int REMOVE_TOP_BIT = 0x7fffffff;
    private static final int STARTING_SIZE = 5;

    private LinkedList<String>[] buckets;
    private int size;
    private boolean resizing = false;

    public ECHashStringSet(int bucketsSize) {
        buckets = new LinkedList[bucketsSize];
        size = 0;
        for (int i = 0; i < buckets.length; i += 1) {
            buckets[i] = new LinkedList<String>();
        }
    }

    public ECHashStringSet() {
        this(STARTING_SIZE);
    }

    public void resize(int newCount) {
        ECHashStringSet newechss = new ECHashStringSet(newCount);
        newechss.resizing = true;
        for (int i = 0; i < buckets.length; i += 1) {
            for (String s : buckets[i]) {
                newechss.put(s);
            }
        }
        buckets = newechss.buckets;
        newechss.resizing = false;
    }

    public void put(String s) {
        size += 1;
        int bucketNum = buckets.length;
        if (size == 1) {

        } else if (size > bucketNum * MAX_LOAD_FACTOR && !resizing) {
            resize(bucketNum * 5);
        } else if (size < bucketNum / MAX_LOAD_FACTOR && !resizing) {
            resize(bucketNum / 5);
        }

        int hashCode = s.hashCode() & REMOVE_TOP_BIT;
        int whichBucket = hashCode % bucketNum;
        LinkedList<String> temp = buckets[whichBucket];
        if (!temp.contains(s)) {
            temp.add(s);
        }
    }

    public boolean contains(String s) {
        if (s != null) {
            return this.contains(s);
        } else {
            return false;
        }
    }

    public List<String> asList() {
        List<String> returnValue = new LinkedList<String>();
        for (int i = 0; i < buckets.length; i += 1) {
            for (String s : buckets[i]) {
                returnValue.add(s);
            }
        }
        return returnValue;
    }
}

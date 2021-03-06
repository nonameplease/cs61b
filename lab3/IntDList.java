
public class IntDList {

    protected DNode _front, _back;

    public IntDList() {
        _front = _back = null;
    }

    public IntDList(Integer... values) {
        _front = _back = null;
        for (int val : values) {
            insertBack(val);
        }
    }

    /** Returns the first item in the IntDList. */
    public int getFront() {
        return _front._val;
    }

    /** Returns the last item in the IntDList. */
    public int getBack() {
        return _back._val;
    }

    /** Return value #I in this list, where item 0 is the first, 1 is the
     *  second, ...., -1 is the last, -2 the second to last.... */
    public int get(int i) {
        if (i >= 0) {
            DNode temp = _front;
            for (int j = 0; j < i; j++) {
                temp = temp._next;
            }
            return temp._val;
        } else {
            DNode temp = _back;
            i = i * -1;
            for (int j = 0; j < i - 1; j++) {
                temp = temp._prev;
            }
            return temp._val;
        }
    }

    /** The length of this list. */
    public int size() {
        int temp = 0;
        for (DNode Temp = _front; Temp != null; Temp = Temp._next) {
            temp += 1;
        }
        return temp;
    }

    /** Adds D to the front of the IntDList. */
    public void insertFront(int d) {
        _front = new DNode(null, d, _front);
        if (_back == null) {
            _back = _front;
        } else {
            _front._next._prev = _front;
        }
    }

    /** Adds D to the back of the IntDList. */
    public void insertBack(int d) {
        _back = new DNode(_back, d, null);
        if (_front == null) {
            _front = _back;
        } else {
            _back._prev._next = _back;
        }
    }

    /** Removes the last item in the IntDList and returns it.
     * This is an extra challenge problem. */
    public int deleteBack() {
        return 0;   // Your code here

    }

    /** Returns a string representation of the IntDList in the form
     *  [] (empty list) or [1, 2], etc. 
     * This is an extra challenge problem. */
    public String toString() {
        return null;   // Your code here
    }

    /* DNode is a "static nested class", because we're only using it inside
     * IntDList, so there's no need to put it outside (and "pollute the
     * namespace" with it. This is also referred to as encapsulation.
     * Look it up for more information! */
    protected static class DNode {
        protected DNode _prev;
        protected DNode _next;
        protected int _val;

        private DNode(int val) {
            this(null, val, null);
        }

        private DNode(DNode prev, int val, DNode next) {
            _prev = prev;
            _val = val;
            _next = next;
        }
    }

}

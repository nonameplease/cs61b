import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a BST based String Set.
 * @author Scott Shao
 */
public class BSTStringSet implements StringSet {
    /** Creates a new empty set. */
    public BSTStringSet() {
        root = null;
    }

    @Override
    public void put(String s) {
        root = putHelper(s, root);
    }

    /**
     * Helper function for put.
     * @param s A String
     * @param n A Node
     * @return Put String s in the BSTStringSet.
     */
    private Node putHelper(String s, Node n) {
        if (s != null) {
            if (n == null) {
                return new Node(s);
            }
            if (s.compareTo(n.s) < 0) {
                n.left = putHelper(s, n.left);
            }
            if (s.compareTo(n.s) > 0) {
                n.right = putHelper(s, n.right);
            }
        }
        return n;
    }

    @Override
    public boolean contains(String s) {
        return containsHelper(s, root);
    }

    /**
     * Hepler function ofr contains.
     * @param s A String
     * @param n A Node
     * @return A boolean whether String s is contained
     * int this set.
     */
    private boolean containsHelper(String s, Node n) {
        if (n == null) {
            return false;
        }
        if (s != null) {
            if (s.compareTo(n.s) < 0) {
                return containsHelper(s, n.left);
            }
            if (s.compareTo(n.s) > 0) {
                return containsHelper(s, n.right);
            }
        }
        return true;
    }

    @Override
    public List<String> asList() {
        return asListHelper(root);
    }

    /**
     * Helper function for asList.
     * @param n A Node
     * @return A List implementation
     */
    private List<String> asListHelper(Node n) {
        if (n == null) {
            return null;
        }
        List<String> returnValue = new ArrayList<String>();
        returnValue.add(n.s);
        asListHelper(n.left);
        asListHelper(n.right);
        return returnValue;
    }

    /** Represents a single Node of the tree. */
    private static class Node {
        /** String stored in this Node. */
        private String s;
        /** Left child of this Node. */
        private Node left;
        /** Right child of this Node. */
        private Node right;

        /** Creates a Node containing SP. */
        public Node(String sp) {
            s = sp;
        }
    }

    /** Root node of the tree. */
    private Node root;
}

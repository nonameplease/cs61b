import com.sun.xml.internal.xsom.impl.scd.Iterators;
import org.junit.Test;
import static org.junit.Assert.*;


public class TestUnionFind {

    UnionFind Test = new UnionFind(20);

    @Test
    public void TestFind1() {
        assertEquals(1, Test.find(1));
        assertEquals(20, Test.find(20));
    }

    @Test
    public void TestSamePartition1() {
        assertEquals(false, Test.samePartition(1, 20));
    }

    @Test
    public void TestUnion() {
        assertEquals(1, Test.union(1, 20));
        assertEquals(1, Test.union(2, 20));
        assertEquals(1, Test.union(3, 1));
    }

    @Test
    public void TestSamePartition() {
        UnionFind set = new UnionFind(8);
        assertEquals(3, set.union(3, 4));
        assertEquals(1, set.union(1, 2));
        assertEquals(5, set.union(5, 6));
        assertEquals(5, set.union(6, 8));
        assertEquals(1, set.union(2, 5));
        assertEquals(true, set.samePartition(1, 8));
    }
}

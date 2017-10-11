package db61b;

import com.sun.source.tree.AssertTree;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestTable {

    String[] columnTitle = {"First", "Second", "Third"};
    Table table = new Table(columnTitle);
    String[] row1 = {"a", "b", "c"};
    String[] row2 = {"d", "e", "f"};
    String[] row3 = {"A", "B", "C"};
    String[] row4 = {"1", "2", "3"};
    String[] duplicated = {"1", "2", "3"};

    @Test
    public void testColumn() {
        assertEquals(3, table.columns());
    }

    @Test
    public void testGetTitle() {
        assertEquals("First", table.getTitle(0));
        assertEquals("Second", table.getTitle(1));
        try {
            table.getTitle(3);
            fail();
        } catch (DBException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testFindColumn() {
        assertEquals(0, table.findColumn("First"));
        assertEquals(1, table.findColumn("Second"));
        assertEquals(-1, table.findColumn("Fourth"));
    }

    @Test
    public void testAdd() {
        assertEquals(true, table.add(row1));
        assertEquals(true, table.add(row2));
        assertEquals(true, table.add(row3));
        assertEquals(true, table.add(row4));
        assertEquals(false, table.add(duplicated));
    }

    @Test
    public void testSize() {
        assertEquals(0, table.size());
        table.add(row1);
        table.add(row2);
        table.add(row3);
        table.add(row4);
        assertEquals(4, table.size());
        table.add(duplicated);
        assertEquals(4, table.size());
    }

    @Test
    public void testGet() {
        table.add(row1);
        table.add(row2);
        table.add(row3);
        table.add(row4);
        assertEquals("e", table.get(1, 1));
        try {
            table.get(5, 5);
        } catch (DBException e) {
            assertTrue(true);
        }
        assertEquals("C", table.get(2, 2));
    }

    @Test
    public void testAdd2() {
        table.add(row1);
        table.add(row2);
        table.add(row3);
        Column first = new Column("First", table);
        Column second = new Column("Second", table);
        Column third = new Column("Third", table);
        List<Column> columns = new ArrayList<Column>();
        columns.add(first);
        columns.add(second);
        columns.add(third);
        table.add(columns, 0, 1, 2);
        assertEquals("a", table.get(3, 0));
        assertEquals("e", table.get(3, 1));
        assertEquals("C", table.get(3, 2));
        String[] aeC = {"a", "e", "C"};
        assertEquals(false, table.add(aeC));
    }

    @Test
    public void testReadTable() {
        Table table2 = Table.readTable("testing/blank");
        assertEquals("First", table2.getTitle(0));
        assertEquals("Third", table2.getTitle(2));
        Table table3 = Table.readTable("testing/enrolled");
        assertEquals("SID", table3.getTitle(0));
        assertEquals("A", table3.get(4, 2));
    }

    @Test
    public void testWriteTable() {
        
    }

}

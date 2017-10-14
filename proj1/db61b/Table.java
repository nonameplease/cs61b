// This is a SUGGESTED skeleton for a class that represents a single
// Table.  You can throw this away if you want, but it is a good
// idea to try to understand it first.  Our solution changes or adds
// about 100 lines in this skeleton.

// Comments that start with "//" are intended to be removed from your
// solutions.
package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static db61b.Utils.*;

/** A single table in a database.
 *  @author P. N. Hilfinger
 */
class Table {
    /** A new Table whose columns are given by COLUMNTITLES, which may
     *  not contain duplicate names. */
    Table(String[] columnTitles) {
        if (columnTitles.length == 0) {
            throw error("table must have at least one column");
        }
        _size = 0;
        _rowSize = columnTitles.length;

        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                                columnTitles[i]);
                }
            }
        }

        // FIXME
        _titles = columnTitles;
        _columns = new ValueList[_rowSize];
        for (int col = 0; col < _rowSize; col += 1) {
            _columns[col] = new ValueList();
        }
    }

    /** A new Table whose columns are give by COLUMNTITLES. */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    public int columns() {
        return _rowSize;
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    public String getTitle(int k) {
        if (k < 0 || k >= columns()) {
            throw error("column number is out of range of this table");
        }
        return _titles[k];
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    public int findColumn(String title) {
        int numTitle = -1;
        for (int i = 0; i < columns(); i+=1) {
            if (title.compareTo(_titles[i]) == 0) {
                numTitle = i;
            }
        }
        return numTitle;
    }

    /** Return the number of rows in this table. */
    public int size() {
        return _size;
    }

    /** Return the value of column number COL (0 <= COL < columns())
     *  of record number ROW (0 <= ROW < size()). */
    public String get(int row, int col) {
        try {
            return _columns[col].get(row);
        } catch (IndexOutOfBoundsException excp) {
            throw error("invalid row or column");
        }
    }

    /** Add a new row whose column values are VALUES to me if no equal
     *  row already exists.  Return true if anything was added,
     *  false otherwise. */
    public boolean add(String[] values) {
        /*boolean duplicate = false;
        boolean smaller = true;
        int index = 0;
        for (int row = 0; row < _size; row += 1) {
            int numOfDuplicates = 0;
            for (int col = 0; col < _rowSize; col += 1) {
                if (get(row, col).compareTo(values[col]) == 0) {
                    numOfDuplicates += 1;
                }
            }
            if (numOfDuplicates == _rowSize) {
                duplicate = true;
            } else {
                numOfDuplicates = 0;
            }
        }
        for (int row = 0; row < _size; row += 1) {
            for (int col = 0; col < _rowSize; col += 1) {
                if (values[col].compareTo(get(_index.get(row), col)) > 0 && !duplicate) {
                    smaller = false;
                }
            }
            if (smaller == true) {
                break;
            } else {
                index += 1;
                smaller = true;
            }
        }
        if (duplicate) {
            return false;
        } else {
            _size += 1;
            for (int col = 0; col < _rowSize; col += 1) {
                _columns[col].add(values[col]);
            }
            for (int i = 0; i < _index.size(); i += 1) {
                if (_index.get(i) >= index) {
                    _index.set(i, _index.get(i) + 1);
                }
            }
            _index.add(_index.size(), index);
            return true;
        }*/

        /*boolean duplicate = false;
        for (int col = 0; col < columns(); col += 1) {
            _columns[col].add(values[col]);
        }
        _size = _columns[0].size();
        for (int row = 0; row < size(); row += 1) {
            if (_index.size() == 0) {
                _index.add(size() - 1);
            } else {
                _index.add(size() - 1);
                if (compareRows(size() - 1, _index.get(row)) < 0) {
                    _index.add(row, size() - 1);
                    break;
                } else {
                    _index.remove(_index.size() - 1);
                }
            }
            //////////////merge point
        }
        for (int row = 0; row < size() - 1; row += 1) {
            if (compareRows(row, size() - 1) == 0) {
                duplicate = true;
            }
        }
        if (!duplicate) {
            return true;
        } else {
            for (int col = 0; col < columns(); col += 1) {
                _columns[col].remove(_columns.length - 1);
            }
            _index.remove(_index.indexOf(size() - 1));
            _size -= 1;
            return false;
            }*/

        if (size() == 0) {
            for (int col = 0; col < columns(); col += 1) {
                ValueList firstline = new ValueList();
                firstline.add(values[col]);
                _columns[col] = firstline;
            }
            _index.add(0, 0);
            _size += 1;
            return true;
        } else {
            for (int row = 0; row < size(); row += 1) {
                boolean duplicate = true;
                for (int col = 0; col < columns(); col += 1) {
                    if (get(row, col).compareTo(values[col]) != 0) {
                        duplicate = false;
                    }
                }
                if (duplicate) {
                    return false;
                }
            }
            for (int col = 0; col < columns(); col += 1) {
                _columns[col].add(values[col]);
            }
            for (int index = 0; index < _index.size(); index += 1) {
                if (compareRows(_index.get(index), size()) > 0) {
                    _index.add(index, size());
                    _size += 1;
                    return true;
                }
            }
            _index.add(_index.size(), _size);
            _size += 1;
            return true;
        }

    }

    /** Add a new row whose column values are extracted by COLUMNS from
     *  the rows indexed by ROWS, if no equal row already exists.
     *  Return true if anything was added, false otherwise. See
     *  Column.getFrom(Integer...) for a description of how Columns
     *  extract values. */
    public boolean add(List<Column> columns, Integer... rows) {
        String [] values = new String[columns.size()];
        for (int col = 0; col < columns.size(); col += 1) {
            values[col] = columns.get(col).getFrom(rows[col]);
        }
        boolean state = add(values);
        return state;
    }

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            // FILL IN
            table = new Table(columnNames);
            ArrayList<String> rows = new ArrayList<String>();
            String nextLine = input.readLine();
            while (nextLine != null) {
                rows.add(nextLine);
                nextLine = input.readLine();
            }
            Object[] rowsValue = rows.toArray();
            for (int row = 0; row < rows.size(); row += 1) {
                String[] values = rowsValue[row].toString().split(",");
                table.add(values);
            }
            if (columnNames == null) {
                throw error("missing column names in DB file");
            }
        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = "";
            output = new PrintStream(name + ".db");
            ValueList[] content = new ValueList[size() + 1];
            for (int i = 0; i < content.length; i += 1) {
                content[i] = new ValueList();
            }
            for (int col = 0; col < columns(); col += 1) {
                content[0].add(getTitle(col));
            }
            for (int row = 0; row < size(); row += 1) {
                for (int col = 0; col < columns(); col += 1) {
                    content[row + 1].add(get(row, col));
                }
            }
            for (int row = 0; row < size() + 1; row += 1) {
                for (int col = 0; col < content[row].size() - 1; col += 1) {
                    output.print(content[row].get(col));
                    output.print(",");
                }
                output.print(content[row].get(content[row].size() - 1));
                output.println();
            }
        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** Print my contents on the standard output, separated by spaces
     *  and indented by two spaces. */
    void print() {
        for (int row = 0; row < _index.size(); row += 1) {
            System.out.print(" ");
            for (int col = 0; col < columns(); col += 1) {
                System.out.print(" " + get(_index.get(row), col).trim());
            }
            System.out.println();
        }
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected from
     *  rows of this table that satisfy CONDITIONS. */
    Table select(List<String> columnNames, List<Condition> conditions) {
        Table result = new Table(columnNames);
        ArrayList<String> values = new ArrayList<String>();
        for (int row = 0; row < size(); row += 1) {
            for (int colselect = 0; colselect < columnNames.size(); colselect += 1) {
                for (int col = 0; col < columns(); col += 1) {
                    if (getTitle(col).compareTo(columnNames.get(colselect)) == 0) {
                        if (conditions != null) {
                            if (Condition.test(conditions, row)) {
                                values.add(get(row, col));
                            }
                        } else {
                            values.add(get(row, col));
                        }
                    }
                }
            }
            if (values.size() == columnNames.size()) {
                String[] valueString = new String[columnNames.size()];
                for (int i = 0; i < columnNames.size(); i += 1) {
                    valueString[i] = values.get(i);
                }
                result.add(valueString);
            }
            values.clear();
        }
        return result;
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected
     *  from pairs of rows from this table and from TABLE2 that match
     *  on all columns with identical names and satisfy CONDITIONS. */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        //create natural inner joint table
        ValueList column = new ValueList();
        ArrayList<Column> columnList = new ArrayList<Column>();
        ArrayList<Column> columnList2 = new ArrayList<Column>();
        ValueList values = new ValueList();
        ArrayList<ArrayList<Column>> columnGroup = new ArrayList<ArrayList<Column>>();
        ArrayList<ArrayList<Integer>> rowGroup = new ArrayList<ArrayList<Integer>>();
        ArrayList<String> titles = new ArrayList<String>();
        for (int col = 0; col < columns(); col += 1) {
            for (int col2 = 0; col2 < table2.columns(); col2 += 1) {
                if (getTitle(col).compareTo(table2.getTitle(col2)) == 0) {
                    column.add(getTitle(col));
                    titles.add(getTitle(col));
                }
            }
        }

        for (int i = 0; i < column.size(); i += 1) {
            Column column1 = new Column(column.get(i), this);
            Column column2 = new Column(column.get(i), table2);
            columnList.add(column1);
            columnList2.add(column2);

        }

        for (int row1 = 0; row1 < size(); row1 += 1) {
            for (int row2 = 0; row2 < table2.size(); row2 += 1) {
                if (equijoin(columnList, columnList2, row1, row2)) {
                    ArrayList<Column> tempColumn = new ArrayList<Column>();
                    ArrayList<Integer> tempRows = new ArrayList<Integer>();
                    boolean state = false;
                    if (conditions != null) {
                        if (Condition.test(conditions, row1, row2)) {
                            state = true;
                        }
                    } else {
                        state = true;
                    }
                    if (state == true) {
                        for (int col = 0; col < column.size(); col += 1) {
                            tempColumn.add(columnList.get(col));
                            tempRows.add(row1);
                        }
                        for (int col = 0; col < columns(); col += 1) {
                            if (!column.contains(getTitle(col))) {
                                tempColumn.add(new Column(getTitle(col), this));
                                tempRows.add(row1);
                                if (!titles.contains(getTitle(col))) {
                                    titles.add(getTitle(col));
                                }
                            }
                        }
                        for (int col = 0; col < table2.columns(); col += 1) {
                            if (!column.contains(table2.getTitle(col))) {
                                tempColumn.add(new Column(table2.getTitle(col), table2));
                                tempRows.add(row2);
                                if (!titles.contains(table2.getTitle(col))) {
                                    titles.add(table2.getTitle(col));
                                }
                            }
                        }
                        columnGroup.add(tempColumn);
                        rowGroup.add(tempRows);
                    }
                }
            }
        }

        Table table = new Table(titles);
        for (int i = 0; i < rowGroup.size(); i += 1) {
            Integer[] ints = new Integer[rowGroup.get(i).size()];
            for (int j = 0; j < rowGroup.get(i).size(); j += 1) {
                ints[j] = rowGroup.get(i).get(j);
            }
            table.add(columnGroup.get(0),ints);
        }
        Table returnTable = table.select(columnNames, null);
        return returnTable;
    }

    /** Return <0, 0, or >0 depending on whether the row formed from
     *  the elements _columns[0].get(K0), _columns[1].get(K0), ...
     *  is less than, equal to, or greater than that formed from elememts
     *  _columns[0].get(K1), _columns[1].get(K1), ....  This method ignores
     *  the _index. */
    private int compareRows(int k0, int k1) {
        for (int i = 0; i < _columns.length; i += 1) {
            int c = _columns[i].get(k0).compareTo(_columns[i].get(k1));
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    /** Return true if the columns COMMON1 from ROW1 and COMMON2 from
     *  ROW2 all have identical values.  Assumes that COMMON1 and
     *  COMMON2 have the same number of elements and the same names,
     *  that the columns in COMMON1 apply to this table, those in
     *  COMMON2 to another, and that ROW1 and ROW2 are indices, respectively,
     *  into those tables. */
    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    int row1, int row2) {
        for (int col = 0; col < common1.size(); col += 1) {
            if (common1.get(col).getFrom(row1).compareTo(common2.get(col).getFrom(row2)) != 0) {
                return false;
            }
        }
        return true;
    }

    /** A class that is essentially ArrayList<String>.  For technical reasons,
     *  we need to encapsulate ArrayList<String> like this because the
     *  underlying design of Java does not properly distinguish between
     *  different kinds of ArrayList at runtime (e.g., if you have a
     *  variable of type Object that was created from an ArrayList, there is
     *  no way to determine in general whether it is an ArrayList<String>,
     *  ArrayList<Integer>, or ArrayList<Object>).  This leads to annoying
     *  compiler warnings.  The trick of defining a new type avoids this
     *  issue. */
    private static class ValueList extends ArrayList<String> {
    }

    /** My column titles. */
    private final String[] _titles;
    /** My columns. Row i consists of _columns[k].get(i) for all k. */
    private final ValueList[] _columns;

    /** Rows in the database are supposed to be sorted. To do so, we
     *  have a list whose kth element is the index in each column
     *  of the value of that column for the kth row in lexicographic order.
     *  That is, the first row (smallest in lexicographic order)
     *  is at position _index.get(0) in _columns[0], _columns[1], ...
     *  and the kth row in lexicographic order in at position _index.get(k).
     *  When a new row is inserted, insert its index at the appropriate
     *  place in this list.
     *  (Alternatively, we could simply keep each column in the proper order
     *  so that we would not need _index.  But that would mean that inserting
     *  a new row would require rearranging _rowSize lists (each list in
     *  _columns) rather than just one. */
    private final ArrayList<Integer> _index = new ArrayList<>();

    /** My number of rows (redundant, but convenient). */
    private int _size;
    /** My number of columns (redundant, but convenient). */
    private final int _rowSize;
}

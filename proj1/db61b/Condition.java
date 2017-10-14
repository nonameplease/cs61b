package db61b;

import java.util.List;
import static db61b.Utils.*;

/** Represents a single 'where' condition in a 'select' command.
 *  @author Scott Shao */
class Condition {

    /** A Condition representing COL1 RELATION COL2, where COL1 and COL2
     *  are column designators. and RELATION is one of the
     *  strings "<", ">", "<=", ">=", "=", or "!=". */
    Condition(Column col1, String relation, Column col2) {
        _col1 = col1;
        _relation = relation;
        _col2 = col2;
        _val2 = null;
    }

    /** A Condition representing COL1 RELATION 'VAL2', where COL1 is
     *  a column designator, VAL2 is a literal value (without the
     *  quotes), and RELATION is one of the strings "<", ">", "<=",
     *  ">=", "=", or "!=".
     */
    Condition(Column col1, String relation, String val2) {
        this(col1, relation, (Column) null);
        _val2 = val2;
    }

    /** Assuming that ROWS are row indices in the respective tables
     *  from which my columns are selected, returns the result of
     *  performing the test I denote. */
    boolean test(Integer... rows) {
        int result;
        if (_col2 != null) {
            result = _col1.getFrom(rows).compareTo(_col2.getFrom(rows));
        } else {
            result = _col1.getFrom(rows).compareTo(_val2);
        }
        switch (_relation) {
        case "<":
            if (result < 0) {
                return true;
            }
            return false;
        case ">":
            if (result > 0) {
                return true;
            }
            return false;
        case "<=":
            if (result <= 0) {
                return true;
            }
            return false;
        case ">=":
            if (result >= 0) {
                return true;
            }
            return false;
        case "=":
            if (result == 0) {
                return true;
            }
            return false;
        case "!=":
            if (result != 0) {
                return true;
            }
            return false;
        default:
            throw error("unrecognizable command");
        }
    }

    /** Return true iff ROWS satisfies all CONDITIONS. */
    static boolean test(List<Condition> conditions, Integer... rows) {
        for (Condition cond : conditions) {
            if (!cond.test(rows)) {
                return false;
            }
        }
        return true;
    }

    /** The operands of this condition.  _col2 is null if the second operand
     *  is a literal. */
    private Column _col1, _col2;
    /** Second operand, if literal (otherwise null). */
    private String _val2;
    /** The relational string that represent the relations between
     * two variables.*/
    private String _relation;
}

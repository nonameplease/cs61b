package game2048;

import com.sun.tools.javac.code.Attribute;

import java.util.Arrays;
import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author Scott Shao
 */
class Model extends Observable {

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to _board[c][r].  Be careful! This is not the usual 2D matrix
     * numbering, where rows are numbered from the top, and the row
     * number is the *first* index. Rather it works like (x, y) coordinates.
     */

    /** Largest piece value. */
    static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    Model(int size) {
        _board = new Tile[size][size];
        _score = _maxScore = 0;
        _gameOver = false;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there. */
    Tile tile(int col, int row) {
        return _board[col][row];
    }

    /** Return the number of squares on one side of the board. */
    int size() {
        return _board.length;
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    boolean gameOver() {
        return _gameOver;
    }

    /** Return the current score. */
    int score() {
        return _score;
    }

    /** Return the current maximum game score (updated at end of game). */
    int maxScore() {
        return _maxScore;
    }

    /** Clear the board to empty and reset the score. */
    void clear() {
        _score = 0;
        _gameOver = false;
        for (Tile[] column : _board) {
            Arrays.fill(column, null);
        }
        setChanged();
    }

    /** Add TILE to the board.  There must be no Tile currently at the
     *  same position. */
    void addTile(Tile tile) {
        assert _board[tile.col()][tile.row()] == null;
        _board[tile.col()][tile.row()] = tile;
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     * small bug: second combo tile won't merge. e.p.: 2,2,4,4 will only
     * become 2,2,8 instead of 4,8.*/
    boolean tilt(Side side) {
        boolean changed;
        changed = false;
        /**FIXME */
        for (int i = 0; i < size(); i++) {
            boolean merged = false;
            for (int j = size() - 1; j >= 0; j--) {
                if (vtile(i, j, side) != null) {
                    int checker = dist(i, j, side);
                    int distance = distWithMerge(i, j, side, merged);
                    Tile t = vtile(i, j, side);
                    setVtile(i, j + distance, side, t);
                    if (checker != distance) {
                        merged = true;
                        _score += vtile(i, j + distance, side).value();
                    } else {
                        merged = false;
                    }
                    if (distance > 0) {
                        changed = true;
                    }
                }
            }
        }

        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** Return the distance a tile at (COL, ROW, SIDE)
     * can move up without merge. */
    private int dist(int col, int row, Side side) {
        int distance = 0;
        for (int i = size() - 1; i > row; i--) {
            if (vtile(col, i, side) == null) {
                distance += 1;
            }
        }
        return distance;
    }

    /** Return the distance a tile at (COL, ROW, SIDE)
     * can move up with merge. If MERGED, then do not merge again. */
    private int distWithMerge(int col, int row, Side side, boolean merged) {
        int distance = dist(col, row, side);
        if (row >= 0 && row + distance < size() - 1 && merged == false) {
            if (vtile(col, row, side) != null && vtile(col, row + distance + 1, side) != null) {
                if (vtile(col, row, side).value() == vtile(col, row + distance + 1, side).value()) {
                    distance += 1;
                }
            }
        }
        return distance;
    }

    /** Return the current Tile at (COL, ROW), when sitting with the board
     *  oriented so that SIDE is at the top (farthest) from you. */
    private Tile vtile(int col, int row, Side side) {
        return _board[side.col(col, row, size())][side.row(col, row, size())];
    }

    /** Move TILE to (COL, ROW), merging with any tile already there,
     *  where (COL, ROW) is as seen when sitting with the board oriented
     *  so that SIDE is at the top (farthest) from you. */
    private void setVtile(int col, int row, Side side, Tile tile) {
        int pcol = side.col(col, row, size()),
            prow = side.row(col, row, size());
        if (tile.col() == pcol && tile.row() == prow) {
            return;
        }
        Tile tile1 = vtile(col, row, side);
        _board[tile.col()][tile.row()] = null;

        if (tile1 == null) {
            _board[pcol][prow] = tile.move(pcol, prow);
        } else {
            _board[pcol][prow] = tile.merge(pcol, prow, tile1);
        }
    }

    /** Deternmine whether game is over and update _gameOver and _maxScore
     *  accordingly. */
    private void checkGameOver() {
        /**FIXME*/
        int max = 0;
        boolean over = true;
        /** check board is full */
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (tile(i, j) == null) {
                    over = false;
                }
                if (tile(i, j) != null) {
                    if (tile(i, j).value() > max) {
                        max = tile(i, j).value();
                    }
                }
            }
        }
        /** check no valid move left */
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                Side[] side = {Side.NORTH, Side.EAST, Side.SOUTH, Side.WEST};
                for (Side k : side) {
                    int dist = dist(i, j, k);
                    int distMerge = distWithMerge(i, j, k, false);
                    if (dist != distMerge) {
                        over = false;
                    }
                }

            }
        }
        if (over || max == MAX_PIECE) {
            _gameOver = true;
            _maxScore = _score;
        }
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        out.format("] %d (max: %d)", score(), maxScore());
        return out.toString();
    }

    /** Current contents of the board. */
    private Tile[][] _board;
    /** Current score. */
    private int _score;
    /** Maximum score so far.  Updated when game ends. */
    private int _maxScore;
    /** True iff game is ended. */
    private boolean _gameOver;

}

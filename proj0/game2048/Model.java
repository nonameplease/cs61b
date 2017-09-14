package game2048;

import com.sun.tools.javac.code.Attribute;

import java.util.Arrays;
import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author
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

    /** Tilt the board toward SIDE. Return true iff this changes the board. */
    boolean tilt(Side side) {
        boolean changed;
        changed = false;
        /*
        // FIXME
        //problem: when two tile in the middle move and one is merged with one on the side, the other tile with same number does not move.
        for (int i = 0; i < size(); i++) {
            int value = 0;
            boolean top = true;
            for (int j = size() - 1; j >= 0; j--) {
                if (vtile(i, j, side) == null) {
                    int counter = 1;
                    for (int k = j - 1; k >= 0; k--) {
                        if (vtile(i, k, side) != null){
                            if (value != 0 && vtile(i, k, side).value() == value) {
                                setVtile(i, k + counter + 1, side, vtile(i, k, side));
                            } else {
                                setVtile(i, k + counter, side, vtile(i, k, side));
                                value = vtile(i, k + counter, side).value();
                            }
                            changed = true;
                            counter = 1;
                        } else {
                                counter += 1;
                        }
                    }
                } else {
                    if (top == false) {
                        if (value != 0 && vtile(i, j, side).value() == value) {
                            setVtile(i, j + 1, side, vtile(i, j, side));
                        }
                    }
                    if (vtile(i, j, side) != null) {
                        value = vtile(i, j, side).value();
                    } else {
                        value = 0;
                    }
                }
                top = false;
            }
        }
        */

        //move tile
        for (int i = 0; i < size(); i++) {
            for (int j = size() - 1; j >= 0; j--) {
                if (vtile(i, j, side) != null) {
                    int distance = distWithMerge(i, j, side);
                    setVtile(i, j + distance, side, vtile(i, j, side));
                    changed = true;
                }
            }
        }

        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** Return the distance a tile can move up without merge */
    private int dist(int col, int row, Side side) {
        int distance = 0;
        for (int i = size() - 1; i > row; i--) {
            if (vtile(col, i, side) == null) {
                distance += 1;
            }
        }
        return distance;
    }

    /** Return the distance a tile can move up with merge */
    private int distWithMerge(int col, int row, Side side) {
        int distance = dist(col, row, side);
        if (row < size() - 1 && row >= 0) {
            if (vtile(col, row + 1, side) != null) {
                if (vtile(col, row, side).value() == vtile(col, row + 1, side).value()) {
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
        // FIXME
        int max = 0;
        boolean over = true;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (tile(i, j) != null) {
                    over = false;
                    if (tile(i, j).value() > max) {
                        max = tile(i, j).value();
                    }
                }
            }
        }
        _maxScore = max;
        if (over || max == 2048) {
            _gameOver = true;
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

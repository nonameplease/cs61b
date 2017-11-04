package qirkat;

import com.sun.tools.javac.code.Attribute;

import java.util.*;

import static qirkat.PieceColor.*;
import static qirkat.Move.*;

/** A Qirkat board.   The squares are labeled by column (a char value between
 *  'a' and 'e') and row (a char value between '1' and '5'.
 *
 *  For some purposes, it is useful to refer to squares using a single
 *  integer, which we call its "linearized index".  This is simply the
 *  number of the square in row-major order (with row 0 being the bottom row)
 *  counting from 0).
 *
 *  Moves on this board are denoted by Moves.
 *  @author Scott Shao
 */
class Board extends Observable {

    /** A new, cleared board at the start of the game. */
    Board() {
        // FIXME?

        //////////
        _board = defaultBoard;
        //////////
        clear();
    }

    /** A copy of B. */
    Board(Board b) {
        internalCopy(b);
    }

    /** Return a constant view of me (allows any access method, but no
     *  method that modifies it). */
    Board constantView() {
        return this.new ConstantBoard();
    }

    /** Clear me to my starting state, with pieces in their initial
     *  positions. */
    void clear() {
        _whoseMove = WHITE;
        _gameOver = false;

        // FIXME
        //////////
        //System.out.println(defaultBoard);
        //System.out.println(String.valueOf(defaultBoard));
        setPieces(String.valueOf(defaultBoard), _whoseMove);
        //////////

        setChanged();
        notifyObservers();
    }

    /** Copy B into me. */
    void copy(Board b) {
        internalCopy(b);
    }

    /** Copy B into me. */
    private void internalCopy(Board b) {
        // FIXME
        _board = b._board;
    }

    /** Set my contents as defined by STR.  STR consists of 25 characters,
     *  each of which is b, w, or -, optionally interspersed with whitespace.
     *  These give the contents of the Board in row-major order, starting
     *  with the bottom row (row 1) and left column (column a). All squares
     *  are initialized to allow horizontal movement in either direction.
     *  NEXTMOVE indicates whose move it is.
     */
    void setPieces(String str, PieceColor nextMove) {

        if (nextMove == EMPTY || nextMove == null) {
            throw new IllegalArgumentException("bad player color");
        }
        str = str.replaceAll("\\s", "");
        if (!str.matches("[bw-]{25}")) {
            throw new IllegalArgumentException("bad board description");
        }

        // FIXME

        _board = str.toCharArray();

        for (int k = 0; k < str.length(); k += 1) {
            switch (str.charAt(k)) {
            case '-':
                set(k, EMPTY);
                break;
            case 'b': case 'B':
                set(k, BLACK);
                break;
            case 'w': case 'W':
                set(k, WHITE);
                break;
            default:
                break;
            }
        }

        // FIXME

        setChanged();
        notifyObservers();
    }

    /** Return true iff the game is over: i.e., if the current player has
     *  no moves. */
    boolean gameOver() {
        return _gameOver;
    }

    /** Return the current contents of square C R, where 'a' <= C <= 'e',
     *  and '1' <= R <= '5'.  */
    PieceColor get(char c, char r) {
        assert validSquare(c, r);
        return get(index(c, r)); // FIXME?
    }

    /** Return the current contents of the square at linearized index K. */
    PieceColor get(int k) {
        assert validSquare(k);
        //return null; // FIXME
        if (_board[k] == 'b') {
            return BLACK;
        } else if (_board[k] == 'w') {
            return WHITE;
        } else {
            return EMPTY;
        }
    }

    /** Set get(C, R) to V, where 'a' <= C <= 'e', and
     *  '1' <= R <= '5'. */
    private void set(char c, char r, PieceColor v) {
        assert validSquare(c, r);
        set(index(c, r), v);  // FIXME?
    }

    /** Set get(K) to V, where K is the linearized index of a square. */
    private void set(int k, PieceColor v) {
        assert validSquare(k);
        // FIXME
        //_board =  _board.toString().substring(0, ) + v.shortName() + _board.substring(k + 1);
        _board[k] = v.shortName().charAt(0);
    }

    /** Return true iff MOV is legal on the current board. */
    boolean legalMove(Move mov) {
        /**
         * Need to be fixed since it is used to check for legal moves.
         */
        //return false; // FIXME
        /*if (validSquare(mov.col0(), mov.row0()) && validSquare(mov.col1(), mov.row1())) {
            if (get(mov.col0(), mov.row0()) != EMPTY) {
                *//**
                 * Up, Down, Left, Right
                 *//*
                if (Math.abs(mov.col0() - mov.col1()) + Math.abs(mov.row0() - mov.row1()) == 1) {
                    if (get(mov.col1(), mov.row1()) == EMPTY) {
                        *//**
                         * Up, Down
                         *//*
                        if (get(mov.col0(), mov.row0()) == BLACK) {
                            if (mov.row1() - mov.row0() <= 0) {
                                return true;
                            }
                        }

                        if (get(mov.col0(), mov.row0()) == WHITE) {
                            if (mov.row1() - mov.row0() >= 0) {
                                return true;
                            }
                        }

                        *//**
                         * Left, Right
                         *//*
                        if (Math.abs(mov.col0() - mov.col1()) == 1) {
                            return true;
                        }
                    }
                }

                *//**
                 * Still need to inpliment diagonal
                 * This should be a lot easier?
                 *//*
            }
        }
        return false;*/
        //assume all move in mov is on board.
        if (!mov.isJump()) {
            if (get(mov.col0(), mov.row0()).isPiece() && !get(mov.col1(), mov.row1()).isPiece()) {

            }
        }
        return false;
    }

    /** Return a list of all legal moves from the current position. */
    ArrayList<Move> getMoves() {
        ArrayList<Move> result = new ArrayList<>();
        getMoves(result);
        return result;
    }

    /** Add all legal moves from the current position to MOVES. */
    void getMoves(ArrayList<Move> moves) {
        if (gameOver()) {
            return;
        }
        if (jumpPossible()) {
            for (int k = 0; k <= MAX_INDEX; k += 1) {
                getJumps(moves, k);
            }
        } else {
            for (int k = 0; k <= MAX_INDEX; k += 1) {
                getMoves(moves, k);
            }
        }
    }

    /** Add all legal non-capturing moves from the position
     *  with linearized index K to MOVES. */
    private void getMoves(ArrayList<Move> moves, int k) {
        // FIXME

        /**
         * Diagonal
         */
        if (k % 2 == 0) {
            if (get(k) == BLACK) {
                if (validSquare(k - 4) && row(k) - 1 == row(k - 4) && get(k - 4) == EMPTY) {
                    moves.add(move(col(k), row(k), col(k - 4), row(k - 4)));
                }

                if (validSquare(k - 6) && row(k) - 1 == row(k - 6) && get(k - 6) == EMPTY) {
                    moves.add(move(col(k), row(k), col(k - 6), row(k - 6)));
                }
            }

            if (get(k) == WHITE) {
                if (validSquare(k + 4) && row(k) + 1 == row(k + 4) && get(k + 4) == EMPTY) {
                    moves.add(move(col(k), row(k), col(k + 4), row(k + 4)));
                }

                if (validSquare(k + 6) && row(k) + 1 == row(k + 6) && get(k + 6) == EMPTY) {
                    moves.add(move(col(k), row(k), col(k + 6), row(k + 6)));
                }
            }
        }

        /**
         * Left and Right
         */
        if (get(k) == BLACK || get(k) == WHITE) {
            if (validSquare(k - 1) && row(k) == row(k - 1) && get(k - 1) == EMPTY) {
                moves.add(move(col(k), row(k), col(k - 1), row(k)));
            }

            if (validSquare(k + 1) && row(k) == row(k + 1) && get(k + 1) == EMPTY) {
                moves.add(move(col(k), row(k), col(k + 1), row(k)));
            }
        }

        /**
         * Up and Down
         */
        if (get(k) == BLACK) {
            if (validSquare(k - 5) && get(k - 5) == EMPTY) {
                moves.add(move(col(k), row(k), col(k - 5), row(k - 5)));
            }
        }

        if (get(k) == WHITE) {
            if (validSquare(k + 5) && get(k + 5) == EMPTY) {
                moves.add(move(col(k), row(k), col(k + 5), row(k + 5)));
            }
        }

    }

    /** Add all legal captures from the position with linearized index K
     *  to MOVES. */
    private void getJumps(ArrayList<Move> moves, int k) {
        // FIXME


        if (get(k) == BLACK || get(k) == WHITE) {
            /**
             * Left and Right
             */
            if (validSquare(k - 2) && row(k) == row(k - 2) && get(k - 1) == get(k).opposite() && get(k - 2) == EMPTY) {
                moves.add(move(col(k), row(k), col(k - 2), row(k - 2)));
            }

            if (validSquare(k + 2) && row(k) == row(k + 2) && get(k + 1) == get(k).opposite() && get(k + 2) == EMPTY) {
                moves.add(move(col(k), row(k), col(k + 2), row(k + 2)));
            }

            /**
             * Up and Down
             */
            if (validSquare(k - 10) && get(k - 5) == get(k).opposite() && get(k - 10) == EMPTY) {
                moves.add(move(col(k), row(k), col(k - 10), row(k - 10)));
            }

            if (validSquare(k + 10) && get(k + 5) == get(k).opposite() && get(k + 10) == EMPTY) {
                moves.add(move(col(k), row(k), col(k + 10), row(k + 10)));
            }

            /**
             * Diagonal
             */
            if (k % 2 == 0) {
                if (validSquare(k - 8) && row(k) - 2 == row(k - 8) && get(k - 4) == get(k).opposite() && get(k - 8) == EMPTY) {
                    moves.add(move(col(k), row(k), col(k - 8), row(k - 8)));
                }

                if (validSquare(k - 12) && row(k) - 2 == row(k - 12) && get(k - 6) == get(k).opposite() && get(k - 12) == EMPTY) {
                    moves.add(move(col(k), row(k), col(k - 12), row(k - 12)));
                }

                if (validSquare(k + 8) && row(k) - 2 == row(k + 8) && get(k + 4) == get(k).opposite() && get(k + 8) == EMPTY) {
                    moves.add(move(col(k), row(k), col(k + 8), row(k + 8)));
                }

                if (validSquare(k + 12) && row(k) + 2 == row(k + 12) && get(k + 6) == get(k).opposite() && get(k + 12) == EMPTY) {
                    moves.add(move(col(k), row(k), col(k + 12), row(k + 12)));
                }
            }
        }
    }

    /** Return true iff MOV is a valid jump sequence on the current board.
     *  MOV must be a jump or null.  If ALLOWPARTIAL, allow jumps that
     *  could be continued and are valid as far as they go.  */
    boolean checkJump(Move mov, boolean allowPartial) {
        if (mov == null) {
            return true;
        }
        return false; // FIXME
    }

    /** Return true iff a jump is possible for a piece at position C R. */
    boolean jumpPossible(char c, char r) {
        return jumpPossible(index(c, r));
    }

    /** Return true iff a jump is possible for a piece at position with
     *  linearized index K. */
    boolean jumpPossible(int k) {
        return false; // FIXME
    }

    /** Return true iff a jump is possible from the current board. */
    boolean jumpPossible() {
        for (int k = 0; k <= MAX_INDEX; k += 1) {
            if (jumpPossible(k)) {
                return true;
            }
        }
        return false;
    }

    /** Return the color of the player who has the next move.  The
     *  value is arbitrary if gameOver(). */
    PieceColor whoseMove() {
        return _whoseMove;
    }

    /** Perform the move C0R0-C1R1, or pass if C0 is '-'.  For moves
     *  other than pass, assumes that legalMove(C0, R0, C1, R1). */
    void makeMove(char c0, char r0, char c1, char r1) {
        makeMove(Move.move(c0, r0, c1, r1, null));
    }

    /** Make the multi-jump C0 R0-C1 R1..., where NEXT is C1R1....
     *  Assumes the result is legal. */
    void makeMove(char c0, char r0, char c1, char r1, Move next) {
        makeMove(Move.move(c0, r0, c1, r1, next));
    }

    /** Make the Move MOV on this Board, assuming it is legal. */
    void makeMove(Move mov) {
        assert legalMove(mov);

        // FIXME
        set(mov.col1(), mov.row1(), get(mov.col0(), mov.row0()));
        set(mov.col0(), mov.row0(), EMPTY);

        setChanged();
        notifyObservers();
    }

    /** Undo the last move, if any. */
    void undo() {
        // FIXME

        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return toString(false);
    }

    /** Return a text depiction of the board.  If LEGEND, supply row and
     *  column numbers around the edges. */
    String toString(boolean legend) {
        Formatter out = new Formatter();
        // FIXME
        if (legend == false) {
            for (int i = 0; i < 25; i += 5) {
                out.format(" ");
                for (int j = 0; j < 5; j +=1) {
                    out.format("%2s", String.valueOf(_board[i + j]));
                }
                if (i < 20) {
                    out.format("\n");
                }
            }
        }
        return out.toString();
    }

    /** Return true iff there is a move for the current player. */
    private boolean isMove() {
        return false;  // FIXME
    }


    /** Player that is on move. */
    private PieceColor _whoseMove;

    /** Set true when game ends. */
    private boolean _gameOver;

    /** Convenience value giving values of pieces at each ordinal position. */
    static final PieceColor[] PIECE_VALUES = PieceColor.values();

    /** One cannot create arrays of ArrayList<Move>, so we introduce
     *  a specialized private list type for this purpose. */
    private static class MoveList extends ArrayList<Move> {
    }

    //////////

    String defaultPattern = "  b b b b b\n  b b b b b\n  b b - w w\n  w w w w w\n  w w w w w";
    private char[] defaultBoard = defaultPattern.toCharArray();
           // "  b b b b b\n  b b b b b\n  b b - w w\n  w w w w w\n  w w w w w";

    private char[] _board;

    //////////

    /** A read-only view of a Board. */
    private class ConstantBoard extends Board implements Observer {
        /** A constant view of this Board. */
        ConstantBoard() {
            super(Board.this);
            Board.this.addObserver(this);
        }

        @Override
        void copy(Board b) {
            assert false;
        }

        @Override
        void clear() {
            assert false;
        }

        @Override
        void makeMove(Move move) {
            assert false;
        }

        /** Undo the last move. */
        @Override
        void undo() {
            assert false;
        }

        @Override
        public void update(Observable obs, Object arg) {
            super.copy((Board) obs);
            setChanged();
            notifyObservers(arg);
        }
    }
}

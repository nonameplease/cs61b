package qirkat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Observable;
import java.util.Observer;
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
        int boardSize = _boardSize;
        _board = new char[boardSize];
        _allMoves = new MoveList();
        _previous = new int[boardSize];
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
        _allMoves = new MoveList();
        for (int i = 0; i < _boardSize; i += 1) {
            _previous[i] = -1;
        }
        setPieces(String.valueOf(defaultBoard), _whoseMove);

        setChanged();
        notifyObservers();
    }

    /** Copy B into me. */
    void copy(Board b) {
        internalCopy(b);
    }

    /** Copy B into me. */
    private void internalCopy(Board b) {
        this._board = new char[_boardSize];
        this._previous = new int[_boardSize];
        this._allMoves = new MoveList();
        this._whoseMove = b.whoseMove();
        this.setPieces(b.toString(), whoseMove());
        this._gameOver = b.gameOver();
        this._allMoves = b._allMoves;
        for (int i = 0; i < _boardSize; i += 1) {
            _previous[i] = b._previous[i];
        }

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
        for (int k = 0; k < str.length(); k += 1) {

            int destk = 0;
            if (k >= 0 && k < _rowlength) {
                destk = k + _rowlength * 4;
            } else if (k >= _rowlength && k < _rowlength * 2) {
                destk = k + _rowlength * 2;
            } else if (k >= _rowlength * 2 && k < _rowlength * 3) {
                destk = k;
            } else if (k >= _rowlength * 2 && k < _rowlength * 4) {
                destk = k - _rowlength * 2;
            } else if (k >= _rowlength * 4 && k < _boardSize) {
                destk = k - _rowlength * 4;
            }

            switch (str.charAt(k)) {
            case '-':
                set(destk, EMPTY);
                break;
            case 'b': case 'B':
                set(destk, BLACK);
                break;
            case 'w': case 'W':
                set(destk, WHITE);
                break;
            default:
                break;
            }
        }
        _whoseMove = nextMove;

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
        return get(index(c, r));
    }

    /** Return the current contents of the square at linearized index K. */
    PieceColor get(int k) {
        assert validSquare(k);
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
        set(index(c, r), v);
    }

    /** Set get(K) to V, where K is the linearized index of a square. */
    private void set(int k, PieceColor v) {
        assert validSquare(k);
        _board[k] = v.shortName().charAt(0);
    }

    /** Return true iff MOV is legal on the current board. */
    boolean legalMove(Move mov) {
        /**
         * Need to be fixed since it is used to check for legal moves.
         */
        ArrayList<Move> legalMoves = getMoves();
        if (legalMoves.isEmpty()) {
            _gameOver = true;
        }
        if (mov == null) {
            return false;
        }
        return legalMoves.contains(mov);
    }

    /** Return a list of all legal moves from the current position. */
    ArrayList<Move> getMoves() {
        ArrayList<Move> result = new ArrayList<>();
        getMoves(result);
        if (result.isEmpty()) {
            _gameOver = true;
        }
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
        if (get(k) != _whoseMove) {
            return;
        }
        ArrayList<Integer> possible = new ArrayList<Integer>();
        possibleStraightMove(k, possible);
        possibleDiagonalMove(k, possible);
        for (int i = 0; i < possible.size(); i += 1) {
            if (get(k) == BLACK) {
                int destk = possible.get(i);
                if (userRow(k) >= userRow(destk)) {
                    if (get(possible.get(i)) == EMPTY
                            && _previous[k] != destk && k >= _rowlength) {
                        moves.add(move(col(k), row(k), col(destk), row(destk)));
                    }
                }
            } else if (get(k) == WHITE) {
                int destk = possible.get(i);
                if (userRow(k) <= userRow(destk)) {
                    if (get(possible.get(i)) == EMPTY
                            && _previous[k] != destk && k < _rowlength * 4) {
                        moves.add(move(col(k), row(k), col(destk), row(destk)));
                    }
                }
            }
        }
    }

    /** Add all legal captures from the position with linearized index K
     *  to MOVES. */
    public void getJumps(ArrayList<Move> moves, int k) {
        if (get(k) != whoseMove()) {
            return;
        }
        Board tempBoard = new Board(this);
        if (get(k) == whoseMove()) {
            getJumpsHelper(moves, k, tempBoard, null);
        }
    }

    /**
     * Method to get all possible one step jumps.
     * @param moves An ArrayList contains all the possible jumps.
     * @param k Linearized index k.
     */
    public void getOneJumps(ArrayList<Move> moves, int k) {
        ArrayList<Integer> possible = new ArrayList<Integer>();
        possibleStraightJump(k, possible);
        possibleDiagonalJump(k, possible);
        for (int i = 0; i < possible.size(); i += 1) {
            if (get(k) != EMPTY) {
                int destk = possible.get(i);
                if (get(destk) == EMPTY) {
                    int jumpedcol = (userCol(k) + userCol(destk)) / 2;
                    int jumpedrow = (userRow(k) + userRow(destk)) / 2;
                    int jumpedk = userLinearize(jumpedcol, jumpedrow);
                    if (get(k).opposite().equals(get(jumpedk))) {
                        moves.add(move(col(k), row(k), col(destk), row(destk)));
                    }
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
        Move mov2 = move(mov, null);
        ArrayList<Move> allMoves = new ArrayList<Move>();
        getJumps(allMoves, mov2.fromIndex());
        if (!allowPartial) {
            for (Move move : allMoves) {
                if (mov2.toString().equals(move.toString())) {
                    return true;
                }
            }
        } else {
            for (Move move : allMoves) {
                if (mov2.toString().substring(0, 6).
                        equals(move.toString().substring(0, 6))) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Return true iff a jump is possible for a piece at position C R. */
    boolean jumpPossible(char c, char r) {
        return jumpPossible(index(c, r));
    }

    /** Return true iff a jump is possible for a piece at position with
     *  linearized index K. */
    boolean jumpPossible(int k) {
        if (get(k) != whoseMove()) {
            return false;
        }
        ArrayList<Move> moves = new ArrayList<Move>();
        //getJumps(moves, k);
        getOneJumps(moves, k);
        return !moves.isEmpty();
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
        if (legalMove(mov)) {
        //if (mov != null) {
            Move mov2 = mov;
            _allMoves.add(mov2);
            while (mov2 != null) {
                if (mov2.isJump()) {
                    set(mov2.col1(), mov2.row1(),
                            get(mov2.col0(), mov2.row0()));
                    set(mov2.jumpedCol(), mov2.jumpedRow(), EMPTY);
                    set(mov2.col0(), mov2.row0(), EMPTY);
                    _previous[index(mov2.col0(), mov2.row0())] = -1;
                } else {
                    set(mov2.col1(), mov2.row1(),
                            get(mov2.col0(), mov2.row0()));
                    set(mov2.col0(), mov2.row0(), EMPTY);
                    _previous[index(mov2.col1(), mov2.row1())] =
                            index(mov2.col0(), mov2.row0());
                }
                mov2 = mov2.jumpTail();
            }
            _whoseMove = _whoseMove.opposite();
        }

        setChanged();
        notifyObservers();
    }

    /** Undo the last move, if any. */
    void undo() {
        ArrayList<Move> newAllMove = new ArrayList<Move>(_allMoves);
        newAllMove.remove(_allMoves.size() - 1);
        clear();
        for (Move moves : newAllMove) {
            makeMove(moves);
        }


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
        int cutoff = SIDE * (SIDE - 1);
        if (!legend) {
            for (int i = cutoff; i >= 0; i -= 5) {
                out.format(" ");
                for (int j = 0; j < 5; j += 1) {
                    out.format("%2s", String.valueOf(_board[i + j]));
                }
                if (i > 4) {
                    out.format("\n");
                }
            }
        }
        return out.toString();
    }

    /** Return true iff there is a move for the current player. */
    private boolean isMove() {
        ArrayList<Move> allMoves = new ArrayList<Move>();
        for (int k = 0; k <= MAX_INDEX; k += 1) {
            if (get(k).equals(whoseMove())) {
                getMoves(allMoves, k);
            }
        }
        if (allMoves.size() > 0) {
            return true;
        }
        return false;
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

    /**
     * The pattern of defaultPatter.
     */
    private String defaultPattern =
            "  b b b b b\n  b b b b b\n  b b - w w\n  w w w w w\n  w w w w w";

    /**
     * Default board ordered in String order.
     */
    private char[] defaultBoard = defaultPattern.toCharArray();

    /**
     * The pattern of linearizedPatter.
     */
    private String linearizedPattern = "wwwww wwwww bb-ww bbbbb bbbbb";

    /**
     * Default board ordered in linearized order.
     */
    private char[] linearizedBoard = linearizedPattern.toCharArray();

    /**
     * Current board.
     */
    private char[] _board;

    /**
     * Size of the board.
     */
    private int _boardSize = SIDE * SIDE;

    /**
     * Length of one row.
     */
    private int _rowlength = SIDE;

    /**
     * Previous occupied index for each index.
     */
    private int[] _previous;

    /**
     * A MoveList contains all move so far.
     */
    private MoveList _allMoves;

    /**
     * ArrayList dictionary.
     * 0 -> Up = 5.
     * 1 -> Down = -5.
     * 2 -> Left = -1.
     * 3 -> Right = 1.
     * 4 -> UpLeft = 4.
     * 5 -> UpRight = 6.
     * 6 -> DownLeft = -6.
     * 7 -> DownRight = -4.
     */
    private ArrayList<Integer> direction =
            new ArrayList<Integer>(Arrays.asList(5, -5, -1, 1, 4, 6, -6, -4));

    /**
     * A integer array representing direction of addition.
     * Even for column, odd for row.
     * Up -> Right -> Down -> Left ->.
     * UpRight -> DownRight -> DownLeft -> UpLeft.
     */
    private Integer[] toThat =
            new Integer[] {0, 1, 1, 0, 0, -1, -1, 0,
                1, 1, 1, -1, -1, -1, -1, 1};

    /**
     * @param k Linearized index.
     * @return column assuming on board.
     */
    private int userCol(int k) {
        return k % 5;
    }

    /**
     * @param k Linearized index.
     * @return row assuming on board.
     */
    private int userRow(int k) {
        return k / 5;
    }

    /**
     * @param col index.
     * @param row index.
     * @return Linearized index k.
     */
    private int userLinearize(int col, int row) {
        return row * 5 + col;
    }

    /**
     * Put all linearized index destination that is ON board in a ArrayList.
     * @param k linearized index.
     * @param possible ArrayList contains the linearized destination.
     */
    public void possibleStraightMove(int k, ArrayList<Integer> possible) {
        int col = userCol(k);
        int row = userRow(k);
        for (int i = 0; i < 8; i += 2) {
            int destcol = col + toThat[i];
            int destrow = row + toThat[i + 1];
            if (destcol >= 0 && destcol <= 4 && destrow >= 0 && destrow <= 4) {
                int destk = userLinearize(destcol, destrow);
                possible.add(destk);
            }
        }
    }

    /**
     * Put all linearized index destination that is ON board in a ArrayList.
     * @param k linearized index.
     * @param possible ArrayList contains the linearized destination.
     */
    public void possibleDiagonalMove(int k, ArrayList<Integer> possible) {
        int col = userCol(k);
        int row = userRow(k);
        for (int i = 8; i < 16; i += 2) {
            int destcol = col + toThat[i];
            int destrow = row + toThat[i + 1];
            if (k % 2 == 0) {
                if (destcol >= 0 && destcol <= 4
                        && destrow >= 0 && destrow <= 4) {
                    int destk = userLinearize(destcol, destrow);
                    possible.add(destk);
                }
            }
        }
    }

    /**
     * Put all linearized index destination that is ON board in a ArrayList.
     * @param k linearized index.
     * @param possible ArrayList contains the linearized destination.
     */
    public void possibleStraightJump(int k, ArrayList<Integer> possible) {
        int col = userCol(k);
        int row = userRow(k);
        for (int i = 0; i < 8; i += 2) {
            int destcol = col + toThat[i] * 2;
            int destrow = row + toThat[i + 1] * 2;
            if (destcol >= 0 && destcol <= 4 && destrow >= 0 && destrow <= 4) {
                int destk = userLinearize(destcol, destrow);
                possible.add(destk);
            }
        }
    }

    /**
     * Put all linearized index destination that is ON board in a ArrayList.
     * @param k linearized index.
     * @param possible ArrayList contains the linearized destination.
     */
    public void possibleDiagonalJump(int k, ArrayList<Integer> possible) {
        int col = userCol(k);
        int row = userRow(k);
        for (int i = 8; i < 16; i += 2) {
            int destcol = col + toThat[i] * 2;
            int destrow = row + toThat[i + 1] * 2;
            if (k % 2 == 0) {
                if (destcol >= 0 && destcol <= 4
                        && destrow >= 0 && destrow <= 4) {
                    int destk = userLinearize(destcol, destrow);
                    possible.add(destk);
                }
            }
        }
    }

    /**
     * A helper method for getJumps.
     * @param moves A ArrayList to contains all moves.
     * @param k Linearized index.
     * @param tempBoard A Board to hold current board state.
     * @param lastMove All the moves so far.
     */
    private void getJumpsHelper(ArrayList<Move> moves, int k,
                                Board tempBoard, Move lastMove) {
        boolean hasNextJump = false;
        ArrayList<Integer> possible = new ArrayList<Integer>();
        possibleStraightJump(k, possible);
        possibleDiagonalJump(k, possible);
        for (int i = 0; i < possible.size(); i += 1) {
            Board temp = new Board(tempBoard);
            if (temp.get(k) != EMPTY) {
                int destk = possible.get(i);
                if (temp.get(destk) == EMPTY) {
                    int jumpedcol = (userCol(k) + userCol(destk)) / 2;
                    int jumpedrow = (userRow(k) + userRow(destk)) / 2;
                    int jumpedk = userLinearize(jumpedcol, jumpedrow);
                    if (temp.get(k).opposite().equals(temp.get(jumpedk))) {
                        hasNextJump = true;
                        _previous[k] = -1;
                        temp.set(jumpedk, EMPTY);
                        temp.set(destk, temp.get(k));
                        temp.set(k, EMPTY);
                        getJumpsHelper(moves, destk, temp,
                                move(lastMove,
                                        move(col(k), row(k),
                                                col(destk), row(destk))));
                    }
                }
            }
        }
        if (!hasNextJump && lastMove != null) {
            moves.add(lastMove);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Board) {
            final Board other = (Board) obj;
            if (whoseMove() == other.whoseMove() && gameOver() == other.gameOver()) {
                for (int k = 0; k < _board.length; k += 1) {
                    if (get(k) != other.get(k)) {
                        return false;
                    }
                    if (_previous[k] != other._previous[k]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

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

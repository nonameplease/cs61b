package qirkat;

import java.util.ArrayList;

import static qirkat.PieceColor.*;

/** A Player that computes its own moves.
 *  @author Scott Shao
 */
class AI extends Player {

    /** Maximum minimax search depth before going to static evaluation. */
    private static final int MAX_DEPTH = 8;
    /** A position magnitude indicating a win (for white if positive, black
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI for GAME that will play MYCOLOR. */
    AI(Game game, PieceColor myColor) {
        super(game, myColor);
    }

    @Override
    Move myMove() {
        Main.startTiming();
        Move move = findMove();
        Main.endTiming();

        // FIXME
        System.out.println(myColor() + " " + "moves" + " " + move);
        //System.out.println(game().board().toString());
        //System.out.println(move.jumpTail());
        return move;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        //System.out.println(board().toString());
        if (myColor() == WHITE) {
            findMove(b, MAX_DEPTH, true, 1, -INFTY, INFTY);
        } else {
            findMove(b, MAX_DEPTH, true, -1, -INFTY, INFTY);
        }
        //System.out.println(board().toString());
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        Move best;
        best = null;
        //System.out.println("depth: " + depth + "\n" + "AI board: " + "\n" + board);

        if (depth == 0 || board.getMoves() == null) {
            return staticScore(board);
        }

        if (sense == 1) {
            int v = -INFTY;
            for (Move move : board.getMoves()) {
                Board tempBoard = new Board(board);
                tempBoard.makeMove(move);
                v = Math.max(v, findMove(tempBoard, depth - 1, saveMove, sense * -1, alpha, beta));
                alpha = Math.max(alpha, v);
                if (beta <= alpha) {
                    best = move;
                    break;
                }
            }
            if (saveMove) {
                _lastFoundMove = best;
            }
            return v;
        } else {
            int v = INFTY;
            for (Move move : board.getMoves()) {
                Board tempBoard = new Board(board);
                tempBoard.makeMove(move);
                v = Math.min(v, findMove(tempBoard, depth - 1, saveMove, sense * -1, alpha, beta));
                beta = Math.min(beta, v);
                if (beta <= alpha) {
                    best = move;
                    break;
                }
            }
            if (saveMove) {
                _lastFoundMove = best;
            }
            return v;
        }
        // FIXME

        /*if (saveMove) {
            _lastFoundMove = best;
        }*/

        //return 0; // FIXME
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        //return 0; // FIXME
        if (board.getMoves().size() == 0) {
            return WINNING_VALUE;
        } else {
            int returnValue = 0;
            for (Move move : board.getMoves()) {
                if (move != null) {
                    if (board.get(move.fromIndex()) == myColor()) {
                        if (move.toString().length() > returnValue) {
                            returnValue = move.toString().length();
                        }
                    }
                }
            }
            return returnValue;
        }
    }
}

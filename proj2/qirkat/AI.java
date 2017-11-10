package qirkat;

import java.util.ArrayList;

import static qirkat.PieceColor.*;

/** A Player that computes its own moves.
 *  @author Scott Shao
 */
class AI extends Player {

    /** Maximum minimax search depth before going to static evaluation. */
    private static final int MAX_DEPTH = 5;
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

        if (move == null) {
            return null;
        } else {


            System.out.println(myColor() + " " + "moves" + " "
                    + move.toString() + ".");
            return move;
        }
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        if (myColor() == WHITE) {
            findMove(b, MAX_DEPTH, true, 1, -INFTY, INFTY);
        } else {
            findMove(b, MAX_DEPTH, true, -1, -INFTY, INFTY);
        }
        ArrayList<Move> finalMove = b.getMoves();
        if (_lastFoundMove == null) {
            if (finalMove.isEmpty()) {
                return null;
            }
            return finalMove.get(0);
        } else {
            return _lastFoundMove;
        }
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
        ArrayList<Move> possibleMoves = board.getMoves();

        if (depth == 0 || possibleMoves.isEmpty()) {
            return staticScore(board);
        }

        if (sense == 1) {
            int v = -INFTY;
            for (Move move : possibleMoves) {
                Board tempBoard = new Board(board);
                tempBoard.makeMove(move);
                v = Math.max(v, findMove(tempBoard, depth - 1,
                        saveMove, sense * -1,
                        alpha, beta));
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
            for (Move move : possibleMoves) {
                Board tempBoard = new Board(board);
                tempBoard.makeMove(move);
                v = Math.min(v, findMove
                        (tempBoard, depth - 1,
                                saveMove, sense * -1,
                                alpha, beta));
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
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        ArrayList<Move> allMoves = board.getMoves();
        if (allMoves.size() == 0) {
            return WINNING_VALUE;
        } else {
            int returnValue = 0;
            for (Move move : allMoves) {
                if (move != null) {
                    if (board.get(move.fromIndex()) == myColor()) {
                        if (move.toString().length() > returnValue) {
                            if (game().board().whoseMove() == BLACK) {
                                if (move.toString().charAt
                                        (move.toString().length() - 1)
                                        == '1') {
                                    return 100;
                                } else {
                                    if (move.toString().charAt
                                            (move.toString().length() - 1)
                                            == '5') {
                                        return 100;
                                    }
                                }
                            }
                            returnValue = move.toString().length();
                        }
                    }
                }
            }
            return returnValue;
        }
    }
}

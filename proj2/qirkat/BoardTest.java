package qirkat;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/** Tests of the Board class.
 *  @author
 */
public class BoardTest {

    private static final String INIT_BOARD =
        "  b b b b b\n  b b b b b\n  b b - w w\n  w w w w w\n  w w w w w";

    private static final String[] GAME1 =
    { "c2-c3", "c4-c2",
      "c1-c3", "a3-c1",
      "c3-a3", "c5-c4",
      "a3-c5-c3",
    };

    private static final String GAME1_BOARD =
        "  b b - b b\n  b - - b b\n  - - w w w\n  w - - w w\n  w w b w w";

    private static void makeMoves(Board b, String[] moves) {
        for (String s : moves) {
            b.makeMove(Move.parseMove(s));
        }
    }

    @Test
    public void testInit1() {
        Board b0 = new Board();
        assertEquals(INIT_BOARD, b0.toString());
    }

    @Test
    public void testMoves1() {
        Board b0 = new Board();
        makeMoves(b0, GAME1);
        assertEquals(GAME1_BOARD, b0.toString());
    }

    @Test
    public void testUndo() {
        Board b0 = new Board();
        Board b1 = new Board(b0);
        makeMoves(b0, GAME1);
        Board b2 = new Board(b0);
        for (int i = 0; i < GAME1.length; i += 1) {
            b0.undo();
        }
        assertEquals("failed to return to start", b1, b0);
        makeMoves(b0, GAME1);
        assertEquals("second pass failed to reach same position", b2, b0);
    }



    //////////////////////////////////////////////////////
    @Test
    public void testPossibleStraightMove() {
        Board testBoard = new Board();
        ArrayList<Integer> testingBuffer = new ArrayList<Integer>();
        ArrayList<Integer> test0 = new ArrayList<Integer>();
        test0.add(5);
        test0.add(1);
        testBoard.PossibleStraightMove(0, testingBuffer);
        assertEquals(test0, testingBuffer);
        testingBuffer.clear();
        ArrayList<Integer> test2 = new ArrayList<Integer>();
        test2.add(7);
        test2.add(3);
        test2.add(1);
        testBoard.PossibleStraightMove(2, testingBuffer);
        assertEquals(test2, testingBuffer);
        test2.add(8);
        test2.add(6);
        /**
         * Testing PossibleDiagonalMove and concatenation of ArrayList.
         */
        testBoard.PossibleDiagonalMove(2, testingBuffer);
        assertEquals(test2, testingBuffer);
        testingBuffer.clear();
    }

    @Test
    public void testPossibleStraightJump() {
        Board testBoard = new Board();
        ArrayList<Integer> testingBuffer = new ArrayList<Integer>();
        ArrayList<Integer> test0 = new ArrayList<Integer>();
        test0.add(10);
        test0.add(2);
        testBoard.PossibleStraightJump(0, testingBuffer);
        assertEquals(test0, testingBuffer);
        testingBuffer.clear();
        ArrayList<Integer> test10 = new ArrayList<Integer>();
        test10.add(20);
        test10.add(12);
        test10.add(0);
        testBoard.PossibleStraightJump(10, testingBuffer);
        assertEquals(test10, testingBuffer);
        test10.add(22);
        test10.add(2);
        /**
         * Testing PossibleDiagonalJump and concatenation of ArrayList.
         */
        testBoard.PossibleDiagonalJump(10, testingBuffer);
        assertEquals(test10, testingBuffer);
        testingBuffer.clear();
    }

    @Test
    public void testSetPieces() {
        /**
         * Did not test PieceColor.
         */
        Board b0 = new Board();
        Board b1 = new Board(b0);
        b0.setPieces(GAME1_BOARD, PieceColor.BLACK);
        assertEquals(GAME1_BOARD, b0.toString());
        System.out.println(b0.toString());
        assertEquals(PieceColor.WHITE, b0.get('a', '1'));
    }

    @Test
    public void testGetMove() {

    }
    //////////////////////////////////////////////////////

}

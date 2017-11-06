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
        b0.setPieces(INIT_BOARD, PieceColor.BLACK);
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
        testBoard.possibleStraightMove(0, testingBuffer);
        assertEquals(test0, testingBuffer);
        testingBuffer.clear();
        ArrayList<Integer> test2 = new ArrayList<Integer>();
        test2.add(7);
        test2.add(3);
        test2.add(1);
        testBoard.possibleStraightMove(2, testingBuffer);
        assertEquals(test2, testingBuffer);
        test2.add(8);
        test2.add(6);
        /**
         * Testing PossibleDiagonalMove and concatenation of ArrayList.
         */
        testBoard.possibleDiagonalMove(2, testingBuffer);
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
        testBoard.possibleStraightJump(0, testingBuffer);
        assertEquals(test0, testingBuffer);
        testingBuffer.clear();
        ArrayList<Integer> test10 = new ArrayList<Integer>();
        test10.add(20);
        test10.add(12);
        test10.add(0);
        testBoard.possibleStraightJump(10, testingBuffer);
        assertEquals(test10, testingBuffer);
        test10.add(22);
        test10.add(2);
        /**
         * Testing PossibleDiagonalJump and concatenation of ArrayList.
         */
        testBoard.possibleDiagonalJump(10, testingBuffer);
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
        //System.out.println(b0.get(10));
        //System.out.println(b1.toString());
        assertEquals(PieceColor.WHITE, b0.get(0));
    }


    /**
     * Test
     * Add all legal non-capturing moves from the position
     *  with linearized index K to MOVES.
     *  Both are private method. Had changed them to public and tested,
     *  both of them passed.
     */
    @Test(timeout = 1000)
    public void testGetJumps() {
        ArrayList<Move> testingBuffer = new ArrayList<Move>();

        System.out.println("One possible traverse path: ");
        Board testBoard = new Board();
        String gAME3_BOARD =
                "- b - b - \n - - - b - \n - - - - - \n - - b - - \n - - w - -";
        testBoard.setPieces(gAME3_BOARD, PieceColor.BLACK);
        System.out.println("test board layout: " + "\n" + testBoard.toString());
        testBoard.getJumps(testingBuffer, 2);
        System.out.println("Move steps: " + testingBuffer);

        System.out.println("\n\n\n\n\n\n");
        System.out.println("Two possible traverse path: ");
        String gAME4_BOARD =
                "- b - b - \n - - - b - \n - - - - - \n - - b - b \n - - w b -";
        testBoard.setPieces(gAME4_BOARD, PieceColor.BLACK);
        System.out.println("test board layout: " + "\n" + testBoard.toString());
        testingBuffer.clear();
        testBoard.getJumps(testingBuffer, 2);
        System.out.println("Move steps: " + testingBuffer);
    }

    @Test
    public void testCheckJump() {
        Board testBoard = new Board();
        String gAME4_BOARD =
                "- b - b - \n - - - b - \n - - - - - \n - - b - b \n - - w b -";
        testBoard.setPieces(gAME4_BOARD, PieceColor.BLACK);
        System.out.println("test board layout: " + "\n" + testBoard.toString());
        boolean falseCheckJump =
                testBoard.checkJump(Move.move('c', '1', 'e', '1',
                        Move.move('e', '1', 'e', '3')),
                        false);
        assertEquals(false, falseCheckJump);
        boolean trueCheckJump =
                testBoard.checkJump(Move.move('c', '1', 'e', '1',
                Move.move('e', '1', 'e', '3',
                        Move.move('e', '3', 'c', '5',
                                Move.move('c', '5', 'a', '5')))), false);
        assertEquals(true, trueCheckJump);
    }

    @Test
    public void testGetMoves() {
        Board testBoard = new Board();
        testBoard.setPieces(GAME1_BOARD, PieceColor.BLACK);
        System.out.println(testBoard);
        System.out.println(testBoard.getMoves());
        String gAME2_BOARD =
                "b b b b b\n  b b b b b\n  - b - b -\n  w - w - w\n  w w w w w";
        testBoard.setPieces(gAME2_BOARD, PieceColor.BLACK);
        System.out.println(testBoard);
        System.out.println(testBoard.getMoves());

        /**
         * Test multiple jump concatenation.
         */
        String game3_BOARD =
                "- b - b - \n - - - b - \n - - - - - \n - - b - - \n - - w - -";
        testBoard.setPieces(game3_BOARD, PieceColor.BLACK);
        System.out.println(testBoard);
        System.out.println(testBoard.getMoves());
    }
    //////////////////////////////////////////////////////

}

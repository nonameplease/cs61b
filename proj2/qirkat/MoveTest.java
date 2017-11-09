/* Author: Paul N. Hilfinger.  (C) 2008. */

package qirkat;

import org.junit.Test;
import static org.junit.Assert.*;

import static qirkat.Move.*;

/** Test Move creation.
 *  @author Scott Shao
 */
public class MoveTest {

    @Test
    public void testMove1() {
        Move m = move('a', '3', 'b', '2');
        assertNotNull(m);
        assertFalse("move should not be jump", m.isJump());
    }

    @Test
    public void testJump1() {
        Move m = move('a', '3', 'a', '5');
        assertNotNull(m);
        assertTrue("move should be jump", m.isJump());
    }

    @Test
    public void testString() {
        assertEquals("a3-b2", move('a', '3', 'b', '2').toString());
        assertEquals("a3-a5", move('a', '3', 'a', '5').toString());
        assertEquals("a3-a5-c3", move('a', '3', 'a', '5',
                                      move('a', '5', 'c', '3')).toString());
    }

    @Test
    public void testParseString() {
        assertEquals("a3-b2", parseMove("a3-b2").toString());
        assertEquals("a3-a5", parseMove("a3-a5").toString());
        assertEquals("a3-a5-c3", parseMove("a3-a5-c3").toString());
        assertEquals("a3-a5-c3-e1", parseMove("a3-a5-c3-e1").toString());
    }

    @Test
    public void testIndex() {
        System.out.println(Move.index('a', '1'));
        System.out.println(Move.index('e', '5'));
    }

    @Test
    public void testMove() {
        Move step1 = move('c', '5', 'a', '5');
        Move step2 = move('e', '5', 'c', '5');
        Move stepnull = move('a', '5');
        assertEquals("e5-c5-a5", move(step2, step1).toString());
        assertEquals("e5-c5-a5", move(step2, move(step1, null)).toString());
        Move test3 = move(step2, step1);
        assertEquals("e5-c5-a5", test3.toString());
        System.out.println(test3.jumpTail());
    }
}

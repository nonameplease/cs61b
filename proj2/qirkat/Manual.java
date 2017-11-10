package qirkat;

import java.util.ArrayList;

import static qirkat.PieceColor.*;
import static qirkat.Command.Type.*;

/** A Player that receives its moves from its Game's getMoveCmnd method.
 *  @author Scott Shao
 */
class Manual extends Player {

    /** A Player that will play MYCOLOR on GAME, taking its moves from
     *  GAME. */
    Manual(Game game, PieceColor myColor) {
        super(game, myColor);
        _prompt = myColor + ": ";
    }

    @Override
    Move myMove() {
        ArrayList<Move> allMoves = board().getMoves();
        if (allMoves.isEmpty()) {
            return null;
        } else {
            Command playerCommand = game().getMoveCmnd(_prompt);
            if (playerCommand != null) {
                Move playerMove = Move.parseMove(playerCommand.operands()[0]);
                if (allMoves.contains(playerMove)) {
                    return playerMove;
                } else {
                    System.out.println("that move is illegal.");
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    /** Identifies the player serving as a source of input commands. */
    private String _prompt;
}


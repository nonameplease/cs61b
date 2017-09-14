package game2048;

import static game2048.Side.*;

/** The input/output and GUI controller for play of a game of 2048.
 *  @author Scott Shao*/
public class Game {

    /** Controller for a game represented by MODEL, using SOURCE as the
     *  the source of key inputs and random Tiles. */
    public Game(Model model, InputSource source) {
        _model = model;
        _source = source;
        _playing = true;
    }

    /** Return true iff we have not received a Quit command. */
    boolean playing() {
        return _playing;
    }

    /** Clear the board and play one game, until receiving a quit or
     *  new-game request.  Update the viewer with each added tile or
     *  change in the board from tilting. */
    void playGame() {
        _model.clear();
        _model.addTile(getValidNewTile());
        while (_playing) {
            if (!_model.gameOver()) {
                _model.addTile(getValidNewTile());
                _model.notifyObservers();
            }

            boolean moved;
            moved = false;
            while (!moved) {
                String cmnd = _source.getKey();
                switch (cmnd) {
                case "Quit":
                    _playing = false;
                    return;
                case "New Game":
                    return;
                case "Up": case "Down": case "Left": case "Right":
                    if (!_model.gameOver() && _model.tilt(keyToSide(cmnd))) {
                        _model.notifyObservers();
                        moved = true;
                    }
                    break;
                default:
                    break;
                }

            }
        }
    }

    /** Return the side indicated by KEY ("Up", "Down", "Left",
     *  or "Right"). */
    private Side keyToSide(String key) {
        switch (key) {
        case "Up":
            return NORTH;
        case "Down":
            return SOUTH;
        case "Left":
            return WEST;
        case "Right":
            return EAST;
        default:
            throw new IllegalArgumentException("unknown key designation");
        }
    }

    /** Return a valid tile, using our source's tile input until finding
     *  one that fits on the current board. Assumes there is at least one
     *  empty square on the board. */
    private Tile getValidNewTile() {
        /** fixed */
        Tile valid = _source.getNewTile(_model.size());
        int x = valid.col();
        int y = valid.row();
        while (_model.tile(x, y) != null) {
            valid = _source.getNewTile(_model.size());
            x = valid.col();
            y = valid.row();
        }
        return valid;
    }

    /** Strings representing the four arrow keys. */
    private static final String[] ARROW_KEYS = {
        "Up", "Down", "Left", "Right"
    };

    /** The playing board. */
    private Model _model;

    /** Input source from standard input. */
    private InputSource _source;

    /** True while user is still willing to play. */
    private boolean _playing;

}

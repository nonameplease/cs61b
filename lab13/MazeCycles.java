import java.util.Observable;
/**
 *  @author Josh Hug
 */

public class MazeCycles extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    private int s;
    private Maze maze;
    private boolean cycle;

    public MazeCycles(Maze m) {
        super(m);
        maze = m;
        s = 0;
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    @Override
    public void solve() {
        // TODO: Your code here!
        findCycle(maze, s, s);
    }

    // Helper methods go here
    private void findCycle(Maze maze, int v, int p) {
        marked[v] = true;
        announce();
        if (cycle) {
            return;
        }
        for (int w : maze.adj(v)) {
            if (!marked[w]) {
                marked[w] = true;
                edgeTo[w] = v;
                announce();
                distTo[w] = distTo[v] + 1;
                findCycle(maze, w, v);
            } else if (w != p) {
                cycle = true;
            }
        }
    }
}


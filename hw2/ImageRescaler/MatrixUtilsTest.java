import org.junit.Test;
import static org.junit.Assert.*;

/** A series of tests that test the accumulateVertical and accumulate methods in the MatrixUtils class.
 *  @author Scott Shao
 */

public class MatrixUtilsTest {
    @Test
    public void testMatrixUtils() {
    double[][] energyMatrix = new double[][]{
            {1000000, 1000000, 1000000, 1000000},
            {1000000, 75990,   30003,   1000000},
            {1000000, 30002,   103046,  1000000},
            {1000000, 29515,   38273,   1000000},
            {1000000, 73403,   35399,   1000000},
            {1000000, 1000000, 1000000, 1000000}
    };

    double[][] horizontalAccumulation = new double[][]{
            {1000000, 2000000, 2075990, 2060005},
            {1000000, 1075990, 1060005, 2060005},
            {1000000, 1030002, 1132561, 2060005},
            {1000000, 1029515, 1067788, 2064914},
            {1000000, 1073403, 1064914, 2064914},
            {1000000, 2000000, 2073403, 2064914}
        };


    double[][] verticalAccumulation = new double[][]{
            {1000000, 1000000, 1000000, 1000000},
            {2000000, 1075990, 1030003, 2000000},
            {2075990, 1060005, 1133049, 2030003},
            {2060005, 1089520, 1098278, 2133049},
            {2089520, 1162923, 1124919, 2098278},
            {2162923, 2124919, 2124919, 2124919}
    };

    double[] verticalSeam = new double[]{1, 2, 1, 1, 2, 1};
    double[] horizontalSeam = new double[]{0, 1, 2, 1};

    assertArrayEquals(horizontalAccumulation,
            MatrixUtils.accumulate(energyMatrix,
                    MatrixUtils.Orientation.HORIZONTAL));


    assertArrayEquals(verticalAccumulation,
            MatrixUtils.accumulateVertical(energyMatrix));
    }


    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(MatrixUtilsTest.class));
    }
}

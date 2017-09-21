import java.util.HashSet;
import java.util.Set;

/**
 *  @author Scott Shao
 */

public class SetDemo {
    /** A simple set demo with ARGS.
     */
    public static void main(String[] args) {
        Set<String> s = new HashSet<String>();
        s.add("papa");
        s.add("bear");
        s.add("mama");
        s.add("bear");
        s.add("baby");
        s.add("bear");
        System.out.print(s);
    }
}

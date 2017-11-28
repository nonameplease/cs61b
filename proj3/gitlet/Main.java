package gitlet;

import java.io.*;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author
 */
public class Main {

    private static final String GITLET_DIR = ".gitlet" + File.separator;

    private static CommitTree init() {
        File f = new File(GITLET_DIR);
        if (f.exists()) {
            System.err.println("A Gitlet version-control system already exists in the current directory.");
            return null;
        }
        f.mkdirs();
        return CommitTree.commitTreeINIT();
    }

    private static CommitTree tryLoad() {
        CommitTree commitTree = null;
        File commitTreeFile = new File(GITLET_DIR + "gitletVCS.ser");
        if (commitTreeFile.exists()) {
            try {
                FileInputStream fileIn = new FileInputStream(commitTreeFile);
                ObjectInputStream objIn = new ObjectInputStream(fileIn);
                commitTree = (CommitTree) objIn.readObject();
                objIn.close();
            } catch (IOException | ClassNotFoundException excp) {
                commitTree = null;
            }
        }
        return commitTree;
    }

    public static void saveTree(CommitTree commitTree) {
        if (commitTree == null) {
            return;
        }

        try {
            File commitTreeFile = new File(GITLET_DIR + "gitletVCS.ser");
            FileOutputStream fileOut = new FileOutputStream(commitTreeFile);
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(commitTree);
        } catch (IOException excp) {
            System.out.println("IOException");
        }
    }

    




    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length == 0) {
            System.err.println("Please enter a command.");
            return;
        }

        String token = "";
        if (args.length > 1) {
            token = args[1];
        }

        String token2 = "";
        if (args.length > 2) {
            token2 = args[2];
        }

    }

}

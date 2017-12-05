package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Scott Shao
 */
public class Main {

    /**
     * Gitlet dir.
     */
    private static final String GITLET_DIR = ".gitlet" + File.separator;

    /**
     * Init commit tree.
     * @return Commit tree.
     */
    public static CommitTree init() {
        File f = new File(GITLET_DIR);
        File stagef = new File(Stage.STAGEDIR);
        if (f.exists()) {
            System.err.println("A Gitlet version-control "
                    + "system already exists in the current directory.");
            return null;
        }
        f.mkdirs();
        stagef.mkdirs();
        return CommitTree.commitTreeINIT();
    }

    /**
     * Load serialized tree.
     * @return Commit tree.
     */
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

    /**
     * Save serialized tree.
     * @param commitTree Commit tree.
     */
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

        String token3 = "";
        if (args.length > 3) {
            token3 = args[3];
        }
        CommitTree commitTree = tryLoad();
        String command = args[0];
        if (!command.equals("init") && commitTree == null) {
            System.err.println("Not in an initialized Gitlet directory.");
            return;
        }
        switch (command) {
        case "init":
            if (args.length == 1) {
                commitTree = init();
            } else {
                System.err.println("Incorrect operands.");
            }
            break;
        case "add":
            if (args.length == 2) {
                commitTree.add(token);
            } else {
                System.err.println("Incorrect operands.");
            }
            break;
        case "commit":
            if (args.length == 2) {
                commitTree.commit(token);
            } else if (args.length == 1) {
                System.err.println("Please enter a commit message.");
            } else {
                System.err.println("Incorrect operands.");
            }
            break;
        case "rm":
            if (args.length == 2) {
                commitTree.remove(token);
            } else {
                System.err.println("Incorrect operands.");
            }
            break;
        case "log":
            if (args.length == 1) {
                commitTree.printLog();
            } else {
                System.err.println("Incorrect operands.");
            }
            break;
        case "global-log":
            if (args.length == 1) {
                commitTree.printGlobalLog();
            } else {
                System.err.println("Incorrect operands.");
            }
            break;
        case "find":
            if (args.length == 2) {
                commitTree.find(token);
            } else {
                System.err.println("Incorrect operands.");
            }
            break;
        case "status":
            if (args.length == 1) {
                System.out.println(commitTree.toString());
            } else {
                System.err.println("Incorrect operands.");
            }
            break;
        case "checkout":
            if (args.length == 3) {
                if (!token.equals("--")) {
                    System.err.println("Incorrect operands.");
                    break;
                }
                commitTree.checkoutFile(token2);
            } else if (args.length == 4) {
                if (!token2.equals("--")) {
                    System.err.println("Incorrect operands.");
                    break;
                }
                commitTree.checkoutCommit(token, token3);
            } else if (args.length == 2) {
                commitTree.checkoutBranch(token);
            } else {
                System.err.println("Incorrect operands.");
            }
            break;
        case "branch":
            if (args.length == 2) {
                commitTree.addNewBranch(token);
            } else {
                System.err.println("Incorrect operands.");
            }
            break;
        case "rm-branch":
            if (args.length == 2) {
                commitTree.removeBranch(token);
            } else {
                System.err.println("Incorrect operands.");
            }
            break;
        case "reset":
            if (args.length == 2) {
                commitTree.reset(token);
            } else {
                System.err.println("Incorrect operands.");
            }
            break;
        case "merge":
            if (args.length == 2) {
                commitTree.merge(token);
            } else {
                System.err.println("Incorrect operands.");
            }
            break;
        default:
            System.err.println("No command with that name exists.");
            break;
        }
        saveTree(commitTree);
    }
}

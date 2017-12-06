package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

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

    /**
     * A hashmap between number of arguments and command.
     * @return The hashmap.
     */
    private static HashMap<Integer, ArrayList<String>> dealingError() {
        HashMap<Integer, ArrayList<String>> errorCase = new HashMap<>();
        ArrayList<String> argOne = new ArrayList<>();
        argOne.add("init");
        argOne.add("commit");
        argOne.add("log");
        argOne.add("global-log");
        argOne.add("status");
        ArrayList<String> argTwo = new ArrayList<>();
        argTwo.add("add");
        argTwo.add("commit");
        argTwo.add("rm");
        argTwo.add("find");
        argTwo.add("checkout");
        argTwo.add("branch");
        argTwo.add("rm-branch");
        argTwo.add("reset");
        argTwo.add("merge");
        ArrayList<String> argThreeAndFour = new ArrayList<>();
        argThreeAndFour.add("checkout");
        errorCase.put(1, argOne);
        errorCase.put(2, argTwo);
        errorCase.put(3, argThreeAndFour);
        errorCase.put(4, argThreeAndFour);
        return errorCase;
    }

    /**
     * Handle error.
     * @param args Input args.
     */
    private static void doComamandError(String[] args) {
        HashMap<Integer, ArrayList<String>> commandMap = dealingError();
        String command = args[0];
        int argLength = args.length;
        if (!commandMap.get(argLength).contains(command)) {
            System.err.println("Incorrect operands.");
        } else if (!commandMap.get(1).contains(command)
                && !commandMap.get(2).contains(command)) {
            System.err.println("No command with that name exists.");
        }
    }

    /**
     * Do command with one argument.
     * @param command Command.
     * @param commitTree CommitTree object.
     * @return Initialized CommitTree.
     */
    private static CommitTree doCommandArgOne(String command,
                                              CommitTree commitTree) {
        if (command.equals("init")) {
            commitTree = init();
        } else if (command.equals("commit")) {
            System.err.println("Please enter a commit message.");
        } else if (command.equals("log")) {
            commitTree.printLog();
        } else if (command.equals("global-log")) {
            commitTree.printGlobalLog();
        } else if (command.equals("status")) {
            System.out.println(commitTree.toString());
        }
        return commitTree;
    }

    /**
     * Do command with two arguments.
     * @param command Command.
     * @param commitTree CommitTree object.
     * @param token Arg.
     */
    private static void doCommandArgTwo(String command,
                                        CommitTree commitTree, String token) {
        if (command.equals("add")) {
            commitTree.add(token);
        } else if (command.equals("commit")) {
            commitTree.commit(token);
        } else if (command.equals("rm")) {
            commitTree.remove(token);
        } else if (command.equals("find")) {
            commitTree.find(token);
        } else if (command.equals("checkout")) {
            commitTree.checkoutBranch(token);
        } else if (command.equals("branch")) {
            commitTree.addNewBranch(token);
        } else if (command.equals("rm-branch")) {
            commitTree.removeBranch(token);
        } else if (command.equals("reset")) {
            commitTree.reset(token);
        } else if (command.equals("merge")) {
            commitTree.merge(token);
        }
    }

    /**
     * Do command with three or more arguments.
     * @param command Command.
     * @param commitTree CommitTree object.
     * @param args Input args.
     */
    private static void doCommandArgMore(String command,
                                         CommitTree commitTree, String[] args) {
        if (args.length == 3) {
            if (!args[1].equals("--")) {
                System.err.println("Incorrect operands.");
            } else {
                commitTree.checkoutFile(args[2]);
            }
        } else if (args.length == 4) {
            if (!args[2].equals("--")) {
                System.err.println("Incorrect operands.");
            } else {
                commitTree.checkoutCommit(args[1], args[3]);
            }
        }
    }

    /**
     * Parse and do command.
     * @param command The command string.
     * @param commitTree CommitTree object.
     * @param args Input args.
     * @return Initialized commitTree.
     */
    public static CommitTree doCommand(String command,
                                       CommitTree commitTree, String[] args) {
        doComamandError(args);
        int argsLength = args.length;
        if (argsLength == 1) {
            commitTree = doCommandArgOne(command, commitTree);
        } else if (argsLength == 2) {
            doCommandArgTwo(command, commitTree, args[1]);
        } else {
            doCommandArgMore(command, commitTree, args);
        }
        return commitTree;
    }

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length == 0) {
            System.err.println("Please enter a command.");
            return;
        }
        CommitTree commitTree = tryLoad();
        String command = args[0];
        if (!command.equals("init") && commitTree == null) {
            System.err.println("Not in an initialized Gitlet directory.");
            return;
        }
        commitTree = doCommand(command, commitTree, args);
        saveTree(commitTree);
    }
}

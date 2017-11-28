package gitlet;

import sun.awt.Symbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class CommitTree implements Serializable {
    private HashMap<String, Branch> branchMap;
    private Branch currentBranch;
    /**
     * Key: Parent HashValue.
     * Value: Commit.
     */
    private HashMap<String, Commit> allCommits;
    private HashMap<String, ArrayList<String>> commitMsgToHashValue;

    private CommitTree() {
        branchMap = new HashMap<>();
        allCommits = new HashMap<>();
        commitMsgToHashValue = new HashMap<>();
    }

    public HashMap<String, Branch> getBranchMap() {
        return branchMap;
    }

    public Branch getCurrentBranch() {
        return currentBranch;
    }

    /**
     * INIT
     */
    public static CommitTree commitTreeINIT() {
        CommitTree commitTree = new CommitTree();
        String message = "initial commit";
        String branchName = "master";
        Commit initCommit = new Commit(null, message);
        Branch initBranch = new Branch(branchName, initCommit);
        ArrayList<String> hashValueBank = new ArrayList<>();
        hashValueBank.add(initCommit.getHashValue());
        commitTree.currentBranch = initBranch;
        commitTree.branchMap.put(branchName, initBranch);
        commitTree.allCommits.put(initCommit.getHashValue(), initCommit);
        commitTree.commitMsgToHashValue.put(message, hashValueBank);
        return commitTree;
    }

    public void add(String fileName) {
        currentBranch.stageFile(fileName);
    }

    public void commit(String msg) {
        if (msg.length() > 0) {
            currentBranch.commitMsg(msg);
        } else {
            currentBranch.commitNoMsg();
        }

        Commit current = currentBranch.getHead();
        allCommits.put(current.getHashValue(), current);
        ArrayList<String> temp = commitMsgToHashValue.get(msg);
        if (temp == null) {
            temp = new ArrayList<>();
        }
        temp.add(current.getHashValue());
        commitMsgToHashValue.put(msg, temp);
    }

    public void remove(String fileName) {
        currentBranch.removeFile(fileName);
    }

    public void printLog() {
        Commit currentCommit = currentBranch.getHead();
        while (currentCommit != null) {
            System.out.println(currentCommit.toString());
            System.out.println();
            currentCommit = currentCommit.getParent();
        }
    }

    public void printGlobalLog() {
        for (String key : allCommits.keySet()) {
            Commit commit = allCommits.get(key);
            System.out.println(commit.toString());
            System.out.println();
        }
    }

    public void find(String msg) {
        if (!commitMsgToHashValue.containsKey(msg)) {
            System.err.println("Found no commit with that message.");
            return;
        }
        for (String hashValue : commitMsgToHashValue.get(msg)) {
            System.out.println(hashValue);
        }
    }

    public void checkout(String fileName) {
        if (!currentBranch.getHead().getParent().contains(fileName)) {
            System.err.println("File does not exist in that commit.");
            return;
        }

    }
}

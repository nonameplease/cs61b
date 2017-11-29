package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.*;

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
        initCommit.setTimeStamp(new Date(0));
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
        if (getCurrentBranch().getCurrentStage().getStagedFiles().keySet() == null) {
            System.err.println(" No changes added to the commit.");
        }

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
        while (currentCommit != currentCommit.getParent()) {
            System.out.println(currentCommit.toString());
            System.out.println();
            currentCommit = currentCommit.getParent();
        }

        System.out.println(currentCommit.toString());
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

    public void checkoutFile(String name) {
        if (!currentBranch.getHead().contains(name)) {
            System.err.println("File does not exist in that commit.");
            return;
        }

        if (branchMap.containsKey(name)) {
            currentBranch = branchMap.get(name);
            Commit commit = currentBranch.getHead();
            commit.restoreAllFiles();
            return;
        }

        if (currentBranch.getHead().contains(name)) {
            currentBranch.getHead().restoreFiles(name);
            return;
        }
    }

    public void checkoutBranch(String name) {
        List<String> currentDirFiles = new ArrayList<>();
        currentDirFiles = Utils.plainFilenamesIn(System.getProperty("user.dir"));

        if (!branchMap.containsKey(name)) {
            System.err.println("No such branch exists.");
            return;
        }

        for (String fileName : currentDirFiles) {
            if (currentBranch.getCurrentStage().getStagedFiles().get(fileName) == null && branchMap.get(name).getCurrentStage().getStagedFiles().containsKey(fileName)) {
                System.err.println("There is an untracked file in the way; delete it or add it first. ");
                return;
            }
        }

        if (currentBranch.getBranchName() == name) {
            System.err.println("No need to checkout the current branch.");
            return;
        }

        Branch givenBranch = branchMap.get(name);
        Commit givenHeadCommit = givenBranch.getHead();
        givenHeadCommit.restoreAllFiles();
        currentBranch = givenBranch;
        currentBranch.getHead().restoreAllFiles();
    }

    public void checkoutCommit(String hashValue, String name) {
        hashValue = getLongCommitHashValue(hashValue);
        if (!allCommits.containsKey(hashValue)) {
            System.err.println("No commit with that id exists.");
            return;
        }
        Commit commit = allCommits.get(hashValue);
        if (!commit.contains(name)) {
            System.err.println("File does not exist in that commit.");
            return;
        }
        commit.restoreFiles(name);
    }

    public void addNewBranch(String branchName) {
        if (branchMap.containsKey(branchName)) {
            System.err.println("A branch with that name already exists.");
            return;
        }
        Commit head = currentBranch.getHead();
        Branch newBranch = new Branch(branchName, head);
        branchMap.put(branchName, newBranch);
    }

    public void removeBranch(String branchName) {
        if (currentBranch.getBranchName().equals(branchName)) {
            System.err.println("Cannot remove the current branch.");
            return;
        }
        if (!branchMap.containsKey(branchName)) {
            System.err.println("A branch with that name does not exist.");
            return;
        }
        branchMap.remove(branchName);
    }

    public void reset(String hashValue) {
        hashValue = getLongCommitHashValue(hashValue);
        if (!allCommits.containsKey(hashValue)) {
            System.err.println("No commit with that id exists.");
            return;
        }
        Commit commit = allCommits.get(hashValue);
        commit.restoreAllFiles();
        currentBranch.setHead(commit);
        currentBranch.setCurrentStage(null);
    }

    public void merge(String branchName) {
        if (!branchMap.containsKey(branchName)) {
            System.err.println("A branch with that name does not exist.");
            return;
        }

        if (currentBranch.getBranchName().equals(branchName)) {
            System.err.println("Cannot merge a branch with itself.");
            return;
        }

        Branch given = branchMap.get(branchName);
        currentBranch.merge(given);
    }

    public String getLongCommitHashValue(String shortHashValue) {
        if (shortHashValue.length() < 40) {
            int length = shortHashValue.length();
            for (String hash : allCommits.keySet()) {
                String shorthash = hash.substring(0, length);
                if (shorthash.equals(shortHashValue)) {
                    return hash;
                }
            }
            return null;
        }
        return shortHashValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Branches ===" + "\n");
        sb.append("*" + currentBranch.getBranchName() + "\n");
        for (String branchName : branchMap.keySet()) {
            sb.append(branchName);
            sb.append("\n");
        }
        sb.append("\n");
        sb.append(currentBranch.getCurrentStage().toString());
        return sb.toString();
    }

}

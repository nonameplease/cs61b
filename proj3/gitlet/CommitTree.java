package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * CommitTree class.
 * @author Scott Shao
 */
public class CommitTree implements Serializable {
    /**
     * Hash map maps branch name to branch.
     */
    private HashMap<String, Branch> branchMap;
    /**
     * Current branch.
     */
    private Branch currentBranch;
    /**
     * Key: Parent HashValue.
     * Value: Commit.
     */
    private HashMap<String, Commit> allCommits;
    /**
     * Hash map maps commit message to SHA1 ID.
     */
    private HashMap<String, ArrayList<String>> commitMsgToHashValue;
    /**
     * Length of SHA1.
     */
    private static final int IDlength = 40;

    /**
     * Initialize commit tree.
     */
    private CommitTree() {
        branchMap = new HashMap<>();
        allCommits = new HashMap<>();
        commitMsgToHashValue = new HashMap<>();
    }

    /**
     * Get branch map.
     * @return Branch map.
     */
    public HashMap<String, Branch> getBranchMap() {
        return branchMap;
    }

    /**
     * Return current branch.
     * @return Current branch.
     */
    public Branch getCurrentBranch() {
        return currentBranch;
    }

    /**
     * INIT.
     * @return Return initialized command tree.
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

    /**
     * Add command.
     * @param fileName File name.
     */
    public void add(String fileName) {
        currentBranch.stageFile(fileName);
    }

    /**
     * Commit command.
     * @param msg Commit message.
     */
    public void commit(String msg) {
        if (getCurrentBranch().getCurrentStage().
                getStagedFiles().keySet().size() == 0 && getCurrentBranch().getCurrentStage().getFilesMarkedForRemove().size() == 0) {
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

    /**
     * Remove file.
     * @param fileName File name.
     */
    public void remove(String fileName) {
        currentBranch.removeFile(fileName);
    }

    /**
     * Log command.
     */
    public void printLog() {
        Commit currentCommit = currentBranch.getHead();
        while (currentCommit != currentCommit.getParent()) {
            System.out.println(currentCommit.toString());
            System.out.println();
            currentCommit = currentCommit.getParent();
        }

        System.out.println(currentCommit.toString());
    }

    /**
     * Global log command.
     */
    public void printGlobalLog() {
        for (String key : allCommits.keySet()) {
            Commit commit = allCommits.get(key);
            System.out.println(commit.toString());
            System.out.println();
        }
    }

    /**
     * Find command.
     * @param msg Commit message.
     */
    public void find(String msg) {
        if (!commitMsgToHashValue.containsKey(msg)) {
            System.err.println("Found no commit with that message.");
            return;
        }
        for (String hashValue : commitMsgToHashValue.get(msg)) {
            System.out.println(hashValue);
        }
    }

    /**
     * Checkout command with file name.
     * @param name File name.
     */
    public void checkoutFile(String name) {
        if (!currentBranch.getHead().contains(name)) {
            System.err.println("File does not exist in that commit.");
            return;
        }

        if (currentBranch.getHead().contains(name)) {
            currentBranch.getHead().restoreFiles(name);
            return;
        }
    }

    /**
     * Checkout command with branch name.
     * @param name Branch name.
     */
    public void checkoutBranch(String name) {
        List<String> workingDirFiles;
        workingDirFiles = Utils.plainFilenamesIn(
                System.getProperty("user.dir"));
        //System.out.println(currentDirFiles);
        //System.out.println(currentBranch.getHead().getFileMapper().keySet());

        if (!branchMap.containsKey(name)) {
            System.err.println("No such branch exists.");
            return;
        }

        if (currentBranch.getBranchName().equals(name)) {
            System.err.println("No need to checkout the current branch.");
            return;
        }

        Branch givenBranch = branchMap.get(name);
        Commit givenHeadCommit = givenBranch.getHead();

        for (String fileName : workingDirFiles) {
            if (givenHeadCommit.getFileMapper().containsKey(fileName)) {
                if (!currentBranch.getHead().getFileMapper().containsKey(fileName) ||
                        currentBranch.getHead().getFile(fileName).equals(new File(fileName))) {
                    System.err.println("There is an untracked file "
                            + "in the way; delete it or add it first. ");
                    return;
                }
            }
        }

        for (String fileName : currentBranch.getHead().getFileMapper().keySet()) {
            if (!givenHeadCommit.getFileMapper().containsKey(fileName)) {
                File f = new File(fileName);
                f.delete();
            }
        }
        givenHeadCommit.restoreAllFiles();
        givenBranch.getCurrentStage().clearStageArea();
        currentBranch = givenBranch;
        //currentBranch.getHead().restoreAllFiles();
    }

    /**
     * Checkout command with commit ID and file name.
     * @param hashValue Commit ID.
     * @param name File name.
     */
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

    /**
     * Add new branch.
     * @param branchName Branch name.
     */
    public void addNewBranch(String branchName) {
        if (branchMap.containsKey(branchName)) {
            System.err.println("A branch with that name already exists.");
            return;
        }
        Commit head = currentBranch.getHead();
        Branch newBranch = new Branch(branchName, head);
        branchMap.put(branchName, newBranch);
    }

    /**
     * Remove branch.
     * @param branchName Branch name.
     */
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

    /**
     * Reset command.
     * @param hashValue Commit ID.
     */
    public void reset(String hashValue) {
        hashValue = getLongCommitHashValue(hashValue);
        if (!allCommits.containsKey(hashValue)) {
            System.err.println("No commit with that id exists.");
            return;
        }
        List<String> workingDirFiles;
        workingDirFiles = Utils.plainFilenamesIn(
                System.getProperty("user.dir"));
        Commit commit = allCommits.get(hashValue);
        for (String fileName : workingDirFiles) {
            if (!currentBranch.getHead().getFileMapper().containsKey(fileName) && commit.getFileMapper().containsKey(fileName)) {
                System.err.println("There is an untracked file in the way; delete it or add it first.");
                return;
            }
        }
        commit.restoreAllFiles();
        currentBranch.setHead(commit);
        currentBranch.setCurrentStage(null);
    }

    /**
     * Merge command.
     * @param branchName Given branch.
     */
    public void merge(String branchName) {
        if (!branchMap.containsKey(branchName)) {
            System.err.println("A branch with that name does not exist.");
            return;
        }

        if (currentBranch.getBranchName().equals(branchName)) {
            System.err.println("Cannot merge a branch with itself.");
            return;
        }

        for (String fileName : currentBranch.getCurrentStage().getStagedFiles().keySet()) {
            if (currentBranch.getCurrentStage().getStagedFiles().get(fileName) == null) {
                System.err.println("You have uncommitted changes.");
                return;
            }
        }

        Branch given = branchMap.get(branchName);
        currentBranch.merge(given);
    }

    /**
     * Get normal commit hash value from
     * a shorthand version.
     * @param shortHashValue The shorthand ID.
     * @return Normal ID.
     */
    public String getLongCommitHashValue(String shortHashValue) {
        if (shortHashValue.length() < IDlength) {
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
            if (branchName != currentBranch.getBranchName()) {
                sb.append(branchName);
                sb.append("\n");
            }
        }
        sb.append("\n");
        sb.append(currentBranch.getCurrentStage().toString());
        return sb.toString();
    }

}

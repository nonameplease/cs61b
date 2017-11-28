package gitlet;

import java.io.Serializable;

public class Branch implements Serializable {
    private final String branchName;
    private Commit head;
    private Stage currentStage;

    public Branch(String branchName, Commit head) {
        this.branchName = branchName;
        this.head = head;
        this.currentStage = new Stage(head);
    }

    public String getBranchName() {
        return branchName;
    }

    public Commit getHead() {
        return head;
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    public void setHead(Commit head) {
        this.head = head;
    }

    public void setCurrentStage(Stage stage) {
        this.currentStage = stage;
    }

    public void commitNoMsg() {
        System.err.println("Please enter a commit message.");
    }

    public void commitMsg(String msg) {
        this.head = new Commit(currentStage, msg);
        currentStage = new Stage(head);
    }

    public void stageFile(String fileName) {
        currentStage.add(fileName);
    }

    public void removeFile(String fileName) {
        currentStage.rm(fileName);
    }

    private static Commit splitPoint(Branch b1, Branch b2) {
        int b1Length = b1.length();
        int b2Length = b2.length();
        if (b2Length > b1Length) {
            return splitPoint(b2, b1);
        }
        Commit commit1 = b1.getHead();
        Commit commit2 = b2.getHead();
        for (int i = 0; i < b1Length - b2Length; i += 1) {
            commit1 = commit1.getParent();
        }
        while (!commit1.equals(commit2)) {
            commit1 = commit1.getParent();
            commit2 = commit2.getParent();
        }
        assert commit1 == commit2;
        return commit1;
    }

    /**
     * Num of commits in branch b.
     */
    private int length() {
        int length = 0;
        Commit current = head;
        while (current != null) {
            length += 1;
            current = current.getParent();
        }
        return length;
    }

    /**
     * I highly doubt this is correct.
     * @param given Given branch.
     */
    public void merge(Branch given) {
        Commit givenHead = given.getHead();
        Commit split = splitPoint(this, given);
        for (String fileName : givenHead.getFileMapper().keySet()) {
            if (!head.contains(fileName) || (!head.modified(split, fileName))) {
                head.copyFile(givenHead, fileName, fileName);
            } else {
                String copyName = fileName + ".conflict";
                head.copyFile(givenHead, fileName, copyName);
            }
        }
    }

}

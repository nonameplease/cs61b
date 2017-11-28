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


}

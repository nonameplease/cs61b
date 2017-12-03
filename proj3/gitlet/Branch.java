package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
        while (current.getParent() != current) {
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
        if (split.equals(givenHead)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (split.equals(getBranchName())) {
            head = given.getHead();
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        Set<String> allFiles = new HashSet<>();
        for (String fileName : head.getFileMapper().keySet()) {
            allFiles.add(fileName);
        }
        for (String fileName : givenHead.getFileMapper().keySet()) {
            allFiles.add(fileName);
        }
        for (String fileName : allFiles) {
            boolean conflict = false;
            if (split.contains(fileName)) {
                if (givenHead.contains(fileName) && head.contains(fileName)) {
                    if (givenHead.modified(split, fileName) && !head.modified(split, fileName)) {
                        head.copyFile(givenHead, fileName, fileName);
                    } else if (givenHead.modified(split, fileName) && head.modified(split, fileName)) {
                        if (!head.getFile(fileName).equals(givenHead.getFile(fileName))) {
                            conflict = true;
                        }
                    }
                } else if (head.contains(fileName) && !givenHead.contains(fileName)) {
                    if (!head.modified(head, fileName)) {
                        givenHead.getCurrentStage().rm(fileName);
                    }
                }
                if (givenHead.contains(fileName)) {
                    if (givenHead.modified(split, fileName) && !head.contains(fileName)) {
                        conflict = true;
                    }
                }
                if (head.contains(fileName)) {
                    if (head.modified(split, fileName) && givenHead.contains(fileName)) {
                        conflict = true;
                    }
                }
            } else {
                if (!head.contains(fileName) && givenHead.contains(fileName)) {
                    File copyFrom = givenHead.getFile(fileName);
                    File copyTo = new File(fileName);
                    Utils.writeContents(copyTo, Utils.readContents(copyFrom));
                    head.getCurrentStage().add(fileName);
                }
                if (head.contains(fileName) && givenHead.contains(fileName)) {
                    if (head.getFile(fileName) != givenHead.getFile(fileName)) {
                        conflict = true;
                    }
                }
            }

            /**
             * Dealing with conflicts.
             */
            if (conflict == true) {
                byte[] currentContent;
                byte[] givenContent;
                if (head.contains(fileName)) {
                    currentContent = Utils.readContents(head.getFile(fileName));
                } else {
                    currentContent = new byte[0];
                }
                if (givenHead.contains(fileName)) {
                    givenContent = Utils.readContents(givenHead.getFile(fileName));
                } else {
                    givenContent = new byte[0];
                }
                StringBuilder sb = new StringBuilder();
                sb.append("<<<<<<< HEAD \n");
                sb.append(currentContent);
                sb.append("======= \n");
                sb.append(givenContent);
                sb.append(">>>>>>> \n");
                File f = new File(fileName);
                Utils.writeContents(f, sb.toString());
                head.getCurrentStage().add(fileName);
            }
        }

        /*for (String fileName : givenHead.getFileMapper().keySet()) {
            if (!head.contains(fileName) || (!head.modified(split, fileName))) {
                head.copyFile(givenHead, fileName, fileName);
            } else {
                String copyName = fileName + ".conflict";
                head.copyFile(givenHead, fileName, copyName);
            }
        }*/
    }

}

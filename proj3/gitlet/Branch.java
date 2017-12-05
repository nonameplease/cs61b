package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Class branch.
 * @author Scott Shao
 */
public class Branch implements Serializable {
    /**
     * Branch name.
     */
    private final String branchName;
    /**
     * Head commit.
     */
    private Commit head;
    /**
     * Current stage.
     */
    private Stage currentStage;

    /**
     * Initialize branch.
     * @param givenBranchName Given branch name.
     * @param givenHead Given commit.
     */
    public Branch(String givenBranchName, Commit givenHead) {
        this.branchName = givenBranchName;
        this.head = givenHead;
        this.currentStage = new Stage(givenHead);
    }

    /**
     * Get current branch name.
     * @return Current branch name.
     */
    public String getBranchName() {
        return branchName;
    }

    /**
     * Get current head commit.
     * @return The current head commit.
     */
    public Commit getHead() {
        return head;
    }

    /**
     * Get current stage.
     * @return The current stage.
     */
    public Stage getCurrentStage() {
        return currentStage;
    }

    /**
     * Set head commit.
     * @param givenHead The head commit.
     */
    public void setHead(Commit givenHead) {
        this.head = givenHead;
    }

    /**
     * Set the current stage to stage.
     * @param givenStage The stage.
     */
    public void setCurrentStage(Stage givenStage) {
        this.currentStage = givenStage;
    }

    /**
     * Commit without message.
     */
    public void commitNoMsg() {
        System.err.println("Please enter a commit message.");
    }

    /**
     * Commit with message.
     * @param msg The message.
     */
    public void commitMsg(String msg) {
        this.head = new Commit(currentStage, msg);
        currentStage = new Stage(head);
    }

    /**
     * Stage file.
     * @param fileName File name.
     */
    public void stageFile(String fileName) {
        currentStage.add(fileName);
    }

    /**
     * Remove file.
     * @param fileName File name.
     */
    public void removeFile(String fileName) {
        currentStage.rm(fileName);
    }

    /**
     * Find split point.
     * @param b1 Branch 1.
     * @param b2 Branch 2.
     * @return Commit at split point.
     */
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
     * @return A int length.
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
        System.out.println(split.getFileMapper().keySet().toString());
        if (split.equals(givenHead)) {
            System.out.println(
                    "Given branch is an ancestor of the current branch.");
            return;
        }
        if (split.equals(getBranchName())) {
            head = givenHead;
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
                    if (givenHead.modified(split, fileName)
                            && !head.modified(split, fileName)) {
                        head.copyFile(givenHead, fileName, fileName);
                    } else if (givenHead.modified(split, fileName)
                            && head.modified(split, fileName)) {
                        if (!head.getFile(fileName).
                                equals(givenHead.getFile(fileName))) {
                            conflict = true;
                        }
                    }
                } else if (head.contains(fileName)
                        && !givenHead.contains(fileName)) {
                    if (!head.modified(head, fileName)) {
                        givenHead.getCurrentStage().rm(fileName);
                    }
                }
                if (givenHead.contains(fileName)) {
                    if (givenHead.modified(split, fileName)
                            && !head.contains(fileName)) {
                        conflict = true;
                    }
                }
                if (head.contains(fileName)) {
                    if (head.modified(split, fileName)
                            && givenHead.contains(fileName)) {
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

            if (conflict) {
                byte[] currentContent;
                byte[] givenContent;
                if (head.contains(fileName)) {
                    currentContent = Utils.readContents(head.getFile(fileName));
                } else {
                    currentContent = new byte[0];
                }
                if (givenHead.contains(fileName)) {
                    givenContent =
                            Utils.readContents(givenHead.getFile(fileName));
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
    }

}

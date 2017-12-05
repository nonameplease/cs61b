package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
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
        List<String> workingDirFiles;
        workingDirFiles = Utils.plainFilenamesIn(
                System.getProperty("user.dir"));
        for (String fileName : workingDirFiles) {
            if (!head.getFileMapper().containsKey(fileName)) {
                if (givenHead.getFileMapper().containsKey(fileName)
                        && !Utils.readContentsAsString(new File(fileName))
                        .equals(Utils.readContentsAsString
                                (givenHead.getFile(fileName)))) {
                    System.err.println("There is an untracked file "
                            + "in the way; delete it or add it first.");
                    return;
                }
            }
        }
        if (split.equals(givenHead)) {
            System.out.println(
                    "Given branch is an ancestor of the current branch.");
            return;
        }
        if (split.equals(head)) {
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
        for (String fileName : split.getFileMapper().keySet()) {
            allFiles.add(fileName);
        }
        boolean hasConflict = false;
        for (String fileName : allFiles) {
            FileBoolean fileBoolean = new FileBoolean(fileName);
            checkState(fileBoolean, fileName, split, givenHead);
            hasConflict = merge(fileBoolean, fileName, givenHead, hasConflict);
        }
        commitMsg("Merged " + given.getBranchName()
                + " into " + getBranchName() + ".");
        if (hasConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /**
     * Helper method.
     * @param fileBoolean FileBoolean.
     * @param fileName File name.
     * @param split Split point.
     * @param givenHead Given head commit.
     */
    private void checkState(FileBoolean fileBoolean, String fileName,
                            Commit split, Commit givenHead) {
        /**
         * Checking states.
         */
        if (split.getFileMapper().containsKey(fileName)) {
            fileBoolean.setPresentInSplit(true);
        }
        if (givenHead.getFileMapper().containsKey(fileName)) {
            fileBoolean.setPresentInGivenCommit(true);
        }
        if (getHead().getFileMapper().containsKey(fileName)) {
            fileBoolean.setPresentInCurrentCommit(true);
        }
        if (fileBoolean.getPresentInSplit()
                && fileBoolean.getPresentInGivenCommit()) {
            if (givenHead.modified(split, fileName)) {
                fileBoolean.setModifiedInGivenCommit(true);
            }
        }
        if (fileBoolean.getPresentInSplit()
                && fileBoolean.getPresentInCurrentCommit()) {
            if (head.modified(split, fileName)) {
                fileBoolean.setModifiedInCurrnetCommit(true);
            }
        }
    }

    /**
     * Helper merge method.
     * @param fileBoolean FileBoolean check states.
     * @param fileName File name.
     * @param givenHead Given head commit.
     * @param hasConflict State has conflict.
     * @return Return boolean has conflict.
     */
    private boolean merge(FileBoolean fileBoolean, String fileName,
                          Commit givenHead, boolean hasConflict) {
        /**
         * Merge.
         */
        if (fileBoolean.getPresentInSplit()
                && fileBoolean.getPresentInGivenCommit()
                && fileBoolean.getPresentInCurrentCommit()
                && fileBoolean.getModifiedInGivenCommit()
                && !fileBoolean.getModifiedInCurrentCommit()) {
            head.copyFile(givenHead, fileName, fileName);
            currentStage.add(fileName);
        } else if (!fileBoolean.getPresentInSplit()
                && !fileBoolean.getPresentInCurrentCommit()
                && fileBoolean.getPresentInGivenCommit()) {
            head.copyFile(givenHead, fileName, fileName);
            currentStage.add(fileName);
        } else if (fileBoolean.getPresentInSplit()
                && fileBoolean.getPresentInCurrentCommit()
                && !fileBoolean.getModifiedInCurrentCommit()
                && !fileBoolean.getPresentInGivenCommit()) {
            File f = new File(fileName);
            f.delete();
            currentStage.getFilesMarkedForRemove().add(fileName);
        } else {
            hasConflict = conflicted(fileBoolean, fileName,
                    givenHead, hasConflict);
        }
        return hasConflict;
    }

    /**
     * Helper method.
     * @param fileBoolean FileBoolean.
     * @param fileName File name.
     * @param givenHead Given head commit.
     * @param hasConflict Boolean has conflict.
     * @return Boolean has conflict.
     */
    private boolean conflicted(FileBoolean fileBoolean,
                               String fileName, Commit givenHead,
                               boolean hasConflict) {
        boolean conflicted = false;
        String givenContent = "";
        String currentContent = "";
        if (fileBoolean.getPresentInSplit()
                && fileBoolean.getModifiedInGivenCommit()
                && fileBoolean.getPresentInCurrentCommit()
                && fileBoolean.getModifiedInGivenCommit()
                && fileBoolean.getModifiedInCurrentCommit()
                && head.modified(givenHead, fileName)) {
            givenContent = Utils.readContentsAsString
                    (givenHead.getFile(fileName));
            currentContent = Utils.readContentsAsString
                    (head.getFile(fileName));
            conflicted = true;
            hasConflict = true;
        } else if (fileBoolean.getPresentInSplit()
                && fileBoolean.getPresentInCurrentCommit()
                && !fileBoolean.getPresentInGivenCommit()
                && fileBoolean.getModifiedInCurrentCommit()) {
            currentContent = Utils.readContentsAsString
                    (head.getFile(fileName));
            conflicted = true;
            hasConflict = true;
        } else if (fileBoolean.getPresentInSplit()
                && fileBoolean.getPresentInGivenCommit()
                && !fileBoolean.getPresentInCurrentCommit()
                && fileBoolean.getModifiedInGivenCommit()) {
            givenContent = Utils.readContentsAsString
                    (givenHead.getFile(fileName));
            conflicted = true;
            hasConflict = true;
        } else if (!fileBoolean.getPresentInSplit()
                && fileBoolean.getPresentInGivenCommit()
                && fileBoolean.getPresentInCurrentCommit()
                && head.modified(givenHead, fileName)) {
            givenContent = Utils.readContentsAsString
                    (givenHead.getFile(fileName));
            currentContent = Utils.readContentsAsString
                    (head.getFile(fileName));
            conflicted = true;
            hasConflict = true;
        } else {
            conflicted = false;
        }
        if (conflicted) {
            StringBuilder sb = new StringBuilder();
            sb.append("<<<<<<< HEAD");
            sb.append(System.lineSeparator());
            sb.append(currentContent);
            sb.append("=======");
            sb.append(System.lineSeparator());
            sb.append(givenContent);
            sb.append(">>>>>>>");
            sb.append(System.lineSeparator());
            Utils.writeContents(new File(fileName), sb.toString());
            currentStage.add(fileName);
        }
        return hasConflict;
    }
}

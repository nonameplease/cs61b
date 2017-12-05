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
        //System.out.println(split.getFileMapper().keySet().toString());
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

        class fileBoolean {
            String fileName;
            boolean presentInSplit;
            boolean presentInGivenCommit;
            boolean presentInCurrentCommit;
            boolean presentInWorkingDir;
            boolean modifiedInGivenCommit;
            boolean modifiedInCurrentCommit;
            boolean modifiedInWorkingDir;

            public fileBoolean (String givenFileName) {
                fileName = givenFileName;
                presentInSplit = false;
                presentInGivenCommit = false;
                presentInCurrentCommit = false;
                presentInWorkingDir = false;
                modifiedInGivenCommit = false;
                modifiedInCurrentCommit = false;
                modifiedInWorkingDir = false;
            }

            public void setPresentInSplit(boolean input) {
                presentInSplit = input;
            }

            public void setPresentInGivenCommit(boolean input) {
                presentInGivenCommit = input;
            }

            public void setPresentInCurrentCommit(boolean input) {
                presentInCurrentCommit = input;
            }

            public void setPresentInWorkingDir(boolean input) {
                presentInWorkingDir = input;
            }

            public void setModifiedInGivenCommit(boolean input) {
                modifiedInGivenCommit = input;
            }

            public void setModifiedInCurrnetCommit(boolean input) {
                modifiedInCurrentCommit = input;
            }

            public void setModifiedInWorkingDir(boolean input) {
                modifiedInWorkingDir = input;
            }
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
            /**
             * Checking states.
             */
            fileBoolean fileBoolean = new fileBoolean(fileName);
            if (split.getFileMapper().containsKey(fileName)) {
                fileBoolean.setPresentInSplit(true);
            }
            if (givenHead.getFileMapper().containsKey(fileName)) {
                fileBoolean.setPresentInGivenCommit(true);
            }
            if (head.getFileMapper().containsKey(fileName)) {
                fileBoolean.setPresentInCurrentCommit(true);
            }
            if (workingDirFiles.contains(fileName)) {
                fileBoolean.setPresentInWorkingDir(true);
            }
            if (!fileBoolean.presentInSplit) {
                if (fileBoolean.presentInGivenCommit) {
                    //fileBoolean.setModifiedInGivenCommit(true);
                }
            } else {
                if (fileBoolean.presentInGivenCommit) {
                    if (givenHead.modified(split, fileName)) {
                        fileBoolean.setModifiedInGivenCommit(true);
                    }
                }
            }
            if (!fileBoolean.presentInSplit) {
                if (fileBoolean.presentInCurrentCommit) {
                    //fileBoolean.setModifiedInCurrnetCommit(true);
                }
            } else {
                if (fileBoolean.presentInCurrentCommit) {
                    if (head.modified(split, fileName)) {
                        fileBoolean.setModifiedInCurrnetCommit(true);
                    }
                }
            }
            if (!fileBoolean.presentInSplit) {
                if (fileBoolean.presentInWorkingDir) {
                    //fileBoolean.setModifiedInWorkingDir(true);
                }
            } else {
                if (fileBoolean.presentInWorkingDir) {
                    if (!split.getFile(fileName).equals(new File(fileName))) {
                        //fileBoolean.setModifiedInWorkingDir(true);
                    }
                }
            }

            /**
             * System out for testing.
             * String fileName;
             boolean presentInSplit;
             boolean presentInGivenCommit;
             boolean presentInCurrentCommit;
             boolean presentInWorkingDir;
             boolean modifiedInGivenCommit;
             boolean modifiedInCurrentCommit;
             boolean modifiedInWorkingDir;
             */
            /*System.out.println("fileName: " + fileName);
            System.out.println();
            System.out.println("presentInSplit: " + fileBoolean.presentInSplit);
            System.out.println();
            System.out.println("presentInGivenCommit: " + fileBoolean.presentInGivenCommit);
            System.out.println();
            System.out.println("presentInCurrentCommit: " + fileBoolean.presentInCurrentCommit);
            System.out.println();
            System.out.println("presentInWorkingDir: " + fileBoolean.presentInWorkingDir);
            System.out.println();
            System.out.println("modifiedInGivenCommit: " + fileBoolean.modifiedInGivenCommit);
            System.out.println();
            System.out.println("modifiedInCurrentCommit: " + fileBoolean.modifiedInCurrentCommit);
            System.out.println();
            System.out.println("modifiedInWorkingDir: " + fileBoolean.modifiedInWorkingDir);
            System.out.println();*/

            /**
             * Merge.
             */
            if (fileBoolean.presentInSplit && fileBoolean.presentInGivenCommit && fileBoolean.presentInCurrentCommit && fileBoolean.modifiedInGivenCommit && !fileBoolean.modifiedInCurrentCommit) {
                head.copyFile(givenHead, fileName, fileName);
                currentStage.add(fileName);
            } else if (fileBoolean.presentInSplit && fileBoolean.presentInGivenCommit && fileBoolean.presentInCurrentCommit && !fileBoolean.modifiedInGivenCommit && fileBoolean.modifiedInCurrentCommit) {
            } else if (fileBoolean.presentInSplit && fileBoolean.presentInGivenCommit && fileBoolean.presentInCurrentCommit && fileBoolean.modifiedInGivenCommit && fileBoolean.modifiedInCurrentCommit && !head.modified(givenHead, fileName)) {
            } else if (fileBoolean.presentInSplit && !fileBoolean.presentInGivenCommit && !fileBoolean.presentInCurrentCommit) {
            } else if (!fileBoolean.presentInSplit && !fileBoolean.presentInGivenCommit && fileBoolean.presentInCurrentCommit) {
            } else if (!fileBoolean.presentInSplit && !fileBoolean.presentInCurrentCommit && fileBoolean.presentInGivenCommit) {
                head.copyFile(givenHead, fileName, fileName);
                currentStage.add(fileName);
            } else if (fileBoolean.presentInSplit && fileBoolean.presentInGivenCommit && !fileBoolean.presentInCurrentCommit && !fileBoolean.modifiedInGivenCommit) {
            } else if (fileBoolean.presentInSplit && fileBoolean.presentInCurrentCommit && !fileBoolean.modifiedInCurrentCommit && !fileBoolean.presentInGivenCommit) {
                File f = new File(fileName);
                f.delete();
                currentStage.getFilesMarkedForRemove().add(fileName);
            } else {
                boolean conflicted = false;
                String givenContent = "";
                String currentContent = "";
                if (fileBoolean.presentInSplit && fileBoolean.presentInGivenCommit && fileBoolean.presentInCurrentCommit && fileBoolean.modifiedInGivenCommit && fileBoolean.modifiedInCurrentCommit && head.modified(givenHead, fileName)) {
                    givenContent = Utils.readContentsAsString(givenHead.getFile(fileName));
                    currentContent = Utils.readContentsAsString(head.getFile(fileName));
                    conflicted = true;
                    hasConflict = true;
                } else if (fileBoolean.presentInSplit && fileBoolean.presentInCurrentCommit && !fileBoolean.presentInGivenCommit && fileBoolean.modifiedInCurrentCommit) {
                    currentContent = Utils.readContentsAsString(head.getFile(fileName));
                    conflicted = true;
                    hasConflict = true;
                } else if (fileBoolean.presentInSplit && fileBoolean.presentInGivenCommit && !fileBoolean.presentInCurrentCommit && fileBoolean.modifiedInGivenCommit) {
                    givenContent = Utils.readContentsAsString(givenHead.getFile(fileName));
                    conflicted = true;
                    hasConflict = true;
                } else if (!fileBoolean.presentInSplit && fileBoolean.presentInGivenCommit && fileBoolean.presentInCurrentCommit && head.modified(givenHead, fileName)) {
                    givenContent = Utils.readContentsAsString(givenHead.getFile(fileName));
                    currentContent = Utils.readContentsAsString(head.getFile(fileName));
                    conflicted = true;
                    hasConflict = true;
                } else {
                    givenContent = "";
                    currentContent = "";
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
                    File f = new File(fileName);
                    Utils.writeContents(f, sb.toString());
                    currentStage.add(fileName);
                }
            }
        }
        commitMsg("Merged " + given.getBranchName() + " into " + getBranchName() + ".");
        if (hasConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

}

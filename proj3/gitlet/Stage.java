package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Stage class.
 * @author Scott Shao
 */
public class Stage implements Serializable {
    /**
     * Head commit.
     */
    private Commit headCommit;
    /**
     * Files new on stage.
     */
    private ArrayList<String> FilesNewOnStage;
    /**
     * Files marked for removal.
     */
    private ArrayList<String> FilesMarkedForRemove;
    /**
     * Stage dir.
     */
    public static final String Stage_Dir = ".gitlet"
            + File.separator + "stage" + File.separator;
    /**
     * Key: File name.
     * Value: Directory.
     * null value means the file has not been added to commits
     * or the file has been modified since last commit.
     */
    private HashMap<String, String> stagedFiles;

    /**
     * Stage with commit.
     * @param givenHeadCommit Given commit.
     */
    public Stage(Commit givenHeadCommit) {
        this.headCommit = givenHeadCommit;
        FilesNewOnStage = new ArrayList<String>();
        FilesMarkedForRemove = new ArrayList<String>();
        stagedFiles = new HashMap<>();
    }

    /**
     * Stage.
     */
    public Stage() {
        FilesNewOnStage = new ArrayList<String>();
        FilesMarkedForRemove = new ArrayList<String>();
        stagedFiles = new HashMap<>();
    }

    /**
     * Get head commit.
     * @return Commit head.
     */
    public Commit getHeadCommit() {
        return this.headCommit;
    }

    /**
     * Get files new on stage.
     * @return ArrayList files new on stage.
     */
    public ArrayList<String> getFilesNewOnStage() {
        return FilesNewOnStage;
    }

    /**
     * Get files marked for removal.
     * @return Arraylist files marked for removal.
     */
    public ArrayList<String> getFilesMarkedForRemove() {
        return FilesMarkedForRemove;
    }

    /**
     * Get staged files.
     * @return Hash map of staged files.
     */
    public HashMap<String, String> getStagedFiles() {
        return this.stagedFiles;
    }

    /**
     * Add command.
     * @param fileName File name.
     */
    public void add(String fileName) {
        File f = new File(fileName);
        if (!f.exists()) {
            System.err.println("File does not exist.");
            return;
        }

        if (unchangedFromLastCommit(fileName)) {
            boolean contains = false;
            File stageArea = new File(Stage_Dir);
            for (File stagedFile : stageArea.listFiles()) {
                if (fileName.equals(stagedFile.getName())) {
                    contains = true;
                }
            }
            if (contains) {
                File stagedFile = new File(Stage_Dir + fileName);
                stagedFile.delete();
                stagedFiles.remove(fileName);
            }
        } else {
            File stagef = new File(Stage_Dir + Utils.getPlainFileName(fileName));
            Utils.writeContents(stagef, Utils.readContents(f));
            stagedFiles.put(stagef.getName(), null);
            FilesNewOnStage.add(stagef.getName());
        }

        if (headCommit.getCurrentStage() != null) {
            if (FilesMarkedForRemove.contains(fileName)) {
                FilesMarkedForRemove.remove(fileName);
            }
        }

    }

    /**
     * Rm command.
     * @param fileName File name.
     */
    public void rm(String fileName) {
        boolean staged = false;
        boolean tracked = false;
        if (stagedFiles.containsKey(fileName)) {
            staged = true;
            stagedFiles.remove(fileName);
            FilesNewOnStage.remove(fileName);
            File f = new File(Stage_Dir + fileName);
            f.delete();
        }
        if (headCommit.getFileMapper().containsKey(fileName)) {
            tracked = true;
            FilesMarkedForRemove.add(fileName);
            File f = new File(fileName);
            f.delete();
        }
        if (!staged && !tracked) {
            System.err.println("No reason to remove the file.");
        }
    }

    /**
     * Clear stage folder.
     */
    public void clearStageArea() {
        /*if (stagedFiles != null) {
            Set<String> fileNames = stagedFiles.keySet();
            for (String fileName : fileNames) {
                rm(fileName);
            }
        }*/
        File dir = new File(Stage_Dir);
        for (File file : dir.listFiles()) {
            file.delete();
        }
    }

    /**
     * Check if file changed since last commit.
     * @param fileName File name.
     * @return Boolean true of false.
     */
    private boolean unchangedFromLastCommit(String fileName) {
        File f = new File(fileName);
        if (headCommit.contains(fileName)) {
            File lastCommitFile = headCommit.getFile(fileName);
            if (Utils.readContentsAsString(lastCommitFile).
                    equals(Utils.readContentsAsString(f))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Staged Files ===" + "\n");
        for (String file : FilesNewOnStage) {
            sb.append(file + "\n");
        }
        sb.append("\n");
        sb.append("=== Removed Files ===" + "\n");
        for (String file : FilesMarkedForRemove) {
            sb.append(file + "\n");
        }
        sb.append("\n");
        sb.append("=== Modifications Not Staged For Commit ===" + "\n");
        sb.append("\n");
        sb.append("=== Untracked Files ===" + "\n");
        sb.append("\n");
        return sb.toString();
    }
}

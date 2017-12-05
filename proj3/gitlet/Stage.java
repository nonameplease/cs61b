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
    private ArrayList<String> filesNewOnStage;
    /**
     * Files marked for removal.
     */
    private ArrayList<String> filesMarkedForRemove;
    /**
     * Stage dir.
     */
    public static final String STAGEDIR = ".gitlet"
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
        filesNewOnStage = new ArrayList<String>();
        filesMarkedForRemove = new ArrayList<String>();
        stagedFiles = new HashMap<>();
    }

    /**
     * Stage.
     */
    public Stage() {
        filesNewOnStage = new ArrayList<String>();
        filesMarkedForRemove = new ArrayList<String>();
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
        return filesNewOnStage;
    }

    /**
     * Get files marked for removal.
     * @return Arraylist files marked for removal.
     */
    public ArrayList<String> getFilesMarkedForRemove() {
        return filesMarkedForRemove;
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
            File stageArea = new File(STAGEDIR);
            for (File stagedFile : stageArea.listFiles()) {
                if (fileName.equals(stagedFile.getName())) {
                    contains = true;
                }
            }
            if (contains) {
                File stagedFile = new File(STAGEDIR + fileName);
                stagedFile.delete();
                getStagedFiles().remove(fileName);
            }
        } else {
            File stagef = new File(STAGEDIR
                    + Utils.getPlainFileName(fileName));
            Utils.writeContents(stagef, Utils.readContents(f));
            getStagedFiles().put(stagef.getName(), null);
            getFilesNewOnStage().add(stagef.getName());
        }

        if (getHeadCommit().getCurrentStage() != null) {
            if (getFilesMarkedForRemove().contains(fileName)) {
                getFilesMarkedForRemove().remove(fileName);
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
        if (getStagedFiles().containsKey(fileName)) {
            staged = true;
            getStagedFiles().remove(fileName);
            getFilesNewOnStage().remove(fileName);
            File f = new File(STAGEDIR + fileName);
            f.delete();
        }
        if (getHeadCommit().getFileMapper().containsKey(fileName)) {
            tracked = true;
            getFilesMarkedForRemove().add(fileName);
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
        File dir = new File(STAGEDIR);
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
        if (getHeadCommit().getFileMapper().containsKey(fileName)
                && getHeadCommit().getFileMapper().get(fileName) != null) {
            File lastCommitFile = getHeadCommit().getFile(fileName);
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
        for (String file : getFilesNewOnStage()) {
            sb.append(file + "\n");
        }
        sb.append("\n");
        sb.append("=== Removed Files ===" + "\n");
        for (String file : getFilesMarkedForRemove()) {
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

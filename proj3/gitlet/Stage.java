package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Stage implements Serializable {
    private Commit headCommit;
    private ArrayList<String> FilesNewOnStage;
    private ArrayList<String> FilesMarkedForRemove;
    public static final String Stage_Dir = ".gitlet" + File.separator + "stage" + File.separator;
    /**
     * Key: File name.
     * Value: Directory.
     * null value means the file has not been added to commits
     * or the file has been modified since last commit.
     */
    private HashMap<String, String> stagedFiles;

    public Stage(Commit headCommit) {
        this.headCommit = headCommit;
        FilesNewOnStage = new ArrayList<String>();
        FilesMarkedForRemove = new ArrayList<String>();
        stagedFiles = new HashMap<>();
    }

    public Stage() {
        FilesNewOnStage = new ArrayList<String>();
        FilesMarkedForRemove = new ArrayList<String>();
        stagedFiles = new HashMap<>();
    }

    public Commit getHeadCommit() {
        return this.headCommit;
    }

    public ArrayList<String> getFilesNewOnStage() {
        return FilesNewOnStage;
    }

    public ArrayList<String> getFilesMarkedForRemove() {
        return FilesMarkedForRemove;
    }

    public HashMap<String, String> getStagedFiles() {
        return this.stagedFiles;
    }

    public void add(String fileName) {
        File f = new File(fileName);
        if (!f.exists()) {
            System.err.println("File does not exist.");
            return;
        }

        if (unchangedFromLastCommit(fileName)) {
            System.err.println("No changes added to the commit.");
        }

        File stagef = new File(Stage_Dir + Utils.getPlainFileName(fileName));
        Utils.writeContents(stagef, Utils.readContents(f));
        stagedFiles.put(stagef.getName(), null);
        FilesNewOnStage.add(stagef.getName());
    }

    public void rm(String fileName) {
        boolean staged = false;
        boolean tracked = false;
        if (stagedFiles.containsKey(fileName)) {
            staged = true;
            stagedFiles.remove(fileName);
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

        /*if (stagedFiles.containsKey(fileName)) {
            stagedFiles.remove(fileName);
            FilesMarkedForRemove.add(fileName);
            try {
                Utils.restrictedDelete(fileName);
            } catch (IllegalArgumentException e) {
                File f = new File(fileName);
                f.delete();
            }
        } else {
            System.err.println("No reason to remove the file.");
        }*/
    }

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

    private boolean unchangedFromLastCommit(String fileName) {
        File f = new File(fileName);
        if (headCommit.contains(fileName)) {
            File lastCommitFile = headCommit.getFile(fileName);
            if (Utils.readContentsAsString(lastCommitFile).equals(Utils.readContentsAsString(f))) {
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
        return sb.toString();
    }
}

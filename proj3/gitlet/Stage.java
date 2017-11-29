package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Stage implements Serializable {
    private Commit headCommit;
    private ArrayList<String> FilesNewOnStage;
    private ArrayList<String> FilesMarkedForRemove;
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

        stagedFiles.put(fileName, null);
        FilesNewOnStage.add(fileName);
    }

    public void rm(String fileName) {
        if (stagedFiles.containsKey(fileName)) {
            stagedFiles.remove(fileName);
            FilesMarkedForRemove.add(fileName);
        } else {
            System.err.println("No reason to remove the file.");
        }
    }

    private boolean unchangedFromLastCommit(String fileName) {
        File f = new File(fileName);
        if (headCommit.contains(fileName)) {
            File backup = headCommit.getFile(fileName);
            if (backup.lastModified() > f.lastModified()) {
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
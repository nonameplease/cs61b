package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class Commit implements Serializable {
    private String hashValue;
    private Commit parent;
    private Date timeStamp;
    private String message;
    private HashMap<String, String> fileMapper;
    private static final String Commit_Dir = ".gitlet" + File.separator + "commits" + File.separator;
    private String thisCommit_Dir;
    private Stage currentStage;

    public Commit(Commit parent, Date timeStamp, String message, HashMap<String, String> fileMapper) {
        this.parent = parent;
        this.timeStamp = timeStamp;
        this.message = message;
        this.fileMapper = fileMapper;

        /**
         * Create unique hashValue for each commit.
         */
        this.hashValue = Utils.sha1(fileMapper, parent, message, timeStamp);
    }

    /**
     * Default commit for INIT.
     */
    public Commit() {
        this(null, new Date(0), "initial commit", null);
    }

    public Commit (Stage currentStage) {
        this.timeStamp = new Date();
        this.currentStage = currentStage;
        if (currentStage != null) {
            fileMapper = currentStage.getStagedFiles();
            parent = currentStage.getHeadCommit();
        } else {
            fileMapper = new HashMap<>();
            parent = this;
        }
    }

    public Commit(Stage currentStage, String message) {
        this(currentStage);
        this.message = message;
        this.hashValue = Utils.sha1(fileMapper.keySet().toString(), parent.toString(), message, timeStamp.toString());
        thisCommit_Dir = Commit_Dir + timeStamp.hashCode() + File.separator;
        //saveFiles();
        saveFilesWithParentCommit();
        if (currentStage != null) {
            currentStage.clearStageArea();
        }
    }

    /*public String findSplitPoint(Commit given) {
        if (given == null) {
            return null;
        }

        if (this.equals(given)) {
            return this.hashValue;
        }

        if (this.timeStamp >= given.timeStamp) {
            return given.findSplitPoint(this.parent);
        } else {
            return this.findSplitPoint(given.parent);
        }
    }*/

    public String getHashValue() {
        return hashValue;
    }

    public Commit getParent() {
        return parent;
    }

    public String getTimeStamp() {
        return timeStamp.toString();
    }

    public String getMessage() {
        return message;
    }

    public HashMap<String, String> getFileMapper() {
        return fileMapper;
    }

    public String getThisCommit_Dir() {
        return thisCommit_Dir;
    }

    /**
     * Potentially break point.
     * @param o An object, ideally a Commit.
     * @return A boolean value decide if two are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Commit) {
            Commit given = (Commit) o;
            return this.hashValue == given.hashValue;
        }
        return false;
    }

    public boolean contains(String fileName) {
        if (fileMapper == null) {
            return false;
        }
        return fileMapper.containsKey(fileName);
    }

    public File getFile(String fileName) {
        String path = fileMapper.get(fileName) + fileName;
        File f = new File(path);
        return f;
    }

    public void copyFile(Commit given, String fileNameCopyFrom, String fileNameCopyTo) {
        File copyFrom = given.getFile(fileNameCopyFrom);
        String path = thisCommit_Dir + fileNameCopyTo;
        File copyTo = new File(path);
        Utils.writeContents(copyTo, Utils.readContents(copyFrom));
        this.fileMapper.put(fileNameCopyTo, thisCommit_Dir);
    }

    public boolean modified(Commit given, String fileName) {
        File givenFile = given.getFile(fileName);
        File thisFile = this.getFile(fileName);
        return !(Utils.readContents(givenFile) == Utils.readContents(thisFile));
    }

    private void saveFiles() {
        File dir = new File(thisCommit_Dir);
        dir.mkdirs();
        if (fileMapper == null) {
            return;
        }
        for (String fileName : fileMapper.keySet()) {
            String fileToSave;
            if (fileMapper.get(fileName) == null) {
                fileToSave = Stage.Stage_Dir + fileName;
            } else {
                fileToSave = fileMapper.get(fileName) + fileName;
            }
            String path = thisCommit_Dir + fileName;
            File f = new File(path);
            byte[] content = Utils.readContents(new File(fileToSave));
            Utils.writeContents(f, content);
            fileMapper.put(fileName, thisCommit_Dir);
        }
    }

    /**
     * Still need to work on it.
     * Current difficulties: Unable to track blobs that are present in both commits
     * but has not been modified since last commit.
     * What should happen: Instead of copy the file to current commit dir,
     * the dir of the file should be added to the current commit fileMapper so that
     * when this file is being checkout with this commit it will not cause
     * no file error.
     */
    private void saveFilesWithParentCommit() {
        File dir = new File(thisCommit_Dir);
        dir.mkdirs();
        if (fileMapper == null) {
            return;
        }
        /**
         * Parent file mapper.
         */
        HashMap<String, String> parentFileMapper = parent.getFileMapper();
        for (String fileName : parentFileMapper.keySet()) {
            if (!fileMapper.containsKey(fileName)) {
                fileMapper.put(fileName, parentFileMapper.get(fileName));
            }
        }


        for (String fileName : fileMapper.keySet()) {
            String fileToSave;
            if (fileMapper.get(fileName) == null) {
                fileToSave = Stage.Stage_Dir + fileName;
            } else {
                fileToSave = fileMapper.get(fileName) + fileName;
            }
            Path file = Paths.get(fileName);
            String path = thisCommit_Dir + file.getFileName();
            File f = new File(path);
            byte[] content = Utils.readContents(new File(fileToSave));
            Utils.writeContents(f, content);
            fileMapper.put(file.getFileName().toString(), thisCommit_Dir);
        }
    }

    public void restoreFiles(String fileName) {
        File copyFrom = getFile(fileName);
        File copyTo = new File(fileName);
        Utils.writeContents(copyTo, Utils.readContents(copyFrom));
    }

    public void restoreAllFiles() {
        Collection<String> fileNames = fileMapper.keySet();
        for (String fileName : fileNames) {
            restoreFiles(fileName);
        }
    }

    public void setTimeStamp(Date newTime) {
        timeStamp = newTime;
    }


    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        String formattedDate = dateFormat.format(this.timeStamp);
        StringBuilder sb = new StringBuilder();
        sb.append("===\n");
        sb.append("commit " + this.hashValue + "\n");
        sb.append("Date: " + formattedDate + "\n");
        sb.append(this.message);
        return sb.toString();
    }

}

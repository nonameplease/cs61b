package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

/**
 * Commit class.
 * @author Scott Shao
 */
public class Commit implements Serializable {
    /**
     * Commit ID.
     */
    private String hashValue;
    /**
     * Commit parent.
     */
    private Commit parent;
    /**
     * Time stamp.
     */
    private Date timeStamp;
    /**
     * Commit message.
     */
    private String message;
    /**
     * File mapper.
     * Key: file name. Value: dir to file, not file path.
     * To get file path: fileMapper.get(fileName) + fileName.
     */
    private HashMap<String, String> fileMapper;
    /**
     * Commit folder dir.
     */
    private static final String COMMITDIR = ".gitlet"
            + File.separator + "commits" + File.separator;
    /**
     * Commit dir.
     */
    private String thisCommitDir;
    /**
     * Current stage.
     */
    private Stage currentStage;

    /**
     * Commit without message.
     * @param givenCurrentStage Current stage.
     */
    public Commit(Stage givenCurrentStage) {
        this.timeStamp = new Date();
        this.currentStage = givenCurrentStage;
        if (givenCurrentStage != null) {
            fileMapper = givenCurrentStage.getStagedFiles();
            parent = givenCurrentStage.getHeadCommit();
        } else {
            fileMapper = new HashMap<>();
            parent = this;
        }
    }

    /**
     *
     * Commit.
     * @param givenCurrentStage Current stage.
     * @param givenMessage Commit message.
     */
    public Commit(Stage givenCurrentStage, String givenMessage) {
        this(givenCurrentStage);
        this.message = givenMessage;
        this.hashValue = Utils.sha1(fileMapper.keySet().toString(),
                parent.toString(), givenMessage, timeStamp.toString());
        thisCommitDir = COMMITDIR + timeStamp.hashCode() + File.separator;
        saveFilesWithParentCommit();
        if (givenCurrentStage != null) {
            givenCurrentStage.clearStageArea();
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

    /**
     * Get the ID of the commit.
     * @return ID of the commit.
     */
    public String getHashValue() {
        return hashValue;
    }

    /**
     * Get the parent of the commit.
     * @return Parent of the commit.
     */
    public Commit getParent() {
        return parent;
    }

    /**
     * Get the time stamp.
     * @return The time stamp.
     */
    public String getTimeStamp() {
        return timeStamp.toString();
    }

    /**
     * Get the commit message.
     * @return Commit message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the fileMapper.
     * @return FileMapper.
     */
    public HashMap<String, String> getFileMapper() {
        return fileMapper;
    }

    /**
     * Get the commit dir.
     * @return Commit dir.
     */
    public String getThisCommitDir() {
        return thisCommitDir;
    }

    /**
     * Get current stage.
     * @return Current stage.
     */
    public Stage getCurrentStage() {
        return currentStage;
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

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Check if the commit contains fileName.
     * @param fileName File name.
     * @return Boolean true or false.
     */
    public boolean contains(String fileName) {
        if (fileMapper == null) {
            return false;
        }
        return fileMapper.containsKey(fileName);
    }

    /**
     * Get file with fileName.
     * @param fileName File name.
     * @return The file.
     */
    public File getFile(String fileName) {
        String path = fileMapper.get(fileName) + fileName;
        File f = new File(path);
        return f;
    }

    /**
     * Copy file content.
     * @param given Given commit.
     * @param fileNameCopyFrom Copy to.
     * @param fileNameCopyTo Copy from.
     */
    public void copyFile(Commit given, String fileNameCopyFrom,
                         String fileNameCopyTo) {
        File copyFrom = given.getFile(fileNameCopyFrom);
        String path = fileNameCopyTo;
        File copyTo = new File(path);
        Utils.writeContents(copyTo, Utils.readContents(copyFrom));
        this.fileMapper.put(fileNameCopyTo, null);
    }

    /**
     * Return if file with fileName is modified
     * in commit given.
     * @param given Given commit.
     * @param fileName File name.
     * @return Boolean true or false.
     */
    public boolean modified(Commit given, String fileName) {
        File givenFile = given.getFile(fileName);
        File thisFile = getFile(fileName);
        return !(Utils.readContentsAsString(givenFile).
                equals(Utils.readContentsAsString(thisFile)));
    }

    /**
     * Base for saveFilesWithParentCommit.
     */
    private void saveFiles() {
        File dir = new File(thisCommitDir);
        dir.mkdirs();
        if (fileMapper == null) {
            return;
        }
        for (String fileName : fileMapper.keySet()) {
            String fileToSave;
            if (fileMapper.get(fileName) == null) {
                fileToSave = Stage.STAGEDIR + fileName;
            } else {
                fileToSave = fileMapper.get(fileName) + fileName;
            }
            String path = thisCommitDir + fileName;
            File f = new File(path);
            byte[] content = Utils.readContents(new File(fileToSave));
            Utils.writeContents(f, content);
            fileMapper.put(fileName, thisCommitDir);
        }
    }

    /**
     * Still need to work on it.
     * Current difficulties: Unable to track blobs that are present
     * in both commits
     * but has not been modified since last commit.
     * What should happen: Instead of copy the file to current commit dir,
     * the dir of the file should be added to the current commit fileMapper
     * so that when this file is being checkout with this commit it will
     * not cause no file error.
     */
    private void saveFilesWithParentCommit() {
        File dir = new File(thisCommitDir);
        dir.mkdirs();
        if (fileMapper == null) {
            return;
        }
        HashMap<String, String> parentFileMapper = parent.getFileMapper();
        for (String fileName : parentFileMapper.keySet()) {
            if (!fileMapper.containsKey(fileName)
                    && !currentStage.getFilesMarkedForRemove().
                    contains(fileName)) {
                fileMapper.put(fileName, parentFileMapper.get(fileName));
            }
        }


        for (String fileName : fileMapper.keySet()) {
            String fileToSave;
            if (fileMapper.get(fileName) == null) {
                fileToSave = Stage.STAGEDIR + fileName;
            } else {
                fileToSave = fileMapper.get(fileName) + fileName;
            }
            Path file = Paths.get(fileName);
            String path = thisCommitDir + file.getFileName();
            File f = new File(path);
            byte[] content = Utils.readContents(new File(fileToSave));
            Utils.writeContents(f, content);
            fileMapper.put(file.getFileName().toString(), thisCommitDir);
        }
    }

    /**
     * Restore given file.
     * @param fileName Given file name.
     */
    public void restoreFiles(String fileName) {
        File copyFrom = getFile(fileName);
        File copyTo = new File(fileName);
        Utils.writeContents(copyTo, Utils.readContents(copyFrom));
    }

    /**
     * Restore all files.
     */
    public void restoreAllFiles() {
        Collection<String> fileNames = fileMapper.keySet();
        for (String fileName : fileNames) {
            restoreFiles(fileName);
        }
    }

    /**
     * Set timestamp.
     * @param newTime The new timestamp.
     */
    public void setTimeStamp(Date newTime) {
        timeStamp = newTime;
    }


    @Override
    public String toString() {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        String formattedDate = dateFormat.format(this.timeStamp);
        StringBuilder sb = new StringBuilder();
        sb.append("===\n");
        sb.append("commit " + this.hashValue + "\n");
        sb.append("Date: " + formattedDate + "\n");
        sb.append(this.message);
        return sb.toString();
    }

}

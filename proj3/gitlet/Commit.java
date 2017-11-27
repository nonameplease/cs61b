package gitlet;

import java.io.Serializable;
import java.util.HashMap;

public class Commit implements Serializable {
    private String hashValue;
    private Commit parent;
    private double timeStamp;
    private String message;
    private HashMap<String, String> fileMapper;

    public Commit(Commit parent, double timeStamp, String message, HashMap<String, String> fileMapper) {
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
        this(null, 0, "initial commit", null);
    }

    public String findSplitPoint(Commit given) {
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
    }

    public String getHashValue() {
        return hashValue;
    }

    public Commit getParent() {
        return parent;
    }

    public double getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public HashMap<String, String> getFileMapper() {
        return fileMapper;
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

}

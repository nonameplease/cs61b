package gitlet;

/**
 * Containing the state of each commit.
 * @author Scott Shao
 */
public class FileBoolean {
    /**
     * Boolean.
     */
    private String fileName;
    /**
     * Boolean.
     */
    private boolean presentInSplit;
    /**
     * Boolean.
     */
    private boolean presentInGivenCommit;
    /**
     * Boolean.
     */
    private boolean presentInCurrentCommit;
    /**
     * Boolean.
     */
    private boolean modifiedInGivenCommit;
    /**
     * Boolean.
     */
    private boolean modifiedInCurrentCommit;

    /**
     * Default constructor.
     * @param givenFileName Given file name.
     */
    public FileBoolean(String givenFileName) {
        fileName = givenFileName;
        presentInSplit = false;
        presentInGivenCommit = false;
        presentInCurrentCommit = false;
        modifiedInGivenCommit = false;
        modifiedInCurrentCommit = false;
    }

    /**
     * Get file name.
     * @return String.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Get present in split.
     * @return Boolean.
     */
    public boolean getPresentInSplit() {
        return presentInSplit;
    }

    /**
     * Get present in given commit.
     * @return Boolean.
     */
    public boolean getPresentInGivenCommit() {
        return presentInGivenCommit;
    }

    /**
     * Get present in current commit.
     * @return Boolean.
     */
    public boolean getPresentInCurrentCommit() {
        return presentInCurrentCommit;
    }

    /**
     * Get modified in given commit.
     * @return Boolean.
     */
    public boolean getModifiedInGivenCommit() {
        return modifiedInGivenCommit;
    }

    /**
     * Get modified in current commit.
     * @return Boolean.
     */
    public boolean getModifiedInCurrentCommit() {
        return modifiedInCurrentCommit;
    }

    /**
     * Set present in split point.
     * @param input Boolean.
     */
    public void setPresentInSplit(boolean input) {
        presentInSplit = input;
    }

    /**
     * Set present in given commit.
     * @param input Boolean.
     */
    public void setPresentInGivenCommit(boolean input) {
        presentInGivenCommit = input;
    }

    /**
     * Set present in current commit.
     * @param input Boolean.
     */
    public void setPresentInCurrentCommit(boolean input) {
        presentInCurrentCommit = input;
    }

    /**
     * Set modified in given commit.
     * @param input Boolean.
     */
    public void setModifiedInGivenCommit(boolean input) {
        modifiedInGivenCommit = input;
    }

    /**
     * Set modified in current commit.
     * @param input Boolean.
     */
    public void setModifiedInCurrnetCommit(boolean input) {
        modifiedInCurrentCommit = input;
    }
}

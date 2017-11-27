package gitlet;

import java.io.File;
import java.io.IOException;

/**
 * Potentially useless.
 */

public class FileWriter {
    public void createFile(String fileName, String content) {
        File f = new File(fileName);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Utils.writeContents(f, content.getBytes());
    }

    public void createDirectory(String directoryName) {
        File f = new File(directoryName);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

}

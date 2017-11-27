package gitlet.Commands;

public class AddCommand implements CommandInterface {
    private String fileName;

    public AddCommand(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean isDangerous() {
        return false;
    }

    @Override
    public boolean execute() {
        /**
         * Failure case.
         */
        if (file does not exist) {
            System.err.println("File does not exist.");
        }
    }
}

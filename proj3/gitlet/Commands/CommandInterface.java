package gitlet.Commands;

public interface CommandInterface {
    boolean isDangerous();
    boolean execute();
}

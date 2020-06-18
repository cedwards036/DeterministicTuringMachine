/**
 * A command to be executed when a DTM halts
 */
public interface HaltingCommand {
    void execute(String haltingState);

    String getHaltingState();
}

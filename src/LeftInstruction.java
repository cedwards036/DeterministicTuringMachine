/**
 * A write instruction that moves the tape to the left after writing the character.
 */
public class LeftInstruction extends Instruction {
    private final char charToWrite;

    public LeftInstruction(char charToWrite) {
        super(charToWrite);
        this.charToWrite = charToWrite;
    }

    @Override
    public void execute(Tape tape) {
        tape.writeToCurrentCell(charToWrite);
        tape.moveLeft();
    }
}

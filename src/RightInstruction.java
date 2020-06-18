/**
 * A write instruction that moves the tape to the right after writing the character.
 */
public class RightInstruction extends Instruction {
    private final char charToWrite;

    public RightInstruction(char charToWrite) {
        super(charToWrite);
        this.charToWrite = charToWrite;
    }

    @Override
    public void execute(Tape tape) {
        tape.writeToCurrentCell(charToWrite);
        tape.moveRight();
    }
}

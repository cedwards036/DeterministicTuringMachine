/**
 * An write instruction to be executed on a Tape running through a DTM. Instructions typically consist of
 * a character to write to the tape and (optionally) a direction in which to move
 */
public abstract class Instruction {
    private final char charToWrite;

    /**
     * Default constructor
     *
     * @param charToWrite the character to write to the tape
     */
    public Instruction(char charToWrite) {
        this.charToWrite = charToWrite;
    }

    public abstract void execute(Tape tape);

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            Instruction otherInstruction = (Instruction) obj;
            return charToWrite == otherInstruction.charToWrite;
        }
    }
}

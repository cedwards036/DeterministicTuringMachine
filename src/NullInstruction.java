/**
 * A write instruction that does nothing to the Tape
 */
public class NullInstruction extends Instruction {
    public NullInstruction() {
        super('0');
    }

    @Override
    public void execute(Tape tape) {
    }
}

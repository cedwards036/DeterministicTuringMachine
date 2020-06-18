import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InstructionTest {

    @Test
    void rightInstructionMovesTapeToTheRight() {
        Tape tape = TapeBuilder.buildFrom("01", 'b');
        Instruction instruction = new RightInstruction('9');
        instruction.execute(tape);
        assertEquals('1', tape.getCurrentCellValue());
    }

    @Test
    void leftInstructionMovesTapeToTheLeft() {
        Tape tape = TapeBuilder.buildFrom("01", 'b');
        tape.moveRight();
        Instruction instruction = new LeftInstruction('9');
        instruction.execute(tape);
        assertEquals('0', tape.getCurrentCellValue());
    }

    @Test
    void instructionsWriteToTheTape() {
        Tape tape = TapeBuilder.buildFrom("01", 'b');
        new RightInstruction('x').execute(tape);
        assertEquals('1', tape.getCurrentCellValue());
        new LeftInstruction('y').execute(tape);
        assertEquals('x', tape.getCurrentCellValue());
        tape.moveRight();
        assertEquals('y', tape.getCurrentCellValue());
    }

    @Test
    void nullInstructionDoesNothing() {
        Tape tape = TapeBuilder.buildFrom("1", 'b');
        Instruction instruction = new NullInstruction();
        instruction.execute(tape);
        assertEquals('1', tape.getCurrentCellValue());
    }
}

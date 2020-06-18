import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TapeBuilderTest {

    @Test
    void createsEmptyTapeFromEmptyString() {
        Tape tape = TapeBuilder.buildFrom("", 'b');
        assertEquals('b', tape.getCurrentCellValue());
    }

    @Test
    void createsSingleCharTapeFromLength1String() {
        Tape tape = TapeBuilder.buildFrom("0", 'b');
        assertEquals('0', tape.getCurrentCellValue());
    }

    @Test
    void createsMultiCharTapeFromMultiCharString() {
        Tape tape = TapeBuilder.buildFrom("12345", 'b');
        assertEquals('1', tape.getCurrentCellValue());
        tape.moveRight();
        assertEquals('2', tape.getCurrentCellValue());
        tape.moveRight();
        assertEquals('3', tape.getCurrentCellValue());
        tape.moveRight();
        assertEquals('4', tape.getCurrentCellValue());
        tape.moveRight();
        assertEquals('5', tape.getCurrentCellValue());
    }
}

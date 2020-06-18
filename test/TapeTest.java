import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TapeTest {

    private void moveRightNCells(Tape tape, int n) {
        for (int i = 0; i < n; i++) {
            tape.moveRight();
        }
    }

    private void moveLeftNCells(Tape tape, int n) {
        for (int i = 0; i < n; i++) {
            tape.moveLeft();
        }
    }

    @Test
    void gettingCurrentCharFromEmptyTapeReturnsBlank() {
        Tape tape = new Tape('b');
        assertEquals('b', tape.getCurrentCellValue());
    }

    @Test
    void tapeTreatsLeftmostNonblankCellAsCurrentCellAtFirst() {
        Tape tape = new Tape('b');
        tape.appendCell('0');
        tape.appendCell('1');
        tape.appendCell('2');
        assertEquals('0', tape.getCurrentCellValue());
    }

    @Test
    void whenTapeMovesRightCurrentCharUpdatesToCharOnTheRight() {
        Tape tape = new Tape('b');
        tape.appendCell('0');
        tape.appendCell('1');
        tape.appendCell('2');
        tape.moveRight();
        assertEquals('1', tape.getCurrentCellValue());
        tape.moveRight();
        assertEquals('2', tape.getCurrentCellValue());
    }

    @Test
    void whenTapeMovesLeftCurrentCharUpdatesToCharOnTheLeft() {
        Tape tape = new Tape('b');
        tape.appendCell('0');
        tape.appendCell('1');
        tape.appendCell('2');
        moveRightNCells(tape, 2);
        tape.moveLeft();
        assertEquals('1', tape.getCurrentCellValue());
        tape.moveLeft();
        assertEquals('0', tape.getCurrentCellValue());
    }

    @Test
    void movingRightPastTheRightmostValueCellAddsNewBlankCellsToTheTape() {
        Tape tape = new Tape('b');
        tape.appendCell('0');
        moveRightNCells(tape, 2);
        assertEquals('b', tape.getCurrentCellValue());
        tape.moveLeft();
        assertEquals('b', tape.getCurrentCellValue());
        tape.moveLeft();
        assertEquals('0', tape.getCurrentCellValue());
    }

    @Test
    void movingLeftPastTheLeftmostValueCellAddsNewBlankCellsToTheTape() {
        Tape tape = new Tape('b');
        tape.appendCell('0');
        moveLeftNCells(tape, 2);
        assertEquals('b', tape.getCurrentCellValue());
        tape.moveRight();
        assertEquals('b', tape.getCurrentCellValue());
        tape.moveRight();
        assertEquals('0', tape.getCurrentCellValue());
    }

    @Test
    void writingToAnEmptyTapeAddsACellWithTheGivenValue() {
        Tape tape = new Tape('b');
        tape.writeToCurrentCell('1');
        assertEquals('1', tape.getCurrentCellValue());
    }

    @Test
    void writingToACellUpdatesTheCellValue() {
        Tape tape = new Tape('b');
        tape.appendCell('0');
        tape.writeToCurrentCell('1');
        assertEquals('1', tape.getCurrentCellValue());
    }

    @Test
    void newCellsAreAppendedToTheRightOfTheRightmostNonBlankCell() {
        Tape tape = new Tape('b');
        tape.appendCell('0');
        moveRightNCells(tape, 2);
        tape.writeToCurrentCell('1');
        tape.appendCell('0');
        moveLeftNCells(tape, 2);
        assertEquals('0', tape.getCurrentCellValue());
        tape.moveRight();
        assertEquals('b', tape.getCurrentCellValue());
        tape.moveRight();
        assertEquals('1', tape.getCurrentCellValue());
        tape.moveRight();
        assertEquals('0', tape.getCurrentCellValue());
    }

    @Test
    void writingTheBlankCharacterDoesNotUpdateTheRightmostCellMarker() {
        Tape tape = new Tape('b');
        tape.appendCell('0');
        tape.moveRight();
        tape.writeToCurrentCell('b');
        tape.appendCell('1');
        assertEquals('1', tape.getCurrentCellValue());
        tape.moveLeft();
        assertEquals('0', tape.getCurrentCellValue());
    }

    @Test
    void testToStringMethodWorksImmediatelyAfterInitialization() {
        assertEquals("", TapeBuilder.buildFrom("", 'b').toString());
        assertEquals("012415", TapeBuilder.buildFrom("012415", 'b').toString());
    }

    @Test
    void testToStringMethodWorksAfterWritingToCellsPastTheLeftAndRightEndsOfTheTape() {
        Tape tape = TapeBuilder.buildFrom("012345", 'b');
        moveRightNCells(tape, 8);
        tape.writeToCurrentCell('8');
        assertEquals("012345bb8", tape.toString());
        moveLeftNCells(tape, 14);
        tape.writeToCurrentCell('7');
        moveLeftNCells(tape, 20);
        assertEquals("7bbbbb012345bb8", tape.toString());
    }
}

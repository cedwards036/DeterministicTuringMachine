/**
 * A tape to be used as input to a DTM
 */
public class Tape {
    private final char blankChar;
    private Cell currentCell;
    private Cell rightmostCell;
    private Cell leftmostCell;
    private int tapeIndex;
    private int tapeSize;

    /**
     * Default constructor
     *
     * @param blankChar the character to be treated as a "blank" by this tape
     */
    public Tape(char blankChar) {
        this.blankChar = blankChar;
        this.tapeIndex = 0;
        this.tapeSize = 0;
    }

    public char getCurrentCellValue() {
        if (currentCell == null) {
            return blankChar;
        }
        return currentCell.getValue();
    }

    /**
     * Write the given value to the current cell
     *
     * @param newValue the value to write to the current cell
     */
    public void writeToCurrentCell(char newValue) {
        if (currentCell == null) {
            appendCell(newValue);
        } else {
            currentCell.setValue(newValue);
            if (newValue != blankChar) {
                if (tapeIndex >= tapeSize) {
                    rightmostCell = currentCell;
                    tapeSize = tapeIndex + 1;
                } else if (tapeIndex < 0) {
                    leftmostCell = currentCell;
                    tapeSize = tapeSize - tapeIndex;
                    tapeIndex = 0;
                }
            }
        }
    }

    /**
     * Append a cell with the given value to the right-most non-blank cell in the tape
     *
     * @param cellValue the value to write to the new cell
     */
    public void appendCell(char cellValue) {
        Cell newCell = new Cell(cellValue);
        if (rightmostCell == null) {
            leftmostCell = newCell;
            rightmostCell = newCell;
            currentCell = newCell;
        } else if (rightmostCell.getRightCell() == null) {
            newCell.setLeftCell(rightmostCell);
            rightmostCell.setRightCell(newCell);
            rightmostCell = newCell;
        } else {
            rightmostCell.getRightCell().setValue(cellValue);
        }
        tapeSize++;
    }

    /**
     * Move the tapehead to the right, such that the "current cell" is now one cell to the right of the previous
     * current cell
     */
    public void moveRight() {
        if (currentCell.getRightCell() == null) {
            Cell blankCell = new Cell(blankChar);
            blankCell.setLeftCell(currentCell);
            currentCell.setRightCell(blankCell);
        }
        currentCell = currentCell.getRightCell();
        tapeIndex += 1;
    }

    /**
     * Move the tapehead to the left, such that the "current cell" is now one cell to the left of the previous
     * current cell
     */
    public void moveLeft() {
        if (currentCell.getLeftCell() == null) {
            Cell blankCell = new Cell(blankChar);
            blankCell.setRightCell(currentCell);
            currentCell.setLeftCell(blankCell);
        }
        currentCell = currentCell.getLeftCell();
        tapeIndex -= 1;
    }

    public int getCurrentCellIndex() {
        return tapeIndex;
    }

    public char getBlankChar() {
        return blankChar;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Cell currentCell = leftmostCell;
        for (int i = 0; i < tapeSize; i++) {
            stringBuilder.append(currentCell.getValue());
            currentCell = currentCell.getRightCell();
        }
        return stringBuilder.toString();
    }

    /**
     * A cell in the tape with a single char for a value
     */
    private static class Cell {
        private char value;
        private Cell leftCell;
        private Cell rightCell;

        public Cell(char value) {
            this.value = value;
        }

        public char getValue() {
            return value;
        }

        public void setValue(char value) {
            this.value = value;
        }

        public Cell getLeftCell() {
            return leftCell;
        }

        public void setLeftCell(Cell leftCell) {
            this.leftCell = leftCell;
        }

        public Cell getRightCell() {
            return rightCell;
        }

        public void setRightCell(Cell rightCell) {
            this.rightCell = rightCell;
        }
    }
}

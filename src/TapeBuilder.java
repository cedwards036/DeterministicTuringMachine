/**
 * A utility class that builds a Tape object given a string of characters
 */
public class TapeBuilder {

    /**
     * Construct a tape consisting of the characters of the given string.
     *
     * @param tapeStr   the string of characters from which to construct the tape
     * @param blankChar the character to be treated as a "blank" character by the tape
     * @return a tape consisting of the characters of the given string
     */
    public static Tape buildFrom(String tapeStr, char blankChar) {
        Tape tape = new Tape(blankChar);
        for (int i = 0; i < tapeStr.length(); i++) {
            tape.appendCell(tapeStr.charAt(i));
        }
        return tape;
    }
}

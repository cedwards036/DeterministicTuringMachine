import java.util.ArrayList;

/**
 * A parser that converts a well-formed input string into a StateTransitionSpec
 */
public class StateTransitionParser {

    private static final String commentMarker = "//";

    /**
     * Parse the given input string into a StateTransitionSpec, if possible
     *
     * @param input the string input to parse
     * @return a StateTransitionSpec with attributes specified in the input string
     * @throws InvalidInputException if input string is malformed, or cannot otherwise be parsed
     */
    public static StateTransitionSpec parse(String input) {
        if (input.equals("")) {
            throw new InvalidInputException("Input cannot be blank");
        } else {
            return new ParserInstance(input).parse();
        }
    }

    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String errorMessage) {
            super(errorMessage);
        }
    }

    /**
     * A parser that parses a non-empty input string into a StateTransitionSpec
     */
    private static class ParserInstance {
        private int lineIdx;
        private final String[] lines;

        /**
         * Default constructor
         *
         * @param input the input string to parse into a StateTransitionSpec
         */
        public ParserInstance(String input) {
            this.lineIdx = 0;
            this.lines = input.split("[\\r\\n]+");
        }

        public StateTransitionSpec parse() {
            String initialState = parseInitialState();
            Character blankChar = parseBlankChar();
            ArrayList<StateTransition> transitions = parseTransitions();
            return new StateTransitionSpec(initialState, blankChar, transitions.toArray(new StateTransition[0]));
        }

        /**
         * Extract the "initial state" field from the input string
         *
         * @return the "initial state" for the output StateTransitionSpec
         */
        private String parseInitialState() {
            String initialState = null;
            while (initialState == null) {
                String currentLine = lines[lineIdx].trim();
                if (!currentLine.startsWith(commentMarker)) {
                    initialState = currentLine;
                }
                lineIdx++;
                if (lineIdx == lines.length) {
                    throw new InvalidInputException("File is too short. Must contain at an initial state, " +
                            "a blank char, and zero or more state transitions");
                }
            }
            return initialState;
        }

        /**
         * Extract the "blank character" field from the input string
         *
         * @return the "blank character" for the output StateTransitionSpec
         */
        private Character parseBlankChar() {
            Character blankChar = null;
            while (blankChar == null) {
                String currentLine = lines[lineIdx].trim();
                if (!currentLine.startsWith(commentMarker)) {
                    if (currentLine.length() != 1) {
                        throw new InvalidInputException("Invalid blank char on line 2: \"" + currentLine +
                                "\". Must be a single character");
                    } else {
                        blankChar = currentLine.charAt(0);
                    }
                }
                lineIdx++;
            }
            return blankChar;
        }

        /**
         * Extract the state transitions from the input string
         *
         * @return the state transitions for the output StateTransitionSpec
         */
        private ArrayList<StateTransition> parseTransitions() {
            ArrayList<StateTransition> transitions = new ArrayList<>();
            while (lineIdx < lines.length) {
                if (!lines[lineIdx].trim().startsWith(commentMarker)) {
                    transitions.add(parseInputLine(lines[lineIdx], lineIdx));
                }
                lineIdx++;
            }
            return transitions;
        }

        private static StateTransition parseInputLine(String inputLine, int lineNumber) {
            return new LineParser(lineNumber + 1, inputLine).parse();
        }
    }

    /**
     * A parser that builds a StateTransition given a valid input string
     */
    private static class LineParser {
        private final int lineNumber;
        private final String[] fields;

        /**
         * Default constructor
         *
         * @param lineNumber   the line number of the line being parsed. This is used for error messaging.
         * @param rawInputLine the line being parsed.
         */
        public LineParser(int lineNumber, String rawInputLine) {
            this.lineNumber = lineNumber;
            this.fields = rawInputLine.trim().split("\\s*,\\s*");
            if (fields.length != 5) {
                throw new InvalidInputException("Cannot parse state transition on line " + lineNumber + ": \"" +
                        rawInputLine + "\"");
            }
        }

        /**
         * Parse the input line into a StateTransition
         *
         * @return the StateTransition that was encoded in the input line
         */
        public StateTransition parse() {
            return new StateTransition(fields[0], parseCharField("given char", 1), fields[2], parseInstruction());
        }

        /**
         * Attempt to parse a char from a specific input field.
         *
         * @param fieldName  the name of the field being parsed. This is used for error messaging.
         * @param fieldIndex the index of the field being parsed within the line.
         * @return the char at the given fieldIndex of the input fields
         * @throws InvalidInputException if the field does not contain exactly one char
         */
        private char parseCharField(String fieldName, int fieldIndex) {
            if (fields[fieldIndex].length() != 1) {
                throw new InvalidInputException("Invalid " + fieldName + " on line " + lineNumber + ": \"" +
                        fields[fieldIndex] + "\". Must be a single character");
            } else {
                return fields[fieldIndex].charAt(0);
            }
        }

        /**
         * Parse an Instruction from the final two fields of the input line
         *
         * @return the Instruction that was encoded in the input line
         * @throws InvalidInputException if the input line does not contain a valid instruction encoding
         */
        private Instruction parseInstruction() {
            Instruction instruction;
            if (fields[4].toLowerCase().equals("r")) {
                instruction = new RightInstruction(parseCharField("char to write", 3));
            } else if (fields[4].toLowerCase().equals("l")) {
                instruction = new LeftInstruction(parseCharField("char to write", 3));
            } else {
                throw new InvalidInputException("Invalid direction on line " + lineNumber + ": \"" +
                        fields[4] + "\". Must be either \"l\" or \"r\"");
            }
            return instruction;
        }
    }
}

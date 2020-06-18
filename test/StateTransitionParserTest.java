import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StateTransitionParserTest {

    @Test
    void testFailsOnUnparsableInput() {
        Exception e = assertThrows(StateTransitionParser.InvalidInputException.class, () -> {
            StateTransitionParser.parse("state\nb\nstate 1, 0, state 2, 1, r\n84jggdpogj\nstate 1, 0, state 2, 1, r");
        });
        assertEquals("Cannot parse state transition on line 4: \"84jggdpogj\"", e.getMessage());
    }

    @Test
    void testFailsOnBlankInput() {
        Exception e = assertThrows(StateTransitionParser.InvalidInputException.class, () -> {
            StateTransitionParser.parse("");
        });
        assertEquals("Input cannot be blank", e.getMessage());
    }

    @Test
    void testFailsOnUnparsableGivenChar() {
        Exception e = assertThrows(StateTransitionParser.InvalidInputException.class, () -> {
            StateTransitionParser.parse("state\nb\nstate 1, abc, state 2, 1, r");
        });
        assertEquals("Invalid given char on line 3: \"abc\". Must be a single character", e.getMessage());
    }

    @Test
    void testFailsOnUnparsableCharToWrite() {
        Exception e = assertThrows(StateTransitionParser.InvalidInputException.class, () -> {
            StateTransitionParser.parse("state\nb\nstate 1, 0, state 2, abc, r");
        });
        assertEquals("Invalid char to write on line 3: \"abc\". Must be a single character", e.getMessage());
    }

    @Test
    void testFailsOnUnparsableDirection() {
        Exception e = assertThrows(StateTransitionParser.InvalidInputException.class, () -> {
            StateTransitionParser.parse("state\nb\nstate 1, 0, state 2, 1, p");
        });
        assertEquals("Invalid direction on line 3: \"p\". Must be either \"l\" or \"r\"", e.getMessage());
    }

    @Test
    void testFailsOnUnparsableBlankChar() {
        Exception e = assertThrows(StateTransitionParser.InvalidInputException.class, () -> {
            StateTransitionParser.parse("initial state\nstate 1, abc, state 2, 1, r");
        });
        assertEquals("Invalid blank char on line 2: \"state 1, abc, state 2, 1, r\". Must be a single character", e.getMessage());
    }

    @Test
    void testFailsOnFileThatIsLessThanTwoLines() {
        Exception e = assertThrows(StateTransitionParser.InvalidInputException.class, () -> {
            StateTransitionParser.parse("state 1, abc, state 2, 1, r");
        });
        assertEquals("File is too short. Must contain at an initial state, a blank char, and zero or more state transitions", e.getMessage());
    }

    @Test
    void testCanParseValidInput() {
        String testInput = "state 1\n" +
                "b\n" +
                "state 1,0,state 2,1,r\n\r" +
                "     state 2, 0, true, 1, L\n" +
                " state 2 , 1 , state 3 , 0 , R   \n\r" +
                "state 3, 0, false, 0, l\n";
        StateTransition[] expectedTransitions = new StateTransition[]{
                new StateTransition("state 1", '0', "state 2", new RightInstruction('1')),
                new StateTransition("state 2", '0', "true", new LeftInstruction('1')),
                new StateTransition("state 2", '1', "state 3", new RightInstruction('0')),
                new StateTransition("state 3", '0', "false", new LeftInstruction('0'))
        };
        assertEquals(new StateTransitionSpec("state 1", 'b', expectedTransitions),
                StateTransitionParser.parse(testInput));
    }

    @Test
    void testIgnoresCommentLines() {
        String testInput = "//initial state\n" +
                "state 2\n" +
                "//blank char\n" +
                "c\n" +
                "state 1,0,state 2,1,r\n\r" +
                "//this is a comment\n" +
                " state 2 , 1 , state 3 , 0 , R   \n\r";
        StateTransition[] expectedTransitions = new StateTransition[]{
                new StateTransition("state 1", '0', "state 2", new RightInstruction('1')),
                new StateTransition("state 2", '1', "state 3", new RightInstruction('0')),
        };
        assertEquals(new StateTransitionSpec("state 2", 'c', expectedTransitions),
                StateTransitionParser.parse(testInput));
    }
}

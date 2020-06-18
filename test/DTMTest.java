import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DTMTest {

    @Test
    void testDTMOutputsHaltingStateForEmptyControl() {
        DTM dtm = new DTM(new StateTransitionSpec("halting state", 'b', new StateTransition[0]));
        assertEquals("halting state", dtm.run(new Tape('b')));
    }

    @Test
    void testDTMOutputsHaltingStateNonTrivialControl() {
        StateTransition[] transitions = new StateTransition[]{
                new StateTransition("state 1", '0', "state 1", new RightInstruction('0')),
                new StateTransition("state 1", '1', "state 1", new RightInstruction('0')),
                new StateTransition("state 1", 'b', "halting state", new LeftInstruction('b'))
        };
        Tape tape = TapeBuilder.buildFrom("0110101", 'b');
        DTM dtm = new DTM(new StateTransitionSpec("state 1", 'b', transitions));
        assertEquals("halting state", dtm.run(tape));
        assertEquals("0000000", tape.toString());
    }

    @Test
    void testDTMCanRunMultipleTimesOnDifferentInputs() {
        StateTransition[] transitions = new StateTransition[]{
                new StateTransition("state 1", '0', "state 2", new RightInstruction('1')),
                new StateTransition("state 1", '1', "state 2", new RightInstruction('1')),
                new StateTransition("state 2", '0', "state 3", new RightInstruction('1')),
                new StateTransition("state 2", '1', "state 3", new RightInstruction('1')),
                new StateTransition("state 3", '0', "state 4", new RightInstruction('1')),
                new StateTransition("state 3", '1', "state 4", new RightInstruction('1')),
                new StateTransition("state 4", '0', "state 4", new RightInstruction('0')),
                new StateTransition("state 4", '1', "state 4", new RightInstruction('0')),
                new StateTransition("state 4", 'b', "halting state", new LeftInstruction('b'))
        };
        DTM dtm = new DTM(new StateTransitionSpec("state 1", 'b', transitions));

        Tape tape1 = TapeBuilder.buildFrom("0100101", 'b');
        assertEquals("halting state", dtm.run(tape1));
        assertEquals("1110000", tape1.toString());

        Tape tape2 = TapeBuilder.buildFrom("00001111011", 'b');
        assertEquals("halting state", dtm.run(tape2));
        assertEquals("11100000000", tape2.toString());
    }

    @Test
    void testDTMThrowsExceptionGivenInvalidInputTape() {
        StateTransition[] transitions = new StateTransition[]{
                new StateTransition("state 1", '0', "halting state", new RightInstruction('0'))
        };
        Tape tape = TapeBuilder.buildFrom("1", 'b');
        DTM dtm = new DTM(new StateTransitionSpec("state 1", 'b', transitions));
        assertThrows(StateControl.InvalidInputException.class, () -> {
            dtm.run(tape);
        });
    }

    @Test
    void testHistoryForSingleRun() {
        StateTransition[] transitions = new StateTransition[]{
                new StateTransition("state 1", '0', "state 1", new RightInstruction('0')),
                new StateTransition("state 1", '1', "state 1", new RightInstruction('0')),
                new StateTransition("state 1", 'b', "state 2", new LeftInstruction('b')),
                new StateTransition("state 2", '0', "state 3", new LeftInstruction('1')),
                new StateTransition("state 2", 'b', "even", new LeftInstruction('b')),
                new StateTransition("state 3", '0', "state 2", new LeftInstruction('0')),
                new StateTransition("state 3", 'b', "odd", new RightInstruction('b')),
        };
        Tape tape = TapeBuilder.buildFrom("0110101", 'b');
        DTM dtm = new DTM(new StateTransitionSpec("state 1", 'b', transitions));
        assertEquals("odd", dtm.run(tape));
        assertEquals("1010101", tape.toString());
        ArrayList<String> expectedHistory = new ArrayList<>();
        expectedHistory.add("state 1: [0]110101");
        expectedHistory.add("state 1: 0[1]10101");
        expectedHistory.add("state 1: 00[1]0101");
        expectedHistory.add("state 1: 000[0]101");
        expectedHistory.add("state 1: 0000[1]01");
        expectedHistory.add("state 1: 00000[0]1");
        expectedHistory.add("state 1: 000000[1]");
        expectedHistory.add("state 1: 0000000[b]");
        expectedHistory.add("state 2: 000000[0]");
        expectedHistory.add("state 3: 00000[0]1");
        expectedHistory.add("state 2: 0000[0]01");
        expectedHistory.add("state 3: 000[0]101");
        expectedHistory.add("state 2: 00[0]0101");
        expectedHistory.add("state 3: 0[0]10101");
        expectedHistory.add("state 2: [0]010101");
        expectedHistory.add("state 3: [b]1010101");
        expectedHistory.add("odd: [1]010101");

        assertEquals(expectedHistory, dtm.getRecentRunHistory());
    }

    @Test
    void testHistoryFormatsOutOfBoundsTraversalCorrectly() {
        StateTransition[] transitions = new StateTransition[]{
                new StateTransition("state 1", '0', "state 1", new RightInstruction('0')),
                new StateTransition("state 1", 'b', "state 2", new RightInstruction('b')),
                new StateTransition("state 2", 'b', "state 3", new RightInstruction('b')),
                new StateTransition("state 3", 'b', "state 4", new LeftInstruction('0')),
                new StateTransition("state 4", 'b', "state 4", new LeftInstruction('b')),
                new StateTransition("state 4", '0', "state 5", new LeftInstruction('0')),
                new StateTransition("state 5", 'b', "state 6", new LeftInstruction('b')),
                new StateTransition("state 6", 'b', "state 7", new LeftInstruction('b')),
                new StateTransition("state 7", 'b', "done", new RightInstruction('0')),
        };
        Tape tape = TapeBuilder.buildFrom("0", 'b');
        DTM dtm = new DTM(new StateTransitionSpec("state 1", 'b', transitions));
        assertEquals("done", dtm.run(tape));
        assertEquals("0bb0bb0", tape.toString());
        ArrayList<String> expectedHistory = new ArrayList<>();
        expectedHistory.add("state 1: [0]");
        expectedHistory.add("state 1: 0[b]");
        expectedHistory.add("state 2: 0b[b]");
        expectedHistory.add("state 3: 0bb[b]");
        expectedHistory.add("state 4: 0b[b]0");
        expectedHistory.add("state 4: 0[b]b0");
        expectedHistory.add("state 4: [0]bb0");
        expectedHistory.add("state 5: [b]0bb0");
        expectedHistory.add("state 6: [b]b0bb0");
        expectedHistory.add("state 7: [b]bb0bb0");
        expectedHistory.add("done: 0[b]b0bb0");

        assertEquals(expectedHistory, dtm.getRecentRunHistory());
    }
}

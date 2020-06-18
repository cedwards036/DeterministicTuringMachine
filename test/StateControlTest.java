import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StateControlTest {

    private static class TestCommand implements HaltingCommand {
        private String haltingState;

        @Override
        public void execute(String haltingState) {
            this.haltingState = haltingState;
        }

        @Override
        public String getHaltingState() {
            return haltingState;
        }
    }

    @Test
    void controlExecutesCallbackCommandWhenHalted() {
        TestCommand haltingCommand = new TestCommand();
        new StateControl(new StateTransitionSpec("halting state", 'b', new StateTransition[0]), haltingCommand).processChar('c');
        assertEquals("halting state", haltingCommand.getHaltingState());
    }

    @Test
    void controlThrowsExceptionIfCurrentStateCannotHandleChar() {
        Instruction instruction = new RightInstruction('0');
        StateTransition transition = new StateTransition("starting state", '1', "new state", instruction);
        Exception e = assertThrows(StateControl.InvalidInputException.class, () -> {
            new StateControl(new StateTransitionSpec("starting state", 'b', new StateTransition[]{transition}), new TestCommand()).processChar('c');
        });
        assertEquals("Invalid character for current state \"starting state\": 'c'", e.getMessage());
    }

    @Test
    void controlReturnsCorrectWriteInstructionForCurrentStateAndChar() {
        Instruction instruction = new LeftInstruction('0');
        StateTransition transition = new StateTransition("starting state", '1', "new state", instruction);
        StateControl control = new StateControl(new StateTransitionSpec("starting state", 'b', new StateTransition[]{transition}), new TestCommand());
        assertEquals(instruction, control.processChar('1'));
    }

    @Test
    void controlReturnsCorrectWriteInstructionForCurrentStateAndCharAfterSeveralTransitions() {
        Instruction instruction1 = new RightInstruction('0');
        StateTransition transition1 = new StateTransition("starting state", '1', "second state", instruction1);
        Instruction instruction2 = new LeftInstruction('1');
        StateTransition transition2 = new StateTransition("second state", '0', "third state", instruction2);
        Instruction instruction3 = new RightInstruction('0');
        StateTransition transition3 = new StateTransition("third state", '0', "another state", instruction3);
        StateTransition[] transitions = {transition1, transition2, transition3};

        StateControl control = new StateControl(new StateTransitionSpec("starting state", 'b', transitions), new TestCommand());
        assertEquals(instruction1, control.processChar('1'));
        assertEquals(instruction2, control.processChar('0'));
        assertEquals(instruction3, control.processChar('0'));
    }

    @Test
    void controlCanHandleMultipleValidInputCharsForASingleState() {
        Instruction instruction1 = new RightInstruction('0');
        StateTransition transition1 = new StateTransition("state1", '1', "state1", instruction1);
        Instruction instruction2 = new LeftInstruction('1');
        StateTransition transition2 = new StateTransition("state1", '0', "state1", instruction2);
        StateTransition[] transitions = {transition1, transition2,};

        StateControl control = new StateControl(new StateTransitionSpec("state1", 'b', transitions), new TestCommand());
        assertEquals(instruction1, control.processChar('1'));
        assertEquals(instruction2, control.processChar('0'));
        assertEquals(instruction1, control.processChar('1'));
    }

    @Test
    void controlCanResetToInitialState() {
        Instruction instruction1 = new RightInstruction('0');
        StateTransition transition1 = new StateTransition("starting state", '1', "second state", instruction1);
        Instruction instruction2 = new LeftInstruction('1');
        StateTransition transition2 = new StateTransition("second state", '0', "third state", instruction2);
        Instruction instruction3 = new RightInstruction('0');
        StateTransition transition3 = new StateTransition("third state", '0', "another state", instruction3);
        StateTransition[] transitions = {transition1, transition2, transition3};

        StateControl control = new StateControl(new StateTransitionSpec("starting state", 'b', transitions), new TestCommand());
        control.processChar('1');
        control.processChar('0');
        control.processChar('0');
        control.reset();
        assertEquals(instruction1, control.processChar('1'));
    }
}

import java.util.HashMap;

/**
 * The main control unit of a DTM
 */
public class StateControl {
    private final String initialState;
    private String currentState;
    private final HashMap<String, HashMap<Character, TransitionOutput>> transitionLookup;
    private final HaltingCommand haltingCommand;

    /**
     * Default constructor
     *
     * @param transitionSpec a group of state transitions, along with a start state and blank char, that define
     *                       the operation of the control unit
     * @param haltingCommand a command that is executed once the state control halts on an input
     */
    public StateControl(StateTransitionSpec transitionSpec, HaltingCommand haltingCommand) {
        this.initialState = transitionSpec.getInitialState();
        this.currentState = initialState;
        this.transitionLookup = buildLookup(transitionSpec.getTransitions());
        this.haltingCommand = haltingCommand;
    }

    /**
     * Process a character read from an input Tape to the DTM, and return the correct instruction to be
     * executed on that tape based on the current state of the machine.
     *
     * @param c the character to process
     * @return an instruction to be executed on a Tape
     */
    public Instruction processChar(char c) {
        if (transitionLookup.containsKey(currentState)) {
            return processCharForNonHaltingState(c);
        } else {
            haltingCommand.execute(currentState);
            return new NullInstruction();
        }
    }

    /**
     * Given a character that will not cause the machine to halt, process the character and return the next
     * instruction to be executed on the tape.
     *
     * @param c the character to process
     * @return an instruction to be executed on a Tape
     */
    private Instruction processCharForNonHaltingState(char c) {
        TransitionOutput output = transitionLookup.get(currentState).get(c);
        if (output == null) {
            throw new InvalidInputException("Invalid character for current state \"" + currentState + "\": " + "'" + c + "'");
        } else {
            currentState = output.getNextState();
            return output.getInstruction();
        }
    }

    /**
     * Return the control to its initial state, in preparation for a new input tape
     */
    public void reset() {
        currentState = initialState;
    }

    /**
     * An exception to be thrown when attempting to process a character that cannot be handled by the current state
     */
    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String errorMessage) {
            super(errorMessage);
        }
    }

    public String getCurrentState() {
        return currentState;
    }

    /**
     * Build a lookup table that allows the control unit to look up the next state and any outputs given
     * the current state and an input character.
     *
     * @param transitions a list of state transitions that define the operation of the control unit
     * @return a lookup table that maps current states to outputs and future states
     */
    private HashMap<String, HashMap<Character, TransitionOutput>> buildLookup(StateTransition[] transitions) {
        HashMap<String, HashMap<Character, TransitionOutput>> lookup = new HashMap<>();
        for (StateTransition transition : transitions) {
            addStateTransitionToLookup(lookup, transition);
        }
        return lookup;
    }

    /**
     * Add the given state transition to the given state lookup table
     *
     * @param lookup     a lookup table that maps current states to outputs and future states
     * @param transition a new state transition to be added to the table
     */
    private void addStateTransitionToLookup(HashMap<String, HashMap<Character, TransitionOutput>> lookup, StateTransition transition) {
        TransitionOutput output = new TransitionOutput(transition.getNewState(), transition.getInstruction());
        HashMap<Character, TransitionOutput> innerMap;
        if (lookup.containsKey(transition.getGivenState())) {
            innerMap = lookup.get(transition.getGivenState());
        } else {
            innerMap = new HashMap<>();
            lookup.put(transition.getGivenState(), innerMap);
        }
        innerMap.put(transition.getGivenChar(), output);
    }

    /**
     * The result from looking up a current state in the state lookup table
     */
    private static class TransitionOutput {
        private final String nextState;
        private final Instruction instruction;

        /**
         * Default constructor
         *
         * @param nextState   the next state to load into the control unit
         * @param instruction the next instruction to execute on the tape
         */
        public TransitionOutput(String nextState, Instruction instruction) {
            this.nextState = nextState;
            this.instruction = instruction;
        }

        public String getNextState() {
            return nextState;
        }

        public Instruction getInstruction() {
            return instruction;
        }

    }
}

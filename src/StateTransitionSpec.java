import java.util.Arrays;

/**
 * A state transition specification that described the entire functionality of a DTM
 */
public class StateTransitionSpec {

    private final String initialState;
    private final char blankChar;
    private final StateTransition[] transitions;

    /**
     * Default constructor
     *
     * @param initialState the initial state of the DTM. This should match at least one of the states in the array
     *                     of state transitions
     * @param blankChar    the character to be treated as a "blank" by any tape inputs to this DTM program
     * @param transitions  the collection of state transitions that define the program's behavior
     */
    public StateTransitionSpec(String initialState, char blankChar, StateTransition[] transitions) {
        this.initialState = initialState;
        this.blankChar = blankChar;
        this.transitions = transitions;
    }

    public String getInitialState() {
        return initialState;
    }

    public StateTransition[] getTransitions() {
        return transitions;
    }

    public char getBlankChar() {
        return blankChar;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            StateTransitionSpec otherSpec = (StateTransitionSpec) obj;
            return initialState.equals(otherSpec.initialState) &&
                    blankChar == otherSpec.blankChar &&
                    Arrays.equals(transitions, otherSpec.transitions);
        }
    }
}

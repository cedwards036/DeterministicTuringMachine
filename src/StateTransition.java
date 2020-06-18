/**
 * A transition from one DTM state to another
 */
public class StateTransition {
    private final String givenState;
    private final char givenChar;
    private final String newState;
    private final Instruction instruction;

    /**
     * Default constructor
     *
     * @param givenState  the current state of the DTM
     * @param givenChar   the char read off the input tape
     * @param newState    the new state of the DTM
     * @param instruction a write instruction to be executed on the input tape
     */
    public StateTransition(String givenState, char givenChar, String newState, Instruction instruction) {
        this.givenState = givenState;
        this.givenChar = givenChar;
        this.newState = newState;
        this.instruction = instruction;
    }

    public String getGivenState() {
        return givenState;
    }

    public char getGivenChar() {
        return givenChar;
    }

    public String getNewState() {
        return newState;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            StateTransition otherTransition = (StateTransition) obj;
            return givenState.equals(otherTransition.givenState) &&
                    givenChar == otherTransition.givenChar &&
                    newState.equals(otherTransition.newState) &&
                    instruction.equals(otherTransition.instruction);
        }
    }
}

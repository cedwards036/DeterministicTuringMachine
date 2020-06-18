import java.util.ArrayList;

/**
 * A deterministic Turing machine
 */
public class DTM {
    private final StateControl control;
    private final HaltingCommand haltingCommand;
    private boolean isHalted;
    private RunHistory history;

    /**
     * Default constructor
     *
     * @param transitionSpec a grouping of state transitions describing the operation of the DTM
     */
    public DTM(StateTransitionSpec transitionSpec) {
        this.haltingCommand = new Command();
        this.control = new StateControl(transitionSpec, haltingCommand);
        this.isHalted = false;
        history = new RunHistory();
    }

    /**
     * Given an input tape, run the tape through the DTM and output the DTM's halting state.
     *
     * @param tape a tape to run through the DTM
     * @return the final state of the state control after processing the given tape
     */
    public String run(Tape tape) {
        resetDTMForNewRun();
        while (!isHalted) {
            history.addTapeSnapshot(tape);
            Instruction instruction = control.processChar(tape.getCurrentCellValue());
            instruction.execute(tape);
        }
        return haltingCommand.getHaltingState();
    }

    /**
     * Reset the DTM in preparation for accepting a new input tape. This
     * ensures that the running of one tape will have no effect on the running
     * of subsequent tapes through the machine.
     */
    private void resetDTMForNewRun() {
        isHalted = false;
        control.reset();
        history.clear();
    }

    /**
     * Get the tape state history from the most recent run
     *
     * @return a history of all intermediate tape states for the most recent run
     */
    public ArrayList<String> getRecentRunHistory() {
        return history.asArrayList();
    }

    /**
     * A command object to be called once the state control halts
     */
    private class Command implements HaltingCommand {
        private String haltingState;

        /**
         * Record the halting state and stop the current run of the DTM
         *
         * @param haltingState the final state of the current program
         */
        @Override
        public void execute(String haltingState) {
            this.haltingState = haltingState;
            isHalted = true;
        }


        @Override
        public String getHaltingState() {
            return haltingState;
        }
    }

    /**
     * A record containing all intermediate tape states of a given run of the program
     */
    private class RunHistory {
        private final ArrayList<String> history;

        public RunHistory() {
            this.history = new ArrayList<>();
        }

        /**
         * Add a tape snapshot to the history
         *
         * @param tape the tape of which to make a snapshot
         */
        public void addTapeSnapshot(Tape tape) {
            history.add(formatTapeForHistory(tape));
        }

        /**
         * Erase the current run history
         */
        public void clear() {
            history.clear();
        }

        public ArrayList<String> asArrayList() {
            return history;
        }

        /**
         * Given a tape, capture the current state of that tape in string format, including
         * all non-blank symbols, and the current "selected cell" of the tape
         *
         * @param tape an input tape to the DTM
         * @return a string representation of the current contents of the given tape
         */
        public String formatTapeForHistory(Tape tape) {
            String tapeString = tape.toString();
            if (tapeString.length() == 0) {
                return "";
            } else if (tape.getCurrentCellIndex() < 0) {
                String fillerBlanks = repeatCharNTimes(tape.getBlankChar(), -tape.getCurrentCellIndex() - 1);
                return control.getCurrentState() + ": [" + tape.getBlankChar() + "]" + fillerBlanks + tapeString;
            } else if (tape.getCurrentCellIndex() >= tapeString.length()) {
                String fillerBlanks = repeatCharNTimes(tape.getBlankChar(), tape.getCurrentCellIndex() - tapeString.length());
                return control.getCurrentState() + ": " + tapeString + fillerBlanks + "[" + tape.getBlankChar() + "]";
            } else {
                return control.getCurrentState() + ": " +
                        tapeString.substring(0, tape.getCurrentCellIndex()) +
                        "[" + tapeString.charAt(tape.getCurrentCellIndex()) + "]" +
                        tapeString.substring(tape.getCurrentCellIndex() + 1);
            }
        }

        /**
         * Create a string consisting of the given character c repeated n times
         *
         * @param c the character to repeat
         * @param n the number of times to repeat the character
         * @return a string consisting of character c repeated n times
         */
        private String repeatCharNTimes(char c, int n) {
            return String.valueOf(c).repeat(n);
        }
    }
}

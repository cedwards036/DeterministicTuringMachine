import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * A driver program used to run a Deterministic Turing Machine capable of accepting
 * user-inputted symbolic tapes
 */
public class DTMMain {

    /**
     * The main driver program for simulating a Deterministic Turing Machine.
     * <p>
     * This program accepts a user-defined set of state transitions, constructs a DTM defined by those
     * state transitions, and, once it is up and running, accepts input tapes to run through the DTM. Once
     * a run is completed, the program will output the halting state of the program, the final state of the tape,
     * and the history of the tapes intermediate states during the program run.
     * </p>
     *
     * @param args an input string consisting of one (second optional), space-separated fields:
     *             <ul>
     *             <li>the filepath of a state transitions file used to define DTM operations</li>
     *             <li>(Optional) the filepath of an output directory in which to place run results. Output will
     *             display to the console if this is not specified</li>
     *             </ul>
     */
    public static void main(String[] args) {
        try {
            StateTransitionSpec transitionSpec = StateTransitionParser.parse(readFile(args[0]));
            Outputter outputter = makeOutputter(args);
            DTM dtm = new DTM(transitionSpec);
            Scanner scanner = new Scanner(System.in);
            boolean programIsRunning = true;
            while (programIsRunning) {
                Tape tape = getTape(transitionSpec.getBlankChar(), scanner);
                String initialTape = tape.toString();
                runTapeThroughDTM(outputter, dtm, tape, initialTape);
                programIsRunning = getYesNoInput(scanner, "\nWould you like to enter another tape? (y / n)");
            }
        } catch (IOException e) {
            System.out.println("Error: cannot load DTM from spec file: \"" + args[0] + "\"");
        } catch (StateTransitionParser.InvalidInputException e) {
            System.out.println("Error while parsing " + args[0] + ": \"" + e.getMessage() + "\"");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Usage: java DTMMain [filepath to state transition spec file] [optional output directory]");
        }
    }

    private static void runTapeThroughDTM(Outputter outputter, DTM dtm, Tape tape, String initialTape) {
        try {
            String haltingState = dtm.run(tape);
            outputter.write(buildRunReport(dtm, tape, initialTape, haltingState));
        } catch (StateControl.InvalidInputException e) {
            System.out.println("The DTM encountered an error: " + e.getMessage());
        }
    }

    /**
     * Create the outputter to use for this run of the program
     *
     * @param args the list of command line arguments to the program
     * @return the outputter to be used
     */
    private static Outputter makeOutputter(String[] args) {
        if (args.length == 2) {
            return new FileOutputter(args[1]);
        } else {
            return new ConsoleOutputter();
        }
    }

    /**
     * Prompt the user to input a tape to the current DTM, returning the given tape
     *
     * @param blankChar the character to be treated as a "blank" in the given tape
     * @param scanner   a Scanner object used to read user input
     * @return an input tape built from the user's string input
     */
    private static Tape getTape(char blankChar, Scanner scanner) {
        System.out.println("Please enter a tape to process: ");
        return TapeBuilder.buildFrom(scanner.nextLine(), blankChar);
    }

    /**
     * Read an input file and output the file contents as a String
     *
     * @param filepath the filepath of the file to read
     * @return the contents of the file as a single String
     * @throws IOException if the filepath is invalid
     */
    private static String readFile(String filepath) throws IOException {
        String fileContents = "";
        fileContents = new String(Files.readAllBytes(Paths.get(filepath)));
        return fileContents;
    }

    /**
     * Create a report string describing the most recent DTM run
     *
     * @param dtm          the DTM used for the most recent run
     * @param tape         the tape object used for the current DTM run
     * @param initialTape  the initial input tape to the current DTM run
     * @param haltingState the halting state of the current DTM run
     * @return a string report describing the most recent DTM run to the user
     */
    private static String buildRunReport(DTM dtm, Tape tape, String initialTape, String haltingState) {
        return "Input tape: " + initialTape + "\n" +
                "Halting State: " + haltingState + "\n" +
                "Final tape contents: " + tape.toString() + "\n" +
                "\nRun History:\n\n" +
                runHistoryToString(dtm);
    }

    /**
     * Convert a DTM's tape history into a string in preparation for reporting out to the user
     *
     * @param dtm the DTM of which to create a history string
     * @return a string representation of all intermediate states of the DTM from its most recent run
     */
    private static String runHistoryToString(DTM dtm) {
        StringBuilder fullHistory = new StringBuilder();
        for (String historyLine : dtm.getRecentRunHistory()) {
            fullHistory.append(historyLine);
            fullHistory.append("\n");
        }
        return fullHistory.toString();
    }

    /**
     * Get an input of "y" or "n" from the user via standard output and convert it into a boolean.
     * This method will loop infinitely until the user inputs "y" or "n", thereby ensuring valid input
     *
     * @param scanner a Scanner object to retrieve the input
     * @param prompt  the prompt to display to the user, asking for the input
     * @return True if the user inputted "y", and False if they inputted "n"
     */
    private static boolean getYesNoInput(Scanner scanner, String prompt) {
        String input = "";
        while (!(input.equals("y") || input.equals("n"))) {
            System.out.println(prompt);
            input = scanner.nextLine().trim().toLowerCase();
        }
        return input.equals("y");
    }

    private interface Outputter {
        void write(String content);
    }

    /**
     * An outputter that writes to the console
     */
    private static class ConsoleOutputter implements Outputter {

        /**
         * Write a string to the console
         *
         * @param content the string content to write to the console
         */
        public void write(String content) {
            System.out.println(content);
        }
    }

    /**
     * An outputter that writes to a file in a given directory
     */
    private static class FileOutputter implements Outputter {

        private final String dirFilepath;

        /**
         * Default constructor
         *
         * @param dirFilepath the filepath of the directory to write a file to
         */
        public FileOutputter(String dirFilepath) {
            this.dirFilepath = dirFilepath;
        }

        /**
         * Write a string to an output file
         *
         * @param content the string content to write to the file
         */
        public void write(String content) {
            String outputFilepath = makeFullFilepath();
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilepath));
                writer.write(content);
                writer.close();
            } catch (IOException e) {
                System.out.println("Invalid output directory: " + dirFilepath);
            }
            System.out.println("Output written to " + outputFilepath);
        }

        private String makeFullFilepath() {
            return dirFilepath + File.separator + makeFileName();
        }

        private String makeFileName() {
            ZonedDateTime currentDatetime = ZonedDateTime.now(ZoneId.systemDefault());
            return "dtm_run_" +
                    currentDatetime.format(DateTimeFormatter.ofPattern("uuuu-MM-dd")) + "T" +
                    currentDatetime.format(DateTimeFormatter.ofPattern("HH-mm-ss")) +
                    ".txt";
        }
    }
}

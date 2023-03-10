// Mukhutdinov Artur

/*
 * Main
 *
 * Ver. 1.0.0
 *
 * Program which implements the FSA validator.
 * Data stores and outputs in the files' fsa.txt and result.txt.
 */

import exceptions.*;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The Main class of the program with the general functionality.
 *
 * @author Artur Mukhutdinov, CS-03 BS1 Innopolis University
 * @version 1.0.0 10 March 2023
 */
public class Main {
    /**
     * Scanner from input file ("fsa.txt")
     */
    private static BufferedReader reader = null;
    /**
     * Writer for output file ("result.txt")
     */
    private static BufferedWriter writer = null;
    /**
     * The static variable which provides the most general and complicated tests for FSA validation
     */
    private static final Checker CHECKER = new Checker();
    /**
     * The static variable which provides a finished report of the FSA validation if there are no runtime errors
     */
    private static final ReportFormation REPORT = new ReportFormation();
    /**
     * Array of possible states in FSA.
     */
    private static final ArrayList<State> STATES = new ArrayList<>();
    /**
     * Array of possible transition token in FSA.
     */
    private static final ArrayList<Transition> ALPHA = new ArrayList<>();
    /**
     * An initial state of FSA. In this particular case, according to the task, only one initial state is allowed.
     */
    private static State initialState = null;
    /**
     * Array of possible final states in FSA.
     */
    private static final ArrayList<State> FINAL_STATES = new ArrayList<>();

    /**
     * The main method provide something like "collection" of the major methods of the entire program
     *
     * @param args canonical parameter for java entry point
     * @throws IOException throws when input file ("fsa.txt") does not exist
     */
    public static void main(String[] args) throws IOException {
        scanFiles();

        makeFormattedInput();

        // Checking FSA for disjoint
        try {
            if (CHECKER.isDisjoint(STATES, initialState)) {
                throw new DisjointStatesException();
            }
        } catch (DisjointStatesException e) {
            writer.write(e.toString());
            reader.close();
            writer.close();
            System.exit(0);
        }

        // Complete a report
        markWarnings();
        REPORT.markCompleteness(CHECKER.isComplete(STATES, ALPHA));

        writer.write(REPORT.toString());

        reader.close();
        writer.close();
    }

    /**
     * Scan files input ("fsa.txt") and output ("result.txt"). Output file will be created again
     *
     * @throws IOException throws when input file ("fsa.txt") does not exist
     */
    private static void scanFiles() throws IOException {
        try {
            reader = new BufferedReader(new FileReader("fsa.txt"));
            writer = new BufferedWriter(new FileWriter("result.txt"));
        } catch (IOException e) {
            writer.write(e.toString());
            reader.close();
            writer.close();
            System.exit(0);
        }
    }

    /**
     * Parse entire set of all possible states
     *
     * @throws IOException throws when input file ("fsa.txt") does not exist
     */
    private static void scanStates() throws IOException {
        try {
            String tempString = reader.readLine();
            String[] stateNames = tempString.substring(8, tempString.length() - 1).split(",");

            // If nothing was appeared
            if (tempString.substring(8, tempString.length() - 1).length() == 0) {
                throw new InputMalformedException();
            }

            for (String stateName : stateNames) {
                if (!CHECKER.isStateNameCorrect(stateName)) {
                    throw new InputMalformedException();
                }

                STATES.add(new State(stateName));
            }

        } catch (IOException | InputMalformedException e) {
            writer.write(e.toString());
            reader.close();
            writer.close();
            System.exit(0);
        }
    }

    /**
     * Parse entire set of all possible transition tokens
     *
     * @throws IOException throws when input file ("fsa.txt") does not exist
     */
    private static void scanAlpha() throws IOException {
        try {
            String tempString = reader.readLine();
            String[] transitionNames = tempString.substring(7, tempString.length() - 1).split(",");

            // If nothing was appeared
            if (tempString.substring(7, tempString.length() - 1).length() == 0) {
                throw new InputMalformedException();
            }

            for (String transitionName : transitionNames) {
                if (!CHECKER.isTransitionNameCorrect(transitionName)) {
                    throw new InputMalformedException();
                }

                ALPHA.add(new Transition(transitionName));
            }

        } catch (IOException | InputMalformedException e) {
            writer.write(e.toString());
            reader.close();
            writer.close();
            System.exit(0);
        }
    }

    /**
     * Parse initial state of FSA
     *
     * @throws IOException throws when input file ("fsa.txt") does not exist
     */
    private static void scanInitialState() throws IOException {
        try {
            String stateName = reader.readLine();
            stateName = stateName.substring(9, stateName.length() - 1);
            String[] tempString = stateName.split(",");

            // If nothing was appeared
            if (stateName.length() == 0) {
                throw new InitialStateNotDefinedException();
            }

            // If more than one initial state
            if (tempString.length > 1) {
                throw new InputMalformedException();
            }

            initialState = getState(stateName);

            // If initialState not in the possible states set
            if (initialState == null) {
                throw new IncorrectStateException(stateName);
            }

        } catch (IOException | InputMalformedException | InitialStateNotDefinedException | IncorrectStateException e) {
            writer.write(e.toString());
            reader.close();
            writer.close();
            System.exit(0);
        }
    }

    /**
     * Parse entire set of all possible final states
     *
     * @throws IOException throws when input file ("fsa.txt") does not exist
     */
    private static void scanFinalStates() throws IOException {
        try {
            String tempString = reader.readLine();
            String[] stateNames = tempString.substring(8, tempString.length() - 1).split(",");

            // If nothing was appeared
            if (tempString.substring(8, tempString.length() - 1).length() == 0) {
                return;
            }

            for (String stateName : stateNames) {
                State tempState = getState(stateName);

                // If particular state is not belong to the possible states set
                if (tempState == null) {
                    throw new IncorrectStateException(stateName);
                }

                FINAL_STATES.add(tempState);
            }

        } catch (IOException | IncorrectStateException e) {
            writer.write(e.toString());
            reader.close();
            writer.close();
            System.exit(0);
        }
    }

    /**
     * Parse the entire set of all possible transitions. Transitions in this implementation presented as
     * links from state_1 --> (possible_states_from_state_1)
     *
     * @throws IOException throws when input file ("fsa.txt") does not exist
     */
    private static void scanTransitions() throws IOException {
        try {
            String tempString = reader.readLine();
            String[] transitions = tempString.substring(7, tempString.length() - 1).split(",");

            // If nothing was appeared
            if (tempString.substring(7, tempString.length() - 1).length() == 0) {
                return;
            }

            for (String transition : transitions) {
                // Transition split by separator = ">"
                String[] transitionSplit = transition.split(">");

                State sourceState = getState(transitionSplit[0]);
                // If state_source from transition does not belong set of possible states
                if (sourceState == null) {
                    throw new IncorrectStateException(transitionSplit[0]);
                }

                Transition trans = getTransition(transitionSplit[1]);
                // If transition token from transition does not belong set of possible transitions
                if (trans == null) {
                    throw new TransitionIsNotPresentedException(transitionSplit[1]);
                }

                State destState = getState(transitionSplit[2]);
                // If state_dest from transition does not belong set of possible states
                if (destState == null) {
                    throw new IncorrectStateException(transitionSplit[2]);
                }

                sourceState.addPossibleTransition(destState, trans);
            }
        } catch (IOException | IncorrectStateException | TransitionIsNotPresentedException e) {
            writer.write(e.toString());
            reader.close();
            writer.close();
            System.exit(0);
        }
    }

    /**
     * Get state by its name from set of all possible states
     *
     * @param stateName name of the state needed state
     * @return State if presented in set of states; Otherwise, null
     */
    private static State getState(String stateName) {
        for (State state : STATES) {
            if (state.getName().equals(stateName)) {
                return state;
            }
        }
        return null;
    }

    /**
     * Get transition token by its name from set of all possible transitions
     *
     * @param transitionName name of the state needed transition token
     * @return Transition if presented in set of transitions; Otherwise, null
     */
    private static Transition getTransition(String transitionName) {
        for (Transition transition : ALPHA) {
            if (transition.name().equals((transitionName))) {
                return transition;
            }
        }
        return null;
    }

    /**
     * The common driver for all parsing methods of the program.
     * Parsing provided sequentially by task's conditions.
     */
    private static void makeFormattedInput() {
        try {
            scanStates();
            scanAlpha();
            scanInitialState();
            scanFinalStates();
            scanTransitions();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Mark appearing warnings in final report
     */
    private static void markWarnings() {
        // If no final states
        if (FINAL_STATES.size() == 0) {
            REPORT.markWarning(1);
        }

        // If some states are not reachable from the initial state, but connected with other states somehow (!disjoint)
        if (!CHECKER.areAllStatesReachable(STATES, initialState)) {
            REPORT.markWarning(2);
        }

        // If there is more than one transition with the same transition token from particular state
        if (!CHECKER.isDeterministic(STATES)) {
            REPORT.markWarning(3);
        }
    }
}

/**
 * Class State implements node of the FSA with the name. Name of states cannot repeat
 */
class State {
    /**
     * All possible states which can be reached from this state
     */
    private ArrayList<State> possibleStatesToMove = new ArrayList<>();
    /**
     * All transition tokens which can be used from this state
     */
    private final ArrayList<Transition> transitions = new ArrayList<>();
    private final String name;

    State(String name) {
        this.name = name;
    }

    public ArrayList<State> getPossibleStatesToMove() {
        return possibleStatesToMove;
    }

    public void setPossibleStatesToMove(ArrayList<State> possibleStatesToMove) {
        this.possibleStatesToMove = possibleStatesToMove;
    }

    public ArrayList<Transition> getTransitions() {
        return transitions;
    }

    public String getName() {
        return name;
    }

    public void addPossibleTransition(State destState, Transition transition) {
        possibleStatesToMove.add(destState);
        transitions.add(transition);
    }
}

/**
 * Transition token
 *
 * @param name of the transition token
 */
record Transition(String name) {
}



// Mukhutdinov Artur

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Main {
    private static BufferedReader reader = null;
    private static BufferedWriter writer = null;
    private static final Checker checker = new Checker();
    private static final ReportFormation report = new ReportFormation();
    private static final ArrayList<State> states = new ArrayList<>();
    private static final ArrayList<Transition> alpha = new ArrayList<>();
    private static State initialState = null;
    private static final ArrayList<State> finalStates = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        scanFiles();

        makeFormattedInput();

        try {
            if (checker.isDisjoint(states, initialState)) {
                throw new DisjointStatesException();
            }
        } catch (DisjointStatesException e) {
            writer.write(e.toString());
            reader.close();
            writer.close();
            System.exit(0);
        }

        markWarnings();
        report.markCompleteness(checker.isComplete(states, alpha));

        writer.write(report.toString());

        reader.close();
        writer.close();
    }

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

    private static void scanStates() throws IOException {
        try {
            String string = reader.readLine();
            String[] stateNames = string.substring(8, string.length() - 1).split(",");

            if (string.substring(8, string.length() - 1).length() == 0) {
                throw new InputMalformedException();
            }

            for (String stateName : stateNames) {
                if (!checker.isStateNameCorrect(stateName)) {
                    throw new InputMalformedException();
                }

                states.add(new State(stateName));
            }

        } catch (IOException | InputMalformedException e) {
            writer.write(e.toString());
            reader.close();
            writer.close();
            System.exit(0);
        }
    }

    private static void scanAlpha() throws IOException {
        try {
            String string = reader.readLine();
            String[] transitionNames = string.substring(7, string.length() - 1).split(",");

            if (string.substring(7, string.length() - 1).length() == 0) {
                throw new InputMalformedException();
            }

            for (String transitionName : transitionNames) {
                if (!checker.isTransitionNameCorrect(transitionName)) {
                    throw new InputMalformedException();
                }

                alpha.add(new Transition(transitionName));
            }

        } catch (IOException | InputMalformedException e) {
            writer.write(e.toString());
            reader.close();
            writer.close();
            System.exit(0);
        }
    }

    private static void scanInitialState() throws IOException {
        try {
            String stateName = reader.readLine();
            stateName = stateName.substring(9, stateName.length() - 1);
            String[] tempString = stateName.split(",");

            if (stateName.length() == 0) {
                throw new InitialStateNotDefinedException();
            }

            if (tempString.length > 1) {
                throw new InputMalformedException();
            }

            initialState = getState(stateName);

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

    private static void scanFinalStates() throws IOException {
        try {
            String string = reader.readLine();
            String[] stateNames = string.substring(8, string.length() - 1).split(",");

            if (string.substring(8, string.length() - 1).length() == 0) {
                return;
            }

            for (String stateName : stateNames) {
                State tempState = getState(stateName);

                if (tempState == null) {
                    throw new IncorrectStateException(stateName);
                }

                finalStates.add(tempState);
            }

        } catch (IOException | IncorrectStateException e) {
            writer.write(e.toString());
            reader.close();
            writer.close();
            System.exit(0);
        }
    }

    private static void scanTransitions() throws IOException {
        try {
            String string = reader.readLine();
            String[] transitions = string.substring(7, string.length() - 1).split(",");

            if (string.substring(7, string.length() - 1).length() == 0) {
                return;
            }

            for (String transition : transitions) {
                String[] transitionSplit = transition.split(">");

                State sourceState = getState(transitionSplit[0]);
                if (sourceState == null) {
                    throw new IncorrectStateException(transitionSplit[0]);
                }

                Transition trans = getTransition(transitionSplit[1]);
                if (trans == null) {
                    throw new TransitionIsNotPresentedException(transitionSplit[1]);
                }

                State destState = getState(transitionSplit[2]);
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

    private static State getState(String stateName) {
        for (State state : states) {
            if (state.getName().equals(stateName)) {
                return state;
            }
        }
        return null;
    }

    private static Transition getTransition(String transitionName) {
        for (Transition transition : alpha) {
            if (transition.getName().equals((transitionName))) {
                return transition;
            }
        }
        return null;
    }

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

    private static void markWarnings() {
        if (finalStates.size() == 0) {
            report.markWarning(1);
        }

        if (!checker.areAllStatesReachable(states, initialState)) {
            report.markWarning(2);
        }

        if (!checker.isDeterministic(states)) {
            report.markWarning(3);
        }
    }
}

class Checker {
    public boolean isStateNameCorrect(String name) {
        for (Character c : name.toCharArray()) {
            if (! (isLetter(c) || isDigit(c)) ) {
                return false;
            }
        }
        return true;
    }

    public boolean isTransitionNameCorrect(String name) {
        for (Character c : name.toCharArray()) {
            if (! (isLetter(c) || isDigit(c) || c == 95) ) {
                return false;
            }
        }
        return true;
    }

    private boolean isLetter(Character c) {
        return (65 <= c && c <= 90) || (97 <= c && c <= 122);
    }

    private boolean isDigit(Character c) {
        return (48 <= c && c <= 57);
    }

    static ArrayList<State> reachedStates = new ArrayList<>();
    public boolean isDisjoint(ArrayList<State> states, State initialState) {
        getAllPossibleReachedStates(initialState);
        return reachedStates.size() != states.size();
    }

    private void getAllPossibleReachedStates(State initialState) {
        reachedStates.add(initialState);
        for (State state : initialState.getPossibleStatesToMove()) {
            if (!reachedStates.contains(state)) {
                getAllPossibleReachedStates(state);
            }
        }
    }

    static HashMap<State, Boolean> canBeVisited = new HashMap<>();
    public boolean areAllStatesReachable(ArrayList<State> states, State initialState) {
        for (State state : states) {
            canBeVisited.put(state, false);
        }

        canBeVisited.put(initialState, true);

        makeMove(initialState);

        return !canBeVisited.containsValue(false);
    }

    private void makeMove(State state) {
        for (State tempState : state.getPossibleStatesToMove()) {
            if (canBeVisited.get(tempState)) {
                continue;
            }
            canBeVisited.put(tempState, true);
            makeMove(tempState);
        }
    }

    public boolean isDeterministic(ArrayList<State> states) {
        for (State state : states) {
            int possibleTransitionsSize = state.getPossibleTransitions().size();
            int distinctPossibleTransitionsSize = state.getPossibleTransitions().stream().distinct().toList().size();
            if (possibleTransitionsSize > distinctPossibleTransitionsSize) {
                return false;
            }
        }

        return true;
    }

    public boolean isComplete(ArrayList<State> states, ArrayList<Transition> transitions) {
        int transitionsCount = 0;

        for (State state : states) {
            transitionsCount += state.getPossibleTransitions().stream().distinct().toList().size();
        }

        return transitionsCount == states.size() * transitions.size();
    }
}

class State {
    ArrayList<State> possibleStatesToMove = new ArrayList<>();
    ArrayList<Transition> transitions = new ArrayList<>();
    String name;

    State(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<State> getPossibleStatesToMove() {
        return possibleStatesToMove;
    }

    public ArrayList<Transition> getPossibleTransitions() {
        return transitions;
    }

    public void addPossibleTransition(State destState, Transition transition) {
        possibleStatesToMove.add(destState);
        transitions.add(transition);
    }
}

class Transition {
    String name;

    Transition(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class IncorrectStateException extends Exception {
    String stateName;
    IncorrectStateException(String stateName) {
        this.stateName = stateName;
    }
    public String toString() {
        return "Error:\nE1: A state '" + stateName + "' is not in the set of states\n";
    }
}

class DisjointStatesException extends Exception {
    @Override
    public String toString() {
        return "Error:\nE2: Some states are disjoint\n";
    }
}

class TransitionIsNotPresentedException extends Exception {
    String transitionName;
    TransitionIsNotPresentedException(String transitionName) {
        this.transitionName = transitionName;
    }
    public String toString() {
        return "Error:\nE3: A transition '" + transitionName +  "' is not represented in the alphabet\n";
    }
}

class InitialStateNotDefinedException extends Exception {
    @Override
    public String toString() {
        return "Error:\nE4: Initial state is not defined\n";
    }
}

class InputMalformedException extends Exception {
    @Override
    public String toString() {
        return "Error:\nE5: Input file is malformed\n";
    }
}

class WarningDoesNotExistException extends Exception {
    @Override
    public String toString() {
        return "Error:\nE6: Warning with this number does not exist\n";
    }
}

class ReportFormation {
    private static final String W1 = "W1: Accepting state is not defined";
    private static final String W2 = "W2: Some states are not reachable from the initial state";
    private static final String W3 = "W3: FSA is nondeterministic";
    private static final String[] warningsMessages = new String[]{W1, W2, W3};
    boolean[] warningsAppearance = {false, false, false};
    boolean completeness;


    public void markWarning(int warningNumber) {
        try {
            if (! (1 <= warningNumber && warningNumber <= 3) ) {
                throw new WarningDoesNotExistException();
            }

            warningsAppearance[warningNumber - 1] = true;
        } catch (WarningDoesNotExistException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    public void markCompleteness(boolean isComplete) {
        completeness = isComplete;
    }

    @Override
    public String toString() {
        StringBuilder outputString = new StringBuilder("FSA is ");

        outputString.append(completeness ? "complete\n" : "incomplete\n");

        boolean areWarningsAppear = false;
        StringBuilder warningMessagesText = new StringBuilder("Warning:\n");
        for (int i = 0; i < 3; i++) {
            if (warningsAppearance[i]) {
                areWarningsAppear = true;
                warningMessagesText.append(warningsMessages[i]).append("\n");
            }
        }

        if (areWarningsAppear) {
            outputString.append(warningMessagesText);
        }

        return outputString.toString();
    }
}



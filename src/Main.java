public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}

class State {
    String name;

    public String getName() {
        return name;
    }
}

class Transition {
    String name;

    public String getName() {
        return name;
    }
}

class IncorrectStateException extends Exception {
    public String toString(State state) {
        return "Error:\nE1: A state" + state.getName() + "is not in the set of states";
    }
}

class DisjointStatesException extends Exception {
    @Override
    public String toString() {
        return "Error:\nE2: Some states are disjoint";
    }
}

class TransitionIsNotPresentedException extends Exception {
    public String toString(Transition transition) {
        return "Error:\nE3: A transition" + transition.getName() +  "is not represented in the alphabet";
    }
}

class InitialStateNotDefinedException extends Exception {
    @Override
    public String toString() {
        return "Error:\nE4: Initial state is not defined";
    }
}

class InputMalformedException extends Exception {
    @Override
    public String toString() {
        return "Error:\nE5: Input file is malformed";
    }
}

class WarningDoesNotExistException extends Exception {
    @Override
    public String toString() {
        return "Error:\nE6: Warning with this number does not exist";
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
            if (! (0 <= warningNumber && warningNumber <= 2) ) {
                throw new WarningDoesNotExistException();
            }

            warningsAppearance[warningNumber] = true;
        } catch (WarningDoesNotExistException e) {
            System.out.println(e.toString());
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



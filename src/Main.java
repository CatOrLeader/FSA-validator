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



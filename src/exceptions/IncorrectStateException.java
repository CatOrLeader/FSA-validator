package exceptions;

public class IncorrectStateException extends Exception {
    private final String stateName;

    public IncorrectStateException(String stateName) {
        this.stateName = stateName;
    }

    public String toString() {
        return "Error:\nE1: A state '" + stateName + "' is not in the set of states\n";
    }
}

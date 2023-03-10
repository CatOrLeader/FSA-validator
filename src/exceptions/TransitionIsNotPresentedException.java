package exceptions;

public class TransitionIsNotPresentedException extends Exception {
    private final String transitionName;

    public TransitionIsNotPresentedException(String transitionName) {
        this.transitionName = transitionName;
    }

    public String toString() {
        return "Error:\nE3: A transition '" + transitionName + "' is not represented in the alphabet\n";
    }
}

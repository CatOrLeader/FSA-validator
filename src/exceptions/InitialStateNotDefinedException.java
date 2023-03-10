package exceptions;

public class InitialStateNotDefinedException extends Exception {
    @Override
    public String toString() {
        return "Error:\nE4: Initial state is not defined\n";
    }
}

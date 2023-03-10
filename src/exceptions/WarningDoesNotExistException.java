package exceptions;

public class WarningDoesNotExistException extends Exception {
    @Override
    public String toString() {
        return "Error:\nE6: Warning with this number does not exist\n";
    }
}

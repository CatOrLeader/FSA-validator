package exceptions;

public class InputMalformedException extends Exception {
    @Override
    public String toString() {
        return "Error:\nE5: Input file is malformed\n";
    }
}

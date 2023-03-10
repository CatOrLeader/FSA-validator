package exceptions;

public class DisjointStatesException extends Exception {
    @Override
    public String toString() {
        return "Error:\nE2: Some states are disjoint\n";
    }
}

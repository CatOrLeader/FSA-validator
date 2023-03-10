import exceptions.WarningDoesNotExistException;

/**
 * Class which represent the final report for the FSA
 */
public class ReportFormation {
    private static final String W1 = "W1: Accepting state is not defined";
    private static final String W2 = "W2: Some states are not reachable from the initial state";
    private static final String W3 = "W3: FSA is nondeterministic";
    private static final String[] warningsMessages = {W1, W2, W3};
    private static final boolean[] warningsAppearance = {false, false, false};
    private static boolean completeness;

    /**
     * Mark that warning should be in the final output
     *
     * @param warningNumber integer number of warning
     */
    public void markWarning(int warningNumber) {
        try {
            if (!(1 <= warningNumber && warningNumber <= 3)) {
                throw new WarningDoesNotExistException();
            }

            warningsAppearance[warningNumber - 1] = true;
        } catch (WarningDoesNotExistException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Mark if the FSA is complete
     *
     * @param isComplete FSA condition
     */
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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The most essential class of the program. Implement all significant tests for the FSA validation
 */
public class Checker {
    private static final int A_ASCII = 65;
    private static final int Z_ASCII = 90;
    private static final int a_ASCII = 97;
    private static final int z_ASCII = 122;
    private static final int UNDERSCORE_ASCII = 95;
    private static final int ASCII_0 = 48;
    private static final int ASCII_9 = 57;
    /**
     * Array of states for checking if the FSA disjoint
     */
    private static final ArrayList<State> REACHED_STATES = new ArrayList<>();
    /**
     * Array of States which is the same as the original FSA States but without directions;
     * (e.g. 1 --> 2 now 1 <--> 2)
     */
    private static final ArrayList<State> UNDIRECTED_STATES = new ArrayList<>();
    /**
     * HashMap which shows how many states can be visited from the initial state
     */
    private static final HashMap<State, Boolean> IS_VISITED_FROM_INITIAL_STATE = new HashMap<>();

    /**
     * Check if the state name is correct according to task's condition
     *
     * @param name current state name for checking
     * @return true - if name is correct; Otherwise, false
     */
    public boolean isStateNameCorrect(String name) {
        for (Character c : name.toCharArray()) {
            if (!(isLetter(c) || isDigit(c))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the transition token name is correct according to task's condition
     *
     * @param name current transition token name for checking
     * @return true - if name is correct; Otherwise, false
     */
    public boolean isTransitionNameCorrect(String name) {
        for (Character c : name.toCharArray()) {
            if (!(isLetter(c) || isDigit(c) || c == UNDERSCORE_ASCII)) {
                return false;
            }
        }
        return true;
    }

    private boolean isLetter(Character c) {
        return (A_ASCII <= c && c <= Z_ASCII) || (a_ASCII <= c && c <= z_ASCII);
    }

    private boolean isDigit(Character c) {
        return (ASCII_0 <= c && c <= ASCII_9);
    }

    /**
     * Check if states are disjoint somehow
     *
     * @param states       original possible states
     * @param initialState original initial state
     * @return true - if states are disjoint; Otherwise, false
     */
    public boolean isDisjoint(ArrayList<State> states, State initialState) {
        createStatesUndirected(states);
        State initialStateInUndirectedGraph = UNDIRECTED_STATES.get(UNDIRECTED_STATES.indexOf(getState(initialState)));
        getAllPossibleReachedStates(initialStateInUndirectedGraph);
        return REACHED_STATES.size() != states.size();
    }

    /**
     * Create an undirected set of states from origin set of states.
     * Undirected set will be saved in static variable UNDIRECTED_STATES.
     *
     * @param states original set of all possible states
     */
    private void createStatesUndirected(ArrayList<State> states) {
        // Copy all states from the original set to undirectedStatesSet
        for (State state : states) {
            State newState = new State(state.getName());
            UNDIRECTED_STATES.add(newState);
        }

        // Implement old transitions between states in the new field on undirectedStatesSet.
        // (Transitions were obtained from the original states transitions)
        for (State state : states) {
            for (State stateInner : state.getPossibleStatesToMove()) {
                UNDIRECTED_STATES.get(UNDIRECTED_STATES.indexOf(getState(state))).addPossibleTransition(
                        UNDIRECTED_STATES.get(UNDIRECTED_STATES.indexOf(getState(stateInner))), null
                );
            }
        }

        // Implement addition transition from [tail_state] to [head_state].
        // (e.g. [original set = (1: 2, 3; 2: ; 3: ); new set = (1: 2, 3; 2: 1 ; 3: 1)])
        for (State state : UNDIRECTED_STATES) {
            ArrayList<State> tempList = (ArrayList<State>) state.getPossibleStatesToMove().clone();
            for (State innerState : tempList) {
                innerState.addPossibleTransition(state, null);
            }
        }

        // Delete duplicates of states in every array of possible transitions from each state
        for (State state : UNDIRECTED_STATES) {
            state.setPossibleStatesToMove(new ArrayList<>(
                    state.getPossibleStatesToMove().stream().distinct().toList()
            ));
        }
    }

    /**
     * Get state from the undirected set of states
     *
     * @param state needed state
     * @return State - if state is found; Otherwise, false
     */
    private State getState(State state) {
        for (State stateUnd : UNDIRECTED_STATES) {
            if (stateUnd.getName().equals(state.getName())) {
                return stateUnd;
            }
        }
        return null;
    }

    /**
     * Fill REACHED_STATE by true if state connected with the other states
     *
     * @param initialState initial state for starting process of iteration over all states
     */
    private void getAllPossibleReachedStates(State initialState) {
        REACHED_STATES.add(initialState);
        for (State state : initialState.getPossibleStatesToMove()) {
            if (!REACHED_STATES.contains(state)) {
                getAllPossibleReachedStates(state);
            }
        }
    }

    /**
     * Check if all states are reachable from the initial state
     *
     * @param states       original set of all possible states
     * @param initialState original initial state of FSA
     * @return true - if all states are accessible from the initial state; Otherwise, false
     */
    public boolean areAllStatesReachable(ArrayList<State> states, State initialState) {
        // Fill the HashMap with false - we are didn't appear in this places now
        for (State state : states) {
            IS_VISITED_FROM_INITIAL_STATE.put(state, false);
        }

        IS_VISITED_FROM_INITIAL_STATE.put(initialState, true);

        makeMove(initialState);

        return !IS_VISITED_FROM_INITIAL_STATE.containsValue(false);
    }

    /**
     * Support step for iteration over all possible moves from the initial state
     *
     * @param state state from which we check every possible transition to another states
     */
    private void makeMove(State state) {
        for (State tempState : state.getPossibleStatesToMove()) {
            if (IS_VISITED_FROM_INITIAL_STATE.get(tempState)) {
                continue;
            }
            IS_VISITED_FROM_INITIAL_STATE.put(tempState, true);
            makeMove(tempState);
        }
    }

    /**
     * Check if there is more than one transition with the same transition token for some state
     *
     * @param states original set of possible states
     * @return true - if there is no more than one transition with the same transition token for each token;
     * Otherwise, false
     */
    public boolean isDeterministic(ArrayList<State> states) {
        for (State state : states) {
            int possibleTransitionsNumber = state.getTransitions().size();
            int distinctPossibleTransitionsNumber = state.getTransitions().stream().distinct().toList().size();
            if (possibleTransitionsNumber > distinctPossibleTransitionsNumber) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checking if the FSA is complete
     *
     * @param states      original set of possible states
     * @param transitions original set of possible transition tokens
     * @return true - if FSA is complete; Otherwise, false
     */
    public boolean isComplete(ArrayList<State> states, ArrayList<Transition> transitions) {
        int transitionsCount = 0;

        for (State state : states) {
            transitionsCount += state.getTransitions().stream().distinct().toList().size();
        }

        // Check if the number of transitions equal to needed number of transitions of FSA to be complete
        return transitionsCount == states.size() * transitions.size();
    }
}

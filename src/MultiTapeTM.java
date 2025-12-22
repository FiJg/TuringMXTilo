import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MultiTapeTM {
    private final int numTapes;
    private final Set<String> states;
    private final String startState;
    private final Set<String> acceptStates;
    private final Set<String> alphabet;
    private final String blank;

    private String currentState;
    private int steps;

    public MultiTapeTM(int numTapes,
                       Set<String> states,
                       String startState,
                       Set<String> acceptStates,
                       Set<String> alphabet,

                       String blank,
                       List<String> tapeNames) {
        this.numTapes = numTapes;
        this.states = states;
        this.startState = startState;
        this.acceptStates = acceptStates;
        this.alphabet = alphabet;
        this.blank = blank;
        this.currentState = startState;
        this.steps = 0;
    }


    public boolean isHalting() {
        return acceptStates.contains(currentState) || "ERROR_HALT".equals(currentState);
    }

    public int getSteps() {
        return steps;
    }

    public String getCurrentState() {
        return currentState;
    }


    public void step() {
        if (isHalting()) {
            return;
        }

        steps++;
    }

    public void printConfig() {
        printSeparator();
        printStateInfo();
        System.out.println();
    }

    public static String makeKey(String state, List<String> symbols) {
        return state + "|" + String.join(",", symbols);
    }


    private List<String> initializeTapeNames(List<String> names) {
        return (names != null) ? names :
                IntStream.rangeClosed(1, numTapes)
                        .mapToObj(i -> "Tape " + i)
                        .collect(Collectors.toList());
    }

    private List<Tape> initializeTapes() {
        return IntStream.range(0, numTapes)
                .mapToObj(i -> new Tape(blank))
                .collect(Collectors.toList());
    }

    private boolean isValidTapeIndex(int index) {
        return index >= 0 && index < numTapes;
    }



    private void printSeparator() {
        System.out.println("-".repeat(60));
    }

    private void printStateInfo() {
        System.out.printf("Step: %d | State: %s%n", steps, currentState);
    }


    private void appendMachineInfo(StringBuilder sb) {
        sb.append("Num of Tapes: ").append(numTapes).append("\n");
        sb.append("Start State: ").append(startState).append("\n");
        sb.append("Accept States: ").append(sortedList(acceptStates)).append("\n");
        sb.append("Alfabet: ").append(sortedList(alphabet)).append("\n\n");
    }



    private List<String> sortedList(Set<String> set) {
        List<String> sorted = new ArrayList<>(set);
        Collections.sort(sorted);
        return sorted;
    }
}
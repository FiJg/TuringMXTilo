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
    private final Map<String, Transition> transitions;
    private final List<Tape> tapes;
    private final List<String> tapeNames;

    private String currentState;
    private int steps;

    public MultiTapeTM(int numTapes,
                       Set<String> states,
                       String startState,
                       Set<String> acceptStates,
                       Set<String> alphabet,
                       Map<String, Transition> transitions,
                       String blank,
                       List<String> tapeNames) {
        this.numTapes = numTapes;
        this.states = states;
        this.startState = startState;
        this.acceptStates = acceptStates;
        this.alphabet = alphabet;
        this.transitions = transitions;
        this.blank = blank;
        this.tapeNames = initializeTapeNames(tapeNames);
        this.tapes = initializeTapes();
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

    public List<Tape> getTapes() {
        return tapes;
    }

    public void setTape(int index, String content) {
        if (isValidTapeIndex(index)) {
            tapes.get(index).load(content);
        }
    }

    public void step() {
        if (isHalting()) {
            return;
        }

        List<String> readSymbols = readAllTapes();
        String key = makeKey(currentState, readSymbols);

        if (!transitions.containsKey(key)) {
            handleMissingTransition(readSymbols);
            return;
        }

        applyTransition(transitions.get(key));
        steps++;
    }

    public void printConfig() {
        printSeparator();
        printStateInfo();
        printAllTapes();
        System.out.println();
    }

    public String encode() {
        StringBuilder sb = new StringBuilder();

        appendHeader(sb);
        appendMachineInfo(sb);
        appendTransitions(sb);

        return sb.toString();
    }

    public static String makeKey(String state, List<String> symbols) {
        return state + "|" + String.join(",", symbols);
    }

    /* helpers */
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

    private List<String> readAllTapes() {
        return tapes.stream()
                .map(Tape::read)
                .collect(Collectors.toList());
    }

    private void handleMissingTransition(List<String> readSymbols) {
        System.out.println("[WARN] no transition found for state " + currentState + " and symbols " + readSymbols);
        currentState = "ERROR_HALT";
        acceptStates.add("ERROR_HALT");
    }

    private void applyTransition(Transition rule) {
        writeToAllTapes(rule.write());
        moveAllHeads(rule.moves());
        currentState = rule.nextState();
    }

    private void writeToAllTapes(List<String> symbols) {
        for (int i = 0; i < numTapes; i++) {
            tapes.get(i).write(symbols.get(i));
        }
    }

    private void moveAllHeads(List<Move> moves) {
        for (int i = 0; i < numTapes; i++) {
            tapes.get(i).moveHead(moves.get(i));
        }
    }

    private void printSeparator() {
        System.out.println("-".repeat(60));
    }

    private void printStateInfo() {
        System.out.printf("Step: %d | State: %s%n", steps, currentState);
    }

    private void printTape(int index) {
        String name = tapeNames.get(index);
        Tape.WindowView view = tapes.get(index).viewWindow(10);
        System.out.printf("%4s: %s%n", name, view.contentStr());
        System.out.printf("      %s%n", view.pointerStr());
    }

    private void printAllTapes() {
        for (int i = 0; i < numTapes; i++) {
            printTape(i);
        }
    }

    private void appendHeader(StringBuilder sb) {
        sb.append("----- machine encoding ---\n");
    }

    private void appendMachineInfo(StringBuilder sb) {
        sb.append("Num of Tapes: ").append(numTapes).append("\n");
        sb.append("Start State: ").append(startState).append("\n");
        sb.append("Accept States: ").append(sortedList(acceptStates)).append("\n");
        sb.append("Alfabet: ").append(sortedList(alphabet)).append("\n\n");
    }

    private void appendTransitions(StringBuilder sb) {
        sb.append("Transitions (Rules):\n");

        List<String> sortedKeys = new ArrayList<>(transitions.keySet());
        Collections.sort(sortedKeys);

        for (String key : sortedKeys) {
            appendTransitionRule(sb, key, transitions.get(key));
        }
    }

    private void appendTransitionRule(StringBuilder sb, String key, Transition rule) {
        String[] parts = key.split("\\|");
        String state = parts[0];
        String readStr = parts[1];

        String writeStr = String.join(", ", rule.write());
        String moveStr = rule.moves().stream()
                .map(Enum::toString)
                .collect(Collectors.joining(", "));

        sb.append(String.format("(%s, [%s]) -> (%s, [%s], [%s])%n",
                state, readStr, rule.nextState(), writeStr, moveStr));
    }

    private List<String> sortedList(Set<String> set) {
        List<String> sorted = new ArrayList<>(set);
        Collections.sort(sorted);
        return sorted;
    }
}
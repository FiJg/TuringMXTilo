import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TMBuilder {
    private final int numTapes;
    private final List<String> tapeNames;
    private final Set<String> states;
    private final Map<String, Transition> transitions;
    private final List<String> alphabet;
    private final Set<String> acceptStates;
    private String startState;

    public TMBuilder(int numTapes, List<String> tapeNames) {
        this.numTapes = numTapes;
        this.tapeNames = (tapeNames != null) ? tapeNames :
                IntStream.rangeClosed(1, numTapes)
                        .mapToObj(i -> "T" + i)
                        .collect(Collectors.toList());

        this.states = new HashSet<>();
        this.transitions = new HashMap<>();
        this.alphabet = Arrays.asList("0", "1", "#", "x");
        this.startState = "q_INIT";
        this.acceptStates = new HashSet<>(Collections.singletonList("HALT"));
    }

    /** simple add method
     * */
    public void add(String sourceState, String read, String targetState, String write, String move) {
        add(sourceState, parseArray(read), targetState, parseArray(write), parseArray(move));
    }

    private String[] parseArray(String input) {
        if (input == null) return new String[numTapes];
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .map(s -> (s.equals("_") || s.equalsIgnoreCase("null")) ? null : s)
                .toArray(String[]::new);
    }


    public void add(String sourceState, String[] readPattern, String targetState,
                    String[] writePattern, String[] movePattern) {
        states.add(sourceState);
        states.add(targetState);

        if (readPattern.length != numTapes) {
            throw new IllegalArgumentException("pattern length problems. exppected " + numTapes);
        }

        List<List<String>> readCombinations = expandWildcards(readPattern);

        for (List<String> concreteRead : readCombinations) {
            List<String> finalWrite = buildWritePattern(concreteRead, writePattern);
            List<Move> finalMove = buildMovePattern(movePattern);

            String key = MultiTapeTM.makeKey(sourceState, concreteRead);
            transitions.put(key, new Transition(targetState, finalWrite, finalMove));
        }
    }

    public MultiTapeTM build() {
        Set<String> allStates = new HashSet<>(states);
        allStates.addAll(acceptStates);

        return new MultiTapeTM(
                numTapes, allStates, startState, acceptStates,
                new HashSet<>(alphabet), transitions, "#", tapeNames
        );
    }

    /* helpers */

    private List<List<String>> expandWildcards(String[] readPattern) {
        List<List<String>> possibilities = new ArrayList<>();
        for (String symbol : readPattern) {
            if ("*".equals(symbol)) {
                possibilities.add(new ArrayList<>(alphabet));
            } else {
                possibilities.add(Collections.singletonList(symbol));
            }
        }
        return cartesianProduct(possibilities);
    }

    private List<String> buildWritePattern(List<String> concreteRead, String[] writePattern) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < numTapes; i++) {
            result.add(writePattern[i] != null ? writePattern[i] : concreteRead.get(i));
        }
        return result;
    }

    private List<Move> buildMovePattern(String[] movePattern) {
        List<Move> result = new ArrayList<>();
        for (String moveStr : movePattern) {
            if (moveStr == null) result.add(Move.STAY);
            else result.add(parseMove(moveStr));
        }
        return result;
    }

    private Move parseMove(String moveStr) {
        return switch (moveStr) {
            case "L" -> Move.LEFT;
            case "R" -> Move.RIGHT;
            case "S" -> Move.STAY;
            default -> throw new IllegalArgumentException(" unknown move : " + moveStr);
        };
    }

    private static <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
        List<List<T>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        for (List<T> list : lists) {
            List<List<T>> newResult = new ArrayList<>();
            for (List<T> partial : result) {
                for (T item : list) {
                    List<T> newPartial = new ArrayList<>(partial);
                    newPartial.add(item);
                    newResult.add(newPartial);
                }
            }
            result = newResult;
        }
        return result;
    }
}
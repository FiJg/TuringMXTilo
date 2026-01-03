import java.util.*;

// for head movement
enum Move {
    LEFT,
    RIGHT,
    STAY
}

record Transition(String nextState, List<String> write, List<Move> moves) {}

public class Tape {
    private static final int DEFAULT_WINDOW_SIZE = 15;
    private static final String DEFAULT_BLANK = "#";

    private final String blank;
    private int head;
    private final Map<Integer, String> cells;

    public Tape(String blank) {
        this.blank = blank;
        this.head = 0;
        this.cells = new HashMap<>();
    }

    public Tape() {
        this(DEFAULT_BLANK);
    }

    /* tape ops */

    public String read() {
        return cells.getOrDefault(head, blank);
    }

    public void write(String symbol) {
        if (isBlankSymbol(symbol)) {
            cells.remove(head);
        } else {
            cells.put(head, symbol);
        }
    }

    public void moveHead(Move move) {
        switch (move) {
            case LEFT -> head--;
            case RIGHT -> head++;
            case STAY -> {}
        }
    }

    public void load(String content) {
        clearTape();
        loadContent(content);
        positionHeadAtStart();
    }

    public String content() {
        if (cells.isEmpty()) {
            return "";
        }

        return buildContentString();
    }

    public WindowView viewWindow(int radius) {
        int start = head - radius;
        int end = head + radius;

        String contentStr = buildWindowContent(start, end);
        String pointerStr = buildPointer(radius);

        return new WindowView(contentStr, pointerStr);
    }

    public WindowView viewWindow() {
        return viewWindow(DEFAULT_WINDOW_SIZE);
    }

    // Record for window view
    public record WindowView(String contentStr, String pointerStr) {}


    private void positionHeadAtStart() {
        if (!cells.isEmpty()) {
            head = Collections.min(cells.keySet());
        }
    }


    private void clearTape() {
        cells.clear();
        head = 0;
    }

    /* helpers */

    private boolean isBlankSymbol(String symbol) {
        return symbol.equals(blank);
    }

    private void loadContent(String content) {
        for (int i = 0; i < content.length(); i++) {
            String symbol = String.valueOf(content.charAt(i));
            if (!isBlankSymbol(symbol)) {
                cells.put(i, symbol);
            }
        }
    }

    private String buildContentString() {
        int minIdx = Collections.min(cells.keySet());
        int maxIdx = Collections.max(cells.keySet());

        StringBuilder sb = new StringBuilder();
        for (int i = minIdx; i <= maxIdx; i++) {
            sb.append(cells.getOrDefault(i, blank));
        }

        return sb.toString();
    }

    private String buildWindowContent(int start, int end) {
        List<String> symbols = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            symbols.add(cells.getOrDefault(i, blank));
        }
        return String.join(" ", symbols);
    }

    private String buildPointer(int radius) {
        int pointerPosition = radius * 2;
        return " ".repeat(pointerPosition) + "^";
    }

}

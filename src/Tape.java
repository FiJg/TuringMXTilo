import java.util.*;

// for head movement
enum Move {
    LEFT,
    RIGHT,
    STAY
}

public class Tape {

    private final String blank;
    private int head;
    private final Map<Integer, String> cells;

    public Tape(String blank) {
        this.blank = blank;
        this.head = 0;
        this.cells = new HashMap<>();
    }

    public String read() {
        return cells.getOrDefault(head, blank);
    }

    public void write(String symbol) {
        cells.put(head, symbol);
    }

    public void moveHead(Move move) {
        switch (move) {
            case LEFT -> head--;
            case RIGHT -> head++;
            case STAY -> {}
        }
    }

    public void load(String content) {
    }

    private void clearTape() {
        cells.clear();
        head = 0;
    }

    private void positionHeadAtStart() {
        if (!cells.isEmpty()) {
            head = Collections.min(cells.keySet());
        }
    }


}

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TapeTest {

    private Tape tape;

    @Before
    public void setUp() {
        tape = new Tape("#");
    }

    @Test
    public void testInitState() {
        // tape should start blank or empty
        assertEquals("New tape should read blank symbol", "#", tape.read());
        assertEquals("New tape content should be empty String", "", tape.content());
    }

    @Test
    public void testReadWrite() {
        tape.write("A");
        assertEquals("should read back the symbol", "A", tape.read());

        tape.write("B");
        assertEquals("should overwrite the previous value", "B", tape.read());
    }


    @Test
    public void testMovementAndPosition() {
        // write on 0 pos
        tape.write("X");

        // go right to 1
        tape.moveHead(Move.RIGHT);
        assertEquals("new cell should be blank symbol", "#", tape.read());
        tape.write("Y");

        // go left back to 0
        tape.moveHead(Move.LEFT);
        assertEquals("should return to OG value at index 0", "X", tape.read());

        tape.moveHead(Move.RIGHT);
        assertEquals("value at index 1 should stay", "Y", tape.read());
    }

    @Test
    public void testLoadContent() {
        String input = "101";
        tape.load(input);

        // check if head is reset to start
        assertEquals("1st char matches", "1", tape.read());

        tape.moveHead(Move.RIGHT);
        assertEquals("2nd char matches", "0", tape.read());

        tape.moveHead(Move.RIGHT);
        assertEquals("3rd char matches", "1", tape.read());

        // 4 th char  - auto blank
        tape.moveHead(Move.RIGHT);
        assertEquals("should be blank", "#", tape.read());
    }

}

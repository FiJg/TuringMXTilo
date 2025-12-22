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
        // tape should start blank
        assertEquals("New tape should read blank symbol", "#", tape.read());
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


}

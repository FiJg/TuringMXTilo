import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class TMBuilderTest {

    private TMBuilder builder;

    @Before
    public void setUp() {
        // init 1 tape builder for testing rules
        builder = new TMBuilder(1, List.of("TestTape"));
    }

    @Test
    public void testSimpleExplicitRule() {

        builder.add("q0", "0", "q1", "1", "R");

        MultiTapeTM tm = builder.build();

        //if tape has 0.
        tm.setTape(0, "0");
        tm.step();

        assertEquals("State should change to q1", "q1", tm.getCurrentState());
        assertEquals("Should write 1", "1", tm.getTapes().get(0).read());
    }


    @Test
    public void testMovementParsing() {
        builder.add("q0", "0", "q1", "_", "R");
        builder.add("q1", "#", "q2", "_", "L");

        MultiTapeTM tm = builder.build();

        tm.setTape(0, "0");

        // s1 - read 0, move R
        tm.step();
        assertEquals("should be in q1", "q1", tm.getCurrentState());
        // if R, it should be at index 1 -  blank.
        assertEquals("head should be at index 1 -reading blank", "#", tm.getTapes().get(0).read());

        // s2: read #, move L - back to start
        tm.step();
        assertEquals("should be in q2", "q2", tm.getCurrentState());
        assertEquals("head should be back at index 0 - reading 0", "0", tm.getTapes().get(0).read());

    }

    @Test
    public void testNoTransitionFound() {
        builder.add("q0", "0", "q1", "0", "S"); // Only handles '0'

        MultiTapeTM tm = builder.build();
        tm.setTape(0, "1");

        tm.step();

        assertTrue("machine should be in halt state", tm.isHalting());
        assertEquals("state should be ERROR_HALT ", "ERROR_HALT", tm.getCurrentState());
    }

}
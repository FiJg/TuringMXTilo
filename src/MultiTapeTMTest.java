import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class MultiTapeTMTest {

    private TMBuilder builder;

    @Before
    public void setUp() {
        builder = new TMBuilder(2, List.of("InputTape", "WorkTape"));
    }

    @Test
    public void testInit() {
        builder.add("q_START", "*,*", "q_END", "_,_", "S,S");
        MultiTapeTM tm = builder.build();

        assertEquals("Should start in defined start state", "q_START", tm.getCurrentState());
        assertEquals("Step count should be 0", 0, tm.getSteps());
        assertEquals("Should have 2 tapes", 2, tm.getTapes().size());
        assertFalse("Should not be halting initially", tm.isHalting());
    }



    @Test
    public void testMultiTapeSync() {

    }

    @Test
    public void testHaltNormal() {

    }

    @Test
    public void testHaltError() {

    }
}

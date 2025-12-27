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

        //MultiTapeTM tm = builder.build();

        // if tape has 0.
       // tm.setTape(0, "0");
       // tm.step();

       // assertEquals("State should change to q1", "q1", tm.getCurrentState());
      //  assertEquals("Should write 1", "1", tm.getTapes().get(0).read());
    }

    @Test
    public void testMovementParsing() {
        builder.add("q0", "0", "q1", "_", "R");
        builder.add("q1", "#", "q2", "_", "L");


    }


}
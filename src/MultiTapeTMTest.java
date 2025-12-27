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
    public void testInitialization() {
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
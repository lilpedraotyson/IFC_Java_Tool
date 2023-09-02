package tecnico.ulisboa.pt;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class LatticeTest {

    @Test
    public void test1() throws FileNotFoundException {
        Lattice lattice = new Lattice(new File("target/test-classes/lattice.txt"));

        assertEquals(lattice.toString(), "Top level: high\n" +
                "Bottom level: low\n" +
                "high: \n" +
                "low: high\n");

        assertAll(() -> assertEquals("high", lattice.join("high", "low")),
                () -> assertEquals("low", lattice.meet("high", "low")),
                () -> assertEquals("high", lattice.join("low", "high")),
                () -> assertEquals("low", lattice.meet("low", "high")));
    }

    @Test
    public void test2() throws FileNotFoundException {
        Lattice lattice = new Lattice(new File("target/test-classes/lattice1.txt"));

        assertEquals(lattice.toString(), "Top level: top\n" +
                "Bottom level: bot\n" +
                "top: \n" +
                "bot: mid\n" +
                "mid: top\n");

        assertAll(() -> assertEquals("top", lattice.join("bot", "top")),
                () -> assertEquals("mid", lattice.meet("top", "mid")),
                () -> assertEquals("bot", lattice.join("bot", "bot")),
                () -> assertEquals("bot", lattice.meet("bot", "top")));
    }
}
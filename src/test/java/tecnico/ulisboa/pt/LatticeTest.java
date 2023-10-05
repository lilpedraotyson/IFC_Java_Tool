package tecnico.ulisboa.pt;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class LatticeTest {

    @Test
    public void test1() throws IOException {
        Lattice lattice = new Lattice(new File("target/test-classes/tests/lattice.txt"));
        String fileContent = new String(Files.readAllBytes(Paths.get("target/test-classes/results/result_lattice.txt")));

        assertEquals(lattice.toString().trim().replaceAll("\\s+", " "), fileContent.trim().replaceAll("\\s+", " "));

        assertAll(() -> assertEquals("high", lattice.join("high", "low")),
                () -> assertEquals("low", lattice.meet("high", "low")),
                () -> assertEquals("high", lattice.join("low", "high")),
                () -> assertEquals("low", lattice.meet("low", "high")));
    }

    @Test
    public void test2() throws IOException {
        Lattice lattice = new Lattice(new File("target/test-classes/tests/lattice1.txt"));
        String fileContent = new String(Files.readAllBytes(Paths.get("target/test-classes/results/result_lattice1.txt")));

        assertEquals(lattice.toString().trim().replaceAll("\\s+", " "), fileContent.trim().replaceAll("\\s+", " "));

        assertAll(() -> assertEquals("top", lattice.join("bot", "top")),
                () -> assertEquals("mid", lattice.meet("top", "mid")),
                () -> assertEquals("bot", lattice.join("bot", "bot")),
                () -> assertEquals("bot", lattice.meet("bot", "top")));
    }

    @Test
    public void test3() throws IOException {
        Lattice lattice = new Lattice(new File("target/test-classes/tests/lattice.txt"));
        ASTParser ast = new ASTParser(lattice, "target/test-classes/tests/ClassTest.java");
        ASTParser ast1 = new ASTParser(lattice, "target/test-classes/tests/ClassTest1.java");
        ASTParser ast2 = new ASTParser(lattice, "target/test-classes/tests/ClassTest2.java");

        String fileContent = new String(Files.readAllBytes(Paths.get("target/test-classes/results/resultClass.java")));
        String fileContent1 = new String(Files.readAllBytes(Paths.get("target/test-classes/results/resultClass1.java")));
        String fileContent2 = new String(Files.readAllBytes(Paths.get("target/test-classes/results/resultClass2.java")));

        assertEquals(ast.toString().trim().replaceAll("\\s+", " "), fileContent.trim().replaceAll("\\s+", " "));
        assertEquals(ast1.toString().trim().replaceAll("\\s+", " "), fileContent1.trim().replaceAll("\\s+", " "));
        assertEquals(ast2.toString().trim().replaceAll("\\s+", " "), fileContent2.trim().replaceAll("\\s+", " "));
    }

    @Test
    public void test4() throws IOException {
        Lattice lattice = new Lattice(new File("target/test-classes/tests/lattice1.txt"));
        ASTParser ast = new ASTParser(lattice, "target/test-classes/tests/ClassTest.java");
        ASTParser ast1 = new ASTParser(lattice, "target/test-classes/tests/ClassTest1.java");
        ASTParser ast2 = new ASTParser(lattice, "target/test-classes/tests/ClassTest2.java");

        String fileContent = new String(Files.readAllBytes(Paths.get("target/test-classes/results/resultClass3.java")));
        String fileContent1 = new String(Files.readAllBytes(Paths.get("target/test-classes/results/resultClass4.java")));
        String fileContent2 = new String(Files.readAllBytes(Paths.get("target/test-classes/results/resultClass5.java")));

        assertEquals(ast.toString().trim().replaceAll("\\s+", " "), fileContent.trim().replaceAll("\\s+", " "));
        assertEquals(ast1.toString().trim().replaceAll("\\s+", " "), fileContent1.trim().replaceAll("\\s+", " "));
        assertEquals(ast2.toString().trim().replaceAll("\\s+", " "), fileContent2.trim().replaceAll("\\s+", " "));
    }
}
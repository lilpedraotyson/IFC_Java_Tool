package tecnico.ulisboa.pt;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class DeclassificationTest {

    @Test
    public void test1() throws IOException {
        Lattice lattice = new Lattice(new File("target/test-classes/tests/lattice1.txt"));
        ASTParser ast = new ASTParser(lattice, "target/test-classes/tests/DeclassificationTest.java");

        String fileContent = new String(Files.readAllBytes(Paths.get("target/test-classes/results/result_declassification.java")));

        assertEquals(ast.toString().trim().replaceAll("\\s+", " "), fileContent.trim().replaceAll("\\s+", " "));
    }

    @Test
    public void test2() throws IOException {
        Lattice lattice = new Lattice(new File("target/test-classes/tests/lattice1.txt"));
        ASTParser ast = new ASTParser(lattice, "target/test-classes/tests/DeclassificationTest1.java");

        String fileContent = new String(Files.readAllBytes(Paths.get("target/test-classes/results/result_declassification1.java")));

        assertEquals(ast.toString().trim().replaceAll("\\s+", " "), fileContent.trim().replaceAll("\\s+", " "));
    }
}
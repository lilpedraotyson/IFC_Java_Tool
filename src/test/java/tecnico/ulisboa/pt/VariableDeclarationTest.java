package tecnico.ulisboa.pt;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class VariableDeclarationTest {

    @Test
    public void test1() throws IOException {
        Lattice lattice = new Lattice(new File("target/test-classes/tests/lattice.txt"));
        ASTParser ast = new ASTParser(lattice, "target/test-classes/tests/VariableDeclarationTest.java");
        ASTParser ast1 = new ASTParser(lattice, "target/test-classes/tests/VariableDeclarationTest1.java");

        String fileContent = new String(Files.readAllBytes(Paths.get("target/test-classes/results/result_variable_declaration.java")));
        String fileContent1 = new String(Files.readAllBytes(Paths.get("target/test-classes/results/result_variable_declaration1.java")));

        assertEquals(ast.toString().trim().replaceAll("\\s+", " "), fileContent.trim().replaceAll("\\s+", " "));
        assertEquals(ast1.toString().trim().replaceAll("\\s+", " "), fileContent1.trim().replaceAll("\\s+", " "));
    }

    @Test
    public void test2() throws IOException {
        Lattice lattice = new Lattice(new File("target/test-classes/tests/lattice1.txt"));
        ASTParser ast = new ASTParser(lattice, "target/test-classes/tests/VariableDeclarationTest2.java");
        ASTParser ast1 = new ASTParser(lattice, "target/test-classes/tests/VariableDeclarationTest3.java");

        String fileContent = new String(Files.readAllBytes(Paths.get("target/test-classes/results/result_variable_declaration2.java")));
        String fileContent1 = new String(Files.readAllBytes(Paths.get("target/test-classes/results/result_variable_declaration3.java")));

        assertEquals(ast.toString().trim().replaceAll("\\s+", " "), fileContent.trim().replaceAll("\\s+", " "));
        assertEquals(ast1.toString().trim().replaceAll("\\s+", " "), fileContent1.trim().replaceAll("\\s+", " "));
    }

    @Test
    public void test3() throws IOException {
        Lattice lattice = new Lattice(new File("target/test-classes/tests/lattice1.txt"));
        ASTParser ast = new ASTParser(lattice, "target/test-classes/tests/VariableDeclarationTest4.java");

        String fileContent = new String(Files.readAllBytes(Paths.get("target/test-classes/results/result_variable_declaration4.java")));

        assertEquals(ast.toString().trim().replaceAll("\\s+", " "), fileContent.trim().replaceAll("\\s+", " "));
    }
}
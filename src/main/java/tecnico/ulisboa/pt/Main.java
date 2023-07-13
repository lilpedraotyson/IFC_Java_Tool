package tecnico.ulisboa.pt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        File output = new File("src/main/resources/Output.java");
        FileWriter myWriter = new FileWriter(output);
        Lattice lattice = new Lattice();

        lattice.addVertex("top");
        lattice.addVertex("left");
        lattice.addVertex("right");
        lattice.addVertex("bot");

        lattice.setTop("top");
        lattice.setBot("bot");

        lattice.addEdge("bot", "left");
        lattice.addEdge("bot", "right");
        lattice.addEdge("left", "top");
        lattice.addEdge("right", "top");

        lattice.depht();

        ASTParser ast = new ASTParser(lattice, "Application.java", "meet");

        //System.out.print(ast);
        myWriter.write(ast.toString());
        myWriter.close();
        //ast.SecurityLevelsToClasses();

        /*System.out.println(lattice);
        System.out.println("\njoin");
        System.out.println(lattice.join("bot", "top"));
        System.out.println("\nmeet");
        System.out.println(lattice.meet("bot", "top"));*/
    }
}
package tecnico.ulisboa.pt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        File output = new File("src/main/resources/Output.java");
        File input_lattice = new File("src/main/resources/lattice.txt");

        Lattice lattice = new Lattice(input_lattice);

        /*lattice.addVertex("top");
        lattice.addVertex("mid");
        lattice.addVertex("bot");

        lattice.setTop("top");
        lattice.setBot("bot");

        lattice.addEdge("bot", "mid");
        lattice.addEdge("mid", "top");

        lattice.depht();*/
        System.out.println(lattice);

        /*System.out.println(lattice.meet("bot", "top"));
        System.out.println(lattice.join("bot", "top"));*/
        ASTParser ast = new ASTParser(lattice, "Application.java", "meet");

        //System.out.print(ast);
        FileWriter myWriter = new FileWriter(output);
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
package tecnico.ulisboa.pt;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Lattice {
    private Map<String, String> matrix = new HashMap<>();
    private String top;
    private String bot;
    private Map<String, Integer> level_depht = new HashMap<>();

    private String combination = "";

    public Lattice(File input) throws FileNotFoundException {
        Scanner myReader = new Scanner(input);
        int count = 0;
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            String levels[] = data.split(" ");
            if (count == 0) {
                this.combination = data;
            } else if (count == 1) {
                this.setTop(levels[0]);
                this.setBot(levels[1]);
            } else
                this.addEdge(levels[0], levels[1]);
            count++;
        }
        this.depht();
    }
    public void setTop(String level) {
        this.addVertex(level);
        this.top = level;
    }

    public void setBot(String level) {
        this.addVertex(level);
        this.bot = level;
    }

    public String getTop() {return this.top;}

    public String getCombinationClasses() {return this.combination;}

    public String getBot() {
        return this.bot;
    }

    public Map<String, String> getMatrix() {return this.matrix;}

    public Map<String, Integer> getLevelDepht() {return this.level_depht;}

    public void addVertex(String level) {
        if (!this.matrix.containsKey(level))
            this.matrix.put(level, "");
    }

    public int levelCount() {
        return this.matrix.keySet().size();
    }

    public void addEdge(String source, String destination) {
        addVertex(source);
        addVertex(destination);

        this.matrix.replace(source, destination);
    }

    public void depht() {
        Map<String, Boolean> visited = new HashMap<>();

        for (String level : matrix.keySet()) {
            visited.put(level, false);
        }

        LinkedList<String> queue = new LinkedList<>();

        visited.replace(this.bot, true);
        queue.add(this.bot);
        this.level_depht.put(this.bot, 0);

        String node = "";
        while (queue.size() != 0) {
            node = queue.poll();

            if (node.equals(this.top)) {
                break;
            }

            String can_flow = matrix.get(node);
            if (!visited.get(can_flow)) {
                visited.replace(can_flow, true);
                queue.add(can_flow);
                this.level_depht.put(can_flow, this.level_depht.get(node)+1);
            }
        }

        /*StringBuilder builder = new StringBuilder();
        builder.append("\n");

        for (String level : this.level_depht.keySet()) {
            builder.append(level + ": ");
            builder.append(this.level_depht.get(level) + ", ");
            builder.append("\n");
        }
        System.out.print(builder);*/
    }

    public String meet(String level1, String level2) {
        String result = "";
        int level1_depht = this.level_depht.get(level1);
        int level2_depht = this.level_depht.get(level2);
        int lmin = Math.min(this.level_depht.get(level1), this.level_depht.get(level2));

        if (lmin == level1_depht) {
            result = level1;
        } else if (lmin == level2_depht) {
            result = level2;
        } else {
            System.out.println("Erro meet");
        }

        return result;
    }

    public String join(String level1, String level2) {
        String result = "";
        int level1_depht = this.level_depht.get(level1);
        int level2_depht = this.level_depht.get(level2);
        int lmax = Math.max(this.level_depht.get(level1), this.level_depht.get(level2));

        if (lmax == level1_depht) {
            result = level1;
        } else if (lmax == level2_depht) {
            result = level2;
        } else {
            System.out.println("Erro meet");
        }
        return result;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Top level: " + this.top + "\n");
        builder.append("Bottom level: " + this.bot + "\n");

        for (String level : matrix.keySet()) {
            builder.append(level + ": " + matrix.get(level));
            builder.append("\n");
        }

        return (builder.toString());
    }
}

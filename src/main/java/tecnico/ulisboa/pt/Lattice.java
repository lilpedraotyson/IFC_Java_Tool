package tecnico.ulisboa.pt;

import java.util.*;
import java.util.stream.Collectors;

public class Lattice {
    private Map<String, List<String>> matrix = new HashMap<>();
    private Map<String, List<String>> reverse_matrix = new HashMap<>();
    private String top;
    private String bot;
    private Map<String, Integer> level_depht = new HashMap<>();

    public void setTop(String level) {
        this.top = level;
    }

    public void setBot(String level) {
        this.bot = level;
    }

    public String getTop() {return this.top;}

    public String getBot() {
        return this.bot;
    }

    public Map<String, List<String>> getMatrix() {return this.matrix;}

    public Map<String, List<String>> getReverseMatrix() {
        return this.reverse_matrix;
    }

    public void addVertex(String level) {
        this.matrix.put(level, new LinkedList<>());
        this.reverse_matrix.put(level, new LinkedList<>());
    }

    public int levelCount() {
        return this.matrix.keySet().size();
    }

    public void addEdge(String source, String destination) {
        if (!this.matrix.containsKey(source))
            addVertex(source);

        if (!this.matrix.containsKey(destination))
            addVertex(destination);

        this.matrix.get(source).add(destination);
        this.reverse_matrix.get(destination).add(source);
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

        String node;
        while (queue.size() != 0) {
            node = queue.poll();

            List<String> can_flow = matrix.get(node);
            for (String level : can_flow) {
                if (!visited.get(level)) {
                    visited.replace(level, true);
                    queue.add(level);
                    this.level_depht.put(level, this.level_depht.get(node)+1);
                }
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

    public void DFS_reverse(String node, Map<String, Boolean> visited, List<String> ancestors) {
        visited.replace(node, true);

        List<String> can_flow = reverse_matrix.get(node);
        for (String l : can_flow) {
            if (!visited.get(l)) {
                DFS_reverse(l, visited, ancestors);
                ancestors.add(l);
            }
        }
    }

    public void DFS(String node, Map<String, Boolean> visited, List<String> descendants) {
        visited.replace(node, true);

        List<String> can_flow = matrix.get(node);
        for (String l : can_flow) {
            if (!visited.get(l)) {
                DFS(l, visited, descendants);
                descendants.add(l);
            }
        }
    }

    public List<String> getAncestors(String level) {
        List<String> ancestors = new LinkedList<>();

        Map<String, Boolean> visited = new HashMap<>();

        for (String l : reverse_matrix.keySet()) {
            visited.put(l, false);
        }
        ancestors.add(level);
        DFS_reverse(level, visited, ancestors);

        return ancestors;
    }

    public List<String> getDescendants(String level) {
        List<String> descendants = new LinkedList<>();

        Map<String, Boolean> visited = new HashMap<>();

        for (String l : matrix.keySet()) {
            visited.put(l, false);
        }
        descendants.add(level);
        DFS(level, visited, descendants);

        return descendants;
    }

    public String meet(String level1, String level2) {
        String result = "";
        List<String> ancestors1 = getAncestors(level1);
        List<String> ancestors2 = getAncestors(level2);

        Set<String> common_levels = ancestors1.stream().distinct().filter(ancestors2::contains).collect(Collectors.toSet());

        int highest = -1;
        int current_depht;
        for (String common : common_levels) {
            current_depht = this.level_depht.get(common);
            if (current_depht > highest)
                result = common;
                highest = current_depht;
        }
        return result;
    }

    public String join(String level1, String level2) {
        String result = "";
        List<String> descendants1 = getDescendants(level1);
        List<String> descendants2 = getDescendants(level2);

        Set<String> common_levels = descendants1.stream().distinct().filter(descendants2::contains).collect(Collectors.toSet());

        int lowest = matrix.keySet().size();
        int current_depht;
        for (String common : common_levels) {
            current_depht = this.level_depht.get(common);
            if (current_depht < lowest)
                result = common;
                lowest = current_depht;
        }
        return result;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Top level: " + this.top + "\n");
        builder.append("Bottom level: " + this.bot + "\n");

        for (String level : matrix.keySet()) {
            builder.append(level + ": ");
            for (String can_flow_level : matrix.get(level)) {
                builder.append(can_flow_level + ", ");
            }
            builder.append("\n");
        }

        builder.append("Reverse: " + "\n");
        for (String level : reverse_matrix.keySet()) {
            builder.append(level + ": ");
            for (String can_flow_level : reverse_matrix.get(level)) {
                builder.append(can_flow_level + ", ");
            }
            builder.append("\n");
        }

        return (builder.toString());
    }
}

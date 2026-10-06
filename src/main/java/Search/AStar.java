package Search;

import Estado.Estado;
import Node.Node;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashSet;

public class AStar {

    private final char[][] map;
    private int numExpanded; // nodes expanded in the last search

    public AStar(char[][] map) {
        this.map = map;
    }

    // minimum possible cost per step (ensures heuristic is admissible)
    private static final int MIN_STEP_COST = 1;

    public static int manhattan(Estado current, Estado goal) {
        int deltaRow = Math.abs(current.line() - goal.line());
        int deltaCol = Math.abs(current.column() - goal.column());
        return (deltaRow + deltaCol) * MIN_STEP_COST;
    }

    public static int cellCost(char c) {
        switch (c) {
            case 'M': return 200;
            case 'A': return 30;
            case 'F': return 15;
            case 'R': return 5;
            default:  return 1;
        }
    }

    /** Returns true if the character is terrain (M, A, F, R or .). */
    public static boolean isTerrain(char c) {
        return c == 'M' || c == 'A' || c == 'F' || c == 'R' || c == '.';
    }

    public List<Estado> validNeighbors(Estado current) {
        List<Estado> neighbors = new ArrayList<>();

        int row = current.line();
        int col = current.column();

        int[][] offsets = { {0, 1}, {0, -1}, {1, 0}, {-1, 0} }; // right, left, down, up

        for (int[] d : offsets) {
            int newRow = row + d[0];
            int newCol = col + d[1];

            if (newRow >= 0 && newRow < map.length && newCol >= 0 && newCol < map[0].length) {
                neighbors.add(new Estado(newRow, newCol));
            }
        }

        return neighbors;
    }

    public Node search(Estado start, Estado goal) {
        return search(start, goal, true);
    }

    public Node search(Estado start, Estado goal, boolean useHeuristic) {
        PriorityQueue<Node> frontier = new PriorityQueue<>();
        Set<Estado> expanded = new HashSet<>();

        // 1. Initial node: g = cost of starting cell (paid on entry), h = manhattan or 0, parent = null
        int initialG = cellCost(map[start.line()][start.column()]);
        int initialH = useHeuristic ? manhattan(start, goal) : 0;
        Node initialNode = new Node(initialG, initialH, null, start);
        frontier.add(initialNode);

        // 2. While the frontier is not empty
        while (!frontier.isEmpty()) {

            // 3. Remove node with lowest f
            Node current = frontier.poll();
            Estado currentState = current.getState();

            // 4. If already expanded -> skip
            if (expanded.contains(currentState)) {
                continue;
            }

            // 5. If it's the goal -> return the node
            // Note: goal is returned before being added to expanded,
            // so it's not counted in numExpanded.
            if (currentState.equals(goal)) {
                this.numExpanded = expanded.size();
                return current;
            }

            // 6. Mark state as expanded
            expanded.add(currentState);

            // 7. For each valid neighbor: create a Node and add to frontier
            for (Estado neighbor : validNeighbors(currentState)) {
                if (!expanded.contains(neighbor)) {
                    int newG = current.getG() + cellCost(map[neighbor.line()][neighbor.column()]);
                    int newH = useHeuristic ? manhattan(neighbor, goal) : 0;
                    Node newNode = new Node(newG, newH, current, neighbor);
                    frontier.add(newNode);
                }
            }
        }

        this.numExpanded = expanded.size();
        return null; // no path found
    }

    public int getNumExpanded() {
        return numExpanded;
    }
}

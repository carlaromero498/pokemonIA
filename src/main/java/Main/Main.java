package Main;

import Estado.Estado;
import Search.AStar;
import Search.CostTable;
import Search.RouteAStar;
import Search.RouteNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        // 1. Read the map
        List<String> lines = Files.readAllLines(Path.of("kanto.txt"));
        char[][] map = new char[lines.size()][];
        for (int i = 0; i < lines.size(); i++) {
            map[i] = lines.get(i).toCharArray();
        }

        // 2. Pre-calculate the cost table (Level 1 A*)
        System.out.println("Starting Level 1 (Cost Table pre-calculation)...");
        CostTable table = new CostTable(map);

        // 3. Configure Level 2 search weight (accepts optional CLI argument, e.g. 2.0 or 1.2)
        double weight = (args.length > 0) ? Double.parseDouble(args[0]) : RouteAStar.DEFAULT_WEIGHT;
        System.out.printf("\nStarting Level 2 (High-level TSP search, W = %.3f)...\n", weight);
        RouteAStar tspSearch = new RouteAStar(table, weight);

        long startTime = System.currentTimeMillis();
        RouteNode solution = tspSearch.search();
        long duration = System.currentTimeMillis() - startTime;

        // 4. Output the results
        if (solution == null) {
            System.out.println("No solution found!");
            return;
        }

        System.out.println("\n=== SOLUTION FOUND ===");
        System.out.println("Search time: " + duration + " ms");
        System.out.println("Expanded nodes (Level 2): " + tspSearch.getNumExpanded());

        // Origin cell cost paid once at route beginning
        int originCost = AStar.cellCost(
            map[table.getPosition(CostTable.START_INDEX).line()]
               [table.getPosition(CostTable.START_INDEX).column()]);

        int totalRouteCost = originCost + solution.getG();
        System.out.println("Total Route Cost: " + totalRouteCost);

        // Gym visit order
        List<Character> visitOrder = tspSearch.getSymbolOrder(solution);
        System.out.println("Visit Order: " + visitOrder);

        // 5. Reconstruct the full cell-by-cell path on the grid (Level 1 detailed path)
        System.out.println("\n=== RECONSTRUCTING FULL GRID PATH ===");
        long reconStart = System.currentTimeMillis();
        List<Estado> fullPath = tspSearch.reconstructFullPath(map, solution);
        long reconDuration = System.currentTimeMillis() - reconStart;

        System.out.println("Reconstruction time: " + reconDuration + " ms");
        System.out.println("Total cells in path: " + fullPath.size() + " (steps: " + (fullPath.size() - 1) + ")");

        // Verification: calculate actual sum of cell costs along fullPath
        int verifiedCost = 0;
        for (Estado cell : fullPath) {
            verifiedCost += AStar.cellCost(map[cell.line()][cell.column()]);
        }
        System.out.println("Path verified cost: " + verifiedCost + " (Matches Total Route Cost? " 
            + (verifiedCost == totalRouteCost ? "YES OK!" : "MISMATCH!") + ")");

        // Preview path
        System.out.println("\nPath start: " + fullPath.get(0) + " ['" + map[fullPath.get(0).line()][fullPath.get(0).column()] + "']");
        System.out.println("Path end:   " + fullPath.get(fullPath.size() - 1) + " ['" + map[fullPath.get(fullPath.size() - 1).line()][fullPath.get(fullPath.size() - 1).column()] + "']");
        System.out.println("First 5 steps: " + fullPath.subList(0, Math.min(5, fullPath.size())));
        System.out.println("Last 5 steps:  " + fullPath.subList(Math.max(0, fullPath.size() - 5), fullPath.size()));
    }
}

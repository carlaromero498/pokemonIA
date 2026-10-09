package Main;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Estado.Estado;
import Map.MapLoader;
import Map.MapWindow;
import Search.AStar;
import Search.CostTable;
import Search.HillClimbing;
import Search.RouteAStar;
import Search.RouteNode;
import Search.Solution;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Carregar mapa
            String mapFilePath = "./kanto.txt";
            MapLoader loader = new MapLoader(mapFilePath);
            char[][] map = loader.getMapa();
            System.out.println("Map loaded: " + loader.getRows() + "x" + loader.getCols());

            // 2. Otimização de Batalhas (Hill Climbing - Pessoa 2)
            System.out.println("\n--- 1. BATTLE PLANNING (Hill Climbing - Person 2) ---");
            HillClimbing battlePlanner = new HillClimbing();
            Solution battlePlan = battlePlanner.createInitialSolution();
            List<Double> battleTimes = battlePlanner.calculateBattleTimes(battlePlan);
            double battleCost = battlePlanner.calculateCost(battlePlan);

            Map<Character, Double> battleTimesByGym = new LinkedHashMap<>();
            for (int gymIndex = 0; gymIndex < battlePlanner.getGymCount(); gymIndex++) {
                char gymSymbol = battlePlanner.getGymSymbol(gymIndex);
                battleTimesByGym.put(gymSymbol, battleTimes.get(gymIndex));
            }
            System.out.printf("Estimated total battle cost: %.2f time units.%n", battleCost);

            // 3. Otimização da Rota (A* / TSP - Pessoa 1)
            System.out.println("\n--- 2. ROUTE PLANNING (A* / TSP - Person 1) ---");
            System.out.println("Calculating Level 1 Cost Table between 26 points...");
            CostTable costTable = new CostTable(map);

            double weight = (args.length > 0) ? Double.parseDouble(args[0]) : 1.4;
            System.out.printf("Running Level 2 Route A* (W = %.3f)...%n", weight);
            RouteAStar routeAStar = new RouteAStar(costTable, weight);

            long searchStart = System.currentTimeMillis();
            RouteNode solution = routeAStar.search();
            long searchDuration = System.currentTimeMillis() - searchStart;

            if (solution == null) {
                throw new IllegalStateException("Failed to find optimal route.");
            }

            // Ordem ótima dos ginásios e rota completa na grelha
            List<Character> visitOrder = routeAStar.getSymbolOrder(solution);
            List<Estado> route = routeAStar.reconstructFullPath(map, solution);

            int originCost = AStar.cellCost(map[costTable.getPosition(CostTable.START_INDEX).line()][costTable
                    .getPosition(CostTable.START_INDEX).column()]);
            int routeCost = originCost + solution.getG();
            int expandedStates = routeAStar.getNumExpanded();
            double totalCost = routeCost + battleCost;

            // 4. Resumo Final Exigido pelo Enunciado
            System.out.println("\n=======================================================");
            System.out.println("                   FINAL RESULTS                       ");
            System.out.println("=======================================================");
            System.out.println("Optimal Gym Visit Order: " + visitOrder);
            System.out.println("Route Length: " + route.size() + " cells (" + (route.size() - 1) + " steps)");
            System.out.printf("Search Time (Route A*): %d ms%n", searchDuration);
            System.out.printf("A* Expanded States: %,d%n", expandedStates);
            System.out.printf("Route Cost: %d minutes%n", routeCost);
            System.out.printf("Battle Cost: %.2f minutes%n", battleCost);
            System.out.printf("TOTAL COST (Route + Battles): %.2f minutes%n", totalCost);
            System.out.println("=======================================================\n");

            // 5. Interface Gráfica (Pessoa 3)
            String statsInfo = String.format(
                    "Route Cost: %d min | Battle Cost: %.2f min | Total: %.2f min | A* Expanded Nodes: %,d",
                    routeCost, battleCost, totalCost, expandedStates);

            System.out.println("Opening GUI with animated route...");
            MapWindow.showMapWithPath(map, route, battleTimesByGym, 25, statsInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

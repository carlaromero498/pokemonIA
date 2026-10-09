package Main;

import java.util.ArrayList;
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

            // 2. Otimização da Rota (A* - Pessoa 1)
            System.out.println("\n--- 1. ROUTE PLANNING (A* - Person 1) ---");
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

            // 3. Experimento de Busca Local ( - Pessoa 2)
            System.out.println("\n--- 2. LOCAL SEARCH EXPERIMENT (Hill Climbing - Person 2) ---");
            int numExperiments = 30;
            System.out.printf("Running %d simulations to analyze minimum, average, and standard deviation...%n",
                    numExperiments);

            HillClimbing battlePlanner = new HillClimbing();
            List<Double> battleCosts = new ArrayList<>(numExperiments);


            List<Double> totalCosts = new ArrayList<>(numExperiments);

            Solution bestPlan = null;
            double bestBattleCost = Double.MAX_VALUE;


            for (int i = 0; i < numExperiments; i++) {
                Solution initialPlan = battlePlanner.createInitialSolution();

                Solution plan = battlePlanner.hillClimb(initialPlan);

                double bCost = battlePlanner.calculateCost(plan);
                double tCost = routeCost + bCost;

                battleCosts.add(bCost);
                totalCosts.add(tCost);

                if (bCost < bestBattleCost) {
                    bestBattleCost = bCost;
                    bestPlan = plan;
                }
            }
            System.out.println("Experimentos registrados: " + battleCosts.size());
            if (bestPlan == null || !battlePlanner.isValidSolution(bestPlan)) {
                throw new IllegalStateException("No valid battle plan was found.");
            }
            battlePlanner.printFinalEnergies(bestPlan);
            battlePlanner.printBattlePlan(bestPlan);
            System.out.println(
                    "¿Best valid plan? " + battlePlanner.isValidSolution(bestPlan)
            );


            // Estatísticas de Batalha
            double worstBattleCost = battleCosts.stream().mapToDouble(Double::doubleValue).max().orElse(0);
            double avgBattleCost = battleCosts.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double sumSqDiffBattle = 0;
            for (double val : battleCosts) {
                sumSqDiffBattle += Math.pow(val - avgBattleCost, 2);
            }
            double stdDevBattle = Math.sqrt(sumSqDiffBattle / (numExperiments - 1));

            // Estatísticas de Custo Total
            double bestTotalCost = routeCost + bestBattleCost;
            double worstTotalCost = routeCost + worstBattleCost;
            double avgTotalCost = routeCost + avgBattleCost;
            double stdDevTotal = stdDevBattle; // Adicionar uma constante não altera o desvio padrão

            // Mapear os tempos de batalha da MELHOR solução encontrada
            List<Double> bestBattleTimes = battlePlanner.calculateBattleTimes(bestPlan);
            Map<Character, Double> battleTimesByGym = new LinkedHashMap<>();
            for (int gymIndex = 0; gymIndex < battlePlanner.getGymCount(); gymIndex++) {
                char gymSymbol = battlePlanner.getGymSymbol(gymIndex);
                battleTimesByGym.put(gymSymbol, bestBattleTimes.get(gymIndex));
            }

            // 4. Resumo Final Exigido pelo Enunciado
            System.out.println("\n=======================================================");
            System.out.println("                   FINAL RESULTS                       ");
            System.out.println("=======================================================");
            System.out.println("Optimal Gym Visit Order: " + visitOrder);
            System.out
                    .println("Route Length:            " + route.size() + " cells (" + (route.size() - 1) + " steps)");
            System.out.printf("Search Time (Route A*):  %d ms%n", searchDuration);
            System.out.printf("A* Expanded States:      %,d%n", expandedStates);
            System.out.printf("Fixed Route Cost:        %d minutes%n", routeCost);
            System.out.println("-------------------------------------------------------");
            System.out.printf("Simulations Performed:   %,d%n", numExperiments);
            System.out.printf("Battle Cost (Min):       %.2f minutes%n", bestBattleCost);
            System.out.printf("Battle Cost (Max):       %.2f minutes%n", worstBattleCost);
            System.out.printf("Battle Cost (Average):   %.2f minutes%n", avgBattleCost);
            System.out.printf("Battle Cost (Std Dev):   %.2f minutes%n", stdDevBattle);
            System.out.println("-------------------------------------------------------");
            System.out.printf("TOTAL COST (Best/Min):   %.2f minutes%n", bestTotalCost);
            System.out.printf("TOTAL COST (Max):        %.2f minutes%n", worstTotalCost);
            System.out.printf("TOTAL COST (Average):    %.2f minutes%n", avgTotalCost);
            System.out.printf("TOTAL COST (Std Dev):    %.2f minutes%n", stdDevTotal);
            System.out.println("=======================================================\n");

            // 5. Interface Gráfica (Pessoa 3)
            String statsInfo = String.format(
                    "Route: %d min | Best Battle: %.2f min | BEST TOTAL: %.2f min (Avg: %.2f, StdDev: %.2f) | A* Expanded: %,d",
                    routeCost, bestBattleCost, bestTotalCost, avgTotalCost, stdDevTotal, expandedStates);

            System.out.println("Opening GUI with animated route...");
            MapWindow.showMapWithPath(map, route, battleTimesByGym, 25, statsInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

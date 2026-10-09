package Search;

import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import Estado.Estado;
import Node.Node;

public class RouteAStar {

    public static final double DEFAULT_WEIGHT = 1.4;

    private final CostTable costTable;
    private final double weight;
    private int numExpanded;

    public RouteAStar(CostTable costTable) {
        this(costTable, DEFAULT_WEIGHT);
    }

    public RouteAStar(CostTable costTable, double weight) {
        this.costTable = costTable;
        this.weight = weight;
    }

    /**
     * Heuristic: lower bound of the remaining cost
     * Calculated as: sum of the minimum incoming edges for all unvisited gyms + min
     * incoming for U
     */
    public int heuristic(int currentLocation, int visitedMask) {
        // Se já visitámos todos, só falta saltar para o U. O custo desse salto é exato
        // O teste de admissibilidade já provou que isto não sobrestima
        if (RouteState.allGymsVisited(visitedMask)) {
            return costTable.getCost(currentLocation, CostTable.DEST_INDEX);
        }

        int lowerBoundSum = 0;

        // Para cada ginásio por visitar, encontrar a aresta de entrada mais barata
        for (int i = 0; i < CostTable.NUM_GYMS; i++) {
            if (!RouteState.isGymVisited(visitedMask, i)) {
                int minCostToI = Integer.MAX_VALUE;

                // Pode vir da localização atual
                minCostToI = Math.min(minCostToI, costTable.getCost(currentLocation, i));

                // Ou pode vir de qualquer outro ginásio que AINDA NÃO FOI VISITADO
                for (int j = 0; j < CostTable.NUM_GYMS; j++) {
                    if (i != j && !RouteState.isGymVisited(visitedMask, j)) {
                        minCostToI = Math.min(minCostToI, costTable.getCost(j, i));
                    }
                }
                lowerBoundSum += minCostToI;
            }
        }

        // Para o U, tem de vir da localização atual ou de algum ginásio por visitar
        int minCostToU = costTable.getCost(currentLocation, CostTable.DEST_INDEX);
        for (int j = 0; j < CostTable.NUM_GYMS; j++) {
            if (!RouteState.isGymVisited(visitedMask, j)) {
                minCostToU = Math.min(minCostToU, costTable.getCost(j, CostTable.DEST_INDEX));
            }
        }
        lowerBoundSum += minCostToU;

        return lowerBoundSum;
    }

    /**
     * Executes the high-level A* search to find the optimal gym visit order.
     */
    public RouteNode search() {
        PriorityQueue<RouteNode> frontier = new PriorityQueue<>();

        // Usamos BitSet para poupar memória e tempo (55 MB vs 436 MB)
        int maxEncodedStates = CostTable.TOTAL_POINTS * (1 << CostTable.NUM_GYMS);
        System.out.println("Allocating closed list (BitSet of " + maxEncodedStates + " bits)...");
        java.util.BitSet expanded = new java.util.BitSet(maxEncodedStates);

        // 1. Nó inicial: Origem, máscara 0
        int startState = RouteState.encode(CostTable.START_INDEX, 0);
        int initialH = (int) (heuristic(CostTable.START_INDEX, 0) * weight);
        RouteNode initialNode = new RouteNode(startState, 0, initialH, null);
        frontier.add(initialNode);

        // 2. Enquanto a fronteira não estiver vazia
        while (!frontier.isEmpty()) {

            // 3. Tira o nó com menor f
            RouteNode current = frontier.poll();
            int currentState = current.getEncodedState();

            // 4. Se o estado já foi expandido -> descarta
            if (expanded.get(currentState)) {
                continue;
            }

            int currentLoc = RouteState.decodeLocation(currentState);
            int currentMask = RouteState.decodeMask(currentState);

            // 5. Teste de Objetivo: Estamos no U e já visitámos tudo?
            if (currentLoc == CostTable.DEST_INDEX && RouteState.allGymsVisited(currentMask)) {
                // Ao contrário do ArrayList, contar no BitSet é o número de bits a true
                // (cardinality)
                this.numExpanded = expanded.cardinality();
                return current;
            }

            // 6. Marca o estado como expandido
            expanded.set(currentState);

            // 7. Expande as ações possíveis consoante a máscara
            if (RouteState.allGymsVisited(currentMask)) {
                // MÁSCARA COMPLETA: A única ação válida é saltar para o destino 'U'

                int costToU = costTable.getCost(currentLoc, CostTable.DEST_INDEX);
                int nextLoc = CostTable.DEST_INDEX;
                int nextState = RouteState.encode(nextLoc, currentMask);

                if (!expanded.get(nextState)) {
                    int newG = current.getG() + costToU;
                    int newH = 0; // heuristic to U from U is 0
                    RouteNode nextNode = new RouteNode(nextState, newG, newG + newH, current);
                    frontier.add(nextNode);
                }

            } else {
                // MÁSCARA INCOMPLETA: Pode saltar para qualquer ginásio ainda não visitado

                for (int gymLoc = 0; gymLoc < CostTable.NUM_GYMS; gymLoc++) {
                    if (!RouteState.isGymVisited(currentMask, gymLoc)) {

                        int stepCost = costTable.getCost(currentLoc, gymLoc);
                        int nextMask = RouteState.visitGym(currentMask, gymLoc);
                        int nextState = RouteState.encode(gymLoc, nextMask);

                        if (!expanded.get(nextState)) {
                            int newG = current.getG() + stepCost;
                            int newH = (int) (heuristic(gymLoc, nextMask) * weight);
                            RouteNode nextNode = new RouteNode(nextState, newG, newG + newH, current);
                            frontier.add(nextNode);
                        }
                    }
                }
            }
        }

        this.numExpanded = expanded.cardinality();
        return null; // sem caminho
    }

    public int getNumExpanded() {
        return numExpanded;
    }

    public double getWeight() {
        return weight;
    }

    // Reconstructs the ordered list of location indices from the solution node
    public List<Integer> getLocationOrder(RouteNode solution) {
        List<Integer> order = new ArrayList<>();
        RouteNode curr = solution;
        while (curr != null) {
            order.add(RouteState.decodeLocation(curr.getEncodedState()));
            curr = curr.getParent();
        }
        Collections.reverse(order);
        return order;
    }

    // Reconstructs the ordered list of symbols ('1', gyms, 'U') from the solution
    // node

    public List<Character> getSymbolOrder(RouteNode solution) {
        List<Integer> locs = getLocationOrder(solution);
        List<Character> symbols = new ArrayList<>(locs.size());
        for (int loc : locs) {
            symbols.add(costTable.getSymbol(loc));
        }
        return symbols;
    }

    // Reconstructs the complete path on the grid by executing A* between
    // each consecutive point
    public List<Estado> reconstructFullPath(char[][] map, RouteNode solution) {
        List<Integer> locations = getLocationOrder(solution);
        List<Estado> fullPath = new ArrayList<>();
        AStar gridAStar = new AStar(map);

        for (int i = 0; i < locations.size() - 1; i++) {
            Estado startPos = costTable.getPosition(locations.get(i));
            Estado goalPos = costTable.getPosition(locations.get(i + 1));

            Node legGoal = gridAStar.search(startPos, goalPos);
            if (legGoal == null) {
                throw new IllegalStateException("Failed to find path between "
                        + costTable.getSymbol(locations.get(i)) + " and "
                        + costTable.getSymbol(locations.get(i + 1)));
            }

            // Extract leg path from goal back to start
            List<Estado> leg = new ArrayList<>();
            Node curr = legGoal;
            while (curr != null) {
                leg.add(curr.getState());
                curr = curr.getParent();
            }
            Collections.reverse(leg);

            // Append to fullPath avoiding duplicate connecting cells
            if (fullPath.isEmpty()) {
                fullPath.addAll(leg);
            } else {
                fullPath.addAll(leg.subList(1, leg.size()));
            }
        }

        return fullPath;
    }
}

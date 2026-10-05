package Search;

import Estado.Estado;
import Node.Node;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Pre-calculates A* distances between all 26 important points on the map.
 *
 * Indexing (aligned with the gym visited bitmask):
 *   Indices 0–23 → 24 gyms (2,3,4,5,6,7,8,9,B,C,D,E,G,H,I,J,K,L,N,O,P,Q,S,T)
 *   Index 24     → start point '1'
 *   Index 25     → destination 'U'
 *
 * Alternative 2: 325 A* runs (using symmetry), reusing already tested code.
 */
public class CostTable {

    public static final int TOTAL_POINTS = 26;
    public static final int NUM_GYMS = 24;
    public static final int START_INDEX = 24;
    public static final int DEST_INDEX = 25;

    // Gym order according to the assignment: symbol → index 0–23
    private static final String GYM_ORDER = "23456789BCDEGHIJKLNOPQST";

    private final int[][] costs;         // costs[i][j] = A* distance from point i to point j
    private final Estado[] positions;    // positions[i] = location of point i on the map
    private final Map<Character, Integer> symbolToIndex;

    public CostTable(char[][] map) {
        this.costs = new int[TOTAL_POINTS][TOTAL_POINTS];
        this.positions = new Estado[TOTAL_POINTS];
        this.symbolToIndex = buildMapping();

        findPoints(map);
        preCalculate(map);
    }

    /** Builds the symbol → index mapping. */
    private Map<Character, Integer> buildMapping() {
        Map<Character, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < GYM_ORDER.length(); i++) {
            map.put(GYM_ORDER.charAt(i), i);
        }
        map.put('1', START_INDEX);
        map.put('U', DEST_INDEX);
        return map;
    }

    /** Scans the map and stores the position of each important point. */
    private void findPoints(char[][] map) {
        int found = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                char c = map[i][j];
                if (!AStar.isTerrain(c)) {
                    Integer index = symbolToIndex.get(c);
                    if (index == null) {
                        throw new IllegalStateException(
                            "Unexpected character on the map: '" + c + "' at (" + i + "," + j + ")");
                    }
                    positions[index] = new Estado(i, j);
                    found++;
                }
            }
        }
        if (found != TOTAL_POINTS) {
            throw new IllegalStateException(
                "Expected " + TOTAL_POINTS + " important points, found " + found);
        }
    }

    /** Pre-calculates costs between all pairs using A* with symmetry (325 runs). */
    private void preCalculate(char[][] map) {
        AStar astar = new AStar(map);
        int runs = 0;

        for (int i = 0; i < TOTAL_POINTS; i++) {
            costs[i][i] = 0; // cost from a point to itself, excluding starting cost
            for (int j = i + 1; j < TOTAL_POINTS; j++) {
                Node result = astar.search(positions[i], positions[j]);
                if (result == null) {
                    throw new IllegalStateException(
                        "No path between point " + i + " and point " + j);
                }
                // Excludes the starting cell cost to avoid double counting when chaining steps.
                // When building a route, sum: cost(start) + d(start,g1) + d(g1,g2) + ... + d(gk,dest).
                int costWithoutStart = result.getG()
                    - AStar.cellCost(map[positions[i].line()][positions[i].column()]);
                costs[i][j] = costWithoutStart;
                costs[j][i] = costWithoutStart; // symmetry (all special points cost 1)
                runs++;
            }
        }
        System.out.println("Cost table pre-calculated: " + runs + " A* runs.");
    }

    /** Returns the cost between two points given their indices. */
    public int getCost(int from, int to) {
        return costs[from][to];
    }

    /** Returns the position of a point given its index. */
    public Estado getPosition(int index) {
        return positions[index];
    }

    /** Returns the index of a symbol (letter or digit). */
    public int getIndex(char symbol) {
        return symbolToIndex.get(symbol);
    }

    /** Returns the symbol corresponding to an index. */
    public char getSymbol(int index) {
        if (index == START_INDEX) return '1';
        if (index == DEST_INDEX) return 'U';
        return GYM_ORDER.charAt(index);
    }
}

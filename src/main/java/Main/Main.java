package Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Estado.Estado;
import Map.MapLoader;
import Map.MapWindow;
import Node.Node;
import Search.AStar;
import Search.HillClimbing;
import Search.Solution;

public class Main {
	public static void main(String[] args) {
		try {
			String mapFilePath = "./kanto.txt";
			MapLoader loader = new MapLoader(mapFilePath);
			char[][] map = loader.getMapa();
			System.out.println("Map loaded: " + loader.getRows() + "x" + loader.getCols());

			HillClimbing battlePlanner = new HillClimbing();
			Solution battlePlan = battlePlanner.createInitialSolution();
			List<Double> battleTimes = battlePlanner.calculateBattleTimes(battlePlan);
			Map<Character, Double> battleTimesByGym = new LinkedHashMap<>();
			List<Character> itinerary = new ArrayList<>();

			for (int gymIndex = 0; gymIndex < battlePlanner.getGymCount(); gymIndex++) {
				char gymSymbol = battlePlanner.getGymSymbol(gymIndex);
				itinerary.add(gymSymbol);
				battleTimesByGym.put(gymSymbol, battleTimes.get(gymIndex));
			}
			itinerary.add('U');

			List<Estado> route = buildRoute(map, itinerary);
			System.out.println("Route calculated: " + route.size() + " cells, visiting "
					+ battlePlanner.getGymCount() + " gyms before reaching U.");
			System.out.printf("Estimated total battle time: %.2f time units.%n",
					battlePlanner.calculateCost(battlePlan));
			MapWindow.showMapWithPath(map, route, battleTimesByGym, 25);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<Estado> buildRoute(char[][] map, List<Character> targets) {
		Estado current = findPosition(map, '1');
		AStar pathFinder = new AStar(map);
		List<Estado> route = new ArrayList<>();
		route.add(current);

		for (char targetSymbol : targets) {
			Estado target = findPosition(map, targetSymbol);
			Node goal = pathFinder.search(current, target);
			if (goal == null) {
				throw new IllegalStateException("No path found to point '" + targetSymbol + "'.");
			}

			List<Estado> segment = reconstructPath(goal);
			for (int index = 1; index < segment.size(); index++) {
				route.add(segment.get(index));
			}
			current = target;
		}

		return List.copyOf(route);
	}

	private static Estado findPosition(char[][] map, char symbol) {
		Estado found = null;
		for (int row = 0; row < map.length; row++) {
			for (int column = 0; column < map[row].length; column++) {
				if (map[row][column] == symbol) {
					if (found != null) {
						throw new IllegalStateException("Point '" + symbol + "' occurs more than once.");
					}
					found = new Estado(row, column);
				}
			}
		}

		if (found == null) {
			throw new IllegalStateException("Point '" + symbol + "' was not found on the map.");
		}
		return found;
	}

	private static List<Estado> reconstructPath(Node goal) {
		List<Estado> path = new ArrayList<>();
		for (Node current = goal; current != null; current = current.getParent()) {
			path.add(current.getState());
		}
		Collections.reverse(path);
		return path;
	}
}

package Map;

import Estado.Estado;
import java.util.List;

public class MapVisualizer {

    public static void displayWithTrainer(char[][] map) {
        int startRow = -1;
        int startColumn = -1;

        for (int row = 0; row < map.length; row++) {
            for (int column = 0; column < map[row].length; column++) {
                if (map[row][column] == '1') {
                    if (startRow != -1) {
                        throw new IllegalStateException("More than one starting position '1' found.");
                    }
                    startRow = row;
                    startColumn = column;
                }
            }
        }

        if (startRow == -1) {
            throw new IllegalStateException("Starting position '1' not found on the map.");
        }

        displayWithTrainer(map, startRow, startColumn);
    }
    //overload to give the option to specify the trainer's position directly
    public static void displayWithTrainer(char[][] map, int row, int column) {
        if (row < 0 || row >= map.length || map[row] == null
                || column < 0 || column >= map[row].length) {
            throw new IndexOutOfBoundsException(
                    "Trainer position is outside the map: (" + row + ", " + column + ")");
        }

        char[][] visualization = new char[map.length][];
        for (int mapRow = 0; mapRow < map.length; mapRow++) {
            visualization[mapRow] = map[mapRow].clone();
        }

        visualization[row][column] = 'X';
        System.out.println("Map with trainer at (" + row + ", " + column + "):");
        for (char[] mapRow : visualization) {
            System.out.println(new String(mapRow));
        }
    }

    public static void displayPath(char[][] map, List<Estado> path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path must contain at least one position.");
        }

        for (Estado position : path) {
            if (position == null) {
                throw new IllegalArgumentException("Path must not contain null positions.");
            }
            displayWithTrainer(map, position.line(), position.column());
        }
    }
}

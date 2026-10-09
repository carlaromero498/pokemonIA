package Map;

import Estado.Estado;
import java.util.List;

public class MapVisualizer {
    private static final long FRAME_DELAY_MILLIS = 250;

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
        validatePosition(map, row, column);

        char[][] visualization = new char[map.length][];
        for (int mapRow = 0; mapRow < map.length; mapRow++) {
            visualization[mapRow] = map[mapRow].clone();
        }

        visualization[row][column] = 'X';
        char originalCell = map[row][column];
        if (isGym(originalCell)) {
            System.out.println("Trainer at gym " + originalCell + " (" + row + ", " + column + "):");
        } else {
            System.out.println("Map with trainer at (" + row + ", " + column + "):");
        }
        for (char[] mapRow : visualization) {
            System.out.println(new String(mapRow));
        }
    }

    public static void displayPath(char[][] map, List<Estado> path) throws InterruptedException {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path must contain at least one position.");
        }

        for (Estado position : path) {
            if (position == null) {
                throw new IllegalArgumentException("Path must not contain null positions.");
            }
            validatePosition(map, position.line(), position.column());
        }

        if (System.console() == null) {
            displayPathSnapshot(map, path);
            return;
        }

        for (int index = 0; index < path.size(); index++) {
            Estado position = path.get(index);
            System.out.print("\u001B[2J\u001B[H");
            displayWithTrainer(map, position.line(), position.column());
            System.out.flush();

            if (index < path.size() - 1) {
                Thread.sleep(FRAME_DELAY_MILLIS);
            }
        }
    }

    private static void displayPathSnapshot(char[][] map, List<Estado> path) {
        char[][] visualization = copyMap(map);
        for (Estado position : path) {
            int row = position.line();
            int column = position.column();
            if (isTerrain(map[row][column])) {
                visualization[row][column] = '*';
            }
        }

        System.out.println("Route overview (* = route; gym, start, and destination symbols are preserved):");
        for (char[] row : visualization) {
            System.out.println(new String(row));
        }
    }

    private static boolean isTerrain(char cell) {
        return cell == 'M' || cell == 'A' || cell == 'F' || cell == 'R' || cell == '.';
    }

    private static boolean isGym(char cell) {
        return "23456789BCDEGHIJKLNOPQST".indexOf(cell) >= 0;
    }

    private static char[][] copyMap(char[][] map) {
        char[][] copy = new char[map.length][];
        for (int row = 0; row < map.length; row++) {
            if (map[row] == null) {
                throw new IllegalArgumentException("Map must not contain null rows.");
            }
            copy[row] = map[row].clone();
        }
        return copy;
    }

    private static void validatePosition(char[][] map, int row, int column) {
        if (row < 0 || row >= map.length || map[row] == null
                || column < 0 || column >= map[row].length) {
            throw new IndexOutOfBoundsException(
                    "Trainer position is outside the map: (" + row + ", " + column + ")");
        }
    }
}

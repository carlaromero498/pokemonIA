package Map;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MapLoader {
    private final char[][] mapa;

    public MapLoader(String path) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(path));
        int rows = lines.size();
        int cols = lines.get(0).length();
        mapa = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            String line = lines.get(i);
            // pad or trim to consistent width
            if (line.length() != cols) {
                if (line.length() < cols) {
                    line = String.format("%-" + cols + "s", line).replace(' ', '.');
                } else {
                    line = line.substring(0, cols);
                }
            }
            for (int j = 0; j < cols; j++) {
                mapa[i][j] = line.charAt(j);
            }
        }
    }

    public char[][] getMapa() {
        return mapa;
    }

    public int getRows() { return mapa.length; }
    public int getCols() { return mapa.length == 0 ? 0 : mapa[0].length; }

    public char getCell(int r, int c) { return mapa[r][c]; }
}

package Map;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import Estado.Estado;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;

public class MapWindow {
    private static final int MIN_CELL_SIZE = 8;
    private static final int MAX_CELL_SIZE = 36;
    private static final int DEFAULT_CELL_SIZE = 18;

    private static final Color MOUNTAIN_COLOR = new Color(111, 78, 55);
    private static final Color WATER_COLOR = new Color(74, 144, 217);
    private static final Color FOREST_COLOR = new Color(46, 125, 50);
    private static final Color ROCKY_COLOR = new Color(141, 153, 174);
    private static final Color PLAIN_COLOR = new Color(217, 230, 165);
    private static final Color GYM_COLOR = new Color(255, 209, 102);
    private static final Color START_COLOR = new Color(42, 157, 143);
    private static final Color DESTINATION_COLOR = new Color(123, 44, 191);
    private static final Color TRAINER_COLOR = new Color(220, 50, 47);

    private MapWindow() {
    }

    public static void showMap(char[][] map) {
        validateMap(map);
        char[][] mapCopy = copyMap(map);
        SwingUtilities.invokeLater(() -> createWindow(mapCopy, null, Map.of(), 0, null));
    }

    public static void showMapWithPath(char[][] map, List<Estado> path) {
        showMapWithPath(map, path, 120);
    }

    public static void showMapWithPath(char[][] map, List<Estado> path, int stepDelayMillis) {
        showMapWithPath(map, path, null, stepDelayMillis);
    }

    public static void showMapWithPath(char[][] map, List<Estado> path,
            Map<Character, Double> battleTimesByGym, int stepDelayMillis) {
        showMapWithPath(map, path, battleTimesByGym, stepDelayMillis, null);
    }

    public static void showMapWithPath(char[][] map, List<Estado> path,
            Map<Character, Double> battleTimesByGym, int stepDelayMillis, String statsInfo) {
        validateMap(map);
        if (path == null) {
            showMap(map);
            return;
        }
        validatePath(map, path);

        char[][] mapCopy = copyMap(map);
        Map<Character, Double> battleTimesCopy = battleTimesByGym == null
                ? Map.of()
                : Map.copyOf(battleTimesByGym);
        int delay = Math.max(20, stepDelayMillis);
        SwingUtilities.invokeLater(() -> createWindow(mapCopy, path, battleTimesCopy, delay, statsInfo));
    }

    private static void createWindow(char[][] map, List<Estado> path,
            Map<Character, Double> battleTimesByGym, int stepDelayMillis, String statsInfo) {
        JFrame frame = new JFrame("Pokemon IA - Kanto Map");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(0, 10));
        frame.getContentPane().setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel(
                "Kanto Map  |  " + map.length + " rows x " + map[0].length + " columns",
                SwingConstants.LEFT);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(14, 16, 4, 16));
        
        JLabel status = new JLabel(path == null ? "Map ready." : "Preparing route animation.");
        status.setBorder(BorderFactory.createEmptyBorder(0, 16, 8, 16));
        
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        if (statsInfo != null && !statsInfo.isEmpty()) {
            JLabel stats = new JLabel(statsInfo);
            stats.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            stats.setForeground(new Color(30, 80, 150));
            stats.setBorder(BorderFactory.createEmptyBorder(0, 16, 4, 16));
            header.add(stats, BorderLayout.CENTER);
        }
        header.add(status, BorderLayout.SOUTH);
        frame.add(header, BorderLayout.NORTH);

        MapCanvas canvas = new MapCanvas(map, path, battleTimesByGym, stepDelayMillis, status);
        JScrollPane scrollPane = new JScrollPane(canvas);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(8, 8));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));
        footer.add(createLegend(), BorderLayout.CENTER);
        footer.add(createZoomControls(canvas, scrollPane), BorderLayout.SOUTH);
        frame.add(footer, BorderLayout.SOUTH);

        frame.setSize(1100, 760);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JPanel createLegend() {
        JPanel legend = new JPanel();
        legend.setOpaque(false);
        legend.setLayout(new javax.swing.BoxLayout(legend, javax.swing.BoxLayout.X_AXIS));
        legend.setBorder(BorderFactory.createEmptyBorder(4, 4, 8, 4));
        addLegendItem(legend, PLAIN_COLOR, "Plain (.)");
        addLegendItem(legend, ROCKY_COLOR, "Rocky (R)");
        addLegendItem(legend, FOREST_COLOR, "Forest (F)");
        addLegendItem(legend, WATER_COLOR, "Water (A)");
        addLegendItem(legend, MOUNTAIN_COLOR, "Mountain (M)");
        addLegendItem(legend, GYM_COLOR, "Gym");
        addLegendItem(legend, START_COLOR, "Start");
        addLegendItem(legend, DESTINATION_COLOR, "Destination");
        addLegendItem(legend, TRAINER_COLOR, "Trainer");
        return legend;
    }

    private static void addLegendItem(JPanel legend, Color color, String label) {
        JPanel item = new JPanel(new BorderLayout(5, 0));
        item.setOpaque(false);
        JPanel swatch = new JPanel();
        swatch.setBackground(color);
        swatch.setPreferredSize(new Dimension(14, 14));
        swatch.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 35)));
        JLabel text = new JLabel(label);
        text.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        item.add(swatch, BorderLayout.WEST);
        item.add(text, BorderLayout.CENTER);
        item.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 8));
        legend.add(item);
    }

    private static JPanel createZoomControls(MapCanvas canvas, JScrollPane scrollPane) {
        JPanel controls = new JPanel();
        controls.setOpaque(false);

        JButton zoomOut = new JButton("-");
        JButton zoomIn = new JButton("+");
        JButton fit = new JButton("Fit");
        JLabel instructions = new JLabel("Hover over a cell to see its coordinates.");
        instructions.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));
        zoomOut.addActionListener(event -> canvas.changeCellSize(-2));
        zoomIn.addActionListener(event -> canvas.changeCellSize(2));
        fit.addActionListener(event -> {
            int viewportWidth = scrollPane.getViewport().getWidth() - 4;
            int viewportHeight = scrollPane.getViewport().getHeight() - 4;
            if (viewportWidth > 0 && viewportHeight > 0) {
                int fittedSize = Math.min(
                        viewportWidth / canvas.getColumns(),
                        viewportHeight / canvas.getRows());
                canvas.setCellSize(Math.max(MIN_CELL_SIZE, Math.min(MAX_CELL_SIZE, fittedSize)));
            }
        });

        controls.add(instructions);
        controls.add(zoomOut);
        controls.add(zoomIn);
        controls.add(fit);
        return controls;
    }

    private static void validateMap(char[][] map) {
        if (map == null || map.length == 0 || map[0] == null || map[0].length == 0) {
            throw new IllegalArgumentException("Map must contain at least one cell.");
        }
        int columns = map[0].length;
        for (char[] row : map) {
            if (row == null || row.length != columns) {
                throw new IllegalArgumentException("Map must be rectangular and contain no null rows.");
            }
        }
    }

    private static char[][] copyMap(char[][] map) {
        char[][] copy = new char[map.length][];
        for (int row = 0; row < map.length; row++) {
            copy[row] = map[row].clone();
        }
        return copy;
    }

    private static void validatePath(char[][] map, List<Estado> path) {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Path must contain at least one position.");
        }
        for (Estado position : path) {
            if (position == null || position.line() < 0 || position.line() >= map.length
                    || position.column() < 0
                    || position.column() >= map[position.line()].length) {
                throw new IllegalArgumentException("Path contains a null or out-of-map position.");
            }
        }
    }

    private static class MapCanvas extends JPanel {
        private static final Map<Character, String> GYM_NAMES = createGymNames();
        private final char[][] map;
        private final List<Estado> path;
        private final Set<Integer> revealedPath;
        private final Map<Character, Double> battleTimesByGym;
        private final int stepDelayMillis;
        private final JLabel status;
        private int cellSize = DEFAULT_CELL_SIZE;
        private int revealedCount;
        private Estado trainerPosition;

        private MapCanvas(char[][] map, List<Estado> path,
                Map<Character, Double> battleTimesByGym, int stepDelayMillis, JLabel status) {
            this.map = map;
            this.path = path == null ? List.of() : List.copyOf(path);
            this.revealedPath = new HashSet<>();
            this.battleTimesByGym = battleTimesByGym;
            this.stepDelayMillis = stepDelayMillis;
            this.status = status;
            this.revealedCount = 0;
            setBackground(Color.WHITE);
            setCellSize(cellSize);
            ToolTipManager.sharedInstance().registerComponent(this);

            if (!this.path.isEmpty() && stepDelayMillis > 0) {
                Timer timer = new Timer(stepDelayMillis, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (revealedCount < path.size()) {
                            trainerPosition = path.get(revealedCount);
                            revealedPath.add(cellIndex(trainerPosition));
                            revealedCount++;
                            char cell = map[trainerPosition.line()][trainerPosition.column()];
                            Double battleTime = battleTimesByGym.get(cell);
                            if (battleTime != null && GYM_NAMES.containsKey(cell)) {
                                status.setText(String.format(
                                        "Battle at Gym %s | estimated duration: %.2f seconds",
                                        cell, battleTime));
                                int battleDelayMillis = (int) Math.min(
                                        Integer.MAX_VALUE,
                                        Math.max(1L, Math.round(battleTime * 1000)));
                                ((Timer) e.getSource()).setDelay(battleDelayMillis);
                            } else {
                                status.setText(String.format(
                                        "Trainer moving: row %d, column %d",
                                        trainerPosition.line(), trainerPosition.column()));
                                ((Timer) e.getSource()).setDelay(stepDelayMillis);
                            }
                            scrollRectToVisible(new Rectangle(
                                    trainerPosition.column() * cellSize,
                                    trainerPosition.line() * cellSize,
                                    cellSize,
                                    cellSize));
                            repaint();
                        } else {
                            status.setText("Destination reached. Route complete.");
                            ((Timer) e.getSource()).stop();
                        }
                    }
                });
                timer.setInitialDelay(100);
                timer.start();
            } else if (!this.path.isEmpty()) {
                for (Estado position : this.path) {
                    revealedPath.add(cellIndex(position));
                }
                revealedCount = this.path.size();
                trainerPosition = this.path.get(this.path.size() - 1);
                status.setText("Route complete.");
            }
        }

        private static Map<Character, String> createGymNames() {
            Map<Character, String> names = new LinkedHashMap<>();
            String symbols = "23456789BCDEGHIJKLNOPQST";
            for (int index = 0; index < symbols.length(); index++) {
                names.put(symbols.charAt(index), "Gym " + (index + 1));
            }
            return names;
        }

        private int getRows() {
            return map.length;
        }

        private int getColumns() {
            return map[0].length;
        }

        private int cellIndex(Estado position) {
            return position.line() * getColumns() + position.column();
        }

        private void changeCellSize(int amount) {
            setCellSize(cellSize + amount);
        }

        private void setCellSize(int newSize) {
            cellSize = Math.max(MIN_CELL_SIZE, Math.min(MAX_CELL_SIZE, newSize));
            setPreferredSize(new Dimension(getColumns() * cellSize, getRows() * cellSize));
            revalidate();
            repaint();
        }

        @Override
        public String getToolTipText(MouseEvent event) {
            int column = event.getX() / cellSize;
            int row = event.getY() / cellSize;
            if (row < 0 || row >= getRows() || column < 0 || column >= getColumns()) {
                return null;
            }

            char cell = map[row][column];
            String pointName = cell == '1' ? "Start" : cell == 'U' ? "Destination" : GYM_NAMES.get(cell);
            if (pointName != null) {
                return pointName + " [" + cell + "] - row " + row + ", column " + column;
            }
            return terrainName(cell) + " - row " + row + ", column " + column;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int row = 0; row < getRows(); row++) {
                for (int column = 0; column < getColumns(); column++) {
                    char cell = map[row][column];
                    int x = column * cellSize;
                    int y = row * cellSize;
                    g.setColor(cellColor(cell));
                    g.fillRect(x, y, cellSize, cellSize);
                    if (revealedPath.contains(row * getColumns() + column)) {
                        g.setColor(new Color(255, 215, 0, 170));
                        g.fillRect(x + 2, y + 2, cellSize - 4, cellSize - 4);
                    }
                    g.setColor(new Color(0, 0, 0, 22));
                    g.setStroke(new BasicStroke(0.5f));
                    g.drawRect(x, y, cellSize, cellSize);
                    drawPointLabel(g, cell, x, y);
                    if (trainerPosition != null && trainerPosition.line() == row
                            && trainerPosition.column() == column) {
                        drawTrainerMarker(g, x, y);
                    }
                }
            }
            g.dispose();
        }

        private void drawTrainerMarker(Graphics2D g, int x, int y) {
            int diameter = Math.max(6, cellSize / 3);
            int markerX = x + 2;
            int markerY = y + 2;
            g.setColor(Color.WHITE);
            g.fillOval(markerX, markerY, diameter, diameter);
            g.setColor(TRAINER_COLOR);
            g.fillOval(markerX + 1, markerY + 1, diameter - 2, diameter - 2);
        }

        private void drawPointLabel(Graphics2D g, char cell, int x, int y) {
            if (cell == '1' || cell == 'U' || GYM_NAMES.containsKey(cell)) {
                g.setColor(cell == '1' || cell == 'U' ? Color.WHITE : new Color(35, 35, 35));
                int fontSize = Math.max(9, cellSize - 4);
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
                String label = Character.toString(cell);
                int textX = x + (cellSize - g.getFontMetrics().stringWidth(label)) / 2;
                int textY = y + (cellSize + g.getFontMetrics().getAscent()
                        - g.getFontMetrics().getDescent()) / 2;
                g.drawString(label, textX, textY);
            }
        }

        private Color cellColor(char cell) {
            if (cell == '1') return START_COLOR;
            if (cell == 'U') return DESTINATION_COLOR;
            if (GYM_NAMES.containsKey(cell)) return GYM_COLOR;
            switch (cell) {
                case 'M': return MOUNTAIN_COLOR;
                case 'A': return WATER_COLOR;
                case 'F': return FOREST_COLOR;
                case 'R': return ROCKY_COLOR;
                case '.': return PLAIN_COLOR;
                default: return Color.MAGENTA;
            }
        }

        private String terrainName(char cell) {
            switch (cell) {
                case 'M': return "Mountain";
                case 'A': return "Water";
                case 'F': return "Forest";
                case 'R': return "Rocky terrain";
                case '.': return "Plain";
                default: return "Unrecognized symbol: " + cell;
            }
        }
    }
}

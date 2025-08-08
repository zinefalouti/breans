package com.ml.breans;
import java.util.*;
import java.util.stream.Collectors;
import java.awt.Desktop;
import java.io.*;

/**
 * Breans - A Multi-step Navigator using the A* algorithm, added in v1.1.2
 * Author: Zine El Abidine Falouti
 * License: Open Source
 */

public class Navigate {

    // Generate a Grid for Testing
    public static int[][] GenGrid(int rows, int cols) {
        int[][] grid = new int[rows][cols];
        Random random = new Random();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = random.nextInt(11); // values 0–10
            }
        }
        return grid;
    }

    // Helper to print a Grid
    public static void PrintGrid(int[][] Grid) {
        for (int[] ints : Grid) {
            for (int anInt : ints) {
                System.out.print(anInt + " | ");
            }
            System.out.println();
        }
    }

    // Slice big grid into tiles
    public static int[][][][] sliceGrid(int[][] Grid, int tileSize) {
        int tileRows = (int) Math.ceil((double) Grid.length / tileSize);
        int tileCols = (int) Math.ceil((double) Grid[0].length / tileSize);

        int[][][][] tiles = new int[tileRows][tileCols][][];
        for (int tr = 0; tr < tileRows; tr++) {
            for (int tc = 0; tc < tileCols; tc++) {
                int startRow = tr * tileSize;
                int startCol = tc * tileSize;
                int rows = Math.min(tileSize, Grid.length - startRow);
                int cols = Math.min(tileSize, Grid[0].length - startCol);

                int[][] tile = new int[rows][cols];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        tile[i][j] = Grid[startRow + i][startCol + j];
                    }
                }
                tiles[tr][tc] = tile;
            }
        }
        return tiles;
    }

    // Spectrum: get 3x3 surrounding tiles
    public static int[][][][] Spectrum(int[][][][] tiles, int Sx, int Sy, int tileSize) {
        int tileRow = Sx / tileSize;
        int tileCol = Sy / tileSize;
        int tilesRows = tiles.length;
        int tilesCols = tiles[0].length;
        int[][][][] result = new int[3][3][][];

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nr = tileRow + dr;
                int nc = tileCol + dc;
                if (nr >= 0 && nr < tilesRows && nc >= 0 && nc < tilesCols) {
                    result[dr + 1][dc + 1] = tiles[nr][nc];
                } else {
                    result[dr + 1][dc + 1] = null;
                }
            }
        }
        return result;
    }

    public static void PrintSpectrum(int[][][][] spectrum) {
        for (int i = 0; i < spectrum.length; i++) {
            for (int j = 0; j < spectrum[i].length; j++) {
                System.out.println("Tile [" + i + "," + j + "]:");
                if (spectrum[i][j] != null) {
                    PrintGrid(spectrum[i][j]);
                } else {
                    System.out.println("No tile (out of bounds)");
                }
            }
        }
    }

    private static boolean isObstacle(int val, int[] obstacles) {
        for (int o : obstacles) if (o == val) return true;
        return false;
    }

    // === A* Node ===
    public static class Node implements Comparable<Node> {
        int x, y;
        double g, h;
        Node parent;

        Node(int x, int y, double g, double h, Node parent) {
            this.x = x;
            this.y = y;
            this.g = g;
            this.h = h;
            this.parent = parent;
        }

        double f() { return g + h; }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.f(), other.f());
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Node)) return false;
            Node n = (Node) o;
            return this.x == n.x && this.y == n.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private static double heuristic(int x, int y, int gx, int gy) {
        return Math.abs(x - gx) + Math.abs(y - gy); // Manhattan distance
    }

    private static List<int[]> buildPath(Node goal) {
        List<int[]> path = new ArrayList<>();
        Node current = goal;
        while (current != null) {
            path.add(new int[]{current.x, current.y});
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }

    // === Nearest open cell if destination blocked ===
    private static int[] findNearestOpen(int[][] grid, int gx, int gy, int[] obstacles) {
        int rows = grid.length, cols = grid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{gx, gy});
        visited[gx][gy] = true;

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int x = cell[0], y = cell[1];
            if (!isObstacle(grid[x][y], obstacles)) return cell;
            for (int[] d : dirs) {
                int nx = x + d[0], ny = y + d[1];
                if (nx >= 0 && ny >= 0 && nx < rows && ny < cols && !visited[nx][ny]) {
                    visited[nx][ny] = true;
                    queue.add(new int[]{nx, ny});
                }
            }
        }
        return null;
    }

    // === Improved A* with fallback and diagonals ===
    public static List<int[]> aStarWithFallback(int[][] grid, int sx, int sy, int gx, int gy, int[] obstacles) {
        int rows = grid.length, cols = grid[0].length;

        // Check if destination is blocked
        if (isObstacle(grid[gx][gy], obstacles)) {
            System.out.println("Destination (" + gx + "," + gy + ") is blocked, finding nearest open cell...");
            int[] nearest = findNearestOpen(grid, gx, gy, obstacles);
            if (nearest == null) {
                System.out.println("No nearby open cell found. Path impossible.");
                return Collections.emptyList();
            }
            gx = nearest[0];
            gy = nearest[1];
            System.out.println("Redirected to nearest open cell: (" + gx + "," + gy + ")");
        }

        boolean[][] closed = new boolean[rows][cols];
        PriorityQueue<Node> open = new PriorityQueue<>();
        Node start = new Node(sx, sy, 0, heuristic(sx, sy, gx, gy), null);
        open.add(start);

        int[][] directions = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        while (!open.isEmpty()) {
            Node current = open.poll();
            if (current.x == gx && current.y == gy) return buildPath(current);

            closed[current.x][current.y] = true;

            for (int[] dir : directions) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];

                if (nx < 0 || ny < 0 || nx >= rows || ny >= cols) continue;
                if (closed[nx][ny]) continue;
                if (isObstacle(grid[nx][ny], obstacles)) continue;

                double g = current.g + ((dir[0] == 0 || dir[1] == 0) ? 1 : 1.4);
                double h = heuristic(nx, ny, gx, gy);

                Node neighbor = new Node(nx, ny, g, h, current);

                boolean better = false;
                for (Node node : open) {
                    if (node.x == nx && node.y == ny && node.f() <= neighbor.f()) {
                        better = true;
                        break;
                    }
                }
                if (!better) open.add(neighbor);
            }
        }
        System.out.println("No path to (" + gx + "," + gy + ") found even after fallback.");
        return Collections.emptyList();
    }

    // === Multi-step pathfinding ===
    public static List<List<int[]>> multiStepNavigate(int[][] grid, int[][][][] tiles,
                                         int[] obstacles, int tileSize, int[][] steps) {
    List<List<int[]>> allPaths = new ArrayList<>();
    int Sx = steps[0][0];
    int Sy = steps[0][1];

    for (int i = 1; i < steps.length; i++) {
        int Dx = steps[i][0];
        int Dy = steps[i][1];

        List<int[]> path = aStarWithFallback(grid, Sx, Sy, Dx, Dy, obstacles);
        if (path.isEmpty()) {
            System.out.println("No path from ("+Sx+","+Sy+") to ("+Dx+","+Dy+")");
        } else {
            System.out.println("Path segment ("+Sx+","+Sy+") -> ("+Dx+","+Dy+"): ");
            for (int[] p : path) System.out.print("(" + p[0] + "," + p[1] + ") ");
            System.out.println();
        }
        allPaths.add(path);

        Spectrum(tiles, Dx, Dy, tileSize); // If you want to keep this side effect

        Sx = Dx; Sy = Dy;
    }
    return allPaths;
}

    //Print and Save as HTML for visual
    public static void exportAllPathsHTML(int[][] grid, List<List<int[]>> allPaths,
                                     int[] obstacles, String filename) throws IOException {
        Set<Integer> obstacleSet = Arrays.stream(obstacles).boxed().collect(Collectors.toSet());

        // Build cell → stepIndex map
        Map<String, Integer> stepIndexMap = new HashMap<>();
        for (int step = 0; step < allPaths.size(); step++) {
            for (int[] p : allPaths.get(step)) {
                stepIndexMap.put(p[0] + "," + p[1], step);
            }
        }

        Random rnd = new Random();

        // Dynamically build CSS for each path class based on allPaths.size()
        StringBuilder pathStyles = new StringBuilder();
        for (int i = 0; i < allPaths.size(); i++) {
            String color = String.format("#%06X", rnd.nextInt(0xFFFFFF + 1));
            pathStyles.append(".path").append(i).append(" { background:").append(color).append("; }\n");
        }

        StringBuilder html = new StringBuilder(String.format("""
            <!DOCTYPE html>
            <html>
            <head>
            <style>
                body { background:#222; color:#fff; font-family:Arial; }
                .grid { display:grid; grid-template-columns: repeat(%d, 10px); }
                .cell { width:10px; height:10px; border:1px solid #333; }
                .empty { background:#ffffff; }
                .obstacle { background:#f55d74; }
                %s
            </style>
            </head><body>
            <h2>Multi-step Path Visualization</h2>
            <div class='grid'>
        """, grid[0].length, pathStyles.toString()));

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                String key = i + "," + j;
                String cls = "empty";
                if (obstacleSet.contains(grid[i][j])) cls = "obstacle";
                else if (stepIndexMap.containsKey(key)) cls = "path" + stepIndexMap.get(key);
                html.append("<div class='cell ").append(cls).append("'></div>");
            }
        }

        html.append("</div></body></html>");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename + ".html"))) {
            writer.write(html.toString());
        }
        Desktop.getDesktop().browse(new File(filename + ".html").toURI());
    }


    public static void main(String[] args) throws IOException{
        //Test Locally
    }
}

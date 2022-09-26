package test.detective;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import net.acidfrog.kronos.perspective.Terminal;

public class World {
    
    private int width;
    private int height;
    private Tile[] tiles;

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width * height];
        generate();
    }

    public World(String path) {
        this.width = -1;
        this.height = -1;
        this.tiles = new Tile[width * height];
        generate(path);
    }

    private void generate(String path) {
        // load world from file
        
        List<String> src = null;
        try {
            src = Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        int max = -1;
        for (String line : src) {
            if (line.length() > max) {
                max = line.length();
            }
        }
        final int w = max;
        // go through each line and add tiles for padding
        List<String> padded = src.stream().map(line -> {
            StringBuilder sb = new StringBuilder(line);
            while (sb.length() < w) {
                sb.append(' ');
            }
            return sb.toString();
        }).collect(Collectors.toList());

        this.width = w;
        this.height = padded.size();
        this.tiles = new Tile[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char c = padded.get(y).charAt(x);
                switch (c) {
                case '.':
                    tiles[x + y * width] = Tile.FLOOR;
                    break;
                default:
                    tiles[x + y * width] = Tile.VOID;
                    break;
                }
            }
        }
    }

    private void generate() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[x + y * width] = Tile.VOID;
            }
        }
    }

    public void render(Terminal terminal) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[x + y * width].render(x, y, terminal);
            }
        }
    }
}

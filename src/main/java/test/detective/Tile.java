package test.detective;

import static test.detective.TileType.*;

import net.acidfrog.kronos.perspective.Terminal;

public class Tile {

    public static final Tile VOID = new Tile(0, NON_SOLID, '\0');
    public static final Tile FLOOR = new Tile(1, NON_SOLID, '.');
    
    private int id;
    private TileType type;
    private char character;
    private World world;

    public Tile(int id, TileType type, char character) {
        this.id = id;
        this.type = type;
        this.character = character;
    }

    public void render(int x, int y, Terminal terminal) {
        terminal.setCharacter(y, x, character);
    }

    public int getId() {
        return id;
    }

    public TileType getType() {
        return type;
    }

    public World getWorld() {
        return world;
    }
}

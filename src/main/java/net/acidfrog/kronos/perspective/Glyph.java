package net.acidfrog.kronos.perspective;

import java.util.ArrayDeque;
import java.util.Deque;

public class Glyph {

    private int line;
    private int col;
    private char character;
    private Deque<Character> under;
    private int color;

    public Glyph(int line, int col, char character) {
        this.line = line;
        this.col = col;
        this.character = character;
        this.under = new ArrayDeque<Character>();
        under.push('\0');
        this.color = 0xCCCCCC;
    }
    
    public int line() {
        return line;
    }

    public int col() {
        return col;
    }

    public char character() {
        return character;
    }

    public Deque<Character> under() {
        return under;
    }

    public int color() {
        return color;
    }

    public void character(char character) {
        this.character = character;
    }

    public void line(int line) {
        this.line = line;
    }

    public void col(int col) {
        this.col = col;
    }

    public void pushUnder(char character) {
        under.push(character);
    }

    public char popUnder() {
        return under.pop();
    }

    public void color(int color) {
        this.color = color;
    }
}

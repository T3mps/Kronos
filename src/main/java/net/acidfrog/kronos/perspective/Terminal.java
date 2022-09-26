package net.acidfrog.kronos.perspective;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

public final class Terminal extends Canvas {
    
    private JFrame frame;
    private int lines;
    private int columns;
    private int characterSize;
    private Glyph[] characters;
    private Font font;
    InputHandler input;

    public Terminal(int lines, int cols) {
        this.frame = new JFrame();
        this.lines = lines;
        this.columns = cols;
        this.characterSize = 14;
        this.characters = new Glyph[lines * cols];
        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < cols; j++) {
                characters[i * cols + j] = new Glyph(i, j, '\0');
            }
        }

        try {
            InputStream is = new BufferedInputStream(new FileInputStream("assets/fonts/curses10x12.ttf"));
            var f = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
            this.font = f.deriveFont(Font.PLAIN, characterSize);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        this.input = new InputHandler();
        this.addKeyListener(input);

        frame.setResizable(false);
		frame.setTitle("TITLE");
		frame.add(this);
        Dimension size = new Dimension(cols * characterSize, lines * characterSize);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
        frame.setFont(font); this.setFont(font);
		frame.setVisible(true);
    }
    
    public Glyph getGlyph(int line, int col) {
        return characters[line * columns + col];
    }

    public Glyph setCharacter(int line, int col, char character) {
        return characters[line * columns + col] = new Glyph(line, col, character);
    }

    public Glyph clearCharacter(int line, int col) {
        return setCharacter(line, col, '\0');
    }

    public Glyph translateCharacter(int originLine, int originCol, int line, int col) {
        if (originLine < 0 || originLine >= lines || originCol < 0 || originCol >= columns) {
            return new Glyph(lines - 1, columns - 1, '\0');
        }
        if (line < 0 || line >= lines || col < 0 || col >= columns) {
            return new Glyph(-1, -1, '\0');
        }

        Glyph originGlyph = characters[originLine * columns + originCol];
        Glyph target = characters[line * columns + col];
        target.pushUnder(target.character());
        target.character(originGlyph.character());
        originGlyph.character(originGlyph.popUnder());

        int originColor = originGlyph.color();
        originGlyph.color(target.color());
        target.color(originColor);
        return target;
    }

    public Glyph translateCharacter(Glyph origin, int line, int col) {
        return translateCharacter(origin.line(), origin.col(), line, col);
    }

    public Glyph[] drawString(int line, int col, String string, int color) {
        Glyph[] glyphs = new Glyph[string.length()];
        for (int i = 0; i < string.length(); i++) {
            glyphs[i] = setCharacter(line, col + i, string.charAt(i));
            glyphs[i].color(color);
        }
        return glyphs;
    }

	public void update() {
	}

	public void render() {
        BufferStrategy bs = getBufferStrategy();

		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		Graphics2D g2d = (Graphics2D) bs.getDrawGraphics();

        g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, columns * characterSize, lines * characterSize);
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < characters.length; i++) {
            int line = i / columns;
            int col = i % columns;
            g2d.setColor(new Color(characters[i].color()));
            g2d.drawString(String.valueOf(characters[i].character()), col * characterSize, (line + 1) * characterSize - 4);
        }

		g2d.dispose();
		bs.show();
    }

    public InputHandler input() {
        return input;
    }
}

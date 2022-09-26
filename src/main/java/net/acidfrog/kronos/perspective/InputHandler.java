package net.acidfrog.kronos.perspective;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {

    private boolean[] keys = new boolean[65536];

    public InputHandler() {
        for (int i = 0; i < keys.length; i++) {
            keys[i] = false;
        }
    }

    public boolean isKey(int keyCode) {
        return keys[keyCode];
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
    
}

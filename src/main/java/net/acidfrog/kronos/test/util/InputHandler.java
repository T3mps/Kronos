package net.acidfrog.kronos.test.util;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	public static final InputHandler instance = new InputHandler();

	private final int NUM_KEYS = 350;
	private final int NUM_BUTTONS = 5;

	private boolean[] keys = new boolean[NUM_KEYS];
	private boolean[] keysLast = new boolean[NUM_KEYS];
	private boolean[] buttons = new boolean[NUM_BUTTONS];
	private boolean[] buttonsLast = new boolean[NUM_BUTTONS];

	private int mouseX, mouseY;
	private int prevMouseX, prevMouseY;
	private int mouseDx, mouseDy;
	public int wheel;
	private char keyTyped;

	private InputHandler() {
		this.mouseX = 0;
		this.mouseY = 0;
		this.prevMouseX = 0;
		this.prevMouseY = 0;
		this.mouseDx = 0;
		this.mouseDy = 0;
	}

	public void initilize(java.awt.Component component) {
		component.addKeyListener(this);
		component.addMouseListener(this);
		component.addMouseMotionListener(this);
		component.addMouseWheelListener(this);
	}

	public void update() {
		System.arraycopy(keys, 0, keysLast, 0, NUM_KEYS);
		System.arraycopy(buttons, 0, buttonsLast, 0, NUM_BUTTONS);

		mouseDx = prevMouseX - mouseX;
		mouseDy = prevMouseY - mouseY;

		prevMouseX = mouseX;
		prevMouseY = mouseY;

		keyTyped = 0;
		wheel = 0;
	}

	public boolean isKey(int keyCode) {
		return keys[keyCode];
	}

	public boolean isKeyDown(int keyCode) {
		return keys[keyCode] && !keysLast[keyCode];
	}

	public boolean isKeyUp(int keyCode) {
		return !keys[keyCode] && !keysLast[keyCode];
	}

	public boolean isButton(int button) {
		return buttons[button];
	}

	public boolean isButtonDown(int button) {
		return !buttonsLast[button] && buttons[button];
	}

	public boolean isButtonUp(int button) {
		return !buttons[button] && !buttonsLast[button];
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public int getMouseDx() {
		return mouseDx;
	}

	public int getMouseDy() {
		return mouseDy;
	}

	public char getKeyTyped() {
		return keyTyped;
	}

	public boolean isKeyTyped() {
		return keyTyped != 0;
	}

	public int getWheel() {
		return wheel;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		keyTyped = e.getKeyChar();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() >= NUM_KEYS)
			return;
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() >= NUM_KEYS)
			return;
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() >= NUM_BUTTONS)
			return;
		buttons[e.getButton()] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() >= NUM_BUTTONS)
			return;
		buttons[e.getButton()] = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = (int) (e.getX() / 1f); // 1 is the scale
		mouseY = (int) (e.getY() / 1f);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = (int) (e.getX() / 1f); // 1 is the scale
		mouseY = (int) (e.getY() / 1f);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		wheel = e.getWheelRotation();
	}

}

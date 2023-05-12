package com.starworks.kronos.input;

import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import com.starworks.kronos.core.Application;
import com.starworks.kronos.maths.Vector2f;

public final class Input {
	
	private static Input s_instance;
	
	private final long m_windowPointer;
	private final DoubleBuffer m_mouseXBuffer;
	private final DoubleBuffer m_mouseYBuffer;
	
	private Input() {
		this.m_windowPointer = Application.get().getWindow().getWindowPointer();
		this.m_mouseXBuffer = BufferUtils.createDoubleBuffer(1);
		this.m_mouseYBuffer = BufferUtils.createDoubleBuffer(1);
	}
	
	public boolean isKeyPressed(int keyCode) {
		long state = GLFW.glfwGetKey(m_windowPointer, keyCode);
		return state == GLFW.GLFW_PRESS;
	}
	
	public boolean isKeyDown(int keyCode) {
		long state = GLFW.glfwGetKey(m_windowPointer, keyCode);
		return state == GLFW.GLFW_PRESS || state == GLFW.GLFW_REPEAT;
	}
	
	public boolean isMouseButtonPressed(int button) {
		long state = GLFW.glfwGetMouseButton(m_windowPointer, button);
		return state == GLFW.GLFW_PRESS;
	}
	
	public Vector2f getMousePosition() {
		m_mouseXBuffer.clear();
		m_mouseYBuffer.clear();
		GLFW.glfwGetCursorPos(m_windowPointer, m_mouseXBuffer, m_mouseYBuffer);
		return new Vector2f((float) m_mouseXBuffer.get(0), (float) m_mouseYBuffer.get(0));
	}
	
	public float getMouseX() {
		m_mouseXBuffer.clear();
		GLFW.glfwGetCursorPos(m_windowPointer, m_mouseXBuffer, null);
		return (float) m_mouseXBuffer.get(0);
	}
	
	public float getMouseY() {
		m_mouseYBuffer.clear();
		GLFW.glfwGetCursorPos(m_windowPointer, null, m_mouseYBuffer);
		return (float) m_mouseYBuffer.get(0);
	}
	
	public static Input get() {
		if (s_instance == null) {
			synchronized (Input.class) {
				if (s_instance == null) {
					s_instance = new Input();
				}
			}
		}
		return s_instance;
	}
	
	 public static final int
     KR_KEY_SPACE         = 32,
     KR_KEY_APOSTROPHE    = 39,
     KR_KEY_COMMA         = 44,
     KR_KEY_MINUS         = 45,
     KR_KEY_PERIOD        = 46,
     KR_KEY_SLASH         = 47,
     KR_KEY_0             = 48,
     KR_KEY_1             = 49,
     KR_KEY_2             = 50,
     KR_KEY_3             = 51,
     KR_KEY_4             = 52,
     KR_KEY_5             = 53,
     KR_KEY_6             = 54,
     KR_KEY_7             = 55,
     KR_KEY_8             = 56,
     KR_KEY_9             = 57,
     KR_KEY_SEMICOLON     = 59,
     KR_KEY_EQUAL         = 61,
     KR_KEY_A             = 65,
     KR_KEY_B             = 66,
     KR_KEY_C             = 67,
     KR_KEY_D             = 68,
     KR_KEY_E             = 69,
     KR_KEY_F             = 70,
     KR_KEY_G             = 71,
     KR_KEY_H             = 72,
     KR_KEY_I             = 73,
     KR_KEY_J             = 74,
     KR_KEY_K             = 75,
     KR_KEY_L             = 76,
     KR_KEY_M             = 77,
     KR_KEY_N             = 78,
     KR_KEY_O             = 79,
     KR_KEY_P             = 80,
     KR_KEY_Q             = 81,
     KR_KEY_R             = 82,
     KR_KEY_S             = 83,
     KR_KEY_T             = 84,
     KR_KEY_U             = 85,
     KR_KEY_V             = 86,
     KR_KEY_W             = 87,
     KR_KEY_X             = 88,
     KR_KEY_Y             = 89,
     KR_KEY_Z             = 90,
     KR_KEY_LEFT_BRACKET  = 91,
     KR_KEY_BACKSLASH     = 92,
     KR_KEY_RIGHT_BRACKET = 93,
     KR_KEY_GRAVE_ACCENT  = 96,
     KR_KEY_WORLD_1       = 161,
     KR_KEY_WORLD_2       = 162;

	 public static final int
     KR_KEY_ESCAPE        = 256,
     KR_KEY_ENTER         = 257,
     KR_KEY_TAB           = 258,
     KR_KEY_BACKSPACE     = 259,
     KR_KEY_INSERT        = 260,
     KR_KEY_DELETE        = 261,
     KR_KEY_RIGHT         = 262,
     KR_KEY_LEFT          = 263,
     KR_KEY_DOWN          = 264,
     KR_KEY_UP            = 265,
     KR_KEY_PAGE_UP       = 266,
     KR_KEY_PAGE_DOWN     = 267,
     KR_KEY_HOME          = 268,
     KR_KEY_END           = 269,
     KR_KEY_CAPS_LOCK     = 280,
     KR_KEY_SCROLL_LOCK   = 281,
     KR_KEY_NUM_LOCK      = 282,
     KR_KEY_PRINT_SCREEN  = 283,
     KR_KEY_PAUSE         = 284,
     KR_KEY_F1            = 290,
     KR_KEY_F2            = 291,
     KR_KEY_F3            = 292,
     KR_KEY_F4            = 293,
     KR_KEY_F5            = 294,
     KR_KEY_F6            = 295,
     KR_KEY_F7            = 296,
     KR_KEY_F8            = 297,
     KR_KEY_F9            = 298,
     KR_KEY_F10           = 299,
     KR_KEY_F11           = 300,
     KR_KEY_F12           = 301,
     KR_KEY_F13           = 302,
     KR_KEY_F14           = 303,
     KR_KEY_F15           = 304,
     KR_KEY_F16           = 305,
     KR_KEY_F17           = 306,
     KR_KEY_F18           = 307,
     KR_KEY_F19           = 308,
     KR_KEY_F20           = 309,
     KR_KEY_F21           = 310,
     KR_KEY_F22           = 311,
     KR_KEY_F23           = 312,
     KR_KEY_F24           = 313,
     KR_KEY_F25           = 314,
     KR_KEY_KP_0          = 320,
     KR_KEY_KP_1          = 321,
     KR_KEY_KP_2          = 322,
     KR_KEY_KP_3          = 323,
     KR_KEY_KP_4          = 324,
     KR_KEY_KP_5          = 325,
     KR_KEY_KP_6          = 326,
     KR_KEY_KP_7          = 327,
     KR_KEY_KP_8          = 328,
     KR_KEY_KP_9          = 329,
     KR_KEY_KP_DECIMAL    = 330,
     KR_KEY_KP_DIVIDE     = 331,
     KR_KEY_KP_MULTIPLY   = 332,
     KR_KEY_KP_SUBTRACT   = 333,
     KR_KEY_KP_ADD        = 334,
     KR_KEY_KP_ENTER      = 335,
     KR_KEY_KP_EQUAL      = 336,
     KR_KEY_LEFT_SHIFT    = 340,
     KR_KEY_LEFT_CONTROL  = 341,
     KR_KEY_LEFT_ALT      = 342,
     KR_KEY_LEFT_SUPER    = 343,
     KR_KEY_RIGHT_SHIFT   = 344,
     KR_KEY_RIGHT_CONTROL = 345,
     KR_KEY_RIGHT_ALT     = 346,
     KR_KEY_RIGHT_SUPER   = 347,
     KR_KEY_MENU          = 348,
     KR_KEY_LAST          = KR_KEY_MENU;
 
	 public static final int
	 KR_MOUSE_BUTTON_1      = 0,
	 KR_MOUSE_BUTTON_2      = 1,
	 KR_MOUSE_BUTTON_3      = 2,
	 KR_MOUSE_BUTTON_4      = 3,
	 KR_MOUSE_BUTTON_5      = 4,
	 KR_MOUSE_BUTTON_6      = 5,
	 KR_MOUSE_BUTTON_7      = 6,
	 KR_MOUSE_BUTTON_8      = 7,
	 KR_MOUSE_BUTTON_LAST   = KR_MOUSE_BUTTON_8,
	 KR_MOUSE_BUTTON_LEFT   = KR_MOUSE_BUTTON_1,
	 KR_MOUSE_BUTTON_RIGHT  = KR_MOUSE_BUTTON_2,
	 KR_MOUSE_BUTTON_MIDDLE = KR_MOUSE_BUTTON_3;
	
	/** Joysticks. See <a target="_blank" href="http://www.glfw.org/docs/latest/input.html#joystick">joystick input</a> for how these are used. */
	public static final int
	 KR_JOYSTICK_1    = 0,
	 KR_JOYSTICK_2    = 1,
	 KR_JOYSTICK_3    = 2,
	 KR_JOYSTICK_4    = 3,
	 KR_JOYSTICK_5    = 4,
	 KR_JOYSTICK_6    = 5,
	 KR_JOYSTICK_7    = 6,
	 KR_JOYSTICK_8    = 7,
	 KR_JOYSTICK_9    = 8,
	 KR_JOYSTICK_10   = 9,
	 KR_JOYSTICK_11   = 10,
	 KR_JOYSTICK_12   = 11,
	 KR_JOYSTICK_13   = 12,
	 KR_JOYSTICK_14   = 13,
	 KR_JOYSTICK_15   = 14,
	 KR_JOYSTICK_16   = 15,
	 KR_JOYSTICK_LAST = KR_JOYSTICK_16;
	
	/** Gamepad buttons. See <a target="_blank" href="http://www.glfw.org/docs/latest/input.html#gamepad">gamepad</a> for how these are used. */
	public static final int
	 KR_GAMEPAD_BUTTON_A            = 0,
	 KR_GAMEPAD_BUTTON_B            = 1,
	 KR_GAMEPAD_BUTTON_X            = 2,
	 KR_GAMEPAD_BUTTON_Y            = 3,
	 KR_GAMEPAD_BUTTON_LEFT_BUMPER  = 4,
	 KR_GAMEPAD_BUTTON_RIGHT_BUMPER = 5,
	 KR_GAMEPAD_BUTTON_BACK         = 6,
	 KR_GAMEPAD_BUTTON_START        = 7,
	 KR_GAMEPAD_BUTTON_GUIDE        = 8,
	 KR_GAMEPAD_BUTTON_LEFT_THUMB   = 9,
	 KR_GAMEPAD_BUTTON_RIGHT_THUMB  = 10,
	 KR_GAMEPAD_BUTTON_DPAD_UP      = 11,
	 KR_GAMEPAD_BUTTON_DPAD_RIGHT   = 12,
	 KR_GAMEPAD_BUTTON_DPAD_DOWN    = 13,
	 KR_GAMEPAD_BUTTON_DPAD_LEFT    = 14,
	 KR_GAMEPAD_BUTTON_LAST         = KR_GAMEPAD_BUTTON_DPAD_LEFT,
	 KR_GAMEPAD_BUTTON_CROSS        = KR_GAMEPAD_BUTTON_A,
	 KR_GAMEPAD_BUTTON_CIRCLE       = KR_GAMEPAD_BUTTON_B,
	 KR_GAMEPAD_BUTTON_SQUARE       = KR_GAMEPAD_BUTTON_X,
	 KR_GAMEPAD_BUTTON_TRIANGLE     = KR_GAMEPAD_BUTTON_Y;
	
	/** Gamepad axes. See <a target="_blank" href="http://www.glfw.org/docs/latest/input.html#gamepad">gamepad</a> for how these are used. */
	public static final int
	 KR_GAMEPAD_AXIS_LEFT_X        = 0,
	 KR_GAMEPAD_AXIS_LEFT_Y        = 1,
	 KR_GAMEPAD_AXIS_RIGHT_X       = 2,
	 KR_GAMEPAD_AXIS_RIGHT_Y       = 3,
	 KR_GAMEPAD_AXIS_LEFT_TRIGGER  = 4,
	 KR_GAMEPAD_AXIS_RIGHT_TRIGGER = 5,
	 KR_GAMEPAD_AXIS_LAST          = KR_GAMEPAD_AXIS_RIGHT_TRIGGER;
}

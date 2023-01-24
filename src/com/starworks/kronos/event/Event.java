package com.starworks.kronos.event;

/*
 * Represents an event. GLFW
 */
public sealed interface Event permits Event.KeyPressed,
									  Event.KeyReleased,
									  Event.KeyRepeated,
									  Event.KeyTyped,
									  Event.MouseButtonPressed,
									  Event.MouseButtonReleased,
									  Event.MouseMoved,
									  Event.MouseWheel,
									  Event.WindowResized,
									  Event.WindowClosed,
									  Event.WindowMoved,
									  Event.WindowFocusGained,
									  Event.WindowFocusLost,
									  Event.WindowMinimized,
									  Event.WindowRestored,
									  Event.WindowMaximized,
									  Event.WindowUnmaximized,
									  Event.WindowRefreshed {

	public static final int NONE 				=  0;

	// Type
	static final int KEY_PRESSED 				=  1;
	static final int KEY_RELEASED 				=  2;
	static final int KEY_REPEATED 				=  3;
	static final int KEY_TYPED 					=  4;
	static final int MOUSE_PRESSED 				=  5;
	static final int MOUSE_RELEASED 			=  6;
	public static final int MOUSE_MOVED 		=  7;
	public static final int MOUSE_WHEEL 		=  8;
	public static final int WINDOW_RESIZED 		=  9;
	public static final int WINDOW_CLOSED 		= 10;
	public static final int WINDOW_MOVED 		= 11;
	public static final int WINDOW_FOCUS_GAINED = 12;
	public static final int WINDOW_FOCUS_LOST 	= 13;
	public static final int WINDOW_MINIMIZED 	= 14;
	public static final int WINDOW_RESTORED 	= 15;
	public static final int WINDOW_MAXIMIZED 	= 16;
	public static final int WINDOW_UNMAXIMIZED 	= 17;
	public static final int WINDOW_REFRESHED 	= 18;

	// Category
	public static final int INPUT    			=  1;
	public static final int KEYBOARD 			=  2;
	public static final int MOUSE    			=  4;
	public static final int WINDOW   			=  8;

	long timestamp();

	int getType();

	int getCategory();
	
	public record KeyPressed(int key, int scancode, int mods, long timestamp) implements Event {
		@Override
		public int getType() 				{ return KEY_PRESSED; }
		@Override
		public int getCategory() 			{ return INPUT | KEYBOARD; }
		@Override
		public KeyPressed clone() 			{ return new KeyPressed(key, scancode, mods, timestamp); }
	}

	public record KeyReleased(int key, int scancode, int mods, long timestamp) implements Event {
		@Override
		public int getType() 				{ return Event.KEY_RELEASED; }
		@Override
		public int getCategory() 			{ return Event.INPUT | Event.KEYBOARD; }
		@Override
		public KeyReleased clone() 			{ return new KeyReleased(key, scancode, mods, timestamp); }
	}

	public record KeyRepeated(int key, int scancode, int mods, long timestamp) implements Event {
		@Override
		public int getType() 				{ return KEY_REPEATED; }
		@Override
		public int getCategory() 			{ return INPUT | KEYBOARD; }
		@Override
		public KeyRepeated clone() 			{ return new KeyRepeated(key, scancode, mods, timestamp); }
	}

	public record KeyTyped(int codepoint, int mods, long timestamp) implements Event {
		@Override
		public int getType() 				{ return KEY_TYPED; }
		@Override
		public int getCategory() 			{ return INPUT | KEYBOARD; }
		@Override
		public KeyTyped clone() 			{ return new KeyTyped(codepoint, mods, timestamp); }
	}

	public record MouseButtonPressed(int button, int mods, long timestamp) implements Event {
		@Override
		public int getType() 				{ return MOUSE_PRESSED; }
		@Override
		public int getCategory() 			{ return INPUT | MOUSE; }
		@Override
		public MouseButtonPressed clone() 	{ return new MouseButtonPressed(button, mods, timestamp); }
	}

	public record MouseButtonReleased(int button, int mods, long timestamp) implements Event {
		@Override
		public int getType() 				{ return MOUSE_RELEASED; }
		@Override
		public int getCategory() 			{ return INPUT | MOUSE; }
		@Override
		public MouseButtonReleased clone() 	{ return new MouseButtonReleased(button, mods, timestamp); }
	}

	public record MouseMoved(double xPosition, double yPosition, long timestamp) implements Event {
		@Override
		public int getType() 				{ return MOUSE_MOVED; }
		@Override
		public int getCategory() 			{ return INPUT | MOUSE; }
		@Override
		public MouseMoved clone() 			{ return new MouseMoved(xPosition, yPosition, timestamp); }
	}

	public record MouseWheel(double xOffset, double yOffset, long timestamp) implements Event {
		@Override
		public int getType() 				{ return MOUSE_WHEEL; }
		@Override
		public int getCategory() 			{ return INPUT | MOUSE; }
		@Override
		public MouseWheel clone() 			{ return new MouseWheel(xOffset, yOffset, timestamp); }
	}

	public record WindowResized(int width, int height, long timestamp) implements Event {
		@Override
		public int getType() 				{ return WINDOW_RESIZED; }
		@Override
		public int getCategory() 			{ return WINDOW; }
		@Override
		public WindowResized clone() 		{ return new WindowResized(width, height, timestamp); }
	}

	public record WindowClosed(long timestamp) implements Event {
		@Override
		public int getType() 				{ return WINDOW_CLOSED; }
		@Override
		public int getCategory() 			{ return WINDOW; }
		@Override
		public WindowClosed clone() 		{ return new WindowClosed(timestamp); }
	}

	public record WindowMoved(int xPosition, int yPosition, long timestamp) implements Event {
		@Override
		public int getType() 				{ return WINDOW_MOVED; }
		@Override
		public int getCategory() 			{ return WINDOW; }
		@Override
		public WindowMoved clone() 			{ return new WindowMoved(xPosition, yPosition, timestamp); }
	}

	public record WindowFocusGained(long timestamp) implements Event {
		@Override
		public int getType() 				{ return WINDOW_FOCUS_GAINED; }
		@Override
		public int getCategory() 			{ return WINDOW; }
		@Override
		public WindowFocusGained clone() 	{ return new WindowFocusGained(timestamp); }
	}

	public record WindowFocusLost(long timestamp) implements Event {
		@Override
		public int getType() 				{ return WINDOW_FOCUS_LOST; }
		@Override
		public int getCategory() 			{ return WINDOW; }
		@Override
		public WindowFocusLost clone() 		{ return new WindowFocusLost(timestamp); }
	}

	public record WindowMinimized(long timestamp) implements Event {
		@Override
		public int getType() 				{ return WINDOW_MINIMIZED; }
		@Override
		public int getCategory() 			{ return WINDOW; }
		@Override
		public WindowMinimized clone() 		{ return new WindowMinimized(timestamp); }
	}

	public record WindowRestored(long timestamp) implements Event {
		@Override
		public int getType() 				{ return WINDOW_RESTORED; }
		@Override
		public int getCategory() 			{ return WINDOW; }
		@Override
		public WindowRestored clone() 		{ return new WindowRestored(timestamp); }
	}
	
	public record WindowMaximized(long timestamp) implements Event {
		@Override
		public int getType() 				{ return WINDOW_MAXIMIZED; }
		@Override
		public int getCategory() 			{ return WINDOW; }
		@Override
		public WindowMaximized clone() 		{ return new WindowMaximized(timestamp); }
	}

	public record WindowUnmaximized(long timestamp) 	implements Event {
		@Override
		public int getType() 				{ return WINDOW_UNMAXIMIZED; }
		@Override
		public int getCategory() 		 	{ return WINDOW; }
		@Override
		public WindowUnmaximized clone() 	{ return new WindowUnmaximized(timestamp); }
	}
	
	public record WindowRefreshed(long timestamp) implements Event {
		@Override
		public int getType() 		   	 	{ return WINDOW_REFRESHED; }
		@Override
		public int getCategory() 	   	 	{ return WINDOW; }
		@Override
		public WindowRefreshed clone() 	 	{ return new WindowRefreshed(timestamp); }
	}
}

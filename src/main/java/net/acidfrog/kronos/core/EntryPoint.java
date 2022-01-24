package net.acidfrog.kronos.core;

public class EntryPoint {
	
	private final Window window;

	public EntryPoint() {
		this.window = Window.create(1024, 768, "Kronos");
		window.run();
	}
	
	public static void main(String[] args) {
		new EntryPoint();
	}

}

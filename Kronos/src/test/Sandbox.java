package test;

import com.starworks.kronos.core.Window;

public class Sandbox {
    
    public static void main(String[] args) {
        Window window = new Window("Sandbox", 800, 600);
        window.create();
        
        while (!window.shouldClose()) {
            window.update();
        }
        
        window.destroy();
    }
}

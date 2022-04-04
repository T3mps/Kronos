package net.acidfrog.kronos.core.architecture;

import net.acidfrog.kronos.core.lang.logger.Logger;
import net.acidfrog.kronos.core.util.Chrono;

public final class EntryPoint {
    
    private EntryPoint() {
        throw new IllegalStateException("Cannot instantiate EntryPoint");
    }

    public static void main(String[] args) {
        Chrono.Clock clock = new Chrono.Clock(true);
        AbstractApplication app = new Application(AbstractApplication.DEFAULT_WINDOW_TITLE);
        app.start();
        clock.stop();
        Logger.logInternal("Application ran for " + clock.durationSeconds() + " seconds.");
    }

    
}

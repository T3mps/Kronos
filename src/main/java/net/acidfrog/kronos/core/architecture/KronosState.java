package net.acidfrog.kronos.core.architecture;

import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;
import net.acidfrog.kronos.core.lang.logger.Logger;

public enum KronosState {

    ENTRY {

        @Override
        public KronosState next() {
            Logger.logInfo("Initializing application...");
            return INITIALIZING;
        }
    },

    INITIALIZING {

        @Override
        public KronosState next() {
            return STARTING;
        }

    },

    STARTING {

        @Override
        public KronosState next() {
            Logger.logInfo("Starting application...");
            return RUNNING;
        }

    },

    RUNNING {
        @Override
        public KronosState next() {
            Logger.logInfo("Stopping application...");
            return STOPPING;
        }

    },

    STOPPING {

        @Override
        public KronosState next() {
            Logger.logInfo("Closing application...");
            return TERMINATED;
        }

    },

    TERMINATED {

        @Override
        public KronosState next() {
            return ERROR;
        }

    },

    ERROR {

        @Override
        public KronosState next() {
            throw new KronosError(KronosErrorLibrary.INTERNAL_APPLICATION_STATE_ERROR);
        }
        
    };
    
    private KronosState() {}

    public abstract KronosState next();

}

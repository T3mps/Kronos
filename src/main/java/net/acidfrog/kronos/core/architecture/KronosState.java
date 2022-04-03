package net.acidfrog.kronos.core.architecture;

import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.error.KronosErrorLibrary;

public enum KronosState {

    INITIALIZING {

        @Override
        public KronosState next() {
            System.out.println("Initializing...");
            return STARTING;
        }
    },
    STARTING {

        @Override
        public KronosState next() {
            System.out.println("Starting application...");
            return RUNNING;
        }
    },
    RUNNING {
        @Override
        public KronosState next() {
            System.out.println("Stopping application...");
            return STOPPING;
        }
    },
    STOPPING {

        @Override
        public KronosState next() {
            System.out.println("Closing application...");
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

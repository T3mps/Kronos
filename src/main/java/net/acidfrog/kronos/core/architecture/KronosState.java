package net.acidfrog.kronos.core.architecture;

public enum KronosState {

    INITIALIZING {

        @Override
        public KronosState next() {
            return STARTING;
        }
    },
    STARTING {

        @Override
        public KronosState next() {
            return RUNNING;
        }
    },
    RUNNING {
        @Override
        public KronosState next() {
            return STOPPING;
        }
    },
    STOPPING {

        @Override
        public KronosState next() {
            return TERMINATED;
        }
    },
    TERMINATED {

        @Override
        public KronosState next() {
            return this;
        }
    },
    ERROR {

        @Override
        public KronosState next() {
            return this;
        }
    };
    
    private KronosState() {}

    public abstract KronosState next();

}

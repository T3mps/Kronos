package net.acidfrog.kronos.scribe;

import net.acidfrog.kronos.scribe.ANSI.AnsiTrait;

/**
 * Represents the level of a message produced by a {@link Logger logger}.
 * This is used to indicate the severity and the color of said message.
 * 
 * @author Ethan Temprovich
 * @version 1.0
 * @since 1.0
 * @see Logger
 */
public final class Level {
    
    /** Value represented by the {@link #OFF} level. */
    public static final int OFF_FLAG   = 0xf7f7;

    /** Value represented by the {@link #FATAL} level. */
    public static final int FATAL_FLAG = 0xC350;

    /** Value represented by the {@link #ERROR} level. */
    public static final int ERROR_FLAG = 0x9C40;

    /** Value represented by the {@link #WARN} level. */
    public static final int WARN_FLAG  = 0x7530;

    /** Value represented by the {@link #INFO} level. */
    public static final int INFO_FLAG  = 0x5C20;

    /** Value represented by the {@link #DEBUG} level. */
    public static final int DEBUG_FLAG = 0x2710;

    /** Value represented by the {@link #TRACE} level. */
    public static final int TRACE_FLAG = 0x1388;

    /** Value represented by the {@link #ALL} level. */
    public static final int ALL_FLAG   = 0x0000;

    /** This {@link Level} will include no other levels of logging. Effectivly turns off logging. */
    public static final Level OFF   = new Level(OFF_FLAG, "OFF", ANSI.Traits.NONE, ANSI.Traits.NONE);

    /** This {@link Level} defines a fatal error has occured. */
    public static final Level FATAL = new Level(FATAL_FLAG, "FATAL", ANSI.Traits.WHITE_FG, ANSI.Traits.RED_BG);

    /** This {@link Level} defines an error has occured, but the program may recover. */
    public static final Level ERROR = new Level(ERROR_FLAG, "ERROR", ANSI.Traits.RED_FG, ANSI.Traits.NONE);

    /** This {@link Level} defines a warning where unexpected behavior has occured, but no errors have yet resulted. */
    public static final Level WARN  = new Level(WARN_FLAG, "WARN", ANSI.Traits.YELLOW_FG, ANSI.Traits.NONE);

    /** This {@link Level} defines the logging of information. */
    public static final Level INFO  = new Level(INFO_FLAG, "INFO", ANSI.Traits.GREEN_FG, ANSI.Traits.NONE);

    /**
     * This {@link Level} defines the default priority {@link Level} of a {@link Logger} and is used to log
     * messaged for debugging purposes.
     */
    public static final Level DEBUG = new Level(DEBUG_FLAG, "DEBUG", ANSI.Traits.BLUE_FG, ANSI.Traits.NONE);

    /** This {link Level} is similar to the {@link #DEBUG} level, but usually carries more information. */
    public static final Level TRACE = new Level(TRACE_FLAG, "TRACE", ANSI.Traits.CYAN_FG, ANSI.Traits.NONE);

    /** This {@link Level} is used as a priority {@link Level} to allow all others. */
    public static final Level ALL   = new Level(ALL_FLAG, "ALL", ANSI.Traits.DESATURATED, ANSI.Traits.NONE);

    /** Used to compare a {@link Level levels} priority over another. */
    private final int flag;
    
    /** The readable name of a {@link Level} */
    private final String name;

    /** The foreground color of a message */
    private AnsiTrait foreground;
    
    /** The background color of a message */
    private AnsiTrait background;

    /**
     * Hidden constructor.
     * 
     * @param flag
     * @param name
     * @param foreground
     * @param background
     */
    private Level(int flag, String name, AnsiTrait foreground, AnsiTrait background) {
        this.flag = flag;
        this.name = name;
        this.foreground = foreground;
        this.background = background;
    }

    /**
     * Returns a {@link Level} with the given name.
     * 
     * @param name
     * @return {@link Level}
     */
    public static Level of(String name) {
        if (name == null) {
            throw new LevelNotFoundException();
        }

        switch (name.trim().toUpperCase()) {
            case "OFF":   return OFF;
            case "FATAL": return FATAL;
            case "ERROR": return ERROR;
            case "WARN":  return WARN;
            case "INFO":  return INFO;
            case "DEBUG": return DEBUG;
            case "TRACE": return TRACE;
            case "ALL":   return ALL;
            default:      throw new LevelNotFoundException();
        }
    }

    /**
     * Returns a {@link Level} with the given flag.
     * 
     * @param flag
     * @return {@link Level}
     */
    public static Level of(int flag) {
        switch (flag) {
            case OFF_FLAG:   return OFF;
            case FATAL_FLAG: return FATAL;
            case ERROR_FLAG: return ERROR;
            case WARN_FLAG:  return WARN;
            case INFO_FLAG:  return INFO;
            case DEBUG_FLAG: return DEBUG;
            case TRACE_FLAG: return TRACE;
            case ALL_FLAG:   return ALL;
            default:         throw new LevelNotFoundException();
        }
    }

    /**
     * Compares the flags of this {@link Level} to another.
     * 
     * @param other
     * @return {@code true} if the flags are equal, {@code false} otherwise.
     */
    public boolean hasPrecedence(Level other) {
        return this.flag >= other.flag;
    }

    /**
     * Returns the flag of this {@link Level}.
     * 
     * @return int
     */
    public int flag() {
        return flag;
    }

    /**
     * @return The foreground color of messages with this {@link Level}.
     */
    public AnsiTrait getForegroundColor() {
        return foreground;
    }

    /**
     * @return The background color of messages with this {@link Level}.
     */
    public AnsiTrait getBackgroundColor() {
        return background;
    }

    /**
     * @return An array containing all {@link Level}s.
     */
    public static Level[] levels() {
        return new Level[] {
            OFF,
            FATAL,
            ERROR,
            WARN,
            INFO,
            DEBUG,
            TRACE,
            ALL,
        };
    }

    /**
     * @return An array containing all {@link Level} flags.
     */
    public static int[] flags() {
        return new int[] {
            OFF_FLAG,
            FATAL_FLAG,
            ERROR_FLAG,
            WARN_FLAG,
            INFO_FLAG,
            DEBUG_FLAG,
            TRACE_FLAG,
            ALL_FLAG,
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }
}

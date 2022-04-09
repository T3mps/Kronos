package net.acidfrog.kronos.core.lang.logger;

import net.acidfrog.kronos.core.Config;

/**
 * Represents the escalation level of a log message. This is used to determine
 * the severity and the color of the message.
 * 
 * @author Ethan Temprovich
 * @see {@link Logger}
 */
public enum LogLevel {
    
    ALL("[ALL]      ",  Ansi.TEXT_GRAY, Ansi.TEXT_VOID, true),

    /**
     * The trace level is used for diagnostic messages. These messages are
     * typically more granular and fine than the debug level. This level is only
     * enabled if the {@link Config#DEBUG} flag is set to true.
     * 
     * @see {@link Config#DEBUG Config}
     */
    TRACE("[TRACE]    ", Ansi.TEXT_CYAN, Ansi.TEXT_VOID,  Config.getInstance().getBoolean("kronos.debug")),

    /**
     * The debug level is used to log information. These messages are typically
     * very verbose and informative. This level is only enabled if the
     * {@link Config#RELEASE} flag is set to false.
     */
    DEBUG("[DEBUG]    ", Ansi.TEXT_GREEN, Ansi.TEXT_VOID, !Config.getInstance().getBoolean("kronos.release")),
    
    /**
     * The info level is used to log expected application behavior. These messages
     * are nothing for a user to worry about. This level is enabled by
     * default.
     */
    INFO ("[INFO]     ", Ansi.TEXT_WHITE, Ansi.TEXT_VOID, true),
    
    /**
     * The warn level is used to log unexpected/undefined behavior. These messages
     * used to inform a user that a problem has occurred, but the application will
     * continue to run. This level is enabled by default.
     */
    WARN ("[WARN]     ", Ansi.TEXT_YELLOW, Ansi.TEXT_VOID, true),
    
    /**
     * The error level is used to log behavior that is accessing an unavaiable
     * service or resource. The application may continue to run, but will be in
     * a highly unstable state. This level is enabled by default.
     */
    ERROR("[ERROR]    ", Ansi.TEXT_RED, Ansi.TEXT_VOID, true),
    
    /**
     * The fatal level is used to notify the user that a catastrophic error has
     * occurred that casued the application to hault. This level is enabled by
     * default.
     */
    FATAL("[FATAL]    ", Ansi.TEXT_BLACK, Ansi.TEXT_RED_BACKGROUND, true);
    
    /** The tag appended to the front of the message */
    private final String prepend;

    /** The foreground and background color */
    private final String fg, bg;

    /** Whether or not the level will be logged */
    private final boolean enabled;

    /**
     * Full hidden constructor.
     * 
     * @param prepend The tag appended to the front of the message
     * @param fg The foreground color
     * @param bg The background color
     * @param enabled Whether or not the level will be logged
     */
    private LogLevel(final String prepend, final String fg, final String bg, final boolean enabled) {
        this.prepend = prepend;
        this.fg = fg;
        this.bg = bg;
        this.enabled = enabled;
    }

    /**
     * Returns the tag appended to the front of the message.
     * 
     * @return The tag
     */
    public String getPrepend() { return prepend; }

    /**
     * Returns the foreground color.
     * 
     * @return The foreground color
     */
    public String getForegroundColor() { return fg; }

    /**
     * Returns the background color.
     * 
     * @return The background color
     */
    public String getBackgroundColor() { return bg; }

    /**
     * Returns whether or not the level will be logged.
     * 
     * @return {@code true} if the level will be logged, {@code false} otherwise
     */
    public boolean isEnabled() { return enabled; }
    
}

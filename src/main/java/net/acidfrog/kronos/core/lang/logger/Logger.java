package net.acidfrog.kronos.core.lang.logger;

import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import net.acidfrog.kronos.core.Config;
import net.acidfrog.kronos.core.Config.OSArbiter;
import net.acidfrog.kronos.core.lang.IDArbiter;
import net.acidfrog.kronos.core.lang.annotations.Debug;

/**
 * Baisc Logger class.
 * 
 * @author Ethan Temprovich
 */
public final class Logger {

    private enum Level {
        
        TRACE("[TRACE] ", LogColors.CYAN,   LogColors.BLACK_BACKGROUND, (Config.RELEASE ? false : true)),
        DEBUG("[DEBUG] ", LogColors.GREEN,  LogColors.BLACK_BACKGROUND, (Config.RELEASE ? false : true)),
        INFO ("[INFO]  ", LogColors.WHITE,  LogColors.BLACK_BACKGROUND, true),
        WARN ("[WARN]  ", LogColors.YELLOW, LogColors.BLACK_BACKGROUND, true),
        ERROR("[ERROR] ", LogColors.RED,    LogColors.BLACK_BACKGROUND, true),
        FATAL("[FATAL] ", LogColors.BLACK,  LogColors.RED_BACKGROUND,   true);
        
        private final String prepend;
        private String fg, bg;

        private boolean enabled;

        private Level(final String prepend, final String fg, final String bg, final boolean enabled) {
            this.prepend = prepend;
            this.fg = fg;
            this.bg = bg;

            if (Config.OPERATING_SYSTEM.equals(OSArbiter.MAC)) {
                if (fg == LogColors.WHITE) this.fg = LogColors.BLACK;
                if (bg == LogColors.BLACK_BACKGROUND) this.bg = "";
                if (bg == LogColors.RED_BACKGROUND) this.fg = LogColors.WHITE;
            }

            this.enabled = enabled;
        }

        public String getPrepend() { return prepend; }

        public boolean isEnabled() { return enabled; }

    }

    public static Logger instance = new Logger(Config.LOG_STREAM, Config.ERROR_STREAM);

    private static final String EXTENSION = ".log";

    private final String instanceName;
    private final String instanceID;
    private final File directory;
    private final PrintStream log;
    private final PrintStream error;

    private Logger(final PrintStream log, final PrintStream error) {
        this.instanceName = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        this.instanceID = IDArbiter.next();
        this.directory = new File(Config.LOGGER_SAVE_PATH);
        this.log = log;
        this.error = error;

       initialize();
    }

    public boolean initialize() {
        if (directory.mkdirs()) log(Level.INFO, "Created directory: " + directory.getAbsolutePath());
        log(Level.INFO, "Logger(" + instanceID + ") initialized @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")));
        return true;
    }

    public void close(int n) {
        log(Level.INFO, "Logger(" + instanceID + ") { " + n + " } closed @ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")));
        
        synchronized(this) {
            log.close();
            error.close();
        }
    }

    private void logToFile(final String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String fileName = instanceName + EXTENSION;
        File file = new File(directory, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath(), true))) {
            writer.append("[" + sdf.format(new Date()) + "]" + message + "\n");
            writer.close();
        } catch (IOException e) {
            error.println("Failed to write to log file: " + e.getMessage());
        }
    }

    private void log(final Level level, final String message) {
        boolean isError = level.ordinal() >= 4;
        String output = level.getPrepend() + message;
        logToFile(output);
        output = level.fg + level.bg + output + LogColors.RESET + "\n";
        (isError ? error : log).print(output);
    }

    public void logTrace(final String message) {
        if (Level.TRACE.isEnabled()) log(Level.TRACE, message);
    }

    public void logDebug(final String message) {
        if (Level.DEBUG.isEnabled()) log(Level.DEBUG, message);
    }

    public void logInfo(final String message) {
        log(Level.INFO, message);
    }

    public void logWarn(final String message) {
        log(Level.WARN, message);
    }

    public void logError(final String message) {
        log(Level.ERROR, message);
    }

    public void logFatal(final String message) {
        log(Level.FATAL, message);
    }

    public void logBytes(final byte[] bytes) {
        String s = "0x";
        for (byte b : bytes) s += String.format("%01X", b);
        log.println(s);
    }

    public void logAnnotatedMethods(final Object object, final Class<? extends Annotation> annotation) {
        logAnnotatedMethods(object, annotation, false, "", "");
    }

    public void logAnnotatedMethods(final Object object, final Class<? extends Annotation> annotation, final boolean tab, final String head, final String tail) {
        for (Method method : getMethodsAnnotatedWith(object.getClass(), annotation)) logDebug((tab ? "\t" : "") + head + object.getClass().getSimpleName() + "." + method.getName() + "()" + tail);
    }

    public void logDebugMethods(final Object object, final boolean tab) {
        logAnnotatedMethods(object, Debug.class, tab, "-", "");
    }
    
    private List<Method> getMethodsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
        final List<Method> methods = new ArrayList<Method>();
        Class<?> clazz = type;

        // cycle through all parent classes (superclass, super-superclass, etc)
        while (clazz.getSuperclass() != null) {
            for (final Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation)) {
                    // Annotation instance = method.getAnnotation(annotation);
                    methods.add(method);
                }
            }
            
            clazz = clazz.getSuperclass(); 
        }

        return methods;
    }

    static final class LogColors {

        // Reset
        public static final String RESET = "\033[0m";  // Text Reset
    
        // Regular Colors
        public static final String BLACK = "\033[0;30m";   // BLACK
        public static final String RED = "\033[0;31m";     // RED
        public static final String GREEN = "\033[0;32m";   // GREEN
        public static final String YELLOW = "\033[0;33m";  // YELLOW
        public static final String BLUE = "\033[0;34m";    // BLUE
        public static final String PURPLE = "\033[0;35m";  // PURPLE
        public static final String CYAN = "\033[0;36m";    // CYAN
        public static final String WHITE = "\033[0;37m";   // WHITE
    
        // High Intensity
        public static final String BLACK_BRIGHT = "\033[0;90m";  // BLACK
        public static final String RED_BRIGHT = "\033[0;91m";    // RED
        public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
        public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
        public static final String BLUE_BRIGHT = "\033[0;94m";   // BLUE
        public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
        public static final String CYAN_BRIGHT = "\033[0;96m";   // CYAN
        public static final String WHITE_BRIGHT = "\033[0;97m";  // WHITE

        // Bold
        public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
        public static final String RED_BOLD = "\033[1;31m";    // RED
        public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
        public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
        public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
        public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
        public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
        public static final String WHITE_BOLD = "\033[1;37m";  // WHITE
    
        // Bold High Intensity
        public static final String BLACK_BOLD_BRIGHT = "\033[1;90m";  // BLACK
        public static final String RED_BOLD_BRIGHT = "\033[1;91m";    // RED
        public static final String GREEN_BOLD_BRIGHT = "\033[1;92m";  // GREEN
        public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m"; // YELLOW
        public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";   // BLUE
        public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m"; // PURPLE
        public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";   // CYAN
        public static final String WHITE_BOLD_BRIGHT = "\033[1;97m";  // WHITE
    
        // Underline
        public static final String BLACK_UNDERLINED = "\033[4;30m";  // BLACK
        public static final String RED_UNDERLINED = "\033[4;31m";    // RED
        public static final String GREEN_UNDERLINED = "\033[4;32m";  // GREEN
        public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
        public static final String BLUE_UNDERLINED = "\033[4;34m";   // BLUE
        public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
        public static final String CYAN_UNDERLINED = "\033[4;36m";   // CYAN
        public static final String WHITE_UNDERLINED = "\033[4;37m";  // WHITE
        
        // Background
        public static final String BLACK_BACKGROUND = "\033[40m";  // BLACK
        public static final String RED_BACKGROUND = "\033[41m";    // RED
        public static final String GREEN_BACKGROUND = "\033[42m";  // GREEN
        public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
        public static final String BLUE_BACKGROUND = "\033[44m";   // BLUE
        public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
        public static final String CYAN_BACKGROUND = "\033[46m";   // CYAN
        public static final String WHITE_BACKGROUND = "\033[47m";  // WHITE

        // High Intensity backgrounds
        public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";  // BLACK
        public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m";    // RED
        public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m";  // GREEN
        public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m"; // YELLOW
        public static final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m";   // BLUE
        public static final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
        public static final String CYAN_BACKGROUND_BRIGHT = "\033[0;106m";   // CYAN
        public static final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m";  // WHITE
        
    }

}

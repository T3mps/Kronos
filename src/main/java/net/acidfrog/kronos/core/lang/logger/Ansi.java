package net.acidfrog.kronos.core.lang.logger;

/**
 * Class for modifying ANSI text. (console text)
 * 
 * @author Ethan Temprovich
 */
public final class Ansi {

    // Reset
    public static final String TEXT_RESET = "\033[0m";  // Text Reset
    public static final String TEXT_VOID = "";
    
    // Regular Colors
    public static final String TEXT_BLACK = "\033[0;30m";   // BLACK
    public static final String TEXT_RED = "\033[0;31m";     // RED
    public static final String TEXT_GREEN = "\033[0;32m";   // GREEN
    public static final String TEXT_YELLOW = "\033[0;33m";  // YELLOW
    public static final String TEXT_BLUE = "\033[0;34m";    // BLUE
    public static final String TEXT_PURPLE = "\033[0;35m";  // PURPLE
    public static final String TEXT_CYAN = "\033[0;36m";    // CYAN
    public static final String TEXT_WHITE = "\033[0;37m";   // WHITE
    public static final String TEXT_GRAY = "\033[0;90m";    // GRAY

    // High Intensity
    public static final String TEXT_RED_BRIGHT = "\033[0;91m";    // RED
    public static final String TEXT_GREEN_BRIGHT = "\033[0;92m";  // GREEN
    public static final String TEXT_YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
    public static final String TEXT_BLUE_BRIGHT = "\033[0;94m";   // BLUE
    public static final String TEXT_PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
    public static final String TEXT_CYAN_BRIGHT = "\033[0;96m";   // CYAN
    public static final String TEXT_WHITE_BRIGHT = "\033[0;97m";  // WHITE

    // Bold
    public static final String TEXT_BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String TEXT_RED_BOLD = "\033[1;31m";    // RED
    public static final String TEXT_GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String TEXT_YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String TEXT_BLUE_BOLD = "\033[1;34m";   // BLUE
    public static final String TEXT_PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String TEXT_CYAN_BOLD = "\033[1;36m";   // CYAN
    public static final String TEXT_WHITE_BOLD = "\033[1;37m";  // WHITE

    // Bold High Intensity
    public static final String TEXT_BLACK_BOLD_BRIGHT = "\033[1;90m";  // BLACK
    public static final String TEXT_RED_BOLD_BRIGHT = "\033[1;91m";    // RED
    public static final String TEXT_GREEN_BOLD_BRIGHT = "\033[1;92m";  // GREEN
    public static final String TEXT_YELLOW_BOLD_BRIGHT = "\033[1;93m"; // YELLOW
    public static final String TEXT_BLUE_BOLD_BRIGHT = "\033[1;94m";   // BLUE
    public static final String TEXT_PURPLE_BOLD_BRIGHT = "\033[1;95m"; // PURPLE
    public static final String TEXT_CYAN_BOLD_BRIGHT = "\033[1;96m";   // CYAN
    public static final String TEXT_WHITE_BOLD_BRIGHT = "\033[1;97m";  // WHITE

    // Underline
    public static final String TEXT_BLACK_UNDERLINED = "\033[4;30m";  // BLACK
    public static final String TEXT_RED_UNDERLINED = "\033[4;31m";    // RED
    public static final String TEXT_GREEN_UNDERLINED = "\033[4;32m";  // GREEN
    public static final String TEXT_YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
    public static final String TEXT_BLUE_UNDERLINED = "\033[4;34m";   // BLUE
    public static final String TEXT_PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
    public static final String TEXT_CYAN_UNDERLINED = "\033[4;36m";   // CYAN
    public static final String TEXT_WHITE_UNDERLINED = "\033[4;37m";  // WHITE
    
    // Background
    public static final String TEXT_BLACK_BACKGROUND = "\033[40m";  // BLACK
    public static final String TEXT_RED_BACKGROUND = "\033[41m";    // RED
    public static final String TEXT_GREEN_BACKGROUND = "\033[42m";  // GREEN
    public static final String TEXT_YELLOW_BACKGROUND = "\033[43m"; // YELLOW
    public static final String TEXT_BLUE_BACKGROUND = "\033[44m";   // BLUE
    public static final String TEXT_PURPLE_BACKGROUND = "\033[45m"; // PURPLE
    public static final String TEXT_CYAN_BACKGROUND = "\033[46m";   // CYAN
    public static final String TEXT_WHITE_BACKGROUND = "\033[47m";  // WHITE

    // High Intensity backgrounds
    public static final String TEXT_BLACK_BACKGROUND_BRIGHT = "\033[0;100m";  // BLACK
    public static final String TEXT_RED_BACKGROUND_BRIGHT = "\033[0;101m";    // RED
    public static final String TEXT_GREEN_BACKGROUND_BRIGHT = "\033[0;102m";  // GREEN
    public static final String TEXT_YELLOW_BACKGROUND_BRIGHT = "\033[0;103m"; // YELLOW
    public static final String TEXT_BLUE_BACKGROUND_BRIGHT = "\033[0;104m";   // BLUE
    public static final String TEXT_PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
    public static final String TEXT_CYAN_BACKGROUND_BRIGHT = "\033[0;106m";   // CYAN
    public static final String TEXT_WHITE_BACKGROUND_BRIGHT = "\033[0;107m";  // WHITE

    public static String colorize(String message, String foregroundColor) {
        return foregroundColor + message + TEXT_RESET;
    }

    public static String colorize(String message, String foregroundColor, String backgroundColor) {
        return foregroundColor + backgroundColor + message + TEXT_RESET;
    }

}

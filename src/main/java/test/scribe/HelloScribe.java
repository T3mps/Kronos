package test.scribe;

import net.acidfrog.kronos.scribe.ANSI;
import net.acidfrog.kronos.scribe.Level;
import net.acidfrog.kronos.scribe.Logger;
import net.acidfrog.kronos.scribe.LoggerFactory;

public class HelloScribe {
    
    public static void main(String[] args) {
        String separator = ANSI.colorize("---\n", ANSI.Traits.GREY_FG);

        System.out.print(ANSI.colorize("Welcome! ", ANSI.Traits.RED_FG));
        System.out.print(ANSI.colorize("This is ", ANSI.Traits.YELLOW_FG));
        System.out.print(ANSI.colorize("a demo ", ANSI.Traits.GREEN_FG));
        System.out.print(ANSI.colorize("of the ", ANSI.Traits.CYAN_FG));
        System.out.print(ANSI.colorize("Scribe ", ANSI.Traits.BLUE_FG));
        System.out.print(ANSI.colorize("framework.\n", ANSI.Traits.MAGENTA_FG));
        System.out.println(separator);

        System.out.println(ANSI.colorize(ANSI.rainbow("Scribe is a simple logging framework for Java,"), ANSI.Traits.BOLD));
        System.out.println(ANSI.colorize(
                                        "designed to be easy to use, and highly customizable via the use of LogCallbacks.\n" +
                                        "A LogCallback is essentially a functional interface that allows a user to\n" +
                                        "customize the output of logged events. The callback recieves the log level\n" +
                                        "and message which allows the user to output this data in any way. A loggers\n" +
                                        "only way to output information is via these callbacks. There are two included\n" +
                                        "callbacks in the Scribe API: ConsoleLogCallback and FileLogCallback.",
                                        ANSI.Traits.ITALIC,
                                        ANSI.Traits.GREY_FG));
        System.out.println(separator);

        System.out.println("The following is a list of available log levels:\n");
        Logger logger = LoggerFactory.get("main", HelloScribe.class).setLevel(Level.ALL);
        for (var level : Level.levels()) {
            logger.log(level, "Level: " + level);
        }
        System.out.println(separator);

        System.out.println("The messages logged to the console are output using the following format:");
        System.out.println(ANSI.colorize("<timestamp> [<name>] <level> <loggedClass> - <message>", ANSI.Traits.WHITE_FG, ANSI.Traits.BLACK_BG, ANSI.Traits.BOLD));
        System.out.println(separator);
        
        // describe ansi colors
        System.out.println("Scribe uses ANSI escape sequences to format the output.\nThe following is a list of available formats:\n");
        System.out.print(ANSI.colorize("\tBOLD ", ANSI.Traits.BOLD));
        System.out.print(ANSI.colorize("DIM ", ANSI.Traits.DIM));
        System.out.print(ANSI.colorize("ITALIC ", ANSI.Traits.ITALIC));
        System.out.print(ANSI.colorize("UNDERLINED ", ANSI.Traits.UNDERLINED));
        System.out.println(ANSI.colorize("SLOW_BLINK ", ANSI.Traits.SLOW_BLINK));
        System.out.print(ANSI.colorize("\tFAST_BLINK ", ANSI.Traits.FAST_BLINK));
        System.out.print(ANSI.colorize("REVERSE ", ANSI.Traits.REVERSE));
        System.out.print(ANSI.colorize("HIDDEN ", ANSI.Traits.HIDDEN));
        System.out.println(ANSI.colorize("STRIKETHROUGH ", ANSI.Traits.STRIKETHROUGH));
        System.out.print(ANSI.colorize("\tFRAMED ", ANSI.Traits.FRAMED));
        System.out.print(ANSI.colorize("ENCIRCLED ", ANSI.Traits.ENCIRCLED));
        System.out.println(ANSI.colorize("OVERLINED", ANSI.Traits.OVERLINED));
        System.out.println(separator);

        System.out.println("The following is a list of available foreground colors:\n");
        System.out.print(ANSI.colorize("\tBLACK_FG ", ANSI.Traits.BLACK_FG));
        System.out.print(ANSI.colorize("RED_FG ", ANSI.Traits.RED_FG));
        System.out.println(ANSI.colorize("GREEN_FG ", ANSI.Traits.GREEN_FG));
        System.out.print(ANSI.colorize("\tYELLOW_FG ", ANSI.Traits.YELLOW_FG));
        System.out.print(ANSI.colorize("BLUE_FG ", ANSI.Traits.BLUE_FG));
        System.out.println(ANSI.colorize("MAGENTA_FG ", ANSI.Traits.MAGENTA_FG));
        System.out.print(ANSI.colorize("\tCYAN_FG ", ANSI.Traits.CYAN_FG));
        System.out.print(ANSI.colorize("WHITE_FG ", ANSI.Traits.WHITE_FG));
        System.out.println(ANSI.colorize("GREY_FG", ANSI.Traits.GREY_FG));
        System.out.println(separator);

        System.out.println("The following is a list of available background colors:\n");
        System.out.print("\t" + ANSI.colorize("BLACK_BG ", ANSI.Traits.BLACK_BG));
        System.out.print(ANSI.colorize("RED_BG ", ANSI.Traits.RED_BG));
        System.out.println(ANSI.colorize("GREEN_BG", ANSI.Traits.GREEN_BG));
        System.out.print("\t" + ANSI.colorize("YELLOW_BG ", ANSI.Traits.YELLOW_BG));
        System.out.print(ANSI.colorize("BLUE_BG ", ANSI.Traits.BLUE_BG));
        System.out.println(ANSI.colorize("MAGENTA_BG", ANSI.Traits.MAGENTA_BG));
        System.out.print("\t" + ANSI.colorize("CYAN_BG ", ANSI.Traits.CYAN_BG));
        System.out.print(ANSI.colorize("WHITE_BG ", ANSI.Traits.WHITE_BG));
        System.out.println(ANSI.colorize("GREY_BG", ANSI.Traits.GREY_BG));
        System.out.println(separator);
    }
}

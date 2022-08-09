package net.acidfrog.kronos.scribe;

public final class ANSI {

    public static final char ESCCODE = 27;
    
    public static final String NEWLINE = System.getProperty("line.separator");

    public static final String PREFIX = ESCCODE + "[";
    
    public static final String SEPARATOR = ";";
    
    public static final String POSTFIX = "m";

    public static final String RESET = PREFIX + Traits.CLEAR + POSTFIX;

    private static final AnsiTrait[] RAINBOW = new AnsiTrait[] { Traits.RED_FG, Traits.YELLOW_FG, Traits.GREEN_FG, Traits.CYAN_FG, Traits.BLUE_FG, Traits.MAGENTA_FG };

    public static String get(AnsiTrait... traits) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(PREFIX);
        for (var trait : traits) {
            String code = trait.toString();
            if (code.equals("")) {
                continue;
            }
            
            sb.append(code);
            sb.append(SEPARATOR);
        }
        sb.append(POSTFIX);

        return sb.toString().replace(SEPARATOR + POSTFIX, POSTFIX);
    }

    public static String colorize(String text, String ansiCode) {
        StringBuilder sb = new StringBuilder();

        sb.append(ansiCode);
        String ef = text.replace(NEWLINE, RESET + NEWLINE + ansiCode);
        sb.append(ef);
        sb.append(RESET);
        return sb.toString();
    }

    public static String colorize(char[] text, String ansiCode) {
        StringBuilder sb = new StringBuilder();

        sb.append(ansiCode);
        String ef = new String(text).replace(NEWLINE, RESET + NEWLINE + ansiCode);
        sb.append(ef);
        sb.append(RESET);
        return sb.toString();
    }

    public static String colorize(char c, String ansiCode) {
        return colorize(String.valueOf(c), ansiCode);
    }

    public static String colorize(String text, AnsiTrait... traits) {
        String ansiCode = get(traits);
        return colorize(text, ansiCode);
    }

    public static String colorize(char[] text, AnsiTrait... traits) {
        String ansiCode = get(traits);
        return colorize(text, ansiCode);
    }

    public static String colorize(char c, AnsiTrait... traits) {
        return colorize(String.valueOf(c), traits);
    }

    public static String rainbow(String text) {
        StringBuilder sb = new StringBuilder();
        char[] chars = text.toCharArray();
        
        int i = 0;
        for (var c : chars) {
            sb.append(colorize(c, RAINBOW[i++ % RAINBOW.length]));
        }
        
        return sb.toString();
    }

    public static String rainbow(char[] text) {
        StringBuilder sb = new StringBuilder();

        int i = 0;
        for (var c : text) {
            sb.append(colorize(c, RAINBOW[i++ % RAINBOW.length]));
        }
        
        return sb.toString();
    }

    public interface AnsiTrait {

        @Override
        public abstract String toString();
    }

    public enum Traits implements AnsiTrait {

        // Clear Screen
        CLEAR_SCREEN() {
            @Override
            public String toString() {
                return "H\\033[2J\"";
            }
        },
    
        // Effects
    
        NONE() {
            @Override
            public String toString() {
                return "";
            }
        },
        CLEAR() {
            @Override
            public String toString() {
                return "0";
            }
        },
        BOLD() {
            @Override
            public String toString() {
                return "1";
            }
        },
        SATURATED() {
            @Override
            public String toString() {
                return "1";
            }
        },
        DIM() {
            @Override
            public String toString() {
                return "2";
            }
        },
        DESATURATED() {
            @Override
            public String toString() {
                return "2";
            }
        },
        ITALIC() {
            @Override
            public String toString() {
                return "3";
            }
        },
        UNDERLINED() {
            @Override
            public String toString() {
                return "4";
            }
        },
        SLOW_BLINK() {
            @Override
            public String toString() {
                return "5";
            }
        },
        FAST_BLINK() {
            @Override
            public String toString() {
                return "6";
            }
        },
        REVERSE() {
            @Override
            public String toString() {
                return "7";
            }
        },
        HIDDEN() {
            @Override
            public String toString() {
                return "8";
            }
        },
        STRIKETHROUGH() {
            @Override
            public String toString() {
                return "9";
            }
        },
        FRAMED() {
            @Override
            public String toString() {
                return "51";
            }
        },
        ENCIRCLED() {
            @Override
            public String toString() {
                return "52";
            }
        },
        OVERLINED() {
            @Override
            public String toString() {
                return "53";
            }
        },
    
        // Colors
    
        BLACK_FG() {
            @Override
            public String toString() {
                return "30";
            }
        },
        RED_FG() {
            @Override
            public String toString() {
                return "31";
            }
        },
        GREEN_FG() {
            @Override
            public String toString() {
                return "32";
            }
        },
        YELLOW_FG() {
            @Override
            public String toString() {
                return "33";
            }
        },
        BLUE_FG() {
            @Override
            public String toString() {
                return "34";
            }
        },
        MAGENTA_FG() {
            @Override
            public String toString() {
                return "35";
            }
        },
        CYAN_FG() {
            @Override
            public String toString() {
                return "36";
            }
        },
        WHITE_FG() {
            @Override
            public String toString() {
                return "37";
            }
        },
        GREY_FG() {
            @Override
            public String toString() {
                return "90";
            }
        },
        BLACK_BG() {
            @Override
            public String toString() {
                return "40";
            }
        },
        RED_BG() {
            @Override
            public String toString() {
                return "41";
            }
        },
        GREEN_BG() {
            @Override
            public String toString() {
                return "42";
            }
        },
        YELLOW_BG() {
            @Override
            public String toString() {
                return "43";
            }
        },
        BLUE_BG() {
            @Override
            public String toString() {
                return "44";
            }
        },
        MAGENTA_BG() {
            @Override
            public String toString() {
                return "45";
            }
        },
        CYAN_BG() {
            @Override
            public String toString() {
                return "46";
            }
        },
        WHITE_BG() {
            @Override
            public String toString() {
                return "47";
            }
        },
        GREY_BG() {
            @Override
            public String toString() {
                return "100";
            }
        },
        BRIGHT_BLACK_FG() {
            @Override
            public String toString() {
                return "90";
            }
        },
        BRIGHT_RED_FG() {
            @Override
            public String toString() {
                return "91";
            }
        },
        BRIGHT_GREEN_FG() {
            @Override
            public String toString() {
                return "92";
            }
        },
        BRIGHT_YELLOW_FG() {
            @Override
            public String toString() {
                return "93";
            }
        },
        BRIGHT_BLUE_FG() {
            @Override
            public String toString() {
                return "94";
            }
        },
        BRIGHT_MAGENTA_FG() {
            @Override
            public String toString() {
                return "95";
            }
        },
        BRIGHT_CYAN_FG() {
            @Override
            public String toString() {
                return "96";
            }
        },
        BRIGHT_WHITE_FG() {
            @Override
            public String toString() {
                return "97";
            }
        },
        BRIGHT_BLACK_BG() {
            @Override
            public String toString() {
                return "100";
            }
        },
        BRIGHT_RED_BG() {
            @Override
            public String toString() {
                return "101";
            }
        },
        BRIGHT_GREEN_BG() {
            @Override
            public String toString() {
                return "102";
            }
        },
        BRIGHT_YELLOW_BG() {
            @Override
            public String toString() {
                return "103";
            }
        },
        BRIGHT_BLUE_BG() {
            @Override
            public String toString() {
                return "104";
            }
        },
        BRIGHT_MAGENTA_BG() {
            @Override
            public String toString() {
                return "105";
            }
        },
        BRIGHT_CYAN_BG() {
            @Override
            public String toString() {
                return "106";
            }
        },
        BRIGHT_WHITE_BG() {
            @Override
            public String toString() {
                return "107";
            }
        };
    }

    public static final class ComplexColor implements AnsiTrait {
    
        private String[] color;
        private boolean fg;
    
        private ComplexColor(int color8, boolean fg) {
            if (0 <= color8 && color8 <= 255) {
                color = new String[] { String.valueOf(color8) };
            } else {
                throw new IllegalArgumentException("Color must be a number inside range [0-255]; received: " + color8);
            }
            this.fg = fg;
        }
    
        private ComplexColor(int r, int g, int b, boolean fg) {
            if ((0 <= r && r <= 255) && (0 <= g && g <= 255) && (0 <= b && b <= 255)) {
                color = new String[] { String.valueOf(r), String.valueOf(g), String.valueOf(b) };
            } else {
                throw new IllegalArgumentException("Color must be a number inside range [0-255]; received: " + r + " " + g + " " + b);
            }
            this.fg = fg;
        }
    
        public static ComplexColor FROM_8BIT_FG(int color8) {
            return new ComplexColor(color8, true);
        }
    
        public static ComplexColor FROM_8BIT_BG(int color8) {
            return new ComplexColor(color8, false);
        }
    
        public static ComplexColor FROM_RGB_FG(int r, int g, int b) {
            return new ComplexColor(r, g, b, true);
        }
    
        public static ComplexColor FROM_RGB_BG(int r, int g, int b) {
            return new ComplexColor(r, g, b, false);
        }
    
        public boolean isTrueColor() {
            return color.length == 3;
        }
    
        protected String code() {
            if (isTrueColor()) {
                return color[0] + ANSI.SEPARATOR + color[1] + ANSI.SEPARATOR + color[2];
            } else {
                return color[0];
            }
        }
    
        @Override
        public String toString() {
            String ANSI_8BIT_COLOR_PREFIX = fg ? "38;5;" : "48;5;";
            String ANSI_TRUE_COLOR_PREFIX = fg ? "38;2;" : "48;2;";
    
            return (isTrueColor() ? ANSI_TRUE_COLOR_PREFIX : ANSI_8BIT_COLOR_PREFIX) + code();
        }
    }
}

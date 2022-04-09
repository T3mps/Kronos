package net.acidfrog.kronos.core.architecture;

import net.acidfrog.kronos.core.Config;
import net.acidfrog.kronos.core.lang.error.KronosError;
import net.acidfrog.kronos.core.lang.logger.Logger;

public class Kronos {

    public static final String NEW_LINE = Config.OPERATING_SYSTEM == Config.OSArbiter.WINDOWS ? "\r\n" : "\n";

    public static final int KR_PRESSED  = 1;
    public static final int KR_RELEASED = 0;

    public static final int
        KR_MOUSE_BUTTON_1      = 0,
        KR_MOUSE_BUTTON_2      = 1,
        KR_MOUSE_BUTTON_3      = 2,
        KR_MOUSE_BUTTON_4      = 3,
        KR_MOUSE_BUTTON_5      = 4,
        KR_MOUSE_BUTTON_6      = 5,
        KR_MOUSE_BUTTON_7      = 6,
        KR_MOUSE_BUTTON_8      = 7,
        KR_MOUSE_BUTTON_LEFT   = KR_MOUSE_BUTTON_1,
        KR_MOUSE_BUTTON_RIGHT  = KR_MOUSE_BUTTON_2,
        KR_MOUSE_BUTTON_MIDDLE = KR_MOUSE_BUTTON_3;
    
        /** The unknown key. */
        public static final int KR_UNKNOWN = -1;
    
        /** Printable keys */
        public static final int
            KR_KEY_SPACE         = 32,
            KR_KEY_APOSTROPHE    = 39,
            KR_KEY_COMMA         = 44,
            KR_KEY_MINUS         = 45,
            KR_KEY_PERIOD        = 46,
            KR_KEY_SLASH         = 47,
            KR_KEY_0             = 48,
            KR_KEY_1             = 49,
            KR_KEY_2             = 50,
            KR_KEY_3             = 51,
            KR_KEY_4             = 52,
            KR_KEY_5             = 53,
            KR_KEY_6             = 54,
            KR_KEY_7             = 55,
            KR_KEY_8             = 56,
            KR_KEY_9             = 57,
            KR_KEY_SEMICOLON     = 59,
            KR_KEY_EQUAL         = 61,
            KR_KEY_A             = 65,
            KR_KEY_B             = 66,
            KR_KEY_C             = 67,
            KR_KEY_D             = 68,
            KR_KEY_E             = 69,
            KR_KEY_F             = 70,
            KR_KEY_G             = 71,
            KR_KEY_H             = 72,
            KR_KEY_I             = 73,
            KR_KEY_J             = 74,
            KR_KEY_K             = 75,
            KR_KEY_L             = 76,
            KR_KEY_M             = 77,
            KR_KEY_N             = 78,
            KR_KEY_O             = 79,
            KR_KEY_P             = 80,
            KR_KEY_Q             = 81,
            KR_KEY_R             = 82,
            KR_KEY_S             = 83,
            KR_KEY_T             = 84,
            KR_KEY_U             = 85,
            KR_KEY_V             = 86,
            KR_KEY_W             = 87,
            KR_KEY_X             = 88,
            KR_KEY_Y             = 89,
            KR_KEY_Z             = 90,
            KR_KEY_LEFT_BRACKET  = 91,
            KR_KEY_BACKSLASH     = 92,
            KR_KEY_RIGHT_BRACKET = 93,
            KR_KEY_GRAVE_ACCENT  = 96,
            KR_KEY_WORLD_1       = 161,
            KR_KEY_WORLD_2       = 162;
    
        /** Function keys. */
        public static final int
            KR_KEY_ESCAPE        = 256,
            KR_KEY_ENTER         = 257,
            KR_KEY_TAB           = 258,
            KR_KEY_BACKSPACE     = 259,
            KR_KEY_INSERT        = 260,
            KR_KEY_DELETE        = 261,
            KR_KEY_RIGHT         = 262,
            KR_KEY_LEFT          = 263,
            KR_KEY_DOWN          = 264,
            KR_KEY_UP            = 265,
            KR_KEY_PAGE_UP       = 266,
            KR_KEY_PAGE_DOWN     = 267,
            KR_KEY_HOME          = 268,
            KR_KEY_END           = 269,
            KR_KEY_CAPS_LOCK     = 280,
            KR_KEY_SCROLL_LOCK   = 281,
            KR_KEY_NUM_LOCK      = 282,
            KR_KEY_PRINT_SCREEN  = 283,
            KR_KEY_PAUSE         = 284,
            KR_KEY_F1            = 290,
            KR_KEY_F2            = 291,
            KR_KEY_F3            = 292,
            KR_KEY_F4            = 293,
            KR_KEY_F5            = 294,
            KR_KEY_F6            = 295,
            KR_KEY_F7            = 296,
            KR_KEY_F8            = 297,
            KR_KEY_F9            = 298,
            KR_KEY_F10           = 299,
            KR_KEY_F11           = 300,
            KR_KEY_F12           = 301,
            KR_KEY_F13           = 302,
            KR_KEY_F14           = 303,
            KR_KEY_F15           = 304,
            KR_KEY_F16           = 305,
            KR_KEY_F17           = 306,
            KR_KEY_F18           = 307,
            KR_KEY_F19           = 308,
            KR_KEY_F20           = 309,
            KR_KEY_F21           = 310,
            KR_KEY_F22           = 311,
            KR_KEY_F23           = 312,
            KR_KEY_F24           = 313,
            KR_KEY_F25           = 314,
            KR_KEY_KP_0          = 320,
            KR_KEY_KP_1          = 321,
            KR_KEY_KP_2          = 322,
            KR_KEY_KP_3          = 323,
            KR_KEY_KP_4          = 324,
            KR_KEY_KP_5          = 325,
            KR_KEY_KP_6          = 326,
            KR_KEY_KP_7          = 327,
            KR_KEY_KP_8          = 328,
            KR_KEY_KP_9          = 329,
            KR_KEY_KP_DECIMAL    = 330,
            KR_KEY_KP_DIVIDE     = 331,
            KR_KEY_KP_MULTIPLY   = 332,
            KR_KEY_KP_SUBTRACT   = 333,
            KR_KEY_KP_ADD        = 334,
            KR_KEY_KP_ENTER      = 335,
            KR_KEY_KP_EQUAL      = 336,
            KR_KEY_LEFT_SHIFT    = 340,
            KR_KEY_LEFT_CONTROL  = 341,
            KR_KEY_LEFT_ALT      = 342,
            KR_KEY_LEFT_SUPER    = 343,
            KR_KEY_RIGHT_SHIFT   = 344,
            KR_KEY_RIGHT_CONTROL = 345,
            KR_KEY_RIGHT_ALT     = 346,
            KR_KEY_RIGHT_SUPER   = 347,
            KR_KEY_MENU          = 348,
            KR_KEY_LAST          = KR_KEY_MENU;

    //==============================================================================
    //=================================== MACROS ===================================
    //==============================================================================

    public static final void FORCE_CLOSE() throws KronosError {
        Logger.logFatal("FORCE_CLOSE");
        Logger.close(1);
        System.exit(1);
    }

}

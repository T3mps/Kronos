package net.acidfrog.kronos.core;

import java.io.File;
import java.io.PrintStream;
import java.util.Locale;

/**
 * Options for Kronos library.
 * 
 * @author Ethan Temprovich
 */
public class Config {

    // system vars
    public static final String OPERATING_SYSTEM = OSArbiter.getOperatingSystemType();

    // engine vars
    public static final boolean DEBUG              =   true;
    public static final boolean RELEASE            =   false;

    public static final int REGULAR_UPDATE_RATE    =   60;
    public static final int PHYSICS_UPDATE_RATE    =   REGULAR_UPDATE_RATE - (REGULAR_UPDATE_RATE / 6);
    
    // math vars
    public static final int     SIN_LOOKUP_BITS;
    public static final boolean FASTMATH           =   true;  // rececomneded true
    public static final boolean IM_FASTMATH        =   false; // imprecise fastmath: reccomended false
    
    // logger vars
    public static final PrintStream LOG_STREAM     =   System.out;
    public static final PrintStream ERROR_STREAM   =   System.err;
    public static final String LOGGER_SAVE_PATH    =   path("data", "logs");
    
    // kron vars
    public static final String KRON_SAVE_PATH      =   path("data", "kron");
    
    // scene vars
    public static final String SCENE_SAVE_PATH     =   path("data", "scenes");
    
    static {
        if (IM_FASTMATH) SIN_LOOKUP_BITS = 14;
        else SIN_LOOKUP_BITS = 16;
    }

    private static final String path(String... strings) {
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            sb.append(s);
            sb.append(File.separator);
        }
        return sb.toString();
    }

    public final class OSArbiter {

        public static final String WINDOWS         = "WINDOWS";
        public static final String MAC             = "DARWIN";
        public static final String LINUX           = "LINUX";
        public static final String NOT_SUPPORTED   = "NOT_SUPPORTED";

        private static String os;
        
        public static String getOperatingSystemType() {
            if (os == null) {
                String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            
                if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) os = MAC;
                else if (OS.indexOf("win") >= 0) os = WINDOWS;
                else if (OS.indexOf("nux") >= 0) os = LINUX;
                else os = NOT_SUPPORTED;
            }

            return os;
        }
    }
}

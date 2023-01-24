package com.starworks.kronos.toolkit;

import java.lang.management.ManagementFactory;

public class SystemInfo {

    public static String getOSName() {
        return System.getProperty("os.name");
    }

    public static String getOSArchitecture() {
        String arch = System.getProperty("os.arch");
        return switch (arch) {
            case "x86" -> "32-bit";
            case "amd64" -> "64-bit";
            default -> arch;
        };
    }

    public static String getMemory() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        return String.valueOf(maxMemory / 1024 / 1024);
    }
    
    public static int getCPUCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static String getCPUName() {
        return System.getenv("PROCESSOR_IDENTIFIER");
    }

    public static String getCPUArchitecture() {
        return System.getenv("PROCESSOR_ARCHITECTURE");
    }
    
    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }

    public static String getJavaVendor() {
        return System.getProperty("java.vendor");
    }

    public static String getJavaHome() {
        return System.getProperty("java.home");
    }

    public static String getJvmName() {
        return ManagementFactory.getRuntimeMXBean().getVmName();
    }

    public static String getJvmVersion() {
        return ManagementFactory.getRuntimeMXBean().getVmVersion();
    }
    
    public static String getJvmVendor() {
        return ManagementFactory.getRuntimeMXBean().getVmVendor();
    }

    public static String getUserName() {
        return System.getProperty("user.name");
    }

    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    public static String getUserDir() {
        return System.getProperty("user.dir");
    }
}

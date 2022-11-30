package com.starworks.kronos.toolkit.internal;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SystemInfo {
    
    public static final String X86 = "x86";
    public static final String X64 = "x64";
    public static final String IA32 = "ia32";
    public static final String IA64 = "ia64";
    public static final String PPC = "ppc";
    public static final String PPC64 = "ppc64";
    public static final String ARM64 = "arm64";

    private static final Map<String, String> archMapping = new HashMap<String, String>();
    static {
        // x86 mappings
        archMapping.put(X86, X86);
        archMapping.put("i386", X86);
        archMapping.put("i486", X86);
        archMapping.put("i586", X86);
        archMapping.put("i686", X86);
        archMapping.put("pentium", X86);

        // x64 mappings
        archMapping.put(X64, X64);
        archMapping.put("amd64", X64);
        archMapping.put("em64t", X64);
        archMapping.put("universal", X64); // Needed for openjdk7 in Mac

        // Itenium 32-bit mappings, usually an HP-UX construct
        archMapping.put(IA32, IA32);
        archMapping.put("ia64n", IA32);

        // Itenium 64-bit mappings
        archMapping.put(IA64, IA64);
        archMapping.put("ia64w", IA64);

        // PowerPC mappings
        archMapping.put(PPC, PPC);
        archMapping.put("power", PPC);
        archMapping.put("powerpc", PPC);
        archMapping.put("power_pc", PPC);
        archMapping.put("power_rs", PPC);

        archMapping.put(PPC64, PPC64);
        archMapping.put("power64", PPC64);
        archMapping.put("powerpc64", PPC64);
        archMapping.put("power_pc64", PPC64);
        archMapping.put("power_rs64", PPC64);

        // aarch64 mappings
        archMapping.put("aarch64", ARM64);
    }

    private static String osName = null;
    private static String osArch;

    public static String getOSName() {
        if (osName == null) {
            String name = System.getProperty("os.name");

            osName = switch (name) {
                case "Windows"  -> "windows";
                case "Mac OS X" -> "macosx";
                case "Darwin"   -> "macosx";
                case "Linux"    -> "linux";
                case "AIX"      -> "aix";
                default         -> "unknown";
            };
        }

        return osName;
    }

    public static String getOSArch() {
        if (osArch == null) {
            String osArch = System.getProperty("os.arch");
            if (isAndroid()) {
                return osArch = "android-arm";
            }
            
            if (osArch.startsWith("arm")) {
                return osArch = armArch();
            } else {
                String lc = osArch.toLowerCase(Locale.US);
                if (archMapping.containsKey(lc)) {
                    return osArch = archMapping.get(lc);
                }
            }

            osArch = osArch.replaceAll("\\W", "_");
        }

        return osArch;
    }

    private static boolean isAndroid() {
        return System.getProperty("java.vm.vendor").equals("The Android Project") || System.getProperty("java.runtime.name", "").toLowerCase().contains("android");
    }

    private static String armArch() {
        if (System.getProperty("os.name").contains("Linux")) {
            String type;

            try {
                Process p = Runtime.getRuntime().exec("uname -m");
                p.waitFor();

                var in = p.getInputStream();
                try {
                    int len = 0;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[32];
                    while((len = in.read(buf, 0, 32)) >= 0) {
                        baos.write(buf, 0, len);
                    }
                    type = baos.toString();
                } finally {
                    in.close();
                }
            } catch (Exception e) {
                return "arm";
            }

            if (type.startsWith("armv6")) {
                // Raspberry PI
                return "armv6";
            } else if (type.startsWith("armv7")) {
                // Generic
                return "armv7";
            } else if (type.startsWith("armv5")) {
                // Use armv5, soft-float ABI
                return "arm";
            } else if (type.equals("aarch64")) {
                // Use arm64
                return "arm64";
            }

            String abi = System.getProperty("sun.arch.abi");
            if (abi != null && abi.startsWith("gnueabihf")) {
                return "armv7";
            }
        }

        return "arm";
    }
}

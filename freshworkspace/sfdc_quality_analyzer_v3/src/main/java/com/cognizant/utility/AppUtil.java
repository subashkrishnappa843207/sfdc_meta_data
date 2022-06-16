package com.cognizant.utility;

/**
 *
 * @author Debdatta Porya
 * @since
 */
public class AppUtil {


    public enum OSType {
        WINDOWS,
        LINUX,
        MACOSX,
        UNKNOWN
    }

    public static OSType getOSType() throws SecurityException {
        String osName = System.getProperty(OS_NAME);

        if (osName != null) {
            if (osName.contains("Windows")) {
                return OSType.WINDOWS;
            }

            if (osName.contains("Linux")) {
                return OSType.LINUX;
            }

            if (osName.contains("OS X")) {
                return OSType.MACOSX;
            }

            // determine another OS here
        }

        return OSType.UNKNOWN;
    }

    public static final String OS_NAME = "os.name";
}

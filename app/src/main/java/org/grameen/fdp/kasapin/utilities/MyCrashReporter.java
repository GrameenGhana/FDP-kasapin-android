package org.grameen.fdp.kasapin.utilities;


import com.balsikandar.crashreporter.CrashReporter;

// Available for Debug only
public final class MyCrashReporter {
    private MyCrashReporter() {
        throw new AssertionError("No instances.");
    }

    public static void log(int priority, String tag, String message) {
        // TODO add log entry to circular buffer.
    }

    public static void logWarning(Throwable t) {
        // TODO report non-fatal warning.
        CrashReporter.logException((Exception) t);
    }

    public static void logError(Throwable t) {
        // TODO report non-fatal error.
        CrashReporter.logException((Exception) t);
    }
}
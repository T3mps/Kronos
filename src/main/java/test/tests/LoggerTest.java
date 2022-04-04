package test.tests;

import net.acidfrog.kronos.core.lang.logger.Logger;

public class LoggerTest {
    
    public static void main(String[] args) {
        Logger.logTrace("Log Trace Test");
		Logger.logDebug("Log Debug Test");
		Logger.logInfo("Log Info Test");
		Logger.logWarn("Log Warn Test");
		Logger.logError("Log Error Test");
		Logger.logFatal("Log Fatal Test");
    }
    
}

package test.tests;

import net.acidfrog.kronos.core.lang.logger.Logger;

public class LoggerTest {
    
    public static void main(String[] args) {
        Logger.instance.logTrace("Log Trace Test");
		Logger.instance.logDebug("Log Debug Test");
		Logger.instance.logInfo("Log Info Test");
		Logger.instance.logWarn("Log Warn Test");
		Logger.instance.logError("Log Error Test");
		Logger.instance.logFatal("Log Fatal Test");
    }
    
}

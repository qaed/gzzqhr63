package nc.bs.integration.common.logging;

import nc.bs.logging.Level;
import nc.bs.logging.LoggerPlugin;
import nc.bs.logging.LoggerPluginProvider;

/**
 * 日志工具类
 * @author rime
 *
 */
public final class IntegrationLogger {
	
	private static final LoggerPlugin logPlugin = LoggerPluginProvider.getInstance().getLoggerPlugin("LightRegisterServlet");
	
	public static void info(Object message) {		
		logPlugin.log(Level.INFO, message);
	}
	
	public static void info(Object message, Throwable t) {		
		logPlugin.log(Level.INFO, message, t);
	}
	
	public static void debug(Object message) {		
		logPlugin.log(Level.DEBUG, message);	
	}
	
	public static void debug(Object message, Throwable t) {		
		logPlugin.log(Level.DEBUG, message, t);	
	}
	
	public static void warn(Object message) {		
		logPlugin.log(Level.WARN, message);	
	}
	
	public static void warn(Object message, Throwable t) {		
		logPlugin.log(Level.WARN, message, t);	
	}
	
	public static void error(Object message) {		
		logPlugin.log(Level.ERROR, message);
	}
	
	public static void error(Object message, Throwable t) {		
		logPlugin.log(Level.ERROR, message, t);		
	}
	
	public static void fatal(Object message) {
		logPlugin.log(Level.FATAL, message);
	}
	
	public static void fatal(Object message,  Throwable t) {
		logPlugin.log(Level.FATAL, message, t);
	}
	
}

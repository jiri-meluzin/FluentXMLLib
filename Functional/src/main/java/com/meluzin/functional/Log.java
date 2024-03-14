package com.meluzin.functional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Log {
	public static final SimpleDateFormat LOG_DATETIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static final SimpleDateFormat XSD_DATETIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	public static Formatter formatter = new CustomerFormatter();

	public static void main(String[] args) {
		System.out.println(Log.LOG_DATETIME_FORMATTER.format(new Date()));
		System.out.println(Log.XSD_DATETIME_FORMATTER.format(new Date()));
		Log.get().severe("started");
		Log.get().severe("finished");
		Log.get().info("finished");
		Log.get().fine("abc");
	}

	private static List<Handler> handlers = new ArrayList<>();
	static {
		try {
			LogManager.getLogManager().readConfiguration(Log.class.getResourceAsStream("/logging.properties"));
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		if (isLoggingConfigFileAvailable()) {
//			String levelPropertyValue = System.getProperty("log.level", "info");
//			setLogLevel(levelPropertyValue);
//		}
//		handlers.add(new SystemOutHandler());
	}

	public static boolean isLoggingConfigFileAvailable() {
		return System.getProperty("java.util.logging.config.file") == null;
	}

	public static void setLogLevel(String levelPropertyValue) {
		String level = Arrays.
				asList("debug", "info", "warning").stream().
				filter(l -> levelPropertyValue.equals(l)).
				findAny().
				orElse("info");
		try {
			LogManager.getLogManager().readConfiguration(Log.class.getResourceAsStream("logging." + level + ".properties"));
		} catch (final IOException e) {
			Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
			Logger.getAnonymousLogger().severe(e.getMessage());
		}
	}

	public static void addHandler(Handler handler) {
		handlers.add(handler);
	}

	public static Logger get(Class<?> cl) {
		return get(cl.getName());
	}

	public static Logger get() {
		StackTraceElement[] el = Thread.currentThread().getStackTrace();
		String className = el[2].getClassName();
		return get(className);
	}

	public static Logger get(String className) {
		Logger logger = Logger.getLogger(className);
//		logger.setUseParentHandlers(false);
//		if (logger.getHandlers().length == 0) {
//			for (Handler handler : handlers) {
//				logger.addHandler(handler);
//			}
//		}

		return logger;
	}

	public static class SystemOutHandler extends ConsoleHandler {
		public SystemOutHandler() {
			setFormatter(formatter);
			setLevel(Level.FINE);
		}
	}
}

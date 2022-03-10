package com.meluzin.functional;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Log {
	public static final SimpleDateFormat LOG_DATETIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static final SimpleDateFormat XSD_DATETIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	public static Formatter formatter = new Formatter() {

		@Override
		public String format(LogRecord paramLogRecord) {
			Date date = new Date(paramLogRecord.getMillis());
			String stackTrace = "";
			if (paramLogRecord.getThrown() != null) {
				try (StringWriter sw = new StringWriter()) {
					try (PrintWriter pw = new PrintWriter(sw)) {
		
						paramLogRecord.getThrown().printStackTrace(pw);
						stackTrace = sw.toString();
					} 
				} catch (IOException e) {
				}
			}
			return LOG_DATETIME_FORMATTER.format(date)+ " [" + paramLogRecord.getSourceClassName()+":" + paramLogRecord.getSourceMethodName() + "] [" + paramLogRecord.getLevel() + "] " + paramLogRecord.getMessage() + stackTrace + "\n";
		}
		
	};
	public static void main(String[] args) {
		 System.out.println(Log.LOG_DATETIME_FORMATTER.format(new Date()));
		 System.out.println(Log.XSD_DATETIME_FORMATTER.format(new Date()));
		 //Log.addHandler(new SystemOutHandler());
		 Log.get().severe("started");
		 Log.get().severe("finished");
		 Log.get().info("finished");
		 Log.get().fine("abc");
	}
	private static List<Handler> handlers = new ArrayList<>();
	static {
		//Logger.getGlobal().setLevel(Level.FINEST);
		//Logger.getGlobal().addHandler(new SystemOutHandler());
		if (System.getProperty("java.util.logging.config.file") == null) {
			//System.setProperty("java.util.logging.config.file", path);
			String level = System.getProperty("log.level", "info");
		      try
		      {
		          LogManager.getLogManager().readConfiguration(Log.class.getResourceAsStream("logging."+level+".properties"));
		      }
		      catch (final IOException e)
		      {
		          Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
		          Logger.getAnonymousLogger().severe(e.getMessage());
		      }
		} else System.out.println("java.util.logging.config.file="+System.getProperty("java.util.logging.config.file"));
		handlers.add(new SystemOutHandler());
	}
	public static void addHandler(Handler handler) {
		handlers.add(handler);
	}
	public static Logger get() {
		StackTraceElement[] el = Thread.currentThread().getStackTrace();
		Logger logger = Logger.getLogger(el[2].getClassName());		
		//logger.addHandler(new SystemOutHandler());
		logger.setUseParentHandlers(false);
		//consoleHandler.setLevel(Level.INFO);
		if (logger.getHandlers().length == 0) {			
			for (Handler handler : handlers) {
				logger.addHandler(handler);
			}
		}
		
		return logger;
	}
	public static class SystemOutHandler extends ConsoleHandler {
	  public SystemOutHandler() {
	   // setOutputStream(System.out);
	    setFormatter(formatter);
	    setLevel(Level.FINE);
	  }
	  @Override
		public void publish(LogRecord record) {
			// TODO Auto-generated method stub
			super.publish(record);
		}
	}
}

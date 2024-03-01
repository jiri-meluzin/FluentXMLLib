package com.meluzin.functional;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class CustomerFormatter extends Formatter {
	private Map<Long, Optional<Thread>> threadMap = new WeakHashMap<>();
	public Optional<String> getThreadName(long threadId) {
		return threadMap.computeIfAbsent(threadId, id -> Thread.getAllStackTraces().keySet().stream().filter(t -> t.getId() == id).findFirst()).map(t -> t.getName());
	}
    public static String abbreviateClassName(String loggerName, int length) {
    	String className = loggerName.replaceAll("[^\\.]+\\.", "");
    	String packageName = loggerName.replaceAll("\\.[^\\.]+$", "");
    	List<String> asList = Arrays.asList(packageName.split("\\."));
    	Collections.reverse(asList);
        return asList.stream()
                //.map(part -> part.length() > 1 ? part.substring(0, 1) + "." : part)
                .reduce((part1, part2) ->(className.length()+part1.length() >= length ? part2.substring(0,1) : (part2)) +"."+ part1)
                .orElse("")+"."+className;
    }
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
		Optional<StackTraceElement> foundStack = new CallerFinder().get();
		String foundMethod = foundStack.map(u -> u.getMethodName()).orElse("N/A method");
		String foundClassName = foundStack.map(u -> u.getClassName()).orElse("N/A class");
		return Log.LOG_DATETIME_FORMATTER.format(date) 
				+" ["+getThreadName(paramLogRecord.getThreadID()).map(s -> s.replace("ForkJoinPool.commonPool-worker", "FJP")).orElse(paramLogRecord.getThreadID()+"")+"]"
				+" [" + abbreviateClassName(foundClassName,20) + ":"
				+ foundMethod + "] [" + paramLogRecord.getLevel() + "] "
				+ paramLogRecord.getMessage() + stackTrace + "\n";
	}
    @SuppressWarnings("removal")
    static final class CallerFinder implements Predicate<StackTraceElement> {
        private static final StackTraceElement[] stackTrace;
        static {
            stackTrace = new Throwable().getStackTrace();
        }

        /**
         * Returns StackFrame of the caller's frame.
         * @return StackFrame of the caller's frame.
         */
        Optional<StackTraceElement> get() {
            return Arrays.asList(stackTrace).stream().filter(this).findFirst();
        }

        private boolean lookingForLogger = true;
        /**
         * Returns true if we have found the caller's frame, false if the frame
         * must be skipped.
         *
         * @param t The frame info.
         * @return true if we have found the caller's frame, false if the frame
         * must be skipped.
         */
        @Override
        public boolean test(StackTraceElement t) {
            final String cname = t.getClassName();
            // We should skip all frames until we have found the logger,
            // because these frames could be frames introduced by e.g. custom
            // sub classes of Handler.
            if (lookingForLogger) {
                // the log record could be created for a platform logger
                lookingForLogger = !isLoggerImplFrame(cname);
                return false;
            }
            // Continue walking until we've found the relevant calling frame.
            // Skips logging/logger infrastructure.
            return !isFilteredFrame(t);
        }
        static boolean isFilteredFrame(StackTraceElement st) {
            // skip logging/logger infrastructure
//            if (System.Logger.class.isAssignableFrom(st.getDeclaringClass())) {
//                return true;
//            }

            // fast escape path: all the prefixes below start with 's' or 'j' and
            // have more than 12 characters.
            final String cname = st.getClassName();
            char c = cname.length() < 12 ? 0 : cname.charAt(0);
            if (c == 's') {
                // skip internal machinery classes
                if (cname.startsWith("sun.util.logging."))   return true;
                if (cname.startsWith("sun.rmi.runtime.Log")) return true;
            } else if (c == 'j') {
                // Message delayed at Bootstrap: no need to go further up.
                if (cname.startsWith("jdk.internal.logger.BootstrapLogger$LogEvent")) return false;
                // skip public machinery classes
                if (cname.startsWith("jdk.internal.logger."))          return true;
                if (cname.startsWith("java.util.logging."))            return true;
                if (cname.startsWith("java.lang.invoke.MethodHandle")) return true;
                if (cname.startsWith("java.security.AccessController")) return true;
            } else if (c == 'o') {
            	if (cname.startsWith("org.codehaus.groovy.vmplugin.v8")) return true;
            	if (cname.startsWith("org.slf4j.impl")) return true;
            }


            return false;
        }

        private boolean isLoggerImplFrame(String cname) {
            return (cname.equals("java.util.logging.Logger") ||
                cname.startsWith("sun.util.logging.PlatformLogger"));
        }
    }
}
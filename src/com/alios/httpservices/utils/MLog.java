package com.alios.httpservices.utils;

import android.content.Context;

import com.alios.ilog.Logger;
import com.alios.ilog.LoggerFactory;
import com.alios.ilog.config.PropertyConfigurator;

public class MLog {

	private static final String DEFAULT_TAG = "REMOTE_SERVICE";

	public static void init(Context cContext) {
		PropertyConfigurator.getConfigurator(cContext).configure();
	}

	public static final void i(String tag, String msg) {
		Logger cLogger = getCurrentLogger(tag);
		cLogger.info(msg);
	}

	public static final void i(String tag, String msg, Throwable throwable) {
		Logger cLogger = getCurrentLogger(tag);
		cLogger.info(msg, throwable);
	}

	public static final void d(String tag, String msg) {
		Logger cLogger = getCurrentLogger(tag);
		cLogger.debug(msg);
	}

	public static final void d(String tag, String msg, Throwable throwable) {
		Logger cLogger = getCurrentLogger(tag);
		cLogger.debug(msg, throwable);
	}

	public static final void w(String tag, String msg) {
		Logger cLogger = getCurrentLogger(tag);
		cLogger.warn(msg);
	}

	public static final void w(String tag, String msg, Throwable throwable) {
		Logger cLogger = getCurrentLogger(tag);
		cLogger.warn(msg, throwable);
	}

	public static final void e(String tag, String msg) {
		Logger cLogger = getCurrentLogger(tag);
		cLogger.error(msg);
	}

	public static final void e(String tag, String msg, Throwable throwable) {
		Logger cLogger = getCurrentLogger(tag);
		cLogger.error(msg, throwable);
	}

	public static final void i(String msg) {
		Logger cLogger = getCurrentLogger();
		cLogger.info(msg);
	}

	public static final void i(String msg, Throwable throwable) {
		Logger cLogger = getCurrentLogger();
		cLogger.info(msg, throwable);
	}

	public static final void d(String msg) {
		Logger cLogger = getCurrentLogger();
		cLogger.debug(msg);
	}

	public static final void d(String msg, Throwable throwable) {
		Logger cLogger = getCurrentLogger();
		cLogger.debug(msg, throwable);
	}

	public static final void w(String msg) {
		Logger cLogger = getCurrentLogger();
		cLogger.warn(msg);
	}

	public static final void w(String msg, Throwable throwable) {
		Logger cLogger = getCurrentLogger();
		cLogger.warn(msg, throwable);
	}

	public static final void e(String msg) {
		Logger cLogger = getCurrentLogger();
		cLogger.error(msg);
	}

	public static final void e(String msg, Throwable throwable) {
		Logger cLogger = getCurrentLogger();
		cLogger.error(msg, throwable);
	}

	public static final Logger getCurrentLogger() {
		return getCurrentLogger(null);
	}

	public static final Logger getCurrentLogger(String tag) {
		StackTraceElement cStackTraceElement = null;
		String cTag = DEFAULT_TAG;
		try {
			cStackTraceElement = Thread.currentThread().getStackTrace()[5];
			cTag = cStackTraceElement.getClassName();
		} catch (Exception e) {
		}
		Logger cLogger = LoggerFactory.getLogger(cTag);
		if (tag == null) {
			cLogger.setClientID(DEFAULT_TAG);
		} else {
			cLogger.setClientID(tag);
		}
		return cLogger;
	}

}

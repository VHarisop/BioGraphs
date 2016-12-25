package gr.demokritos.iit.biographs;

import java.io.IOException;
import java.util.logging.*;

/**
 * An auxiliary class used for BioGraphs' logging facilities.
 * @author vharisop
 *
 */
public class Logging {
	/*
	 * The private formatter used for logging.
	 */
	private static final Formatter fmt = new SimpleFormatter();
	
	/*
	 * Internal logger.
	 */
	private static final Logger privLogger =
			Logger.getLogger(Logging.class.getName());
	
	/**
	 * Creates a console logger with a specified name.
	 * @param loggerName a name for the logger
	 * @return a new {@link Logger}
	 */
	public static final Logger getLogger(String loggerName) {
		Logger logger = Logger.getLogger(loggerName);
		return logger;
	}
	
	/**
	 * Attempts to create a {@link Logger} that logs to a specified file. If
	 * the file cannot be accessed for some reason, outputs a warning and
	 * returns a console logger instead.
	 * @param loggerName a name for the logger
	 * @param filename the path of the log file
	 * @return a new {@link Logger}
	 */
	public static final Logger
	getFileLogger(String loggerName, String filename) {
		Logger logger = Logger.getLogger(loggerName);
		try {
			FileHandler fileHandler = new FileHandler(filename, true);
			fileHandler.setFormatter(fmt);
			logger.addHandler(fileHandler);
		} catch (SecurityException | IOException e) {
			privLogger.warning("File " + filename
				+ " cannot be used. Logging to console instead...");
		}
		return logger;
	}
}

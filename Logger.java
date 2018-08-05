package udssr.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logger
 * 
 * This class allows us to log any kind of information to .html files and on the
 * console.
 * 
 * @author Max Gosau, Malte Lutterman
 *
 */
public final class Logger {

	/**
	 * This is the only instance of this class (Singleton Design Pattern)
	 */
	private static Logger loggerInstance = null;

	/**
	 * Path to the log files folder
	 */
	private final String LOG_FILE_PATH = "";

	/**
	 * Current file for logs
	 */
	private File logFile;

	/**
	 * Private constructor since the Singleton Design Pattern is being used (creates only
	 * one object of this class). Creates a new log file.
	 */
	private Logger() {
		createLogFile();
	}

	/**
	 * Getter for the only instance of this class (Singleton Design Pattern)
	 * 
	 * @return The instance of this class
	 */
	public static Logger getInstance() {

		// Create a new instance if we didn't create an object of this class before
		if (loggerInstance == null) {
			loggerInstance = new Logger();
		}
		return loggerInstance;
	}

	/**
	 * Creates a new log file, if we need a new one.
	 */
	private void createLogFile() {

		// Create a new file which is named by the current time
		logFile = new File(LOG_FILE_PATH + File.separator + "["
				+ new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(new Date()) + "].html");

		// Create folder if it is missing
		File logFilePath = new File(LOG_FILE_PATH);
		try {
			if (!logFilePath.exists()) {
				logFilePath.mkdirs();
			}
		}
		// Can't create folder? => Write to console, if console output is enabled
		catch (Exception e) {
			/* use this if you have a Settings Class
			// Console output enabled?
			if (Settings.getInstance().getConsoleLog()) {
				e.printStackTrace();
			}
			*/
			e.printStackTrace();
		}
	}

	/**
	 * This method logs messages in .html files and in the console, if enabled. It
	 * is possible to turn off one or both of those outputs.
	 * 
	 * @param msg
	 *            The message we want to log
	 * @param type
	 *            The type of the message we want to log (e.g. exception,
	 *            information, ...)
	 */
	public void log(String msg, MsgType type) {
		/* use this if you have a Settings Class
		// Log on console?
		if (Settings.getInstance().getConsoleLog()) {
			logToConsole(msg, type);
		}
		*/
		
		// Log on console
		logToConsole(msg, type);
		
		/* use this if you have a Settings Class
		// Log in file (.html)?
		if (Settings.getInstance().getFileLog()) {
			logToFile(msg, type);
		}
		*/
		
		// Log in file (.html)
		logToFile(msg, type);
	}

	/**
	 * This method logs exceptions. In order to do that, we take the exception,
	 * convert its stack trace to a String and then call our log method for Strings
	 * by passing the converted trace as a parameter to it.
	 * 
	 * @param e
	 *            The exception we want to log.
	 * @param type
	 *            The log type of the log entry.
	 */
	public void log(Exception e) {
		try {

			// Convert the stack trace of our exception to a string
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw, true);
			e.printStackTrace(pw);

			// Call log(String msg, logType type) with the converted trace
			log(sw.getBuffer().toString(), MsgType.EXCEPTION);

			// Close the writers
			sw.flush();
			sw.close();

			pw.flush();
			pw.close();
		}
		// IOException while logging? => Log it on the console, if console output is
		// enabled
		catch (IOException e1) {

			/* use this if you have a Settings Class
			// Console output enabled?
			if (Settings.getInstance().getConsoleLog()) {
				e.printStackTrace();
			}
			*/
			e.printStackTrace();
		}
	}

	/**
	 * Logs information to .html files.
	 * 
	 * @param msg
	 *            The message we want to log.
	 * @param type
	 *            The type of the message we want to log. We use this to choose
	 *            different colors for each type.
	 */
	private void logToFile(String msg, MsgType type) {

		// Current time of the log
		String currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

		// Use a different color for each log type
		String textColor = "";
		String htmlOutput = "<!DOCTYPE html>" + "<html>" + "<head>" + "<meta charset=\"UTF-8\">" + "</head>" + "<body>"
				+ "<p>" + currentTime + " [";

		// Try in case the type is invalid (we call type.name() and could get a
		// NullPointerException there)
		try {
			// Select a color
			textColor = colorDecode(type);

			// Save the HTML content
			htmlOutput += type.name() + "]:</p>" + "<p " + textColor + ">" + msg + "</p>" + "<hr>" + "</body>"
					+ "</html>";
		}
		// Invalid type? => empty brackets (print no type)
		catch (Exception e) {
			htmlOutput += "]:</p>" + "<p " + textColor + ">" + msg + "</p>" + "<hr>" + "</body>" + "</html>";
		}

		// Write the content to the log file
		try {
			FileWriter writer = new FileWriter(logFile, true);
			writer.write(htmlOutput);

			writer.flush();
			writer.close();
		}
		// IOException while writing? => Write to console, if console output is enabled
		catch (IOException e) {

			/* use this if you have a Settings Class
			// Console output enabled?
			if (Settings.getInstance().getConsoleLog()) {
				e.printStackTrace();
			}
			*/
			e.printStackTrace();
		}
	}

	/**
	 * Determines the correct color for a given message type.
	 * 
	 * @param type
	 *            The message type.
	 * @return The corresponding color to that message type.
	 */
	private String colorDecode(MsgType type) {
		switch (type) {
		case DEBUG:
			return "style=\"color:LimeGreen;\"";
		case INFO:
			return "style=\"color:DodgerBlue;\"";
		case EXCEPTION:
			return "style=\"color:Tomato;\"";
		case WARNING:
			return "style=\"color:Orange;\"";
		default:
			return "";
		// We don't need a default case here because the text color will be black if we
		// don't set anything else
		}
	}

	/**
	 * Logs information on the console, if enabled.
	 * 
	 * @param msg
	 *            The message we want to log.
	 * @param type
	 *            The type of the message we want to log.
	 */
	private void logToConsole(String msg, MsgType type) {

		// Current time of the log
		String currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

		// Try in case of an invalid type (we call type.name() which could lead to a
		// NullPointerException)
		try {
			String s = currentTime + " [" + type.name() + "]: " + System.getProperty("line.separator") + msg;
			if (type == MsgType.EXCEPTION) {
				System.err.println(s);
			} else {
				System.out.println(s);
			}
		}
		// Invalid type? => empty brackets (print no type)
		catch (Exception e) {
			System.out.println(currentTime + " []: " + System.getProperty("line.separator") + msg);
		}
	}
}

package udssr.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Settings
 * 
 * This class saves settings, especially relevant for
 * developers. This class allows to run the program in a test mode or to
 * activate/deactivate logs.
 *
 * Needs settings.properties File to work
 * 
 * @author Team Max Gosau
 *
 */
public final class Settings {

	/**
	 * This is the only instance of this class (Singleton Design Pattern)
	 */
	private static Settings settingsInstance = null;

	/**
	 * Specifies if console output should be enabled
	 */
	private boolean consoleLog;

	/**
	 * Specifies if file output should be enabled
	 */
	private boolean fileLog;

	/**
	 * The path to the settings file
	 */
	public static final String SETTINGS_PATH = "";

	/**
	 * Specifies if the program is run in test mode.
	 */
	private boolean testMode;

	/**
	 * Private constructor because the Singleton Design Pattern is being used (creates only
	 * one object of this class).
	 */
	private Settings() {
		try {

			// Read settings file to find out if we want to log, and if so, where
			FileReader reader = new FileReader(SETTINGS_PATH + File.separator + "settings.properties");
			Properties props = new Properties();
			props.load(reader);
			reader.close();

			// Initialize new settings from settings file here
			this.consoleLog = Boolean.parseBoolean(props.getProperty("consoleLog"));
			this.fileLog = Boolean.parseBoolean(props.getProperty("fileLog"));
			this.testMode = Boolean.parseBoolean(props.getProperty("testMode"));
		}
		// File not found? => Log to console if console output is enabled
		catch (FileNotFoundException e) {
			if (consoleLog) {
				Logger.getInstance().log(e);
			}
		}
		// IOException? => Log to console if console output is enabled
		catch (IOException e) {
			if (consoleLog) {
				Logger.getInstance().log(e);
			}
		}
	}

	/**
	 * Getter for the only instance of this class (Singleton Design Pattern)
	 * 
	 * @return The instance of this class
	 */
	public static Settings getInstance() {

		if (settingsInstance == null) {
			settingsInstance = new Settings();
		}
		return settingsInstance;
	}

	/**
	 * Getter for consoleLog
	 * 
	 * @return consoleLog Determines whether the game events, warnings and
	 *         exceptions should be written to the console
	 */
	public boolean getConsoleLog() {
		return consoleLog;
	}

	/**
	 * Getter for fileLog
	 * 
	 * @return fileLog Determines whether the game events, warnings and exceptions
	 *         should be written to a file
	 */
	public boolean getFileLog() {
		return fileLog;
	}

	/**
	 * Getter for the test mode attribute which specifies, if the program is run in
	 * test mode.
	 * 
	 * @return The test mode attribute
	 */
	public boolean isTestMode() {
		return testMode;
	}
}

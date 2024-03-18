package be.witmoca.YABA;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import be.witmoca.YABA.ui.YabaLauncher.LaunchDetails;
import be.witmoca.YABA.ui.YabaLauncher.YabaFileFilter;
import be.witmoca.YABA.ui.YabaLauncher.YabaLauncher;

public class Main {
	// Main application is only responsible for starting the application

	public static void main(String[] args) {
		// Set the look and feel
		try {
			UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
		} catch (Exception e) {
			// Ignore and run with the default L&F on error
		}
		
		// Extract arguments
		File argFile = extractFileFromArgs(args);
		LaunchDetails launchDetails;
		if(argFile != null) {
			// If file is in arguments, open the file
			launchDetails = new LaunchDetails(argFile, false, false);
		} else {
			// Run the launcher if no file has been given
			launchDetails = YabaLauncher.launchAndWaitForClose();
			// check if the application should be closed
			if(launchDetails.isCancelled())
				return;
		}
		



		try {
			// Init the application with the right file
			YabaApplication app = new YabaApplication(launchDetails.getFile(), launchDetails.isNewFile());
			// Start the application
			app.StartApplication();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	/**
	 * Searches for the first loadable database in the arguments
	 * 
	 * @param args the arguments passed to the application (usually the arguments of
	 *             the main method)
	 * @return the file to load, {@code null} if none was found
	 */
	private static File extractFileFromArgs(String[] args) {
		for (String s : args) {
			// Check if string is valid
			s = s.trim();
			if (s.isEmpty())
				continue;

			File test = new File(s);
			// check if the string denotes an existing file
			if (!test.exists() || !test.isFile())
				continue;

			// Check if the file filter accepts this as a valid file
			if ((new YabaFileFilter()).accept(test))
				return test;
		}
		return null;
	}
}

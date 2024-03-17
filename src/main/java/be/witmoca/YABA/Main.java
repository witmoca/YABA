package be.witmoca.YABA;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import be.witmoca.YABA.ui.YabaLauncher.LaunchDetails;
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

		// Run the launcher
		LaunchDetails launchDetails = YabaLauncher.launchAndWaitForClose();
		// check if the application should be closed
		if(launchDetails.isCancelled())
			return;


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
}

package be.witmoca.YABA;

import java.sql.SQLException;

import javax.swing.JOptionPane;

public class Main {
	// Main application is only responsible for starting the application

	public static void main(String[] args) {
		// Init the application
		YABAApplication app = new YABAApplication();

		// Start the application
		try {
			app.StartApplication();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
}

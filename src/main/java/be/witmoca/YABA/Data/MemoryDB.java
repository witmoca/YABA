/**
 *
 */
package be.witmoca.YABA.Data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.sqlite.SQLiteCommitListener;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.DateClass;
import org.sqlite.SQLiteConfig.DatePrecision;
import org.sqlite.SQLiteConnection;


/**
 *
 */
public class MemoryDB implements AutoCloseable{
	private static final int APPLICATION_ID = 0x59414241; //YABA in hex
	private static final String EMPTY_DB = "Databases/Default_1.yaba";

	private final SQLiteConnection db;

	public static MemoryDB createEmpty(File location) throws SQLException, IOException {
		// Copy the default DB to the new location
		// All bytes are read into memory, memory is written to file
		try(InputStream is = MemoryDB.class.getClassLoader().getResourceAsStream(EMPTY_DB)){
			byte[] buffer = is.readAllBytes();
			try (FileOutputStream fos = new FileOutputStream(location)){
				fos.write(buffer);
			}
		}

		return loadExisting(location);
	}

	public static MemoryDB loadExisting(File location) throws SQLException, IOException {
		if(!location.exists() || !location.isFile()) {
			throw new NoSuchFileException(location.getAbsolutePath(), null,"This is not an existing file");
		}

		if(!location.canRead() || !location.canWrite()) {
			throw new AccessDeniedException(location.getAbsolutePath(), null, "You do not have enough rights to access this file");
		}

		// open connection to sql file
		MemoryDB mdb = new MemoryDB(location);

		// check sanity
		mdb.checkDBSanity();

		// upgrade the DB to a new version if necessary (does nothing if version has not changed)
		mdb.upgradeDBVersion();

		return mdb;
	}

	private MemoryDB(File file) throws SQLException {
		/**
		 * List of Pragmas present in the default DB
		 * Application_ID = APPLICATION_ID
		 * Busy_Timeout = 3000 //default is 3s, put in fixed value in case of future changes in library
		 * encoding = UTF-8
		 * foreign_keys = 1 // enforce foreign keys
		 * read_uncommitted = 0 // Default, but fixed just in case
		 * user_version is set to the version of the db file later on. For version 1.0.0 the value was hardcoded (1000000)
		 * synchronous = 2 // Full sync mode
		 *
		 */

		// Define sqlite java driver specific configs (configs not actually part of the sqlite database)
		SQLiteConfig config = new SQLiteConfig();
		config.setDateClass(DateClass.INTEGER.getValue()); // Store dates in Unix Time (SQLITE support 64 bit formats when necessary)
		config.setDatePrecision(DatePrecision.SECONDS.getValue()); // Unix Time is in seconds

		// Create connection
		db = (SQLiteConnection) DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath(), config.toProperties());
		db.setAutoCommit(false);
		db.commit();
	}

	/**
	 * Does a sanity check before beginning
	 *
	 * @throws SQLException when DB is not sane or an error occurs
	 */
	private void checkDBSanity() throws SQLException {
		// Check application_id
		try (Statement appIdCheck = db.createStatement()) {
			// always returns a value! (0 by default)
			ResultSet appIdResult = appIdCheck.executeQuery("PRAGMA application_id");

			if (appIdResult.getInt(1) != APPLICATION_ID)
				throw new SQLException("File corrupt: Application ID incorrect. APP_ID: " + appIdResult.getInt(1));
		}

		// Check foreign keys
		try (Statement foreignKeyCheck = db.createStatement()) {
			ResultSet foreignKeyResult = foreignKeyCheck.executeQuery("PRAGMA foreign_key_check");
			String errorString = "";
			while (foreignKeyResult.next()) {
				for (int i = 1; i <= foreignKeyResult.getMetaData().getColumnCount(); i++) {
					errorString += foreignKeyResult.getString(i) + " ";
				}
				errorString += "\n";
			}
			if (!errorString.equals("")) {
				throw new SQLException("Foreign key constraints violated. Violations: \n" + errorString);
			}
		}

		// Check integrity
		try (Statement integrityCheck = db.createStatement()) {
			ResultSet integrityCheckResult = integrityCheck.executeQuery("PRAGMA integrity_check");
			if (!integrityCheckResult.next())
				new SQLException("Integrity check failed, no check results returned");

			String okString = integrityCheckResult.getString(1);
			// If returned value is not 'ok' -> Errors
			if (!okString.equalsIgnoreCase("ok")) {
				String errorString = okString + "\n";
				while (integrityCheckResult.next())
					errorString += integrityCheckResult.getString(1) + "\n";
				throw new SQLException("Integrity check failed. Violations: \n" + errorString);
			}
		}
	}

	/**
	 * Updates the file version whenever necessary
	 *
	 * @throws SQLException when DB cannot be upgraded, or an error occurs
	 */
	private void upgradeDBVersion() throws SQLException{
		// Check user_version
		try (Statement versionCheck = db.createStatement()) {
			// always returns a value! (0 as default)
			ResultSet appversionCheckIdResult = versionCheck.executeQuery("PRAGMA user_version");
			AppVersion fileVersion = new AppVersion(appversionCheckIdResult.getInt(1), "");
			if (AppVersion.getInternalAppVersion().compareTo(fileVersion) < 0) {
				// File is newer than app => update app
				throw new SQLException("The file you are trying to open cannot be opened using this software version, please update your software");
			} else if (fileVersion.getVERSION_MAJOR() < AppVersion.getInternalAppVersion().getVERSION_MAJOR()) {
				// Major version of file < major version of app => upgrade the file format
				switch(fileVersion.getVERSION_MAJOR()) {
					case 0:
						throw new SQLException("File corrupt: file version ID is lower than 1.0.0. File version: " + fileVersion.toString());
					case 1:
						// Upgrade from version 0.x.y to version 1.x.y
						// break
					case 2:
						// Upgrade from version 1.x.y to version 2.x.y
						// break
					default:
						// Major version unsupported
						throw new SQLException("File cannot be read. File version needs to be upgraded. Upgrade not supported. File version: " + fileVersion.toString());
				}
			}
		}

		// Always set the version of the file to the current (only allows strict forward compatibility)
		try(Statement versionUpdate = db.createStatement()){
			versionUpdate.executeUpdate("PRAGMA user_version = " + AppVersion.getInternalAppVersion().getAppVersionInt());
		}

		// Commit changes
		db.commit();
	}
	
	/**
	 * You are supposed to execute statements by submitting tasks
	 * Exposes  java.sql.Connection#prepareStatement(String)
	 * @see  java.sql.Connection#prepareStatement(String)
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return db.prepareStatement(sql);
	}
	
	/**
	 * Exposes java.sql.Connection#createStatement()
	 * @see  java.sql.Connection#createStatement()
	 */
	public Statement createStatement() throws SQLException {
		return db.createStatement();
	}
	
	/**
	 * Exposes java.sql.Connection#commit()
	 * @see java.sql.Connection#commit()
	 */
	public void commit() throws SQLException {
		db.commit();
	}
	
	public void close() throws SQLException {
		// Close the database
		db.close();
	}
	
	public void addCommitListener(SQLiteCommitListener lstnr) {
		db.addCommitListener(lstnr);
	}
}

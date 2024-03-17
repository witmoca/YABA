/**
 *
 */
package be.witmoca.YABA;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import be.witmoca.YABA.Data.MemoryDB;

/**
 *
 */
public class YabaApplication {
	private final MemoryDB memory;

	public YabaApplication(File file, boolean newFile) throws SQLException, IOException {
		memory = newFile ? MemoryDB.createEmpty(file) : MemoryDB.loadExisting(file);
	}

	public void StartApplication() throws SQLException, IOException {
		// dummy test
		try(Statement s = memory.createStatement()){
			try(ResultSet rs = s.executeQuery("SELECT name FROM sqlite_schema WHERE type='table' AND name NOT LIKE 'sqlite_%'")){
				while(rs.next()) {
					System.out.println(rs.getString(1));
				}
			}
		}
	}
}

/**
 *
 */
package be.witmoca.YABA;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import be.witmoca.YABA.Data.MemoryDB;
import be.witmoca.YABA.ui.YabaUI;

/**
 *
 */
public class YabaApplication {
	private final MemoryDB memory;

	public YabaApplication(File file, boolean newFile) throws SQLException, IOException {
		memory = newFile ? MemoryDB.createEmpty(file) : MemoryDB.loadExisting(file);
	}

	public void StartApplication() throws InterruptedException, ExecutionException {
		YabaUI.show(memory); // Does not return until the app closes
	}
}

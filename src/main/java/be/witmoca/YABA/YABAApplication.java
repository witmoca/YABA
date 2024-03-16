/**
 *
 */
package be.witmoca.YABA;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;

import javax.swing.filechooser.FileSystemView;

import be.witmoca.YABA.Data.MemoryDB;

/**
 *
 */
public class YABAApplication {
	private MemoryDB memory;

	public YABAApplication() {

	}

	public void StartApplication() throws SQLException, IOException {
		// TEST FILE
		File loadFile = new File(FileSystemView.getFileSystemView().getHomeDirectory().getAbsoluteFile() + File.separator + "test.yaba");
		if(!loadFile.exists()) {
			memory = MemoryDB.createEmpty(loadFile);
		} else {
			memory = MemoryDB.loadExisting(loadFile);
		}
	}
}

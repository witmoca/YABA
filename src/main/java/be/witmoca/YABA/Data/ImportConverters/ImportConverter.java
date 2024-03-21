package be.witmoca.YABA.Data.ImportConverters;

import java.io.File;
import javax.swing.filechooser.FileFilter;

import be.witmoca.YABA.Data.MemoryDB;

public abstract class ImportConverter extends FileFilter {
	
	/**
	 * 
	 * @param file File to import
	 * @param memory Database to import too
	 */
	public abstract void Import(File file, MemoryDB memory);


	@Override
	public String toString() {
		return this.getDescription();
	}

}

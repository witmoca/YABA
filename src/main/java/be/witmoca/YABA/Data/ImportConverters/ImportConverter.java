package be.witmoca.YABA.Data.ImportConverters;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileFilter;

import be.witmoca.YABA.Data.MemoryDB;

public abstract class ImportConverter extends FileFilter {
	
	/**
	 * 
	 * @param file File to import
	 * @param memory Database to import too
	 * @return number of Statements imported
	 * @throws IOException 
	 */
	public abstract int Import(File file, MemoryDB memory) throws IOException;


	@Override
	public String toString() {
		return this.getDescription();
	}

}

package be.witmoca.YABA.Data.ImportConverters;

import java.io.File;

import be.witmoca.YABA.Data.MemoryDB;

public class ArgentaCSV extends ImportConverter {

	public ArgentaCSV() {
		
	}

	@Override
	public void Import(File file, MemoryDB memory) {
		System.out.println("Importing file " + file.getAbsolutePath());
	}

	@Override
	public boolean accept(File pathname) {
		return pathname.getName().endsWith(".csv");
	}

	@Override
	public String getDescription() {
		return "Argenta (.CSV)";
	}
}

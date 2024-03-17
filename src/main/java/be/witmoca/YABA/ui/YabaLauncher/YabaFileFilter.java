package be.witmoca.YABA.ui.YabaLauncher;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class YabaFileFilter extends FileFilter {

	public YabaFileFilter() {
	}

	@Override
	public boolean accept(File f) {
		return (f.canRead() && f.canWrite() && f.getName().endsWith(".yaba")) || f.isDirectory();
	}

	@Override
	public String getDescription() {
		return "YABA File Format (.yaba)";
	}

}

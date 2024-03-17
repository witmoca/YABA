package be.witmoca.YABA.ui.YabaLauncher;

import java.io.File;

public class LaunchDetails {
	private final File file;
	private final boolean newFile;
	private final boolean cancelled;

	/**
	 *
	 * @param file the file to create or load
	 * @param newFile true if file needs to be created (new), false if existing (load)
	 * @param cancelled true if the dialog has been cancelled
	 */
	public LaunchDetails(File file, boolean newFile, boolean cancelled) {
		this.file = file;
		this.newFile = newFile;
		this.cancelled = cancelled;
	}

	public File getFile() {
		return file;
	}

	public boolean isNewFile() {
		return newFile;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public String toString() {
		return "LaunchDetails [file=" + file + ", newFile=" + newFile + ", cancelled=" + cancelled + "]";
	}
}

package be.witmoca.YABA.ui.YabaLauncher;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class YabaLauncher extends JFrame implements WindowListener {
	private static final long serialVersionUID = 1L;
	
	private Object windowClosedLock = new Object();
	private File returnValue = null;
	private boolean returnCancelled = false;
	private boolean returnNew = false;
	
	private YabaLauncher() {
		// Initialise frame
		super("YABA Launcher");
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setIconImages();
		this.addWindowListener(this);
		this.setLayout(new BorderLayout());

		// Components
		this.add(new BasicFileChooserPanel(this), BorderLayout.CENTER);
		this.add(new RecentFileSuggestionPanel(this), BorderLayout.SOUTH);

		// Draw frame
		this.setExtendedState(NORMAL);
		this.pack();
		this.setVisible(true);
	}
	
	public static LaunchDetails launchAndWaitForClose() {
			// Create the gui on the EDT (using a new task)
			FutureTask<YabaLauncher> createGui = new FutureTask<YabaLauncher>(new Callable<YabaLauncher>() {
				@Override
				public YabaLauncher call() throws Exception {
					return new YabaLauncher();
				}
			});
			// Schedule task on the EDT
			SwingUtilities.invokeLater(createGui);
			
			// Wait for the gui task to finish
			YabaLauncher launcherGui = null;
			try {
				launcherGui = createGui.get();
			} catch (InterruptedException | CancellationException | ExecutionException e) {
				// Report cancellation on error
				return new LaunchDetails(null, false, true);
			}
			
			// Report cancellation on gui creation fail
			if(launcherGui == null) {
				
				return new LaunchDetails(null, false, true);
			}
			
			// Wait for the lock that denotes the gui closing
			synchronized(launcherGui.windowClosedLock) {
				while(launcherGui.isVisible()) {
					try {
						launcherGui.windowClosedLock.wait();
					} catch (InterruptedException e) {
						// Ignore interrupts. Visible state of window is leading
					}
				}
			}
			
			return new LaunchDetails(launcherGui.returnValue, launcherGui.returnNew,launcherGui.returnCancelled);
	}
	
	protected void closeLauncher() {
		// This will only set the window invisible. The callable call() method is responsible for destroying the object
		synchronized(this.windowClosedLock) {
			this.setVisible(false);
			// Schedule for destruction
			this.dispose();
			// Let wait() know that the window is closing
			this.windowClosedLock.notifyAll();
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		this.setReturnCancelled(true);
		this.closeLauncher();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void setIconImages() {
		List<Image> icons = new ArrayList<Image>();
		/* TODO
		icons.add(UiIcon.LOGO_256.getIcon().getImage());
		icons.add(UiIcon.LOGO_128.getIcon().getImage());
		icons.add(UiIcon.LOGO_64.getIcon().getImage());
		icons.add(UiIcon.LOGO_48.getIcon().getImage());
		icons.add(UiIcon.LOGO_32.getIcon().getImage());
		icons.add(UiIcon.LOGO_16.getIcon().getImage());*/
		this.setIconImages(icons);
	}

	protected void setReturnValue(File returnValue) {
		this.returnValue = returnValue;
	}

	protected void setReturnCancelled(boolean returnCancelled) {
		this.returnCancelled = returnCancelled;
	}

	protected void setReturnNew(boolean returnNew) {
		this.returnNew = returnNew;
	}
}

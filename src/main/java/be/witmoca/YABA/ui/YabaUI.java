package be.witmoca.YABA.ui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import be.witmoca.YABA.Data.MemoryDB;

public class YabaUI extends JFrame implements WindowListener{
	private static final long serialVersionUID = 1L;
	
	private final MemoryDB memory;
	
	private Object windowClosedLock = new Object();

	private YabaUI(MemoryDB memory) {
		super("Yet Another Budget App");
		this.memory = memory;
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setIconImages();
		this.addWindowListener(this);
		this.setLayout(new BorderLayout());
		
		// Add components
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
		tabs.addTab("Import Statements", new ImporterPanel(memory));
		tabs.addTab("Statements", new StatementOverviewPanel(memory));
		tabs.addTab("Budget Categories", new BudgetCategoryPanel(memory));
		this.add(tabs, BorderLayout.CENTER);
		
		// Draw Frame
		this.setExtendedState(NORMAL);
		this.pack();
		this.setVisible(true);
	}
	
	public static YabaUI show(MemoryDB memory) throws InterruptedException, ExecutionException {
		// Create the gui on the EDT (using a new task)
		FutureTask<YabaUI> createGui = new FutureTask<YabaUI>(new Callable<YabaUI>() {
			@Override
			public YabaUI call() throws Exception {
				return new YabaUI(memory);
			}
		});
		// Schedule task on the EDT
		SwingUtilities.invokeLater(createGui);
		
		// Wait for the gui task to finish
		YabaUI ui = createGui.get();
		
		// Wait for the lock that denotes the gui closing
		synchronized(ui.windowClosedLock) {
			while(ui.isVisible()) {
				try {
					ui.windowClosedLock.wait();
				} catch (InterruptedException e) {
					// Ignore interrupts. Visible state of window is leading
				}
			}
		}
		return ui;
	}
	
	private void closeApplication() {
		synchronized(this.windowClosedLock) {
			// Try to commit & close
			try {
				memory.commit();
				memory.close();
			} catch (SQLException e) {
			}
			this.setVisible(false);
			this.dispose();
			this.windowClosedLock.notifyAll();		
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		this.closeApplication();
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
}

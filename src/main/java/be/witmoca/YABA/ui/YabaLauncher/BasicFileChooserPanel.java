package be.witmoca.YABA.ui.YabaLauncher;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class BasicFileChooserPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private final YabaLauncher parent;
	private final YabaFileFilter yff = new YabaFileFilter();

	public BasicFileChooserPanel(YabaLauncher parent) {
		super(new FlowLayout());
		this.parent = parent;

		this.add(new JButton(new NewFileAction("New", null)));
		this.add(new JButton(new LoadFileAction("Load", null)));
	}

	private class NewFileAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private NewFileAction(String text, Icon icon) {
			super(text, icon);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			final JFileChooser fc = new JFileChooser();
			fc.resetChoosableFileFilters();
			fc.setFileFilter(yff);
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setMultiSelectionEnabled(false);
			
			int result = fc.showSaveDialog(parent);
			if(result != JFileChooser.APPROVE_OPTION) {
				return;
			}
			File f = fc.getSelectedFile();
			
			// If filter is selected, make sure the file extension is correct
			if(fc.getFileFilter() == yff) {
				if(!f.getName().endsWith(".yaba")) {
					f = new File(f.getAbsolutePath() + ".yaba");
				}
			}
			
			// If the file exists, let the user confirm choice
			if(f.exists()) {
				int confirm = JOptionPane.showConfirmDialog(parent, "This file exists already. Do you wish to delete the existing file and create new one?");
				if (confirm != JOptionPane.YES_OPTION) {
					return;
				}
			}
			
			parent.setReturnCancelled(false);
			parent.setReturnNew(true);
			parent.setReturnValue(f);
			parent.closeLauncher();
		}
	}
	
	private class LoadFileAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private LoadFileAction(String text, Icon icon) {
			super(text, icon);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			final JFileChooser fc = new JFileChooser();
			fc.resetChoosableFileFilters();
			fc.setFileFilter(new YabaFileFilter());
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setMultiSelectionEnabled(false);
			
			int result = fc.showOpenDialog(parent);
			if(result != JFileChooser.APPROVE_OPTION) {
				return;
			}

			parent.setReturnCancelled(false);
			parent.setReturnNew(false);
			parent.setReturnValue(fc.getSelectedFile());
			parent.closeLauncher();
		}
	}
}





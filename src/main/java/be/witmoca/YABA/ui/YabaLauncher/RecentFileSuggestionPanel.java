package be.witmoca.YABA.ui.YabaLauncher;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class RecentFileSuggestionPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private final YabaLauncher parent;
	
	public RecentFileSuggestionPanel(YabaLauncher parent) {
		super(new FlowLayout());
		this.parent = parent;
		
		// TODO - Placeholders
		this.add(new JButton(new FileSuggestionAction(new File("Suggested File 1.yaba"), null)));
		this.add(new JButton(new FileSuggestionAction(new File("Suggested File 2.yaba"), null)));
		this.add(new JButton(new FileSuggestionAction(new File("Suggested File 3.yaba"), null)));
		this.add(new JButton(new FileSuggestionAction(new File("Suggested File 4.yaba"), null)));
	}
	
	private class FileSuggestionAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		private final File suggestion;

		private FileSuggestionAction(File suggestion, Icon icon) {
			super(suggestion.getName(), icon);
			this.suggestion = suggestion;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(!suggestion.exists()) {
				JOptionPane.showMessageDialog(parent, "This file does not exist anymore. Please choose another file.","File error", JOptionPane.WARNING_MESSAGE);
				return;
			}

			if(!suggestion.canRead() || !suggestion.canWrite()) {
				JOptionPane.showMessageDialog(parent, "You do not have enough rights to access this file. Please choose another file.","File error", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			parent.setReturnCancelled(false);
			parent.setReturnNew(false);
			parent.setReturnValue(suggestion);
			parent.closeLauncher();
		}
	}
}

package be.witmoca.YABA.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import be.witmoca.YABA.Data.MemoryDB;
import be.witmoca.YABA.Data.ImportConverters.ArgentaXLSX;
import be.witmoca.YABA.Data.ImportConverters.ImportConverter;

public class ImporterPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	// List of all the possible Import converters
	private final ImportConverter[] converterChoices = {new ArgentaXLSX()};
	private final JLabel fileToLoadLabel = new JLabel ("Select file to load");
	private final JButton importButton;
	private final MemoryDB memory;
	
	private File chosenFile;
	private ImportConverter chosenConverter;

	public ImporterPanel(MemoryDB memory) {
		super(new BorderLayout());
		this.memory = memory;
		
		// North panel
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		
		inputPanel.add(new JButton(new ChooseFileAction()));
		inputPanel.add(this.fileToLoadLabel);
		
		importButton = new JButton(new ImportAction());
		importButton.setEnabled(false);
		inputPanel.add(importButton);
		
		
		this.add(inputPanel, BorderLayout.NORTH);
	}
	
	private class ChooseFileAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		public ChooseFileAction() {
			super("Choose File to Load");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			final JFileChooser fc = new JFileChooser();
			fc.resetChoosableFileFilters();
			fc.setAcceptAllFileFilterUsed(false);
			
			for(FileFilter ic : converterChoices) {
				fc.addChoosableFileFilter(ic);
			}
			
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setMultiSelectionEnabled(false);
			
			if(fc.showOpenDialog(ImporterPanel.this) != JFileChooser.APPROVE_OPTION)
				return;
			
			
			File f = fc.getSelectedFile();
			
			if(f == null || !f.exists())
				return;
			
			chosenFile = f;
			chosenConverter = (ImportConverter) fc.getFileFilter();
			fileToLoadLabel.setText(f.getAbsolutePath());
			importButton.setEnabled(true);
		}
	}
	
	private class ImportAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ImportAction() {
			super("Import");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int count = 0;
			try {
				count = chosenConverter.Import(chosenFile, memory);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(ImporterPanel.this, e1.getMessage() + "", "Error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
			
			JOptionPane.showMessageDialog(ImporterPanel.this, "Import ok. Amount of lines imported: " + count);
		}
	}
}

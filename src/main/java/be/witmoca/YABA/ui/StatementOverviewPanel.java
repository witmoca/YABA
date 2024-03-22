package be.witmoca.YABA.ui;

import java.awt.BorderLayout;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.sqlite.SQLiteCommitListener;

import be.witmoca.YABA.Data.MemoryDB;
import be.witmoca.YABA.Data.TransactionStatement;

public class StatementOverviewPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final String[] TABLE_HEADERS = {"Date/Time","Amount","Description","Own Account","Counterparty Account"};

	public StatementOverviewPanel(MemoryDB memory) {
		super(new BorderLayout());
		this.add(new JScrollPane(new JTable(new StatementTableModel(memory))));
	}
	
	private class StatementTableModel implements TableModel, SQLiteCommitListener {
		private final MemoryDB memory;
		private final List<TableModelListener> tableListeners= new ArrayList<TableModelListener>();
		private final Object contentLock = new Object();
		
		private List<TransactionStatement> content = new ArrayList<>();
		
		private StatementTableModel(MemoryDB memory) {
			this.memory = memory;
			this.onCommit(); // fire a 'commit' to init the table
			memory.addCommitListener(this);
		}
		
		// SQLiteCommitListener interface (fires outside of EDT!)
		@Override
		public void onCommit() {
			// Fire the 'content has updated' event on the EDT
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					// Get the new content -> Important that this called 'later', not while commit is happen (select statements are not allowed while the event is fired)
					synchronized(contentLock) {
						StatementTableModel.this.content = TransactionStatement.getAll(memory);				
					}
					
					synchronized (tableListeners) {
						for(TableModelListener l : tableListeners)
							l.tableChanged(new TableModelEvent(StatementTableModel.this));
					}
				}
			});
		}

		@Override
		public void onRollback() {
			// Ignore
		}
		
		// TableModel interface
		@Override
		public int getRowCount() {
			synchronized(contentLock) {
				return content.size();
			}
		}
	
		@Override
		public int getColumnCount() {
			return TABLE_HEADERS.length;
		}
	
		@Override
		public String getColumnName(int columnIndex) {
			return TABLE_HEADERS[columnIndex];
		}
	
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch(columnIndex) {
				case 0: 
					return Date.class;
				case 1:
					return Integer.class;
				default: return String.class;
			}
		}
	
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
	
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			synchronized(contentLock) {
				return content.get(rowIndex).getDisplayValueAt(columnIndex);
			}
		}
	
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			// ignore
			return;
		}
	
		@Override
		public void addTableModelListener(TableModelListener l) {
			synchronized (this.tableListeners) {
				this.tableListeners.add(l);
			}
		}
	
		@Override
		public void removeTableModelListener(TableModelListener l) {
			synchronized (this.tableListeners) {
				this.tableListeners.remove(l);
			}
		}
	}
}

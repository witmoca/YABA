package be.witmoca.YABA.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TransactionStatement {
	private final long id;
	private final long timeStamp;
	private final int cents;
	private final String description;
	private final String originating_Acc;
	private final String receiving_Acc;
	
	

	
	public TransactionStatement(long id, long timeStamp, int cents, String description, String originating_Acc,	String receiving_Acc) {
		this.id = id;
		this.timeStamp = timeStamp;
		this.cents = cents;
		this.description = description;
		this.originating_Acc = originating_Acc;
		this.receiving_Acc = receiving_Acc;
	}


	public static List<TransactionStatement> getAll(MemoryDB memory){
		List<TransactionStatement> returnList = new ArrayList<>();
		try(Statement s = memory.createStatement()){
			try (ResultSet rs = s.executeQuery("SELECT Id, Timestamp, Cents, Description, Originating_Acc, Receiving_ACC FROM Transaction_History ORDER BY Timestamp Desc")){
				while(rs.next()) {
					returnList.add(new TransactionStatement(rs.getLong(1), rs.getLong(2),rs.getInt(3),rs.getString(4), rs.getString(5), rs.getString(6)));
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			// Returns empty list in case of error
		}
		return returnList;
	}

	
	public Object getValueAt(int index) {
		switch(index) {
			case 0:
				return this.getId();
			case 1:
				return this.getTimeStamp();
			case 2:
				return this.getCents();
			case 3:
				return this.getDescription();
			case 4:
				return this.getOriginating_Acc();
			case 5:
				return this.getReceiving_Acc();
			default:
				return null;
		}
	}

	public long getId() {
		return id;
	}


	public long getTimeStamp() {
		return timeStamp;
	}


	public int getCents() {
		return cents;
	}


	public String getDescription() {
		return description;
	}


	public String getOriginating_Acc() {
		return originating_Acc;
	}


	public String getReceiving_Acc() {
		return receiving_Acc;
	}
}

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
	private final String own_Acc;
	private final String counterParty_Acc;
	
	

	
	public TransactionStatement(long id, long timeStamp, int cents, String description, String own_Acc,	String counterParty_acc) {
		this.id = id;
		this.timeStamp = timeStamp;
		this.cents = cents;
		this.description = description;
		this.own_Acc = own_Acc;
		this.counterParty_Acc = counterParty_acc;
	}


	public static List<TransactionStatement> getAll(MemoryDB memory){
		List<TransactionStatement> returnList = new ArrayList<>();
		try(Statement s = memory.createStatement()){
			try (ResultSet rs = s.executeQuery("SELECT Id, Timestamp, Cents, Description, Own_Acc, CounterParty_Acc FROM Transaction_History ORDER BY Timestamp Desc")){
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

	
	public Object getDisplayValueAt(int index) {
		switch(index) {
			case 0:
				return this.getTimeStamp();
			case 1:
				return  this.getCents() / 100.0f;
			case 2:
				return this.getDescription();
			case 3:
				return this.getOwn_Acc();
			case 4:
				return this.getCounterParty_Acc();
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


	public String getOwn_Acc() {
		return own_Acc;
	}


	public String getCounterParty_Acc() {
		return counterParty_Acc;
	}
}

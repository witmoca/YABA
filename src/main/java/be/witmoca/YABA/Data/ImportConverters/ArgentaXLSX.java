package be.witmoca.YABA.Data.ImportConverters;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import be.witmoca.YABA.Data.MemoryDB;

public class ArgentaXLSX extends ImportConverter {

	public ArgentaXLSX() {
		
	}

	@Override
	public int Import(File file, MemoryDB memory) throws IOException{
		if(!file.exists() || !file.isFile() || !file.canRead())
			throw new IOException("File does not exist or cannot be read");
		
		int rowNr = 1;
		try( Workbook workbook = new XSSFWorkbook(file)){
			Sheet firstSheet = workbook.getSheetAt(0);
			
			Row headerRow = firstSheet.getRow(0);
			if(headerRow.getLastCellNum() < 11)
				return 0;
	
			
			try(PreparedStatement inSt = memory.prepareStatement("INSERT INTO Transaction_History (Timestamp, Cents, Description, Own_Acc, CounterParty_Acc) VALUES (?,?,?,?,?)")){
				for(; rowNr <= firstSheet.getLastRowNum(); rowNr++) {
					Row row = firstSheet.getRow(rowNr);
						
					inSt.setLong(1, row.getCell(7).getDateCellValue().getTime());
					
					// 1) x100 (put in 'cents')
					// 2) round to nearest long
					// 3)convert to int (or give exception if past max/min int)
					inSt.setInt(2, Math.toIntExact(Math.round(row.getCell(5).getNumericCellValue()*100)));
					
					String counterName = row.getCell(9).getRichStringCellValue().getString().trim(); 
					String descrIn = row.getCell(10).getRichStringCellValue().getString().trim();
					
					
					if(counterName.length() == 0 && descrIn.length() == 0) {
						inSt.setString(3, row.getCell(4).getRichStringCellValue().getString().trim()); // If no party or comment, set description of type of transfer as descr.
					} else if (counterName.length() > 0 && descrIn.length() == 0){
						inSt.setString(3, counterName);
					} else if (counterName.length() == 0 && descrIn.length() > 0){
						inSt.setString(3, descrIn);
					} else {
						inSt.setString(3, counterName + "(" + descrIn + ")");
					}
										
					
					inSt.setString(4, row.getCell(0).getRichStringCellValue().getString().trim());
					
					String counterParty = row.getCell(8).getRichStringCellValue().getString().trim(); // Get account nr of party
					if(counterParty.length() == 0) // If no account nr, get text name of counter party
						counterParty = row.getCell(9).getRichStringCellValue().getString().trim();
					inSt.setString(5, counterParty);
					inSt.executeUpdate();
				}
			} 
			memory.commit();
			
			return rowNr-1;
		} catch (InvalidFormatException | SQLException e) {
			throw new IOException(e);
		}
		
	}

	@Override
	public boolean accept(File pathname) {
		return pathname.getName().endsWith(".xlsx");
	}

	@Override
	public String getDescription() {
		return "Argenta (.xlsx)";
	}
}

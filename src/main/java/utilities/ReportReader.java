/**
 * Copyright (c) Douglas 2020.
 * All Rights Reserved.
 *
 * ver          Developer          Date        Comments
 * ----- ---------------------  ----------  ----------------------------------------
 * 1.00  Eng. Mohamed Moustafa  16/09/2020  - Script created.
 */
package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ReportReader {
	public String accountCode;
	public String FHI;

	static FileInputStream fis = null ; 
	public  FileInputStream getFileInputStream(String reportLocation)
	{
		String filePath = System.getProperty("user.dir") + reportLocation;
		File  srcFile  = new File(filePath);

		try {
			fis = new FileInputStream(srcFile);
		} catch (FileNotFoundException e) {
			System.out.println("Test data file not found. terminating process!! : Check file path of TestData");
			System.exit(0);
		}
		return fis ; 
	}
	
	public Object [][] getBalanceReportExcelData(String reportLocation) throws IOException
	{
		fis = getFileInputStream(reportLocation);
		HSSFWorkbook wb = new HSSFWorkbook(fis);
		HSSFSheet sheet = wb.getSheetAt(0);

		int TotalNumberOfRows = (sheet.getLastRowNum()+1);
		int TotalNumberOfCols = 11 ;

		String [] [] arrayExcelData = new String [TotalNumberOfRows][TotalNumberOfCols];

		HSSFRow row = sheet.getRow(8);
		accountCode = row.getCell(3).toString();
		FHI = row.getCell(4).toString();
				
		Reporter.Log("################################### Report Data From The Excel  ###################################");
		Reporter.Log("Participant =  " + row.getCell(1));
		Reporter.Log("Account code = " + row.getCell(3));
		Reporter.Log("FHI = " + row.getCell(4));
		Reporter.Log("Currency = " + row.getCell(5));
		Reporter.Log("Date = " + row.getCell(6));
		Reporter.Log("Opening balance = " + row.getCell(7));
		Reporter.Log("Sum of entries (debit) = " + row.getCell(8));
		Reporter.Log("Sum of entries (credit) = " + row.getCell(11));
		Reporter.Log("Closing balance = " + row.getCell(12));
		Reporter.Log("Num of entries (debit) = " + row.getCell(13));
		Reporter.Log("Num of entries (credit) = " + row.getCell(15));

		wb.close();
		return arrayExcelData;
	}
	
	public Object [][] getTransactionStatusRreportExcelData(String reportLocation) throws IOException
	{
		fis = getFileInputStream(reportLocation);
		HSSFWorkbook wb = new HSSFWorkbook(fis);
		HSSFSheet sheet = wb.getSheetAt(0);

		int TotalNumberOfRows = (sheet.getLastRowNum()+1);
		int TotalNumberOfCols = 11 ;

		String [] [] arrayExcelData = new String [TotalNumberOfRows][TotalNumberOfCols];

		HSSFRow row = sheet.getRow(8);

		Reporter.Log("################################### Report Data From The Excel  ###################################");
		Reporter.Log("FHI =  " + row.getCell(1));
		Reporter.Log("Country from = " + row.getCell(3));
		Reporter.Log("Country to = " + row.getCell(4));
		Reporter.Log("Currency = " + row.getCell(5));
		Reporter.Log("Transaction status = " + row.getCell(6));
		Reporter.Log("Number of transactions, having this status = " + row.getCell(7));

		wb.close();
		return arrayExcelData;
	}
}

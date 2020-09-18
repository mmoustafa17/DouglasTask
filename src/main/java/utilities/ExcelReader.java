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
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExcelReader {
	
	private Workbook workbook;
	private Sheet worksheet;
	private int rows;
	private String testCaseName;
	private int testCaseStartRow = 0;
	private int testCaseEndRow=0;
	private int usedColumnsCount = 0;
	private int iterationCount = 0;
	
	public ExcelReader(String workSheetName, String testCaseName) throws BiffException, IOException{
		String filePath = System.getProperty("user.dir") + "/src/test/resources/TestDataFiles/InputData.xls";
		File  srcFile  = new File(filePath);
		workbook = Workbook.getWorkbook(srcFile);
		 worksheet = workbook.getSheet(workSheetName);
		 this.testCaseName = testCaseName;
		 rows = getRowCount();
		 testCaseStartRow = getTestCaseStartRow();
		 testCaseEndRow = getTestCaseEndRow();
		 usedColumnsCount = getUsedColumnsCount();
		 iterationCount = getIterationCount();
	}
	

	private int getIterationCount(){
		try {
			for(int i =testCaseStartRow; i <= testCaseEndRow; i++){
				if(getCellData(usedColumnsCount,i).equalsIgnoreCase("Yes")){
					iterationCount++;
			}
		}
	} catch (Exception e) {
			e.printStackTrace();
		}
		if(iterationCount > 0){
			System.out.println("*************************************************************************************");
			System.out.println("Total number of iterations selected for test script: '"+testCaseName+"' is"+" "+iterationCount);	
			System.out.println("*************************************************************************************");
		}else{
			System.out.println("*************************************************************************************");
			System.out.println("Total number of iterations selected is 0. Please check execute column in TestData.xls file");
			System.out.println("*************************************************************************************");
		}
		
		return iterationCount;
	}
	
/*Return Two Dimensional Array to DataProvider*/	
	public Object[][] getTestdata(){
		int row = 0;
		int col = 0;
		String data[][] = new String[iterationCount][usedColumnsCount-1];

		//Get the Test Data
		for(int i =testCaseStartRow; i <= testCaseEndRow; i++){
			col = 0;
			boolean flag = false;
			if(getCellData(usedColumnsCount,i).equalsIgnoreCase("Yes")){
				flag = true;
				for(int j = 1; j < usedColumnsCount; j++){
					data[row][col] = getCellData(j, i);
					col++;
				}
			}
			if(flag){
				row++;
			}
		}
		return data;
	}
	
	/*Get Cell Data*/
	private String getCellData(int col, int row) {
		return worksheet.getCell(col, row).getContents();
	}
	
	/*Get TestCase Start Row*/
	private int getTestCaseStartRow(){
		try {
			for(int i = 0; i < rows; i++){
				if(worksheet.getCell(0,i).getContents().equals(testCaseName.trim())){
					testCaseStartRow = i;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return testCaseStartRow;
	}
	
	/*Get Test Case End Row*/
	private int getTestCaseEndRow(){
		try {
			for(int i = 0; i < rows; i++){
				if(worksheet.getCell(0,i).getContents().equals(testCaseName.trim())){
					testCaseEndRow = i;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return testCaseEndRow;
	}
	
	/*Get the Columns Count for the referenced test case*/
	private int getUsedColumnsCount(){
		try {
			int count = 0;
			while(!worksheet.getCell(count,testCaseStartRow-1).getContents().equalsIgnoreCase("Execute")){
					usedColumnsCount = count++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return usedColumnsCount+1;
	}
	
	/*Gets the total number of row count in the excel sheet*/
	private int getRowCount() {
		return worksheet.getRows();
	}

}

package com.cognizant.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cognizant.gui.SingleOrgLoginPage;
import com.cognizant.uicomponent.DisplayApexCodeCoverageDetails;
import com.cognizant.uicomponent.DisplayImpactedComponents;
import com.cognizant.uicomponent.DisplayMultiOrgMissingComponents;
import com.cognizant.uicomponent.DisplaySecurityHealthCheckDetails;
import com.cognizant.uicomponent.DisplaySecurityHealthCheckDetailsController;

import javafx.collections.ObservableList;

public final class ExcelUtils {
	private XSSFWorkbook _workbook = null;
	private int _rowPosition = 0;
	public FileInputStream fis = null;
	public String SFDC_Mapping_FilePath = System.getProperty("user.dir")+"\\target\\classes\\com\\cognizant\\sfdc_quality_analyzer_v3\\excel\\SFDCSobjectMappingSheet.xlsx";
	public String Excel_Report_Directory = System.getProperty("user.dir")+"\\Report\\Excel";
	public String Excel_FileName_Prefix = "\\ImpactAnalysisReport_";
	public String DownLoadResultExcel_FileName_Prefix = "\\ResultData_";
	public String SecurityHealthCheckExcel_FileName_Prefix = "\\SecurityHealthCheckReport_";
	public String ApexCodeCoverageExcel_FileName_Prefix = "\\ApexCodeCoverageReport_";
    private static final Logger log = Logger.getLogger(ExcelUtils.class); 
	//Map<String, HashMap<Integer, ArrayList<String>>> FoundComponentsFinalMap=new HashMap<String,HashMap<Integer,ArrayList<String>>>(); 
	// Opens Excel sheet
	private Sheet openSheet(String path) throws FileNotFoundException, IOException {
		_workbook = new XSSFWorkbook(new FileInputStream(new File(path)));
		return _workbook.getSheetAt(0);
	}

	// Creates Excel sheet
	public void createExcelSheet(String excelFile) throws IOException {
		_workbook = new XSSFWorkbook();
		XSSFSheet sheet = _workbook.createSheet("Summary");

		Row row = sheet.createRow(0);
		row.createCell(0).setCellValue("ORG_ID");
		row.createCell(1).setCellValue("OBJECTNAME");
		row.createCell(2).setCellValue("BUTTONS");
		row.createCell(3).setCellValue("FIELDS");
		row.createCell(4).setCellValue("RELATEDLISTS");
		closeWorkbook(excelFile);
	}

	// Close Excel Sheet
	public void closeWorkbook(String path) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(new File(path));
		_workbook.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}
	
	//Reading Excel Sheet
	public void readExcel(String path) throws IOException {
		Sheet sheet = openSheet(path);
		Iterator<Row> iterator = sheet.iterator();

        while (iterator.hasNext()) {

            Row currentRow = iterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();

            while (cellIterator.hasNext()) {

                Cell currentCell = cellIterator.next();
                //getCellTypeEnum shown as deprecated for version 3.15
                //getCellTypeEnum ill be renamed to getCellType starting from version 4.0
                if (currentCell.getCellTypeEnum() == CellType.STRING) {
                    System.out.print(currentCell.getStringCellValue() + "--");
                } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                    System.out.print(currentCell.getNumericCellValue() + "--");
                }

            }
            System.out.println();
        }
	}
	

	private String getFileName(String baseName) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String dateTimeInfo = dateFormat.format(new Date());
        baseName = baseName.concat(dateTimeInfo);
        return baseName.concat(String.format(".xlsx"));
    }
 
    public String export(String[] arrayColumnName, Map<String, HashMap<String,String>> ExcelComponentFinalMap) { 
    	String resultFilePath = null;
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Summary");
 
            writeHeaderLine(arrayColumnName, sheet);
 
            writeDataLines(ExcelComponentFinalMap, arrayColumnName, workbook, sheet);
 
            resultFilePath = getFileName(Excel_Report_Directory.concat(DownLoadResultExcel_FileName_Prefix));
        	System.out.println("Excel File Path :"+resultFilePath);

        	File fileDir = new File(Excel_Report_Directory);
        	if(!fileDir.exists()) {
        		fileDir.mkdirs();
        	}
            FileOutputStream outputStream = new FileOutputStream(resultFilePath);
            workbook.write(outputStream);
            workbook.close(); 
            
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileDir);
        } catch (IOException | SQLException e) {
            System.out.println("File IO error:");
            e.printStackTrace();
        }
        System.out.println("Excel Report Directory Path :"+Excel_Report_Directory);
        System.out.println("Excel File Path :"+resultFilePath);
		return resultFilePath;
    }
    
    public String exportExcel(String[] arrayColumnName, Map<String, HashMap<String,String>> ExcelComponentFinalMap) { 
    	String resultFilePath = null;
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Summary");
 
            writeHeaderLine(arrayColumnName, sheet);
 
            writeDataLinesExcel(ExcelComponentFinalMap, workbook, sheet);
 
            resultFilePath = getFileName(Excel_Report_Directory.concat(Excel_FileName_Prefix));
        	System.out.println("Excel File Path :"+resultFilePath);

        	File fileDir = new File(Excel_Report_Directory);
        	if(!fileDir.exists()) {
        		fileDir.mkdirs();
        	}
            FileOutputStream outputStream = new FileOutputStream(resultFilePath);
            workbook.write(outputStream);
            workbook.close(); 
            
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileDir);
        } catch (IOException | SQLException e) {
            System.out.println("File IO error:");
            e.printStackTrace();
        }
        System.out.println("Excel Report Directory Path :"+Excel_Report_Directory);
        System.out.println("Excel File Path :"+resultFilePath);
		return resultFilePath;
    }
    
    
    public String export(String[] arrayColumnName, HashMap<String, HashMap<String, ArrayList<String>>> ExcelComponentFinalMap) { 
    	String resultFilePath = null;
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Summary");
 
            writeHeaderLine(arrayColumnName, sheet);
 
            writeDataLines(ExcelComponentFinalMap, workbook, sheet);
 
            resultFilePath = getFileName(Excel_Report_Directory.concat(Excel_FileName_Prefix));
        	System.out.println("Excel File Path :"+resultFilePath);

        	File fileDir = new File(Excel_Report_Directory);
        	if(!fileDir.exists()) {
        		fileDir.mkdirs();
        	}
            FileOutputStream outputStream = new FileOutputStream(resultFilePath);
            workbook.write(outputStream);
            workbook.close(); 
            
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileDir);
        } catch (IOException | SQLException e) {
            System.out.println("File IO error:");
            e.printStackTrace();
        }
        System.out.println("Excel Report Directory Path :"+Excel_Report_Directory);
        System.out.println("Excel File Path :"+resultFilePath);
		return resultFilePath;
    }
    
    public String export(String[] arrayColumnName, ObservableList<DisplayMultiOrgMissingComponents> ExcelComponentFinalData) { 
    	String resultFilePath = null;
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Summary");
 
            writeHeaderLine(arrayColumnName, sheet);
 
            writeDataLines(ExcelComponentFinalData, arrayColumnName, workbook, sheet);
 
            resultFilePath = getFileName(Excel_Report_Directory.concat(Excel_FileName_Prefix));
        	System.out.println("Excel File Path :"+resultFilePath);

        	File fileDir = new File(Excel_Report_Directory);
        	if(!fileDir.exists()) {
        		fileDir.mkdirs();
        	}
            FileOutputStream outputStream = new FileOutputStream(resultFilePath);
            workbook.write(outputStream);
            workbook.close(); 
            
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileDir);
        } catch (IOException | SQLException e) {
            System.out.println("File IO error:");
            e.printStackTrace();
        }
        System.out.println("Excel Report Directory Path :"+Excel_Report_Directory);
        System.out.println("Excel File Path :"+resultFilePath);
		return resultFilePath;
    }
    
    public String exportSecurityHealthCheckReport(String[] arrayColumnName, ObservableList<DisplaySecurityHealthCheckDetails> ExcelSummaryFinalData) { 
    	String resultFilePath = null;
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Summary");
 
            writeHeaderLine(arrayColumnName, sheet);
 
            writeDataLinesInSecurityHealthCheckReport(ExcelSummaryFinalData, arrayColumnName, workbook, sheet);
 
            resultFilePath = getFileName(Excel_Report_Directory.concat(SecurityHealthCheckExcel_FileName_Prefix));
        	System.out.println("Excel File Path :"+resultFilePath);

        	File fileDir = new File(Excel_Report_Directory);
        	if(!fileDir.exists()) {
        		fileDir.mkdirs();
        	}
            FileOutputStream outputStream = new FileOutputStream(resultFilePath);
            workbook.write(outputStream);
            workbook.close(); 
            
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileDir);
        } catch (IOException | SQLException e) {
            System.out.println("File IO error:");
            e.printStackTrace();
        }
        System.out.println("Excel Report Directory Path :"+Excel_Report_Directory);
        System.out.println("Excel File Path :"+resultFilePath);
		return resultFilePath;
    }
    
    public String exportApexCodeCoverageReport(String[] arrayColumnName, ObservableList<DisplayApexCodeCoverageDetails> ExcelSummaryFinalData) { 
    	String resultFilePath = null;
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Summary");
 
            writeHeaderLine(arrayColumnName, sheet);
 
            writeDataLinesInApexCodeCoverageReport(ExcelSummaryFinalData, arrayColumnName, workbook, sheet);
 
            resultFilePath = getFileName(Excel_Report_Directory.concat(ApexCodeCoverageExcel_FileName_Prefix));
        	System.out.println("Excel File Path :"+resultFilePath);

        	File fileDir = new File(Excel_Report_Directory);
        	if(!fileDir.exists()) {
        		fileDir.mkdirs();
        	}
            FileOutputStream outputStream = new FileOutputStream(resultFilePath);
            workbook.write(outputStream);
            workbook.close(); 
            
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileDir);
        } catch (IOException | SQLException e) {
            System.out.println("File IO error:");
            e.printStackTrace();
        }
        System.out.println("Excel Report Directory Path :"+Excel_Report_Directory);
        System.out.println("Excel File Path :"+resultFilePath);
		return resultFilePath;
    }
    
    private void writeHeaderLine(String[] arrayColumnName, XSSFSheet sheet) throws SQLException {
        // write header line containing column names
        int numberOfColumns = arrayColumnName.length;
 
        Row headerRow = sheet.createRow(0);
 
        // exclude the first column which is the ID field
        for (int i = 0; i < numberOfColumns; i++) {
            String columnName = arrayColumnName[i];
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(columnName);
        }
    }
    
    private void writeDataLines(ResultSet result, XSSFWorkbook workbook, XSSFSheet sheet) throws SQLException {
        ResultSetMetaData metaData = result.getMetaData();
        int numberOfColumns = metaData.getColumnCount();
        int rowCount = 1;
 
        while (result.next()) {
            Row row = sheet.createRow(rowCount++);
 
            for (int i = 1; i <= numberOfColumns; i++) {
                Object valueObject = result.getObject(i);
                Cell cell = row.createCell(i - 1);
 
                if (valueObject instanceof Boolean) {
                    cell.setCellValue((Boolean) valueObject);
                } else if (valueObject instanceof Double) {
                    cell.setCellValue((double) valueObject);
                } else if (valueObject instanceof Float) {
                    cell.setCellValue((float) valueObject);
                } else {
                	cell.setCellValue((String) valueObject);
                }
            }
        }
    }
 
    private void writeDataLinesExcel(Map<String,HashMap<String,String>> FoundComponentsFinalMap, XSSFWorkbook workbook, XSSFSheet sheet) throws SQLException {
        int rowCount = 1;
        //ArrayList<String> objArr = FoundComponentsFinalList;
		System.out.println("FoundComponentsFinalMap.size() : "+FoundComponentsFinalMap.size());
		for (String i : FoundComponentsFinalMap.keySet()) {
		   Map<String, String> s = FoundComponentsFinalMap.get(i) ;
		      //System.out.println("key :" + i + " value: " + s);
		     System.out.println("s.size() : "+s.size());
		     for(String j : s.keySet()) {
		    	String k = s.get(j);
	    		String[] arrlastModifiedDetails = k.split("#SFDC#");
	    		String lastModifiedDate = arrlastModifiedDetails[1];
	    		String lastModifiedUserId = arrlastModifiedDetails[0];
		    	System.out.println("First key : " + i + " Second Key : " + j +" value : "+k);
		    	Row row = sheet.createRow(rowCount++);
		    	// Cell of the Component Type
	            row.createCell(0).setCellValue(i.toString());
	            // Cell of the Component Name
	            row.createCell(1).setCellValue(j.toString());
	            // Cell of the Last Modified By ID
	            row.createCell(2).setCellValue(lastModifiedUserId.toString());
	            // Cell of the Last Modified Date
	            row.createCell(3).setCellValue(lastModifiedDate.toString());
		     }
		}      
    }
    
    private void writeDataLines(HashMap<String, HashMap<String, ArrayList<String>>> FoundComponentsFinalMap, XSSFWorkbook workbook, XSSFSheet sheet) throws SQLException {
        int rowCount = 1;
        //ArrayList<String> objArr = FoundComponentsFinalList;
		System.out.println("FoundComponentsFinalMap.size() : "+FoundComponentsFinalMap.size());
		for (String i : FoundComponentsFinalMap.keySet()) {
		   Map<String, ArrayList<String>> s = FoundComponentsFinalMap.get(i) ;
		      //System.out.println("key :" + i + " value: " + s);
		     System.out.println("s.size() : "+s.size());
		     for(String j : s.keySet()) {
		    	ArrayList<String> k = s.get(j);
		    	System.out.println("First key : " + i + " Second Key : " + j +" value : "+k);
		    	Row row = sheet.createRow(rowCount++);
		    	// Cell of the Component Type
	            row.createCell(0).setCellValue(i.toString());
	            // Cell of the Component Name
	            row.createCell(1).setCellValue(j.toString());
	            // Cell of the Last Modified Date
	            row.createCell(2).setCellValue(k.toString());
		     }
		}      
    }
    
    private void writeDataLines(ObservableList<DisplayMultiOrgMissingComponents> masterMultiOrgMissingData, String[] arrayColumnName, XSSFWorkbook workbook, XSSFSheet sheet) throws SQLException {
        int rowCount = 1;
        //ArrayList<String> objArr = FoundComponentsFinalList;
		System.out.println("masterMultiOrgMissingData.size() : "+masterMultiOrgMissingData.size());
		/*String[] arrayColumnName = {"ComponentType", "SourceOrgId", "SourceOrgComponentNames", "TargetOrgId", "TargetOrgComponentNames", 
			"MissingComponents", "MatchingComponents"};*/
		for (int i = 0; i < masterMultiOrgMissingData.size() ; i++) {
		   DisplayMultiOrgMissingComponents dmomc_data = masterMultiOrgMissingData.get(i) ;
	       Row row = sheet.createRow(rowCount++);
	       for(int j = 0; j <arrayColumnName.length; j++) {
		    	if(arrayColumnName[j].equals("ComponentType")) {
		    		row.createCell(j).setCellValue(dmomc_data.getComponentType().toString());
		    	}else if(arrayColumnName[j].equals("SourceOrgId")) {
		    		row.createCell(j).setCellValue(dmomc_data.getSourceOrgId().toString());
		    	}else if(arrayColumnName[j].equals("SourceOrgComponentNames")) {
		    		row.createCell(j).setCellValue(dmomc_data.getSourceOrgComponentDetails().toString());
		    	}else if(arrayColumnName[j].equals("TargetOrgId")) {
		    		row.createCell(j).setCellValue(dmomc_data.getTargetOrgId().toString());
		    	}else if(arrayColumnName[j].equals("TargetOrgComponentNames")) {
		    		row.createCell(j).setCellValue(dmomc_data.getTargetOrgComponentDetails().toString());
		    	}else if(arrayColumnName[j].equals("MissingComponents")) {
		    		row.createCell(j).setCellValue(dmomc_data.getMissingComponentDetails().toString());
		    	}else if(arrayColumnName[j].equals("MatchingComponents")) {
		    		row.createCell(j).setCellValue(dmomc_data.getMatchingComponentDetails().toString());
		    	}
		    }
		}      
    }
    
    private void writeDataLinesInSecurityHealthCheckReport(ObservableList<DisplaySecurityHealthCheckDetails> SecurityHealthCheckSummaryData, String[] arrayColumnName, XSSFWorkbook workbook, XSSFSheet sheet) throws SQLException {
        int rowCount = 1;
        //ArrayList<String> objArr = FoundComponentsFinalList;
		System.out.println("SecurityHealthCheckSummaryData.size() : "+SecurityHealthCheckSummaryData.size());
		/*String[] arrayColumnName = {"RiskCategory", "Status", "Setting", "Group", "YourValue", "StandardValue"};	*/
		for (int i = 0; i < SecurityHealthCheckSummaryData.size() ; i++) {
		   DisplaySecurityHealthCheckDetails dshcd_data = SecurityHealthCheckSummaryData.get(i) ;
	       Row row = sheet.createRow(rowCount++);
	       for(int j = 0; j <arrayColumnName.length; j++) {
		    	if(arrayColumnName[j].equals("RiskCategory")) {
		    		row.createCell(j).setCellValue(dshcd_data.getRiskCategory().toString());
		    	}else if(arrayColumnName[j].equals("Status")) {
		    		row.createCell(j).setCellValue(dshcd_data.getStatus().toString());
		    	}else if(arrayColumnName[j].equals("Setting")) {
		    		row.createCell(j).setCellValue(dshcd_data.getSetting().toString());
		    	}else if(arrayColumnName[j].equals("Group")) {
		    		row.createCell(j).setCellValue(dshcd_data.getGroup().toString());
		    	}else if(arrayColumnName[j].equals("YourValue")) {
		    		row.createCell(j).setCellValue(dshcd_data.getYourValue().toString());
		    	}else if(arrayColumnName[j].equals("StandardValue")) {
		    		row.createCell(j).setCellValue(dshcd_data.getStandardValue().toString());
		    	}
		    }
		}      
    }
    
    private void writeDataLinesInApexCodeCoverageReport(ObservableList<DisplayApexCodeCoverageDetails> ApexCodeCoverageSummaryData, String[] arrayColumnName, XSSFWorkbook workbook, XSSFSheet sheet) throws SQLException {
        int rowCount = 1;
        //ArrayList<String> objArr = FoundComponentsFinalList;
		System.out.println("ApexCodeCoverageSummaryData.size() : "+ApexCodeCoverageSummaryData.size());
		/*String[] arrayColumnName = {"ApexTestClassId", "TestMethodName", "ApexClassOrTriggerId", "CodeCoveragePercentage", "NumLinesCovered", "NumLinesUncovered"};	*/
		for (int i = 0; i < ApexCodeCoverageSummaryData.size() ; i++) {
		   DisplayApexCodeCoverageDetails daccd_data = ApexCodeCoverageSummaryData.get(i) ;
	       Row row = sheet.createRow(rowCount++);
	       for(int j = 0; j <arrayColumnName.length; j++) {
		    	if(arrayColumnName[j].equals("ApexTestClassId")) {
		    		row.createCell(j).setCellValue(daccd_data.getApexTestClassId().toString());
		    	}else if(arrayColumnName[j].equals("TestMethodName")) {
		    		row.createCell(j).setCellValue(daccd_data.getTestMethodName().toString());
		    	}else if(arrayColumnName[j].equals("ApexClassOrTriggerId")) {
		    		row.createCell(j).setCellValue(daccd_data.getApexClassOrTriggerId().toString());
		    	}else if(arrayColumnName[j].equals("CodeCoveragePercentage")) {
		    		row.createCell(j).setCellValue(daccd_data.getCodeCoveragePercentage().toString());
		    	}else if(arrayColumnName[j].equals("NumLinesCovered")) {
		    		row.createCell(j).setCellValue(daccd_data.getNumLinesCovered().toString());
		    	}else if(arrayColumnName[j].equals("NumLinesUncovered")) {
		    		row.createCell(j).setCellValue(daccd_data.getNumLinesUncovered().toString());
		    	}
		    }
		}      
    }
    
    private void writeDataLines(Map<String,HashMap<String,String>> FoundComponentsFinalMap, String[] arrayColumnName, XSSFWorkbook workbook, XSSFSheet sheet) throws SQLException {
        int rowCount = 1;
        //ArrayList<String> objArr = FoundComponentsFinalList;
		System.out.println("FoundComponentsFinalMap.size() : "+FoundComponentsFinalMap.size());
		for (String i : FoundComponentsFinalMap.keySet()) {
		   Map<String, String> s = FoundComponentsFinalMap.get(i) ;
	       //System.out.println("key :" + i + " value: " + s);
	       System.out.println("s.size() : "+s.size());
	       Row row = sheet.createRow(rowCount++);
	       for(int j = 0; j < s.size(); j ++) {
		    	String k = s.get(arrayColumnName[j]);
		    	System.out.println("First key : " + i + " Second Key : " + j +" value : "+k);
		    	// Cell of the Component Type
	            row.createCell(j).setCellValue(k.toString());
	       }
		}      
    }
    
	public String getCellData(String columnName1, String columnName2, String searchString) throws IOException {
	  Cell second_column = null ;
	  Sheet sheet = openSheet(SFDC_Mapping_FilePath);
	  Row row = sheet.getRow(0);
	  // it will give you count of row which is used or filled
	  short lastcolumnused = row.getLastCellNum();

	  int colnum1 = 0;
	  for (int i = 0; i < lastcolumnused; i++) {
	   if (row.getCell(i).getStringCellValue().equalsIgnoreCase(columnName1)) {
	    colnum1 = i;
	    break;
	   }
	  }
	  
	  int colnum2 = 0;
	  for (int i = 0; i < lastcolumnused; i++) {
	   if (row.getCell(i).getStringCellValue().equalsIgnoreCase(columnName2)) {
	    colnum2 = i;
	    break;
	   }
	  }

	  int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
	  //Create a loop over all the rows of excel file to read it
	  for (int i = 0; i < rowCount+1; i++) {
        Row finalRow = sheet.getRow(i);
        Cell first_column = finalRow.getCell(colnum1);
            //Print Excel data in console
        String CellValue = first_column.getStringCellValue();
        	if(CellValue.contentEquals(searchString)) {
        		second_column = finalRow.getCell(colnum2);
        		log.info(columnName2+" column value is "+second_column.getStringCellValue());
    		    break;
	        }
	    }
	  //returning the final value
	  return second_column.getStringCellValue();
    }
	
	public boolean getCellData(String columnName, String searchString) throws IOException {
		  Sheet sheet = openSheet(SFDC_Mapping_FilePath);
		  Row row = sheet.getRow(0);
		  boolean foundcolumn = false;
		  // it will give you count of row which is used or filled
		  short lastcolumnused = row.getLastCellNum();

		  int colnum = 0;
		  for (int i = 0; i < lastcolumnused; i++) {
		   if (row.getCell(i).getStringCellValue().equalsIgnoreCase(columnName)) {
		    colnum = i;
		    break;
		   }
		  }
		  

		  int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
		  //Create a loop over all the rows of excel file to read it
		  for (int i = 0; i < rowCount+1; i++) {
	        Row finalRow = sheet.getRow(i);
	        Cell first_column = finalRow.getCell(colnum);
	            //Print Excel data in console
	        String CellValue = first_column.getStringCellValue();
	        	if(CellValue.contentEquals(searchString)) {
	        		foundcolumn = true;
	    		    break;
		        }else {
		        	foundcolumn = false;
		        }
		    }
		  //returning the final value
		  return foundcolumn;
	    }
	
	// Updating matching test case occurrences if any in excel sheet
		public synchronized void writeExcel(String path, int pos, String orgID, Map<String, List<Map<String, List<String>>>> RESULT_MAP)
				throws FileNotFoundException, IOException {
			Sheet sheet = openSheet(path);
			for(Map.Entry<String, List<Map<String, List<String>>>> map : RESULT_MAP.entrySet()) {
				Row row = sheet.createRow(++_rowPosition);
				for(int i = 0; i < 5; i++) {
					row.createCell(i).setCellValue("NA");
				}
				row.getCell(0).setCellValue(orgID);
				row.getCell(1).setCellValue(map.getKey());
				List<Map<String, List<String>>> resultList = map.getValue();
				for(Map<String, List<String>> list : resultList) {
					for(Map.Entry<String, List<String>> map1 : list.entrySet()) {
						if(map1.getKey().equals("button")) {
							Cell cell = row.getCell(2);
							if(!cell.getStringCellValue().equalsIgnoreCase("NA")) {
								String value = cell.getStringCellValue();
								cell.setCellValue(value + "," + map1.getValue().toString());
							} else {
								cell.setCellValue(map1.getValue().toString());							
							}
						} else if(map1.getKey().equals("field")) {
							Cell cell = row.getCell(3);
							if(!cell.getStringCellValue().equalsIgnoreCase("NA")) {
								String value = cell.getStringCellValue();
								cell.setCellValue(value + "," + map1.getValue().toString());
							} else {
								cell.setCellValue(map1.getValue().toString());							
							}
						} else if(map1.getKey().equals("relatedList")) {
							Cell cell = row.getCell(4);
							if(!cell.getStringCellValue().equalsIgnoreCase("NA")) {
								String value = cell.getStringCellValue();
								cell.setCellValue(value + "," + map1.getValue().toString());
							} else {
								cell.setCellValue(map1.getValue().toString());							
							}
						}
					}
				}
			}
		}
}

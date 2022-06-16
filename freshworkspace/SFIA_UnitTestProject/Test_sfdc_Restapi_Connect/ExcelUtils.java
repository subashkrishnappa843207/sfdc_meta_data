package Test_sfdc_Restapi_Connect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class ExcelUtils {
	private static XSSFWorkbook _workbook;
	private int _rowPosition = 0;
	
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

}

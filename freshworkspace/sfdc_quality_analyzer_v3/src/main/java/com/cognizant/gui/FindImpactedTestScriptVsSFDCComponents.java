package com.cognizant.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.cognizant.encryption.EncryptionUtil;
import com.cognizant.sfdc_quality_analyzer_v3.App;
import com.cognizant.uicomponent.DisplayImpactedComponents;
import com.cognizant.uicomponent.DisplayImpactedComponentsController;
import com.cognizant.uicomponent.DisplayImpactedTestScripts;
import com.cognizant.uicomponent.DisplayImpactedTestScriptsController;
import com.cognizant.utility.Config;
import com.cognizant.utility.ExcelUtils;
import com.cognizant.utility.JsonUtils;
import com.cognizant.utility.XMLUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FindImpactedTestScriptVsSFDCComponents implements Initializable {
	private static final Logger log = Logger.getLogger(FindImpactedTestScriptVsSFDCComponents.class); 
	ExcelUtils excelUtil = new ExcelUtils();
	JsonUtils jsonUtil = new JsonUtils();
	XMLUtils xmlUtil = new XMLUtils();
	HashMap<String, HashMap<String, ArrayList<String>>> FinalImpactedTestCasesMap = new HashMap<String, HashMap<String, ArrayList<String>>>();
    private ObservableList<String> cbFileFormatList = FXCollections.observableArrayList("EXCEL", "JSON");
    
	@FXML Button FindImpactedComponentsButton;
	@FXML Button ViewMoreDetailsButton;
	@FXML ComboBox SelectReportingFileFormat;
	@FXML Button generateImpactAnalysisReportButton;
	@FXML TextField GeneratedReportPath;
	@FXML TextField TestCaseMappingFilePath;
	@FXML TextField ImpactedSalesforceComponentsJSONFilePath;

	@FXML ImageView RecordFoundSmiley;
	@FXML Label RecordCountPrefix;
	@FXML Label RecordCount;
	@FXML Label RecordCountSuffix;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
    	SelectReportingFileFormat.setItems(cbFileFormatList);
	}
	
    @FXML private void ViewMoreDetails() throws IOException {
    	log.info("Placeholder method for View Details about impacted Test Scripts");
    	if(DisplayImpactedTestScriptsController.masterImpactedTestScriptsData.size() > 0) {
    		DisplayImpactedTestScriptsController.masterImpactedTestScriptsData.clear();
    	}
    	log.info("FoundComponentsFinalMap.size() : "+FinalImpactedTestCasesMap.size());
		for (String i : FinalImpactedTestCasesMap.keySet()) {
		   Map<String, ArrayList<String>> s = FinalImpactedTestCasesMap.get(i) ;
		      //System.out.println("key :" + i + " value: " + s);
		   log.info("s.size() : "+s.size());
		     for(String j : s.keySet()) {
		    	ArrayList<String> k = s.get(j);
		    	ObservableList<String> oListValue = FXCollections.observableArrayList(k);
		    	log.info("First key : " + i + " Second Key : " + j +" value : "+oListValue);
		    	DisplayImpactedTestScriptsController.masterImpactedTestScriptsData.add(new DisplayImpactedTestScripts(i, j, oListValue));
		     }
		} 
		log.info("************************************");
		//System.out.println(DisplayImpactedTestScriptsController.masterImpactedTestScriptsData);
		DisplayImpactedTestScriptsController sfdc_ditsc = new DisplayImpactedTestScriptsController();
    	Stage primaryStage = new Stage();
    	sfdc_ditsc.start(primaryStage);
    }
    
    @FXML private void FindImpactedTestScripts() throws IOException {
    	log.info("Placeholder method to Find Impacted Test Scripts");
        File fileHappySmileyPath = new File(System.getProperty("user.dir")+"\\target\\classes\\com\\cognizant\\sfdc_quality_analyzer_v3\\images\\icons8-happy-cloud.png");
        File fileSadSmileyPath = new File(System.getProperty("user.dir")+"\\target\\classes\\com\\cognizant\\sfdc_quality_analyzer_v3\\images\\icons8-sad-cloud.png");
        Image HappySmileyImage = new Image(fileHappySmileyPath.toURI().toString());
        Image SadSmileyImage = new Image(fileSadSmileyPath.toURI().toString());
        
       	if((TestCaseMappingFilePath.getText() == null) || (TestCaseMappingFilePath.getText() == "") 
       			|| (TestCaseMappingFilePath.getText().isEmpty())) {
    		Alert AutomationTestScriptErrorAlert = new Alert(AlertType.ERROR);
    		AutomationTestScriptErrorAlert.setHeaderText("Automation TestScript Mapping File Import error");
    		AutomationTestScriptErrorAlert.setContentText("Please Import Automation TestScript Mapping File before proceed!!!");
    		AutomationTestScriptErrorAlert.showAndWait();
       	}else if((ImpactedSalesforceComponentsJSONFilePath.getText() == null) || (ImpactedSalesforceComponentsJSONFilePath.getText() == "") 
       			|| (ImpactedSalesforceComponentsJSONFilePath.getText().isEmpty())) {
    		Alert ImpactedSFDCJSONFileErrorAlert = new Alert(AlertType.ERROR);
    		ImpactedSFDCJSONFileErrorAlert.setHeaderText("Impacted Salesforce Component File Import error");
    		ImpactedSFDCJSONFileErrorAlert.setContentText("Please Import Impacted Salesforce Component File before proceed!!!");
    		ImpactedSFDCJSONFileErrorAlert.showAndWait();
       	}else {
       		log.info("Placeholder method to Find Impacted Test Scripts");
        	HashMap<String, HashMap<String, ArrayList<String>>> ReturnedTestCaseMappedComponents = GetTestCaseMappingJSONData();
        	HashMap<String, ArrayList<String>> ReturnedImpactedSFDCComponents = GetImpactedSFDCComponentsJSONData();
        	log.info("Size of Impacted Component Map : "+ReturnedImpactedSFDCComponents.size());
        	for(String returnComponentType : ReturnedImpactedSFDCComponents.keySet()) {
        		log.info("Impacted Component Type : "+returnComponentType);
        		HashMap<String, ArrayList<String>> FinalImpactedSFDCComponentsMap = new HashMap<String, ArrayList<String>>();
        		ArrayList<String> returnImpactedComponentList = ReturnedImpactedSFDCComponents.get(returnComponentType);
				for (String returnTestCaseName : ReturnedTestCaseMappedComponents.keySet()) {
					log.info("Return Test Case Name : "+returnTestCaseName);
				    Map<String, ArrayList<String>> returnMappedTestCaseMap = ReturnedTestCaseMappedComponents.get(returnTestCaseName) ;
			    	if(returnMappedTestCaseMap.get(returnComponentType) != null) {
			    		ArrayList<String> returnTCSFDCComponentNameList = returnMappedTestCaseMap.get(returnComponentType);
			    		log.info("First key : " + returnTestCaseName + " Second Key : " + returnComponentType +" SFDCTestCaseMappedvalue : "+returnTCSFDCComponentNameList);
			    		returnTCSFDCComponentNameList.retainAll(returnImpactedComponentList);
			    		if(returnTCSFDCComponentNameList.size() > 0) {
			    			log.info("*******************************************************************************"); 
			    			log.info(returnTCSFDCComponentNameList);
			    			log.info("*******************************************************************************"); 
				    		FinalImpactedSFDCComponentsMap.put(returnTestCaseName, returnTCSFDCComponentNameList);
			    		}
			    	}else {
			    		log.info("First key : " + returnTestCaseName + " Second Key : " + returnComponentType +" SFDCTestCaseMappedvalue : NO RESULT FOUND");
			    	}
				}
				FinalImpactedTestCasesMap.put(returnComponentType, FinalImpactedSFDCComponentsMap);
        	}
        	
        	log.info("************Final Impacted TestCase v/s SFDC Component List********************"); 
    	    int totalCount = 0;
    		for (String i : FinalImpactedTestCasesMap.keySet()) {
 			   Map<String, ArrayList<String>> s = FinalImpactedTestCasesMap.get(i) ;
 			      //System.out.println("key :" + i + " value: " + s);
 			     log.info("s.size() : "+s.size());
 			     if(s.size() > 0) {
	 			     for(String j : s.keySet()) {
	 			    	totalCount = totalCount + 1;
	 			    	ArrayList<String> k = s.get(j);
	 			    	log.info("First key : " + i + " Second Key : " + j +" value : "+k);
	 			     }
 			     }
 			 } 
    		log.info("*******************************************************************************");
    		if(totalCount > 0) {
    			RecordFoundSmiley.setImage(HappySmileyImage);
    			RecordCountPrefix.setText("Total");
    			RecordCountPrefix.setTextFill(Color.WHITE);
    			RecordCountPrefix.setStyle("-fx-font: 18 arial;-fx-font-weight: bold;");
    			RecordCountPrefix.setWrapText(true);
    			RecordCountPrefix.setTextAlignment(TextAlignment.CENTER);
    			RecordCount.setText(String.valueOf(totalCount));
    			RecordCount.setTextFill(Color.WHITE);
    			RecordCount.setStyle("-fx-font: 48 arial;-fx-font-weight: bold;");
    			RecordCount.setWrapText(true);
    			RecordCount.setTextAlignment(TextAlignment.CENTER);
    			RecordCountSuffix.setText("Records Found");
    			RecordCountSuffix.setTextFill(Color.WHITE);
    			RecordCountSuffix.setStyle("-fx-font: 18 arial;-fx-font-weight: bold;");
    			RecordCountSuffix.setWrapText(true);
    			RecordCountSuffix.setTextAlignment(TextAlignment.CENTER);
    		}else {
    			RecordFoundSmiley.setImage(SadSmileyImage);
    			RecordCountPrefix.setText("Total");
    			RecordCountPrefix.setTextFill(Color.WHITE);
    			RecordCountPrefix.setWrapText(true);
    			RecordCountPrefix.setTextAlignment(TextAlignment.CENTER);
    			RecordCountPrefix.setStyle("-fx-font: 18 arial;-fx-font-weight: bold;");
    			RecordCount.setText(String.valueOf(totalCount));
    			RecordCount.setTextFill(Color.WHITE);
    			RecordCount.setWrapText(true);
    			RecordCount.setTextAlignment(TextAlignment.CENTER);
    			RecordCount.setStyle("-fx-font: 48 arial;-fx-font-weight: bold;");
    			RecordCountSuffix.setText("Records Found");
    			RecordCountSuffix.setTextFill(Color.WHITE);
    			RecordCountSuffix.setStyle("-fx-font: 18 arial;-fx-font-weight: bold;");
    			RecordCountSuffix.setWrapText(true);
    			RecordCountSuffix.setTextAlignment(TextAlignment.CENTER);
    		}
       	}
    }
    
    @FXML private void ImportSalesforceJSONFile() throws IOException {
    	log.info("Implement code to Choose Impacted Salesforce Component File!!!");
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Impacted Salesforce Component file");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")+"\\Report\\JSON"));
		File selectedFile = fileChooser.showOpenDialog(null);
		try {
			if(selectedFile.getAbsolutePath() != null) {
				ImpactedSalesforceComponentsJSONFilePath.setText(selectedFile.getAbsolutePath());
			} else  {
				log.info("You haven't selected any Impacted Salesforce Component JSON file yet!!!");
		    }
		}catch(Exception ex) {
			log.info("You haven't selected any Impacted Salesforce Component JSON file yet!!!");
		}
    }
    
    @FXML private void ImportTestCaseMappingJSONFile() throws IOException {
    	log.info("Implement code to Choose Automation Test Script Mapping File!!!");
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Automation Test Script Mapping file");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")+"\\Mapping\\JSON\\Target"));
		File selectedFile = fileChooser.showOpenDialog(null);
		try {
			if(selectedFile.getAbsolutePath() != null) {
				TestCaseMappingFilePath.setText(selectedFile.getAbsolutePath());
			} else  {
				log.info("You haven't selected any Automation Test Script Mapping JSON file yet!!!");
		    }
		}catch(Exception ex) {
			log.info("You haven't selected any Automation Test Script Mapping JSON file yet!!!");
			log.error(ex.getMessage());
		}
    }
    
    @FXML private void generateImpactAnalysisReport() throws IOException {
    	log.info("Placeholder method for Generating Impact Analysis report based on given File Format");
    	String fileFormat = (String) SelectReportingFileFormat.getSelectionModel().getSelectedItem();
    	String[] arrayColumnName = {"ComponentType", "TestCaseName", "ComponentNames"};	
    	if(fileFormat.equalsIgnoreCase("excel")) {
    		log.info("EXCEL Selected");
			String reportFilePath = excelUtil.export(arrayColumnName, FinalImpactedTestCasesMap);
			GeneratedReportPath.setText(reportFilePath);
    	}else if(fileFormat.equalsIgnoreCase("json")) {
    		log.info("JSON Selected");
			String reportFilePath = jsonUtil.export(arrayColumnName, FinalImpactedTestCasesMap);
			GeneratedReportPath.setText(reportFilePath);
    	}
    }
    
    @FXML private void switchToTestAutomationHomePage() throws IOException {
    	log.info("Moving to Selenium Test Automation Home Page");
    	App.setRoot("SeleniumAutomationHomePage");
    }
    
    @FXML private void switchBackToPreviousPage() throws IOException {
    	log.info("Moving back to SFDC Test Impact Analysis Page");
        App.setRoot("SFDCTestImpactAnalysisPage");
    }
	
	@FXML private void switchBackToHomePage() throws IOException {
		log.info("Moving Back to Home Page");
        App.setRoot("AppHomePage");
    }
    
    @FXML private void signOut() throws IOException {
    	App.setRoot("AppLauncherPage");
        //Platform.exit();
        //System.exit(0);
    }
    
    private HashMap<String, HashMap<String, ArrayList<String>>> GetTestCaseMappingJSONData() {
    	HashMap<String, HashMap<String, ArrayList<String>>> SelectedTestCaseMappedComponents = new HashMap<String, HashMap<String, ArrayList<String>>>();

    	log.info("TestCaseMappingFilePath : " + TestCaseMappingFilePath.getText());
    	try {
	    	FileReader readerTestCaseMappingFile = new FileReader(TestCaseMappingFilePath.getText());
	    	JSONParser jsonParser = new JSONParser();
			//Read TestCase Mapping JSON file
			Object objMappedTestCases = jsonParser.parse(readerTestCaseMappingFile);
			JSONObject TestCasesMappingDetail = (JSONObject) objMappedTestCases;
			//System.out.println(TestCasesMappingDetail);
			JSONArray FinalMappingDetailsArray = (JSONArray) TestCasesMappingDetail.get("FinalMappingDetails");
			for(int i = 0; i < FinalMappingDetailsArray.size() ; i ++) {
				JSONObject IndividualTestCaseDetail = (JSONObject) FinalMappingDetailsArray.get(i);
	    		String TestCaseName = (String) IndividualTestCaseDetail.get("TestCaseName");
	    		JSONArray TotalRecords = (JSONArray) IndividualTestCaseDetail.get("records");
	    		log.info("Test Case Name = "+TestCaseName); 
	    		log.info("************************************************************"); 
	        	HashMap<String, ArrayList<String>> MappedComponentValues = new HashMap<String, ArrayList<String>>();
	    		for(int j = 0; j < TotalRecords.size() ; j ++) {
	        		JSONObject MappedComponentDetail = (JSONObject) TotalRecords.get(j);
	        		String ComponentType = (String) MappedComponentDetail.get("ComponentType");
	        		JSONArray ComponentNames = (JSONArray) MappedComponentDetail.get("ComponentName");
	        		log.info("Component Type : "+ComponentType); 
	        		log.info("Component Names : "+ComponentNames); 
	        		ArrayList<String> observableComponentList = new ArrayList<String>();
	        		for (int l=0;l<ComponentNames.size();l++){ 
	        			  observableComponentList.add(ComponentNames.get(l).toString());
	        		} 
	        		MappedComponentValues.put(ComponentType, observableComponentList);
	    		}
	    		SelectedTestCaseMappedComponents.put(TestCaseName, MappedComponentValues); 
			}
			log.info("*******************************************************************"); 
			log.info(SelectedTestCaseMappedComponents);
			log.info("*******************************************************************");
		    readerTestCaseMappingFile.close();
    	} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
		return SelectedTestCaseMappedComponents; 
    }
    
    private HashMap<String, ArrayList<String>> GetImpactedSFDCComponentsJSONData() {
    	
    	HashMap<String, ArrayList<String>> ImpactedComponentsMap = new HashMap<String, ArrayList<String>>();
    	try {
    		FileReader readerImpactedSalesforceComponentsJSONFile = new FileReader(ImpactedSalesforceComponentsJSONFilePath.getText());
    		JSONParser jsonParser = new JSONParser();
    		log.info("ImpactedSalesforceComponentsJSONFilePath : " + ImpactedSalesforceComponentsJSONFilePath.getText());
    		//Read Salesforce Impacted Components JSON file
    		Object objImpactSFDCComponents = jsonParser.parse(readerImpactedSalesforceComponentsJSONFile);
    		JSONObject ImpactSFDCComponentsDetail = (JSONObject) objImpactSFDCComponents;
    		//System.out.println(ImpactSFDCComponentsDetail);
    		JSONArray FinalImpactedComponentDetailsArray = (JSONArray) ImpactSFDCComponentsDetail.get("FinalImpactDetails");
    		for(int i = 0; i < FinalImpactedComponentDetailsArray.size() ; i ++) {
    			JSONObject IndividualComponentDetail = (JSONObject) FinalImpactedComponentDetailsArray.get(i);
        		String ComponentType = (String) IndividualComponentDetail.get("ComponentType");
        		JSONArray ChangedComponentNames = (JSONArray) IndividualComponentDetail.get("records");
        		log.info("ComponentType : "+ComponentType); 
        		ArrayList<String> observableComponentList = new ArrayList<String>();
        		for(int j = 0; j < ChangedComponentNames.size() ; j ++) {
            		JSONObject ChangedComponents = (JSONObject) ChangedComponentNames.get(j);
            		String ChangeComponentName = (String) ChangedComponents.get("ComponentName");
            		//System.out.println("Component Type : "+ComponentType); 
            		observableComponentList.add(ChangeComponentName);
            		log.info("Component Name : "+ChangeComponentName); 
        		}
        		ImpactedComponentsMap.put(ComponentType, observableComponentList);
        		log.info("********************************");
    		}
    		log.info("********************************"); 
    		log.info(ImpactedComponentsMap);
    		log.info("********************************"); 
    	    readerImpactedSalesforceComponentsJSONFile.close();
    	}catch(Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
        }
		return ImpactedComponentsMap;
    }
}
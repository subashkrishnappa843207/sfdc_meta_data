package com.cognizant.gui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cognizant.login.OAuthTokenFlow;
import com.cognizant.sfdc_quality_analyzer_v3.App;
import com.cognizant.uicomponent.DisplayImpactedComponents;
import com.cognizant.uicomponent.DisplayImpactedComponentsController;
import com.cognizant.uicomponent.DisplayInScopeComponents;
import com.cognizant.uicomponent.DisplayInScopeComponentsController;
import com.cognizant.uicomponent.DisplayMultiOrgMissingComponents;
import com.cognizant.uicomponent.DisplayMultiOrgMissingComponentsController;
import com.cognizant.uicomponent.DisplayOutScopeComponents;
import com.cognizant.uicomponent.DisplayOutScopeComponentsController;
import com.cognizant.utility.Config;
import com.cognizant.utility.ExcelUtils;
import com.cognizant.utility.JsonUtils;
import com.cognizant.utility.SFDCContext;
import com.cognizant.utility.XMLUtils;

import io.restassured.response.Response;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class MultiOrgAnalysisSummaryWholeOrgPage implements Initializable {
	private static final Logger log = Logger.getLogger(MultiOrgAnalysisSummaryWholeOrgPage.class);
	ExcelUtils excelUtil = new ExcelUtils();
	JsonUtils jsonUtil = new JsonUtils();
	XMLUtils xmlUtil = new XMLUtils();
	ArrayList<String> InScopeItems = new ArrayList<String>(); 
	ArrayList<String> OutScopeItems = new ArrayList<String>(); 

    private ObservableList<String> cbFileFormatList = FXCollections.observableArrayList("EXCEL", "JSON"); //, "JSON", "XML"
    
	@FXML Button FindImpactedComponentsButton;
	@FXML Button ViewMoreDetailsButton;
	@FXML ComboBox SelectReportingFileFormat;
	@FXML Button generateImpactAnalysisReportButton;
	@FXML TextField GeneratedReportPath;
	@FXML ListView<String> SalesforceComponentListView;
	@FXML TextArea SelectedSalesforceComponentsTextArea;
	@FXML ImageView RecordFoundSmiley;
	@FXML Label RecordCountPrefix;
	@FXML Label RecordCount;
	@FXML Label RecordCountSuffix;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	    	//Setting valid file formats to the ComboBox
    	SelectReportingFileFormat.setItems(cbFileFormatList);
	}
	
    @FXML
    private void switchBackToHomePage() throws IOException {
    	log.info("Moving back to Home Page");
        App.setRoot("AppHomePage");
    }
    
    @FXML
    private void switchBackToPreviousPage() throws IOException {
    	log.info("Moving Back to Previous Page");
        App.setRoot("MultiOrgHomePage");
    }
    
    @FXML
    private void SeeInScopeComponents() throws IOException {
    	log.info("Placeholder method to See In Scope Items");
	    for (String name : Config.SFDC_SINGLE_INSCOPE_ITEMS) {
	    	DisplayInScopeComponentsController.masterData.add(new DisplayInScopeComponents(name));
	    }
	    DisplayInScopeComponentsController sfdc_dicc = new DisplayInScopeComponentsController();
    	Stage primaryStage = new Stage();
    	sfdc_dicc.start(primaryStage);
    }
    
    @FXML
    private void SeeOutScopeComponents() throws IOException {
    	log.info("Placeholder method to See Out Scope Items");
	    for (String name : Config.SFDC_SINGLE_OUTSCOPE_ITEMS) {
	    	DisplayOutScopeComponentsController.masterData.add(new DisplayOutScopeComponents(name));
	    }
		DisplayOutScopeComponentsController sfdc_docc = new DisplayOutScopeComponentsController();
    	Stage primaryStage = new Stage();
    	sfdc_docc.start(primaryStage);
    }
    
    @FXML
    private void FindImpactedComponents() throws IOException, JSONException, ParseException {
    	log.info("Placeholder method for Find impacted components");
    	File fileHappySmileyPath = new File(System.getProperty("user.dir")+"\\target\\classes\\com\\cognizant\\sfdc_quality_analyzer_v3\\images\\icons8-happy-cloud.png");
        File fileSadSmileyPath = new File(System.getProperty("user.dir")+"\\target\\classes\\com\\cognizant\\sfdc_quality_analyzer_v3\\images\\icons8-sad-cloud.png");
        Image HappySmileyImage = new Image(fileHappySmileyPath.toURI().toString());
        Image SadSmileyImage = new Image(fileSadSmileyPath.toURI().toString());
    	if(DisplayMultiOrgMissingComponentsController.masterMultiOrgMissingData.size() > 0) {
    		DisplayMultiOrgMissingComponentsController.masterMultiOrgMissingData.clear();
    	}

    	ArrayList<String> ArrayListMultiOrgSourceFound = new ArrayList<String>(); 
    	ArrayList<String> ArrayListMultiOrgSourceNotFound = new ArrayList<String>(); 
    	ArrayList<String> ArrayListMultiOrgTargetFound = new ArrayList<String>(); 
    	ArrayList<String> ArrayListMultiOrgTargetNotFound = new ArrayList<String>(); 
        ObservableList<String> selectedSObjects = FXCollections.observableArrayList(Config.SFDC_SINGLE_INSCOPE_ITEMS);
    	ArrayList<String> SFDCSObjectsScopeItemsMultiOrgSource = new ArrayList<String>(); 
    	ArrayList<String> SFDCSObjectsScopeItemsMultiOrgTarget = new ArrayList<String>(); 
		//Retrieving all SObject Names from MultiOrgSource
		OAuthTokenFlow.generateRequestSpecification("MultiOrgSource");
		Response toolingSOBjectResponseMultiOrgSource = SFDCContext.getToolingQuery();
	    JSONObject resultsSObjectMultiOrgSource = new JSONObject(toolingSOBjectResponseMultiOrgSource.body().asPrettyString());
		JSONArray jsonRecordsArrayMultiOrgSource = resultsSObjectMultiOrgSource.optJSONArray("sobjects");
		log.info(jsonRecordsArrayMultiOrgSource.length());
		for(int k=0;k<jsonRecordsArrayMultiOrgSource.length();k++) {
			JSONObject jsonRecordsMultiOrgSource = new JSONObject(jsonRecordsArrayMultiOrgSource.get(k).toString());
			SFDCSObjectsScopeItemsMultiOrgSource.add(jsonRecordsMultiOrgSource.get("name").toString());
		}
		Config.SFDC_MULTI_SOURCE_SOBJECTS_ITEMS = SFDCSObjectsScopeItemsMultiOrgSource;
		log.info("SFDCSObjectsScopeItemsMultiOrgSource Size : "+SFDCSObjectsScopeItemsMultiOrgSource.size());
		log.info("Config.SFDC_MULTI_SOURCE_SOBJECTS_ITEMS Size : "+Config.SFDC_MULTI_SOURCE_SOBJECTS_ITEMS.size());
		
		//Retrieving all SObject Names from MultiOrgTarget
		OAuthTokenFlow.generateRequestSpecification("MultiOrgTarget");
		Response toolingSOBjectResponseMultiOrgTarget = SFDCContext.getToolingQuery();
	    JSONObject resultsSObjectMultiOrgTarget = new JSONObject(toolingSOBjectResponseMultiOrgTarget.body().asPrettyString());
		JSONArray jsonRecordsArrayMultiOrgTarget = resultsSObjectMultiOrgTarget.optJSONArray("sobjects");
		log.info(jsonRecordsArrayMultiOrgTarget.length());
		for(int l=0;l<jsonRecordsArrayMultiOrgTarget.length();l++) {
			JSONObject jsonRecordsMultiOrgTarget = new JSONObject(jsonRecordsArrayMultiOrgTarget.get(l).toString());
			SFDCSObjectsScopeItemsMultiOrgTarget.add(jsonRecordsMultiOrgTarget.get("name").toString());
		}
		Config.SFDC_MULTI_TARGET_SOBJECTS_ITEMS = SFDCSObjectsScopeItemsMultiOrgTarget;
		log.info("SFDCSObjectsScopeItemsMultiOrgTarget Size : "+SFDCSObjectsScopeItemsMultiOrgTarget.size());
		log.info("Config.SFDC_MULTI_TARGET_SOBJECTS_ITEMS Size : "+Config.SFDC_MULTI_TARGET_SOBJECTS_ITEMS.size());
		
    	int totalCountTarget = 0 , totalCountSource = 0;
		for(int i = 0 ; i < selectedSObjects.size() ; i++) {
			log.info("selectedSObjects Name : "+selectedSObjects.get(i));

    		//Separating in-scope items from whole Tooling Sobject items		
			String splittedSelectedSObject = selectedSObjects.get(i);
			log.info("SObject Name : "+splittedSelectedSObject);
			ObservableList<String> FoundComponentListSourceOrg = FXCollections.observableArrayList();
			ObservableList<String> FoundComponentListTargetOrg = FXCollections.observableArrayList();
			//**************************************************************************************************
    		//*************************Fetching all Components from Source Org Now******************************
			//**************************************************************************************************
			if(Config.SFDC_MULTI_SOURCE_SOBJECTS_ITEMS.contains(splittedSelectedSObject)) {
        		OAuthTokenFlow.generateRequestSpecification("MultiOrgSource");
    			String SObjectFieldName = excelUtil.getCellData("SOBJECT", "FieldName",splittedSelectedSObject);
    			log.info("SELECT "+SObjectFieldName+" FROM "+splittedSelectedSObject);
    			ArrayListMultiOrgSourceFound.add(splittedSelectedSObject);
                //Fetching all Components from Source Org first
    			try {
		        	Response SourceOrgFoundElementsResponse = SFDCContext.runToolingQuery("SELECT "+SObjectFieldName+" FROM "+splittedSelectedSObject);
		        	//System.out.println(responseGetQueryDetails.body().asPrettyString());
		    	    JSONObject SourceResultsRecordInfo = new JSONObject(SourceOrgFoundElementsResponse.body().asPrettyString());
		    		JSONArray jsonRecordsArraySourceOrg = SourceResultsRecordInfo.optJSONArray("records");
	        		//
		    		for(int j=0;j<jsonRecordsArraySourceOrg.length();j++) {
		    			JSONObject jsonFinalRecordsSourceOrg = new JSONObject(jsonRecordsArraySourceOrg.get(j).toString());
		    			if(!(jsonFinalRecordsSourceOrg.getString(SObjectFieldName).toString().contains("null"))) {
		    				FoundComponentListSourceOrg.add(jsonFinalRecordsSourceOrg.getString(SObjectFieldName).toString());
		    			}
		    			totalCountSource= totalCountSource + 1;
		    		}

		    		//***********************************************************************
		    		int TotalResultSizeSource = SourceResultsRecordInfo.getInt("totalSize");
		    		boolean amIonLastPageSource = SourceResultsRecordInfo.getBoolean("done");
		    		while(!amIonLastPageSource){	
		    			String nextRecordsUrl = SourceResultsRecordInfo.getString("nextRecordsUrl");
		    			System.out.println("nextRecordsUrl : "+nextRecordsUrl);
		    			SourceOrgFoundElementsResponse = SFDCContext.getToolingQuery(nextRecordsUrl);
		    			SourceResultsRecordInfo = new JSONObject(SourceOrgFoundElementsResponse.body().asPrettyString());
		    			jsonRecordsArraySourceOrg = SourceResultsRecordInfo.optJSONArray("records");
		    			amIonLastPageSource = SourceResultsRecordInfo.getBoolean("done");
		    			System.out.println("Move on to next page as amIonLastPageSource = "+amIonLastPageSource);
		    			System.out.println("jsonResultsRecordsArray Size : "+jsonRecordsArraySourceOrg.length());
			    		for(int j=0;j<jsonRecordsArraySourceOrg.length();j++) {
			    			JSONObject jsonFinalRecordsSourceOrg = new JSONObject(jsonRecordsArraySourceOrg.get(j).toString());
			    			if(!(jsonFinalRecordsSourceOrg.getString(SObjectFieldName).toString().contains("null"))) {
			    				FoundComponentListSourceOrg.add(jsonFinalRecordsSourceOrg.getString(SObjectFieldName).toString());
			    			}
			    			totalCountSource = totalCountSource + 1;
			    		}
		    		}
		    		log.info("Total Count On First page: "+TotalResultSizeSource);
		    		log.info("Final Count : "+totalCountSource);
		    		log.info("FoundComponentListSourceOrg Size : "+FoundComponentListSourceOrg.size());
    			}catch(RuntimeException runExcep){
    				log.error(runExcep.getMessage());
    				throw new RuntimeException("Error occured while retrieving records for " + splittedSelectedSObject +" component", runExcep);
    			}
			}else {
				ArrayListMultiOrgSourceNotFound.add(splittedSelectedSObject);
				log.info(splittedSelectedSObject+" Object Not found in Source org");
			}
			//**************************************************************************************************
    		//*************************Fetching all Components from Target Org Now******************************
			//**************************************************************************************************
			if(Config.SFDC_MULTI_TARGET_SOBJECTS_ITEMS.contains(splittedSelectedSObject)) {
				ArrayListMultiOrgTargetFound.add(splittedSelectedSObject);
				String SObjectFieldName = excelUtil.getCellData("SOBJECT", "FieldName",splittedSelectedSObject);
    			log.info("SELECT "+SObjectFieldName+" FROM "+splittedSelectedSObject);
    			try {
		        	OAuthTokenFlow.generateRequestSpecification("MultiOrgTarget");
		        	Response TargetOrgFoundElementsResponse = SFDCContext.runToolingQuery("SELECT "+SObjectFieldName+" FROM "+splittedSelectedSObject);
		        	//System.out.println(responseGetQueryDetails.body().asPrettyString());
		    	    JSONObject TargetResultsRecordInfo = new JSONObject(TargetOrgFoundElementsResponse.body().asPrettyString());
		    		JSONArray jsonRecordsArrayTargetOrg = TargetResultsRecordInfo.optJSONArray("records");
	        		//
		    		for(int j=0;j<jsonRecordsArrayTargetOrg.length();j++) {
		    			JSONObject jsonFinalRecordsTargetOrg = new JSONObject(jsonRecordsArrayTargetOrg.get(j).toString());
		    			if(!(jsonFinalRecordsTargetOrg.getString(SObjectFieldName).toString().contains("null"))) {
		    				FoundComponentListTargetOrg.add(jsonFinalRecordsTargetOrg.getString(SObjectFieldName).toString());
		    			}
		    			totalCountTarget = totalCountTarget + 1;
		    		}

		    		//***********************************************************************
		    		int TotalResultSizeTarget = TargetResultsRecordInfo.getInt("totalSize");
		    		boolean amIonLastPageTarget = TargetResultsRecordInfo.getBoolean("done");
		    		while(!amIonLastPageTarget){	
		    			String nextRecordsUrl = TargetResultsRecordInfo.getString("nextRecordsUrl");
		    			System.out.println("nextRecordsUrl : "+nextRecordsUrl);
		    			TargetOrgFoundElementsResponse = SFDCContext.getToolingQuery(nextRecordsUrl);
		    			TargetResultsRecordInfo = new JSONObject(TargetOrgFoundElementsResponse.body().asPrettyString());
		    			jsonRecordsArrayTargetOrg = TargetResultsRecordInfo.optJSONArray("records");
		    			amIonLastPageTarget = TargetResultsRecordInfo.getBoolean("done");
		    			System.out.println("Move on to next page as amIonLastPageTarget = "+amIonLastPageTarget);
		    			System.out.println("jsonResultsRecordsArray Size : "+jsonRecordsArrayTargetOrg.length());
			    		for(int j=0;j<jsonRecordsArrayTargetOrg.length();j++) {
			    			JSONObject jsonFinalRecordsTargetOrg = new JSONObject(jsonRecordsArrayTargetOrg.get(j).toString());
			    			if(!(jsonFinalRecordsTargetOrg.getString(SObjectFieldName).toString().contains("null"))) {
			    				FoundComponentListTargetOrg.add(jsonFinalRecordsTargetOrg.getString(SObjectFieldName).toString());
			    			}
			    			totalCountTarget = totalCountTarget + 1;
			    		}
		    		}
		    		log.info("Total Count On First page: "+TotalResultSizeTarget);
		    		log.info("Final Count : "+totalCountTarget);
		    		log.info("FoundComponentListTargetOrg Size : "+FoundComponentListTargetOrg.size());
    			}catch(RuntimeException runExcep){
    				log.error(runExcep.getMessage());
    				throw new RuntimeException("Error occured while retrieving records for " + splittedSelectedSObject +" component", runExcep);
    			}
			}else {
				ArrayListMultiOrgTargetNotFound.add(splittedSelectedSObject);
				log.info(splittedSelectedSObject+" Object Not found in Target org");
			}

			//Now Comparing the 2 ArrayList between Source Org and Target Org Component List
			//if(FoundComponentListTargetOrg.size() > 0 && FoundComponentListSourceOrg.size() > 0) {
			ArrayList<String> listMissing = new ArrayList<>(CollectionUtils.disjunction(FoundComponentListTargetOrg, FoundComponentListSourceOrg));
			ObservableList<String> oListMissing = FXCollections.observableArrayList(listMissing);
			log.info("List of elements are Missing : "+oListMissing);
			ArrayList<String> listMatching = new ArrayList<>(CollectionUtils.intersection(FoundComponentListTargetOrg, FoundComponentListSourceOrg));
			ObservableList<String> oListMatching = FXCollections.observableArrayList(listMatching);
			log.info("List of elements are Matching : "+oListMatching);

			DisplayMultiOrgMissingComponentsController.masterMultiOrgMissingData.add(new DisplayMultiOrgMissingComponents(splittedSelectedSObject, Config.SFDC_MULTI_SOURCE_ORGID, 
					FoundComponentListSourceOrg, Config.SFDC_MULTI_TARGET_ORGID, FoundComponentListTargetOrg, oListMissing, oListMatching));
			//}
		}
		ArrayList<String> listMissingSObject = new ArrayList<>(CollectionUtils.disjunction(Config.SFDC_MULTI_TARGET_SOBJECTS_ITEMS, Config.SFDC_MULTI_SOURCE_SOBJECTS_ITEMS));
		ObservableList<String> oListMissingSObject = FXCollections.observableArrayList(listMissingSObject);
		log.info("***Important Note : Below List of Sobjects includes both InScope and OutScope Items of this Tool");
		log.info("List of SObjects are Missing B/w Source Org and Traget Org: "+oListMissingSObject);
		ArrayList<String> listMatchingSObject = new ArrayList<>(CollectionUtils.intersection(Config.SFDC_MULTI_TARGET_SOBJECTS_ITEMS, Config.SFDC_MULTI_SOURCE_SOBJECTS_ITEMS));
		ObservableList<String> oListMatchingSObject = FXCollections.observableArrayList(listMatchingSObject);
		log.info("List of SObjects are Matching B/w Source Org and Traget Org: "+oListMatchingSObject);
		
		//log.info("ArrayListMultiOrgTargetFound Size : "+ArrayListMultiOrgTargetFound.size());
		log.info("SObjects found in MultiOrgTarget Org, which are InScope of this Tool : "+ArrayListMultiOrgTargetFound.toString());
		//log.info("ArrayListMultiOrgTargetNotFound Size : "+ArrayListMultiOrgTargetNotFound.size());
		log.info("SObjects not found in MultiOrgTarget Org, which are InScope of this Tool : "+ArrayListMultiOrgTargetNotFound.toString());
		//log.info("ArrayListMultiOrgSourceFound Size : "+ArrayListMultiOrgSourceFound.size());
		log.info("SObjects found in MultiOrgSource Org, which are InScope of this Tool : "+ArrayListMultiOrgSourceFound.toString());
		//log.info("ArrayListMultiOrgSourceNotFound Size : "+ArrayListMultiOrgSourceNotFound.size());
		log.info("SObjects not found in MultiOrgSource Org, which are InScope of this Tool : "+ArrayListMultiOrgSourceNotFound.toString());
		if(oListMatchingSObject.size() > 0) {
			RecordFoundSmiley.setImage(HappySmileyImage);
			RecordCountPrefix.setText("Total");
			RecordCountPrefix.setTextFill(Color.WHITE);
			RecordCountPrefix.setStyle("-fx-font: 18 arial;-fx-font-weight: bold;");
			RecordCountPrefix.setWrapText(true);
			RecordCountPrefix.setTextAlignment(TextAlignment.CENTER);
			RecordCount.setText(String.valueOf(oListMatchingSObject.size()));
			RecordCount.setTextFill(Color.WHITE);
			RecordCount.setStyle("-fx-font: 48 arial;-fx-font-weight: bold;");
			RecordCount.setWrapText(true);
			RecordCount.setTextAlignment(TextAlignment.CENTER);
			RecordCountSuffix.setText("Objects Compared");
			RecordCountSuffix.setTextFill(Color.WHITE);
			RecordCountSuffix.setStyle("-fx-font: 14 arial;-fx-font-weight: bold;");
			RecordCountSuffix.setWrapText(true);
			RecordCountSuffix.setTextAlignment(TextAlignment.CENTER);
		}else {
			RecordFoundSmiley.setImage(SadSmileyImage);
			RecordCountPrefix.setText("Total");
			RecordCountPrefix.setTextFill(Color.WHITE);
			RecordCountPrefix.setWrapText(true);
			RecordCountPrefix.setTextAlignment(TextAlignment.CENTER);
			RecordCountPrefix.setStyle("-fx-font: 18 arial;-fx-font-weight: bold;");
			RecordCount.setText(String.valueOf(oListMatchingSObject.size()));
			RecordCount.setTextFill(Color.WHITE);
			RecordCount.setWrapText(true);
			RecordCount.setTextAlignment(TextAlignment.CENTER);
			RecordCount.setStyle("-fx-font: 48 arial;-fx-font-weight: bold;");
			RecordCountSuffix.setText("Objects Compared");
			RecordCountSuffix.setTextFill(Color.WHITE);
			RecordCountSuffix.setStyle("-fx-font: 14 arial;-fx-font-weight: bold;");
			RecordCountSuffix.setWrapText(true);
			RecordCountSuffix.setTextAlignment(TextAlignment.CENTER);
		}
    }
    
    @FXML
    private void generateImpactAnalysisReport() throws IOException {
    	log.info("Placeholder method for Generating Impact Analysis report based on given File Format");
    	String fileFormat = (String) SelectReportingFileFormat.getSelectionModel().getSelectedItem();
    	String[] arrayColumnName = {"ComponentType", "SourceOrgId", "SourceOrgComponentNames", "TargetOrgId", "TargetOrgComponentNames", 
    			"MissingComponents", "MatchingComponents"};	
    	
    	if(fileFormat.equalsIgnoreCase("excel")) {
    		log.info("EXCEL Selected");
			String reportFilePath = excelUtil.export(arrayColumnName, DisplayMultiOrgMissingComponentsController.masterMultiOrgMissingData);
			GeneratedReportPath.setText(reportFilePath);
    	}else if(fileFormat.equalsIgnoreCase("json")) {
    		log.info("JSON Selected");
			String reportFilePath = jsonUtil.export(arrayColumnName, DisplayMultiOrgMissingComponentsController.masterMultiOrgMissingData);
			GeneratedReportPath.setText(reportFilePath);
    	}/*else if(fileFormat.equalsIgnoreCase("xml")) {
    		System.out.println("XML Selected");
    		String reportFilePath = xmlUtil.export(arrayColumnName, SourceOrgFoundComponentsMap);
    		GeneratedReportPath.setText(reportFilePath);
    	}*/
    }
    
    @FXML
    private void ViewMoreDetails() throws IOException {
    	log.info("Placeholder method for View Details about impacted components");
    	log.info("************************************");
    	//log.info(DisplayMultiOrgMissingComponentsController.masterMultiOrgMissingData);
		DisplayMultiOrgMissingComponentsController sfdc_dmomcc = new DisplayMultiOrgMissingComponentsController();
    	Stage primaryStage = new Stage();
    	sfdc_dmomcc.start(primaryStage);
    	
    }
    
    @FXML
    private void createPackageXMLFile() throws IOException {
    	log.info("Placeholder method for Creating package.xml file based on the identified impacted components");
    }
    
    @FXML
    private void identifyAutomationTestScripts() throws IOException {
    	log.info("Moving to SFDC Test Impact Analysis Page");
    	App.setRoot("SFDCTestImpactAnalysisPage");
    }
    
    @FXML
    private void switchToTestAutomationHomePage() throws IOException {
    	log.info("Moving to Selenium Automation Home Page");
    	App.setRoot("SeleniumAutomationHomePage");
    }
    
    @FXML
    private void signOut() throws IOException {
    	App.setRoot("AppLauncherPage");
        //Platform.exit();
        //System.exit(0);
    }
}

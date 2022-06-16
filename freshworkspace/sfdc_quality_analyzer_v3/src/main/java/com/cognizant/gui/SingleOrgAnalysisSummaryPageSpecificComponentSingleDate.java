package com.cognizant.gui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cognizant.login.OAuthTokenFlow;
import com.cognizant.sfdc_quality_analyzer_v3.App;
import com.cognizant.uicomponent.DisplayImpactedComponents;
import com.cognizant.uicomponent.DisplayImpactedComponentsController;
import com.cognizant.uicomponent.DisplayOutScopeComponents;
import com.cognizant.uicomponent.DisplayOutScopeComponentsController;
import com.cognizant.uicomponent.DoughnutChart;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
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

public class SingleOrgAnalysisSummaryPageSpecificComponentSingleDate implements Initializable {
	private static final Logger log = Logger.getLogger(SingleOrgAnalysisSummaryPageSpecificComponentSingleDate.class);
	ExcelUtils excelUtil = new ExcelUtils();
	JsonUtils jsonUtil = new JsonUtils();
	XMLUtils xmlUtil = new XMLUtils();
	ArrayList<String> InScopeItems = new ArrayList<String>(); 
	ArrayList<String> OutScopeItems = new ArrayList<String>(); 
	LocalDateTime FromDate;
    Map<String, HashMap<String, String>> FoundComponentsFinalMap=new HashMap<String,HashMap<String,String>>(); 
    private ObservableList<String> cbFileFormatList = FXCollections.observableArrayList("EXCEL", "JSON", "XML");
	ObservableList<String> listview_InScopeToolingSObjectsItemsList = FXCollections.observableArrayList(Config.SFDC_SINGLE_INSCOPE_ITEMS);
	ObservableList<String> listview_SelectedToolingSObjectsItemsList = FXCollections.observableArrayList();
	
	@FXML ComboBox SalesforceComponentCombobox;
	@FXML DatePicker SelectFromDate;
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
	@FXML ListView<String> SalesforceInScopeComponentListView;
	@FXML ListView<String> SalesforceSelectedComponentListView;
	@FXML Button AddComponent;
	@FXML Button RemoveComponent;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Setting valid file formats to the ComboBox
		SelectReportingFileFormat.setItems(cbFileFormatList);

		SalesforceInScopeComponentListView.setItems(listview_InScopeToolingSObjectsItemsList);
	    SalesforceSelectedComponentListView.setItems(listview_SelectedToolingSObjectsItemsList);
		//SalesforceInScopeComponentListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		/*
		AddComponent.setOnAction((ActionEvent event) -> {
	    	String selectedItem = SalesforceInScopeComponentListView.getSelectionModel().getSelectedItem();
	    	System.out.println(selectedItem);
	        if (selectedItem != null) {
	        	SalesforceInScopeComponentListView.getSelectionModel().clearSelection();
	        	listview_InScopeToolingSObjectsItemsList.remove(selectedItem);
	        	listview_SelectedToolingSObjectsItemsList.add(selectedItem);
			    Collections.sort(listview_InScopeToolingSObjectsItemsList);
			    Collections.sort(listview_SelectedToolingSObjectsItemsList);
	    	}

	    });


	    RemoveComponent.setOnAction((ActionEvent event) -> {
	        String removedItem = SalesforceSelectedComponentListView.getSelectionModel().getSelectedItem();
		      System.out.println(removedItem);
		      if (removedItem != null) {
		    	  SalesforceSelectedComponentListView.getSelectionModel().clearSelection();
		    	  listview_SelectedToolingSObjectsItemsList.remove(removedItem);
		    	  listview_InScopeToolingSObjectsItemsList.add(removedItem);
				  Collections.sort(listview_InScopeToolingSObjectsItemsList);
				  Collections.sort(listview_SelectedToolingSObjectsItemsList);
		      }
	    });
	    */
	}

    @FXML
    private void RemoveComponentFromListView() throws IOException {
	      String removedItem = SalesforceSelectedComponentListView.getSelectionModel().getSelectedItem();
	      log.info("Removed Item = "+removedItem);
	      if (removedItem != null) {
	    	  SalesforceSelectedComponentListView.getSelectionModel().clearSelection();
	    	  listview_SelectedToolingSObjectsItemsList.remove(removedItem);
	    	  listview_InScopeToolingSObjectsItemsList.add(removedItem);
			  Collections.sort(listview_InScopeToolingSObjectsItemsList);
			  Collections.sort(listview_SelectedToolingSObjectsItemsList);
	      }
    }
    
    @FXML
    private void AddComponentToListView() throws IOException {
    	String selectedItem = SalesforceInScopeComponentListView.getSelectionModel().getSelectedItem();
    	log.info("Selected Item = "+selectedItem);
        if (selectedItem != null) {
        	SalesforceInScopeComponentListView.getSelectionModel().clearSelection();
        	listview_InScopeToolingSObjectsItemsList.remove(selectedItem);
        	listview_SelectedToolingSObjectsItemsList.add(selectedItem);
		    Collections.sort(listview_InScopeToolingSObjectsItemsList);
		    Collections.sort(listview_SelectedToolingSObjectsItemsList);
    	}
    }
    
    @FXML
    private void switchBackToHomePage() throws IOException {
        App.setRoot("AppHomePage");
    }
    
    @FXML
    private void switchBackToPreviousPage() throws IOException {
        App.setRoot("SingleOrgHomePage");
    }
    
    @FXML
    private void SelectFromDate() throws IOException {
    	log.info("Placeholder method for Selecting From Date"+SelectFromDate.getValue());
    }
    
    
    @FXML
    private void generateImpactAnalysisReport() throws IOException {
    	log.info("Placeholder method for Generating Impact Analysis report based on given File Format");
    	String fileFormat = (String) SelectReportingFileFormat.getSelectionModel().getSelectedItem();
    	String[] arrayColumnName = {"ComponentType", "ComponentName", "LastModifiedByID", "LastModifiedDate"};	
    	if(fileFormat.equalsIgnoreCase("excel")) {
    		log.info("EXCEL Selected");
			String reportFilePath = excelUtil.exportExcel(arrayColumnName, FoundComponentsFinalMap);
			GeneratedReportPath.setText(reportFilePath);
    	}else if(fileFormat.equalsIgnoreCase("json")) {
    		log.info("JSON Selected");
			String reportFilePath = jsonUtil.export(arrayColumnName, FoundComponentsFinalMap);
			GeneratedReportPath.setText(reportFilePath);
    	}else if(fileFormat.equalsIgnoreCase("xml")) {
    		log.info("XML Selected");
    		String reportFilePath = xmlUtil.export(arrayColumnName, FoundComponentsFinalMap);
    		GeneratedReportPath.setText(reportFilePath);
    	}
    }
    
    @FXML
    private void ViewMoreDetails() throws IOException {
    	log.info("Placeholder method for View Details about impacted components");
    	if(DisplayImpactedComponentsController.masterData.size() > 0) {
    		DisplayImpactedComponentsController.masterData.clear();
    	}

		log.info("FoundComponentsFinalMap.size() : "+FoundComponentsFinalMap.size());
		for (String i : FoundComponentsFinalMap.keySet()) {
		   Map<String, String> s = FoundComponentsFinalMap.get(i) ;
		      //System.out.println("key :" + i + " value: " + s);
		     log.info("s.size() : "+s.size());
		     for(String j : s.keySet()) {
		    	String k = s.get(j);
	    		String[] arrlastModifiedDetails = k.split("#SFDC#");
	    		String lastModifiedDate = arrlastModifiedDetails[1];
	    		String lastModifiedUserId = arrlastModifiedDetails[0];
		    	log.info("First key : " + i + " Second Key : " + j +" value : "+k);
		    	DisplayImpactedComponentsController.masterData.add(new DisplayImpactedComponents(i, j, lastModifiedUserId, lastModifiedDate));
		     }
		} 
		log.info("************************************");
		//System.out.println(DisplayImpactedComponentsController.masterData);
		DisplayImpactedComponentsController sfdc_dicc = new DisplayImpactedComponentsController();
    	Stage primaryStage = new Stage();
    	sfdc_dicc.start(primaryStage);
    }
    
    @FXML
    private void ViewOutOfScopeComponents() throws IOException {
    	log.info("Placeholder method for View Details about Out of Scope components");
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
        if(FoundComponentsFinalMap.size() > 0) {
        	FoundComponentsFinalMap.clear();
        }
        
        ObservableList<String> selectedSObjects = null;
    	int totalCount = 0;
    	int finalFoundDataCount = 0;
    	if(SalesforceSelectedComponentListView.getItems().size() == 0) {
    		Alert SelectetComponentErrorAlert = new Alert(AlertType.ERROR);
    		SelectetComponentErrorAlert.setHeaderText("Salesforce Component selection Error");
    		SelectetComponentErrorAlert.setContentText("Please select the Salesforce Component before proceed!!!");
    		SelectetComponentErrorAlert.showAndWait();
    	} else if(SelectFromDate.getValue() == null) {
    		Alert FromDateErrorAlert = new Alert(AlertType.ERROR);
    		FromDateErrorAlert.setHeaderText("From Date not selected");
    		FromDateErrorAlert.setContentText("Please select the From Date field before proceed!!!");
    		FromDateErrorAlert.showAndWait();
    	} else {
    		selectedSObjects = SalesforceSelectedComponentListView.getItems();
    		HashMap<String,HashMap<String, HashMap<String, String>>> PrimarySOQLResultsMap = new HashMap<String,HashMap<String, HashMap<String, String>>>();
    		//String[] splittedSelectedSObjectList = selectedSObjects.split(";");
    		for(int i = 0 ; i < selectedSObjects.size() ; i++) {
    			HashMap<String, HashMap<String, String>> SecondarySOQLResultsMap = new HashMap<String, HashMap<String, String>>();
    			String splittedSelectedSObject = selectedSObjects.get(i);
    			log.info("SObject Name : "+splittedSelectedSObject);
    			String SObjectFieldName = excelUtil.getCellData("SOBJECT", "FieldName",splittedSelectedSObject);
    			log.info("SELECT "+SObjectFieldName+", LastModifiedById, LastModifiedDate FROM "+splittedSelectedSObject);
    			String[] selectedFields = {SObjectFieldName, "LastModifiedById", "LastModifiedDate"};
    			try {
		        	Response responseGetQueryDetails = SFDCContext.runToolingQuery("SELECT "+SObjectFieldName+", LastModifiedById, LastModifiedDate FROM "+splittedSelectedSObject);
		    		//System.out.println(responseGetQueryDetails.body().asPrettyString());
		    	    JSONObject resultsRecordInfo = new JSONObject(responseGetQueryDetails.body().asPrettyString());
		    		JSONArray jsonResultsRecordsArray = resultsRecordInfo.optJSONArray("records");
	        		//
		    		for(int j=0;j<jsonResultsRecordsArray.length();j++) {
		    			HashMap<String, String> queryDetailsMap = new HashMap<String, String>();
		    			//for(String selectedFieldName : selectedFields) {
		        			JSONObject jsonFinalRecords = new JSONObject(jsonResultsRecordsArray.get(j).toString());
		        			queryDetailsMap.put(jsonFinalRecords.getString(selectedFields[0]), jsonFinalRecords.getString(selectedFields[1])+"#SFDC#"+jsonFinalRecords.getString(selectedFields[2]));
		    			//}
		        			SecondarySOQLResultsMap.put(String.valueOf(totalCount), queryDetailsMap);
		    			totalCount = totalCount + 1;
		    		}

		    		//***********************************************************************
		    		int TotalResultSize = resultsRecordInfo.getInt("totalSize");
		    		boolean amIonLastPage = resultsRecordInfo.getBoolean("done");
		    		while(!amIonLastPage){	
		    			String nextRecordsUrl = resultsRecordInfo.getString("nextRecordsUrl");
		    			System.out.println("nextRecordsUrl : "+nextRecordsUrl);
		    			responseGetQueryDetails = SFDCContext.getToolingQuery(nextRecordsUrl);
		    			resultsRecordInfo = new JSONObject(responseGetQueryDetails.body().asPrettyString());
		    			jsonResultsRecordsArray = resultsRecordInfo.optJSONArray("records");
		    			amIonLastPage = resultsRecordInfo.getBoolean("done");
		    			System.out.println("Move on to next page as amIonLastPage = "+amIonLastPage);
		    			System.out.println("jsonResultsRecordsArray Size : "+jsonResultsRecordsArray.length());
			    		for(int j=0;j<jsonResultsRecordsArray.length();j++) {
			    			HashMap<String, String> queryDetailsMap = new HashMap<String, String>();
			    			//for(String selectedFieldName : selectedFields) {
			        			JSONObject jsonFinalRecords = new JSONObject(jsonResultsRecordsArray.get(j).toString());
			        			queryDetailsMap.put(jsonFinalRecords.getString(selectedFields[0]), jsonFinalRecords.getString(selectedFields[1])+"#SFDC#"+jsonFinalRecords.getString(selectedFields[2]));
			    			//}
			    			SecondarySOQLResultsMap.put(String.valueOf(totalCount), queryDetailsMap);
			    			totalCount = totalCount + 1;
			    		}
		    		}
		    		PrimarySOQLResultsMap.put(splittedSelectedSObject, SecondarySOQLResultsMap);
		    		log.info("Total Count On First page: "+TotalResultSize);
		    		log.info("Final Count : "+totalCount);
		    		log.info("SecondarySOQLResultsMap Size : "+SecondarySOQLResultsMap.size());
		    		log.info("PrimarySOQLResultsMap Size : "+PrimarySOQLResultsMap.size());
    			}catch(RuntimeException runExcep){
    				log.error(runExcep.getMessage());
    				throw new RuntimeException("Error occured while retrieving records for " + splittedSelectedSObject +" component", runExcep);
    			}
    		}
    		
		    for (String firstKeySet : PrimarySOQLResultsMap.keySet()) {
    		   Map<String, HashMap<String, String>> secondMap = PrimarySOQLResultsMap.get(firstKeySet) ;
    		   HashMap<String, String> FoundComponentsMap = new HashMap<String, String>(); 
    		   for(String secondKeySet : secondMap.keySet()) {
    			    
    		    	HashMap<String, String> thirdMap = secondMap.get(secondKeySet);
    		    	log.info("First key : " + firstKeySet + " Size of Second Map : "+secondMap.size()+" value : "+thirdMap);
    		    	//System.out.println("First key : " + firstKeySet + " Second Key : " + secondKeySet +" value : "+thirdMap);
    		    	for(String thirdKeySet : thirdMap.keySet()) {
      		    		String lastModifiedDetails = thirdMap.get(thirdKeySet);
      		    		String[] arrlastModifiedDetails = lastModifiedDetails.split("#SFDC#");
      		    		String lastModifiedDate = arrlastModifiedDetails[1];
      		    		String lastModifiedUserId = arrlastModifiedDetails[0];
    		    		if(!lastModifiedDate.toString().contains("null")) {
							boolean findMatch = SFDCContext.verifyDateIsInRangeWithoutCreatedDate(SelectFromDate.getValue(), 
									lastModifiedDate.toString());
							if(findMatch) {
								//totalCount = totalCount + 1;
								FoundComponentsMap.put(thirdKeySet, lastModifiedUserId+"#SFDC#"+lastModifiedDate);
								//finalFoundDataCount = finalFoundDataCount + 1;
								log.info("finalFoundDataCount = "+finalFoundDataCount);
								log.info(FoundComponentsMap);
								log.info("***********************************************************************");
							}
						}	
    		    	}
    		    }
    		    if(FoundComponentsMap.size()>0) {
    			   FoundComponentsFinalMap.put(firstKeySet,FoundComponentsMap);
    			   finalFoundDataCount = finalFoundDataCount + FoundComponentsMap.size();
    		   }
    		}
			log.info("finalFoundDataCount = "+finalFoundDataCount);
  		    log.info("FoundComponentsFinalMap Size = "+FoundComponentsFinalMap.size());
    		if(finalFoundDataCount > 0) {
    			RecordFoundSmiley.setImage(HappySmileyImage);
    			RecordCountPrefix.setText("Total");
    			RecordCountPrefix.setTextFill(Color.WHITE);
    			RecordCountPrefix.setStyle("-fx-font: 18 arial;-fx-font-weight: bold;");
    			RecordCountPrefix.setWrapText(true);
    			RecordCountPrefix.setTextAlignment(TextAlignment.CENTER);
    			RecordCount.setText(String.valueOf(finalFoundDataCount));
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
    			RecordCount.setText(String.valueOf(finalFoundDataCount));
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
    
    
    @FXML
    private void createPackageXMLFile() throws IOException {
    	log.info("Placeholder method for Creating package.xml file based on the identified impacted components");
    }
    
    @FXML
    private void identifyAutomationTestScripts() throws IOException {
    	App.setRoot("SFDCTestImpactAnalysisPage");
    }
    
    @FXML
    private void switchToTestAutomationHomePage() throws IOException {
    	App.setRoot("SeleniumAutomationHomePage");
    }
    
    @FXML
    private void signOut() throws IOException {
    	App.setRoot("AppLauncherPage");
        //Platform.exit();
        //System.exit(0);
    }
}

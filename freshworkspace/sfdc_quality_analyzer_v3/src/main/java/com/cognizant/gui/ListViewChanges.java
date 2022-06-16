package com.cognizant.gui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cognizant.sfdc_quality_analyzer_v3.App;
import com.cognizant.uicomponent.DisplayImpactedComponents;
import com.cognizant.uicomponent.DisplayImpactedComponentsController;
import com.cognizant.uicomponent.DisplayOutScopeComponents;
import com.cognizant.uicomponent.DisplayOutScopeComponentsController;
import com.cognizant.uicomponent.SFDCSecurityHealthCheckDetailsPage;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class ListViewChanges implements Initializable {
	private static final Logger log = Logger.getLogger(ListViewChanges.class);
	ExcelUtils excelUtil = new ExcelUtils();
	JsonUtils jsonUtil = new JsonUtils();
	XMLUtils xmlUtil = new XMLUtils();
	ArrayList<String> InScopeItems = new ArrayList<String>(); 
	ArrayList<String> OutScopeItems = new ArrayList<String>(); 
	LocalDateTime FromDate;
	LocalDateTime ToDate;
    Map<String, HashMap<String, String>> FoundComponentsFinalMap=new HashMap<String,HashMap<String,String>>(); 
    private ObservableList<String> cbFileFormatList = FXCollections.observableArrayList("EXCEL", "JSON", "XML");
    
	@FXML DatePicker SelectFromDate;
	@FXML DatePicker SelectToDate;
	@FXML Button FindImpactedComponentsButton;
	@FXML Button ViewMoreDetailsButton;
	@FXML ComboBox SelectReportingFileFormat;
	@FXML Button generateImpactAnalysisReportButton;
	@FXML TextField GeneratedReportPath;
	@FXML ListView<String> SalesforceInScopeComponentListView;
	@FXML ListView<String> SalesforceSelectedComponentListView;
	@FXML Button AddComponent;
	@FXML Button RemoveComponent;
	@FXML TextArea SelectedSalesforceComponentsTextArea;
	@FXML ImageView RecordFoundSmiley;
	@FXML Label RecordCountPrefix;
	@FXML Label RecordCount;
	@FXML Label RecordCountSuffix;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Setting valid file formats to the ComboBox
		SelectReportingFileFormat.setItems(cbFileFormatList);
		
		ObservableList<String> listview_InScopeToolingSObjectsItemsList = FXCollections.observableArrayList(Config.SFDC_SINGLE_INSCOPE_ITEMS);
		ObservableList<String> listview_SelectedToolingSObjectsItemsList = FXCollections.observableArrayList();
		
		SalesforceInScopeComponentListView.setItems(listview_InScopeToolingSObjectsItemsList);
	    SalesforceSelectedComponentListView.setItems(listview_SelectedToolingSObjectsItemsList);
		//SalesforceInScopeComponentListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		AddComponent.setOnAction((ActionEvent event) -> {
	    	String selectedItem = SalesforceInScopeComponentListView.getSelectionModel().getSelectedItem();
	    	log.info("Selected Items = "+selectedItem);
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
	      log.info("Rempved Items = "+removedItem);
	      if (removedItem != null) {
	    	  SalesforceSelectedComponentListView.getSelectionModel().clearSelection();
	    	  listview_SelectedToolingSObjectsItemsList.remove(removedItem);
	    	  listview_InScopeToolingSObjectsItemsList.add(removedItem);
			  Collections.sort(listview_InScopeToolingSObjectsItemsList);
			  Collections.sort(listview_SelectedToolingSObjectsItemsList);
	      }
	    });
	    

		/*
		SelectedSalesforceComponentsTextArea.setWrapText(true);
		SalesforceComponentListView.getSelectionModel().selectedItemProperty()
		    .addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
		     ObservableList<String> selectedItems = SalesforceComponentListView.getSelectionModel().getSelectedItems();
		     StringBuilder builder = new StringBuilder("");

		     for (String name : selectedItems) {
		      builder.append(name + ";");
		     }
		     SelectedSalesforceComponentsTextArea.setText(builder.toString());
		});
		*/
	}

    @FXML
    private void switchBackToHomePage() throws IOException {
    	log.info("Moving Back to Home Page");
        App.setRoot("AppHomePage");
    }
    
    @FXML
    private void switchBackToPreviousPage() throws IOException {
    	log.info("Moving back to Previous Page");
        App.setRoot("SingleOrgHomePage");
    }
    
    @FXML
    private void SelectFromDate() throws IOException {
    	log.info("Placeholder method for Selecting From Date : "+SelectFromDate.getValue());
    }
    
    @FXML
    private void SelectToDate() throws IOException {
    	log.info("Placeholder method for Selecting To Date : "+SelectToDate.getValue().toString());
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

    	FileWriter myWriter = new FileWriter("LastModifiedResponse_FinalOutput.txt");
    	log.info("Selected ListView Items : "+SalesforceSelectedComponentListView.getItems());
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
    	  } else if(SelectToDate.getValue() == null) {
    		Alert ToDateErrorAlert = new Alert(AlertType.ERROR);
    		ToDateErrorAlert.setHeaderText("To Date not selected");
    		ToDateErrorAlert.setContentText("Please select the To Date field before proceed!!!");
    		ToDateErrorAlert.showAndWait();
    	} else {
    		selectedSObjects = SalesforceSelectedComponentListView.getItems();
    		//String[] splittedSelectedSObjectList = selectedSObjects.split(";");
    		for(int i = 0 ; i < selectedSObjects.size() ; i++) {
    			HashMap<String, String> FoundComponentsMap = new HashMap<String, String>(); 
    			String splittedSelectedSObject = selectedSObjects.get(i);
    			log.info("SObject Name : "+splittedSelectedSObject);
    			String SObjectFieldName = excelUtil.getCellData("SOBJECT", "FieldName",splittedSelectedSObject);
    			log.info("SELECT "+SObjectFieldName+", LastModifiedDate FROM "+splittedSelectedSObject);
    			Response LastModifiedDateResponse = null;
    			LastModifiedDateResponse = SFDCContext.runToolingQuery("SELECT "+SObjectFieldName+", LastModifiedDate FROM "+splittedSelectedSObject);
    			//System.out.println(LastModifiedDateResponse.asPrettyString());
    			JSONObject resultsJObject = new JSONObject(LastModifiedDateResponse.body().asPrettyString());
    			JSONArray jsonRecordsArray = resultsJObject.optJSONArray("records");
    			int jsonObjectSize = resultsJObject.getInt("totalSize");
    			int k = 0;
    			for(int j=0;j<jsonObjectSize;j++) {
    				JSONObject jsonFinalRecords = new JSONObject(jsonRecordsArray.get(j).toString());
    				ArrayList<String> FoundComponentList = new ArrayList<String>();

					LastModifiedDateResponse = SFDCContext.runToolingQuery("SELECT "+SObjectFieldName+", LastModifiedDate FROM "+splittedSelectedSObject);
					if(!(jsonFinalRecords.getString("LastModifiedDate").toString().contains("null"))) {
						boolean findMatch = SFDCContext.verifyDateIsInRangeWithoutCreatedDate(SelectFromDate.getValue(), SelectToDate.getValue(), 
								jsonFinalRecords.getString("LastModifiedDate").toString());
						
						if(findMatch) {
							totalCount = totalCount + 1;
							
							//FoundComponentList.add(jsonFinalRecords.getString(SObjectFieldName));
							//FoundComponentList.add(jsonFinalRecords.getString("LastModifiedDate"));
	
							FoundComponentsMap.put(jsonFinalRecords.getString(SObjectFieldName), jsonFinalRecords.getString("LastModifiedDate"));
							k = k + 1;
							log.info("k = "+k);
							log.info(FoundComponentsMap);
							log.info("***********************************************************************");
						}
					}
    			}
    			FoundComponentsFinalMap.put(splittedSelectedSObject,FoundComponentsMap);
    			//System.out.println(FoundComponentsFinalMap);
    		}
    		myWriter.write(FoundComponentsFinalMap.toString());
    		myWriter.close();
    		
    		if(totalCount > 0) {
    			RecordFoundSmiley.setImage(HappySmileyImage);
    			RecordCountPrefix.setText("Total");
    			RecordCount.setText(String.valueOf(totalCount));
    			RecordCountSuffix.setText("Records Found");
    		}else {
    			RecordFoundSmiley.setImage(SadSmileyImage);
    			RecordCountPrefix.setText("Total");
    			RecordCount.setText(String.valueOf(totalCount));
    			RecordCountSuffix.setText("Records Found");
    		}
    	}
    }
    
    
    @FXML
    private void generateImpactAnalysisReport() throws IOException {
    	log.info("Placeholder method for Generating Impact Analysis report based on given File Format");
    	String fileFormat = (String) SelectReportingFileFormat.getSelectionModel().getSelectedItem();
    	String[] arrayColumnName = {"ComponentType", "ComponentName", "LastModifiedDate"};	
    	if(fileFormat.equalsIgnoreCase("excel")) {
    		log.info("EXCEL Selected");
			String reportFilePath = excelUtil.export(arrayColumnName, FoundComponentsFinalMap);
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
		log.info(DisplayImpactedComponentsController.masterData);
		DisplayImpactedComponentsController sfdc_dicc = new DisplayImpactedComponentsController();
    	Stage primaryStage = new Stage();
    	sfdc_dicc.start(primaryStage);
    }
    
    @FXML
    private void createPackageXMLFile() throws IOException {
    	log.info("Placeholder method for Creating package.xml file based on the identified impacted components");
    }
    
    @FXML
    private void identifyAutomationTestScripts() throws IOException {
    	log.info("Moving to SFDC Test Impact Analysis");
    	App.setRoot("SFDCTestImpactAnalysisPage");
    }
    
    @FXML
    private void switchToTestAutomationHomePage() throws IOException {
    	log.info("Moving to Selenium Test Automation Home Page");
    	App.setRoot("SeleniumAutomationHomePage");
    }
    
    @FXML
    private void signOut() throws IOException {
    	App.setRoot("AppLauncherPage");
        //Platform.exit();
        //System.exit(0);
    }
}

package com.cognizant.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.cognizant.login.OAuthToken;
import com.cognizant.login.OAuthTokenFlow;
import com.cognizant.sfdc_quality_analyzer_v3.App;
import com.cognizant.uicomponent.DisplayImpactedComponents;
import com.cognizant.uicomponent.DisplayImpactedComponentsController;
import com.cognizant.uicomponent.DisplayMappedComponents;
import com.cognizant.uicomponent.DisplayMappedComponentsController;
import com.cognizant.uicomponent.SalesforceTestCaseMappingConfig;
import com.cognizant.utility.Config;
import com.cognizant.utility.ExcelUtils;
import com.cognizant.utility.JsonUtils;
import com.cognizant.utility.SFDCContext;
import com.cognizant.utility.XMLUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import io.restassured.response.Response;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class SFDCandTestScriptNewMappingDocumentPage implements Initializable {
	private static final Logger log = Logger.getLogger(SFDCandTestScriptNewMappingDocumentPage.class);
	public int cnt= 1;
	ExcelUtils excelUtil = new ExcelUtils();
	JsonUtils jsonUtil = new JsonUtils();
	XMLUtils xmlUtil = new XMLUtils();
    //static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
    static final String RESPONSESERVICE = "/services/oauth2/authorize?response_type=token";
    static final String REDIRECT_URI = "https://login.salesforce.com/services/oauth2/success";
    public static String successURL = null;
    public static Map<String, String> successURLParams = new HashMap<>();
	ArrayList<String> InScopeItems = new ArrayList<String>(); 
	ObservableList<String> CreatedTestCaseList = FXCollections.observableArrayList();
	private String Mapping_TestCase_SourceDirectory = System.getProperty("user.dir")+"\\Mapping\\JSON\\Sources";
    HashMap<String, HashMap<String, ObservableList<String>>> FinalReviewHashMap = new HashMap<String, HashMap<String, ObservableList<String>>>();
    HashMap<String, HashMap<String, ObservableList<String>>> FinalSaveHashMap = new HashMap<String, HashMap<String, ObservableList<String>>>();
    
    Config config;
    int totalMappedComponentRows = 0;
	boolean componentExist = false;
    @FXML VBox MappingElementVBox;
    @FXML ScrollPane MappingElementScrollPane;
    @FXML Button AddTestCaseButton;
    @FXML Button RemoveTestCaseButton;
    @FXML Button InitialSaveMappingButton;
    @FXML Button ReviewMappingDetailsButton;
    @FXML Button SaveMappingDetailsButton;
    @FXML TextField MappingTestCaseName;
    @FXML ListView<String> MappedTestCaseListView;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		try {
			setListViewItems();
			VBox stackedTitledPanes = createStackedTitledPanes();
		    makeScrollable(stackedTitledPanes); 
		    addElementToMappedListView();
		    //uploadExistingMappingFile();
		    MappedTestCaseListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
		        @Override
		        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		            // Your action here
		        	log.info("Selected item = " + newValue);

		             String strTestCaseName = newValue;
				   	 HashMap<String, HashMap<String, ObservableList<String>>> SelectedTestCaseMappedComponents = new HashMap<String, HashMap<String, ObservableList<String>>>();
				   	 HashMap<String, ObservableList<String>> MappedComponentValues = new HashMap<String, ObservableList<String>>();
					 try {
			      	     ClearMappedItemListViewSelection();
					     File folder = new File(Mapping_TestCase_SourceDirectory);
					     File[] listOfFiles = folder.listFiles();
					     JSONParser jsonParser = new JSONParser();
						      for (int i = 0; i < listOfFiles.length; i++) {
						        if (listOfFiles[i].getName().contains(strTestCaseName)) {
						          log.info("File Found and File Name : " + listOfFiles[i].getName());
						          String fileAbsolutePath = listOfFiles[i].getAbsolutePath();
						          log.info("File Absolute Path : " + fileAbsolutePath);
					        	  HashMap<String, ObservableList<String>> IndividualTestCaseReviewDetail = new HashMap<String, ObservableList<String>>();
					        	  FileReader reader = new FileReader(fileAbsolutePath);

					              //Read JSON file
					              Object obj = jsonParser.parse(reader);
					              JSONObject individualTestCaseDetail = (JSONObject) obj;
					              log.info(individualTestCaseDetail);
					              String TestCaseName = (String) individualTestCaseDetail.get("TestCaseName");
					              JSONArray TotalRecords = (JSONArray) individualTestCaseDetail.get("records");

					              log.info("TestCaseName = "+TestCaseName); 
					              log.info("********************************"); 
					              for(int j = 0; j < TotalRecords.size() ; j ++) {
					            	  JSONObject MappedComponentDetail = (JSONObject) TotalRecords.get(j);
					            	  String ComponentType = (String) MappedComponentDetail.get("ComponentType");
					            	  JSONArray ComponentNames = (JSONArray) MappedComponentDetail.get("MappedComponentNames");
					            	  log.info("Component Type : "+ComponentType); 
					            	  log.info("Component Names : "+ComponentNames); 
						              ObservableList<String> observableComponentList = FXCollections.observableArrayList();
					            	  for (int l=0;l<ComponentNames.size();l++){ 
					            		  observableComponentList.add(ComponentNames.get(l).toString());
					            	  } 
					            	  MappedComponentValues.put(ComponentType, observableComponentList);
					              }
					              log.info("********************************"); 
					              log.info("********************************"); 
					              reader.close();
					              SelectedTestCaseMappedComponents.put(TestCaseName, MappedComponentValues);
						         }
						      } 
						  } catch (Exception e) {
								// TODO Auto-generated catch block
							  log.error(e.getMessage());
						  }
						  //Now Populating the ComponentTypes ListView based on Selected TestCase     
						  for (String i : SelectedTestCaseMappedComponents.keySet()) {
							   Map<String, ObservableList<String>> s = SelectedTestCaseMappedComponents.get(i) ;
							     //System.out.println("key :" + i + " value: " + s);
							     log.info("s.size() : "+s.size());
							     for(String j : s.keySet()) {
							    	ObservableList<String> k = s.get(j);
							    	System.out.println("First key : " + i + " Second Key : " + j +" value : "+k);
							    	switch(j) {
							    	   case "ApexClass" :
							    		  SalesforceTestCaseMappingConfig.ApexClassMappedLV.setItems(k);
							    	      break;
							    	   case "ApexComponent" :
							    		  SalesforceTestCaseMappingConfig.ApexComponentMappedLV.setItems(k);
							    	      break;
							    	   case "ApexPage" :
							    		   SalesforceTestCaseMappingConfig.ApexPageMappedLV.setItems(k);
								    	   break;
							    	   case "ApexTrigger" :
							    		   SalesforceTestCaseMappingConfig.ApexTriggerMappedLV.setItems(k);
								    	   break;
							    	   case "AssignmentRule" :
							    		   SalesforceTestCaseMappingConfig.AssignmentRuleMappedLV.setItems(k);
								    	   break;
							    	   case "AuraDefinition" :
							    		   SalesforceTestCaseMappingConfig.AuraDefinitionMappedLV.setItems(k);
								    	   break;
							    	   case "AutoResponseRule" :
							    		   SalesforceTestCaseMappingConfig.AutoResponseRuleMappedLV.setItems(k);
								    	   break;
							    	   case "BusinessProcess" :
							    		   SalesforceTestCaseMappingConfig.BusinessProcessMappedLV.setItems(k);
								    	   break;
							    	   case "CompactLayout" :
							    		   SalesforceTestCaseMappingConfig.CompactLayoutMappedLV.setItems(k);
								    	   break;
							    	   case "CustomApplication" :
							    		   SalesforceTestCaseMappingConfig.CustomApplicationMappedLV.setItems(k);
								    	   break;
							    	   case "CustomField" :
							    		   SalesforceTestCaseMappingConfig.CustomFieldMappedLV.setItems(k);
								    	   break;
							    	   case "CustomObject" :
							    		   SalesforceTestCaseMappingConfig.CustomObjectMappedLV.setItems(k);
								    	   break;
							    	   case "CustomTab" :
							    		   SalesforceTestCaseMappingConfig.CustomTabMappedLV.setItems(k);
								    	   break;
							    	   case "FieldMapping" :
							    		   SalesforceTestCaseMappingConfig.FieldMappingMappedLV.setItems(k);
								    	   break;
							    	   case "FieldSet" :
							    		   SalesforceTestCaseMappingConfig.FieldSetMappedLV.setItems(k);
								    	   break;
							    	   case "FlexiPage" :
							    		   SalesforceTestCaseMappingConfig.FlexiPageMappedLV.setItems(k);
								    	   break;
							    	   case "Flow" :
							    		   SalesforceTestCaseMappingConfig.FlowMappedLV.setItems(k);
								    	   break;
							    	   case "FlowDefinition" :
							    		   SalesforceTestCaseMappingConfig.FlowDefinitionMappedLV.setItems(k);
								    	   break;
							    	   case "HomePageComponent" :
							    		   SalesforceTestCaseMappingConfig.HomePageComponentMappedLV.setItems(k);
								    	   break;
							    	   case "HomePageLayout" :
							    		   SalesforceTestCaseMappingConfig.HomePageLayoutMappedLV.setItems(k);
								    	   break;
							    	   case "Layout" :
							    		   SalesforceTestCaseMappingConfig.LayoutMappedLV.setItems(k);
								    	   break;
							    	   case "PathAssistant" :
							    		   SalesforceTestCaseMappingConfig.PathAssistantMappedLV.setItems(k);
								    	   break;
							    	   case "PermissionSet" :
							    		   SalesforceTestCaseMappingConfig.PermissionSetMappedLV.setItems(k);
								    	   break;
							    	   case "PermissionSetGroup" :
							    		   SalesforceTestCaseMappingConfig.PermissionSetGroupMappedLV.setItems(k);
								    	   break;
							    	   case "Profile" :
							    		   SalesforceTestCaseMappingConfig.ProfileMappedLV.setItems(k);
								    	   break;
							    	   case "QuickActionDefinition" :
							    		   SalesforceTestCaseMappingConfig.QuickActionDefinitionMappedLV.setItems(k);
								    	   break;
							    	   case "RecordType" :
							    		   SalesforceTestCaseMappingConfig.RecordTypeMappedLV.setItems(k);
								    	   break;
							    	   case "UserRole" :
							    		   SalesforceTestCaseMappingConfig.UserRoleMappedLV.setItems(k);
								    	   break;
							    	   case "ValidationRule" :
							    		   SalesforceTestCaseMappingConfig.ValidationRuleMappedLV.setItems(k);
								    	   break;
							    	   case "WebLink" :
							    		   SalesforceTestCaseMappingConfig.WebLinkMappedLV.setItems(k);
								    	   break;  
							    	   default : 
							    	      // Statements
							    		  log.info("No Component Type found in JSON file");
							    		}
							     	}
						    	}
				        }
				    });
			}catch(Exception ex) {
				log.error(ex.getMessage());
			}
	}
    
    @FXML private void switchBackToHomePage() throws IOException {
    	log.info("Moving Back to Home Page");
        App.setRoot("AppHomePage");
    }
    
    @FXML private void switchBackToPreviousPage() throws IOException {
    	log.info("Moving Back to Previous Page");
        App.setRoot("SFDCandTestScriptMappingPage");
    }
    
    @FXML private void AddTestCase() throws Exception {
       log.info("Implement code for adding a test case...");
	   //Adding test case to the Listview
	   String strTestCaseName = MappingTestCaseName.getText();
	   log.info("Added Test Case Name = "+strTestCaseName);
	   if (strTestCaseName != null) {
	    	CreatedTestCaseList.add(strTestCaseName);
		    Collections.sort(CreatedTestCaseList);
		    MappedTestCaseListView.getSelectionModel().select(strTestCaseName);
	   }
	   MappedTestCaseListView.setItems(CreatedTestCaseList); 
	   ClearMappedItemListViewSelection();

    }

	@FXML private void RemoveTestCase() throws IOException {
		  log.info("Implement code for removing a test case...");
	      String removedTestCaseName = MappedTestCaseListView.getSelectionModel().getSelectedItem();
	      log.info("Removed Test Case Name = "+removedTestCaseName);
	      log.info("File Name : " + removedTestCaseName+".json");
	      log.info("File Absolute Path : " + Mapping_TestCase_SourceDirectory+"\\"+removedTestCaseName+".json");
          File file = new File(Mapping_TestCase_SourceDirectory+"\\"+removedTestCaseName+".json");
          if(file.delete()){
        	  log.info(Mapping_TestCase_SourceDirectory+"\\"+removedTestCaseName+".json File deleted successfully");
        	  CreatedTestCaseList.remove(removedTestCaseName);
    		  Collections.sort(CreatedTestCaseList);
          } else {
        	  log.info("Failed to delete the file : "+Mapping_TestCase_SourceDirectory+"\\"+removedTestCaseName+".json");
          }
    }
	    
    @FXML private void signOut() throws IOException {
    	App.setRoot("AppLauncherPage");
        //Platform.exit();
        //System.exit(0);
    }
	    
    @FXML private void SaveMappingDetails() throws Exception {
	  log.info("Implement code for Saving the mapping details...");
	  log.info("Mapping_TestCase_SourceDirectory : "+Mapping_TestCase_SourceDirectory);

      File folder = new File(Mapping_TestCase_SourceDirectory);
      File[] listOfFiles = folder.listFiles();
      JSONParser jsonParser = new JSONParser();
      try{
	      for (int i = 0; i < listOfFiles.length; i++) {
	        if (listOfFiles[i].isFile()) {
	        	log.info("File Name : " + listOfFiles[i].getName());
	            String fileAbsolutePath = Mapping_TestCase_SourceDirectory+"\\"+listOfFiles[i].getName();
	            log.info("File Absolute Path : " + fileAbsolutePath);

	        	  HashMap<String, ObservableList<String>> IndividualTestCaseSaveDetail = new HashMap<String, ObservableList<String>>();
	        	  FileReader reader = new FileReader(fileAbsolutePath);
	              //Read JSON file
	              Object obj = jsonParser.parse(reader);
	              JSONObject individualTestCaseDetail = (JSONObject) obj;
	              //log.info(individualTestCaseDetail);
	              String TestCaseName = (String) individualTestCaseDetail.get("TestCaseName");
	              JSONArray TotalRecords = (JSONArray) individualTestCaseDetail.get("records");

	              log.info("TestCaseName : "+TestCaseName); 
	              log.info("********************************"); 
	              for(int j = 0; j < TotalRecords.size() ; j ++) {
	            	  JSONObject MappedComponentDetail = (JSONObject) TotalRecords.get(j);
	            	  String ComponentType = (String) MappedComponentDetail.get("ComponentType");
	            	  JSONArray ComponentNames = (JSONArray) MappedComponentDetail.get("MappedComponentNames");
	            	  log.info("Component Type : "+ComponentType); 
	            	  log.info("Component Names : "+ComponentNames); 
		              ObservableList<String> observableComponentList = FXCollections.observableArrayList();
	            	  for (int l=0;l<ComponentNames.size();l++){ 
	            		  observableComponentList.add(ComponentNames.get(l).toString());
	            	  } 
		              IndividualTestCaseSaveDetail.put(ComponentType, observableComponentList);
	              }
	              log.info("********************************"); 
	              log.info("********************************"); 
	              reader.close();
	              FinalSaveHashMap.put(TestCaseName, IndividualTestCaseSaveDetail);
	    		  //Implement code to delete all test case source json file
	  			  File deleteFile = new File(fileAbsolutePath);
		          if(deleteFile.delete()){
		        	  log.info(fileAbsolutePath+" File deleted successfully");
		          } else {
		        	  log.info("Failed to delete the file : "+fileAbsolutePath);
		          }
	         }
	      } 
		  //Creating Final Mapping JSON File
		  jsonUtil.export(FinalSaveHashMap);
		  
      } catch (Exception e) {
    	  log.error(e.getMessage());
      }
	  MappingTestCaseName.clear();
    }
	    
    @FXML void InitialMappingDetailsSave() throws Exception {
    	 String strTestCaseName = null;
    	 if(!(MappedTestCaseListView.getSelectionModel().getSelectedItem().isEmpty()) || (MappedTestCaseListView.getSelectionModel().getSelectedItem() != null)
    			|| (MappedTestCaseListView.getSelectionModel().getSelectedItem() != "")) {
    		strTestCaseName = MappedTestCaseListView.getSelectionModel().getSelectedItem();
    	 }else {
    		strTestCaseName = MappingTestCaseName.getText();
    	 }

	   	 HashMap<String, HashMap<String, ObservableList<String>>> MappedTestCaseComponentsFinalMap = new HashMap<String, HashMap<String, ObservableList<String>>>();
	   	 HashMap<String, ObservableList<String>> FinalMappedValues = new HashMap<String, ObservableList<String>>();
	   	 if(SalesforceTestCaseMappingConfig.ApexClassMappedLV.getItems().size() > 0) {
	   		FinalMappedValues.put("ApexClass", SalesforceTestCaseMappingConfig.ApexClassMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.ApexComponentMappedLV.getItems().size() > 0) {
	   		FinalMappedValues.put("ApexComponent", SalesforceTestCaseMappingConfig.ApexComponentMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.ApexPageMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("ApexPage", SalesforceTestCaseMappingConfig.ApexPageMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.ApexTriggerMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("ApexTrigger", SalesforceTestCaseMappingConfig.ApexTriggerMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.AssignmentRuleMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("AssignmentRule", SalesforceTestCaseMappingConfig.AssignmentRuleMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.AuraDefinitionMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("AuraDefinition", SalesforceTestCaseMappingConfig.AuraDefinitionMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.AutoResponseRuleMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("AutoResponseRule", SalesforceTestCaseMappingConfig.AutoResponseRuleMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.BusinessProcessMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("BusinessProcess", SalesforceTestCaseMappingConfig.BusinessProcessMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.CompactLayoutMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("CompactLayout", SalesforceTestCaseMappingConfig.CompactLayoutMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.CustomApplicationMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("CustomApplication", SalesforceTestCaseMappingConfig.CustomApplicationMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.CustomFieldMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("CustomField", SalesforceTestCaseMappingConfig.CustomFieldMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.CustomObjectMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("CustomObject", SalesforceTestCaseMappingConfig.CustomObjectMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.CustomTabMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("CustomTab", SalesforceTestCaseMappingConfig.CustomTabMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.FieldMappingMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("FieldMapping", SalesforceTestCaseMappingConfig.FieldMappingMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.FieldSetMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("FieldSet", SalesforceTestCaseMappingConfig.FieldSetMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.FlexiPageMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("FlexiPage", SalesforceTestCaseMappingConfig.FlexiPageMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.FlowMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("Flow", SalesforceTestCaseMappingConfig.FlowMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.FlowDefinitionMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("FlowDefinition", SalesforceTestCaseMappingConfig.FlowDefinitionMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.HomePageComponentMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("HomePageComponent", SalesforceTestCaseMappingConfig.HomePageComponentMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.HomePageLayoutMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("HomePageLayout", SalesforceTestCaseMappingConfig.HomePageLayoutMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.LayoutMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("Layout", SalesforceTestCaseMappingConfig.LayoutMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.PathAssistantMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("PathAssistant", SalesforceTestCaseMappingConfig.PathAssistantMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.PermissionSetMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("PermissionSet", SalesforceTestCaseMappingConfig.PermissionSetMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.PermissionSetGroupMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("PermissionSetGroup", SalesforceTestCaseMappingConfig.PermissionSetGroupMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.ProfileMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("Profile", SalesforceTestCaseMappingConfig.ProfileMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.QuickActionDefinitionMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("QuickActionDefinition", SalesforceTestCaseMappingConfig.QuickActionDefinitionMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.RecordTypeMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("RecordType", SalesforceTestCaseMappingConfig.RecordTypeMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.UserRoleMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("UserRole", SalesforceTestCaseMappingConfig.UserRoleMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.ValidationRuleMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("ValidationRule", SalesforceTestCaseMappingConfig.ValidationRuleMappedLV.getItems());
	   	 }
	   	 if(SalesforceTestCaseMappingConfig.WebLinkMappedLV.getItems().size() > 0) {
		   	 FinalMappedValues.put("WebLink", SalesforceTestCaseMappingConfig.WebLinkMappedLV.getItems());
	   	 }
	   	 
	   	 //Updating the final HashMap with tescaseName and ComponentDetails
	   	 MappedTestCaseComponentsFinalMap.put(strTestCaseName,FinalMappedValues);
	   	 writeTestCaseJsonData(strTestCaseName, MappedTestCaseComponentsFinalMap);
	 }
	    
    @FXML private void ReviewMappingDetails() throws Exception {
    	  log.info("Implement code for Reviewing the final mapping details...");
    	  log.info("Mapping_TestCase_SourceDirectory : "+Mapping_TestCase_SourceDirectory);
	      if(DisplayMappedComponentsController.masterMappedData.size() > 0) {
	    		DisplayMappedComponentsController.masterMappedData.clear();
	    	}
	      File folder = new File(Mapping_TestCase_SourceDirectory);
	      File[] listOfFiles = folder.listFiles();
	      JSONParser jsonParser = new JSONParser();
          try{
		      for (int i = 0; i < listOfFiles.length; i++) {
		        if (listOfFiles[i].isFile()) {
		        	  log.info("File Name : " + listOfFiles[i].getName());
		              String fileAbsolutePath = Mapping_TestCase_SourceDirectory+"\\"+listOfFiles[i].getName();
		              log.info("File Absolute Path : " + fileAbsolutePath);
	
		        	  HashMap<String, ObservableList<String>> IndividualTestCaseReviewDetail = new HashMap<String, ObservableList<String>>();
		        	  FileReader reader = new FileReader(fileAbsolutePath);
		              //Read JSON file
		              Object obj = jsonParser.parse(reader);
		              JSONObject individualTestCaseDetail = (JSONObject) obj;
		              //System.out.println(individualTestCaseDetail);
		              String TestCaseName = (String) individualTestCaseDetail.get("TestCaseName");
		              JSONArray TotalRecords = (JSONArray) individualTestCaseDetail.get("records");

		              log.info("TestCaseName : "+TestCaseName); 
		              log.info("********************************"); 
		              for(int j = 0; j < TotalRecords.size() ; j ++) {
		            	  JSONObject MappedComponentDetail = (JSONObject) TotalRecords.get(j);
		            	  String ComponentType = (String) MappedComponentDetail.get("ComponentType");
		            	  JSONArray ComponentNames = (JSONArray) MappedComponentDetail.get("MappedComponentNames");
		            	  log.info("Component Type : "+ComponentType); 
		            	  log.info("Component Names : "+ComponentNames); 
			              ObservableList<String> observableComponentList = FXCollections.observableArrayList();
		            	  for (int l=0;l<ComponentNames.size();l++){ 
		            		  observableComponentList.add(ComponentNames.get(l).toString());
		            	  } 
			              IndividualTestCaseReviewDetail.put(ComponentType, observableComponentList);
		              }
		              log.info("********************************"); 
		              log.info("********************************"); 
		              reader.close();
		              FinalReviewHashMap.put(TestCaseName, IndividualTestCaseReviewDetail);
		         }
		      } 
			  for (String i : FinalReviewHashMap.keySet()) {
				   Map<String, ObservableList<String>> s = FinalReviewHashMap.get(i) ;
				      //System.out.println("key :" + i + " value: " + s);
				     log.info("s.size() : "+s.size());
				     for(String j : s.keySet()) {
				    	ObservableList<String> k = s.get(j);
				    	log.info("First key : " + i + " Second Key : " + j +" value : "+k);
				    	DisplayMappedComponentsController.masterMappedData.add(new DisplayMappedComponents(i, j, k));
				     }
				}
			    log.info("************************************");
			    //log.info(DisplayMappedComponentsController.masterMappedData);
				DisplayMappedComponentsController sfdc_dmcc = new DisplayMappedComponentsController();
		    	Stage primaryStage = new Stage();
		    	sfdc_dmcc.start(primaryStage);
          } catch (Exception e) {
              e.printStackTrace();
          }
    }
	    
    private void writeTestCaseJsonData(String testCaseName, HashMap<String, HashMap<String, ObservableList<String>>> MappedTestCaseComponentsFinalMap){
        try {

	    	File fileDir = new File(Mapping_TestCase_SourceDirectory);
	    	if(!fileDir.exists()) {
	    		fileDir.mkdirs();
	    	}
			//JSONObject jsonFinalResultObject = new JSONObject();
			//JSONArray jsonSecondaryResultArray = new JSONArray();
		    JSONObject TestCaseWiseResultDetails = new JSONObject();
			for (String i : MappedTestCaseComponentsFinalMap.keySet()) {
			   Map<String, ObservableList<String>> s = MappedTestCaseComponentsFinalMap.get(i) ;
			      //System.out.println("key :" + i + " value: " + s);
			   log.info("s.size() : "+s.size());

			     TestCaseWiseResultDetails.put("totalRecords", s.size());
			     TestCaseWiseResultDetails.put("TestCaseName", i);
				 JSONArray jsonResultArray = new JSONArray();
			     for(String j : s.keySet()) {
			    	ObservableList<String> k = s.get(j);
			    	log.info("First key : " + i + " Second Key : " + j +" value : "+k);
			    	JSONObject jsonResultsDetails = new JSONObject();
			    	jsonResultsDetails.put("ComponentType", j);
			    	jsonResultsDetails.put("MappedComponentNames", k);
			    	jsonResultArray.add(jsonResultsDetails);
			     }
			     TestCaseWiseResultDetails.put("records", jsonResultArray);
			} 
			//jsonFinalResultObject.put(TestCaseWiseResultDetails);
	
			Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
			JsonParser jp = new JsonParser(); 
			JsonElement je = jp.parse(TestCaseWiseResultDetails.toString());
			String prettyJsonString = gson.toJson(je); 
			//String file_name = "records.json";
	
			File deleteFile = new File(Mapping_TestCase_SourceDirectory+"\\"+testCaseName+".json");
	          if(deleteFile.delete()){
	        	  log.info(Mapping_TestCase_SourceDirectory+"\\"+testCaseName+".json File deleted successfully");
	          } else {
	        	  log.info("Failed to delete the file : "+Mapping_TestCase_SourceDirectory+"\\"+testCaseName+".json");
	          }
	          
	        FileOutputStream fileOutputStream = new FileOutputStream(new File(Mapping_TestCase_SourceDirectory+"\\"+testCaseName+".json"),true);
            fileOutputStream.write(prettyJsonString.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    public TitledPane createTitledPane(String title, ListView<String> listView, Button buttonName, ListView<String> mappedListView) {
	    HBox content = new HBox();
	    //Button add = new Button("Add");
	    HBox.setMargin(listView, new Insets(2));
	    HBox.setMargin(buttonName, new Insets(5));
	    content.setMinWidth(200.00);
	    content.setMinHeight(200.00);
	    content.getChildren().addAll(listView,buttonName,mappedListView);
	    content.setAlignment(Pos.CENTER);
	    
	    TitledPane pane = new TitledPane(title, content);
	    //pane.getStyleClass().add("stacked-titled-pane");
	    
	    pane.setExpanded(false);

	    return pane;
	  }

    private ScrollPane makeScrollable(final VBox node) {
	    MappingElementScrollPane.setContent(node);
	    MappingElementScrollPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
	      @Override public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
	        node.setPrefWidth(bounds.getWidth());
	      }
	    });
	    return MappingElementScrollPane;
	  }
    
    private VBox createStackedTitledPanes() {
	    //final VBox stackedTitledPanes = new VBox();
    	
    	MappingElementVBox.getChildren().setAll(
	      createTitledPane("ApexClass", SalesforceTestCaseMappingConfig.ApexClassLV, SalesforceTestCaseMappingConfig.ApexClassButton, SalesforceTestCaseMappingConfig.ApexClassMappedLV),
	      createTitledPane("ApexComponent", SalesforceTestCaseMappingConfig.ApexComponentLV,  SalesforceTestCaseMappingConfig.ApexComponentButton, SalesforceTestCaseMappingConfig.ApexComponentMappedLV),
	      createTitledPane("ApexPage", SalesforceTestCaseMappingConfig.ApexPageLV, SalesforceTestCaseMappingConfig.ApexPageButton, SalesforceTestCaseMappingConfig.ApexPageMappedLV),
	      createTitledPane("ApexTrigger", SalesforceTestCaseMappingConfig.ApexTriggerLV, SalesforceTestCaseMappingConfig.ApexTriggerButton, SalesforceTestCaseMappingConfig.ApexTriggerMappedLV),
	      createTitledPane("AssignmentRule", SalesforceTestCaseMappingConfig.AssignmentRuleLV, SalesforceTestCaseMappingConfig.AssignmentRuleButton, SalesforceTestCaseMappingConfig.AssignmentRuleMappedLV),
	      createTitledPane("AuraDefinition", SalesforceTestCaseMappingConfig.AuraDefinitionLV, SalesforceTestCaseMappingConfig.AuraDefinitionButton, SalesforceTestCaseMappingConfig.AuraDefinitionMappedLV),
	      createTitledPane("AutoResponseRule", SalesforceTestCaseMappingConfig.AutoResponseRuleLV, SalesforceTestCaseMappingConfig.AutoResponseRuleButton, SalesforceTestCaseMappingConfig.AutoResponseRuleMappedLV),
	      createTitledPane("BusinessProcess", SalesforceTestCaseMappingConfig.BusinessProcessLV, SalesforceTestCaseMappingConfig.BusinessProcessButton, SalesforceTestCaseMappingConfig.BusinessProcessMappedLV),
	      createTitledPane("CompactLayout", SalesforceTestCaseMappingConfig.CompactLayoutLV, SalesforceTestCaseMappingConfig.CompactLayoutButton, SalesforceTestCaseMappingConfig.CompactLayoutMappedLV),
	      createTitledPane("CustomApplication", SalesforceTestCaseMappingConfig.CustomApplicationLV, SalesforceTestCaseMappingConfig.CustomApplicationButton, SalesforceTestCaseMappingConfig.CustomApplicationMappedLV),
	      createTitledPane("CustomField", SalesforceTestCaseMappingConfig.CustomFieldLV, SalesforceTestCaseMappingConfig.CustomFieldButton, SalesforceTestCaseMappingConfig.CustomFieldMappedLV),
	      createTitledPane("CustomObject", SalesforceTestCaseMappingConfig.CustomObjectLV, SalesforceTestCaseMappingConfig.CustomObjectButton, SalesforceTestCaseMappingConfig.CustomObjectMappedLV),
	      createTitledPane("CustomTab", SalesforceTestCaseMappingConfig.CustomTabLV, SalesforceTestCaseMappingConfig.CustomTabButton, SalesforceTestCaseMappingConfig.CustomTabMappedLV),
	      createTitledPane("FieldMapping", SalesforceTestCaseMappingConfig.FieldMappingLV, SalesforceTestCaseMappingConfig.FieldMappingButton, SalesforceTestCaseMappingConfig.FieldMappingMappedLV),
	      createTitledPane("FieldSet", SalesforceTestCaseMappingConfig.FieldSetLV, SalesforceTestCaseMappingConfig.FieldSetButton, SalesforceTestCaseMappingConfig.FieldSetMappedLV),
	      createTitledPane("FlexiPage", SalesforceTestCaseMappingConfig.FlexiPageLV, SalesforceTestCaseMappingConfig.FlexiPageButton, SalesforceTestCaseMappingConfig.FlexiPageMappedLV),
	      createTitledPane("Flow", SalesforceTestCaseMappingConfig.FlowLV, SalesforceTestCaseMappingConfig.FlowButton, SalesforceTestCaseMappingConfig.FlowMappedLV),
	      createTitledPane("FlowDefinition", SalesforceTestCaseMappingConfig.FlowDefinitionLV, SalesforceTestCaseMappingConfig.FlowDefinitionButton, SalesforceTestCaseMappingConfig.FlowDefinitionMappedLV),
	      createTitledPane("HomePageComponent", SalesforceTestCaseMappingConfig.HomePageComponentLV, SalesforceTestCaseMappingConfig.HomePageComponentButton, SalesforceTestCaseMappingConfig.HomePageComponentMappedLV),
	      createTitledPane("HomePageLayout", SalesforceTestCaseMappingConfig.HomePageLayoutLV, SalesforceTestCaseMappingConfig.HomePageLayoutButton, SalesforceTestCaseMappingConfig.HomePageLayoutMappedLV),
	      createTitledPane("Layout", SalesforceTestCaseMappingConfig.LayoutLV, SalesforceTestCaseMappingConfig.LayoutButton, SalesforceTestCaseMappingConfig.LayoutMappedLV),
	      createTitledPane("PathAssistant", SalesforceTestCaseMappingConfig.PathAssistantLV, SalesforceTestCaseMappingConfig.PathAssistantButton, SalesforceTestCaseMappingConfig.PathAssistantMappedLV),
	      createTitledPane("PermissionSet", SalesforceTestCaseMappingConfig.PermissionSetLV, SalesforceTestCaseMappingConfig.PermissionSetButton, SalesforceTestCaseMappingConfig.PermissionSetMappedLV),
	      createTitledPane("PermissionSetGroup", SalesforceTestCaseMappingConfig.PermissionSetGroupLV, SalesforceTestCaseMappingConfig.PermissionSetGroupButton, SalesforceTestCaseMappingConfig.PermissionSetGroupMappedLV),
	      createTitledPane("Profile", SalesforceTestCaseMappingConfig.ProfileLV, SalesforceTestCaseMappingConfig.ProfileButton, SalesforceTestCaseMappingConfig.ProfileMappedLV),
	      createTitledPane("QuickActionDefinition", SalesforceTestCaseMappingConfig.QuickActionDefinitionLV, SalesforceTestCaseMappingConfig.QuickActionDefinitionButton, SalesforceTestCaseMappingConfig.QuickActionDefinitionMappedLV),
	      createTitledPane("RecordType", SalesforceTestCaseMappingConfig.RecordTypeLV, SalesforceTestCaseMappingConfig.RecordTypeButton, SalesforceTestCaseMappingConfig.RecordTypeMappedLV),
	      createTitledPane("UserRole", SalesforceTestCaseMappingConfig.UserRoleLV, SalesforceTestCaseMappingConfig.UserRoleButton, SalesforceTestCaseMappingConfig.UserRoleMappedLV),
	      createTitledPane("ValidationRule", SalesforceTestCaseMappingConfig.ValidationRuleLV, SalesforceTestCaseMappingConfig.ValidationRuleButton, SalesforceTestCaseMappingConfig.ValidationRuleMappedLV),
	      createTitledPane("WebLink", SalesforceTestCaseMappingConfig.WebLinkLV, SalesforceTestCaseMappingConfig.WebLinkButton, SalesforceTestCaseMappingConfig.WebLinkMappedLV)
	    );
	    ((TitledPane) MappingElementVBox.getChildren().get(0)).setExpanded(true);
	    //stackedTitledPanes.getStyleClass().add("stacked-titled-panes");

	    return MappingElementVBox;
	  }
    
    private void addElementToMappedListView() throws Exception{

    	SalesforceTestCaseMappingConfig.ApexClassButton.setOnAction((ActionEvent event) -> {
	    	  SalesforceTestCaseMappingConfig.ApexClassMappedComponents = SalesforceTestCaseMappingConfig.ApexClassLV.getSelectionModel().getSelectedItems();
	  	      if (SalesforceTestCaseMappingConfig.ApexClassMappedComponents.size() > 0) {
	  	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.ApexClassMappedComponents);
		          SalesforceTestCaseMappingConfig.ApexClassMappedLV.setItems(SalesforceTestCaseMappingConfig.ApexClassMappedComponents);

	  	      }
	        });
    	
    	SalesforceTestCaseMappingConfig.ApexComponentButton.setOnAction((ActionEvent event) -> {
	    	  SalesforceTestCaseMappingConfig.ApexComponentMappedComponents = SalesforceTestCaseMappingConfig.ApexComponentLV.getSelectionModel().getSelectedItems();
	  	      if (SalesforceTestCaseMappingConfig.ApexComponentMappedComponents.size() > 0) {
	  	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.ApexComponentMappedComponents);
		          SalesforceTestCaseMappingConfig.ApexComponentMappedLV.setItems(SalesforceTestCaseMappingConfig.ApexComponentMappedComponents);
	  	      }
	        });

    	SalesforceTestCaseMappingConfig.ApexPageButton.setOnAction((ActionEvent event) -> {
	    	  SalesforceTestCaseMappingConfig.ApexPageMappedComponents = SalesforceTestCaseMappingConfig.ApexPageLV.getSelectionModel().getSelectedItems();
	  	      if (SalesforceTestCaseMappingConfig.ApexPageMappedComponents.size() > 0) {
	  	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.ApexPageMappedComponents);
		          SalesforceTestCaseMappingConfig.ApexPageMappedLV.setItems(SalesforceTestCaseMappingConfig.ApexPageMappedComponents);
	  	      }
	        });
    	
    	SalesforceTestCaseMappingConfig.ApexTriggerButton.setOnAction((ActionEvent event) -> {
      	  SalesforceTestCaseMappingConfig.ApexTriggerMappedComponents = SalesforceTestCaseMappingConfig.ApexTriggerLV.getSelectionModel().getSelectedItems();
    	      if (SalesforceTestCaseMappingConfig.ApexTriggerMappedComponents.size() > 0) {
    	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.ApexTriggerMappedComponents); 
		          SalesforceTestCaseMappingConfig.ApexTriggerMappedLV.setItems(SalesforceTestCaseMappingConfig.ApexTriggerMappedComponents);
    	      }
  	        });
    	
    	SalesforceTestCaseMappingConfig.AssignmentRuleButton.setOnAction((ActionEvent event) -> {
        	  SalesforceTestCaseMappingConfig.AssignmentRuleMappedComponents = SalesforceTestCaseMappingConfig.AssignmentRuleLV.getSelectionModel().getSelectedItems();
      	      if (SalesforceTestCaseMappingConfig.AssignmentRuleMappedComponents.size() > 0) {
      	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.AssignmentRuleMappedComponents);
		          SalesforceTestCaseMappingConfig.AssignmentRuleMappedLV.setItems(SalesforceTestCaseMappingConfig.AssignmentRuleMappedComponents);
      	      }
    	    });
    	
    	SalesforceTestCaseMappingConfig.AuraDefinitionButton.setOnAction((ActionEvent event) -> {
      	  SalesforceTestCaseMappingConfig.AuraDefinitionMappedComponents = SalesforceTestCaseMappingConfig.AuraDefinitionLV.getSelectionModel().getSelectedItems();
    	      if (SalesforceTestCaseMappingConfig.AuraDefinitionMappedComponents.size() > 0) {
    	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.AuraDefinitionMappedComponents);
		          SalesforceTestCaseMappingConfig.AuraDefinitionMappedLV.setItems(SalesforceTestCaseMappingConfig.AuraDefinitionMappedComponents);
    	      }
  	        });
    	
    	SalesforceTestCaseMappingConfig.AutoResponseRuleButton.setOnAction((ActionEvent event) -> {
        	  SalesforceTestCaseMappingConfig.AutoResponseRuleMappedComponents = SalesforceTestCaseMappingConfig.AutoResponseRuleLV.getSelectionModel().getSelectedItems();
      	      if (SalesforceTestCaseMappingConfig.AutoResponseRuleMappedComponents.size() > 0) {
      	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.AutoResponseRuleMappedComponents);
		          SalesforceTestCaseMappingConfig.AutoResponseRuleMappedLV.setItems(SalesforceTestCaseMappingConfig.AutoResponseRuleMappedComponents);
      	      }
    	    });
    	
    	SalesforceTestCaseMappingConfig.AuraDefinitionButton.setOnAction((ActionEvent event) -> {
        	  SalesforceTestCaseMappingConfig.AuraDefinitionMappedComponents = SalesforceTestCaseMappingConfig.AuraDefinitionLV.getSelectionModel().getSelectedItems();
      	      if (SalesforceTestCaseMappingConfig.AuraDefinitionMappedComponents.size() > 0) {
      	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.AuraDefinitionMappedComponents);
		          SalesforceTestCaseMappingConfig.AuraDefinitionMappedLV.setItems(SalesforceTestCaseMappingConfig.AuraDefinitionMappedComponents);
      	      }
    	    });
    	
    	SalesforceTestCaseMappingConfig.BusinessProcessButton.setOnAction((ActionEvent event) -> {
        	  SalesforceTestCaseMappingConfig.BusinessProcessMappedComponents = SalesforceTestCaseMappingConfig.BusinessProcessLV.getSelectionModel().getSelectedItems();
      	      if (SalesforceTestCaseMappingConfig.BusinessProcessMappedComponents.size() > 0) {
      	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.BusinessProcessMappedComponents);
		          SalesforceTestCaseMappingConfig.BusinessProcessMappedLV.setItems(SalesforceTestCaseMappingConfig.BusinessProcessMappedComponents);
      	      }
    	    });
    	
    	SalesforceTestCaseMappingConfig.CompactLayoutButton.setOnAction((ActionEvent event) -> {
      	  SalesforceTestCaseMappingConfig.CompactLayoutMappedComponents = SalesforceTestCaseMappingConfig.CompactLayoutLV.getSelectionModel().getSelectedItems();
    	      if (SalesforceTestCaseMappingConfig.CompactLayoutMappedComponents.size() > 0) {
    	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.CompactLayoutMappedComponents);
		          SalesforceTestCaseMappingConfig.CompactLayoutMappedLV.setItems(SalesforceTestCaseMappingConfig.CompactLayoutMappedComponents);
    	      }
  	        });
    	
    	SalesforceTestCaseMappingConfig.CustomApplicationButton.setOnAction((ActionEvent event) -> {
      	  SalesforceTestCaseMappingConfig.CustomApplicationMappedComponents = SalesforceTestCaseMappingConfig.CustomApplicationLV.getSelectionModel().getSelectedItems();
    	      if (SalesforceTestCaseMappingConfig.CustomApplicationMappedComponents.size() > 0) {
    	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.CustomApplicationMappedComponents);
		          SalesforceTestCaseMappingConfig.CustomApplicationMappedLV.setItems(SalesforceTestCaseMappingConfig.CustomApplicationMappedComponents);
    	      }
  	        });
    	
    	SalesforceTestCaseMappingConfig.CustomFieldButton.setOnAction((ActionEvent event) -> {
      	  SalesforceTestCaseMappingConfig.CustomFieldMappedComponents = SalesforceTestCaseMappingConfig.CustomFieldLV.getSelectionModel().getSelectedItems();
    	      if (SalesforceTestCaseMappingConfig.CustomFieldMappedComponents.size() > 0) {
    	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.CustomFieldMappedComponents);
		          SalesforceTestCaseMappingConfig.CustomFieldMappedLV.setItems(SalesforceTestCaseMappingConfig.CustomFieldMappedComponents);
    	      }
  	        });
    	
    	SalesforceTestCaseMappingConfig.CustomObjectButton.setOnAction((ActionEvent event) -> {
        	  SalesforceTestCaseMappingConfig.CustomObjectMappedComponents = SalesforceTestCaseMappingConfig.CustomObjectLV.getSelectionModel().getSelectedItems();
      	      if (SalesforceTestCaseMappingConfig.CustomObjectMappedComponents.size() > 0) {
      	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.CustomObjectMappedComponents);
		          SalesforceTestCaseMappingConfig.CustomObjectMappedLV.setItems(SalesforceTestCaseMappingConfig.CustomObjectMappedComponents);
      	      }
    	    });
    	
    	SalesforceTestCaseMappingConfig.CustomTabButton.setOnAction((ActionEvent event) -> {
        	  SalesforceTestCaseMappingConfig.CustomTabMappedComponents = SalesforceTestCaseMappingConfig.CustomTabLV.getSelectionModel().getSelectedItems();
      	      if (SalesforceTestCaseMappingConfig.CustomTabMappedComponents.size() > 0) {
      	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.CustomTabMappedComponents);
		          SalesforceTestCaseMappingConfig.CustomTabMappedLV.setItems(SalesforceTestCaseMappingConfig.CustomTabMappedComponents);
      	      }
    	    });
    	
    	SalesforceTestCaseMappingConfig.FieldMappingButton.setOnAction((ActionEvent event) -> {
        	  SalesforceTestCaseMappingConfig.FieldMappingMappedComponents = SalesforceTestCaseMappingConfig.FieldMappingLV.getSelectionModel().getSelectedItems();
      	      if (SalesforceTestCaseMappingConfig.FieldMappingMappedComponents.size() > 0) {
      	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.FieldMappingMappedComponents);
		          SalesforceTestCaseMappingConfig.FieldMappingMappedLV.setItems(SalesforceTestCaseMappingConfig.FieldMappingMappedComponents);
      	      }
    	    });
    	
    	SalesforceTestCaseMappingConfig.FieldSetButton.setOnAction((ActionEvent event) -> {
        	  SalesforceTestCaseMappingConfig.FieldSetMappedComponents = SalesforceTestCaseMappingConfig.FieldSetLV.getSelectionModel().getSelectedItems();
      	      if (SalesforceTestCaseMappingConfig.FieldSetMappedComponents.size() > 0) {
      	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.FieldSetMappedComponents);
		          SalesforceTestCaseMappingConfig.FieldSetMappedLV.setItems(SalesforceTestCaseMappingConfig.FieldSetMappedComponents);
      	      }
    	    });
    	
    	SalesforceTestCaseMappingConfig.FlexiPageButton.setOnAction((ActionEvent event) -> {
        	  SalesforceTestCaseMappingConfig.FlexiPageMappedComponents = SalesforceTestCaseMappingConfig.FlexiPageLV.getSelectionModel().getSelectedItems();
      	      if (SalesforceTestCaseMappingConfig.FlexiPageMappedComponents.size() > 0) {
      	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.FlexiPageMappedComponents);
		          SalesforceTestCaseMappingConfig.FlexiPageMappedLV.setItems(SalesforceTestCaseMappingConfig.FlexiPageMappedComponents);
      	      }
    	    });
    	
    	SalesforceTestCaseMappingConfig.FlowButton.setOnAction((ActionEvent event) -> {
      	  SalesforceTestCaseMappingConfig.FlowMappedComponents = SalesforceTestCaseMappingConfig.FlowLV.getSelectionModel().getSelectedItems();
    	      if (SalesforceTestCaseMappingConfig.FlowMappedComponents.size() > 0) {
    	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.FlowMappedComponents);
		          SalesforceTestCaseMappingConfig.FlowMappedLV.setItems(SalesforceTestCaseMappingConfig.FlowMappedComponents);
    	      }
  	        });
    	
    	SalesforceTestCaseMappingConfig.FlowDefinitionButton.setOnAction((ActionEvent event) -> {
      	  SalesforceTestCaseMappingConfig.FlowDefinitionMappedComponents = SalesforceTestCaseMappingConfig.FlowDefinitionLV.getSelectionModel().getSelectedItems();
    	      if (SalesforceTestCaseMappingConfig.FlowDefinitionMappedComponents.size() > 0) {
    	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.FlowDefinitionMappedComponents);
		          SalesforceTestCaseMappingConfig.FlowDefinitionMappedLV.setItems(SalesforceTestCaseMappingConfig.FlowDefinitionMappedComponents);
    	      }
  	        });
    	
    	SalesforceTestCaseMappingConfig.HomePageComponentButton.setOnAction((ActionEvent event) -> {
      	  SalesforceTestCaseMappingConfig.HomePageComponentMappedComponents = SalesforceTestCaseMappingConfig.HomePageComponentLV.getSelectionModel().getSelectedItems();
    	      if (SalesforceTestCaseMappingConfig.HomePageComponentMappedComponents.size() > 0) {
    	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.HomePageComponentMappedComponents);
		          SalesforceTestCaseMappingConfig.HomePageComponentMappedLV.setItems(SalesforceTestCaseMappingConfig.HomePageComponentMappedComponents);
    	      }
  	        });
    	
    	SalesforceTestCaseMappingConfig.HomePageLayoutButton.setOnAction((ActionEvent event) -> {
      	  SalesforceTestCaseMappingConfig.HomePageLayoutMappedComponents = SalesforceTestCaseMappingConfig.HomePageLayoutLV.getSelectionModel().getSelectedItems();
    	      if (SalesforceTestCaseMappingConfig.HomePageLayoutMappedComponents.size() > 0) {
    	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.HomePageLayoutMappedComponents);
		          SalesforceTestCaseMappingConfig.HomePageLayoutMappedLV.setItems(SalesforceTestCaseMappingConfig.HomePageLayoutMappedComponents);
    	      }
  	        });
    	
    	SalesforceTestCaseMappingConfig.LayoutButton.setOnAction((ActionEvent event) -> {
      	  SalesforceTestCaseMappingConfig.LayoutMappedComponents = SalesforceTestCaseMappingConfig.LayoutLV.getSelectionModel().getSelectedItems();
    	      if (SalesforceTestCaseMappingConfig.LayoutMappedComponents.size() > 0) {
    	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.LayoutMappedComponents);
		          SalesforceTestCaseMappingConfig.LayoutMappedLV.setItems(SalesforceTestCaseMappingConfig.LayoutMappedComponents);
    	      }
  	        });
    	
    	SalesforceTestCaseMappingConfig.PathAssistantButton.setOnAction((ActionEvent event) -> {
        	  SalesforceTestCaseMappingConfig.PathAssistantMappedComponents = SalesforceTestCaseMappingConfig.PathAssistantLV.getSelectionModel().getSelectedItems();
      	      if (SalesforceTestCaseMappingConfig.PathAssistantMappedComponents.size() > 0) {
      	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.PathAssistantMappedComponents);
		          SalesforceTestCaseMappingConfig.PathAssistantMappedLV.setItems(SalesforceTestCaseMappingConfig.PathAssistantMappedComponents);
      	      }
    	    });
    	
    	SalesforceTestCaseMappingConfig.PermissionSetButton.setOnAction((ActionEvent event) -> {
        	  SalesforceTestCaseMappingConfig.PermissionSetMappedComponents = SalesforceTestCaseMappingConfig.PermissionSetLV.getSelectionModel().getSelectedItems();
      	      if (SalesforceTestCaseMappingConfig.PermissionSetMappedComponents.size() > 0) {
      	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.PermissionSetMappedComponents);
		          SalesforceTestCaseMappingConfig.PermissionSetMappedLV.setItems(SalesforceTestCaseMappingConfig.PermissionSetMappedComponents);
      	      }
    	    });
    	
    	SalesforceTestCaseMappingConfig.PermissionSetGroupButton.setOnAction((ActionEvent event) -> {
        	  SalesforceTestCaseMappingConfig.PermissionSetGroupMappedComponents = SalesforceTestCaseMappingConfig.PermissionSetGroupLV.getSelectionModel().getSelectedItems();
      	      if (SalesforceTestCaseMappingConfig.PermissionSetGroupMappedComponents.size() > 0) {
      	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.PermissionSetGroupMappedComponents);
		          SalesforceTestCaseMappingConfig.PermissionSetGroupMappedLV.setItems(SalesforceTestCaseMappingConfig.PermissionSetGroupMappedComponents);
      	      }
    	    });
    	
    	SalesforceTestCaseMappingConfig.ProfileButton.setOnAction((ActionEvent event) -> {
        	  SalesforceTestCaseMappingConfig.ProfileMappedComponents = SalesforceTestCaseMappingConfig.ProfileLV.getSelectionModel().getSelectedItems();
      	      if (SalesforceTestCaseMappingConfig.ProfileMappedComponents.size() > 0) {
      	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.ProfileMappedComponents);
		          SalesforceTestCaseMappingConfig.ProfileMappedLV.setItems(SalesforceTestCaseMappingConfig.ProfileMappedComponents);
      	      }
    	    });
    	
    	SalesforceTestCaseMappingConfig.QuickActionDefinitionButton.setOnAction((ActionEvent event) -> {
        	  SalesforceTestCaseMappingConfig.QuickActionDefinitionMappedComponents = SalesforceTestCaseMappingConfig.QuickActionDefinitionLV.getSelectionModel().getSelectedItems();
      	      if (SalesforceTestCaseMappingConfig.QuickActionDefinitionMappedComponents.size() > 0) {
      	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.QuickActionDefinitionMappedComponents);
		          SalesforceTestCaseMappingConfig.QuickActionDefinitionMappedLV.setItems(SalesforceTestCaseMappingConfig.QuickActionDefinitionMappedComponents);
      	      }
    	    });
    	
    	SalesforceTestCaseMappingConfig.RecordTypeButton.setOnAction((ActionEvent event) -> {
      	  SalesforceTestCaseMappingConfig.RecordTypeMappedComponents = SalesforceTestCaseMappingConfig.RecordTypeLV.getSelectionModel().getSelectedItems();
    	      if (SalesforceTestCaseMappingConfig.RecordTypeMappedComponents.size() > 0) {
    	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.RecordTypeMappedComponents);
		          SalesforceTestCaseMappingConfig.RecordTypeMappedLV.setItems(SalesforceTestCaseMappingConfig.RecordTypeMappedComponents);
    	      }
  	        });
    	
    	SalesforceTestCaseMappingConfig.UserRoleButton.setOnAction((ActionEvent event) -> {
      	  SalesforceTestCaseMappingConfig.UserRoleMappedComponents = SalesforceTestCaseMappingConfig.UserRoleLV.getSelectionModel().getSelectedItems();
    	      if (SalesforceTestCaseMappingConfig.UserRoleMappedComponents.size() > 0) {
    	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.UserRoleMappedComponents);
		          SalesforceTestCaseMappingConfig.UserRoleMappedLV.setItems(SalesforceTestCaseMappingConfig.UserRoleMappedComponents);
    	      }
  	        });
    	
    	SalesforceTestCaseMappingConfig.ValidationRuleButton.setOnAction((ActionEvent event) -> {
      	  SalesforceTestCaseMappingConfig.ValidationRuleMappedComponents = SalesforceTestCaseMappingConfig.ValidationRuleLV.getSelectionModel().getSelectedItems();
    	      if (SalesforceTestCaseMappingConfig.ValidationRuleMappedComponents.size() > 0) {
    	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.ValidationRuleMappedComponents);
		          SalesforceTestCaseMappingConfig.ValidationRuleMappedLV.setItems(SalesforceTestCaseMappingConfig.ValidationRuleMappedComponents);
    	      }
  	        });
    	
    	SalesforceTestCaseMappingConfig.WebLinkButton.setOnAction((ActionEvent event) -> {
      	  SalesforceTestCaseMappingConfig.WebLinkMappedComponents = SalesforceTestCaseMappingConfig.WebLinkLV.getSelectionModel().getSelectedItems();
    	      if (SalesforceTestCaseMappingConfig.WebLinkMappedComponents.size() > 0) {
    	    	  log.info("Inside If Condition : "+SalesforceTestCaseMappingConfig.WebLinkMappedComponents);
		          SalesforceTestCaseMappingConfig.WebLinkMappedLV.setItems(SalesforceTestCaseMappingConfig.WebLinkMappedComponents);
    	      }
  	        });
    }
    
    private void ResetElementToMappedListView() {
		// TODO Auto-generated method stub
    	SalesforceTestCaseMappingConfig.ApexClassMappedLV.getItems().clear();
    	SalesforceTestCaseMappingConfig.ApexComponentMappedLV.getItems().clear();
    	SalesforceTestCaseMappingConfig.ApexPageMappedLV.getItems().clear();
    	SalesforceTestCaseMappingConfig.ApexTriggerMappedLV.getItems().clear();
	}

    public void setListViewItems() throws Exception{
    	ObservableList<String> ObservableList_ApexClassLVList = FXCollections.observableArrayList(Config.SFDC_ApexClassLV_ItemsList);
		SalesforceTestCaseMappingConfig.ApexClassLV.setItems(ObservableList_ApexClassLVList);
		SalesforceTestCaseMappingConfig.ApexClassLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_ApexComponentLVList = FXCollections.observableArrayList(Config.SFDC_ApexComponentLV_ItemsList);
		SalesforceTestCaseMappingConfig.ApexComponentLV.setItems(ObservableList_ApexComponentLVList);
		SalesforceTestCaseMappingConfig.ApexComponentLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_ApexPageLVList = FXCollections.observableArrayList(Config.SFDC_ApexPageLV_ItemsList);
		SalesforceTestCaseMappingConfig.ApexPageLV.setItems(ObservableList_ApexPageLVList);
		SalesforceTestCaseMappingConfig.ApexPageLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_ApexTriggerLVList = FXCollections.observableArrayList(Config.SFDC_ApexTriggerLV_ItemsList);
		SalesforceTestCaseMappingConfig.ApexTriggerLV.setItems(ObservableList_ApexTriggerLVList);
		SalesforceTestCaseMappingConfig.ApexTriggerLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_AssignmentRuleLVList = FXCollections.observableArrayList(Config.SFDC_AssignmentRuleLV_ItemsList);
		SalesforceTestCaseMappingConfig.AssignmentRuleLV.setItems(ObservableList_AssignmentRuleLVList);
		SalesforceTestCaseMappingConfig.AssignmentRuleLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_AuraDefinitionLVList = FXCollections.observableArrayList(Config.SFDC_AuraDefinitionLV_ItemsList);
		SalesforceTestCaseMappingConfig.AuraDefinitionLV.setItems(ObservableList_AuraDefinitionLVList);
		SalesforceTestCaseMappingConfig.AuraDefinitionLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_AutoResponseRuleLVList = FXCollections.observableArrayList(Config.SFDC_AutoResponseRuleLV_ItemsList);
		SalesforceTestCaseMappingConfig.AutoResponseRuleLV.setItems(ObservableList_AutoResponseRuleLVList);
		SalesforceTestCaseMappingConfig.AutoResponseRuleLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_BusinessProcessLVList = FXCollections.observableArrayList(Config.SFDC_BusinessProcessLV_ItemsList);
		SalesforceTestCaseMappingConfig.BusinessProcessLV.setItems(ObservableList_BusinessProcessLVList);
		SalesforceTestCaseMappingConfig.BusinessProcessLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_CompactLayoutLVList = FXCollections.observableArrayList(Config.SFDC_CompactLayoutLV_ItemsList);
		SalesforceTestCaseMappingConfig.CompactLayoutLV.setItems(ObservableList_CompactLayoutLVList);
		SalesforceTestCaseMappingConfig.CompactLayoutLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_CustomApplicationLVList = FXCollections.observableArrayList(Config.SFDC_CustomApplicationLV_ItemsList);
		SalesforceTestCaseMappingConfig.CustomApplicationLV.setItems(ObservableList_CustomApplicationLVList);
		SalesforceTestCaseMappingConfig.CustomApplicationLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_CustomFieldLVList = FXCollections.observableArrayList(Config.SFDC_CustomFieldLV_ItemsList);
		SalesforceTestCaseMappingConfig.CustomFieldLV.setItems(ObservableList_CustomFieldLVList);
		SalesforceTestCaseMappingConfig.CustomFieldLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_CustomObjectLVList = FXCollections.observableArrayList(Config.SFDC_CustomObjectLV_ItemsList);
		SalesforceTestCaseMappingConfig.CustomObjectLV.setItems(ObservableList_CustomObjectLVList);
		SalesforceTestCaseMappingConfig.CustomObjectLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_CustomTabLVList = FXCollections.observableArrayList(Config.SFDC_CustomTabLV_ItemsList);
		SalesforceTestCaseMappingConfig.CustomTabLV.setItems(ObservableList_CustomTabLVList);
		SalesforceTestCaseMappingConfig.CustomTabLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_FieldMappingLVList = FXCollections.observableArrayList(Config.SFDC_FieldMappingLV_ItemsList);
		SalesforceTestCaseMappingConfig.FieldMappingLV.setItems(ObservableList_FieldMappingLVList);
		SalesforceTestCaseMappingConfig.FieldMappingLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_FieldSetLVList = FXCollections.observableArrayList(Config.SFDC_FieldSetLV_ItemsList);
		SalesforceTestCaseMappingConfig.FieldSetLV.setItems(ObservableList_FieldSetLVList);
		SalesforceTestCaseMappingConfig.FieldSetLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_FlexiPageLVList = FXCollections.observableArrayList(Config.SFDC_FlexiPageLV_ItemsList);
		SalesforceTestCaseMappingConfig.FlexiPageLV.setItems(ObservableList_FlexiPageLVList);
		SalesforceTestCaseMappingConfig.FlexiPageLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_FlowLVList = FXCollections.observableArrayList(Config.SFDC_FlowLV_ItemsList);
		SalesforceTestCaseMappingConfig.FlowLV.setItems(ObservableList_FlowLVList);
		SalesforceTestCaseMappingConfig.FlowLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_FlowDefinitionLVList = FXCollections.observableArrayList(Config.SFDC_FlowDefinitionLV_ItemsList);
		SalesforceTestCaseMappingConfig.FlowDefinitionLV.setItems(ObservableList_FlowDefinitionLVList);
		SalesforceTestCaseMappingConfig.FlowDefinitionLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_HomePageComponentLVList = FXCollections.observableArrayList(Config.SFDC_HomePageComponentLV_ItemsList);
		SalesforceTestCaseMappingConfig.HomePageComponentLV.setItems(ObservableList_HomePageComponentLVList);
		SalesforceTestCaseMappingConfig.HomePageComponentLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_HomePageLayoutLVList = FXCollections.observableArrayList(Config.SFDC_HomePageLayoutLV_ItemsList);
		SalesforceTestCaseMappingConfig.HomePageLayoutLV.setItems(ObservableList_HomePageLayoutLVList);
		SalesforceTestCaseMappingConfig.HomePageLayoutLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_LayoutLVList = FXCollections.observableArrayList(Config.SFDC_LayoutLV_ItemsList);
		SalesforceTestCaseMappingConfig.LayoutLV.setItems(ObservableList_LayoutLVList);
		SalesforceTestCaseMappingConfig.LayoutLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_PathAssistantLVList = FXCollections.observableArrayList(Config.SFDC_PathAssistantLV_ItemsList);
		SalesforceTestCaseMappingConfig.PathAssistantLV.setItems(ObservableList_PathAssistantLVList);
		SalesforceTestCaseMappingConfig.PathAssistantLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_PermissionSetLVList = FXCollections.observableArrayList(Config.SFDC_PermissionSetLV_ItemsList);
		SalesforceTestCaseMappingConfig.PermissionSetLV.setItems(ObservableList_PermissionSetLVList);
		SalesforceTestCaseMappingConfig.PermissionSetLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_PermissionSetGroupLVList = FXCollections.observableArrayList(Config.SFDC_PermissionSetGroupLV_ItemsList);
		SalesforceTestCaseMappingConfig.PermissionSetGroupLV.setItems(ObservableList_PermissionSetGroupLVList);
		SalesforceTestCaseMappingConfig.PermissionSetGroupLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_ProfileLVList = FXCollections.observableArrayList(Config.SFDC_ProfileLV_ItemsList);
		SalesforceTestCaseMappingConfig.ProfileLV.setItems(ObservableList_ProfileLVList);
		SalesforceTestCaseMappingConfig.ProfileLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_QuickActionDefinitionLVList = FXCollections.observableArrayList(Config.SFDC_QuickActionDefinitionLV_ItemsList);
		SalesforceTestCaseMappingConfig.QuickActionDefinitionLV.setItems(ObservableList_QuickActionDefinitionLVList);
		SalesforceTestCaseMappingConfig.QuickActionDefinitionLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_RecordTypeLVList = FXCollections.observableArrayList(Config.SFDC_RecordTypeLV_ItemsList);
		SalesforceTestCaseMappingConfig.RecordTypeLV.setItems(ObservableList_RecordTypeLVList);
		SalesforceTestCaseMappingConfig.RecordTypeLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_UserRoleLVList = FXCollections.observableArrayList(Config.SFDC_UserRoleLV_ItemsList);
		SalesforceTestCaseMappingConfig.UserRoleLV.setItems(ObservableList_UserRoleLVList);
		SalesforceTestCaseMappingConfig.UserRoleLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_ValidationRuleLVList = FXCollections.observableArrayList(Config.SFDC_ValidationRuleLV_ItemsList);
		SalesforceTestCaseMappingConfig.ValidationRuleLV.setItems(ObservableList_ValidationRuleLVList);
		SalesforceTestCaseMappingConfig.ValidationRuleLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		ObservableList<String> ObservableList_WebLinkLVList = FXCollections.observableArrayList(Config.SFDC_WebLinkLV_ItemsList);
		SalesforceTestCaseMappingConfig.WebLinkLV.setItems(ObservableList_WebLinkLVList);
		SalesforceTestCaseMappingConfig.WebLinkLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
  
    public void ClearMappedItemListViewSelection() throws Exception{
 	   SalesforceTestCaseMappingConfig.ApexClassLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.ApexClassMappedLV.setItems(SalesforceTestCaseMappingConfig.ApexClassLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.ApexComponentLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.ApexComponentMappedLV.setItems(SalesforceTestCaseMappingConfig.ApexComponentLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.ApexPageLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.ApexPageMappedLV.setItems(SalesforceTestCaseMappingConfig.ApexPageLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.ApexTriggerLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.ApexTriggerMappedLV.setItems(SalesforceTestCaseMappingConfig.ApexTriggerLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.AssignmentRuleLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.AssignmentRuleMappedLV.setItems(SalesforceTestCaseMappingConfig.AssignmentRuleLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.AuraDefinitionLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.AuraDefinitionMappedLV.setItems(SalesforceTestCaseMappingConfig.AuraDefinitionLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.AutoResponseRuleLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.AutoResponseRuleMappedLV.setItems(SalesforceTestCaseMappingConfig.AutoResponseRuleLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.BusinessProcessLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.BusinessProcessMappedLV.setItems(SalesforceTestCaseMappingConfig.BusinessProcessLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.CompactLayoutLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.CompactLayoutMappedLV.setItems(SalesforceTestCaseMappingConfig.CompactLayoutLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.CustomApplicationLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.CustomApplicationMappedLV.setItems(SalesforceTestCaseMappingConfig.CustomApplicationLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.CustomFieldLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.CustomFieldMappedLV.setItems(SalesforceTestCaseMappingConfig.CustomFieldLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.CustomObjectLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.CustomObjectMappedLV.setItems(SalesforceTestCaseMappingConfig.CustomObjectLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.CustomTabLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.CustomTabMappedLV.setItems(SalesforceTestCaseMappingConfig.CustomTabLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.FieldMappingLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.FieldMappingMappedLV.setItems(SalesforceTestCaseMappingConfig.FieldMappingLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.FieldSetLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.FieldSetMappedLV.setItems(SalesforceTestCaseMappingConfig.FieldSetLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.FlexiPageLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.FlexiPageMappedLV.setItems(SalesforceTestCaseMappingConfig.FlexiPageLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.FlowLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.FlowMappedLV.setItems(SalesforceTestCaseMappingConfig.FlowLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.FlowDefinitionLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.FlowDefinitionMappedLV.setItems(SalesforceTestCaseMappingConfig.FlowDefinitionLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.HomePageComponentLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.HomePageComponentMappedLV.setItems(SalesforceTestCaseMappingConfig.HomePageComponentLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.HomePageLayoutLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.HomePageLayoutMappedLV.setItems(SalesforceTestCaseMappingConfig.HomePageLayoutLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.LayoutLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.LayoutMappedLV.setItems(SalesforceTestCaseMappingConfig.LayoutLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.PathAssistantLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.PathAssistantMappedLV.setItems(SalesforceTestCaseMappingConfig.PathAssistantLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.PermissionSetLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.PermissionSetMappedLV.setItems(SalesforceTestCaseMappingConfig.PermissionSetLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.PermissionSetGroupLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.PermissionSetGroupMappedLV.setItems(SalesforceTestCaseMappingConfig.PermissionSetGroupLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.ProfileLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.ProfileMappedLV.setItems(SalesforceTestCaseMappingConfig.ProfileLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.QuickActionDefinitionLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.QuickActionDefinitionMappedLV.setItems(SalesforceTestCaseMappingConfig.QuickActionDefinitionLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.RecordTypeLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.RecordTypeMappedLV.setItems(SalesforceTestCaseMappingConfig.RecordTypeLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.UserRoleLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.UserRoleMappedLV.setItems(SalesforceTestCaseMappingConfig.UserRoleLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.ValidationRuleLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.ValidationRuleMappedLV.setItems(SalesforceTestCaseMappingConfig.ValidationRuleLV_MappedItemsList);
 	   
 	   SalesforceTestCaseMappingConfig.WebLinkLV_MappedItemsList.clear();
 	   SalesforceTestCaseMappingConfig.WebLinkMappedLV.setItems(SalesforceTestCaseMappingConfig.WebLinkLV_MappedItemsList); 
    }

    public void uploadExistingMappingFile() throws Exception {
    	if(!Config.SFDC_EXISTING_MAPPING_FILE_PATH.equalsIgnoreCase("sfdc.mappingfilepath")) {
	    	String filePath = Config.SFDC_EXISTING_MAPPING_FILE_PATH;
	  	    JSONParser jsonParser = new JSONParser();
		    FileReader reader = new FileReader(filePath);
		    Object obj = jsonParser.parse(reader);
	        JSONObject ComponentObject = (JSONObject) obj;
	        
	        //int TotalTestCases = (int) ComponentObject.get("TotalTestCasesMapped");
	        JSONArray finalMappingDetails = (JSONArray) ComponentObject.get("FinalMappingDetails");
			for(int i = 0; i < finalMappingDetails.size() ; i++) {
				//Instantiating HashMap for storing test case mapping info
	    		HashMap<String, HashMap<String, ObservableList<String>>> IndividualTestCaseFinalDetail = new HashMap<String, HashMap<String, ObservableList<String>>>();
	    		HashMap<String, ObservableList<String>> IndividualTestCaseDetail = new HashMap<String, ObservableList<String>>();
	    		//Start retrieving details from JSON mapping file
			    JSONObject testcaseDetail = (JSONObject) finalMappingDetails.get(i);
			    String TestCaseName = (String) testcaseDetail.get("TestCaseName");
			    JSONArray mappedRecords = (JSONArray) testcaseDetail.get("records");
			    for(int j = 0; j < mappedRecords.size(); j++) {
			    	JSONObject mappedComponentDetails = (JSONObject) mappedRecords.get(j);
			    	String componentType = (String) mappedComponentDetails.get("ComponentType");
			    	JSONArray componentNames = (JSONArray) mappedComponentDetails.get("ComponentName");
			    	ObservableList<String> observableComponentList = FXCollections.observableArrayList();
	            	for (int k=0;k<componentNames.size();k++){ 
	            		  observableComponentList.add(componentNames.get(k).toString());
	            	  } 
	            	IndividualTestCaseDetail.put(componentType, observableComponentList);
			    }
			    //Finally adding individual test case detail in HashMap
			    IndividualTestCaseFinalDetail.put(TestCaseName, IndividualTestCaseDetail);
			    //Creating JSON File per Test Case
			    writeTestCaseJsonData(TestCaseName,IndividualTestCaseFinalDetail);
			    //This is for pupulating the test case names in left side list view
				CreatedTestCaseList.add(TestCaseName);
			    Collections.sort(CreatedTestCaseList);
			}
		    System.out.println("******************************************************************************************");
		    MappedTestCaseListView.setItems(CreatedTestCaseList); 
	        reader.close();
    	}
    }

}

/*
DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "ApexClass", SalesforceTestCaseMappingConfig.ApexClassMappedLV.getItems()));
 DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "ApexComponent", SalesforceTestCaseMappingConfig.ApexComponentMappedLV.getItems()));
 DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "ApexPage", SalesforceTestCaseMappingConfig.ApexPageMappedLV.getItems()));
 DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "ApexTrigger", SalesforceTestCaseMappingConfig.ApexTriggerMappedLV.getItems()));
DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "AssignmentRule", SalesforceTestCaseMappingConfig.AssignmentRuleMappedLV.getItems()));
DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "AuraDefinition", SalesforceTestCaseMappingConfig.AuraDefinitionMappedLV.getItems()));
DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "AutoResponseRule", SalesforceTestCaseMappingConfig.AutoResponseRuleMappedLV.getItems()));
 DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "BusinessProcess", SalesforceTestCaseMappingConfig.BusinessProcessMappedLV.getItems()));
 DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "CompactLayout", SalesforceTestCaseMappingConfig.CompactLayoutMappedLV.getItems()));
 DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "CustomApplication", SalesforceTestCaseMappingConfig.CustomApplicationMappedLV.getItems()));
DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "CustomField", SalesforceTestCaseMappingConfig.CustomFieldMappedLV.getItems()));
DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "CustomObject", SalesforceTestCaseMappingConfig.CustomObjectMappedLV.getItems()));
DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "CustomTab", SalesforceTestCaseMappingConfig.CustomTabMappedLV.getItems()));
 DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "FieldMapping", SalesforceTestCaseMappingConfig.FieldMappingMappedLV.getItems()));
 DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "FieldSet", SalesforceTestCaseMappingConfig.FieldSetMappedLV.getItems()));
 DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "FlexiPage", SalesforceTestCaseMappingConfig.FlexiPageMappedLV.getItems()));
DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "Flow", SalesforceTestCaseMappingConfig.FlowMappedLV.getItems()));
DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "FlowDefinition", SalesforceTestCaseMappingConfig.FlowDefinitionMappedLV.getItems()));
DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "HomePageComponent", SalesforceTestCaseMappingConfig.HomePageComponentMappedLV.getItems()));
 DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "HomePageLayout", SalesforceTestCaseMappingConfig.HomePageLayoutMappedLV.getItems()));
 DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "Layout", SalesforceTestCaseMappingConfig.LayoutMappedLV.getItems()));
 DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "PathAssistant", SalesforceTestCaseMappingConfig.PathAssistantMappedLV.getItems()));
DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "PermissionSet", SalesforceTestCaseMappingConfig.PermissionSetMappedLV.getItems()));
DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "PermissionSetGroup", SalesforceTestCaseMappingConfig.PermissionSetGroupMappedLV.getItems()));
DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "Profile", SalesforceTestCaseMappingConfig.ProfileMappedLV.getItems()));
DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "QuickActionDefinition", SalesforceTestCaseMappingConfig.QuickActionDefinitionMappedLV.getItems()));
DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "RecordType", SalesforceTestCaseMappingConfig.RecordTypeMappedLV.getItems()));
DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "UserRole", SalesforceTestCaseMappingConfig.UserRoleMappedLV.getItems()));
DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "ValidationRule", SalesforceTestCaseMappingConfig.ValidationRuleMappedLV.getItems()));
DisplayMappedComponentsController.masterData.add(new DisplayMappedComponents(testCaseName, "WebLink", SalesforceTestCaseMappingConfig.WebLinkMappedLV.getItems()));
 */

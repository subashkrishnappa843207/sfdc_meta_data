package com.cognizant.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import com.cognizant.login.OAuthTokenFlow;
import com.cognizant.sfdc_quality_analyzer_v3.App;
import com.cognizant.uicomponent.DisplayMultiOrgMissingComponentsController;
import com.cognizant.uicomponent.SalesforceTestCaseMappingConfig;
import com.cognizant.utility.Config;
import com.cognizant.utility.ExcelUtils;
import com.cognizant.utility.JsonUtils;
import com.cognizant.utility.SFDCContext;
import com.cognizant.utility.XMLUtils;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class SFDCDataloaderHomePage implements Initializable {
	private static final Logger log = Logger.getLogger(SFDCDataloaderHomePage.class);
	public int cnt= 1;
	ExcelUtils excelUtil = new ExcelUtils();
	JsonUtils jsonUtil = new JsonUtils();
	XMLUtils xmlUtil = new XMLUtils();
	private String SFDC_TOOLING_SOBJECT_JSON_FilePath = System.getProperty("user.dir")+"\\target\\classes\\com\\cognizant\\sfdc_quality_analyzer_v3\\json\\SFDC_TOOLING_SOBJECT_JSON_FILE_";
	private String SFDC_OBJECT_JSON_FilePath = System.getProperty("user.dir")+"\\target\\classes\\com\\cognizant\\sfdc_quality_analyzer_v3\\json\\FDC_OBJECT_JSON_FILE_";
    //static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
    static final String RESPONSESERVICE = "/services/oauth2/authorize?response_type=token";
    static final String REDIRECT_URI = "https://login.salesforce.com/services/oauth2/success";
    public static String successURL = null;
    public static Map<String, String> successURLParams = new HashMap<>();
    Config config;
    ArrayList<String> selectedFieldItems = new ArrayList<String>();
    String SelectedFieldsSOQLQuery = "";
    String SOQLWhereCondition = "";
    String SOQLOtherCondition = "";
    String SOQLSortingTypeCondition = "";
    StringBuilder Querybuilder = new StringBuilder("");
    ArrayList<String> selectedFields = new ArrayList<String>();
    HashMap<String, HashMap<String, String>> finalSOQLResultsMap = new HashMap<String, HashMap<String, String>>();
    
    @FXML ComboBox<String> SelectSalesforceObjectType;
    @FXML ComboBox<String> SelectSalesforceObjectName;
    @FXML ComboBox<String> SortingFieldNames;
    @FXML ComboBox<String> SortingTypes;
    @FXML ComboBox<String> LogicalOperators;
    @FXML ComboBox<String> WhereConditionFieldNames;
    @FXML ComboBox<String> RelationalOperators;
    @FXML ListView SalesforceObjectSelectedFields;
    @FXML RadioButton CSVFileFormat;
    @FXML RadioButton JSONFileFormat;
    @FXML RadioButton ExcelFileFormat;
    ToggleGroup toggleFileFormatGroup = new ToggleGroup();
    
    @FXML Button AddSortingConditionButton;
    @FXML Button AddQueryConditionButton;
    @FXML Button AddSelectedFieldsButton;
    @FXML Button RunSOQLQueryButton;
    @FXML Button DownloadSOQLQueryResultsButton;
    @FXML TextField EnterQueryConditionValue;
    @FXML TextArea FinalSOQLQueryTextArea;
    
    @FXML Label SOQLResultTotalRecordCountLabel;
    @FXML Label SOQLResultMessageLabel;
    @FXML ImageView SOQLResultImageView;
    private ObservableList<String> cbSalesforceObjectType = FXCollections.observableArrayList("Salesforce Tooling SObjects", "Salesforce Objects");
    private ObservableList<String> cbSelectSalesforceSObjectsList = FXCollections.observableArrayList(Config.SFDC_SINGLE_SOBJECTS_ITEMS);
    private ObservableList<String> cbSelectSalesforceObjectsList = FXCollections.observableArrayList(Config.SFDC_SINGLE_OBJECTS_ITEMS);
    private ObservableList<String> cbSortingTypesItemsList = FXCollections.observableArrayList("A to Z", "Z to A");
    private ObservableList<String> cbLogicalOperatorsItemsList = FXCollections.observableArrayList("WHERE", "AND", "OR");
    private ObservableList<String> FieldNamesItemsList = FXCollections.observableArrayList();
    private ObservableList<String> cbRelationalOperatorsItemsList = FXCollections.observableArrayList("=", "!=", "<", "<=", ">", ">=", "starts with", "ends with", "contains"
    		, "IN", "NOT IN", "INCLUDES", "EXCLUDES");
    
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		SelectSalesforceObjectType.setItems(cbSalesforceObjectType);

		SortingTypes.setItems(cbSortingTypesItemsList);
		LogicalOperators.setItems(cbLogicalOperatorsItemsList);
		RelationalOperators.setItems(cbRelationalOperatorsItemsList);
		SalesforceObjectSelectedFields.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		JSONFileFormat.setToggleGroup(toggleFileFormatGroup);
		ExcelFileFormat.setToggleGroup(toggleFileFormatGroup);

		OAuthTokenFlow.generateRequestSpecification("SingleOrg");
				     
	}
    
	@FXML
    private void switchBackToHomePage() throws IOException {
		log.info("Moving Back to Home Page");
        App.setRoot("AppHomePage");
    }
	
	@FXML
    private void SelectSalesforceObjectType() throws IOException {
		if(SelectSalesforceObjectType.getSelectionModel().getSelectedItem().equals("Salesforce Tooling SObjects")) {
			SelectSalesforceObjectName.setItems(cbSelectSalesforceSObjectsList);
		}else if(SelectSalesforceObjectType.getSelectionModel().getSelectedItem().equals("Salesforce Objects")) {
			SelectSalesforceObjectName.setItems(cbSelectSalesforceObjectsList);
		}
    }
	
	@FXML
    private void SelectSalesforceObjectName() throws IOException {
		try {
			FieldNamesItemsList.clear();
	    	String selectedObjectTypeItem = SelectSalesforceObjectType.getSelectionModel().getSelectedItem();
	        if (selectedObjectTypeItem.equals("Salesforce Objects")) {
	        	String selectedObjectNameItem = SelectSalesforceObjectName.getSelectionModel().getSelectedItem();
	        	log.info(selectedObjectTypeItem +" -- "+selectedObjectNameItem);
	    		Response responseSFDCObjectDescribeInfo = SFDCContext.getDescribeObject(selectedObjectNameItem);
	    		//System.out.println(responseSFDCObjectDescribeInfo.getBody().asPrettyString());
	    		JSONObject resultsSFDCObjectDescribeInfo = new JSONObject(responseSFDCObjectDescribeInfo.body().asPrettyString());
				JSONArray jsonSFDCObjectFieldsArray = resultsSFDCObjectDescribeInfo.optJSONArray("fields");    
				for(int j = 0; j<jsonSFDCObjectFieldsArray.length();j++) {
					JSONObject IndividualSFDCObjectFieldDetail = (JSONObject) jsonSFDCObjectFieldsArray.get(j);
	        		String Name = (String) IndividualSFDCObjectFieldDetail.get("name");
	        		String Label = (String) IndividualSFDCObjectFieldDetail.get("label");
	        		FieldNamesItemsList.add(Name);
				}
		        if(FieldNamesItemsList.size() > 0) {
		        	SortingFieldNames.setItems(FieldNamesItemsList);
		        	WhereConditionFieldNames.setItems(FieldNamesItemsList);
		        	SalesforceObjectSelectedFields.setItems(FieldNamesItemsList);
		        }
	        }else if(selectedObjectTypeItem.equals("Salesforce Tooling SObjects")) {
	        	FieldNamesItemsList.clear();
	        	String selectedObjectNameItem = SelectSalesforceObjectName.getSelectionModel().getSelectedItem();
	        	Response responseSObjectDescribeInfo = SFDCContext.getDescribeToolingObject(selectedObjectNameItem);
	        	//System.out.println(responseSObjectDescribeInfo.getBody().asPrettyString());
				JSONObject resultsJObjectDescribeInfo = new JSONObject(responseSObjectDescribeInfo.body().asPrettyString());
				JSONArray jsonSObjectFieldsArray = resultsJObjectDescribeInfo.optJSONArray("fields");    
				for(int j = 0; j<jsonSObjectFieldsArray.length();j++) {
					JSONObject IndividualSObjectFieldDetail = (JSONObject) jsonSObjectFieldsArray.get(j);
	        		String Name = (String) IndividualSObjectFieldDetail.get("name");
	        		String Label = (String) IndividualSObjectFieldDetail.get("label");
	        		FieldNamesItemsList.add(Name);
				}
		        if(FieldNamesItemsList.size() > 0) {
		        	SortingFieldNames.setItems(FieldNamesItemsList);
		        	WhereConditionFieldNames.setItems(FieldNamesItemsList);
		        	SalesforceObjectSelectedFields.setItems(FieldNamesItemsList);
		        }
	    	}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			log.info(e.getMessage());
		}
	}
	
    @FXML
    private void switchBackToPreviousPage() throws IOException {
    	log.info("Moving to SFDC Dataloader Login Page");
        App.setRoot("SFDCDataLoaderLoginPage");
    }
    
    @FXML
    private void AddSelectedFields() throws IOException {
    	log.info("Implement code to append Query Condition to exisitng SOQL Query");
        SOQLWhereCondition = "";
        SOQLOtherCondition = "";
        SOQLSortingTypeCondition = "";
        if(selectedFields.size() > 0) {
        	selectedFields.clear();
        }
        Querybuilder.setLength(0);;
        ObservableList<String> selectedItems = SalesforceObjectSelectedFields.getSelectionModel().getSelectedItems();
        //selectedFields = (ArrayList<String>) selectedItems;
		StringBuilder builder = new StringBuilder("SELECT ");
	    for (int i = 0; i < selectedItems.size() ; i ++ ) {
	    	 if(i == (selectedItems.size() - 1)) {
	    		 builder.append(selectedItems.get(i));
	    		 selectedFields.add(selectedItems.get(i));
	    	 }else {
	    		 builder.append(selectedItems.get(i) +" , ");
	    		 selectedFields.add(selectedItems.get(i));
	    	 }
	     }
	    
		String SelectSFDCObjectName = SelectSalesforceObjectName.getSelectionModel().getSelectedItem();
		if (!SelectSFDCObjectName.equals("Select Salesforce Object Name")) {
			// Your action here
			SelectedFieldsSOQLQuery = builder.toString()+" FROM "+SelectSFDCObjectName;
			log.info(SelectedFieldsSOQLQuery);
			FinalSOQLQueryTextArea.setText(SelectedFieldsSOQLQuery);
		}
    }
    
    @FXML
    private void AddQueryCondition() throws IOException {
    	log.info("Implement code to append Query Condition to exisitng SOQL Query");
        String LastUpdatedSOQLQuery = FinalSOQLQueryTextArea.getText();

        if((LastUpdatedSOQLQuery.isEmpty()) || (LastUpdatedSOQLQuery == null) || (LastUpdatedSOQLQuery == "")) {
    		Alert SOQLQueryTextAreaErrorAlert = new Alert(AlertType.ERROR);
    		SOQLQueryTextAreaErrorAlert.setHeaderText("SOQL Query Area Field is Blank");
    		SOQLQueryTextAreaErrorAlert.setContentText("SOQL Query Area Field is Blank. Please select the Object and Field Details before proceed!!!");
    		SOQLQueryTextAreaErrorAlert.showAndWait();
        }else {
        	//StringBuilder builder = new StringBuilder(LastUpdatedSOQLQuery+ " ");
        	if(LogicalOperators.getSelectionModel().getSelectedItem().equals("WHERE")) {
            	String RelationalOperator = RelationalOperators.getSelectionModel().getSelectedItem();
            	if(RelationalOperator != null) {
            		SOQLWhereCondition = " WHERE " + WhereConditionFieldNames.getSelectionModel().getSelectedItem()+" "+GetFinalCondition(RelationalOperator);
            	}else {
            		SOQLWhereCondition = " WHERE " + WhereConditionFieldNames.getSelectionModel().getSelectedItem()+" "+GetFinalCondition("=");
            	}
        	}else if(LogicalOperators.getSelectionModel().getSelectedItem().equals("AND")) {
            	String RelationalOperator = RelationalOperators.getSelectionModel().getSelectedItem();
            	if(RelationalOperator != null) {
                	SOQLOtherCondition = " AND " + WhereConditionFieldNames.getSelectionModel().getSelectedItem()+" "+GetFinalCondition(RelationalOperator);
                	Querybuilder.append(SOQLOtherCondition);
            	}else {
                	SOQLOtherCondition = " AND " + WhereConditionFieldNames.getSelectionModel().getSelectedItem()+" "+GetFinalCondition("=");
                	Querybuilder.append(SOQLOtherCondition);
            	}
            	//builder.append(SOQLOtherCondition);
        	}else if(LogicalOperators.getSelectionModel().getSelectedItem().equals("OR")) {
            	String RelationalOperator = RelationalOperators.getSelectionModel().getSelectedItem();
               	if(RelationalOperator != null) {
                	SOQLOtherCondition = " OR " + WhereConditionFieldNames.getSelectionModel().getSelectedItem()+" "+GetFinalCondition(RelationalOperator);
                	Querybuilder.append(SOQLOtherCondition);
            	}else {
                	SOQLOtherCondition = " OR " + WhereConditionFieldNames.getSelectionModel().getSelectedItem()+" "+GetFinalCondition("=");
                	Querybuilder.append(SOQLOtherCondition);
            	}
        	}else {
        		Alert SOQLQueryLogicalOperatorErrorAlert = new Alert(AlertType.ERROR);
        		SOQLQueryLogicalOperatorErrorAlert.setHeaderText("Select Valid Logical Operator Error");
        		SOQLQueryLogicalOperatorErrorAlert.setContentText("Please Select Valid Logical Operator before proceed!!!");
        		SOQLQueryLogicalOperatorErrorAlert.showAndWait();
        	}
        	//SOQLOtherCondition = builder.toString();
        	log.info(SelectedFieldsSOQLQuery + SOQLWhereCondition + Querybuilder.toString());
	        FinalSOQLQueryTextArea.setText(SelectedFieldsSOQLQuery + SOQLWhereCondition + Querybuilder.toString());
        }
    }
    
    @FXML private void AddSortingCondition() throws IOException {
    	log.info("Implement code to append Sorting Condition to exisitng SOQL Query");
        String LastUpdatedSOQLQuery = FinalSOQLQueryTextArea.getText();
        if((LastUpdatedSOQLQuery.isEmpty()) || (LastUpdatedSOQLQuery == null) || (LastUpdatedSOQLQuery == "")) {
    		Alert SOQLQueryTextAreaErrorAlert = new Alert(AlertType.ERROR);
    		SOQLQueryTextAreaErrorAlert.setHeaderText("SOQL Query Area Field is Blank");
    		SOQLQueryTextAreaErrorAlert.setContentText("SOQL Query Area Field is Blank. Please select the Object and Field Details before proceed!!!");
    		SOQLQueryTextAreaErrorAlert.showAndWait();
        }else {
        	//StringBuilder builder = new StringBuilder(LastUpdatedSOQLQuery+ " ");
        	if(SortingFieldNames.getSelectionModel().getSelectedItem() != null) {
        		
        		if(SortingTypes.getSelectionModel().getSelectedItem() != null) {
        			if(SortingTypes.getSelectionModel().getSelectedItem().equals("A to Z")) {
        				SOQLSortingTypeCondition = " ORDER BY "+SortingFieldNames.getSelectionModel().getSelectedItem()+" ASC";
        			}else {
        				SOQLSortingTypeCondition = " ORDER BY "+SortingFieldNames.getSelectionModel().getSelectedItem()+" DESC";
        			}	
        		}else {
        			SOQLSortingTypeCondition = " ORDER BY "+SortingFieldNames.getSelectionModel().getSelectedItem()+" ASC";
        		}
        	}else {
        		Alert SOQLQuerySortingFieldErrorAlert = new Alert(AlertType.ERROR);
        		SOQLQuerySortingFieldErrorAlert.setHeaderText("Select Valid Field Name Error");
        		SOQLQuerySortingFieldErrorAlert.setContentText("Please Select Valid Field Name for Sorting before proceed!!!");
        		SOQLQuerySortingFieldErrorAlert.showAndWait();
        	}
        	// Your action here
        	log.info(SelectedFieldsSOQLQuery + SOQLWhereCondition + Querybuilder.toString() + SOQLSortingTypeCondition);
	   		FinalSOQLQueryTextArea.setText(SelectedFieldsSOQLQuery + SOQLWhereCondition + Querybuilder.toString() + SOQLSortingTypeCondition);
        }
    }
    
    @FXML
    private void RunSOQLQuery() throws IOException {
    	log.info("Implement code to Run SOQL Query");
        File fileHappySmileyPath = new File(System.getProperty("user.dir")+"\\target\\classes\\com\\cognizant\\sfdc_quality_analyzer_v3\\images\\icons8-happy-cloud.png");
        File fileSadSmileyPath = new File(System.getProperty("user.dir")+"\\target\\classes\\com\\cognizant\\sfdc_quality_analyzer_v3\\images\\icons8-sad-cloud.png");
        Image HappySmileyImage = new Image(fileHappySmileyPath.toURI().toString());
        Image SadSmileyImage = new Image(fileSadSmileyPath.toURI().toString());
        String errorMessage = null;
        if(finalSOQLResultsMap.size() > 0) {
        	finalSOQLResultsMap.clear();
        }
    	try {
    		int totalCount = 0;
	    	String FinalSOQLQuery = FinalSOQLQueryTextArea.getText();
	    	String selectedObjectTypeItem = SelectSalesforceObjectType.getSelectionModel().getSelectedItem();
	        if (selectedObjectTypeItem.equals("Salesforce Objects")) {
	        	Response responseGetQueryDetails = SFDCContext.runDataQuery(FinalSOQLQuery);
	        	errorMessage = responseGetQueryDetails.body().asPrettyString();
	    		//System.out.println(responseGetQueryDetails.body().asPrettyString());
	    	    JSONObject resultsJObject = new JSONObject(responseGetQueryDetails.body().asPrettyString());
	    		JSONArray jsonRecordsArray = resultsJObject.optJSONArray("records");
	    		int jsonObjectSize = resultsJObject.getInt("totalSize");
		    	if(jsonObjectSize > 0) {
	        		boolean amIonLastPage = resultsJObject.getBoolean("done");
	        		System.out.println("Inside If condition as amIonLastPage = "+amIonLastPage);
		    		log.info("Total Records found : "+jsonObjectSize);
		    		for(int j=0;j<jsonRecordsArray.length();j++) {
		    			HashMap<String, String> queryDetailsMap = new HashMap<String, String>();
	        			for(String fieldName : selectedFields) {
		        			JSONObject jsonFinalRecords = new JSONObject(jsonRecordsArray.get(j).toString());
		        			queryDetailsMap.put(fieldName, jsonFinalRecords.getString(fieldName));
	        			}
	        			finalSOQLResultsMap.put(String.valueOf(totalCount), queryDetailsMap);
	        			totalCount = totalCount + 1;
		    		}
		    		
	        		while(!amIonLastPage){	
	        			String nextRecordsUrl = null;
	        			System.out.println("Inside While loop as amIonLastPage = "+amIonLastPage);
	        			if(!amIonLastPage) {
	        				nextRecordsUrl = resultsJObject.getString("nextRecordsUrl");
	        				System.out.println("nextRecordsUrl : "+nextRecordsUrl);
	        				Response responseRecordInfo = SFDCContext.getDataQuery(nextRecordsUrl);
	        				resultsJObject = new JSONObject(responseRecordInfo.body().asPrettyString());
	        				jsonRecordsArray = resultsJObject.optJSONArray("records");
	        				amIonLastPage = resultsJObject.getBoolean("done");
	        				System.out.println("Move on to next page as amIonLastPage = "+amIonLastPage);
	        				System.out.println("jsonGetResultsRecordsArray Size : "+jsonRecordsArray.length());
	    		    		for(int j=0;j<jsonRecordsArray.length();j++) {
	    		    			HashMap<String, String> queryDetailsMap = new HashMap<String, String>();
	    	        			for(String fieldName : selectedFields) {
	    		        			JSONObject jsonFinalRecords = new JSONObject(jsonRecordsArray.get(j).toString());
	    		        			queryDetailsMap.put(fieldName, jsonFinalRecords.getString(fieldName));
	    	        			}
	    	        			finalSOQLResultsMap.put(String.valueOf(totalCount), queryDetailsMap);
	    	        			totalCount = totalCount + 1;
	    		    		}
	        			}	
	        		}
	        		System.out.println("finalSOQLResultsMap Size : "+finalSOQLResultsMap.size());
    				System.out.println("Final Count : "+totalCount);
	        		//System.out.println(finalSOQLResultsMap);
	        		SOQLResultImageView.setImage(HappySmileyImage);
	        		//SOQLResultTotalRecordCountLabel.setText(String.valueOf(jsonObjectSize));
	        		SOQLResultMessageLabel.setText(String.valueOf(jsonObjectSize)+" Records Found");
		    		SOQLResultMessageLabel.setTextFill(Color.WHITE);
		    		SOQLResultMessageLabel.setStyle("-fx-font: 36 arial;-fx-font-weight: bold;");
		    		SOQLResultMessageLabel.setWrapText(true);
		    		SOQLResultMessageLabel.setTextAlignment(TextAlignment.CENTER);
	    		}else {
	    			SOQLResultImageView.setImage(SadSmileyImage);
	        		//SOQLResultTotalRecordCountLabel.setText(String.valueOf(0));
	        		SOQLResultMessageLabel.setText("No Records Found");
		    		SOQLResultMessageLabel.setTextFill(Color.WHITE);
		    		SOQLResultMessageLabel.setStyle("-fx-font: 36 arial;-fx-font-weight: bold;");
		    		SOQLResultMessageLabel.setWrapText(true);
		    		SOQLResultMessageLabel.setTextAlignment(TextAlignment.CENTER);
	    		}
	         }else if(selectedObjectTypeItem.equals("Salesforce Tooling SObjects")) {
		        	Response responseGetQueryDetails = SFDCContext.runToolingQuery(FinalSOQLQuery);
		        	errorMessage = responseGetQueryDetails.body().asPrettyString();
		    		//System.out.println(responseGetQueryDetails.body().asPrettyString());
		    	    JSONObject resultsJObject = new JSONObject(responseGetQueryDetails.body().asPrettyString());
		    		JSONArray jsonRecordsArray = resultsJObject.optJSONArray("records");
		    		int jsonObjectSize = resultsJObject.getInt("totalSize");
			    	if(jsonObjectSize > 0) {
		        		boolean amIonLastPage = resultsJObject.getBoolean("done");
		        		System.out.println("Inside If condition as amIonLastPage = "+amIonLastPage);
			    		log.info("Total Records found : "+jsonObjectSize);
			    		for(int j=0;j<jsonRecordsArray.length();j++) {
			    			HashMap<String, String> queryDetailsMap = new HashMap<String, String>();
		        			for(String fieldName : selectedFields) {
			        			JSONObject jsonFinalRecords = new JSONObject(jsonRecordsArray.get(j).toString());
			        			queryDetailsMap.put(fieldName, jsonFinalRecords.getString(fieldName));
		        			}
		        			finalSOQLResultsMap.put(String.valueOf(totalCount), queryDetailsMap);
		        			totalCount = totalCount + 1;
			    		}
			    		
		        		while(!amIonLastPage){	
		        			String nextRecordsUrl = null;
		        			System.out.println("Inside While loop as amIonLastPage = "+amIonLastPage);
		        			if(!amIonLastPage) {
		        				nextRecordsUrl = resultsJObject.getString("nextRecordsUrl");
		        				System.out.println("nextRecordsUrl : "+nextRecordsUrl);
		        				Response responseRecordInfo = SFDCContext.getToolingQuery(nextRecordsUrl);
		        				resultsJObject = new JSONObject(responseRecordInfo.body().asPrettyString());
		        				jsonRecordsArray = resultsJObject.optJSONArray("records");
		        				amIonLastPage = resultsJObject.getBoolean("done");
		        				System.out.println("Move on to next page as amIonLastPage = "+amIonLastPage);
		        				System.out.println("jsonGetResultsRecordsArray Size : "+jsonRecordsArray.length());
		    		    		for(int j=0;j<jsonRecordsArray.length();j++) {
		    		    			HashMap<String, String> queryDetailsMap = new HashMap<String, String>();
		    	        			for(String fieldName : selectedFields) {
		    		        			JSONObject jsonFinalRecords = new JSONObject(jsonRecordsArray.get(j).toString());
		    		        			queryDetailsMap.put(fieldName, jsonFinalRecords.getString(fieldName));
		    	        			}
		    	        			finalSOQLResultsMap.put(String.valueOf(totalCount), queryDetailsMap);
		    	        			totalCount = totalCount + 1;
		    		    		}
		        			}	
		        		}
		        		System.out.println("finalSOQLResultsMap Size : "+finalSOQLResultsMap.size());
	    				System.out.println("Final Count : "+totalCount);
		        		//System.out.println(finalSOQLResultsMap);
		        		SOQLResultImageView.setImage(HappySmileyImage);
		        		//SOQLResultTotalRecordCountLabel.setText(String.valueOf(jsonObjectSize));
		        		SOQLResultMessageLabel.setText(String.valueOf(jsonObjectSize)+" Records Found");
			    		SOQLResultMessageLabel.setTextFill(Color.WHITE);
			    		SOQLResultMessageLabel.setStyle("-fx-font: 36 arial;-fx-font-weight: bold;");
			    		SOQLResultMessageLabel.setWrapText(true);
			    		SOQLResultMessageLabel.setTextAlignment(TextAlignment.CENTER);
				}else {
					SOQLResultImageView.setImage(SadSmileyImage);
		    		//SOQLResultTotalRecordCountLabel.setText(String.valueOf(0));
		    		SOQLResultMessageLabel.setText("No Records Found");
		    		SOQLResultMessageLabel.setTextFill(Color.WHITE);
		    		SOQLResultMessageLabel.setStyle("-fx-font: 36 arial;-fx-font-weight: bold;");
		    		SOQLResultMessageLabel.setWrapText(true);
		    		SOQLResultMessageLabel.setTextAlignment(TextAlignment.CENTER);
				}
	        }
    	}catch(JSONException jsonexcep) {
    		jsonexcep.printStackTrace();
        	log.info(errorMessage);
			SOQLResultImageView.setImage(SadSmileyImage);
    		//SOQLResultTotalRecordCountLabel.setText("");
    		SOQLResultMessageLabel.setText(errorMessage);
    		SOQLResultMessageLabel.setTextFill(Color.WHITE);
    		SOQLResultMessageLabel.setStyle("-fx-font: 12 arial;-fx-font-weight: regular;");
    		SOQLResultMessageLabel.setWrapText(true);
    		SOQLResultMessageLabel.setTextAlignment(TextAlignment.CENTER);
    	}
    }
    
    @FXML
    private void DownloadSOQLQueryResults() throws IOException {
    	log.info("Implement code to Display SOQL Query Result");
    	log.info("**************************************************");
    	log.info(finalSOQLResultsMap);
    	log.info(selectedFields);
    	log.info("**************************************************");
        String[] arraySelectedFields = selectedFields.toArray(new String[selectedFields.size()]);
        log.info(arraySelectedFields);
    	if(JSONFileFormat.isSelected()){
    		log.info("JSON Selected");
    		log.info("finalSOQLResultsMap Size : "+finalSOQLResultsMap.size());
			String reportFilePath = jsonUtil.exportJSONFile(arraySelectedFields, finalSOQLResultsMap);
		}else if(ExcelFileFormat.isSelected()){
			log.info("Excel Selected");
			log.info("finalSOQLResultsMap Size : "+finalSOQLResultsMap.size());
			String reportFilePath = excelUtil.export(arraySelectedFields, finalSOQLResultsMap);
		}
    }
    
    @FXML
    private void signOut() throws IOException {
    	App.setRoot("AppLauncherPage");
        //Platform.exit();
        //System.exit(0);
    }
    
    public void FindFieldDetails() {

    }
    
    public String GetFinalCondition(String RelationalOperator) {
    	String remainingString = null;
    	switch(RelationalOperator) {
	 	   case "=" :
	 		  remainingString = RelationalOperator +" '"+EnterQueryConditionValue.getText()+"'";
	 	      break;
	 	   case "!=" :
	 		  remainingString = RelationalOperator +" '"+EnterQueryConditionValue.getText()+"'";
	 	      break;
	 	   case "<" :
	 		  remainingString = RelationalOperator +" '"+EnterQueryConditionValue.getText()+"'";
		      break;
	 	   case "<=" :
	 		  remainingString = RelationalOperator +" '"+EnterQueryConditionValue.getText()+"'";
		      break;
	 	   case ">" :
	 		  remainingString = RelationalOperator +" '"+EnterQueryConditionValue.getText()+"'";
	    	  break;
	 	   case ">=" :
	 		  remainingString = RelationalOperator +" '"+EnterQueryConditionValue.getText()+"'";
	    	  break;
	 	   case "starts with" :
	 		  remainingString = "LIKE" +" '"+EnterQueryConditionValue.getText()+"%'";
		      break;
	 	   case "ends with" :
	 		  remainingString = "LIKE" +" '%"+EnterQueryConditionValue.getText()+"'";
		      break;
	 	   case "contains" :
	 		  remainingString = "LIKE" +" '%"+EnterQueryConditionValue.getText()+"%'";
		      break;
	 	   case "IN" :
	 		  remainingString = RelationalOperator +" ('"+EnterQueryConditionValue.getText()+"')";
		      break;
	 	   case "NOT IN" :
	 		  remainingString = RelationalOperator +" ('"+EnterQueryConditionValue.getText()+"')";
		      break;
	 	   case "INCLUDES" :
	 		  remainingString = RelationalOperator +" ('"+EnterQueryConditionValue.getText()+"')";
		      break;
	 	   case "EXCLUDES" :
	 		  remainingString = RelationalOperator +" ('"+EnterQueryConditionValue.getText()+"')";
		      break;
	    	}   
    	return remainingString;
    }
    
}

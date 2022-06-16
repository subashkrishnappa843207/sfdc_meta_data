package com.cognizant.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cognizant.login.OAuthTokenFlow;
import com.cognizant.sfdc_quality_analyzer_v3.App;
import com.cognizant.uicomponent.DisplayApexCodeCoverageDetails;
import com.cognizant.uicomponent.DisplayApexCodeCoverageDetailsController;
import com.cognizant.uicomponent.DisplaySecurityHealthCheckDetails;
import com.cognizant.uicomponent.DisplaySecurityHealthCheckDetailsController;
import com.cognizant.uicomponent.DoughnutChart;
import com.cognizant.uicomponent.SFDCSecurityHealthCheckDetailsPage;
import com.cognizant.utility.Config;
import com.cognizant.utility.SFDCContext;

import io.restassured.response.Response;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class MultiOrgHomePage implements Initializable {
	private static final Logger log = Logger.getLogger(MultiOrgHomePage.class);
    @FXML RadioButton FirstRadioButton;
    @FXML RadioButton SecondRadioButton;
    ToggleGroup toggleGroup = new ToggleGroup();
    
    @FXML Label SourceSalesforceUserName;
    @FXML Label SourceOrganizationEdition;
    @FXML Label SourceIsSandbox;
    @FXML Label SourceOrganizationName;
    @FXML Label SourceOrganizationID;
    @FXML Label SourceEndpointURL;
    @FXML Label TargetSalesforceUserName;
    @FXML Label TargetOrganizationEdition;
    @FXML Label TargetIsSandbox;
    @FXML Label TargetOrganizationName;
    @FXML Label TargetOrganizationID;
    @FXML Label TargetEndpointURL;
    @FXML Button MultiOrgAnalysisSummaryPageButton;
    @FXML Button SourceOrgSecurityHealthCheckDetailsButton;
    @FXML Button SourceOrgApexOrgWideCoverageDetailsButton;
    @FXML Button TargetOrgSecurityHealthCheckDetailsButton;
    @FXML Button TargetOrgApexOrgWideCoverageDetailsButton;
    @FXML Label SourceSecHealthCheckScoreLabel;
    @FXML Label SourceApexOrgWideCodeCoverageScoreLabel;
    @FXML Label TargetSecHealthCheckScoreLabel;
    @FXML Label TargetApexOrgWideCodeCoverageScoreLabel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	    FirstRadioButton.setToggleGroup(toggleGroup);
	    SecondRadioButton.setToggleGroup(toggleGroup);
	    //Display basic information about logged in User and Organization details of Source Org
	    SourceSalesforceUserName.setText("Source Org Username : "+Config.SFDC_MULTI_SOURCE_USERNAME);
	    SourceOrganizationEdition.setText("Source Org Edition : "+Config.SFDC_MULTI_SOURCE_ORGTYPE);
	    SourceIsSandbox.setText("Is it a Sandbox? : "+Config.SFDC_MULTI_SOURCE_ISSANDBOX);
	    SourceOrganizationName.setText("Source Org Name : "+Config.SFDC_MULTI_SOURCE_ORGNAME);
	    SourceOrganizationID.setText("Source Org Id : "+Config.SFDC_MULTI_SOURCE_ORGID);
	    SourceEndpointURL.setText("Source Org Endpoint : "+Config.OAUTH_INSTANCEURL_MULTI_SOURCE);
	    //Display basic information about logged in User and Organization details of Target Org
	    TargetSalesforceUserName.setText("Target Org Username : "+Config.SFDC_MULTI_TARGET_USERNAME);
	    TargetOrganizationEdition.setText("Target Org Edition : "+Config.SFDC_MULTI_TARGET_ORGTYPE);
	    TargetIsSandbox.setText("Is it a Sandbox? : "+Config.SFDC_MULTI_TARGET_ISSANDBOX);
	    TargetOrganizationName.setText("Target Org Name : "+Config.SFDC_MULTI_TARGET_ORGNAME);
	    TargetOrganizationID.setText("Target Org Id : "+Config.SFDC_MULTI_TARGET_ORGID);
	    TargetEndpointURL.setText("Target Org Endpoint : "+Config.OAUTH_INSTANCEURL_MULTI_TARGET);
	    
		OAuthTokenFlow.generateRequestSpecification("MultiOrgSource");
        OAuthTokenFlow.generateRequestSpecification("MultiOrgTarget");
	    
		//Showing Security Health Check score of Source Org
	    SourceSecHealthCheckScoreLabel.setText(Config.SFDC_MULTI_SOURCE_SECURITY_HEALTHCHECK_SCORE+"%");
	    SourceSecHealthCheckScoreLabel.setTextFill(Color.WHITE);
	    SourceSecHealthCheckScoreLabel.setStyle("-fx-font: 48 arial;-fx-font-weight: bold;"); 
        //Showing Apex Org Wide Coverage score of Source Org
	    SourceApexOrgWideCodeCoverageScoreLabel.setText(Config.SFDC_MULTI_SOURCE_APEXORGWIDECODECOVERAGE_SCORE+"%");
	    SourceApexOrgWideCodeCoverageScoreLabel.setTextFill(Color.WHITE);
	    SourceApexOrgWideCodeCoverageScoreLabel.setStyle("-fx-font: 48 arial;-fx-font-weight: bold;");
		//Showing Security Health Check score of Target Org
	    TargetSecHealthCheckScoreLabel.setText(Config.SFDC_MULTI_TARGET_SECURITY_HEALTHCHECK_SCORE+"%");
	    TargetSecHealthCheckScoreLabel.setTextFill(Color.WHITE);
	    TargetSecHealthCheckScoreLabel.setStyle("-fx-font: 48 arial;-fx-font-weight: bold;");
        //Showing Apex Org Wide Coverage score of Target Org
	    TargetApexOrgWideCodeCoverageScoreLabel.setText(Config.SFDC_MULTI_TARGET_APEXORGWIDECODECOVERAGE_SCORE+"%");
	    TargetApexOrgWideCodeCoverageScoreLabel.setTextFill(Color.WHITE);
	    TargetApexOrgWideCodeCoverageScoreLabel.setStyle("-fx-font: 48 arial;-fx-font-weight: bold;");
	}
	
    private JSONArray getSalesforceToolingRecords(String Query) throws JSONException {
	    Response Risk_Response = SFDCContext.runToolingQuery(Query);
	    //System.out.println("Risk_Response:"+Risk_Response.asPrettyString());
	    JSONObject resultsJObject = new JSONObject(Risk_Response.body().asPrettyString());
		JSONArray jsonRecordsArray = resultsJObject.optJSONArray("records");
		return jsonRecordsArray;
    }

    @FXML private void switchToMultiOrgAnalysisSummaryPage() throws IOException {
        // add a change listener 
        RadioButton selectedRadioButton = (RadioButton)toggleGroup.getSelectedToggle(); 
        if (selectedRadioButton != null) { 
            String selectedRadioButtonText = selectedRadioButton.getText(); 
            String selectedRadioButtonId = selectedRadioButton.getId();
            log.info("Selected Radio Button:"+selectedRadioButtonText+" And the ID is:"+selectedRadioButtonId);
            if(selectedRadioButtonId.equalsIgnoreCase("FirstRadioButton")) {
            	log.info("Moving to Multi Org Analysis Summary Specific Components Page");
            	App.setRoot("MultiOrgAnalysisSummarySpecificComponentsPage");
            }else if(selectedRadioButtonId.equalsIgnoreCase("SecondRadioButton")) {
            	log.info("Moving to Multi Org Analysis Summary Whole Org Page"); 
            	App.setRoot("MultiOrgAnalysisSummaryWholeOrgPage");
            }else {
            	log.info("Please select your choice first");
            }
        }    
    }
    
    @FXML private void SourceOrgSecurityHealthCheckDetails() throws Exception {
    	log.info("Implementing code to see Security Health Check Score in details!!!");
    	int highRiskCount = 0, mediumRiskCount = 0, meetStandardCount = 0;
    	//Old Code of showing Security Health Check Score
    	/*
    	SFDCSecurityHealthCheckDetailsPage sfdc_shcd = new SFDCSecurityHealthCheckDetailsPage();
    	Stage primaryStage = new Stage();
    	sfdc_shcd.start(primaryStage);
    	*/
    	if(DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.size() > 0) {
    		DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.clear();
    	}
    	OAuthTokenFlow.generateRequestSpecification("MultiOrgSource");
    	String hcs_HighRisk_Query = "SELECT RiskType, Setting, SettingGroup, OrgValue, StandardValue FROM SecurityHealthCheckRisks where SettingRiskCategory='HIGH_RISK'";
    	log.info("Running Query = "+hcs_HighRisk_Query);
    	JSONArray jsonHighRiskArray = getSalesforceToolingRecords(hcs_HighRisk_Query);
	    int totalSizeHighRisk = jsonHighRiskArray.length();
	    log.info("Total Records :"+totalSizeHighRisk);
	    for(int i=0;i<totalSizeHighRisk;i++) {
			JSONObject jsonRecords = new JSONObject(jsonHighRiskArray.get(i).toString());
			if(jsonRecords.getString("RiskType").contentEquals("HIGH_RISK")) {
				highRiskCount += highRiskCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("High Risk Security Settings", "Critical", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEDIUM_RISK")) {
				mediumRiskCount += mediumRiskCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("High Risk Security Settings", "Warning", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEETS_STANDARD")) {
				meetStandardCount += meetStandardCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("High Risk Security Settings", "Compliant", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}
	    }

    	//Below lines of code to add Medium Risk Security settings in Tree Table View
    	String hcs_MediumRisk_Query = "SELECT RiskType, Setting, SettingGroup, OrgValue, StandardValue FROM SecurityHealthCheckRisks where SettingRiskCategory='MEDIUM_RISK'";
    	log.info("Running Query = "+hcs_MediumRisk_Query);
    	JSONArray jsonMediumRiskArray = getSalesforceToolingRecords(hcs_MediumRisk_Query);
	    int totalSizeMediumRisk = jsonMediumRiskArray.length();
	    log.info("Total Records :"+totalSizeMediumRisk);
	    for(int i=0;i<totalSizeMediumRisk;i++) {
			JSONObject jsonRecords = new JSONObject(jsonMediumRiskArray.get(i).toString());
			if(jsonRecords.getString("RiskType").contentEquals("HIGH_RISK")) {
				highRiskCount += highRiskCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("Medium Risk Security Settings", "Critical", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEDIUM_RISK")) {
				mediumRiskCount += mediumRiskCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("Medium Risk Security Settings", "Warning", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEETS_STANDARD")) {
				meetStandardCount += meetStandardCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("Medium Risk Security Settings", "Compliant", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}
	    }
	    
    	//Below lines of code to add Medium Risk Security settings in Tree Table View
    	String hcs_LowRisk_Query = "SELECT RiskType, Setting, SettingGroup, OrgValue, StandardValue FROM SecurityHealthCheckRisks where SettingRiskCategory='LOW_RISK'";
    	log.info("Running Query = "+hcs_LowRisk_Query);
    	JSONArray jsonLowRiskArray = getSalesforceToolingRecords(hcs_LowRisk_Query);
	    int totalSizeLowRisk = jsonLowRiskArray.length();
	    log.info("Total Records :"+totalSizeLowRisk);
	    for(int i=0;i<totalSizeLowRisk;i++) {
			JSONObject jsonRecords = new JSONObject(jsonLowRiskArray.get(i).toString());
			if(jsonRecords.getString("RiskType").contentEquals("HIGH_RISK")) {
				highRiskCount += highRiskCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("Low Risk Security Settings", "Critical", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEDIUM_RISK")) {
				mediumRiskCount += mediumRiskCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("Low Risk Security Settings", "Warning", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEETS_STANDARD")) {
				meetStandardCount += meetStandardCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("Low Risk Security Settings", "Compliant", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}
	    } 
	    
    	//Below lines of code to add Medium Risk Security settings in Tree Table View
    	String hcs_Informational_Query = "SELECT RiskType, Setting, SettingGroup, OrgValue, StandardValue FROM SecurityHealthCheckRisks where SettingRiskCategory='INFORMATIONAL'";
    	log.info("Running Query = "+hcs_Informational_Query);
    	JSONArray jsonInformationalArray = getSalesforceToolingRecords(hcs_Informational_Query);
	    int totalSizeInformational = jsonInformationalArray.length();
	    log.info("Total Records :"+totalSizeInformational);
	    for(int i=0;i<totalSizeInformational;i++) {
			JSONObject jsonRecords = new JSONObject(jsonInformationalArray.get(i).toString());
			if(jsonRecords.getString("RiskType").contentEquals("HIGH_RISK")) {
				highRiskCount += highRiskCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("Informational Security Settings", "Critical", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEDIUM_RISK")) {
				mediumRiskCount += mediumRiskCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("Informational Security Settings", "Warning", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEETS_STANDARD")) {
				meetStandardCount += meetStandardCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("Informational Security Settings", "Compliant", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}
	    }
 
	    log.info("************************************");
	    //log.info(DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData);
		DisplaySecurityHealthCheckDetailsController sfdc_dshcd = new DisplaySecurityHealthCheckDetailsController();
    	Stage primaryStage = new Stage();
    	sfdc_dshcd.start(primaryStage);
    	
    }
    
    @FXML private void SourceOrgApexOrgWideCoverageDetails() throws IOException, JSONException {
    	log.info("Implement code to see Apex Org Wide Coverage Score in details!!!");

    	OAuthTokenFlow.generateRequestSpecification("MultiOrgSource");
    	if(DisplayApexCodeCoverageDetailsController.masterApexCodeCoverageData.size() > 0) {
    		DisplayApexCodeCoverageDetailsController.masterApexCodeCoverageData.clear();
    	}
    	String apexOrgWideCoverageQuery = "SELECT ApexTestClassId,TestMethodName,ApexClassorTriggerId,NumLinesCovered,NumLinesUncovered,Coverage FROM ApexCodeCoverage";
    	log.info("Running Query = "+apexOrgWideCoverageQuery);
    	Response apexOrgWideCoverageResponse = SFDCContext.runToolingQuery(apexOrgWideCoverageQuery);
	    //System.out.println("apexOrgWideCoverageResponse:"+apexOrgWideCoverageResponse.asPrettyString());
	    JSONObject resultsJObject = new JSONObject(apexOrgWideCoverageResponse.body().asPrettyString());
		JSONArray jsonRecordsArray = resultsJObject.optJSONArray("records");

	    int totalSize = jsonRecordsArray.length();
	    int totalCount = 0;
	    log.info("Total Records :"+totalSize);
	    if(totalSize > 0) {
		    for(int i=0;i<totalSize;i++) {
				JSONObject jsonRecords = new JSONObject(jsonRecordsArray.get(i).toString());
				double NumLinesCovered = Integer.parseInt(jsonRecords.getString("NumLinesCovered"));
				double NumLinesUncovered = Integer.parseInt(jsonRecords.getString("NumLinesUncovered"));
				double CodeCoveragePercentage = (NumLinesCovered / (NumLinesCovered + NumLinesUncovered))*100;

				DisplayApexCodeCoverageDetailsController.masterApexCodeCoverageData.add(new DisplayApexCodeCoverageDetails(jsonRecords.getString("ApexTestClassId"), 
						jsonRecords.getString("TestMethodName"), jsonRecords.getString("ApexClassOrTriggerId"), String.valueOf(CodeCoveragePercentage+" %"), 
						jsonRecords.getString("NumLinesCovered"), jsonRecords.getString("NumLinesUncovered")));
				totalCount += totalCount;
		    }
	    }
	    log.info("***********************************************************************************");
		//System.out.println(DisplayApexCodeCoverageDetailsController.masterApexCodeCoverageData);
		DisplayApexCodeCoverageDetailsController sfdc_daccdc = new DisplayApexCodeCoverageDetailsController();
    	Stage primaryStage = new Stage();
    	sfdc_daccdc.start(primaryStage);
    }
    
    @FXML private void TargetOrgSecurityHealthCheckDetails() throws Exception {
    	log.info("Implementing code to see Security Health Check Score in details!!!");
    	int highRiskCount = 0, mediumRiskCount = 0, meetStandardCount = 0;

    	if(DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.size() > 0) {
    		DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.clear();
    	}
    	OAuthTokenFlow.generateRequestSpecification("MultiOrgTarget");
    	String hcs_HighRisk_Query = "SELECT RiskType, Setting, SettingGroup, OrgValue, StandardValue FROM SecurityHealthCheckRisks where SettingRiskCategory='HIGH_RISK'";
    	log.info("Running Query = "+hcs_HighRisk_Query);
    	JSONArray jsonHighRiskArray = getSalesforceToolingRecords(hcs_HighRisk_Query);
	    int totalSizeHighRisk = jsonHighRiskArray.length();
	    log.info("Total Records :"+totalSizeHighRisk);
	    for(int i=0;i<totalSizeHighRisk;i++) {
			JSONObject jsonRecords = new JSONObject(jsonHighRiskArray.get(i).toString());
			if(jsonRecords.getString("RiskType").contentEquals("HIGH_RISK")) {
				highRiskCount += highRiskCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("High Risk Security Settings", "Critical", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEDIUM_RISK")) {
				mediumRiskCount += mediumRiskCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("High Risk Security Settings", "Warning", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEETS_STANDARD")) {
				meetStandardCount += meetStandardCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("High Risk Security Settings", "Compliant", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}
	    }

    	//Below lines of code to add Medium Risk Security settings in Tree Table View
    	String hcs_MediumRisk_Query = "SELECT RiskType, Setting, SettingGroup, OrgValue, StandardValue FROM SecurityHealthCheckRisks where SettingRiskCategory='MEDIUM_RISK'";
    	log.info("Running Query = "+hcs_MediumRisk_Query);
    	JSONArray jsonMediumRiskArray = getSalesforceToolingRecords(hcs_MediumRisk_Query);
	    int totalSizeMediumRisk = jsonMediumRiskArray.length();
	    log.info("Total Records :"+totalSizeMediumRisk);
	    for(int i=0;i<totalSizeMediumRisk;i++) {
			JSONObject jsonRecords = new JSONObject(jsonMediumRiskArray.get(i).toString());
			if(jsonRecords.getString("RiskType").contentEquals("HIGH_RISK")) {
				highRiskCount += highRiskCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("Medium Risk Security Settings", "Critical", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEDIUM_RISK")) {
				mediumRiskCount += mediumRiskCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("Medium Risk Security Settings", "Warning", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEETS_STANDARD")) {
				meetStandardCount += meetStandardCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("Medium Risk Security Settings", "Compliant", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}
	    }
	    
    	//Below lines of code to add Medium Risk Security settings in Tree Table View
    	String hcs_LowRisk_Query = "SELECT RiskType, Setting, SettingGroup, OrgValue, StandardValue FROM SecurityHealthCheckRisks where SettingRiskCategory='LOW_RISK'";
    	log.info("Running Query = "+hcs_LowRisk_Query);
    	JSONArray jsonLowRiskArray = getSalesforceToolingRecords(hcs_LowRisk_Query);
	    int totalSizeLowRisk = jsonLowRiskArray.length();
	    log.info("Total Records :"+totalSizeLowRisk);
	    for(int i=0;i<totalSizeLowRisk;i++) {
			JSONObject jsonRecords = new JSONObject(jsonLowRiskArray.get(i).toString());
			if(jsonRecords.getString("RiskType").contentEquals("HIGH_RISK")) {
				highRiskCount += highRiskCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("Low Risk Security Settings", "Critical", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEDIUM_RISK")) {
				mediumRiskCount += mediumRiskCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("Low Risk Security Settings", "Warning", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEETS_STANDARD")) {
				meetStandardCount += meetStandardCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("Low Risk Security Settings", "Compliant", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}
	    } 
	    
    	//Below lines of code to add Medium Risk Security settings in Tree Table View
    	String hcs_Informational_Query = "SELECT RiskType, Setting, SettingGroup, OrgValue, StandardValue FROM SecurityHealthCheckRisks where SettingRiskCategory='INFORMATIONAL'";
    	log.info("Running Query = "+hcs_Informational_Query);
    	JSONArray jsonInformationalArray = getSalesforceToolingRecords(hcs_Informational_Query);
	    int totalSizeInformational = jsonInformationalArray.length();
	    log.info("Total Records :"+totalSizeInformational);
	    for(int i=0;i<totalSizeInformational;i++) {
			JSONObject jsonRecords = new JSONObject(jsonInformationalArray.get(i).toString());
			if(jsonRecords.getString("RiskType").contentEquals("HIGH_RISK")) {
				highRiskCount += highRiskCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("Informational Security Settings", "Critical", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEDIUM_RISK")) {
				mediumRiskCount += mediumRiskCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("Informational Security Settings", "Warning", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEETS_STANDARD")) {
				meetStandardCount += meetStandardCount;
				DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.add(new DisplaySecurityHealthCheckDetails("Informational Security Settings", "Compliant", 
						jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"), jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue")));
			}
	    }
 
	    log.info("********************************************************************************************************");
		//System.out.println(DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData);
		DisplaySecurityHealthCheckDetailsController sfdc_dshcd = new DisplaySecurityHealthCheckDetailsController();
    	Stage primaryStage = new Stage();
    	sfdc_dshcd.start(primaryStage);
    	
    }
    
    @FXML private void TargetOrgApexOrgWideCoverageDetails() throws IOException, JSONException {
    	log.info("Implement code to see Apex Org Wide Coverage Score in details!!!");

    	OAuthTokenFlow.generateRequestSpecification("MultiOrgTarget");
    	if(DisplayApexCodeCoverageDetailsController.masterApexCodeCoverageData.size() > 0) {
    		DisplayApexCodeCoverageDetailsController.masterApexCodeCoverageData.clear();
    	}
    	String apexOrgWideCoverageQuery = "SELECT ApexTestClassId,TestMethodName,ApexClassorTriggerId,NumLinesCovered,NumLinesUncovered,Coverage FROM ApexCodeCoverage";
	    Response apexOrgWideCoverageResponse = SFDCContext.runToolingQuery(apexOrgWideCoverageQuery);
	    //System.out.println("apexOrgWideCoverageResponse:"+apexOrgWideCoverageResponse.asPrettyString());
	    JSONObject resultsJObject = new JSONObject(apexOrgWideCoverageResponse.body().asPrettyString());
		JSONArray jsonRecordsArray = resultsJObject.optJSONArray("records");

	    int totalSize = jsonRecordsArray.length();
	    int totalCount = 0;
	    log.info("Total Records :"+totalSize);
	    if(totalSize > 0) {
		    for(int i=0;i<totalSize;i++) {
				JSONObject jsonRecords = new JSONObject(jsonRecordsArray.get(i).toString());
				double NumLinesCovered = Integer.parseInt(jsonRecords.getString("NumLinesCovered"));
				double NumLinesUncovered = Integer.parseInt(jsonRecords.getString("NumLinesUncovered"));
				double CodeCoveragePercentage = (NumLinesCovered / (NumLinesCovered + NumLinesUncovered))*100;

				DisplayApexCodeCoverageDetailsController.masterApexCodeCoverageData.add(new DisplayApexCodeCoverageDetails(jsonRecords.getString("ApexTestClassId"), 
						jsonRecords.getString("TestMethodName"), jsonRecords.getString("ApexClassOrTriggerId"), String.valueOf(CodeCoveragePercentage+" %"), 
						jsonRecords.getString("NumLinesCovered"), jsonRecords.getString("NumLinesUncovered")));
				totalCount += totalCount;
		    }
	    }
	    log.info("*************************************************************************************************************");
		//System.out.println(DisplayApexCodeCoverageDetailsController.masterApexCodeCoverageData);
		DisplayApexCodeCoverageDetailsController sfdc_daccdc = new DisplayApexCodeCoverageDetailsController();
    	Stage primaryStage = new Stage();
    	sfdc_daccdc.start(primaryStage);
    }
    
    @FXML private void switchBackToPreviousPage() throws IOException {
    	log.info("Moving back to Previous Page");
        App.setRoot("MultiOrgLoginPage");
    }
    
    @FXML private void switchBackToHomePage() throws IOException {
    	log.info("Moving back to Home Page");
        App.setRoot("AppHomePage");
    }
    
    @FXML private void signOut() throws IOException {
    	App.setRoot("AppLauncherPage");
        //Platform.exit();
        //System.exit(0);
    }
   
}

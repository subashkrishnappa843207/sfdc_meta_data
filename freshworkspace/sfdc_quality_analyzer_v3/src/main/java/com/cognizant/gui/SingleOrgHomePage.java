package com.cognizant.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cognizant.login.OAuthTokenFlow;
import com.cognizant.sfdc_quality_analyzer_v3.App;
import com.cognizant.uicomponent.DisplayApexCodeCoverageDetails;
import com.cognizant.uicomponent.DisplayApexCodeCoverageDetailsController;
import com.cognizant.uicomponent.DisplayImpactedComponents;
import com.cognizant.uicomponent.DisplayImpactedComponentsController;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class SingleOrgHomePage implements Initializable {
    private static final Logger log = Logger.getLogger(SingleOrgHomePage.class); 
    @FXML RadioButton FirstRadioButton;
    @FXML RadioButton SecondRadioButton;
    @FXML RadioButton ThirdRadioButton;
    @FXML RadioButton FourthRadioButton;
    @FXML RadioButton FifthRadioButton;
    ToggleGroup toggleGroup = new ToggleGroup();
    @FXML TextField TextSalesforceUserName;
    @FXML TextField TextOrganizationEdition;
    @FXML TextField isSandbox;
    @FXML TextField TextOrganizationName;
    @FXML TextField TextOrganizationID;
    @FXML TextField TextEndpointURL;
    @FXML PieChart SecurityHealthCheckChart;
    @FXML PieChart ApexOrgWideCoverageChart;
    @FXML AnchorPane SecurityHealthCheckAnchorPane;
    @FXML AnchorPane ApexOrgWideCoverageAnchorPane;
    @FXML BorderPane SecurityHealthCheckBorderPane;
    @FXML BorderPane ApexOrgWideCoverageBorderPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	    FirstRadioButton.setToggleGroup(toggleGroup);
	    SecondRadioButton.setToggleGroup(toggleGroup);
	    ThirdRadioButton.setToggleGroup(toggleGroup);
	    FourthRadioButton.setToggleGroup(toggleGroup);
	    FifthRadioButton.setToggleGroup(toggleGroup);

		OAuthTokenFlow.generateRequestSpecification("SingleOrg");
		//Creating Security Health Check score DoughnutChart
		int intHealthCheckScore = Integer.parseInt(Config.SFDC_SINGLE_SECURITY_HEALTHCHECK_SCORE);
    	ObservableList<PieChart.Data> pieChartData_sechealth = FXCollections.observableArrayList(
                new PieChart.Data("Secured", intHealthCheckScore),
                new PieChart.Data("Not Secured", (100 - intHealthCheckScore)));
    	Label caption_sechealth = new Label();
        caption_sechealth.setTextFill(Color.WHITE);
        caption_sechealth.setStyle("-fx-font: 12 arial;");   
        final DoughnutChart chart_sechealth = new DoughnutChart(pieChartData_sechealth);
        chart_sechealth.setLabelsVisible(false);
        chart_sechealth.setLegendVisible(false);
        SecurityHealthCheckBorderPane.setCenter(chart_sechealth);
        chart_sechealth.getData().stream().forEach(data_sechealth ->{
            data_sechealth.getNode().addEventHandler(MouseEvent.ANY, e->{
            	caption_sechealth.setText(data_sechealth.getName()+" % : "+String.valueOf(data_sechealth.getPieValue())+" %");
            });
        });

        SecurityHealthCheckBorderPane.setBottom(caption_sechealth);
        SecurityHealthCheckBorderPane.setMargin(caption_sechealth, new Insets(0, 0, 10, 120));
        //SecurityHealthCheckPane.getChildren().add(chart_sechealth);
        
        //Creating Apex Org Wide Coverage score DoughnutChart
        int intApexOrgWideCoverage = Integer.parseInt(Config.SFDC_SINGLE_APEXORGWIDECODECOVERAGE_SCORE);
    	ObservableList<PieChart.Data> pieChartData_apexcodecoverage = FXCollections.observableArrayList(
                new PieChart.Data("Covered", intApexOrgWideCoverage),
                new PieChart.Data("Not Covered", (100 - intApexOrgWideCoverage)));
    	Label caption_apexcodecoverage = new Label();
    	caption_apexcodecoverage.setTextFill(Color.WHITE);
    	caption_apexcodecoverage.setStyle("-fx-font: 12 arial;");  
        final DoughnutChart chart_apexcodecoverage = new DoughnutChart(pieChartData_apexcodecoverage);
        chart_apexcodecoverage.setLabelsVisible(false);
        chart_apexcodecoverage.setLegendVisible(false);
        ApexOrgWideCoverageBorderPane.setCenter(chart_apexcodecoverage);
        chart_apexcodecoverage.getData().stream().forEach(data_apexcodecoverage ->{
            data_apexcodecoverage.getNode().addEventHandler(MouseEvent.ANY, e->{
            	caption_apexcodecoverage.setText(data_apexcodecoverage.getName()+" Code % : "+String.valueOf(data_apexcodecoverage.getPieValue())+" %");
            });
        });

        ApexOrgWideCoverageBorderPane.setBottom(caption_apexcodecoverage);
        ApexOrgWideCoverageBorderPane.setMargin(caption_apexcodecoverage, new Insets(0, 0, 10, 120));
        //ApexOrgWideCoveragePane.getChildren().add(chart_apexcodecoverage);
        
	    //Display basic information about logged in User and Organization details on Home page
		TextOrganizationID.setText(Config.SFDC_SINGLE_ORGID);
		TextSalesforceUserName.setText(Config.SFDC_SINGLE_USERNAME);
		TextOrganizationEdition.setText(Config.SFDC_SINGLE_ORGTYPE);
		isSandbox.setText(Config.SFDC_SINGLE_ISSANDBOX);
		TextOrganizationName.setText(Config.SFDC_SINGLE_ORGNAME);
		TextEndpointURL.setText(Config.OAUTH_INSTANCEURL_SINGLE);
	}
	
    private JSONArray getSalesforceToolingRecords(String Query) throws JSONException {
	    Response Risk_Response = SFDCContext.runToolingQuery(Query);
	    //System.out.println("Risk_Response:"+Risk_Response.asPrettyString());
	    JSONObject resultsJObject = new JSONObject(Risk_Response.body().asPrettyString());
		JSONArray jsonRecordsArray = resultsJObject.optJSONArray("records");
		return jsonRecordsArray;
    }

    @FXML
    private void switchToSingleOrgAnalysisSummaryPage() throws IOException {
        
        // add a change listener 
        RadioButton selectedRadioButton = (RadioButton)toggleGroup.getSelectedToggle(); 
        if (selectedRadioButton != null) { 
            String selectedRadioButtonText = selectedRadioButton.getText(); 
            String selectedRadioButtonId = selectedRadioButton.getId();
            log.info("Selected Radio Button:"+selectedRadioButtonText+" And the ID is:"+selectedRadioButtonId);
            if(selectedRadioButtonId.equalsIgnoreCase("FirstRadioButton")) {
            	App.setRoot("SingleOrgAnalysisSummaryPageWholeOrgSingleDate");
            }else if(selectedRadioButtonId.equalsIgnoreCase("SecondRadioButton")) {
            	App.setRoot("SingleOrgAnalysisSummaryPageWholeOrgMultiDate");
            }else if(selectedRadioButtonId.equalsIgnoreCase("ThirdRadioButton")) {
            	App.setRoot("SingleOrgAnalysisSummaryPageSpecificComponentSingleDate");
            }else if(selectedRadioButtonId.equalsIgnoreCase("FourthRadioButton")) {
            	App.setRoot("SingleOrgAnalysisSummaryPageSpecificComponentMultiDate");
            }else if(selectedRadioButtonId.equalsIgnoreCase("FifthRadioButton")) {
            	App.setRoot("SingleOrgAnalysisSummaryPageQuickView");
            }else {
            	log.info("Please select your choice first");
            }
        } 
    }
    
    @FXML private void SecurityHealthCheckDetailsButton() throws Exception {
    	log.info("Implementing code to see Security Health Check Score in details!!!");
    	int highRiskCount = 0, mediumRiskCount = 0, meetStandardCount = 0;

    	if(DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.size() > 0) {
    		DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData.clear();
    	}

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
    
    @FXML
    private void ApexOrgWideCoverageDetailsButton() throws IOException, JSONException {
    	log.info("Implement code to see Apex Org Wide Coverage Score in details!!!");
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
	    log.info("************************************");
	    //log.info(DisplayApexCodeCoverageDetailsController.masterApexCodeCoverageData);
		DisplayApexCodeCoverageDetailsController sfdc_daccdc = new DisplayApexCodeCoverageDetailsController();
    	Stage primaryStage = new Stage();
    	sfdc_daccdc.start(primaryStage);
    }
    
    @FXML
    private void switchBackToPreviousPage() throws IOException {
        App.setRoot("SingleOrgLoginPage");
    }
    
    @FXML
    private void switchBackToHomePage() throws IOException {
        App.setRoot("AppHomePage");
    }
    
    @FXML
    private void signOut() throws IOException {
    	App.setRoot("AppLauncherPage");
        //Platform.exit();
        //System.exit(0);
    }
    

    


}

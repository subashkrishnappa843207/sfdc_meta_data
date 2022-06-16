package com.cognizant.gui;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cognizant.login.OAuthToken;
import com.cognizant.login.OAuthTokenFlow;
import com.cognizant.sfdc_quality_analyzer_v3.App;
import com.cognizant.utility.Config;
import com.cognizant.utility.ExcelUtils;
import com.cognizant.utility.JsonUtils;
import com.cognizant.utility.SFDCContext;
import com.cognizant.utility.XMLUtils;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;


public class MultiOrgLoginPage implements Initializable {
	private static final Logger log = Logger.getLogger(MultiOrgLoginPage.class);
	Config config;
	public int cnt= 1;
	ExcelUtils excelUtil = new ExcelUtils();
	JsonUtils jsonUtil = new JsonUtils();
	XMLUtils xmlUtil = new XMLUtils();
    @FXML ComboBox<String> sourceEnvironmentList;
    @FXML ComboBox<String> sourceAPIVersionList;
    @FXML ComboBox<String> targetEnvironmentList;
    @FXML ComboBox<String> targetAPIVersionList;
    @FXML Button multiOrgSourceUserLoginButton;
    @FXML Button multiOrgTargetUserLoginButton;
    @FXML Button MultiOrgHomepageButton;
    @FXML Label SourceOrgUserName;
    @FXML Label SourceOrgInstanceURL;
    @FXML Label SourceOrgID;
    @FXML Label TargetOrgUserName;
    @FXML Label TargetOrgInstanceURL;
    @FXML Label TargetOrgID;
    @FXML ImageView SourceLoginSuccessImageview;
    @FXML ImageView TargetLoginSuccessImageview;
    
    boolean sourceOrgLoginSuccess = false;
    boolean targetOrgLoginSuccess = false;
    public static String SourceOrgSuccessURL = null;
    public static Map<String, String> SourceOrgSuccessURLParams = new HashMap<>();
    public static String TargetOrgSuccessURL = null;
    public static Map<String, String> TargetOrgSuccessURLParams = new HashMap<>();
	ArrayList<String> InScopeItems = new ArrayList<String>(); 
	ArrayList<String> OutScopeItems = new ArrayList<String>(); 
    private ObservableList<String> cbSourceOrgEnvironmentItemsList = FXCollections.observableArrayList("Production", "Sandbox");
    private ObservableList<String> cbSourceAPIVersionItemsList = FXCollections.observableArrayList("52.0", "51.0", "50.0", "49.0", "48.0", "47.0", "46.0", "45.0" , "44.0",
    		"43.0", "42.0", "41.0", "40.0", "39.0", "38.0", "37.0", "36.0", "35.0", "34.0", "33.0", "32.0", "31.0", "30.0");
    private ObservableList<String> cbTargetOrgEnvironmentItemsList = FXCollections.observableArrayList("Production", "Sandbox");
    private ObservableList<String> cbTargetAPIVersionItemsList = FXCollections.observableArrayList("52.0", "51.0", "50.0", "49.0", "48.0", "47.0", "46.0", "45.0" , "44.0",
    		"43.0", "42.0", "41.0", "40.0", "39.0", "38.0", "37.0", "36.0", "35.0", "34.0", "33.0", "32.0", "31.0", "30.0");
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		sourceEnvironmentList.setItems(cbSourceOrgEnvironmentItemsList);
		sourceAPIVersionList.setItems(cbSourceAPIVersionItemsList);
		targetEnvironmentList.setItems(cbTargetOrgEnvironmentItemsList);
		targetAPIVersionList.setItems(cbTargetAPIVersionItemsList);
	}
    
    @FXML
    private void multiOrgSourceOrgUserLogin() throws IOException, URISyntaxException {
	    log.info("sourceEnvironmentList.getValue(): " + sourceEnvironmentList.getSelectionModel().getSelectedItem());
	    log.info("sourceAPIVersionList.getValue(): " + sourceAPIVersionList.getSelectionModel().getSelectedItem());
	    Config.OAUTH_ENVIRONMENT_MULTI_SOURCE = sourceEnvironmentList.getSelectionModel().getSelectedItem();
	    Config.OAUTH_API_VERSION_MULTI_SOURCE = sourceAPIVersionList.getSelectionModel().getSelectedItem();
	    //App.setRoot("SingleOrgHomePage");
	    SFDCContext.setVersion("MultiOrgSource");
    	OAuthToken oAuthToken = new OAuthToken();
    	OAuthTokenFlow oAuthTokenFlow = new OAuthTokenFlow(config);
        //System.out.println(oAuthTokenFlow.getStartUrl(Config.OAUTH_ENVIRONMENT_MULTI_SOURCE, "SingleOrg"));
        Stage stage = new Stage();
        WebView webView = new WebView();   
        final WebEngine webEngine = webView.getEngine();
        webEngine.load(oAuthTokenFlow.getStartUrl(Config.OAUTH_ENVIRONMENT_MULTI_SOURCE, "SingleOrg"));
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
            	cnt = cnt+1;
            	System.out.println("Count = "+cnt);
            	System.out.println("Old State = "+oldState);
            	System.out.println("New State = "+newState);
                if (newState == State.SUCCEEDED) {
                    stage.setTitle(webEngine.getTitle());
                    //System.out.println(webEngine.getUserAgent());
                    SourceOrgSuccessURL = webEngine.getLocation();
                    //System.out.println("URL Details = "+webEngine.getLocation());
                    if(SourceOrgSuccessURL.contains("access_token")){
            	        try {
            	        	String urlString2Decode = SourceOrgSuccessURL;
            	        	String decodedURL = URLDecoder.decode(urlString2Decode, "UTF-8");
            	        	URL url = new URL(decodedURL);
            	        	URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            	        	String decodedURLAsString = uri.toASCIIString();
							OAuthTokenFlow.handleCompletedUrl(decodedURLAsString,"MultiOrgSource");
							log.info("Instance URL for Source Org Login:"+Config.OAUTH_INSTANCEURL_MULTI_SOURCE);
							stage.close();
							RequestSpecification requestSpec = OAuthTokenFlow.generateRequestSpecification("MultiOrgSource");
							sourceOrgLoginSuccess = true;
					        File fileSourceLoginSuccessPath = new File(System.getProperty("user.dir")+"\\target\\classes\\com\\cognizant\\sfdc_quality_analyzer_v3\\images\\icons8-user-shield.png");
					        Image SourceLoginSuccessImage = new Image(fileSourceLoginSuccessPath.toURI().toString());
					        SourceLoginSuccessImageview.setImage(SourceLoginSuccessImage);
							setSourceBasicOrgDetails();
						    SourceOrgUserName.setText("Logged in Username : "+Config.SFDC_MULTI_SOURCE_USERNAME);;
						    SourceOrgInstanceURL.setText("Endpoint URL : "+Config.OAUTH_INSTANCEURL_MULTI_SOURCE);;
						    SourceOrgID.setText("Org Id : "+Config.SFDC_MULTI_SOURCE_ORGID);;
						    findSourceOrgHealthCheckScore();
						    findSourceOrgApexOrgWideCoverageScore();
							setScopeItemList();
							//App.setRoot("SingleOrgHomePage");
						}catch (IOException e) {
							log.error(e.getMessage());
						} catch (URISyntaxException e) {
							log.error(e.getMessage());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							log.error(e.getMessage());
						}
                	}else {
                		sourceOrgLoginSuccess = false;
                	}

                }
            }
        });

        VBox root = new VBox();
        root.getChildren().add(webView);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();  
        
	    
	    sourceOrgLoginSuccess = true;

        /******************************************************************************************/
    }
    
    @FXML
    private void multiOrgTargetOrgUserLogin() throws IOException, URISyntaxException {
    	log.info("targetEnvironmentList.getValue(): " + targetEnvironmentList.getSelectionModel().getSelectedItem());
    	log.info("targetAPIVersionList.getValue(): " + targetAPIVersionList.getSelectionModel().getSelectedItem());
	    Config.OAUTH_ENVIRONMENT_MULTI_TARGET = targetEnvironmentList.getSelectionModel().getSelectedItem();
	    Config.OAUTH_API_VERSION_MULTI_TARGET = targetAPIVersionList.getSelectionModel().getSelectedItem();
	    //App.setRoot("SingleOrgHomePage");
	    SFDCContext.setVersion("MultiOrgTarget");
    	OAuthToken oAuthToken = new OAuthToken();
    	OAuthTokenFlow oAuthTokenFlow = new OAuthTokenFlow(config);
    	//log.info(oAuthTokenFlow.getStartUrl(Config.OAUTH_ENVIRONMENT_MULTI_TARGET, "MultiOrg"));
        Stage stage = new Stage();
        WebView webView = new WebView();   
        final WebEngine webEngine = webView.getEngine();
        webEngine.load(oAuthTokenFlow.getStartUrl(Config.OAUTH_ENVIRONMENT_MULTI_TARGET, "MultiOrg"));
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
            	cnt = cnt+1;
            	System.out.println("Count = "+cnt);
            	System.out.println("Old State = "+oldState);
            	System.out.println("New State = "+newState);
                if (newState == State.SUCCEEDED) {
                    stage.setTitle(webEngine.getTitle());
                    System.out.println(webEngine.getUserAgent());
                    TargetOrgSuccessURL = webEngine.getLocation();
                    //log.info("URL Details = "+webEngine.getLocation());
                    if(TargetOrgSuccessURL.contains("access_token")){
            	        try {
            	        	String urlString2Decode = TargetOrgSuccessURL;
            	        	String decodedURL = URLDecoder.decode(urlString2Decode, "UTF-8");
            	        	URL url = new URL(decodedURL);
            	        	URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            	        	String decodedURLAsString = uri.toASCIIString();
							OAuthTokenFlow.handleCompletedUrl(decodedURLAsString,"MultiOrgTarget");
							log.info("Instance URL for Target Org Login:"+Config.OAUTH_INSTANCEURL_MULTI_TARGET);
							stage.close();
							OAuthTokenFlow.generateRequestSpecification("MultiOrgTarget");
							targetOrgLoginSuccess = true;
							File fileTargetLoginSuccessPath = new File(System.getProperty("user.dir")+"\\target\\classes\\com\\cognizant\\sfdc_quality_analyzer_v3\\images\\icons8-user-shield.png");
					        Image TargetLoginSuccessImage = new Image(fileTargetLoginSuccessPath.toURI().toString());
					        TargetLoginSuccessImageview.setImage(TargetLoginSuccessImage);
							setTargetBasicOrgDetails();
							TargetOrgUserName.setText("Logged in Username : "+Config.SFDC_MULTI_TARGET_USERNAME);;
						    TargetOrgInstanceURL.setText("Endpoint URL : "+Config.OAUTH_INSTANCEURL_MULTI_TARGET);;
						    TargetOrgID.setText("Org Id : "+Config.SFDC_MULTI_TARGET_ORGID);;
						    findTargetOrgHealthCheckScore();
						    findTargetOrgApexOrgWideCoverageScore();
							//setScopeItemList();
							//App.setRoot("SingleOrgHomePage");
							if((sourceOrgLoginSuccess == true) && (targetOrgLoginSuccess == true)) {
								MultiOrgHomepageButton.setDisable(false);
							}
						}catch (IOException e) {
							log.error(e.getMessage());
						} catch (URISyntaxException e) {
							log.error(e.getMessage());
						}
                	}else {
                		targetOrgLoginSuccess = false;
                	}

                }
            }
        });

        VBox root = new VBox();
        root.getChildren().add(webView);
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();  
        /*
	    targetOrgLoginSuccess = true;
        File fileTargetLoginSuccessPath = new File("src/main/resources/com/cognizant/sfdc_quality_analyzer_v3/images/icons8-user-shield.png");
        Image TargetLoginSuccessImage = new Image(fileTargetLoginSuccessPath.toURI().toString());
        TargetLoginSuccessImageview.setImage(TargetLoginSuccessImage);
		if((sourceOrgLoginSuccess == true) && (targetOrgLoginSuccess == true)) {
			MultiOrgHomepageButton.setDisable(false);
		}
		*/
        /******************************************************************************************/
    }
    
    @FXML
    private void switchToMultiOrgHomePage() throws IOException {
    	log.info("Moving to Multi Org Home Page");
    	App.setRoot("MultiOrgHomePage");
    }
    
    @FXML
    private void switchBackToPreviousPage() throws IOException {
    	log.info("Moving back to Previous Page");
        App.setRoot("SFDCOrgImpactAnalysisPage");
    }
    
    @FXML
    private void switchBackToHomePage() throws IOException {
    	log.info("Moving back to Home Page");
        App.setRoot("AppHomePage");
    }
    
    @FXML
    private void signOut() throws IOException {
    	App.setRoot("AppLauncherPage");
        //Platform.exit();
        //System.exit(0);
    }
    
    private void setSourceBasicOrgDetails() { 
	    Response userInfoResponse = SFDCContext.getUserDetails();
		//System.out.println("User Detail Response ="+userInfoResponse.asPrettyString());
	    Config.SFDC_MULTI_SOURCE_ORGID = userInfoResponse.jsonPath().getString("organization_id");
	    Config.SFDC_MULTI_SOURCE_USERNAME = userInfoResponse.jsonPath().getString("preferred_username");
		
		Response organizationDetailsResponse = SFDCContext.getOrganizationInformation(Config.SFDC_MULTI_SOURCE_ORGID);
		String organizationDetailsString = organizationDetailsResponse.body().asPrettyString();
		try {
			JSONObject resultsJObject = new JSONObject(organizationDetailsString);
			JSONArray resultsJArray1 = resultsJObject.optJSONArray("records");
			JSONObject j = new JSONObject(resultsJArray1.get(0).toString());
			log.info("organization_edition :"+j.getString("OrganizationType"));
			log.info("organization_name :"+j.getString("Name"));
			log.info("IsSandbox :"+j.getString("IsSandbox"));
			Config.SFDC_MULTI_SOURCE_ORGTYPE = j.getString("OrganizationType");
			Config.SFDC_MULTI_SOURCE_ORGNAME = j.getString("Name");
			Config.SFDC_MULTI_SOURCE_ISSANDBOX = j.getString("IsSandbox");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
    }
    
    private void setTargetBasicOrgDetails() { 
	    Response userInfoResponse = SFDCContext.getUserDetails();
		//System.out.println("User Detail Response ="+userInfoResponse.asPrettyString());
	    Config.SFDC_MULTI_TARGET_ORGID = userInfoResponse.jsonPath().getString("organization_id");
	    Config.SFDC_MULTI_TARGET_USERNAME = userInfoResponse.jsonPath().getString("preferred_username");
		
		Response organizationDetailsResponse = SFDCContext.getOrganizationInformation(Config.SFDC_MULTI_TARGET_ORGID);
		String organizationDetailsString = organizationDetailsResponse.body().asPrettyString();
		try {
			JSONObject resultsJObject = new JSONObject(organizationDetailsString);
			JSONArray resultsJArray1 = resultsJObject.optJSONArray("records");
			JSONObject j = new JSONObject(resultsJArray1.get(0).toString());
			log.info("organization_edition :"+j.getString("OrganizationType"));
			log.info("organization_name :"+j.getString("Name"));
			log.info("IsSandbox :"+j.getString("IsSandbox"));
			Config.SFDC_MULTI_TARGET_ORGTYPE = j.getString("OrganizationType");
			Config.SFDC_MULTI_TARGET_ORGNAME = j.getString("Name");
			Config.SFDC_MULTI_TARGET_ISSANDBOX = j.getString("IsSandbox");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
    }
    
    private void setScopeItemList() throws IOException, JSONException {
    	ArrayList<String> toolingSObjectList = new ArrayList<String>();
		//OAuthTokenFlow.generateRequestSpecification("SingleOrg");
		Response toolingResponse = SFDCContext.getToolingQuery();
	    JSONObject resultsJObject = new JSONObject(toolingResponse.body().asPrettyString());
		JSONArray jsonRecordsArray = resultsJObject.optJSONArray("sobjects");
		log.info(jsonRecordsArray.length());
		for(int i=0;i<jsonRecordsArray.length();i++) {
			JSONObject jsonRecords = new JSONObject(jsonRecordsArray.get(i).toString());
			toolingSObjectList.add(jsonRecords.get("name").toString());
		}
		//Separating in-scope items from whole Tooling Sobject items
		for(String item : toolingSObjectList) {
			if((excelUtil.getCellData("SOBJECT",item)) && (!excelUtil.getCellData("SOBJECT", "FieldName", item).contentEquals("OOS"))) {
				InScopeItems.add(item);
			}else {
				OutScopeItems.add(item);
			}
		}
		
		Config.SFDC_SINGLE_INSCOPE_ITEMS = InScopeItems;
		Config.SFDC_SINGLE_OUTSCOPE_ITEMS = OutScopeItems;
    }
    
    private void findSourceOrgHealthCheckScore() {
    	String healthCheckScoreQuery = "SELECT Score FROM SecurityHealthCheck";
	    Response healthCheckScoreResponse = SFDCContext.runToolingQuery(healthCheckScoreQuery);
	    //System.out.println("healthCheckScoreResponse:"+healthCheckScoreResponse.asPrettyString());
	    try {
			JSONObject resultsJObject = new JSONObject(healthCheckScoreResponse.body().asPrettyString());
			JSONArray resultsJArray1 = resultsJObject.optJSONArray("records");
			JSONObject j = new JSONObject(resultsJArray1.get(0).toString());
			String strHealthCheckScore = j.getString("Score");
			log.info("Org Health Check Score :"+j.getString("Score")+" and Value :"+strHealthCheckScore);
			//Setting the score value into Config parameter
			Config.SFDC_MULTI_SOURCE_SECURITY_HEALTHCHECK_SCORE = strHealthCheckScore;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}	
    }
    
    private void findTargetOrgHealthCheckScore() {
    	String healthCheckScoreQuery = "SELECT Score FROM SecurityHealthCheck";
	    Response healthCheckScoreResponse = SFDCContext.runToolingQuery(healthCheckScoreQuery);
	    //System.out.println("healthCheckScoreResponse:"+healthCheckScoreResponse.asPrettyString());
	    try {
			JSONObject resultsJObject = new JSONObject(healthCheckScoreResponse.body().asPrettyString());
			JSONArray resultsJArray1 = resultsJObject.optJSONArray("records");
			JSONObject j = new JSONObject(resultsJArray1.get(0).toString());
			String strHealthCheckScore = j.getString("Score");
			log.info("Org Health Check Score :"+j.getString("Score")+" and Value :"+strHealthCheckScore);
			//Setting the score value into Config parameter
			Config.SFDC_MULTI_TARGET_SECURITY_HEALTHCHECK_SCORE = strHealthCheckScore;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}	
    }
    
    private void findSourceOrgApexOrgWideCoverageScore() {
		String apexOrgWideCoverageScoreQuery = "SELECT PercentCovered FROM ApexOrgWideCoverage";
	    Response apexOrgWideCoverageResponse = SFDCContext.runToolingQuery(apexOrgWideCoverageScoreQuery);
	    //System.out.println("apexOrgWideCoverageResponse:"+apexOrgWideCoverageResponse.asPrettyString());
	    try {
			JSONObject resultsJObject = new JSONObject(apexOrgWideCoverageResponse.body().asPrettyString());
			JSONArray resultsJArray1 = resultsJObject.optJSONArray("records");
			JSONObject j = new JSONObject(resultsJArray1.get(0).toString());
			String strApexOrgWideCoverage = j.getString("PercentCovered");
			log.info("Apex Org Wide Coverage Score :"+j.getString("PercentCovered")+" and String Value :"+strApexOrgWideCoverage);
			Config.SFDC_MULTI_SOURCE_APEXORGWIDECODECOVERAGE_SCORE = strApexOrgWideCoverage;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}  
    }
    
    private void findTargetOrgApexOrgWideCoverageScore() {
		String apexOrgWideCoverageScoreQuery = "SELECT PercentCovered FROM ApexOrgWideCoverage";
	    Response apexOrgWideCoverageResponse = SFDCContext.runToolingQuery(apexOrgWideCoverageScoreQuery);
	    //System.out.println("apexOrgWideCoverageResponse:"+apexOrgWideCoverageResponse.asPrettyString());
	    try {
			JSONObject resultsJObject = new JSONObject(apexOrgWideCoverageResponse.body().asPrettyString());
			JSONArray resultsJArray1 = resultsJObject.optJSONArray("records");
			JSONObject j = new JSONObject(resultsJArray1.get(0).toString());
			String strApexOrgWideCoverage = j.getString("PercentCovered");
			log.info("Apex Org Wide Coverage Score :"+j.getString("PercentCovered")+" and String Value :"+strApexOrgWideCoverage);
			Config.SFDC_MULTI_TARGET_APEXORGWIDECODECOVERAGE_SCORE = strApexOrgWideCoverage;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}  
    }
}

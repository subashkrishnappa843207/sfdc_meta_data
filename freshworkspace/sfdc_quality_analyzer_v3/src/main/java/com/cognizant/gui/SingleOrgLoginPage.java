package com.cognizant.gui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class SingleOrgLoginPage implements Initializable {
	
	public int cnt= 1;
	ExcelUtils excelUtil = new ExcelUtils();
	JsonUtils jsonUtil = new JsonUtils();
	XMLUtils xmlUtil = new XMLUtils();
    private static final Logger log = Logger.getLogger(SingleOrgLoginPage.class); 
    //static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
    static final String RESPONSESERVICE = "/services/oauth2/authorize?response_type=token";
    static final String REDIRECT_URI = "https://login.salesforce.com/services/oauth2/success";
    public static String successURL = null;
    public static Map<String, String> successURLParams = new HashMap<>();
	ArrayList<String> InScopeItems = new ArrayList<String>(); 
	ArrayList<String> OutScopeItems = new ArrayList<String>(); 
    Config config;
    
    @FXML ComboBox<String> EnvironmentList;
    @FXML ComboBox<String> APIVersionList;
    private ObservableList<String> cbEnvironmentItemsList = FXCollections.observableArrayList("Production", "Sandbox");
    private ObservableList<String> cbAPIVersionItemsList = FXCollections.observableArrayList("52.0", "51.0", "50.0", "49.0", "48.0", "47.0", "46.0", "45.0" , "44.0",
    		"43.0", "42.0", "41.0", "40.0", "39.0", "38.0", "37.0", "36.0", "35.0", "34.0", "33.0", "32.0", "31.0", "30.0");
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		EnvironmentList.setItems(cbEnvironmentItemsList);
		APIVersionList.setItems(cbAPIVersionItemsList);
	}

    /************************************************************
    public static Map<String, String> getQueryParameters(String url) throws URISyntaxException {
        url = url.replace("#","?");
        Map<String, String> params = new HashMap<>();
        new URIBuilder(url).getQueryParams().stream().forEach(kvp -> params.put(kvp.getName(), kvp.getValue()));
        return params;
    }
     ************************************************************/ 
    @FXML
    private void singleOrgUserLogin() throws IOException, URISyntaxException {
    	log.info("EnvironmentList.getValue(): " + EnvironmentList.getSelectionModel().getSelectedItem());
    	log.info("APIVersionList.getValue(): " + APIVersionList.getSelectionModel().getSelectedItem());
	    Config.OAUTH_ENVIRONMENT_SINGLE = EnvironmentList.getSelectionModel().getSelectedItem();
	    Config.OAUTH_API_VERSION_SINGLE = APIVersionList.getSelectionModel().getSelectedItem();
	    //App.setRoot("SingleOrgHomePage");
	    SFDCContext.setVersion("SingleOrg");
    	OAuthToken oAuthToken = new OAuthToken();
    	OAuthTokenFlow oAuthTokenFlow = new OAuthTokenFlow(config);
        //System.out.println(oAuthTokenFlow.getStartUrl(Config.OAUTH_ENVIRONMENT_SINGLE,"SingleOrg"));
        Stage stage = new Stage();
        WebView webView = new WebView();   
        final WebEngine webEngine = webView.getEngine();
        webEngine.load(oAuthTokenFlow.getStartUrl(Config.OAUTH_ENVIRONMENT_SINGLE,"SingleOrg"));
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
            	cnt = cnt+1;
            	log.info("Count = "+cnt);
            	log.info("Old State = "+oldState);
            	log.info("New State = "+newState);
                if (newState == State.SUCCEEDED) {
                    stage.setTitle(webEngine.getTitle());
                    //System.out.println(webEngine.getUserAgent());
                    successURL = webEngine.getLocation();
                    //System.out.println("URL Details = "+webEngine.getLocation());
                    if(successURL.contains("access_token")){
            	        try {
            	        	String urlString2Decode = successURL;
            	        	String decodedURL = URLDecoder.decode(urlString2Decode, "UTF-8");
            	        	URL url = new URL(decodedURL);
            	        	URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            	        	String decodedURLAsString = uri.toASCIIString();
							OAuthTokenFlow.handleCompletedUrl(decodedURLAsString,"SingleOrg");
							//System.out.println("Access Token for Single Org Login:"+Config.OAUTH_ACCESSTOKEN_SINGLE);
							//System.out.println("Instance URL for Single Org Login:"+Config.OAUTH_INSTANCEURL_SINGLE);
							stage.close();
							OAuthTokenFlow.generateRequestSpecification("SingleOrg");
							setBasicOrgDetails();
							fillHealthCheckScorePieChart();
							fillApexOrgWideCoveragePieChart();
							setScopeItemList();
							App.setRoot("SingleOrgHomePage");
						}catch (IOException e) {
							log.error(e.getMessage());
						} catch (URISyntaxException e) {
							log.error(e.getMessage());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							log.error(e.getMessage());
						}
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
        
        /******************************************************************************************/
    }
    
    @FXML
    private void switchBackToHomePage() throws IOException {
        App.setRoot("AppHomePage");
    }
    
    @FXML
    private void switchBackToPreviousPage() throws IOException {
        App.setRoot("SFDCOrgImpactAnalysisPage");
    }
    
    @FXML
    private void signOut() throws IOException {
    	App.setRoot("AppLauncherPage");
        //Platform.exit();
        //System.exit(0);
    }

    private void fillHealthCheckScorePieChart() {
    	String healthCheckScoreQuery = "SELECT Score FROM SecurityHealthCheck";
	    Response healthCheckScoreResponse = SFDCContext.runToolingQuery(healthCheckScoreQuery);
	    log.info("healthCheckScoreResponse:"+healthCheckScoreResponse.asPrettyString());
	    try {
			JSONObject resultsJObject = new JSONObject(healthCheckScoreResponse.body().asPrettyString());
			JSONArray resultsJArray1 = resultsJObject.optJSONArray("records");
			JSONObject j = new JSONObject(resultsJArray1.get(0).toString());
			String strHealthCheckScore = j.getString("Score");
			log.info("Org Health Check Score :"+j.getString("Score")+" and Value :"+strHealthCheckScore);
			//Setting the score value into Config parameter
			Config.SFDC_SINGLE_SECURITY_HEALTHCHECK_SCORE = strHealthCheckScore;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}	
    }
    
    private void fillApexOrgWideCoveragePieChart() {
		String apexOrgWideCoverageScoreQuery = "SELECT PercentCovered FROM ApexOrgWideCoverage";
	    Response apexOrgWideCoverageResponse = SFDCContext.runToolingQuery(apexOrgWideCoverageScoreQuery);
	    System.out.println("apexOrgWideCoverageResponse:"+apexOrgWideCoverageResponse.asPrettyString());
	    try {
			JSONObject resultsJObject = new JSONObject(apexOrgWideCoverageResponse.body().asPrettyString());
			JSONArray resultsJArray1 = resultsJObject.optJSONArray("records");
			JSONObject j = new JSONObject(resultsJArray1.get(0).toString());
			String strApexOrgWideCoverage = j.getString("PercentCovered");
			log.info("Apex Org Wide Coverage Score :"+j.getString("PercentCovered")+" and String Value :"+strApexOrgWideCoverage);
			Config.SFDC_SINGLE_APEXORGWIDECODECOVERAGE_SCORE = strApexOrgWideCoverage;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}  
    }
    
    private void setBasicOrgDetails() { 
	    Response userInfoResponse = SFDCContext.getUserDetails();
		//System.out.println("User Detail Response ="+userInfoResponse.asPrettyString());
	    Config.SFDC_SINGLE_ORGID = userInfoResponse.jsonPath().getString("organization_id");
	    Config.SFDC_SINGLE_USERNAME = userInfoResponse.jsonPath().getString("preferred_username");
		
		Response organizationDetailsResponse = SFDCContext.getOrganizationInformation(SFDCContext.getOrgID());
		String organizationDetailsString = organizationDetailsResponse.body().asPrettyString();
		try {
			JSONObject resultsJObject = new JSONObject(organizationDetailsString);
			JSONArray resultsJArray1 = resultsJObject.optJSONArray("records");
			JSONObject j = new JSONObject(resultsJArray1.get(0).toString());
			log.info("organization_edition :"+j.getString("OrganizationType"));
			log.info("organization_name :"+j.getString("Name"));
			log.info("IsSandbox :"+j.getString("IsSandbox"));
			Config.SFDC_SINGLE_ORGTYPE = j.getString("OrganizationType");
			Config.SFDC_SINGLE_ORGNAME = j.getString("Name");
			Config.SFDC_SINGLE_ISSANDBOX = j.getString("IsSandbox");
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
		//System.out.println(jsonRecordsArray.length());
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
}

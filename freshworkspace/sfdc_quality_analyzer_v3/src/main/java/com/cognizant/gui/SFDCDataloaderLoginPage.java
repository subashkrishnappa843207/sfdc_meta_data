package com.cognizant.gui;

import java.io.FileWriter;
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
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class SFDCDataloaderLoginPage implements Initializable {
	private static final Logger log = Logger.getLogger(SFDCDataloaderLoginPage.class);
	public int cnt= 1;
	ExcelUtils excelUtil = new ExcelUtils();
	JsonUtils jsonUtil = new JsonUtils();
	XMLUtils xmlUtil = new XMLUtils();
    //static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
	private String SFDC_SOBJECT_JSON_FilePath = System.getProperty("user.dir")+"\\target\\classes\\com\\cognizant\\sfdc_quality_analyzer_v3\\json\\SFDC_TOOLING_SOBJECT_JSON_FILE_";
	private String SFDC_OBJECT_JSON_FilePath = System.getProperty("user.dir")+"\\target\\classes\\com\\cognizant\\sfdc_quality_analyzer_v3\\json\\SFDC_OBJECT_JSON_FILE_";
    static final String RESPONSESERVICE = "/services/oauth2/authorize?response_type=token";
    static final String REDIRECT_URI = "https://login.salesforce.com/services/oauth2/success";
    public static String successURL = null;
    public static Map<String, String> successURLParams = new HashMap<>();
	ArrayList<String> SFDCSObjectsScopeItems = new ArrayList<String>(); 
	ArrayList<String> SFDCObjectsScopeItems = new ArrayList<String>(); 
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

    @FXML
    private void sfdcDataloaderLogin() throws IOException, URISyntaxException {
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
							log.info("Instance URL for Single Org Login:"+Config.OAUTH_INSTANCEURL_SINGLE);
							stage.close();
							OAuthTokenFlow.generateRequestSpecification("SingleOrg");
							setBasicOrgDetails();
							SaveSFDCObjectMetadata();
							SaveToolingAPISObjectsMetadata();
							App.setRoot("SFDCDataLoaderHomePage");
						}catch (IOException e) {
							e.printStackTrace();
						} catch (URISyntaxException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
		log.info("Moving back to Home Page");
        App.setRoot("AppHomePage");
    }
    
    @FXML
    private void signOut() throws IOException {
    	App.setRoot("AppLauncherPage");
        //Platform.exit();
        //System.exit(0);
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
			log.info(e.getMessage());
		}
    }
    private void SaveToolingAPISObjectsMetadata() throws IOException, JSONException {
		Response toolingSOBjectResponse = SFDCContext.getToolingQuery();
	    JSONObject resultsSObject = new JSONObject(toolingSOBjectResponse.body().asPrettyString());
		JSONArray jsonRecordsArray = resultsSObject.optJSONArray("sobjects");
		log.info(jsonRecordsArray.length());
		//JSONArray jsonSObjectFieldsArray = new JSONArray();
		for(int i=0;i<jsonRecordsArray.length();i++) {
			JSONObject jsonRecords = new JSONObject(jsonRecordsArray.get(i).toString());
			SFDCSObjectsScopeItems.add(jsonRecords.get("name").toString());
		}
		//Separating in-scope items from whole Tooling Sobject items		
		Config.SFDC_SINGLE_SOBJECTS_ITEMS = SFDCSObjectsScopeItems;

    }
    
    private void SaveSFDCObjectMetadata() throws JSONException, IOException {
		Response SFDCObjectResponse = SFDCContext.getObjectQuery();
	    JSONObject resultsJObject = new JSONObject(SFDCObjectResponse.body().asPrettyString());
		JSONArray jsonRecordsArray = resultsJObject.optJSONArray("sobjects");
		log.info(jsonRecordsArray.length());
		//JSONArray jsonObjectFieldsArray = new JSONArray();
		for(int i=0;i<jsonRecordsArray.length();i++) {
			JSONObject jsonRecords = new JSONObject(jsonRecordsArray.get(i).toString());
			log.info("Name:"+jsonRecords.get("name"));
		    SFDCObjectsScopeItems.add(jsonRecords.get("name").toString());
		}
		Config.SFDC_SINGLE_OBJECTS_ITEMS = SFDCObjectsScopeItems;  
    }
    
    private void SaveToolingAPISObjectsMetadata_Later() throws IOException, JSONException {
		HashMap<String, HashMap<String, String>> ToolingSOBJECTFieldLevelDetails = new HashMap<String, HashMap<String, String>>();
    	
		Response toolingSOBjectResponse = SFDCContext.getToolingQuery();
	    JSONObject resultsSObject = new JSONObject(toolingSOBjectResponse.body().asPrettyString());
		JSONArray jsonRecordsArray = resultsSObject.optJSONArray("sobjects");
		log.info(jsonRecordsArray.length());
		for(int i=0;i<jsonRecordsArray.length();i++) {
			JSONObject jsonRecords = new JSONObject(jsonRecordsArray.get(i).toString());
			SFDCSObjectsScopeItems.add(jsonRecords.get("name").toString());
			Response responseSObjectDescribeInfo = SFDCContext.getDescribeToolingObject(jsonRecords.get("name").toString());
			JSONObject resultsJObjectDescribeInfo = new JSONObject(responseSObjectDescribeInfo.body().asPrettyString());
			JSONArray jsonSObjectFieldsArray = resultsJObjectDescribeInfo.optJSONArray("fields");
			HashMap<String, String> fieldDetails = new HashMap<String, String>();
			for(int j = 0; j<jsonSObjectFieldsArray.length();j++) {
				JSONObject IndividualSObjectFieldDetail = (JSONObject) jsonSObjectFieldsArray.get(j);
        		fieldDetails.put("name", (String) IndividualSObjectFieldDetail.get("name"));
        		fieldDetails.put("label", (String) IndividualSObjectFieldDetail.get("label"));
			}
			ToolingSOBJECTFieldLevelDetails.put(jsonRecords.get("name").toString(), fieldDetails);
		}
		//Separating in-scope items from whole Tooling Sobject items		
		Config.SFDC_SINGLE_SOBJECTS_ITEMS = SFDCSObjectsScopeItems;
		
		FileWriter myWriter = new FileWriter(SFDC_SOBJECT_JSON_FilePath+Config.SFDC_SINGLE_ORGID+".json");
		myWriter.write(ToolingSOBJECTFieldLevelDetails.toString());
		myWriter.close();
		
    }
    
    private void SaveSFDCObjectMetadata_Later() throws JSONException, IOException {
		HashMap<String, HashMap<String, String>> SFDCOBJECTFieldLevelDetails = new HashMap<String, HashMap<String, String>>();
		Response SFDCObjectResponse = SFDCContext.getObjectQuery();
	    JSONObject resultsJObject = new JSONObject(SFDCObjectResponse.body().asPrettyString());
	    System.out.println(resultsJObject);
		JSONArray jsonRecordsArray = resultsJObject.optJSONArray("sobjects");
		System.out.println(jsonRecordsArray.length());
		for(int i=0;i<jsonRecordsArray.length();i++) {
			JSONObject jsonRecords = new JSONObject(jsonRecordsArray.get(i).toString());
		    System.out.println("Name:"+jsonRecords.get("name"));
			//Response responseDescribeInfo = SFDCContext.getDescribeObject(jsonRecords.get("name").toString().trim());
		    SFDCObjectsScopeItems.add(jsonRecords.get("name").toString());
			Response responseObjectDescribeInfo = SFDCContext.getDescribeObject(jsonRecords.get("name").toString());
			JSONObject resultsObjectDescribeInfo = new JSONObject(responseObjectDescribeInfo.body().asPrettyString());
			JSONArray jsonObjectFieldsArray = resultsObjectDescribeInfo.optJSONArray("fields");
			HashMap<String, String> fieldDetails = new HashMap<String, String>();
			for(int j = 0; j<jsonObjectFieldsArray.length();j++) {
				JSONObject IndividualObjectFieldDetail = (JSONObject) jsonObjectFieldsArray.get(j);
        		fieldDetails.put("name", (String) IndividualObjectFieldDetail.get("name"));
        		fieldDetails.put("label", (String) IndividualObjectFieldDetail.get("label"));
			}
			SFDCOBJECTFieldLevelDetails.put(jsonRecords.get("name").toString(), fieldDetails);
		}
		Config.SFDC_SINGLE_OBJECTS_ITEMS = SFDCObjectsScopeItems;
		FileWriter myWriter = new FileWriter(SFDC_OBJECT_JSON_FilePath+Config.SFDC_SINGLE_ORGID+".json");
		myWriter.write(SFDCOBJECTFieldLevelDetails.toString());
		myWriter.close();
		
    }
}

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
import org.json.simple.parser.JSONParser;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SFDCandTestScriptMappingPage implements Initializable {
	private static final Logger log = Logger.getLogger(SFDCandTestScriptMappingPage.class);
	public int cnt= 1;
	ExcelUtils excelUtil = new ExcelUtils();
	JsonUtils jsonUtil = new JsonUtils();
	XMLUtils xmlUtil = new XMLUtils();
    //static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
    static final String RESPONSESERVICE = "/services/oauth2/authorize?response_type=token";
    static final String REDIRECT_URI = "https://login.salesforce.com/services/oauth2/success";
    public static String successURL = null;
    public static Map<String, String> successURLParams = new HashMap<>();
	ArrayList<String> MappingInScopeItems = new ArrayList<String>(); 
    Config config;
    private String Mapping_TestCase_SourceDirectory = System.getProperty("user.dir")+"\\Mapping\\JSON\\Sources";
    @FXML Label SuccessfulLoginLabel;
    @FXML TextField SuccessfulLoginUserName;
    @FXML TextField SuccessfulLoginOrgName;
    @FXML TextField SuccessfulLoginOrgInstanceURL;
    @FXML TextField SuccessfulLoginOrgSandbox;

    @FXML ComboBox<String> SalesforceTestCaseMappingEnvironmentList;
    @FXML ComboBox<String> SalesforceTestCaseMappingAPIVersionList;
    @FXML Button ImportExistingMappingFIleButton;
    @FXML TextField ExistingTestCasesMappingFilePath;
    @FXML RadioButton NewMappingDocumentRadioButton;
    @FXML RadioButton ExistingMappingDocumentRadioButton;
    private ToggleGroup toggleGroup = new ToggleGroup();
    private ObservableList<String> cbEnvironmentItemsList = FXCollections.observableArrayList("Production", "Sandbox");
    private ObservableList<String> cbAPIVersionItemsList = FXCollections.observableArrayList("52.0", "51.0", "50.0", "49.0", "48.0", "47.0", "46.0", "45.0" , "44.0",
    		"43.0", "42.0", "41.0", "40.0", "39.0", "38.0", "37.0", "36.0", "35.0", "34.0", "33.0", "32.0", "31.0", "30.0");
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		SalesforceTestCaseMappingEnvironmentList.setItems(cbEnvironmentItemsList);
		SalesforceTestCaseMappingAPIVersionList.setItems(cbAPIVersionItemsList);
		NewMappingDocumentRadioButton.setToggleGroup(toggleGroup);
		ExistingMappingDocumentRadioButton.setToggleGroup(toggleGroup);
		if(Config.loginSuccessful) {
		    SuccessfulLoginLabel.setText("Login Successful");
		    SuccessfulLoginUserName.setText(Config.SFDC_SINGLE_USERNAME);
		    SuccessfulLoginOrgName.setText(Config.SFDC_SINGLE_ORGNAME);
		    SuccessfulLoginOrgInstanceURL.setText(Config.OAUTH_INSTANCEURL_SINGLE);
		    SuccessfulLoginOrgSandbox.setText(Config.SFDC_SINGLE_ISSANDBOX);
		}
	}
	
	@FXML private void switchToMappingSummaryPage() throws IOException {
        System.out.println("Perfom Automation test script mapping with Salesforce org's component");
        //System.out.println("ExistingTestCasesMappingFilePath.getText().trim() : "+ExistingTestCasesMappingFilePath.getText().trim());
        RadioButton selectedRadioButton = (RadioButton)toggleGroup.getSelectedToggle(); 
        if (selectedRadioButton != null) { 
            String selectedRadioButtonText = selectedRadioButton.getText(); 
            String selectedRadioButtonId = selectedRadioButton.getId();
            log.info("Selected Radio Button:"+selectedRadioButtonText+" And the ID is:"+selectedRadioButtonId);
            if(selectedRadioButtonId.equalsIgnoreCase("NewMappingDocumentRadioButton")) {
            	 File folder = new File(Mapping_TestCase_SourceDirectory);
			     File[] listOfFiles = folder.listFiles();
				      for (int i = 0; i < listOfFiles.length; i++) {
				    	  if (listOfFiles[i].isFile()) {
				    		  log.info("File Name : " + listOfFiles[i].getName());
					          //String fileAbsolutePath = Mapping_TestCase_SourceDirectory+"\\"+listOfFiles[i].getName();
					          String fileAbsolutePath = listOfFiles[i].getAbsolutePath();
					          log.info("File Absolute Path : " + fileAbsolutePath);
					          File deleteFile = new File(fileAbsolutePath);
					          if(deleteFile.delete()){
					        	  log.info(fileAbsolutePath+" File deleted successfully");
					          } else {
					        	  log.info("Failed to delete the file : "+fileAbsolutePath);
					          }
				    	  }
				      }
				//Delete Existing Source JSON Files
            	App.setRoot("SFDCandTestScriptNewMappingDocumentPage");
            }else if(selectedRadioButtonId.equalsIgnoreCase("ExistingMappingDocumentRadioButton")) {
                if((ExistingTestCasesMappingFilePath.getText().trim() == null) || (ExistingTestCasesMappingFilePath.getText().trim() == "") 
                		|| (ExistingTestCasesMappingFilePath.getText().trim().isEmpty())) {
                	Alert errorAlert = new Alert(AlertType.ERROR);
            		errorAlert.setHeaderText("Mapping File Not Selected");
            		errorAlert.setContentText("Please Select the Existing Mapping File before you proceed!!");
            		errorAlert.showAndWait();
                }else {
                	log.info("Moving to Update Existing Mapping File Page");
                	Config.SFDC_EXISTING_MAPPING_FILE_PATH = ExistingTestCasesMappingFilePath.getText();
                	App.setRoot("SFDCandTestScriptExistingMappingDocumentPage");
                }
            }else {
            	log.info("Please select your choice first");
            	Alert errorAlert = new Alert(AlertType.ERROR);
        		errorAlert.setHeaderText("Mapping Option Radio Button not Selected");
        		errorAlert.setContentText("Before you proceed, Please Select the Mapping Option Radio Button !!");
        		errorAlert.showAndWait();
            }
        }
    }

	@FXML private void switchBackToPreviousPage() throws IOException {
		log.info("Moving to Selenium Automation Home Page");
        App.setRoot("SeleniumAutomationHomePage");
    }
    
	@FXML private void switchBackToHomePage() throws IOException {
        App.setRoot("AppHomePage");
    }
    
	@FXML private void SalesforceSignInForTestCasesMapping() throws IOException, URISyntaxException {
		log.info("Implement code to login to salesforce before mapping !!!");
		log.info("EnvironmentList.getValue(): " + SalesforceTestCaseMappingEnvironmentList.getSelectionModel().getSelectedItem());
		log.info("APIVersionList.getValue(): " + SalesforceTestCaseMappingAPIVersionList.getSelectionModel().getSelectedItem());
	    Config.OAUTH_ENVIRONMENT_SINGLE = SalesforceTestCaseMappingEnvironmentList.getSelectionModel().getSelectedItem();
	    Config.OAUTH_API_VERSION_SINGLE = SalesforceTestCaseMappingAPIVersionList.getSelectionModel().getSelectedItem();
	    //App.setRoot("SingleOrgHomePage");
	    SFDCContext.setVersion("SingleOrg");
    	//OAuthToken oAuthToken = new OAuthToken();
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
						    setScopeItemList();
						    retrieveSalesforceItemList();
						    SuccessfulLoginLabel.setText("Login Successful");
						    SuccessfulLoginUserName.setText(Config.SFDC_SINGLE_USERNAME);
						    SuccessfulLoginOrgName.setText(Config.SFDC_SINGLE_ORGNAME);
						    SuccessfulLoginOrgInstanceURL.setText(Config.OAUTH_INSTANCEURL_SINGLE);
						    SuccessfulLoginOrgSandbox.setText(Config.SFDC_SINGLE_ISSANDBOX);
						    Config.loginSuccessful = true;
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
	
	@FXML private void ImportExistingMappingFIle() throws IOException {
		log.info("Implement code to Choose existing mapping file for further update!!!");
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Existing Mapping file");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")+"\\Mapping\\JSON\\Target"));
		File selectedFile = fileChooser.showOpenDialog(null);
		try {
			if(selectedFile.getAbsolutePath() != null) {
				ExistingTestCasesMappingFilePath.setText(selectedFile.getAbsolutePath());
			} else  {
				log.info("You haven't selected any mapping file yet!!!");
		    }
		}catch(Exception ex) {
			log.info("You haven't selected any mapping file yet!!!");
		}
    }
	
    @FXML private void signOut() throws IOException {
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
			e.printStackTrace();
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
		//System.out.println(toolingSObjectList.toString());
		//Separating in-scope items from whole Tooling Sobject items
		
		for(String item : toolingSObjectList) {
			if((excelUtil.getCellData("SOBJECT",item)) && (excelUtil.getCellData("SOBJECT", "IncludedInMappingSheet", item).contentEquals("Yes"))) {
				MappingInScopeItems.add(item);
				//(excelUtil.getCellData("SOBJECT",item)) && 
			}
		}
		Config.SFDC_MAPPING_INSCOPE_ITEMS = MappingInScopeItems;
		log.info(Config.SFDC_MAPPING_INSCOPE_ITEMS);
    }
    
    private void retrieveSalesforceItemList() {
    	int totalCount = 0;
    	for(int i = 0 ; i < Config.SFDC_MAPPING_INSCOPE_ITEMS.size() ; i++) {
  			String splittedSelectedSObject = Config.SFDC_MAPPING_INSCOPE_ITEMS.get(i);
  			log.info("SObject Name : "+splittedSelectedSObject);
  	    	try {
	  			String SObjectFieldName = excelUtil.getCellData("SOBJECT", "FieldName",splittedSelectedSObject);
	  			log.info("SELECT "+SObjectFieldName+", LastModifiedDate FROM "+splittedSelectedSObject);
  			
	        	Response responseGetQueryDetails = SFDCContext.runToolingQuery("SELECT "+SObjectFieldName+" FROM "+splittedSelectedSObject);
	    		//System.out.println(responseGetQueryDetails.body().asPrettyString());
	    	    JSONObject resultsRecordInfo = new JSONObject(responseGetQueryDetails.body().asPrettyString());
	    		JSONArray jsonResultsRecordsArray = resultsRecordInfo.optJSONArray("records");
	    		ArrayList<String> foundComponentList = new ArrayList<String>();
	    		for(int j=0;j<jsonResultsRecordsArray.length();j++) {
        			JSONObject jsonFinalRecords = new JSONObject(jsonResultsRecordsArray.get(j).toString());
        			foundComponentList.add(jsonFinalRecords.getString(SObjectFieldName));
	    			totalCount = totalCount + 1;
	    		}

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
	        			JSONObject jsonFinalRecords = new JSONObject(jsonResultsRecordsArray.get(j).toString());
	        			foundComponentList.add(jsonFinalRecords.getString(SObjectFieldName));
		    			totalCount = totalCount + 1;
		    		}
	    		}
	    		addElementToObservableArrayList(splittedSelectedSObject, foundComponentList);
	    		log.info("************************************************************");
	    		log.info("Total Count On First page: "+TotalResultSize);
	    		log.info("Final Count : "+totalCount);
	    		log.info("splittedSelectedSObject Name : "+splittedSelectedSObject+" AND foundComponentList Size : "+foundComponentList.size());
  			}catch(RuntimeException | IOException | JSONException runExcep){
  				log.error(runExcep.getMessage());
  				throw new RuntimeException("Error occured while retrieving records for " + splittedSelectedSObject +" component", runExcep);
  			}
  		}
    }
    
    private void addElementToObservableArrayList(String sObjectName, ArrayList<String> ElementLists) throws RuntimeException,IOException,JSONException{
    	System.out.println("sObjectName : "+sObjectName+" -- ElementNames : "+ElementLists);
    	switch(sObjectName) {
    	  case "ApexClass":
    	    // code block
    		Config.SFDC_ApexClassLV_ItemsList = ElementLists;
    	    break;
    	  case "ApexComponent":
    	    // code block
    		Config.SFDC_ApexComponentLV_ItemsList = ElementLists;
    	    break;
    	  case "ApexPage":
      	    // code block
      		Config.SFDC_ApexPageLV_ItemsList = ElementLists;
      	    break;
      	  case "ApexTrigger":
      	    // code block
      		Config.SFDC_ApexTriggerLV_ItemsList = ElementLists;
      	    break;
    	  case "AssignmentRule":
      	    // code block
      		Config.SFDC_AssignmentRuleLV_ItemsList = ElementLists;
      	    break;
      	  case "AuraDefinition":
      	    // code block
      		Config.SFDC_AuraDefinitionLV_ItemsList = ElementLists;
      	    break;
    	  case "AutoResponseRule":
      	    // code block
      		Config.SFDC_AutoResponseRuleLV_ItemsList = ElementLists;
      	    break;
      	  case "BusinessProcess":
      	    // code block
      		Config.SFDC_BusinessProcessLV_ItemsList = ElementLists;
      	    break;
      	  case "CompactLayout":
    	    // code block
    		Config.SFDC_CompactLayoutLV_ItemsList = ElementLists;
    	    break;
    	  case "CustomApplication":
    	    // code block
    		Config.SFDC_CustomApplicationLV_ItemsList = ElementLists;
    	    break;
      	  case "CustomField":
    	    // code block
    		Config.SFDC_CustomFieldLV_ItemsList = ElementLists;
    	    break;
    	  case "CustomObject":
    	    // code block
    		Config.SFDC_CustomObjectLV_ItemsList = ElementLists;
    	    break; 
    	  case "CustomTab":
      	    // code block
      		Config.SFDC_CustomTabLV_ItemsList = ElementLists;
      	    break;
      	  case "FieldMapping":
      	    // code block
      		Config.SFDC_FieldMappingLV_ItemsList = ElementLists;
      	    break;
      	  case "FieldSet":
    	    // code block
    		Config.SFDC_FieldSetLV_ItemsList = ElementLists;
    	    break;
    	  case "FlexiPage":
    	    // code block
    		Config.SFDC_FlexiPageLV_ItemsList = ElementLists;
    	    break;
      	  case "Flow":
    	    // code block
    		Config.SFDC_FlowLV_ItemsList = ElementLists;
    	    break;
    	  case "FlowDefinition":
    	    // code block
    		Config.SFDC_FlowDefinitionLV_ItemsList = ElementLists;
    	    break;
    	  case "HomePageComponent":
      	    // code block
      		Config.SFDC_HomePageComponentLV_ItemsList = ElementLists;
      	    break;
      	  case "HomePageLayout":
      	    // code block
      		Config.SFDC_HomePageLayoutLV_ItemsList = ElementLists;
      	    break;
      	  case "Layout":
    	    // code block
    		Config.SFDC_LayoutLV_ItemsList = ElementLists;
    	    break;
    	  case "PathAssistant":
    	    // code block
    		Config.SFDC_PathAssistantLV_ItemsList = ElementLists;
    	    break;
      	  case "PermissionSet":
    	    // code block
    		Config.SFDC_PermissionSetLV_ItemsList = ElementLists;
    	    break;
    	  case "PermissionSetGroup":
    	    // code block
    		Config.SFDC_PermissionSetGroupLV_ItemsList = ElementLists;
    	    break;
    	  case "Profile":
      	    // code block
      		Config.SFDC_ProfileLV_ItemsList = ElementLists;
      	    break;
      	  case "QuickActionDefinition":
      	    // code block
      		Config.SFDC_QuickActionDefinitionLV_ItemsList = ElementLists;
      	    break;
      	  case "RecordType":
    	    // code block
    		Config.SFDC_RecordTypeLV_ItemsList = ElementLists;
    	    break;
    	  case "UserRole":
    	    // code block
    		Config.SFDC_UserRoleLV_ItemsList = ElementLists;
    	    break;
      	  case "ValidationRule":
    	    // code block
    		Config.SFDC_ValidationRuleLV_ItemsList = ElementLists;
    	    break;
    	  case "WebLink":
    	    // code block
    		Config.SFDC_WebLinkLV_ItemsList = ElementLists;
    	    break;
    	  default:
    	    // code block
    		break;
    	}
    }
}

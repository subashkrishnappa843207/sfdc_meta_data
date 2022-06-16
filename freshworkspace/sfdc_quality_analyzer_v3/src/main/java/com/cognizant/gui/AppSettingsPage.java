package com.cognizant.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.cognizant.sfdc_quality_analyzer_v3.App;
import com.cognizant.utility.Config;
import com.cognizant.utility.ExcelUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class AppSettingsPage implements Initializable {
	ExcelUtils excelUtil = new ExcelUtils();
    private static final Logger log = Logger.getLogger(AppSettingsPage.class); 
    
	Config config = new Config();
	@FXML TextField SFDCLoginURLTextbox;
	@FXML TextField SFDCClientIDSingleOrg;
	@FXML TextField SFDCClientIDMultiOrg;
	@FXML TextField SFDCProdCallbackURL;
	@FXML TextField SFDCSandboxCallbackURL;
	@FXML CheckBox IsUsingCustomLoginURL;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		if(!Config.OAUTH_SINGLE_CLIENTID.equals("sfdc.oauth.clientid")) {
			SFDCClientIDSingleOrg.setText(Config.OAUTH_SINGLE_CLIENTID);
			//log.info(Config.OAUTH_SINGLE_CLIENTID);
		}else {
			log.info(Config.OAUTH_SINGLE_CLIENTID);
		}
		if(!Config.OAUTH_MULTI_CLIENTID.equals("sfdc.oauth.clientid")) {
			SFDCClientIDMultiOrg.setText(Config.OAUTH_MULTI_CLIENTID);
			//log.info(Config.OAUTH_MULTI_CLIENTID);
		}else {
			log.info(Config.OAUTH_MULTI_CLIENTID);
		}
	}
    
    @FXML
    private void GoBackToLoginPage() throws IOException {
        App.setRoot("AppLauncherPage");
        log.info("Going back to the Login Page");
    }
    
    @FXML
    private void SaveSettingsDetails() throws IOException {
    	System.out.println("Implement the code for saving the Settings Details");

    	if((IsUsingCustomLoginURL.isSelected() && SFDCLoginURLTextbox.getText().trim().isEmpty())
    			|| (IsUsingCustomLoginURL.isSelected() && SFDCLoginURLTextbox.getText().trim().equals(""))) {
    		Alert errorAlert = new Alert(AlertType.ERROR);
    		errorAlert.setHeaderText("Input not valid");
    		errorAlert.setContentText("Please fill in Custom Login URL as you have checked above checkbox!!");
    		errorAlert.showAndWait();
    		log.error("Please fill in Custom Login URL as you have checked above checkbox!!");
    	} else {
        	Config.OAUTH_CUSTOM_LOGIN_URL = SFDCLoginURLTextbox.getText().trim();
        	//log.info("Custom URL Field has been set successfully");
    	}
    	
    	if(SFDCClientIDSingleOrg.getText().trim().equals("") || SFDCClientIDSingleOrg.getText().trim().isEmpty()) {
    		Alert errorAlert = new Alert(AlertType.ERROR);
    		errorAlert.setHeaderText("Primary Client ID Not populated");
    		errorAlert.setContentText("It is mandatory to fill out Primary Client ID everytime you save the setting details!! "
    				+ "In case you dont have a Client ID , Please create a Connected App as per our help document and retrieve "
    				+ "the Client ID to Explore the other features of this tool!! ");
    		errorAlert.showAndWait();
    	} else {        	
        	Config.OAUTH_SINGLE_CLIENTID = SFDCClientIDSingleOrg.getText().trim(); 
        	log.info("Primary Client Id has been set successfully");
    	}
    	
    	if(SFDCClientIDMultiOrg.getText().trim().equals("") || SFDCClientIDMultiOrg.getText().trim().isEmpty()) {
    		Alert errorAlert = new Alert(AlertType.WARNING);
    		errorAlert.setHeaderText("Secondary Client ID Not populated");
    		errorAlert.setContentText("Please note that you haven't filled in Secondary Client ID field with your last save. "
    				+ "However, this field is not mandatory to run Single Org Impact Analysis. But Don't forget to fill out this field "
    				+ "in case you would like to perform Multi Org Impact Analysis in future!! "
    				+ "And Please refer our Help document to know more about How to create Connected App and retrieve Client Id for this tool!! ");
    		errorAlert.showAndWait();
    	} else {        	
        	Config.OAUTH_MULTI_CLIENTID = SFDCClientIDMultiOrg.getText().trim(); 
        	log.info("Secondary Client Id has been set successfully");
    	}
    }
    
    @FXML
    private void ResetButton() throws IOException {
        System.out.println("Implement code to reset the Settings Page details");
        SFDCLoginURLTextbox.setText("");
        SFDCClientIDSingleOrg.setText("");
        SFDCClientIDMultiOrg.setText("");
        IsUsingCustomLoginURL.setSelected(false);
        SFDCProdCallbackURL.setText(Config.OAUTH_PROD_REDIRECTURI_VAL);
        SFDCSandboxCallbackURL.setText(Config.OAUTH_SB_REDIRECTURI_VAL);
    }
    
    
}
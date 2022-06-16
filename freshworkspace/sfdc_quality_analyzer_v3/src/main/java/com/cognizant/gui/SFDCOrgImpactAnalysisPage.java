package com.cognizant.gui;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.cognizant.sfdc_quality_analyzer_v3.App;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class SFDCOrgImpactAnalysisPage {
	private static final Logger log = Logger.getLogger(SFDCOrgImpactAnalysisPage.class);
	
    @FXML
    private void switchToSingleOrgLoginPage() throws IOException {
    	log.info("Moving to Single Org Impact Analysis Login Page");
        App.setRoot("SingleOrgLoginPage");
    }
    
    @FXML
    private void switchToMultiOrgLoginPage() throws IOException {
    	log.info("Moving to Multi Org Impact Analysis Login Page");
        App.setRoot("MultiOrgLoginPage");
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
    
}
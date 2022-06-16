package com.cognizant.gui;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.cognizant.sfdc_quality_analyzer_v3.App;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class SFDCTestImpactAnalysisPage {
	private static final Logger log = Logger.getLogger(SFDCTestImpactAnalysisPage.class);
	
    @FXML
    private void FindImpactedTestScriptsVsSFDC() throws IOException {
    	log.info("Moving to Find Impacted Test scripts vs SFDC component page");
        App.setRoot("FindImpactedTestScriptVsSFDCComponents");
    }
    
    @FXML
    private void FindImpactedJIRAStoriesVsTestScripts() throws IOException {
    	log.info("Moving to Find impacted JIRA stories vs Test Scripts");
        //App.setRoot("MultiOrgLoginPage");
    }
    
    @FXML
    private void FindImpactedJIRAStoriesVsSFDC() throws IOException {
    	log.info("Moving to Find Impacted JIRA Stories vs SFDC Components");
        //App.setRoot("MultiOrgLoginPage");
    }
    
    @FXML
    private void switchBackToPreviousPage() throws IOException {
    	log.info("Moving back to previous page");
        App.setRoot("SeleniumAutomationHomePage");
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
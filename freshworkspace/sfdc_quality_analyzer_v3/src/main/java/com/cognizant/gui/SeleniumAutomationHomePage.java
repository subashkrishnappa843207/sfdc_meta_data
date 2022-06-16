package com.cognizant.gui;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.cognizant.sfdc_quality_analyzer_v3.App;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class SeleniumAutomationHomePage {
	private static final Logger log = Logger.getLogger(SeleniumAutomationHomePage.class);
	
    @FXML
    private void launchCITStool() throws IOException {
    	log.info("Moving to Launch CITS Home Page");
        App.setRoot("LaunchCITSHomePage");
    }
    
    @FXML
    private void switchToMapTestScriptWithJIRAStories() throws IOException {
    	log.info("Moving to JIRA and Test Script Mapping Page");
        App.setRoot("JIRAandTestScriptMappingPage");
    }
    
    @FXML
    private void switchToSeleniumTestExecutionPage() throws IOException {
    	log.info("Moving to Selenium Test Execution Page");
        App.setRoot("SeleniumTestExecutionPage");
    }
    
    @FXML
    private void switchToMapTestScriptWithSalesforceComponents() throws IOException {
    	log.info("Moving to SFDC and Test Script Mapping Page");
        App.setRoot("SFDCandTestScriptMappingPage");
    }
    
    @FXML
    private void switchToMapJIRAStoriesWithSalesforceComponents() throws IOException {
    	log.info("Moving to SFDC and JIRA Stories Mapping Page");
        App.setRoot("SFDCandJIRAStoriesMappingPage");
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
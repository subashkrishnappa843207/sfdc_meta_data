package com.cognizant.gui;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.cognizant.sfdc_quality_analyzer_v3.App;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class JIRAandTestScriptMappingPage {
	private static final Logger log = Logger.getLogger(JIRAandTestScriptMappingPage.class); 
	
	@FXML
    private void mapAutomationScriptsWithSfdcComponents() throws IOException {
        log.info("Perfom Automation test script mapping with Salesforce org's component");
    }

	@FXML
    private void switchBackToPreviousPage() throws IOException {
		log.info("Moving Back to Previous Page");
        App.setRoot("SeleniumAutomationHomePage");
    }
    
	@FXML
    private void switchBackToHomePage() throws IOException {
		log.info("Moving Back to Home Page");
        App.setRoot("AppHomePage");
    }
    
    @FXML
    private void signOut() throws IOException {
    	App.setRoot("AppLauncherPage");
        //Platform.exit();
        //System.exit(0);
    }
}

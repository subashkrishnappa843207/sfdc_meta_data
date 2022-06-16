package com.cognizant.gui;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.cognizant.sfdc_quality_analyzer_v3.App;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class SFDCandJIRAStoriesMappingPage {
	private static final Logger log = Logger.getLogger(SFDCandJIRAStoriesMappingPage.class);
	
	@FXML
    private void switchToSeleniumVersionControlPage() throws IOException {
		log.info("Moving to Selenium Version Controlling Page");
        App.setRoot("SeleniumAutomationHomePage");
    }
	
	@FXML
    private void switchBackToPreviousPage() throws IOException {
		log.info("Moving back to Previous Page");
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

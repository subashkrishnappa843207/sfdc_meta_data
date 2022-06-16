package com.cognizant.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.cognizant.sfdc_quality_analyzer_v3.App;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class AppHomePage implements Initializable {
    private static final Logger log = Logger.getLogger(AppHomePage.class); 
    @FXML Label HomePageWelcomeHeader;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		HomePageWelcomeHeader.setTextFill(Color.WHITE);
		HomePageWelcomeHeader.setStyle("-fx-font: 24 arial;-fx-font-weight: bold;"); 
	}
	
    @FXML
    private void switchToSfdcOrgAnalysisHomePage() throws IOException {
        App.setRoot("SFDCOrgImpactAnalysisPage");
        log.info("Switching to SFDC Org Impact Analysis Page");
    }
    
    @FXML
    private void switchToQualityCoverageHomePage() throws IOException {
    	App.setRoot("SFDCTestImpactAnalysisPage");
    	log.info("Switching to SFDC Test Impact Analysis Page");
    }
    
    @FXML
    private void switchToSFDCAutoTestingHomePage() throws IOException {
        App.setRoot("SeleniumAutomationHomePage");
        log.info("Switching to Selenium Automation Home Page");
    }
    
    @FXML
    private void switchToSFDCDataloaderPage() throws IOException {
        App.setRoot("SFDCDataLoaderLoginPage");
        log.info("Switching to SFDC Dataloader Login Page");
    }
    
    @FXML
    private void signOut() throws IOException {
    	App.setRoot("AppLauncherPage");
    	log.info("Switching to App Launcher Page");
        //Platform.exit();
        //System.exit(0);
    }
    
}
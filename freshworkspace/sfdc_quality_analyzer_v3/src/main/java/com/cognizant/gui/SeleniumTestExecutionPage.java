package com.cognizant.gui;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.cognizant.sfdc_quality_analyzer_v3.App;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SeleniumTestExecutionPage {
	private static final Logger log = Logger.getLogger(SeleniumTestExecutionPage.class);
	
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
    private void BrowserStackButton() throws IOException {
		log.info("Implement code to Integrate with BrowserStack");
    }
	
	@FXML
    private void SaucelabsButton() throws IOException {
		log.info("Implement code to Integrate with Saucelabs");
    }
	
	@FXML
    private void CircleCIButton() throws IOException {
		log.info("Implement code to Integrate with CircleCI");
    }
	
	@FXML
    private void LocalTestExecutionButton() throws IOException {
		log.info("Implement code to Perform Test Execution on Local Machine");
    }
	
	@FXML
    private void DockerButton() throws IOException {
		log.info("Implement code to Integrate with Docker and Zalenium");
    }
	
	@FXML
    private void AWSButton() throws IOException {
		log.info("Implement code to Integrate with Amazon WebServices");
    }
	
	@FXML
    private void AzureButton() throws IOException {
		log.info("Implement code to Integrate with Azure Devops");
    }
	
	@FXML
    private void PerfectoButton() throws IOException {
		log.info("Implement code to Integrate with Perfecto");
    }
	
	@FXML
    private void CumulusCIButton() throws IOException {
		log.info("Implement code to Integrate with CumulusCI");
    }
    
    @FXML
    private void signOut() throws IOException {
    	App.setRoot("AppLauncherPage");
        //Platform.exit();
        //System.exit(0);
    }
}

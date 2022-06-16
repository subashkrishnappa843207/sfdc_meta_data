package com.cognizant.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.cognizant.sfdc_quality_analyzer_v3.App;
import com.cognizant.utility.Config;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class AppLauncherPage implements Initializable {
	private static final Logger log = Logger.getLogger(AppLauncherPage.class); 
	@FXML Label ToolLaunchPageLabel;
	@FXML Label FooterLabel;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ToolLaunchPageLabel.setTextFill(Color.WHITE);
		ToolLaunchPageLabel.setStyle("-fx-font: 36 arial;-fx-font-weight: bold;"); 
		FooterLabel.setTextFill(Color.WHITE);
		FooterLabel.setStyle("-fx-font: 14 arial;-fx-font-weight: bold;"); 
	}
	
    @FXML
    private void switchToAppHomePage() throws IOException {
        App.setRoot("AppHomePage");
        log.info("Switching to App Home Page");
    }
    
    @FXML
    private void switchToSettingsPage() throws IOException {
        App.setRoot("AppSettingsPage");
        log.info("Switching to App Settings Page");

    }
    
    @FXML
    private void exit() throws IOException {
        Platform.exit();
        System.exit(0);
    }
}

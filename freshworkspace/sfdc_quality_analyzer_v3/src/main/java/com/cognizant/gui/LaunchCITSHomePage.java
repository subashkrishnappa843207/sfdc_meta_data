package com.cognizant.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.cognizant.sfdc_quality_analyzer_v3.App;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class LaunchCITSHomePage implements Initializable {
    @FXML RadioButton FirstRadioButton;
    @FXML RadioButton SecondRadioButton;
    @FXML RadioButton ThirdRadioButton;
    @FXML TextField ProjectPathTextField;
    ToggleGroup toggleGroup = new ToggleGroup();
    private static final Logger log = Logger.getLogger(LaunchCITSHomePage.class); 
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	    FirstRadioButton.setToggleGroup(toggleGroup);
	    SecondRadioButton.setToggleGroup(toggleGroup);
	    ThirdRadioButton.setToggleGroup(toggleGroup);
	}
    
    @FXML private void switchBackToPreviousPage() throws IOException {
    	log.info("Moving back to Previous Page");
        App.setRoot("SeleniumAutomationHomePage");
    }
    
	@FXML private void switchBackToHomePage() throws IOException {
		log.info("Moving Back to Home Page");
        App.setRoot("AppHomePage");
    }
    
	@FXML private void launchCITSTool() throws IOException, URISyntaxException {
		log.info("launching CITS Automation Tool");
        log.info("Base Directory : "+System.getProperty("user.dir"));
		String versionJava = Runtime.class.getPackage().getSpecificationVersion();
		//System.out.println(version);
		try{ 
			if(FirstRadioButton.isSelected()) {
				log.info("Java version is : "+versionJava);
				Desktop.getDesktop().browse(new URI("https://github.com/CognizantQAHub/Cognizant-Intelligent-Test-Scripter/tree/1.1"));
				/*
				Process p = Runtime.getRuntime().exec("cmd /c start "+System.getProperty("user.dir")+"\\AutomationProjects\\cognizant-intelligent-test-scripter-1.2\\Run.bat");
			    p.waitFor();
			    ProjectPathTextField.setText(System.getProperty("user.dir")+"\\AutomationProjects\\cognizant-intelligent-test-scripter-1.2\\Projects");
			    */
			}else if(SecondRadioButton.isSelected()){
				System.out.println("Java version is : "+versionJava);
				Desktop.getDesktop().browse(new URI("https://github.com/CognizantQAHub/Cognizant-Intelligent-Test-Scripter/tree/1.2"));
				/*
				 Process p = Runtime.getRuntime().exec("cmd /c start "+System.getProperty("user.dir")+"\\AutomationProjects\\cognizant-intelligent-test-scripter-1.3\\Run.bat");
				 p.waitFor();
			     ProjectPathTextField.setText(System.getProperty("user.dir")+"\\AutomationProjects\\cognizant-intelligent-test-scripter-1.3\\Projects");
				 */
			}else if(ThirdRadioButton.isSelected()){
				log.info("Java version is : "+versionJava);
				Desktop.getDesktop().browse(new URI("https://github.com/CognizantQAHub/Cognizant-Intelligent-Test-Scripter/tree/1.4"));
				/*
				Process p = Runtime.getRuntime().exec("cmd /c start "+System.getProperty("user.dir")+"\\AutomationProjects\\cognizant-intelligent-test-scripter-1.4\\Run.bat");
			    p.waitFor();
			    ProjectPathTextField.setText(System.getProperty("user.dir")+"\\AutomationProjects\\cognizant-intelligent-test-scripter-1.4\\Projects");
			    */
			}
		}catch( IOException ex ){
		    //Validate the case when file can't be accessed (not enough permissions)
			log.error(ex.getMessage());
		}
    }
	
	@FXML private void JavaVersionCheck() throws IOException {
		log.info("Java Version Check");
		String versionJava = Runtime.class.getPackage().getSpecificationVersion();
		//System.out.println(version);
		if(versionJava.contains("1.8")) {
			log.info("Java version is : "+versionJava);
			FirstRadioButton.setSelected(true);
		}else if(Integer.parseInt(versionJava) == 9 || Integer.parseInt(versionJava) == 10){
			log.info("Java version is : "+versionJava);
			SecondRadioButton.setSelected(true);
		}else if(Integer.parseInt(versionJava) >= 11){
			log.info("Java version is : "+versionJava);
			ThirdRadioButton.setSelected(true);
		}
    }
	
	@FXML private void OpenCITSProjectPath() {
		log.info("Go to Automation Project Path");
		try {
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + ProjectPathTextField.getText());
		}catch( IOException ex ){
		    //Validate the case when file can't be accessed (not enough permissions)
			log.error(ex.getMessage());
		}
    }
	
	@FXML private void clickHelpButton() {
		try {
	        Desktop.getDesktop().browse(new URI("https://cognizantqahub.github.io/Cognizant-Intelligent-Test-Scripter-Helpdoc/index.html"));
	    } catch (IOException e1) {
	    	log.error(e1.getMessage());
	    } catch (URISyntaxException e1) {
	    	log.error(e1.getMessage());
	    }
	}
	
	@FXML private void clickOpenGitRepoButton() {
		try {
	        Desktop.getDesktop().browse(new URI("https://github.com/CognizantQAHub/Cognizant-Intelligent-Test-Scripter/"));
	    } catch (IOException e1) {
	    	log.error(e1.getMessage());
	    } catch (URISyntaxException e1) {
	    	log.error(e1.getMessage());
	    }
	}
	
	@FXML private void clickOpenSeleniumOfficialSiteButton() {
		try {
	        Desktop.getDesktop().browse(new URI("https://www.selenium.dev/"));
	    } catch (IOException e1) {
	    	log.error(e1.getMessage());
	    } catch (URISyntaxException e1) {
	    	log.error(e1.getMessage());
	    }
	}

    @FXML private void signOut() throws IOException {
    	App.setRoot("AppLauncherPage");
        //Platform.exit();
        //System.exit(0);
    }
}

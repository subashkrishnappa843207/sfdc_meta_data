package com.cognizant.sfdc_quality_analyzer_v3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
//import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static final Logger log = Logger.getLogger(App.class.getName()); 
    static{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hhmmss");
        System.setProperty("current.date", dateFormat.format(new Date()));
    }
    
    @Override
    public void start(Stage stage) throws IOException {
    	
        scene = new Scene(loadFXML("AppLauncherPage"), 900, 700);
        scene.getStylesheets().add(App.class.getResource("css/master.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Salesforce Quality Analyzer Tool");
        stage.getIcons().add(new Image(App.class.getResourceAsStream("images/SalesForceQA360.png")));
        stage.setResizable(false);
        stage.show();
        log.info("Base Directory : "+System.getProperty("user.dir"));
        //System.out.println("System.getProperty(\"user.dir\") : "+System.getProperty("user.dir"));
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        //scene.getStylesheets().add(App.class.getResource("css/LaunchPage.css").toExternalForm());
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/"+fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
    	String log4jConfPath = System.getProperty("user.dir")+"\\target\\classes\\com\\cognizant\\sfdc_quality_analyzer_v3\\log4j.properties";
    	PropertyConfigurator.configure(log4jConfPath);
        launch();
    }

}
package com.cognizant.uicomponent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cognizant.sfdc_quality_analyzer_v3.App;
import com.cognizant.utility.ExcelUtils;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


/**
 * View-Controller for the Apex Code Coverage Details.
 * 
 * @author Debdatta Porya
 */
public class DisplayApexCodeCoverageDetailsController extends Application {
	private static final Logger log = Logger.getLogger(DisplayApexCodeCoverageDetailsController.class);
	ExcelUtils excelUtil = new ExcelUtils();
	@FXML private TextField filterField;
	@FXML private Button DownloadReportButton;
	@FXML private TableView<DisplayApexCodeCoverageDetails> displayResultTable;
	@FXML private TableColumn<DisplayApexCodeCoverageDetails, String> ApexTestClassIdColumn;
	@FXML private TableColumn<DisplayApexCodeCoverageDetails, String> TestMethodNameColumn;
	@FXML private TableColumn<DisplayApexCodeCoverageDetails, String> ApexClassOrTriggerIdColumn;
	@FXML private TableColumn<DisplayApexCodeCoverageDetails, String> CodeCoveragePercentageColumn;
	@FXML private TableColumn<DisplayApexCodeCoverageDetails, String> NumLinesCoveredColumn;
	@FXML private TableColumn<DisplayApexCodeCoverageDetails, String> NumLinesUncoveredColumn;
	public static ObservableList<DisplayApexCodeCoverageDetails> masterApexCodeCoverageData = FXCollections.observableArrayList();

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 * 
	 * Initializes the table columns and sets up sorting and filtering.
	 */
	@FXML
	private void initialize() {
		// 0. Initialize the columns.
		ApexTestClassIdColumn.setCellValueFactory(cellData -> cellData.getValue().ApexTestClassIdProperty());
		TestMethodNameColumn.setCellValueFactory(cellData -> cellData.getValue().TestMethodNameProperty());
		ApexClassOrTriggerIdColumn.setCellValueFactory(cellData -> cellData.getValue().ApexClassOrTriggerIdProperty());
		CodeCoveragePercentageColumn.setCellValueFactory(cellData -> cellData.getValue().CodeCoveragePercentageProperty());
		NumLinesCoveredColumn.setCellValueFactory(cellData -> cellData.getValue().NumLinesCoveredProperty());
		NumLinesUncoveredColumn.setCellValueFactory(cellData -> cellData.getValue().NumLinesUncoveredProperty());
		// 1. Wrap the ObservableList in a FilteredList (initially display all data).
		FilteredList<DisplayApexCodeCoverageDetails> filteredData = new FilteredList<>(masterApexCodeCoverageData, p -> true);
		
		// 2. Set the filter Predicate whenever the filter changes.
		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(displayResult -> {
				// If filter text is empty, display all Row Data
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				
				// Compare the Row data with filter text.
				String lowerCaseFilter = newValue.toLowerCase();
				
				if (displayResult.getApexTestClassId().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches 
				} else if (displayResult.getTestMethodName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches 
				} else if (displayResult.getApexClassOrTriggerId().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches 
				} else if (displayResult.getCodeCoveragePercentage().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches 
				} else if (displayResult.getNumLinesCovered().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches 
				} else if (displayResult.getNumLinesUncovered().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches
				}
				return false; // Does not match.
			});
		});
		
		// 3. Wrap the FilteredList in a SortedList. 
		SortedList<DisplayApexCodeCoverageDetails> sortedData = new SortedList<>(filteredData);
		
		// 4. Bind the SortedList comparator to the TableView comparator.
		// 	  Otherwise, sorting the TableView would have no effect.
		sortedData.comparatorProperty().bind(displayResultTable.comparatorProperty());
		
		// 5. Add sorted (and filtered) data to the table.
		displayResultTable.setItems(sortedData);
	}
	
	@FXML public void DownloadApexCodeCoverageReport() {
		String[] arrayColumnName = {"ApexTestClassId", "TestMethodName", "ApexClassOrTriggerId", "CodeCoveragePercentage", "NumLinesCovered", "NumLinesUncovered"};	
		String reportFilePath = excelUtil.exportApexCodeCoverageReport(arrayColumnName, DisplayApexCodeCoverageDetailsController.masterApexCodeCoverageData);
		System.out.println(reportFilePath);
	}
	
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Apex Code Coverage Details !!!");

        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/DisplayApexCodeCoverageDetailsTable.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Scene scene = new Scene(page);
            //scene.getStylesheets().add(App.class.getResource("css/master.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}

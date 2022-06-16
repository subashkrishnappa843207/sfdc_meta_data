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
 * View-Controller for the Security Health Check Score.
 * 
 * @author Debdatta Porya
 */
public class DisplaySecurityHealthCheckDetailsController extends Application {
	private static final Logger log = Logger.getLogger(DisplaySecurityHealthCheckDetailsController.class);
	ExcelUtils excelUtil = new ExcelUtils();
	@FXML private TextField filterField;
	@FXML private Button DownloadReportButton;
	@FXML private TableView<DisplaySecurityHealthCheckDetails> displayResultTable;
	@FXML private TableColumn<DisplaySecurityHealthCheckDetails, String> RiskCategoryColumn;
	@FXML private TableColumn<DisplaySecurityHealthCheckDetails, String> StatusColumn;
	@FXML private TableColumn<DisplaySecurityHealthCheckDetails, String> SettingColumn;
	@FXML private TableColumn<DisplaySecurityHealthCheckDetails, String> GroupColumn;
	@FXML private TableColumn<DisplaySecurityHealthCheckDetails, String> YourValueColumn;
	@FXML private TableColumn<DisplaySecurityHealthCheckDetails, String> StandardValueColumn;
	public static ObservableList<DisplaySecurityHealthCheckDetails> masterSecurityHealthCheckData = FXCollections.observableArrayList();

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 * 
	 * Initializes the table columns and sets up sorting and filtering.
	 */
	@FXML
	private void initialize() {
		// 0. Initialize the columns.
		RiskCategoryColumn.setCellValueFactory(cellData -> cellData.getValue().RiskCategoryProperty());
		StatusColumn.setCellValueFactory(cellData -> cellData.getValue().StatusProperty());
		SettingColumn.setCellValueFactory(cellData -> cellData.getValue().SettingProperty());
		GroupColumn.setCellValueFactory(cellData -> cellData.getValue().GroupProperty());
		YourValueColumn.setCellValueFactory(cellData -> cellData.getValue().YourValueProperty());
		StandardValueColumn.setCellValueFactory(cellData -> cellData.getValue().StandardValueProperty());
		// 1. Wrap the ObservableList in a FilteredList (initially display all data).
		FilteredList<DisplaySecurityHealthCheckDetails> filteredData = new FilteredList<>(masterSecurityHealthCheckData, p -> true);
		
		// 2. Set the filter Predicate whenever the filter changes.
		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(displayResult -> {
				// If filter text is empty, display all Row Data
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				
				// Compare the Row data with filter text.
				String lowerCaseFilter = newValue.toLowerCase();
				
				if (displayResult.getRiskCategory().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches Risk Category.
				} else if (displayResult.getStatus().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches Status.
				} else if (displayResult.getSetting().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches Setting.
				} else if (displayResult.getGroup().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches Group.
				} else if (displayResult.getYourValue().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches Your Value.
				} else if (displayResult.getStandardValue().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches Standard Value.
				}
				return false; // Does not match.
			});
		});
		
		// 3. Wrap the FilteredList in a SortedList. 
		SortedList<DisplaySecurityHealthCheckDetails> sortedData = new SortedList<>(filteredData);
		
		// 4. Bind the SortedList comparator to the TableView comparator.
		// 	  Otherwise, sorting the TableView would have no effect.
		sortedData.comparatorProperty().bind(displayResultTable.comparatorProperty());
		
		// 5. Add sorted (and filtered) data to the table.
		displayResultTable.setItems(sortedData);
	}
	
	@FXML public void DownloadSecurityHealthCheckReport() {
		String[] arrayColumnName = {"RiskCategory", "Status", "Setting", "Group", "YourValue", "StandardValue"};	
		String reportFilePath = excelUtil.exportSecurityHealthCheckReport(arrayColumnName, DisplaySecurityHealthCheckDetailsController.masterSecurityHealthCheckData);
		System.out.println(reportFilePath);
	}
	
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Securtiy Health Check Score Details !!!");

        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/DisplaySecurityHealthCheckDetailsTable.fxml"));
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

package com.cognizant.uicomponent;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.cognizant.sfdc_quality_analyzer_v3.App;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


/**
 * View-Controller for the Display Impacted Test Scripts based on found impacted SFDC Components.
 * 
 * @author Debdatta Porya
 */
public class DisplayImpactedTestScriptsController extends Application {
	private static final Logger log = Logger.getLogger(DisplayImpactedTestScriptsController.class);
	@FXML private TextField MappedFilterField;
	@FXML private TableView<DisplayImpactedTestScripts> ReviewMappedComponentTable;
	@FXML private TableColumn<DisplayImpactedTestScripts, String> TestCaseNameColumn;
	@FXML private TableColumn<DisplayImpactedTestScripts, String> MappedComponentTypeColumn;
	@FXML private TableColumn<DisplayImpactedTestScripts, String> MappedComponentNamesColumn;
	public static ObservableList<DisplayImpactedTestScripts> masterImpactedTestScriptsData = FXCollections.observableArrayList();

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 * 
	 * Initializes the table columns and sets up sorting and filtering.
	 */
	@FXML
	private void initialize() {
		// 0. Initialize the columns.
		TestCaseNameColumn.setCellValueFactory(cellData -> cellData.getValue().testCaseNameProperty());
		MappedComponentTypeColumn.setCellValueFactory(cellData -> cellData.getValue().componentTypeProperty());
		MappedComponentNamesColumn.setCellValueFactory(cellData -> cellData.getValue().componentNameProperty());
		// 1. Wrap the ObservableList in a FilteredList (initially display all data).
		FilteredList<DisplayImpactedTestScripts> filteredData = new FilteredList<>(masterImpactedTestScriptsData, p -> true);
		
		// 2. Set the filter Predicate whenever the filter changes.
		MappedFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(displayResult -> {
				// If filter text is empty, display all Details
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				
				// Compare Test Case Name and Component Types  with filter text.
				String lowerCaseFilter = newValue.toLowerCase();
				
				if (displayResult.getTestCaseName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches Test Cases Name.
				} else if (displayResult.getComponentType().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches Component Type.
				} /*else if (displayResult.getComponentNames().forEach(action -> action.toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches Component Names.
				}*/
				return false; // Does not match.
			});
		});
		
		// 3. Wrap the FilteredList in a SortedList. 
		SortedList<DisplayImpactedTestScripts> sortedData = new SortedList<>(filteredData);
		
		// 4. Bind the SortedList comparator to the TableView comparator.
		// 	  Otherwise, sorting the TableView would have no effect.
		sortedData.comparatorProperty().bind(ReviewMappedComponentTable.comparatorProperty());
		
		// 5. Add sorted (and filtered) data to the table.
		ReviewMappedComponentTable.setItems(sortedData);
	}
	
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Impacted Test Cases details !!!");

        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/DisplayImpactedTestScriptsTable.fxml"));
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

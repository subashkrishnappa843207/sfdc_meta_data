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
 * View-Controller for the Display Impacted Components.
 * 
 * @author Debdatta Porya
 */
public class DisplayImpactedComponentsController extends Application {
	private static final Logger log = Logger.getLogger(DisplayImpactedComponentsController.class);
	@FXML private TextField filterField;
	@FXML private TableView<DisplayImpactedComponents> displayResultTable;
	@FXML private TableColumn<DisplayImpactedComponents, String> componentTypeColumn;
	@FXML private TableColumn<DisplayImpactedComponents, String> componentNameColumn;
	@FXML private TableColumn<DisplayImpactedComponents, String> lastModifiedByIDColumn;
	@FXML private TableColumn<DisplayImpactedComponents, String> lastModifiedDateColumn;
	public static ObservableList<DisplayImpactedComponents> masterData = FXCollections.observableArrayList();

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 * 
	 * Initializes the table columns and sets up sorting and filtering.
	 */
	@FXML
	private void initialize() {
		// 0. Initialize the columns.
		componentTypeColumn.setCellValueFactory(cellData -> cellData.getValue().componentTypeProperty());
		componentNameColumn.setCellValueFactory(cellData -> cellData.getValue().componentNameProperty());
		lastModifiedByIDColumn.setCellValueFactory(cellData -> cellData.getValue().lastModifiedByIDProperty());
		lastModifiedDateColumn.setCellValueFactory(cellData -> cellData.getValue().lastModifiedDateProperty());
		// 1. Wrap the ObservableList in a FilteredList (initially display all data).
		FilteredList<DisplayImpactedComponents> filteredData = new FilteredList<>(masterData, p -> true);
		
		// 2. Set the filter Predicate whenever the filter changes.
		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(displayResult -> {
				// If filter text is empty, display all Impacted Components.
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				
				String lowerCaseFilter = newValue.toLowerCase();
				
				if (displayResult.getComponentType().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches component type.
				} else if (displayResult.getComponentName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches component name.
				} else if (displayResult.getLastModifiedByID().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last modified date.
				} else if (displayResult.getLastModifiedDate().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last modified date.
				}
				return false; // Does not match.
			});
		});
		
		// 3. Wrap the FilteredList in a SortedList. 
		SortedList<DisplayImpactedComponents> sortedData = new SortedList<>(filteredData);
		
		// 4. Bind the SortedList comparator to the TableView comparator.
		// 	  Otherwise, sorting the TableView would have no effect.
		sortedData.comparatorProperty().bind(displayResultTable.comparatorProperty());
		
		// 5. Add sorted (and filtered) data to the table.
		displayResultTable.setItems(sortedData);
	}
	
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Salesforce Impacted Components !!!");

        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/DisplayImpactedComponentsTable.fxml"));
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

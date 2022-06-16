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
 * View-Controller for the Display In Scope Components 
 * of Salesforce Impact Analyzer tool.
 * 
 * @author Debdatta Porya
 */
public class DisplayInScopeComponentsController extends Application {
	private static final Logger log = Logger.getLogger(DisplayInScopeComponentsController.class);
	@FXML private TextField filterField;
	@FXML private TableView<DisplayInScopeComponents> displayResultTable;
	@FXML private TableColumn<DisplayInScopeComponents, String> componentTypeColumn;
	public static ObservableList<DisplayInScopeComponents> masterData = FXCollections.observableArrayList();

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

		// 1. Wrap the ObservableList in a FilteredList (initially display all data).
		FilteredList<DisplayInScopeComponents> filteredData = new FilteredList<>(masterData, p -> true);
		
		// 2. Set the filter Predicate whenever the filter changes.
		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(displayResult -> {
				// If filter text is empty, display all In scope components.
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				
				String lowerCaseFilter = newValue.toLowerCase();
				
				if (displayResult.getComponentType().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches Component Type.
				}
				return false; // Does not match.
			});
		});
		
		// 3. Wrap the FilteredList in a SortedList. 
		SortedList<DisplayInScopeComponents> sortedData = new SortedList<>(filteredData);
		
		// 4. Bind the SortedList comparator to the TableView comparator.
		// 	  Otherwise, sorting the TableView would have no effect.
		sortedData.comparatorProperty().bind(displayResultTable.comparatorProperty());
		
		// 5. Add sorted (and filtered) data to the table.
		displayResultTable.setItems(sortedData);
	}
	
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Salesforce In Scope Components !!!");

        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/DisplayInScopeComponentsTable.fxml"));
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

package com.cognizant.uicomponent;

import java.io.IOException;
import java.util.List;

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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


/**
 * View-Controller for the Display Multi Org Missing Components.
 * 
 * @author Debdatta Porya
 */
public class DisplayMultiOrgMissingComponentsController extends Application {
	private static final Logger log = Logger.getLogger(DisplayMultiOrgMissingComponentsController.class);
	@FXML private TextField MappedFilterField;
	@FXML private TableView<DisplayMultiOrgMissingComponents> ReviewMultiOrgReviewComponentTable;
	@FXML private TableColumn<DisplayMultiOrgMissingComponents, String> ComponentTypeColumn;
	@FXML private TableColumn<DisplayMultiOrgMissingComponents, String> SourceOrgIdColumn;
	@FXML private TableColumn<DisplayMultiOrgMissingComponents, List<String>> SourceOrgComponentDetailsColumn;
	@FXML private TableColumn<DisplayMultiOrgMissingComponents, String> TargetOrgIdColumn;
	@FXML private TableColumn<DisplayMultiOrgMissingComponents, List<String>> TargetOrgComponentDetailsColumn;
	@FXML private TableColumn<DisplayMultiOrgMissingComponents, List<String>> MissingComponentDetailsColumn;
	@FXML private TableColumn<DisplayMultiOrgMissingComponents, List<String>> MatchingComponentDetailsColumn;
	public static ObservableList<DisplayMultiOrgMissingComponents> masterMultiOrgMissingData = FXCollections.observableArrayList();

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 * 
	 * Initializes the table columns and sets up sorting and filtering.
	 */
	@FXML
	private void initialize() {
		// 0. Initialize the columns.
		ComponentTypeColumn.setCellValueFactory(cellData -> cellData.getValue().ComponentTypeProperty());
		SourceOrgIdColumn.setCellValueFactory(cellData -> cellData.getValue().SourceOrgIdProperty());
		SourceOrgComponentDetailsColumn.setCellValueFactory(cellData -> cellData.getValue().SourceOrgComponentDetailsProperty());
		TargetOrgIdColumn.setCellValueFactory(cellData -> cellData.getValue().TargetOrgIdProperty());
		TargetOrgComponentDetailsColumn.setCellValueFactory(cellData -> cellData.getValue().TargetOrgComponentDetailsProperty());
		MissingComponentDetailsColumn.setCellValueFactory(cellData -> cellData.getValue().MissingComponentDetailsProperty());
		MatchingComponentDetailsColumn.setCellValueFactory(cellData -> cellData.getValue().MatchingComponentDetailsProperty());

		// 1. Wrap the ObservableList in a FilteredList (initially display all data).
		FilteredList<DisplayMultiOrgMissingComponents> filteredData = new FilteredList<>(masterMultiOrgMissingData, p -> true);
		
		// 2. Set the filter Predicate whenever the filter changes.
		MappedFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(displayResult -> {
				// If filter text is empty, display all Details
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				
				// Compare Test Case Name and Component Types  with filter text.
				String lowerCaseFilter = newValue.toLowerCase();
				
				if (displayResult.getComponentType().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches Component Type.
				} else if (displayResult.getSourceOrgId().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches Source Org Id.
				} else if (displayResult.getTargetOrgId().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches Target Org Id.
				}
				return false; // Does not match.
			});
		});
		
		// 3. Wrap the FilteredList in a SortedList. 
		SortedList<DisplayMultiOrgMissingComponents> sortedData = new SortedList<>(filteredData);
		
		// 4. Bind the SortedList comparator to the TableView comparator.
		// 	  Otherwise, sorting the TableView would have no effect.
		sortedData.comparatorProperty().bind(ReviewMultiOrgReviewComponentTable.comparatorProperty());
		
		// 5. Add sorted (and filtered) data to the table.
		ReviewMultiOrgReviewComponentTable.setItems(sortedData);
	}
	
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Review Multi Org Missing And Marching Components !!!");

        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/DisplayMultiOrgMissingComponentsTable.fxml"));
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

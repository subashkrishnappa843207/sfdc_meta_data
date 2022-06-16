package com.cognizant.uicomponent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cognizant.utility.SFDCContext;

import io.restassured.response.Response;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;

@SuppressWarnings("all")
public class SFDCSecurityHealthCheckDetailsPage extends Application{
	/***************************************************************************************************
	 * @purpose : Show Security Health check score details in new window
	 * @author: Debdatta Porya
	 ***************************************************************************************************/
	private static final Logger log = Logger.getLogger(SFDCSecurityHealthCheckDetailsPage.class);
	private TreeItem<Map<String, Object>> root;
    private TreeTableView<Map<String, Object>> tree;

    @Override
    public void start(Stage primaryStage) throws Exception {
    	//DetailsAnchorPane = FXMLLoader.load(getClass().getResource("SFDCSecurityHealthCheckDetailsPage.fxml"));
    	SplitPane DetailsSplitPane = new SplitPane();
    	DetailsSplitPane.setOrientation(Orientation.VERTICAL);
    	AnchorPane FirstAnchor = new AnchorPane();
    	AnchorPane SecondAnchor = new AnchorPane();
    	Label label = new Label("Search your keyword here : ");
        TextField filter = new TextField();
        filter.setPromptText("Search your keyword here!!!");
        filter.textProperty().addListener((observable, oldValue, newValue) -> filterChanged(newValue));
        HBox hbox = new HBox(label, filter);
        root = new TreeItem<>();
        tree = new TreeTableView<>(root);
        addColumn("STATUS", "status");
        addColumn("SETTING", "setting");
        addColumn("GROUP", "group");
        addColumn("YOUR_VALUE", "yourvalue");
        addColumn("STANDARD_VALUE", "standardvalue");

        setup();
        tree.setShowRoot(false);
        FirstAnchor.getChildren().addAll(hbox);
        FirstAnchor.setMaxSize(100, 500);
        SecondAnchor.getChildren().addAll(tree);
        DetailsSplitPane.setDividerPositions(0.07);
        //SecurityHealthCheckAnchorPane.getChildren().add(tree);
        DetailsSplitPane.getItems().addAll(FirstAnchor, SecondAnchor);
        //DetailsAnchorPane.getChildren().addAll(DetailsSplitPane);
        Scene scene = new Scene(DetailsSplitPane);
        primaryStage.setTitle("Salesforce Security Health Check Details Page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void filterChanged(String filter) {
        if (filter.isEmpty()) {
            tree.setRoot(root);         
        } else {
            TreeItem<Map<String, Object>> filteredRoot = new TreeItem<>();
            filter(root, filter, filteredRoot);
            tree.setRoot(filteredRoot);
        }
    }


    private void filter(TreeItem<Map<String, Object>> root, String filter, TreeItem<Map<String, Object>> filteredRoot) {
        for (TreeItem<Map<String, Object>> child : root.getChildren()) {            
            TreeItem<Map<String, Object>> filteredChild = new TreeItem<>();
            filteredChild.setValue(child.getValue());
            filteredChild.setExpanded(true);
            filter(child, filter, filteredChild );
            if (!filteredChild.getChildren().isEmpty() || isMatch(filteredChild.getValue(), filter)) {
                System.out.println(filteredChild.getValue() + " matches.");
                filteredRoot.getChildren().add(filteredChild);
            }
        }
    }

    private boolean isMatch(Map<String, Object> value, String filter) {
        return value.values().stream().anyMatch((Object o) -> o.toString().contains(filter));
    }
    
    private JSONArray getSalesforceToolingRecords(String Query) throws JSONException {
	    Response hcs_HighRisk_Response = SFDCContext.runToolingQuery(Query);
	    System.out.println("hcs_HighRisk_Response:"+hcs_HighRisk_Response.asPrettyString());
	    JSONObject resultsJObject = new JSONObject(hcs_HighRisk_Response.body().asPrettyString());
		JSONArray jsonRecordsArray = resultsJObject.optJSONArray("records");
		return jsonRecordsArray;
    }

    private void setup() throws JSONException {
    	int highRiskCount = 0, mediumRiskCount = 0, meetStandardCount = 0;
    	//Below lines of code to add High Risk Security settings in Tree Table View
        TreeItem<Map<String, Object>> highRiskSettings = createItem(root, "High Risk Security Settings", "", "", "","");
        TreeItem<Map<String, Object>> highRiskCriticalSettings = createItem(highRiskSettings, "Critical", "", "","","");
        TreeItem<Map<String, Object>> highRiskWarningSettings = createItem(highRiskSettings, "Warning", "", "","","");
        TreeItem<Map<String, Object>> highRiskCompliantSettings = createItem(highRiskSettings, "Compliant", "", "","","");
    	String hcs_HighRisk_Query = "SELECT RiskType, Setting, SettingGroup, OrgValue, StandardValue FROM SecurityHealthCheckRisks where SettingRiskCategory='HIGH_RISK'";
    	JSONArray jsonHighRiskArray = getSalesforceToolingRecords(hcs_HighRisk_Query);
	    int totalSizeHighRisk = jsonHighRiskArray.length();
	    System.out.println("Total Records :"+totalSizeHighRisk);
	    for(int i=0;i<totalSizeHighRisk;i++) {
			JSONObject jsonRecords = new JSONObject(jsonHighRiskArray.get(i).toString());
			if(jsonRecords.getString("RiskType").contentEquals("HIGH_RISK")) {
				highRiskCount += highRiskCount;
				createItem(highRiskCriticalSettings, "Critical", jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"),
						jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue"));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEDIUM_RISK")) {
				mediumRiskCount += mediumRiskCount;
				createItem(highRiskWarningSettings, "Warning", jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"),
						jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue"));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEETS_STANDARD")) {
				meetStandardCount += meetStandardCount;
				createItem(highRiskCompliantSettings, "Compliant", jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"),
						jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue"));
			}
	    }

    	//Below lines of code to add Medium Risk Security settings in Tree Table View
        TreeItem<Map<String, Object>> mediumRiskSettings = createItem(root, "Medium Risk Security Settings", "", "", "","");
        TreeItem<Map<String, Object>> mediumRiskCriticalSettings = createItem(mediumRiskSettings, "Critical", "", "","","");
        TreeItem<Map<String, Object>> mediumRiskWarningSettings = createItem(mediumRiskSettings, "Warning", "", "","","");
        TreeItem<Map<String, Object>> mediumRiskCompliantSettings = createItem(mediumRiskSettings, "Compliant", "", "","","");
    	String hcs_MediumRisk_Query = "SELECT RiskType, Setting, SettingGroup, OrgValue, StandardValue FROM SecurityHealthCheckRisks where SettingRiskCategory='MEDIUM_RISK'";
    	JSONArray jsonMediumRiskArray = getSalesforceToolingRecords(hcs_MediumRisk_Query);
	    int totalSizeMediumRisk = jsonMediumRiskArray.length();
	    System.out.println("Total Records :"+totalSizeMediumRisk);
	    for(int i=0;i<totalSizeMediumRisk;i++) {
			JSONObject jsonRecords = new JSONObject(jsonMediumRiskArray.get(i).toString());
			if(jsonRecords.getString("RiskType").contentEquals("HIGH_RISK")) {
				highRiskCount += highRiskCount;
				createItem(mediumRiskCriticalSettings, "Critical", jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"),
						jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue"));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEDIUM_RISK")) {
				mediumRiskCount += mediumRiskCount;
				createItem(mediumRiskWarningSettings, "Warning", jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"),
						jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue"));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEETS_STANDARD")) {
				meetStandardCount += meetStandardCount;
				createItem(mediumRiskCompliantSettings, "Compliant", jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"),
						jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue"));
			}
	    }
	    
    	//Below lines of code to add Medium Risk Security settings in Tree Table View
        TreeItem<Map<String, Object>> lowRiskSettings = createItem(root, "Low Risk Security Settings", "", "", "","");
        TreeItem<Map<String, Object>> lowRiskCriticalSettings = createItem(lowRiskSettings, "Critical", "", "","","");
        TreeItem<Map<String, Object>> lowRiskWarningSettings = createItem(lowRiskSettings, "Warning", "", "","","");
        TreeItem<Map<String, Object>> lowRiskCompliantSettings = createItem(lowRiskSettings, "Compliant", "", "","","");
    	String hcs_LowRisk_Query = "SELECT RiskType, Setting, SettingGroup, OrgValue, StandardValue FROM SecurityHealthCheckRisks where SettingRiskCategory='LOW_RISK'";
    	JSONArray jsonLowRiskArray = getSalesforceToolingRecords(hcs_LowRisk_Query);
	    int totalSizeLowRisk = jsonLowRiskArray.length();
	    System.out.println("Total Records :"+totalSizeLowRisk);
	    for(int i=0;i<totalSizeLowRisk;i++) {
			JSONObject jsonRecords = new JSONObject(jsonLowRiskArray.get(i).toString());
			if(jsonRecords.getString("RiskType").contentEquals("HIGH_RISK")) {
				highRiskCount += highRiskCount;
				createItem(lowRiskCriticalSettings, "Critical", jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"),
						jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue"));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEDIUM_RISK")) {
				mediumRiskCount += mediumRiskCount;
				createItem(lowRiskWarningSettings, "Warning", jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"),
						jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue"));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEETS_STANDARD")) {
				meetStandardCount += meetStandardCount;
				createItem(lowRiskCompliantSettings, "Compliant", jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"),
						jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue"));
			}
	    } 
	    
    	//Below lines of code to add Medium Risk Security settings in Tree Table View
        TreeItem<Map<String, Object>> informationalSettings = createItem(root, "Informational Security Settings", "", "", "","");
        TreeItem<Map<String, Object>> informationalCriticalSettings = createItem(informationalSettings, "Critical", "", "","","");
        TreeItem<Map<String, Object>> informationalWarningSettings = createItem(informationalSettings, "Warning", "", "","","");
        TreeItem<Map<String, Object>> informationalCompliantSettings = createItem(informationalSettings, "Compliant", "", "","","");
    	String hcs_Informational_Query = "SELECT RiskType, Setting, SettingGroup, OrgValue, StandardValue FROM SecurityHealthCheckRisks where SettingRiskCategory='INFORMATIONAL'";
    	JSONArray jsonInformationalArray = getSalesforceToolingRecords(hcs_Informational_Query);
	    int totalSizeInformational = jsonInformationalArray.length();
	    System.out.println("Total Records :"+totalSizeInformational);
	    for(int i=0;i<totalSizeInformational;i++) {
			JSONObject jsonRecords = new JSONObject(jsonInformationalArray.get(i).toString());
			if(jsonRecords.getString("RiskType").contentEquals("HIGH_RISK")) {
				highRiskCount += highRiskCount;
				createItem(informationalCriticalSettings, "Critical", jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"),
						jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue"));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEDIUM_RISK")) {
				mediumRiskCount += mediumRiskCount;
				createItem(informationalWarningSettings, "Warning", jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"),
						jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue"));
			}else if(jsonRecords.getString("RiskType").contentEquals("MEETS_STANDARD")) {
				meetStandardCount += meetStandardCount;
				createItem(informationalCompliantSettings, "Compliant", jsonRecords.getString("Setting"), jsonRecords.getString("SettingGroup"),
						jsonRecords.getString("OrgValue"), jsonRecords.getString("StandardValue"));
			}
	    }
	    
     }

    private TreeItem<Map<String, Object>> createItem(TreeItem<Map<String, Object>> parent, String status, String setting, String group, String yourvalue, String standardvalue) {
        TreeItem<Map<String, Object>> item = new TreeItem<>();
        Map<String, Object> value = new HashMap<>();
        value.put("status",  status);
        value.put("setting", setting);
        value.put("group", group);
        value.put("yourvalue", yourvalue);
        value.put("standardvalue", standardvalue);
        item.setValue(value);
        parent.getChildren().add(item);
        item.setExpanded(true);
        return item;
    }

    protected void addColumn(String label, String dataIndex) {
        TreeTableColumn<Map<String, Object>, String> column = new TreeTableColumn<>(label);
        column.setPrefWidth(200);
        column.setCellValueFactory(
            (TreeTableColumn.CellDataFeatures<Map<String, Object>, String> param) -> {
                ObservableValue<String> result = new ReadOnlyStringWrapper("");
                if (param.getValue().getValue() != null) {
                    result = new ReadOnlyStringWrapper("" + param.getValue().getValue().get(dataIndex));
                }
                return result;
            }
        );      
        tree.getColumns().add(column);

        //tree.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }


}
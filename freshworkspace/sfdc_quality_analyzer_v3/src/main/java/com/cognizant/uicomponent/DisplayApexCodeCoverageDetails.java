package com.cognizant.uicomponent;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;

import com.cognizant.login.OAuthToken;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Getter Setter for the Display Apex Code Coverage Details 
 * using Salesforce Impact Analyzer tool.
 */
public class DisplayApexCodeCoverageDetails {
	private static final Logger log = Logger.getLogger(DisplayApexCodeCoverageDetails.class);
	/*String[] arrayColumnName = {"ApexTestClassId", "TestMethodName", "ApexClassOrTriggerId", "CodeCoveragePercentage", "NumLinesCovered", "NumLinesUncovered"};	*/
	private final StringProperty ApexTestClassId;
	private final StringProperty TestMethodName;
	private final StringProperty ApexClassOrTriggerId;
	private final StringProperty CodeCoveragePercentage;
	private final StringProperty NumLinesCovered;
	private final StringProperty NumLinesUncovered;
	
	public DisplayApexCodeCoverageDetails(String ApexTestClassId, String TestMethodName, String ApexClassOrTriggerId, String CodeCoveragePercentage, 
			String NumLinesCovered, String NumLinesUncovered) {
		this.ApexTestClassId = new SimpleStringProperty(ApexTestClassId);
		this.TestMethodName = new SimpleStringProperty(TestMethodName);
		this.ApexClassOrTriggerId = new SimpleStringProperty(ApexClassOrTriggerId);
		this.CodeCoveragePercentage = new SimpleStringProperty(CodeCoveragePercentage);
		this.NumLinesCovered = new SimpleStringProperty(NumLinesCovered);
		this.NumLinesUncovered = new SimpleStringProperty(NumLinesUncovered);
	}
	
	public String getApexTestClassId() {
		return ApexTestClassId.get();
	}

	public void setApexTestClassId(String ApexTestClassId) {
		this.ApexTestClassId.set(ApexTestClassId);
	}
	
	public StringProperty ApexTestClassIdProperty() {
		return ApexTestClassId;
	}
	
	public String getTestMethodName() {
		return TestMethodName.get();
	}

	public void setTestMethodName(String TestMethodName) {
		this.TestMethodName.set(TestMethodName);
	}
	
	public StringProperty TestMethodNameProperty() {
		return TestMethodName;
	}

	public String getApexClassOrTriggerId() {
		return ApexClassOrTriggerId.get();
	}

	public void setApexClassOrTriggerId(String ApexClassOrTriggerId) {
		this.ApexClassOrTriggerId.set(ApexClassOrTriggerId);
	}
	
	public StringProperty ApexClassOrTriggerIdProperty() {
		return ApexClassOrTriggerId;
	}
	
	public String getCodeCoveragePercentage() {
		return CodeCoveragePercentage.get();
	}

	public void setCodeCoveragePercentage(String CodeCoveragePercentage) {
		this.CodeCoveragePercentage.set(CodeCoveragePercentage);
	}
	
	public StringProperty CodeCoveragePercentageProperty() {
		return CodeCoveragePercentage;
	}
	
	public String getNumLinesCovered() {
		return NumLinesCovered.get();
	}

	public void setNumLinesCovered(String NumLinesCovered) {
		this.NumLinesCovered.set(NumLinesCovered);
	}
	
	public StringProperty NumLinesCoveredProperty() {
		return NumLinesCovered;
	}

	public String getNumLinesUncovered() {
		return NumLinesUncovered.get();
	}

	public void setNumLinesUncovered(String NumLinesUncovered) {
		this.NumLinesUncovered.set(NumLinesUncovered);
	}
	
	public StringProperty NumLinesUncoveredProperty() {
		return NumLinesUncovered;
	}
}
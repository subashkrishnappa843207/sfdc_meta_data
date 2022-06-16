package com.cognizant.uicomponent;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Getter Setter for the Display Security Health Check Score detail Components 
 * of Salesforce Impact Analyzer tool.
 */
public class DisplaySecurityHealthCheckDetails {
	private static final Logger log = Logger.getLogger(DisplaySecurityHealthCheckDetails.class);
	private final StringProperty riskcategory;
	private final StringProperty status;
	private final StringProperty setting;
	private final StringProperty group;
	private final StringProperty yourvalue;
	private final StringProperty standardvalue;
	
	public DisplaySecurityHealthCheckDetails(String riskcategory, String status, String setting, String group, String yourvalue, String standardvalue) {
		this.riskcategory = new SimpleStringProperty(riskcategory);
		this.status = new SimpleStringProperty(status);
		this.setting = new SimpleStringProperty(setting);
		this.group = new SimpleStringProperty(group);
		this.yourvalue = new SimpleStringProperty(yourvalue);
		this.standardvalue = new SimpleStringProperty(standardvalue);
	}
	
	public String getRiskCategory() {
		return riskcategory.get();
	}

	public void setRiskCategory(String riskcategory) {
		this.riskcategory.set(riskcategory);
	}
	
	public StringProperty RiskCategoryProperty() {
		return riskcategory;
	}
	
	public String getStatus() {
		return status.get();
	}

	public void setStatus(String status) {
		this.status.set(status);
	}
	
	public StringProperty StatusProperty() {
		return status;
	}

	public String getSetting() {
		return setting.get();
	}

	public void setSetting(String setting) {
		this.setting.set(setting);
	}
	
	public StringProperty SettingProperty() {
		return setting;
	}
	
	public String getGroup() {
		return group.get();
	}

	public void setGroup(String group) {
		this.group.set(group);
	}
	
	public StringProperty GroupProperty() {
		return group;
	}
	
	public String getYourValue() {
		return yourvalue.get();
	}

	public void setYourValue(String yourvalue) {
		this.yourvalue.set(yourvalue);
	}
	
	public StringProperty YourValueProperty() {
		return yourvalue;
	}

	public String getStandardValue() {
		return standardvalue.get();
	}

	public void setStandardValue(String standardvalue) {
		this.standardvalue.set(standardvalue);
	}
	
	public StringProperty StandardValueProperty() {
		return standardvalue;
	}
}
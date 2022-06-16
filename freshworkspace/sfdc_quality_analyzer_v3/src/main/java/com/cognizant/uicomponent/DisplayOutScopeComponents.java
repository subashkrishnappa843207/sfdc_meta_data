package com.cognizant.uicomponent;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Getter Setter for the Display Out of Scope Components 
 * of Salesforce Impact Analyzer tool.
 */
public class DisplayOutScopeComponents {
	private static final Logger log = Logger.getLogger(DisplayOutScopeComponents.class);
	private final StringProperty componentType;

	public DisplayOutScopeComponents(String componentType) {
		this.componentType = new SimpleStringProperty(componentType);
	}
	
	public String getComponentType() {
		return componentType.get();
	}

	public void setComponentType(String componentType) {
		this.componentType.set(componentType);
	}
	
	public StringProperty componentTypeProperty() {
		return componentType;
	}

}
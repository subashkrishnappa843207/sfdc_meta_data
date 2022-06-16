package com.cognizant.uicomponent;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Getter Setter class for the Display In Scope Components of Salesforce Impact Analyzer tool.
 */
public class DisplayInScopeComponents {
	private static final Logger log = Logger.getLogger(DisplayInScopeComponents.class);
	private final StringProperty componentType;

	public DisplayInScopeComponents(String componentType) {
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
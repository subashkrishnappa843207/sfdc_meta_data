package com.cognizant.uicomponent;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 */
public class DisplayImpactedComponents {
	private static final Logger log = Logger.getLogger(DisplayImpactedComponents.class);
	private final StringProperty componentType;
	private final StringProperty componentName;
	private final StringProperty lastModifiedByID;
	private final StringProperty lastModifiedDate;
	public DisplayImpactedComponents(String componentType, String componentName, String lastModifiedByID, String lastModifiedDate) {
		this.componentType = new SimpleStringProperty(componentType);
		this.componentName = new SimpleStringProperty(componentName);
		this.lastModifiedByID = new SimpleStringProperty(lastModifiedByID);
		this.lastModifiedDate = new SimpleStringProperty(lastModifiedDate);
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

	public String getComponentName() {
		return componentName.get();
	}

	public void setComponentName(String componentName) {
		this.componentName.set(componentName);
	}
	
	public StringProperty componentNameProperty() {
		return componentName;
	}
	
	public String getLastModifiedByID() {
		return lastModifiedByID.get();
	}

	public void setLastModifiedByID(String lastModifiedByID) {
		this.lastModifiedByID.set(lastModifiedByID);
	}
	
	public StringProperty lastModifiedByIDProperty() {
		return lastModifiedByID;
	}
	
	public String getLastModifiedDate() {
		return lastModifiedDate.get();
	}

	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate.set(lastModifiedDate);
	}
	
	public StringProperty lastModifiedDateProperty() {
		return lastModifiedDate;
	}

}
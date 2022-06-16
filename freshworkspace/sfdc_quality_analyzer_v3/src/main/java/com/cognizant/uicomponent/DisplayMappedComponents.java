package com.cognizant.uicomponent;

import java.util.List;

import org.apache.log4j.Logger;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class DisplayMappedComponents {
	private static final Logger log = Logger.getLogger(DisplayMappedComponents.class);
	private final SimpleStringProperty TestCaseName;
    private final SimpleStringProperty ComponentType;
    private final SimpleListProperty ComponentNames;
 
    public DisplayMappedComponents (String TCaseName , String CType, ObservableList<String> CNames) {
		this.TestCaseName = new SimpleStringProperty(TCaseName);
    	this.ComponentType = new SimpleStringProperty(CType);
    	this.ComponentNames = new SimpleListProperty<String>(CNames);
    }

	public String getTestCaseName() {
		return TestCaseName.get();
	}

	public void setTestCaseName(String TCaseName) {
		this.TestCaseName.set(TCaseName);
	}
	
	public StringProperty testCaseNameProperty() {
		return TestCaseName;
	}
	
	public String getComponentType() {
		return ComponentType.get();
	}

	public void setComponentType(String CType) {
		this.ComponentType.set(CType);
	}
	
	public StringProperty componentTypeProperty() {
		return ComponentType;
	}
	
    public ObservableList<String> getComponentNames() {
        return ComponentNames.get();
    }
    
    public void setComponentNames(ObservableList<String> CNames) {
    	this.ComponentNames.set(CNames);
    }

	public ListProperty componentNameProperty() {
		return ComponentNames;
	}
	
}
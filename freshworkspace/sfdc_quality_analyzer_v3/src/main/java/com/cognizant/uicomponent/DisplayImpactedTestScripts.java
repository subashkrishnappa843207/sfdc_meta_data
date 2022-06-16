package com.cognizant.uicomponent;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 *
 */
public class DisplayImpactedTestScripts {
	private static final Logger log = Logger.getLogger(DisplayImpactedTestScripts.class);
    private final SimpleStringProperty ComponentType;
	private final SimpleStringProperty TestCaseName;
    private final SimpleListProperty ComponentNames;
 
    public DisplayImpactedTestScripts (String CType, String TCaseName , ObservableList<String> k) {
		this.TestCaseName = new SimpleStringProperty(TCaseName);
    	this.ComponentType = new SimpleStringProperty(CType);
    	this.ComponentNames = new SimpleListProperty<String>(k);
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
package com.cognizant.uicomponent;

import java.util.List;

import org.apache.log4j.Logger;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class DisplayMultiOrgMissingComponents {
	private static final Logger log = Logger.getLogger(DisplayMultiOrgMissingComponents.class);
	private final SimpleStringProperty ComponentType;
    private final SimpleStringProperty SourceOrgId;
    private final SimpleListProperty SourceOrgComponentDetails;
    private final SimpleStringProperty TargetOrgId;
    private final SimpleListProperty TargetOrgComponentDetails;
    private final SimpleListProperty MissingComponentDetails;
    private final SimpleListProperty MatchingComponentDetails;
 
    public DisplayMultiOrgMissingComponents (String ComponentType , String SourceOrgId, ObservableList<String> SourceOrgComponentDetails,
    		String TargetOrgId, ObservableList<String> TargetOrgComponentDetails, 
    		ObservableList<String> MissingComponentDetails, ObservableList<String> MatchingComponentDetails) {
		this.ComponentType = new SimpleStringProperty(ComponentType);
    	this.SourceOrgId = new SimpleStringProperty(SourceOrgId);
    	this.SourceOrgComponentDetails = new SimpleListProperty<String>(SourceOrgComponentDetails);
    	this.TargetOrgId = new SimpleStringProperty(TargetOrgId);
    	this.TargetOrgComponentDetails = new SimpleListProperty<String>(TargetOrgComponentDetails);
    	this.MissingComponentDetails = new SimpleListProperty<String>(MissingComponentDetails);
    	this.MatchingComponentDetails = new SimpleListProperty<String>(MatchingComponentDetails);
    	
    }

	public String getComponentType() {
		return ComponentType.get();
	}

	public void setComponentType(String ComponentType) {
		this.ComponentType.set(ComponentType);
	}
	
	public StringProperty ComponentTypeProperty() {
		return ComponentType;
	}
	
	public String getSourceOrgId() {
		return SourceOrgId.get();
	}

	public void setSourceOrgId(String SourceOrgId) {
		this.SourceOrgId.set(SourceOrgId);
	}
	
	public StringProperty SourceOrgIdProperty() {
		return SourceOrgId;
	}
	
    public ObservableList<String> getSourceOrgComponentDetails() {
        return SourceOrgComponentDetails.get();
    }
    
    public void setSourceOrgComponentDetails(ObservableList<String> SourceOrgComponentDetails) {
    	this.SourceOrgComponentDetails.set(SourceOrgComponentDetails);
    }

	public ListProperty SourceOrgComponentDetailsProperty() {
		return SourceOrgComponentDetails;
	}
	
	public String getTargetOrgId() {
		return TargetOrgId.get();
	}

	public void setTargetOrgId(String TargetOrgId) {
		this.TargetOrgId.set(TargetOrgId);
	}
	
	public StringProperty TargetOrgIdProperty() {
		return TargetOrgId;
	}
	
    public ObservableList<String> getTargetOrgComponentDetails() {
        return TargetOrgComponentDetails.get();
    }
    
    public void setTargetOrgComponentDetails(ObservableList<String> TargetOrgComponentDetails) {
    	this.TargetOrgComponentDetails.set(TargetOrgComponentDetails);
    }

	public ListProperty TargetOrgComponentDetailsProperty() {
		return TargetOrgComponentDetails;
	}
	
    public ObservableList<String> getMissingComponentDetails() {
        return MissingComponentDetails.get();
    }
    
    public void setMissingComponentDetails(ObservableList<String> MissingComponentDetails) {
    	this.MissingComponentDetails.set(MissingComponentDetails);
    }

	public ListProperty MissingComponentDetailsProperty() {
		return MissingComponentDetails;
	}
	
    public ObservableList<String> getMatchingComponentDetails() {
        return MatchingComponentDetails.get();
    }
    
    public void setMatchingComponentDetails(ObservableList<String> MatchingComponentDetails) {
    	this.MatchingComponentDetails.set(MatchingComponentDetails);
    }

	public ListProperty MatchingComponentDetailsProperty() {
		return MatchingComponentDetails;
	}
}
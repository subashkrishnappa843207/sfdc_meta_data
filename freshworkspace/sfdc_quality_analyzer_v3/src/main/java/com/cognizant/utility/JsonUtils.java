package com.cognizant.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.cognizant.uicomponent.DisplayMultiOrgMissingComponents;
import com.cognizant.uicomponent.SFDCSecurityHealthCheckDetailsPage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javafx.collections.ObservableList;

public class JsonUtils {
	private static final Logger log = Logger.getLogger(JsonUtils.class);
	public FileInputStream fis = null;
	public String SFDC_Mapping_FilePath = System.getProperty("user.dir")+"\\target\\classes\\com\\cognizant\\sfdc_quality_analyzer_v3\\excel\\SFDCSobjectMappingSheet.xlsx";
	public String JSON_Report_Directory = System.getProperty("user.dir")+"\\Report\\JSON";
	public String JSON_FileName_Prefix = "\\ImpactAnalysisReport_";
	public String JSON_Mapping_Directory = System.getProperty("user.dir")+"\\Mapping\\JSON\\Target";
	public String JSON_MappingFileName_Prefix = "\\FinalMappingFile_";
	public String DownLoadResultJSON_FileName_Prefix = "\\ResultData_";

	private String resultFilePath = null;
	private String mappingFinalFilePath = null;
	private String getFileName(String baseName) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String dateTimeInfo = dateFormat.format(new Date());
        baseName = baseName.concat(dateTimeInfo);
        return baseName.concat(String.format(".json"));
    }
	
	public String exportJSONFile(String[] arrayColumnName, Map<String, HashMap<String, String>> FoundComponentsFinalMap) {
		try {
			resultFilePath = getFileName(JSON_Report_Directory.concat(DownLoadResultJSON_FileName_Prefix));
	    	System.out.println("JSON File Path :"+resultFilePath);
	
	    	File fileDir = new File(JSON_Report_Directory);
	    	if(!fileDir.exists()) {
	    		fileDir.mkdirs();
	    	}
	
			System.out.println("FoundComponentsFinalMap.size() : "+FoundComponentsFinalMap.size());
			JSONObject jsonFinalResultObject = new JSONObject();
			JSONArray jsonFinalResultArray = new JSONArray();
			
			for (String i : FoundComponentsFinalMap.keySet()) {
			   Map<String, String> s = FoundComponentsFinalMap.get(i) ;
			      //System.out.println("key :" + i + " value: " + s);
			     System.out.println("s.size() : "+s.size());
			     JSONObject componentwiseResultDetails = new JSONObject();
			     componentwiseResultDetails.put("TotalRecords", s.size());
			     JSONObject jsonResultsDetails = new JSONObject();
				 JSONArray jsonResultArray = new JSONArray();
			       for(int j = 0; j < s.size(); j ++) {
				    String k = s.get(arrayColumnName[j]);
			    	System.out.println("First key : " + i + " Second Key : " + j +" value : "+k);
			    	jsonResultsDetails.put(arrayColumnName[j], k);
			    	//jsonResultArray.add(jsonResultsDetails);
			     }
			     jsonFinalResultArray.add(jsonResultsDetails);
			} 
			jsonFinalResultObject.put("TotalRecords",jsonFinalResultArray.size());
			jsonFinalResultObject.put("DownloadedSOQLResults",jsonFinalResultArray);
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
			JsonParser jp = new JsonParser(); 
			JsonElement je = jp.parse(jsonFinalResultObject.toString());
			String prettyJsonString = gson.toJson(je); 
			FileWriter file = new FileWriter(resultFilePath); 
		  	file.write(prettyJsonString); 
		  	file.close(); 
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileDir);
		} catch (IOException e) { 
			//TODO Auto-generated catch block e.printStackTrace(); } 
			e.printStackTrace();
	  }
		return resultFilePath;
  }
	
	public String export(String[] fieldNames, Map<String, HashMap<String, String>> FoundComponentsFinalMap) {
		try {
			resultFilePath = getFileName(JSON_Report_Directory.concat(JSON_FileName_Prefix));
	    	System.out.println("JSON File Path :"+resultFilePath);
	
	    	File fileDir = new File(JSON_Report_Directory);
	    	if(!fileDir.exists()) {
	    		fileDir.mkdirs();
	    	}
	
			System.out.println("FoundComponentsFinalMap.size() : "+FoundComponentsFinalMap.size());
			JSONObject jsonFinalResultObject = new JSONObject();
			JSONArray jsonFinalResultArray = new JSONArray();
			
			for (String i : FoundComponentsFinalMap.keySet()) {
			   Map<String, String> s = FoundComponentsFinalMap.get(i) ;
			      //System.out.println("key :" + i + " value: " + s);
			     System.out.println("s.size() : "+s.size());
			     JSONObject componentwiseResultDetails = new JSONObject();
			     componentwiseResultDetails.put("TotalRecords", s.size());
			     componentwiseResultDetails.put(fieldNames[0], i);
				 JSONArray jsonResultArray = new JSONArray();
			     for(String j : s.keySet()) {
			    	String k = s.get(j);
		    		String[] arrlastModifiedDetails = k.split("#SFDC#");
		    		String lastModifiedDate = arrlastModifiedDetails[1];
		    		String lastModifiedUserId = arrlastModifiedDetails[0];
			    	System.out.println("First key : " + i + " Second Key : " + j +" value : "+k);
			    	JSONObject jsonResultsDetails = new JSONObject();
			    	jsonResultsDetails.put(fieldNames[1], j);
			    	jsonResultsDetails.put(fieldNames[2], lastModifiedUserId);
			    	jsonResultsDetails.put(fieldNames[3], lastModifiedDate);
			    	jsonResultArray.add(jsonResultsDetails);
			     }
			     componentwiseResultDetails.put("records", jsonResultArray);
			     jsonFinalResultArray.add(componentwiseResultDetails);
			} 
			jsonFinalResultObject.put("TotalRecords",jsonFinalResultArray.size());
			jsonFinalResultObject.put("FinalImpactDetails",jsonFinalResultArray);
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
			JsonParser jp = new JsonParser(); 
			JsonElement je = jp.parse(jsonFinalResultObject.toString());
			String prettyJsonString = gson.toJson(je); 
			FileWriter file = new FileWriter(resultFilePath); 
		  	file.write(prettyJsonString); 
		  	file.close(); 
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileDir);
		} catch (IOException e) { 
			//TODO Auto-generated catch block e.printStackTrace(); } 
			e.printStackTrace();
	  }
		return resultFilePath;
  }
	
	public String export(String[] fieldNames, ObservableList<DisplayMultiOrgMissingComponents> masterMultiOrgMissingData) {
		try {
			resultFilePath = getFileName(JSON_Report_Directory.concat(JSON_FileName_Prefix));
	    	System.out.println("JSON File Path :"+resultFilePath);
			System.out.println("masterMultiOrgMissingData.size() : "+masterMultiOrgMissingData.size());
			/*String[] arrayColumnName = {"ComponentType", "SourceOrgId", "SourceOrgComponentNames", "TargetOrgId", "TargetOrgComponentNames", 
				"MissingComponents", "MatchingComponents"};*/
	    	File fileDir = new File(JSON_Report_Directory);
	    	if(!fileDir.exists()) {
	    		fileDir.mkdirs();
	    	}

			JSONObject jsonFinalResultObject = new JSONObject();
			JSONArray jsonFinalResultArray = new JSONArray();
			
			for (int i = 0; i < masterMultiOrgMissingData.size() ; i++) {
				 DisplayMultiOrgMissingComponents dmomc_data = masterMultiOrgMissingData.get(i) ;
			     JSONObject componentwiseResultDetails = new JSONObject();
				 //JSONArray jsonResultArray = new JSONArray();
			     JSONObject jsonResultsDetails = new JSONObject();
			     for(int j = 0; j < fieldNames.length ; j ++) {
			    	//jsonResultsDetails.put(fieldNames[j], dmomc_data.getComponentType());
			    	if(fieldNames[j].equals("ComponentType")) {
			    		jsonResultsDetails.put(fieldNames[j], dmomc_data.getComponentType());
			    	}else if(fieldNames[j].equals("SourceOrgId")) {
			    		jsonResultsDetails.put(fieldNames[j],dmomc_data.getSourceOrgId().toString());
			    	}else if(fieldNames[j].equals("SourceOrgComponentNames")) {
			    		jsonResultsDetails.put(fieldNames[j],dmomc_data.getSourceOrgComponentDetails().toString());
			    	}else if(fieldNames[j].equals("TargetOrgId")) {
			    		jsonResultsDetails.put(fieldNames[j],dmomc_data.getTargetOrgId().toString());
			    	}else if(fieldNames[j].equals("TargetOrgComponentNames")) {
			    		jsonResultsDetails.put(fieldNames[j],dmomc_data.getTargetOrgComponentDetails().toString());
			    	}else if(fieldNames[j].equals("MissingComponents")) {
			    		jsonResultsDetails.put(fieldNames[j],dmomc_data.getMissingComponentDetails().toString());
			    	}else if(fieldNames[j].equals("MatchingComponents")) {
			    		jsonResultsDetails.put(fieldNames[j],dmomc_data.getMatchingComponentDetails().toString());
			    	}
			    	//jsonResultArray.add(jsonResultsDetails);
			     }
			     jsonFinalResultArray.add(jsonResultsDetails);
			} 
			jsonFinalResultObject.put("TotalRecords",jsonFinalResultArray.size());
			jsonFinalResultObject.put("FinalImpactDetails",jsonFinalResultArray);
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
			JsonParser jp = new JsonParser(); 
			JsonElement je = jp.parse(jsonFinalResultObject.toString());
			String prettyJsonString = gson.toJson(je); 
			FileWriter file = new FileWriter(resultFilePath); 
		  	file.write(prettyJsonString); 
		  	file.close(); 
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileDir);
		} catch (IOException e) { 
			//TODO Auto-generated catch block e.printStackTrace(); } 
			e.printStackTrace();
	  }
		return resultFilePath;
  }

	public String export(HashMap<String, HashMap<String, ObservableList<String>>> FoundComponentsFinalMap) {
		try {
			mappingFinalFilePath = getFileName(JSON_Mapping_Directory.concat(JSON_MappingFileName_Prefix));
	    	System.out.println("JSON File Path :"+resultFilePath);
	
	    	File fileDir = new File(JSON_Mapping_Directory);
	    	if(!fileDir.exists()) {
	    		fileDir.mkdirs();
	    	}
	
			System.out.println("FoundComponentsFinalMap.size() : "+FoundComponentsFinalMap.size());
			JSONObject jsonFinalResultObject = new JSONObject();
			JSONArray jsonFinalResultArray = new JSONArray();
			for (String i : FoundComponentsFinalMap.keySet()) {
			   Map<String, ObservableList<String>> s = FoundComponentsFinalMap.get(i) ;
			      //System.out.println("key :" + i + " value: " + s);
			     System.out.println("s.size() : "+s.size());
			     JSONObject TestCaseResultDetails = new JSONObject();
				 JSONArray jsonResultArray = new JSONArray();
			     for(String j : s.keySet()) {
			    	ObservableList<String> k = s.get(j);
			    	System.out.println("First key : " + i + " Second Key : " + j +" value : "+k);
			    	JSONObject jsonResultsDetails = new JSONObject();
			    	jsonResultsDetails.put("ComponentType", j);
			    	jsonResultsDetails.put("ComponentName", k);
			    	jsonResultArray.add(jsonResultsDetails);
			     }
			     TestCaseResultDetails.put("records", jsonResultArray);
			     TestCaseResultDetails.put("TotalRecords", s.size());
			     TestCaseResultDetails.put("TestCaseName", i);
			     jsonFinalResultArray.add(TestCaseResultDetails);
			} 
			jsonFinalResultObject.put("TotalTestCasesMapped",jsonFinalResultArray.size());
			jsonFinalResultObject.put("FinalMappingDetails",jsonFinalResultArray);
	
			Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
			JsonParser jp = new JsonParser(); 
			JsonElement je = jp.parse(jsonFinalResultObject.toString());
			String prettyJsonString = gson.toJson(je); 
			FileWriter file = new FileWriter(mappingFinalFilePath); 
		  	file.write(prettyJsonString); 
		  	file.close(); 
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileDir);
		} catch (IOException e) { 
			//TODO Auto-generated catch block e.printStackTrace(); } 
			e.printStackTrace();
	  }
		return resultFilePath;
  }
	
	public String export(String[] fieldNames, HashMap<String, HashMap<String, ArrayList<String>>> FoundComponentsFinalMap) {
		try {
			resultFilePath = getFileName(JSON_Report_Directory.concat(JSON_FileName_Prefix));
	    	System.out.println("JSON File Path :"+resultFilePath);
	
	    	File fileDir = new File(JSON_Report_Directory);
	    	if(!fileDir.exists()) {
	    		fileDir.mkdirs();
	    	}
	
			System.out.println("FoundComponentsFinalMap.size() : "+FoundComponentsFinalMap.size());
			JSONObject jsonFinalResultObject = new JSONObject();
			JSONArray jsonFinalResultArray = new JSONArray();
			
			for (String i : FoundComponentsFinalMap.keySet()) {
			   Map<String, ArrayList<String>> s = FoundComponentsFinalMap.get(i) ;
			      //System.out.println("key :" + i + " value: " + s);
			     System.out.println("s.size() : "+s.size());
			     JSONObject componentwiseResultDetails = new JSONObject();
			     componentwiseResultDetails.put("TotalRecords", s.size());
			     componentwiseResultDetails.put(fieldNames[0], i);
				 JSONArray jsonResultArray = new JSONArray();
			     for(String j : s.keySet()) {
			    	ArrayList<String> k = s.get(j);
			    	System.out.println("First key : " + i + " Second Key : " + j +" value : "+k);
			    	JSONObject jsonResultsDetails = new JSONObject();
			    	jsonResultsDetails.put(fieldNames[1], j);
			    	jsonResultsDetails.put(fieldNames[2], k);
			    	jsonResultArray.add(jsonResultsDetails);
			     }
			     componentwiseResultDetails.put("records", jsonResultArray);
			     jsonFinalResultArray.add(componentwiseResultDetails);
			} 
			jsonFinalResultObject.put("TotalRecords",jsonFinalResultArray.size());
			jsonFinalResultObject.put("FinalImpactDetails",jsonFinalResultArray);
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
			JsonParser jp = new JsonParser(); 
			JsonElement je = jp.parse(jsonFinalResultObject.toString());
			String prettyJsonString = gson.toJson(je); 
			FileWriter file = new FileWriter(resultFilePath); 
		  	file.write(prettyJsonString); 
		  	file.close(); 
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileDir);
		} catch (IOException e) { 
			//TODO Auto-generated catch block e.printStackTrace(); } 
			e.printStackTrace();
	  }
		return resultFilePath;
  }
	
	public void writeJson( Map<String, HashMap<String, String>> FoundComponentsFinalMap) {
		
	}
	
	public String readJson( Map<String, HashMap<String, String>> FoundComponentsFinalMap) {
		return null;
	}

}

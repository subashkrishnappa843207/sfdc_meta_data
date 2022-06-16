package Test_sfdc_Restapi_Connect;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.restassured.response.Response;

public class MainClass {

	public static void main(String[] args) throws JSONException, IOException {
		// TODO Auto-generated method stub
		RESTAPIBaseContext.generateAccessToken();
		//VERY VERY VERY Important Query. Do not modify
		/***********************************************************************************/
		String BUTTONCHANGES_QUERY = "SELECT Id,Name,LastModifiedDate FROM WebLink";
		String FIELDSCHANGES_QUERY = "SELECT Id,Name,LastModifiedDate FROM Layout";
		String CUSTOMFIELDS_QUERY = "SELECT NameSpacePrefix, DeveloperName, TableEnumOrId, CreatedDate, LastModifiedDate FROM CustomField WHERE TableEnumOrId = 'Account'";
		String Layout_QUERY = "SELECT Name, CreatedDate, LastModifiedDate FROM Layout WHERE TableEnumOrId = 'Account'";
		String WorkFLowRule_QUERY = "SELECT Name, CreatedDate, LastModifiedDate FROM WorkflowRule WHERE TableEnumOrId = 'Account'";
		String ApexTrigger_QUERY = "SELECT Name, CreatedDate, LastModifiedDate FROM ApexTrigger";
		/***********************************************************************************/
	    //Response toolingResponse = RESTAPISFDCContext.getToolingQuery();
	    //System.out.println(toolingResponse.body().asPrettyString());
		//SFDCContext.getSObjectHeaders("SendEmail");
	    /***********************Below code to get LastModified date****************/
		/*
		Response response = RESTAPISFDCContext.getToolingQuery();
	    JSONObject resultsJObject = new JSONObject(response.body().asPrettyString());
		JSONArray jsonRecordsArray = resultsJObject.optJSONArray("sobjects");
		System.out.println(jsonRecordsArray.length());
		FileWriter myWriter = new FileWriter("ApexTrigger.txt");
		for(int i=11;i<jsonRecordsArray.length();i++) {
			JSONObject jsonRecords = new JSONObject(jsonRecordsArray.get(i).toString());
		    try {
				System.out.println("Name:"+jsonRecords.get("name"));
				Response responseLastModifiedInfo = RESTAPISFDCContext.toolingQuery("SELECT Name, CreatedDate, LastModifiedDate FROM "+jsonRecords.get("name"));
			    System.out.println(responseLastModifiedInfo.body().asPrettyString());
			    //JSONObject resultsJObjectLastModifiedInfo = new JSONObject(responseLastModifiedInfo.body().asPrettyString());
		    	myWriter.write(responseLastModifiedInfo.body().asPrettyString());
		    	myWriter.write("\n");
		        //System.out.println("Successfully wrote to the file.");
		      } catch (IOException e) {
		        System.out.println("An error occurred.");
		        e.printStackTrace();
		      }
			}
		myWriter.close();
		*/
		/********************End of code to get LastModified date******************/
	    /***********************Below code to get Describe info per Settings related SObject****************/
		/*
		Response response = RESTAPISFDCContext.getToolingQuery();
	    JSONObject resultsJObject = new JSONObject(response.body().asPrettyString());
		JSONArray jsonRecordsArray = resultsJObject.optJSONArray("sobjects");
		System.out.println(jsonRecordsArray.length());
		FileWriter myWriter = new FileWriter("SOBJECT_Basic_info.txt");
		for(int i=0;i<jsonRecordsArray.length();i++) {
			JSONObject jsonRecords = new JSONObject(jsonRecordsArray.get(i).toString());
		    System.out.println("Name:"+jsonRecords.get("name"));
			Response responseDescribeInfo = RESTAPISFDCContext.getDescribeInfoQuery("/services/data/v49.0/tooling/sobjects/"+jsonRecords.get("name").toString().trim()+"/describe");
	    	//Response responseDescribeInfo = RESTAPISFDCContext.getDescribeInfoQuery("/services/data/v50.0/tooling/sobjects/"+jsonRecords.get("name").toString().trim());
	    	//System.out.println(responseDescribeInfo.body().asPrettyString());
			myWriter.write(responseDescribeInfo.asPrettyString());
			myWriter.write("\n");
			System.out.println("Successfully wrote to the file.");
		}
		myWriter.close();
		*/
		/********************End of code to get Describe info per SObject level******************/
	    /***********************Below code to get LastModified date based on specific query****************/
		
		/*
		FileWriter myWriter = new FileWriter("ApexCodeCoverage.txt");
		//Response responseLastModifiedInfo = RESTAPISFDCContext.toolingQuery("SELECT DeveloperName,LastModifiedDate FROM CustomField WHERE LastModifiedDate >= LAST_N_DAYS:450");
		//Response responseLastModifiedInfo = RESTAPISFDCContext.toolingQuery("SELECT ApexTestClassId , TestMethodName , ApexClassorTriggerId ,NumLinesCovered, NumLinesUncovered , Coverage FROM ApexCodeCoverage ");
		Response responseGetQueryInfo = RESTAPISFDCContext.dataQuery("SELECT BillingPostalCode FROM Account WHERE NumberofLocations__c > 1");
		System.out.println(responseGetQueryInfo.body().asPrettyString());
	    //JSONObject resultsJObjectLastModifiedInfo = new JSONObject(responseLastModifiedInfo.body().asPrettyString());
	    JSONObject resultsJObject = new JSONObject(responseGetQueryInfo.body().asPrettyString());
		JSONArray jsonRecordsArray = resultsJObject.optJSONArray("records");
		int jsonObjectSize = resultsJObject.getInt("totalSize");
		for(int j=0;j<jsonObjectSize;j++) {
			JSONObject jsonFinalRecords = new JSONObject(jsonRecordsArray.get(j).toString());
	    	myWriter.write(jsonFinalRecords.toString());
	    	//myWriter.write(jsonFinalRecords.getString("LastModifiedDate"));
	    	myWriter.write("\n");
	    	myWriter.write("**********************************************************");
	    	myWriter.write("\n");
		}

        //System.out.println("Successfully wrote to the file.");
    	myWriter.close();
		 */
		/********************End of code to get LastModified date  based on specific query******************/		
	    /***********************Below code to get Describe info per Salesforce Object****************/
		/*
		Response response = RESTAPISFDCContext.getObjectQuery();
	    JSONObject resultsJObject = new JSONObject(response.body().asPrettyString());
	    System.out.println(resultsJObject);
		FileWriter myWriter = new FileWriter("SFDCOBJECT_DESCRIBE_INFO.json");
		JSONArray jsonRecordsArray = resultsJObject.optJSONArray("sobjects");
		System.out.println(jsonRecordsArray.length());

		for(int i=0;i<jsonRecordsArray.length();i++) {
			JSONObject jsonRecords = new JSONObject(jsonRecordsArray.get(i).toString());
		    System.out.println("Name:"+jsonRecords.get("name"));
			//Response responseDescribeInfo = RESTAPISFDCContext.getDescribeInfoQuery("/services/data/v49.0/tooling/sobjects/"+jsonRecords.get("name").toString().trim()+"/describe");
	    	Response responseDescribeInfo = RESTAPISFDCContext.getDescribeInfoQuery("/services/data/v50.0/sobjects/"+jsonRecords.get("name").toString().trim()+"/describe");
	    	//System.out.println(responseDescribeInfo.body().asPrettyString());
			myWriter.write(responseDescribeInfo.asPrettyString());
			myWriter.write("\n");
			System.out.println("Successfully wrote to the file.");
		}
		myWriter.close();
*/
		/********************End of code to get Describe info per Salesforce Object******************/		
	    /***********************Below code to get more than 2000 records of any Salesforce Object****************/
		int totalCount = 0;
		String[] fieldNames = {"Id", "Name"};
		HashMap<String, HashMap<String, String>> finalSOQLResultsMap = new HashMap<String, HashMap<String, String>>();
		FileWriter myWriter = new FileWriter("SFDCOBJECT_RECORDS_INFO.json");
		Response responseRecordInfo = RESTAPISFDCContext.dataQuery("SELECT Id, Name From Account");
		JSONObject resultsRecordInfo = new JSONObject(responseRecordInfo.body().asPrettyString());
		JSONArray jsonResultsRecordsArray = resultsRecordInfo.optJSONArray("records");
		System.out.println("jsonResultsRecordsArray Size : "+jsonResultsRecordsArray.length());
		for(int j=0;j<jsonResultsRecordsArray.length();j++) {
			HashMap<String, String> queryDetailsMap = new HashMap<String, String>();
			for(String fieldName : fieldNames) {
    			JSONObject jsonFinalRecords = new JSONObject(jsonResultsRecordsArray.get(j).toString());
    			queryDetailsMap.put(fieldName, jsonFinalRecords.getString(fieldName));
			}
			finalSOQLResultsMap.put(String.valueOf(totalCount), queryDetailsMap);
			totalCount = totalCount + 1;
		}

		//***********************************************************************
		int TotalResultSize = resultsRecordInfo.getInt("totalSize");
		boolean amIonLastPage = resultsRecordInfo.getBoolean("done");
		while(!amIonLastPage){	
			String nextRecordsUrl = resultsRecordInfo.getString("nextRecordsUrl");
			System.out.println("nextRecordsUrl : "+nextRecordsUrl);
			responseRecordInfo = RESTAPISFDCContext.getDataQuery(nextRecordsUrl);
			resultsRecordInfo = new JSONObject(responseRecordInfo.body().asPrettyString());
			jsonResultsRecordsArray = resultsRecordInfo.optJSONArray("records");
			amIonLastPage = resultsRecordInfo.getBoolean("done");
			System.out.println("Move on to next page as amIonLastPage = "+amIonLastPage);
			System.out.println("jsonResultsRecordsArray Size : "+jsonResultsRecordsArray.length());
			for(int j=0;j<jsonResultsRecordsArray.length();j++) {
    			HashMap<String, String> queryDetailsMap = new HashMap<String, String>();
    			for(String fieldName : fieldNames) {
        			JSONObject jsonFinalRecords = new JSONObject(jsonResultsRecordsArray.get(j).toString());
        			queryDetailsMap.put(fieldName, jsonFinalRecords.getString(fieldName));
    			}
    			finalSOQLResultsMap.put(String.valueOf(totalCount), queryDetailsMap);
    			totalCount = totalCount + 1;
    		}
		}
		System.out.println("Total Count On First page: "+TotalResultSize);
		System.out.println("Final Count : "+totalCount);
		System.out.println("finalSOQLResultsMap Size : "+finalSOQLResultsMap.size());
		for (String firstKeySet : finalSOQLResultsMap.keySet()) {
		   Map<String, String> secondMap = finalSOQLResultsMap.get(firstKeySet) ;
		   for(String secondKeySet : secondMap.keySet()) {
		    	String fieldValue = secondMap.get(secondKeySet);
		    	System.out.println("First key : " + firstKeySet + " Second Key : " + secondKeySet +" value : "+fieldValue);
		    	//DisplayImpactedComponentsController.masterData.add(new DisplayImpactedComponents(i, j, k));
		     }
		} 
		myWriter.write(finalSOQLResultsMap.toString());
		myWriter.write("\n");
		//myWriter.write(responseRecordInfo.asPrettyString());
		System.out.println("Successfully wrote to the file.");
		myWriter.close();
		/********************End of code to get more than 2000 records of any Salesforce Object******************/
	}
}

package Test_sfdc_Restapi_Connect;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonUtils {

	@SuppressWarnings("unchecked")
	public synchronized void writeJson(Map<String, List<Map<String, List<String>>>> RESULT_MAP, String path) {
		  JSONObject jsonObjectDetails = new JSONObject();
	      JSONArray objectArray = new JSONArray();
		int i = 0;
		for (Map.Entry<String, List<Map<String, List<String>>>> map : RESULT_MAP.entrySet()) {
			JSONObject recordObjectDetails = new JSONObject(); // Inserting key-valuepairs into the json object
																// recordObjectDetails.put("ID", objId);
			recordObjectDetails.put("ObjectId", "OBJID-" + (i + 1));
			recordObjectDetails.put("ObjectName", map.getKey());
			List<Map<String, List<String>>> resultList = map.getValue();

			int lid = 1;
			JSONArray layoutArray = new JSONArray();
			for (Map<String, List<String>> map1 : resultList) {
				JSONObject recordLayoutDetails = new JSONObject();
				recordLayoutDetails.put("lid", "LID-" + lid);
				recordLayoutDetails.put("Buttons", map1.containsKey("button") ? map1.get("button").toString() : null);
				recordLayoutDetails.put("Fields", map1.containsKey("field") ? map1.get("field").toString() : null);
				recordLayoutDetails.put("RelatedLists",
						map1.containsKey("relatedList") ? map1.get("relatedList").toString() : null);
				layoutArray.add(recordLayoutDetails);
				lid++;
			}

			recordObjectDetails.put("LayoutDetails", layoutArray);
			  objectArray.add(recordObjectDetails);
		}
		  jsonObjectDetails.put("ComparisonDetails",objectArray);
		  Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
		  JsonParser jp = new JsonParser(); 
		  JsonElement je = jp.parse(jsonObjectDetails.toString());
		  String prettyJsonString = gson.toJson(je); 
		  try { FileWriter file = new FileWriter(path); 
		  	file.write(prettyJsonString); 
		  	file.close(); 
		  } catch (IOException e) { 
		//TODO Auto-generated catch block e.printStackTrace(); } 
    System.out.println("JSON file created......");
	}
	}

}

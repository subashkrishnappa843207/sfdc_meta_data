package com.cognizant.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class XMLUtils {
	private static final Logger log = Logger.getLogger(XMLUtils.class);
	public FileInputStream fis = null;
	public String SFDC_Mapping_FilePath = System.getProperty("user.dir")+"\\target\\classes\\com\\cognizant\\sfdc_quality_analyzer_v3\\excel\\SFDCSobjectMappingSheet.xlsx";
	public String XML_Report_Directory = System.getProperty("user.dir")+"\\Report\\XML";
	public String XML_FileName_Prefix = "\\ImpactAnalysisReport_";
	private String resultFilePath = null;
	private String getFileName(String baseName) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String dateTimeInfo = dateFormat.format(new Date());
        baseName = baseName.concat(dateTimeInfo);
        return baseName.concat(String.format(".xml"));
    }
	
	public String export(String[] fieldNames, Map<String, HashMap<String, String>> FoundComponentsFinalMap) {
		try {
			resultFilePath = getFileName(XML_Report_Directory.concat(XML_FileName_Prefix));
	    	System.out.println("XML File Path :"+resultFilePath);
	
	    	File fileDir = new File(XML_Report_Directory);
	    	if(!fileDir.exists()) {
	    		fileDir.mkdirs();
	    	}
	
			System.out.println("FoundComponentsFinalMap.size() : "+FoundComponentsFinalMap.size());
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            // root element
            Element root = document.createElement("SalesforceImpactAnalyzerReport");
            document.appendChild(root);
			for (String i : FoundComponentsFinalMap.keySet()) {
			   Map<String, String> s = FoundComponentsFinalMap.get(i) ;
			      //System.out.println("key :" + i + " value: " + s);
			     System.out.println("s.size() : "+s.size());
				 int hashMapSize = 0;
			     for(String j : s.keySet()) {
			    	String k = s.get(j);
		    		String[] arrlastModifiedDetails = k.split("#SFDC#");
		    		String lastModifiedDate = arrlastModifiedDetails[1];
		    		String lastModifiedUserId = arrlastModifiedDetails[0];
			    	System.out.println("First key : " + i + " Second Key : " + j +" value : "+k);
		            // Component Type element
		            Element componentType = document.createElement(i);
		            root.appendChild(componentType);
		            // set an attribute to staff element
		            Attr attr = document.createAttribute("id");
		            hashMapSize = hashMapSize + 1;
		            attr.setValue(String.valueOf(hashMapSize));
		            componentType.setAttributeNode(attr);
		            // Component Name element
		            Element componentName = document.createElement(fieldNames[1]);
		            componentName.appendChild(document.createTextNode(j));
		            componentType.appendChild(componentName);
		            
		            // last Modified By ID element
		            Element elemLastModifiedByID = document.createElement(fieldNames[2]);
		            elemLastModifiedByID.appendChild(document.createTextNode(lastModifiedUserId));
		            componentType.appendChild(elemLastModifiedByID);
		 
		            // last Modified Date element
		            Element elemLastModifiedDate = document.createElement(fieldNames[3]);
		            elemLastModifiedDate.appendChild(document.createTextNode(lastModifiedDate));
		            componentType.appendChild(elemLastModifiedDate);
			     }
			} 
	
            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(resultFilePath));
 
            // If you use
            // StreamResult result = new StreamResult(System.out);
            // the output will be pushed to the standard output ...
            // You can use that for debugging 
 
            transformer.transform(domSource, streamResult);
            System.out.println("Done creating XML File");
            
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileDir);

		} catch (IOException | ParserConfigurationException | TransformerException e) { 
			//TODO Auto-generated catch block e.printStackTrace(); } 
			e.printStackTrace();
	  }
		return resultFilePath;
  }
	
}

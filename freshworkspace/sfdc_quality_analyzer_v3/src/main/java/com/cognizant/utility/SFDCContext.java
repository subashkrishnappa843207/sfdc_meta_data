package com.cognizant.utility;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.time.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.apache.log4j.Logger;

import com.cognizant.gui.SingleOrgLoginPage;
import com.cognizant.login.OAuthTokenFlow;

import io.restassured.response.Response;

public class SFDCContext {
    private static final Logger log = Logger.getLogger(SFDCContext.class); 
    
	private static final String SERVICE_ENDPOINT = "/services/data";
	private static final String SOBJECTS_ENDPOINT = "/sobjects";
	private static final String TOOLING_ENDPOINT = "/tooling/sobjects";
	private static final String BUTTON_CHANGES = "/tooling/sobjects/WebLink";
	private static String VERSION = null;
	private static final String PAGELAYOUTS_QUERY = "SELECT Name FROM Layout WHERE EntityDefinitionId=";
	private static final String BUTTONCHANGES_QUERY = "SELECT Id FROM WebLink WHERE EntityDefinitionId =";
	private static final String HEALTHCHECKSCORE_QUERY = "SELECT Score FROM SecurityHealthCheck";

	private static final int MYTHREADS = 30;
	private static ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);
	public static List<String> relatedList = new ArrayList<>();
	public static List<String> buttonChangeList = new ArrayList<>();
	public static List<String> fieldChangeList = new ArrayList<>();
	private static List<Map<String, String>> sobjectList = new ArrayList<>();
	
	public static void setVersion(String OrgType) {
		if(OrgType.equalsIgnoreCase("singleorg")) {
			VERSION = "/v"+Config.OAUTH_API_VERSION_SINGLE;
			log.info("API Version set to "+VERSION);
		}else if(OrgType.equalsIgnoreCase("multiorgsource")) {
			VERSION = "/v"+Config.OAUTH_API_VERSION_MULTI_SOURCE;
			log.info("API Version set to "+VERSION);
		}else if(OrgType.equalsIgnoreCase("multiorgtarget")) {
			VERSION = "/v"+Config.OAUTH_API_VERSION_MULTI_TARGET;
			log.info("API Version set to "+VERSION);
		}else {
			log.info("Function Parameter is not valid!! "
					+ "Valid parameter is one of the following : SingleOrg, MultiOrgSource, MultiOrgTarget");
		}
	}

	protected static Response toolingQuery(String query, String obj) {
		query += "'" + obj + "'";
		log.info("Executed Tooling query =  "+query);
		return OAuthTokenFlow.query(SERVICE_ENDPOINT + VERSION + "/tooling/query", query);
	}
	
	public static Response getObjectQuery() {
		return OAuthTokenFlow.get(SERVICE_ENDPOINT + VERSION + SOBJECTS_ENDPOINT);
	}
	
	public static Response getDescribeObject(String ObjectName) {
		return OAuthTokenFlow.get(SERVICE_ENDPOINT + VERSION + SOBJECTS_ENDPOINT +"/"+ ObjectName +"/describe");
	}
	
	public static Response getDescribeToolingObject(String ObjectName) {
		return OAuthTokenFlow.get(SERVICE_ENDPOINT + VERSION + TOOLING_ENDPOINT +"/"+ ObjectName +"/describe");
	}
	
	public static Response runToolingQuery(String query) {
		log.info("Executed Tooling Query ="+query);
		return OAuthTokenFlow.query(SERVICE_ENDPOINT + VERSION + "/tooling/query", query);
	}
	
	public static Response getToolingQuery(String queryParam) {
		log.info("Executed Data Query ="+queryParam);
		return OAuthTokenFlow.get(queryParam);
	}
	
	public static Response runDataQuery(String query) {
		log.info("Executed Data Query ="+query);
		return OAuthTokenFlow.query(SERVICE_ENDPOINT + VERSION + "/query", query);
	}
	
	public static Response getDataQuery(String queryParam) {
		log.info("Executed Data Query ="+queryParam);
		return OAuthTokenFlow.get(queryParam);
	}
	
	public static Response getToolingQuery() {
		return OAuthTokenFlow.get(SERVICE_ENDPOINT + VERSION + TOOLING_ENDPOINT);
	}
	
	/****************For own testing purpose****************/
	public static void getSObjects(){
		List<Map<String, String>> sobjectURL = OAuthTokenFlow.get(SERVICE_ENDPOINT + VERSION + SOBJECTS_ENDPOINT).getBody()
				.jsonPath().getList("sobjects");
		int totalSize = sobjectURL.size();
		//System.out.println("Total sObject Size :"+totalSize);
		for(int i=0;i<totalSize;i++) {
			//System.out.println(sobjectURL.get(i));
			Map<String, String> sObjectProp = sobjectURL.get(i);
			for(int j=0;j<sObjectProp.size();j++) {
				System.out.println(sObjectProp.get("name"));
			}
		}
	}
	
	public static Response getUserDetails(){
		
		String userDetailsEndpoint = null;
		if(Config.OAUTH_ENVIRONMENT_SINGLE.equals("Production") || Config.OAUTH_ENVIRONMENT_MULTI_SOURCE.equals("Production")
				|| Config.OAUTH_ENVIRONMENT_MULTI_TARGET.equals("Production")) {
			userDetailsEndpoint = "https://login.salesforce.com/services/oauth2/userinfo";
		}else if(Config.OAUTH_ENVIRONMENT_SINGLE.equals("Sandbox") || Config.OAUTH_ENVIRONMENT_MULTI_SOURCE.equals("Sandbox")
				|| Config.OAUTH_ENVIRONMENT_MULTI_TARGET.equals("Sandbox")) {
			userDetailsEndpoint = "https://test.salesforce.com/services/oauth2/userinfo";
		}
		log.info(OAuthTokenFlow.get(userDetailsEndpoint));
		Response userInfoResponse = OAuthTokenFlow.get(userDetailsEndpoint);
		//log.info("User Detail Response ="+userInfoResponse.asPrettyString());
		String organization_id = userInfoResponse.jsonPath().getString("organization_id");
		String preferred_username = userInfoResponse.jsonPath().getString("preferred_username");
		log.info("Org Id:"+organization_id);
		log.info("preferred_username:"+preferred_username);
		return userInfoResponse;
	}
	
	public static String getOrgID() {
		String orgDetailsEndpoint = null;
		if(Config.OAUTH_ENVIRONMENT_SINGLE.equals("Production") || Config.OAUTH_ENVIRONMENT_MULTI_SOURCE.equals("Production")
				|| Config.OAUTH_ENVIRONMENT_MULTI_TARGET.equals("Production")) {
			orgDetailsEndpoint = "https://login.salesforce.com/services/oauth2/userinfo";
		}else if(Config.OAUTH_ENVIRONMENT_SINGLE.equals("Sandbox") || Config.OAUTH_ENVIRONMENT_MULTI_SOURCE.equals("Sandbox")
				|| Config.OAUTH_ENVIRONMENT_MULTI_TARGET.equals("Sandbox")) {
			orgDetailsEndpoint = "https://test.salesforce.com/services/oauth2/userinfo";
		}
		Response response = OAuthTokenFlow.get(orgDetailsEndpoint);
		String organization_id = response.jsonPath().getString("organization_id");
		return organization_id;
	}

	public static Response getOrganizationInformation(String OrgID){
		String OrganizationDetailsQuery = "SELECT Id,InstanceName,IsSandbox,Name,OrganizationType,PrimaryContact from Organization where Id = '" + OrgID + "'";
		log.info("Below running query is to find out Org Details");
		log.info(OrganizationDetailsQuery);
		Response OrganizationDetailsQueryResponse = OAuthTokenFlow.query(SERVICE_ENDPOINT + VERSION + "/query", OrganizationDetailsQuery);	
		return OrganizationDetailsQueryResponse;	
	}
		
	public static List<String> getLastModifiedDateSobjects() {
		List<String> sobjectsString = new ArrayList<>();
		List<Map<String, String>> sobjectURL = OAuthTokenFlow.get(SERVICE_ENDPOINT + VERSION + SOBJECTS_ENDPOINT).getBody()
				.jsonPath().getList("sobjects");
		for (int i = 0; i < sobjectURL.size(); i++) {
			sobjectsString.add(getlastModDate(sobjectURL.get(i).get("name")));
		}
		return sobjectsString;
	}

	public static void fetchUpdates(String category) throws ParseException {
		if (sobjectList.size() == 0)
			sobjectList = OAuthTokenFlow.get(SERVICE_ENDPOINT + VERSION + SOBJECTS_ENDPOINT).getBody().jsonPath()
					.getList("sobjects");
		for (int i = 0; i < sobjectList.size(); i++) {
			String url = sobjectList.get(i).get("name");
			Runnable worker = new SFDCUpdates(url, category);
			executor.execute(worker);
		}
	}

	public static void terminateExecutor() {
		executor.shutdown();
		while (!executor.isTerminated()) {

		}
	}

	public static boolean verifyDateIsInRange(LocalDate fromDate, LocalDate toDate, String createdDate,
		String lastModifiedDate) throws ParseException {
		
		DateTimeFormatter format1 = DateTimeFormatter.ofPattern("YYYY-MM-DD");
		DateTimeFormatter format3 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		
		LocalDateTime frmdate = fromDate.atStartOfDay();
		LocalDateTime tdate = toDate.atStartOfDay();
		
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").format(frmdate);
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").format(tdate);
	
		LocalDateTime createdate = LocalDateTime.parse(createdDate, format3);
		LocalDateTime lastmoddate = LocalDateTime.parse(lastModifiedDate, format3);
		
		return ((createdate.isAfter(frmdate) && lastmoddate.isBefore(tdate)) || (lastmoddate.isBefore(tdate) && lastmoddate.isAfter(frmdate)));
	}
	
	public static boolean verifyDateIsInRange(LocalDate fromDate, String createdDate,
			String lastModifiedDate) throws ParseException {
		
		DateTimeFormatter format1 = DateTimeFormatter.ofPattern("YYYY-MM-DD");
		DateTimeFormatter format3 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		
		LocalDateTime frmdate = fromDate.atStartOfDay();
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").format(frmdate);

		LocalDate createdate = LocalDate.parse(createdDate, format3);
		LocalDate lastmoddate = LocalDate.parse(lastModifiedDate, format3);
		
		return true;
	}
	
	public static boolean verifyDateIsInRangeWithoutCreatedDate(LocalDate fromDate, LocalDate toDate,
			String lastModifiedDate) throws ParseException {
		
		DateTimeFormatter format1 = DateTimeFormatter.ofPattern("YYYY-MM-DD");
		DateTimeFormatter format3 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		
		LocalDateTime frmdate = fromDate.atStartOfDay();
		LocalDateTime tdate = toDate.atStartOfDay();
		
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").format(frmdate);
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").format(tdate);
		
		LocalDateTime lastmoddate = LocalDateTime.parse(lastModifiedDate, format3);
		
		return (lastmoddate.isBefore(tdate) && lastmoddate.isAfter(frmdate));
	}
	
	public static boolean verifyDateIsInRangeWithoutCreatedDate(LocalDate fromDate,
			String lastModifiedDate) throws ParseException {
		
		DateTimeFormatter format1 = DateTimeFormatter.ofPattern("YYYY-MM-DD");
		DateTimeFormatter format3 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		
		LocalDateTime frmdate = fromDate.atStartOfDay();
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").format(frmdate);
		
		LocalDateTime lastmoddate = LocalDateTime.parse(lastModifiedDate, format3);
		
		return (lastmoddate.isAfter(frmdate));
	}

	public static List<String> getPageLayouts() {
		List<String> sobjects = getLastModifiedDateSobjects();
		List<String> sobjectLayouts = new ArrayList<String>();
		for (int i = 0; i < sobjects.size(); i++) {
			List<Map<String, String>> sobjectLayout = toolingQuery(PAGELAYOUTS_QUERY, sobjects.get(i)).body().jsonPath()
					.getList("records");

			sobjectLayouts.add(sobjectLayout.get(i).get("Name"));
		}
		return sobjectLayouts;

	}

	protected static String getlastModDate(String obj) {
		return OAuthTokenFlow.get(SERVICE_ENDPOINT + VERSION + SOBJECTS_ENDPOINT + "/" + obj + "/describe")
				.getHeader("Last-Modified").toString();
	}

	protected static Response objectDescribe(String obj) {
		return OAuthTokenFlow.get(SERVICE_ENDPOINT + VERSION + SOBJECTS_ENDPOINT + "/" + obj + "/describe");
	}

	protected static boolean verifyLastModifiedDate(String fromDate, String toDate, String lastModDate) {
		DateTimeFormatter format1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm:ss z");
		LocalDate d3 = LocalDate.parse(lastModDate, formatter);
		ZonedDateTime d1 = LocalDate.parse(fromDate, format1).atStartOfDay(ZoneId.of("GMT"));
		ZonedDateTime d2 = Instant.now().atZone(ZoneId.of("GMT"));
		return (((d3.isBefore(d1.toLocalDate()) || d3.equals(d1.toLocalDate())))
				&& (d3.isBefore(d2.toLocalDate()) || d3.equals(d1.toLocalDate())));
	}

	protected static void generateJsonFiles(String str, String name) throws IOException {
		final String currentDir = System.getProperty("user.dir") + "//Analyzer//Results//Json_reports//";
		FileWriter outputFile = new FileWriter(currentDir + name + ".json");
		outputFile.write(str);
		outputFile.flush();
		outputFile.close();
	}

	public static class SFDCUpdates implements Runnable {
		private final String objectName;
		private final String category;

		public SFDCUpdates(String objectName, String category) {
			this.objectName = objectName;
			this.category = category;
		}

		@Override
		public void run() {
			switch (category) {
			case "button":
				buttonContext();
				break;
			case "field":
				fieldContext();
				break;
			case "relatedList":
				relatedListContext();
				break;
			default:
			}
		}

		private void buttonContext() {
			List<Map<String, String>> sobjectLayout = toolingQuery(BUTTONCHANGES_QUERY, objectName).body().jsonPath()
					.getList("records");
			for (int j = 0; j < sobjectLayout.size(); j++) {
				String pageId = sobjectLayout.get(j).get("Id");
				buttonChangeList.add(
						OAuthTokenFlow.get(SERVICE_ENDPOINT + VERSION + BUTTON_CHANGES + "/" + pageId).body().asString());
			}
		}

		private void fieldContext() {
			Response response = objectDescribe(objectName);
			String sobjectDescribe = response.body().asString();
			try {
				new JsonComparator().fieldAnalysis(sobjectDescribe, objectName);
			} catch (IOException | IllegalAccessException | InstantiationException e) {
				e.printStackTrace();
			}
		}

		private void relatedListContext() {
			String sobjectDescribe = OAuthTokenFlow
					.get(SERVICE_ENDPOINT + VERSION + SOBJECTS_ENDPOINT + "/" + objectName + "/describe/layouts").body()
					.asString();
			try {
				new JsonComparator().relatedListAnalysis(objectName, sobjectDescribe);
			} catch (IOException | IllegalAccessException | InstantiationException e) {
				e.printStackTrace();
			}
		}
	}
}

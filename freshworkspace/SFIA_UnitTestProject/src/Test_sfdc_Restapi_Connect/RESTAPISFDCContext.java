package Test_sfdc_Restapi_Connect;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.restassured.response.Response;

public class RESTAPISFDCContext {
	private static final String SERVICE_ENDPOINT = "/services/data";
	private static final String SOBJECTS_ENDPOINT = "/sobjects";
	private static final String METADATA_ENDPOINT = "/metadata";
	private static final String TOOLING_ENDPOINT = "/tooling/sobjects";
	private static final String BUTTON_CHANGES = "/tooling/sobjects/WebLink";
	private static final String VERSION = "/v49.0";
	private static final String PAGELAYOUTS_QUERY = "SELECT Name FROM Layout WHERE EntityDefinitionId=";
	private static final String BUTTONCHANGES_QUERY = "SELECT Id FROM WebLink WHERE EntityDefinitionId =";

	private static final int MYTHREADS = 30;
	private static ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);
	public static List<String> relatedList = new ArrayList<>();
	public static List<String> buttonChangeList = new ArrayList<>();
	public static List<String> fieldChangeList = new ArrayList<>();
	private static List<Map<String, String>> sobjectList = new ArrayList<>();

	protected static Response getMetadataQuery() {
		return RESTAPIBaseContext.get(SERVICE_ENDPOINT + VERSION + METADATA_ENDPOINT);
	}
	
	protected static Response getToolingQuery() {
		return RESTAPIBaseContext.get(SERVICE_ENDPOINT + VERSION + TOOLING_ENDPOINT);
	}
	
	protected static Response getObjectQuery() {
		return RESTAPIBaseContext.get(SERVICE_ENDPOINT + VERSION + SOBJECTS_ENDPOINT);
	}
	
	protected static Response toolingQuery(String query, String obj) {
		query = query+"'" + obj + "'";
		return RESTAPIBaseContext.query(SERVICE_ENDPOINT + VERSION + "/tooling/query", query);
	}
	
	protected static Response toolingQuery(String query) {
		//query = query+"'" + obj + "'";
		return RESTAPIBaseContext.query(SERVICE_ENDPOINT + VERSION + "/tooling/query", query);
	}
	protected static Response dataQuery(String query) {
		//query = query+"'" + obj + "'";
		return RESTAPIBaseContext.query(SERVICE_ENDPOINT + VERSION + "/query", query);
	}
	protected static Response getDataQuery(String queryParam) {
		//query = query+"'" + obj + "'";
		return RESTAPIBaseContext.get(queryParam);
	}
	
	//SERVICE_ENDPOINT + VERSION + SOBJECTS_ENDPOINT + "/" + objectName + "/describe/layouts"
	private static String accountLayout = "/sobjects/Account/describe/layouts";
	protected static Response toolingSObjectQuery(String Id) {
		//return BaseContext.get(SERVICE_ENDPOINT + VERSION + SOBJECTS_ENDPOINT);
		return RESTAPIBaseContext.get(SERVICE_ENDPOINT + VERSION + accountLayout+Id);
	}
	protected static Response getDescribeInfoQuery(String strURL) {
		return RESTAPIBaseContext.get(strURL);
	}
	/*
	protected static Response getDescribeInfoQuery(String objectName) {
		return RESTAPIBaseContext.get(SERVICE_ENDPOINT + VERSION + SOBJECTS_ENDPOINT + "/" + objectName+"/describe");
	}*/
//Currently running
	protected static Response getRestInfoQuery() {
		return RESTAPIBaseContext.get(SERVICE_ENDPOINT + VERSION + BUTTON_CHANGES);
	}
	
	protected static Response getAnalyticsInfoQuery() {
		return RESTAPIBaseContext.get(SERVICE_ENDPOINT + VERSION +"/analytics");
	}
	
	protected static Response getAPIInfoQuery() {
		return RESTAPIBaseContext.get(SERVICE_ENDPOINT + VERSION +"/actions/standard/chatterPost");
	}
	
	public static List<String> getLastModifiedDateSobjects() {
		List<String> sobjectsString = new ArrayList<>();
		List<Map<String, String>> sobjectURL = RESTAPIBaseContext.get(SERVICE_ENDPOINT + VERSION + SOBJECTS_ENDPOINT).getBody()
				.jsonPath().getList("sobjects");
		for (int i = 0; i < sobjectURL.size(); i++) {
			sobjectsString.add(getlastModDate(sobjectURL.get(i).get("name")));
		}
		return sobjectsString;
	}

	public static void fetchUpdates(String category) throws ParseException {
		if (sobjectList.size() == 0)
			sobjectList = RESTAPIBaseContext.get(SERVICE_ENDPOINT + VERSION + SOBJECTS_ENDPOINT).getBody().jsonPath()
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

	protected static boolean verifyDateIsInRange(String fromDate, String toDate, String createdDate,
			String lastModifiedDate) throws ParseException {
		DateTimeFormatter format1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter format3 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		ZonedDateTime d1 = LocalDate.parse(fromDate, format1).atStartOfDay(ZoneId.of("GMT"));
		ZonedDateTime d2 = LocalDate.parse(toDate, format1).atStartOfDay(ZoneId.of("GMT"));
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(d1);
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(d2);

		LocalDate s1 = LocalDate.parse(createdDate, format3);
		LocalDate s2 = LocalDate.parse(lastModifiedDate, format3);
		return ((s1.isAfter(d1.toLocalDate()) || (s2.isBefore(d2.toLocalDate()) && s2.isAfter(d1.toLocalDate()))));
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

	protected static void getSObjectHeaders(String obj) {
		//System.out.println(BaseContext.get(SERVICE_ENDPOINT + VERSION + SOBJECTS_ENDPOINT + "/" + obj + "/describe").getHeaders());
		System.out.println(RESTAPIBaseContext.get(SERVICE_ENDPOINT + VERSION + "/sobjects/CustomField").asPrettyString());

	    try {
			FileWriter myWriter = new FileWriter("filename.txt");
	    	myWriter.write(RESTAPIBaseContext.get(SERVICE_ENDPOINT + VERSION + "/sobjects/Account/describe/recordstypes").asPrettyString());
	        System.out.println("Successfully wrote to the file.");
	    	myWriter.close();
	      } catch (IOException e) {
	        System.out.println("An error occurred.");
	        e.printStackTrace();
	      }

	}
	protected static String getlastModDate(String obj) {
		return RESTAPIBaseContext.get(SERVICE_ENDPOINT + VERSION + SOBJECTS_ENDPOINT + "/" + obj + "/describe")
				.getHeader("Last-Modified").toString();
	}

	protected static Response objectDescribe(String obj) {
		return RESTAPIBaseContext.get(SERVICE_ENDPOINT + VERSION + SOBJECTS_ENDPOINT + "/" + obj + "/describe");
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

	public static void buttonContextDummy(String objectName) {
		List<Map<String, String>> sobjectLayout = toolingQuery(BUTTONCHANGES_QUERY, objectName).body().jsonPath()
				.getList("records");
		for (int j = 0; j < sobjectLayout.size(); j++) {
			String pageId = sobjectLayout.get(j).get("Id");
			buttonChangeList.add(
					RESTAPIBaseContext.get(SERVICE_ENDPOINT + VERSION + BUTTON_CHANGES + "/" + pageId).body().asString());
		}
		for(int i=0;i<buttonChangeList.size();i++) {
			System.out.println(buttonChangeList.get(i));
		}
	}
	
	public static class SFDCUpdates implements Runnable {
		private static String objectName;
		private final String category;

		public SFDCUpdates(String objectName, String category) {
			SFDCUpdates.objectName = objectName;
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
		


		public static void buttonContext() {
			List<Map<String, String>> sobjectLayout = toolingQuery(BUTTONCHANGES_QUERY, objectName).body().jsonPath()
					.getList("records");
			for (int j = 0; j < sobjectLayout.size(); j++) {
				String pageId = sobjectLayout.get(j).get("Id");
				buttonChangeList.add(
						RESTAPIBaseContext.get(SERVICE_ENDPOINT + VERSION + BUTTON_CHANGES + "/" + pageId).body().asString());
			}
			for(int i=0;i<buttonChangeList.size();i++) {
				System.out.println(buttonChangeList.get(i));
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
			String sobjectDescribe = RESTAPIBaseContext
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

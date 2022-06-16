package Test_sfdc_Restapi_Connect;

import static io.restassured.RestAssured.given;

import org.json.JSONException;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RESTAPIBaseContext {
	private static final String TOKEN_ENDPOINT = "/services/oauth2/token";
	private static final String Access_Token_Prop = "access_token";
	private static final String Instance_Url_Prop = "instance_url";
	private static final String CLIENT_ID = "3MVG9oNqAtcJCF.Ge6OPXBlhYAK2LLcKKgmXtNBgyOK_lb_3yv8oYt.l3qPGwexjZ..pGQMLhe6ehBX7C79Yt";
	private static final String CLIENT_SECRET = "F75FCD243BC07526FDFD91D8E679E4982BBDE61F51452CFC7EAB98DABEA1F962";
	private static final String USERNAME = "gopal_poria@salesforce-practice.com";
	private static final String PASSWORD = "Covid@2021";
	private static final String LOGIN_URL = "https://login.salesforce.com/";
	private static RequestSpecification requestSpec;

	public static void generateAccessToken() throws JSONException {
		
		Response response = given().urlEncodingEnabled(false).queryParam("grant_type", "password")
				.queryParam("client_id", CLIENT_ID)
				.queryParam("client_secret", CLIENT_SECRET)
				.queryParam("username", USERNAME).queryParam("password", PASSWORD).when().post(LOGIN_URL + TOKEN_ENDPOINT)
				.then().assertThat().statusCode(200).extract().response();
		/*
		Response response = given().urlEncodingEnabled(false).queryParam("grant_type", "token")
				.queryParam("client_id", "3MVG9oNqAtcJCF.Ge6OPXBlhYAK2LLcKKgmXtNBgyOK_lb_3yv8oYt.l3qPGwexjZ..pGQMLhe6ehBX7C79Yt")
				.queryParam("redirect_uri","https://localhost:7700/oauthcallback.html")
				//.queryParam("client_secret", "F75FCD243BC07526FDFD91D8E679E4982BBDE61F51452CFC7EAB98DABEA1F962")
				//.queryParam("username", "gopal_poria@salesforce-practice.com")
				//.queryParam("password", "Covid@2020")
				.queryParam("display", "popup")
				//.when().post("https://login.salesforce.com/services/oauth2/authorize" + TOKEN_ENDPOINT)
				.when().get()
				.then().assertThat().statusCode(200).extract().response();
		*/
		System.out.println("Login Response ="+response.asPrettyString());
		String accessToken = response.jsonPath().getString(Access_Token_Prop);
		String instanceUrl = response.jsonPath().getString(Instance_Url_Prop);
		System.out.println("Access Token ="+accessToken);
		System.out.println("instance URL ="+instanceUrl);
		RequestSpecBuilder builder = new RequestSpecBuilder();
		builder.setBaseUri(instanceUrl);
		builder.setContentType("application/json");
		requestSpec = builder.build().auth().preemptive().oauth2(accessToken);
	}
	
	public static Response get(String url) {
		return given().spec(requestSpec).get(url)
				.then().statusCode(200).extract().response();
	}
	
	public static Response query(String url, String query) {
		return given().spec(requestSpec).param("q", query).get(url)
				.then()
				.statusCode(200).extract().response();
	}
}

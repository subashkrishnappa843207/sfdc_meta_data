package com.cognizant.login;


import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cognizant.gui.SingleOrgLoginPage;
import com.cognizant.utility.Config;

import static io.restassured.RestAssured.given;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * the oauth token flow. this is normally used for client to server where the client is not a secured environment
 * as it does not involve a secret. We use this as our standard login for SF oauth. The disadvantage to this flow is
 * it prompts for authentication and authorization everytime.
 */
public class OAuthTokenFlow extends OAuthFlow {
	
	private static RequestSpecification requestSpec;
	private static final Logger log = Logger.getLogger(OAuthTokenFlow.class); 
	
    public OAuthTokenFlow(Config config) {
        super();
    }


    public String getStartUrl(String environmentType, String loginType) throws UnsupportedEncodingException {
        return getStartUrlImpl(environmentType , loginType);
    }

    public static String getStartUrlImpl(String environmentType , String loginType) throws UnsupportedEncodingException {    	
		// TODO Auto-generated method stub
		String SERVER_URL = null, REDIRECT_URI = null , CLIENT_ID = null;
		if(loginType.equalsIgnoreCase("singleorg")) {
			if(environmentType.equalsIgnoreCase("production")) {
				SERVER_URL = Config.OAUTH_PROD_AUTHORIZE_URL_VAL;
				REDIRECT_URI = Config.OAUTH_PROD_REDIRECTURI_VAL;
				CLIENT_ID = Config.OAUTH_SINGLE_CLIENTID;
			}else if(environmentType.equalsIgnoreCase("sandbox")) {
				SERVER_URL = Config.OAUTH_SB_AUTHORIZE_URL_VAL;
				REDIRECT_URI = Config.OAUTH_SB_REDIRECTURI_VAL;
				CLIENT_ID = Config.OAUTH_SINGLE_CLIENTID;
			} else {
				log.info("Function Parameter is not valid!! "
						+ "Valid parameter is one of the following : Production or Sandbox");
			}
		} else if(loginType.equalsIgnoreCase("multiorg")){
			if(environmentType.equalsIgnoreCase("production")) {
				SERVER_URL = Config.OAUTH_PROD_AUTHORIZE_URL_VAL;
				REDIRECT_URI = Config.OAUTH_PROD_REDIRECTURI_VAL;
				CLIENT_ID = Config.OAUTH_MULTI_CLIENTID;
			}else if(environmentType.equalsIgnoreCase("sandbox")) {
				SERVER_URL = Config.OAUTH_SB_AUTHORIZE_URL_VAL;
				REDIRECT_URI = Config.OAUTH_SB_REDIRECTURI_VAL;
				CLIENT_ID = Config.OAUTH_MULTI_CLIENTID;
			} else {
				log.info("Function Parameter is not valid!! "
						+ "Valid parameter is one of the following : Production or Sandbox");
			}
		}else {
			log.info("Function Parameter is not valid!! "
					+ "Valid parameter is one of the following : SingleOrg or MultiOrg");
		}
		
	    String loginUrl = SERVER_URL + "?client_id=" + CLIENT_ID +
	            "&redirect_uri="+ REDIRECT_URI +
	            "&response_type=token";
	    
		return loginUrl;
    }

    //public static boolean handleCompletedUrl(String url, Config config) throws URISyntaxException {
    public static boolean handleCompletedUrl(String url, String loginType) throws URISyntaxException {
        Map<String, String> params = getSuccessURLParameters(url);
        if (params.containsKey("access_token")){    	
            //we don't use most of this but I still like to track what we should get
            OAuthToken token = new OAuthToken();
            token.setInstanceUrl(params.get("instance_url"));
            token.setId(params.get("id"));
            token.setAccessToken(params.get("access_token"));

            //optional parameters
            if (params.containsKey("refresh_token")) {
                token.setRefreshToken(params.get("refresh_token"));
            }

            //currently unused parameters
            if (params.containsKey("scope")) {
                token.setScope(params.get("scope"));
            }
            if (params.containsKey("signature")) {
                token.setSignature(params.get("signature"));
            }
            if (params.containsKey("token_type")) {
                token.setTokenType(params.get("token_type"));
            }
            if (params.containsKey("issued_at")) {
                String issued_at = params.get("issued_at");
                if (issued_at != null && !issued_at.equals("")) {
                    token.setIssuedAt(Long.valueOf(issued_at));
                }
            }

            if(loginType.equalsIgnoreCase("singleorg")) {
            	Config.OAUTH_ACCESSTOKEN_SINGLE = token.getAccessToken();
            	Config.OAUTH_INSTANCEURL_SINGLE = token.getInstanceUrl();
            	Config.OAUTH_REFRESHTOKEN_SINGLE = token.getRefreshToken();
            }else if(loginType.equalsIgnoreCase("multiorgsource")) {
            	Config.OAUTH_ACCESSTOKEN_MULTI_SOURCE = token.getAccessToken();
            	Config.OAUTH_INSTANCEURL_MULTI_SOURCE = token.getInstanceUrl();
            	Config.OAUTH_REFRESHTOKEN_MULTI_SOURCE = token.getRefreshToken();
            }else if(loginType.equalsIgnoreCase("multiorgtarget")) {
            	Config.OAUTH_ACCESSTOKEN_MULTI_TARGET = token.getAccessToken();
            	Config.OAUTH_INSTANCEURL_MULTI_TARGET = token.getInstanceUrl();
            	Config.OAUTH_REFRESHTOKEN_MULTI_TARGET = token.getRefreshToken();
            }else {
            	log.info("Function Parameter is not valid!! "
    					+ "Valid Environment is one of the following : SinglOrg , MultiOrgSource or MultiOrgTarget");
            }
            
            //********************************************************************************
            return true;
        }

        return false;
    }

	public static RequestSpecification generateRequestSpecification(String environment) {

		String instanceUrl = null;
		String accessToken = null;
		if(environment.equalsIgnoreCase("singleorg")) {
			instanceUrl = Config.OAUTH_INSTANCEURL_SINGLE;
			accessToken = Config.OAUTH_ACCESSTOKEN_SINGLE;
		}else if(environment.equalsIgnoreCase("multiorgsource")) {
			instanceUrl = Config.OAUTH_INSTANCEURL_MULTI_SOURCE;
			accessToken = Config.OAUTH_ACCESSTOKEN_MULTI_SOURCE;
		}else if(environment.equalsIgnoreCase("multiorgtarget")) {
			instanceUrl = Config.OAUTH_INSTANCEURL_MULTI_TARGET;
			accessToken = Config.OAUTH_ACCESSTOKEN_MULTI_TARGET;
		}else {
			log.info("Function Parameter is not valid!! "
					+ "Valid parameter is one of the following : SingleOrg, MultiOrgSource, MultiOrgTarget");
		}
		RequestSpecBuilder builder = new RequestSpecBuilder();
		builder.setBaseUri(instanceUrl);
		builder.setContentType("application/json");
		requestSpec = builder.build().auth().preemptive().oauth2(accessToken);

		return requestSpec;
	}
	
	public static Response get(String url) {
		//return given().spec(requestSpec).get(url).then().statusCode(200).extract().response();
		return given().spec(requestSpec).get(url).then().extract().response();
	}
	
	public static Response query(String url, String query) {
		//return given().spec(requestSpec).param("q", query).get(url).then().statusCode(200).extract().response();
		return given().spec(requestSpec).param("q", query).get(url).then().extract().response();
	}
	
	public static int getStatusCode(String url) {
		//return given().spec(requestSpec).get(url).then().statusCode(200).extract().response();
		return given().spec(requestSpec).get(url).then().extract().statusCode();
	}
	
	public static int queryStatusCode(String url, String query) {
		//return given().spec(requestSpec).param("q", query).get(url).then().statusCode(200).extract().response();
		return given().spec(requestSpec).param("q", query).get(url).then().extract().statusCode();
	}
}


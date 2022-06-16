
package com.cognizant.login;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import com.cognizant.utility.Config;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuthFlow is basic instrumentation of delegating authentication to an external web browser using OAuth2
 * Currently derived classes include authorization_code flow and token flow. authorization_flow would be setup
 * by individuals using connected apps as it requires storing a secret which cannot be done for the normal login
 */
public abstract class OAuthFlow {
    //protected static Logger logger = LogManager.getLogger(OAuthFlow.class);
    //protected final Config config;
	private static final Logger log = Logger.getLogger(OAuthFlow.class);
    private String reasonPhrase;
    private int statusCode;
    boolean loginSuccess = false;
    /*public OAuthFlow(Shell parent, Config config) {
        super(parent);
        this.config = config;
    }*/

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public int getStatusCode() {
        return statusCode;
    }

    //public abstract String getStartUrl(Config config) throws UnsupportedEncodingException;
	/*****************************************************************************/
	    public static Map<String, String> getQueryParameters(String url) throws URISyntaxException {
	        url = url.replace("#","?");
	        Map<String, String> params = new HashMap<>();
	        new URIBuilder(url).getQueryParams().stream().forEach(kvp -> params.put(kvp.getName(), kvp.getValue()));
	        return params;
	    }
	    
		/*****************************************************************************/
	    public static Map<String, String> getSuccessURLParameters(String url) {
	        //url = url.replace("#","?");
	        Map<String, String> params = new HashMap<>();
	        String[] splitTempURL = url.split("#");
	        //System.out.println("After First Split:"+splitTempURL.toString());
	        String[] splitTempURL2 = splitTempURL[1].split("&");
	        for(String splitTempURL3 : splitTempURL2) {
	        	String[] strFinal = splitTempURL3.split("=");
	        	params.put(strFinal[0], strFinal[1]);
        	}
	        //System.out.println("Final Map key value pair:"+params);
	        //new URIBuilder(url).getQueryParams().stream().forEach(kvp -> params.put(kvp.getName(), kvp.getValue()));
	        return params;
	    }
	/**************************************************************************/
	public String getStartUrl(String environmentType) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return null;
	}

}

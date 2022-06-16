package com.cognizant.login;

import org.apache.log4j.Logger;

import com.cognizant.gui.SingleOrgAnalysisSummaryPageWholeOrgSingleDate;

/**
 * The common class for listening to browser events use in oauth flows.
 */
public abstract class OAuthBrowserListener{
    //protected final Config config;
	private static final Logger log = Logger.getLogger(OAuthBrowserListener.class);
    private String reasonPhrase;
    private int statusCode;
    private boolean result;


    public abstract void changed();

    public abstract void succeeded();

    public String getReasonPhrase() {
        return reasonPhrase;
    }
    

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
    
    /*
    public void doErrorHandling(String url, Exception e, Logger logger){
        logger.error("Failed to retrieve oauth token. Bad Url result:" + url, e);
    }
     */
}

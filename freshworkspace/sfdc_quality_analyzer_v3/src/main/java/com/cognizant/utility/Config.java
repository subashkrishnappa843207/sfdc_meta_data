package com.cognizant.utility;


import com.cognizant.encryption.EncryptionAesUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.TimeZone;

/**
 * @author Debdatta Porya
 * @ModifiedBy Debdatta Porya
 * @since 2021
 */
public class Config {
    //private static Logger logger = LogManager.getLogger(Config.class);
    //*************************************************************************************************************************************
    //******************************************************SALESFORCE SETTINGS***********************************************************
    //*************************************************************************************************************************************
    public static final String DEFAULT_ENDPOINT_URL = "https://login.salesforce.com";
    public static final String OAUTH_PROD_ENVIRONMENT_VAL = "Production";
    public static final String OAUTH_SB_ENVIRONMENT_VAL = "Sandbox";

    public static final String OAUTH_PROD_SERVER_VAL = "https://login.salesforce.com/";
    public static final String OAUTH_SB_SERVER_VAL = "https://test.salesforce.com/";
    public static String OAUTH_CUSTOM_LOGIN_URL = DEFAULT_ENDPOINT_URL;
    
    public static final String OAUTH_PROD_AUTHORIZE_URL_VAL = "https://login.salesforce.com/services/oauth2/authorize";
    public static final String OAUTH_SB_AUTHORIZE_URL_VAL = "https://test.salesforce.com/services/oauth2/authorize";

    public static final String OAUTH_PROD_REDIRECTURI_VAL = "https://login.salesforce.com/services/oauth2/success";
    public static final String OAUTH_SB_REDIRECTURI_VAL = "https://test.salesforce.com/services/oauth2/success";

    // salesforce client connectivity for Single Org
    public static final String USERNAME = "sfdc.username"; //$NON-NLS-1$
    public static final String PASSWORD = "sfdc.password"; //$NON-NLS-1$
    public static final String ENDPOINT = "sfdc.endpoint"; //$NON-NLS-1$
    
    public static final String OAUTH_PREFIX = "sfdc.oauth.";
    public static final String API_PREFIX = "sfdc.apiversion.";
    public static final String OAUTH_PARTIAL_BULK = "bulk";
    public static final String OAUTH_PARTIAL_PARTNER = "partner";
    public static final String OAUTH_PARTIAL_SERVER = "server";
    public static final String OAUTH_PARTIAL_CLIENTSECRET = "clientsecret";
    public static final String OAUTH_PARTIAL_CLIENTID = "clientid";
    public static final String OAUTH_PARTIAL_REDIRECTURI = "redirecturi";
    public static final String OAUTH_PARTIAL_BULK_CLIENTID = OAUTH_PARTIAL_BULK + "." + OAUTH_PARTIAL_CLIENTID;
    public static final String OAUTH_PARTIAL_PARTNER_CLIENTID = OAUTH_PARTIAL_PARTNER + "." + OAUTH_PARTIAL_CLIENTID;

    public static String OAUTH_ENVIRONMENT_SINGLE = OAUTH_PREFIX + "environment.single";
    public static String OAUTH_ENVIRONMENT_MULTI_SOURCE = OAUTH_PREFIX + "environment.multi.source";
    public static String OAUTH_ENVIRONMENT_MULTI_TARGET = OAUTH_PREFIX + "environment.multi.target";
    public static String OAUTH_API_VERSION_SINGLE = OAUTH_PREFIX + "api.single";
    public static String OAUTH_API_VERSION_MULTI_SOURCE = OAUTH_PREFIX + "api.multi.source";
    public static String OAUTH_API_VERSION_MULTI_TARGET = OAUTH_PREFIX + "api.multi.target";
    
    public static String OAUTH_ACCESSTOKEN_SINGLE = OAUTH_PREFIX + "single.accesstoken";
    public static String OAUTH_REFRESHTOKEN_SINGLE = OAUTH_PREFIX + "single.refreshtoken";
    public static String OAUTH_INSTANCEURL_SINGLE = OAUTH_PREFIX + "single.instanceurl";
    public static String OAUTH_ACCESSTOKEN_MULTI_SOURCE = OAUTH_PREFIX + "multi.source.accesstoken";
    public static String OAUTH_REFRESHTOKEN_MULTI_SOURCE = OAUTH_PREFIX + "multi.source.refreshtoken";
    public static String OAUTH_INSTANCEURL_MULTI_SOURCE = OAUTH_PREFIX + "multi.source.instanceurl";
    public static String OAUTH_ACCESSTOKEN_MULTI_TARGET = OAUTH_PREFIX + "multi.target.accesstoken";
    public static String OAUTH_REFRESHTOKEN_MULTI_TARGET = OAUTH_PREFIX + "multi.target.refreshtoken";
    public static String OAUTH_INSTANCEURL_MULTI_TARGET = OAUTH_PREFIX + "multi.target.instanceurl";
    
    public static final String OAUTH_SERVER = OAUTH_PREFIX + OAUTH_PARTIAL_SERVER;
    public static String OAUTH_SINGLE_CLIENTSECRET = OAUTH_PREFIX + OAUTH_PARTIAL_CLIENTSECRET;
    public static String OAUTH_SINGLE_CLIENTID = OAUTH_PREFIX + OAUTH_PARTIAL_CLIENTID;
    public static String OAUTH_SINGLE_REDIRECTURI = OAUTH_PREFIX + OAUTH_PARTIAL_REDIRECTURI;
    public static String OAUTH_MULTI_CLIENTSECRET = OAUTH_PREFIX + OAUTH_PARTIAL_CLIENTSECRET;
    public static String OAUTH_MULTI_CLIENTID = OAUTH_PREFIX + OAUTH_PARTIAL_CLIENTID;
    public static String OAUTH_MULTI_REDIRECTURI = OAUTH_PREFIX + OAUTH_PARTIAL_REDIRECTURI;
    
    public static String SFDC_SINGLE_SECURITY_HEALTHCHECK_SCORE = "sfdc.single.sechealthcheck_score";
    public static String SFDC_MULTI_SOURCE_SECURITY_HEALTHCHECK_SCORE = "sfdc.multi.source.sechealthcheck_score";
    public static String SFDC_MULTI_TARGET_SECURITY_HEALTHCHECK_SCORE = "sfdc.multi.target.sechealthcheck_score";
    public static String SFDC_SINGLE_APEXORGWIDECODECOVERAGE_SCORE = "sfdc.single.apexorgwidecodecoverage_score";
    public static String SFDC_MULTI_SOURCE_APEXORGWIDECODECOVERAGE_SCORE = "sfdc.multi.source.apexorgwidecodecoverage_score";
    public static String SFDC_MULTI_TARGET_APEXORGWIDECODECOVERAGE_SCORE = "sfdc.multi.target.apexorgwidecodecoverage_score";
    
    public static String SFDC_SINGLE_ORGID = "sfdc.single.orgid";
    public static String SFDC_SINGLE_USERNAME = "sfdc.single.username";
    public static String SFDC_SINGLE_ORGTYPE = "sfdc.single.orgtype";
    public static String SFDC_SINGLE_ORGNAME = "sfdc.single.orgname";
    public static String SFDC_SINGLE_ISSANDBOX = "sfdc.single.issandbox";
    public static boolean loginSuccessful = false;
    public static String SFDC_EXISTING_MAPPING_FILE_PATH = "sfdc.mappingfilepath";
    
    public static String SFDC_MULTI_SOURCE_ORGID = "sfdc.multi.source.orgid";
    public static String SFDC_MULTI_SOURCE_USERNAME = "sfdc.multi.source.username";
    public static String SFDC_MULTI_SOURCE_ORGTYPE = "sfdc.multi.source.orgtype";
    public static String SFDC_MULTI_SOURCE_ORGNAME = "sfdc.multi.source.orgname";
    public static String SFDC_MULTI_SOURCE_ISSANDBOX = "sfdc.multi.source.issandbox";
    
    public static String SFDC_MULTI_TARGET_ORGID = "sfdc.multi.target.orgid";
    public static String SFDC_MULTI_TARGET_USERNAME = "sfdc.multi.target.username";
    public static String SFDC_MULTI_TARGET_ORGTYPE = "sfdc.multi.target.orgtype";
    public static String SFDC_MULTI_TARGET_ORGNAME = "sfdc.multi.target.orgname";
    public static String SFDC_MULTI_TARGET_ISSANDBOX = "sfdc.multi.target.issandbox";

    public static ArrayList<String> SFDC_MAPPING_INSCOPE_ITEMS = null;
    public static ArrayList<String> SFDC_SINGLE_INSCOPE_ITEMS = null;
    public static ArrayList<String> SFDC_SINGLE_OUTSCOPE_ITEMS = null;
    public static ArrayList<String> SFDC_MULTI_SOURCE_INSCOPE_ITEMS = null;
    public static ArrayList<String> SFDC_MULTI_SOURCE_OUTSCOPE_ITEMS = null;
    public static ArrayList<String> SFDC_MULTI_TARGET_INSCOPE_ITEMS = null;
    public static ArrayList<String> SFDC_MULTI_TARGET_OUTSCOPE_ITEMS = null;
    public static ArrayList<String> SFDC_SINGLE_SOBJECTS_ITEMS = null;
    public static ArrayList<String> SFDC_SINGLE_OBJECTS_ITEMS = null;
    public static ArrayList<String> SFDC_MULTI_TARGET_SOBJECTS_ITEMS = null;
    public static ArrayList<String> SFDC_MULTI_SOURCE_SOBJECTS_ITEMS = null;
    //*************************************************************************************************************************************
    //******************************************************TEST CASE MAPPING SETTINGS**********************************************************
    //*************************************************************************************************************************************
    public static ArrayList<String> SFDC_ApexClassLV_ItemsList = null;
    public static ArrayList<String> SFDC_ApexComponentLV_ItemsList = null;
    public static ArrayList<String> SFDC_ApexPageLV_ItemsList = null;
    public static ArrayList<String> SFDC_ApexTriggerLV_ItemsList = null;
    public static ArrayList<String> SFDC_AssignmentRuleLV_ItemsList = null;
    public static ArrayList<String> SFDC_AuraDefinitionLV_ItemsList = null;
    public static ArrayList<String> SFDC_AutoResponseRuleLV_ItemsList = null;
    public static ArrayList<String> SFDC_BusinessProcessLV_ItemsList = null;
    public static ArrayList<String> SFDC_CompactLayoutLV_ItemsList = null;
    public static ArrayList<String> SFDC_CustomApplicationLV_ItemsList = null;
    public static ArrayList<String> SFDC_CustomFieldLV_ItemsList = null;
    public static ArrayList<String> SFDC_CustomObjectLV_ItemsList = null;
    public static ArrayList<String> SFDC_CustomTabLV_ItemsList = null;
    public static ArrayList<String> SFDC_FieldMappingLV_ItemsList = null;
    public static ArrayList<String> SFDC_FieldSetLV_ItemsList = null;
    public static ArrayList<String> SFDC_FlexiPageLV_ItemsList = null;
    public static ArrayList<String> SFDC_FlowLV_ItemsList = null;
    public static ArrayList<String> SFDC_FlowDefinitionLV_ItemsList = null;
    public static ArrayList<String> SFDC_HomePageComponentLV_ItemsList = null;
    public static ArrayList<String> SFDC_HomePageLayoutLV_ItemsList = null;
    public static ArrayList<String> SFDC_LayoutLV_ItemsList = null;
    public static ArrayList<String> SFDC_PathAssistantLV_ItemsList = null;
    public static ArrayList<String> SFDC_PermissionSetLV_ItemsList = null;
    public static ArrayList<String> SFDC_PermissionSetGroupLV_ItemsList = null;
    public static ArrayList<String> SFDC_ProfileLV_ItemsList = null;
    public static ArrayList<String> SFDC_QuickActionDefinitionLV_ItemsList = null;
    public static ArrayList<String> SFDC_RecordTypeLV_ItemsList = null;
    public static ArrayList<String> SFDC_UserRoleLV_ItemsList = null;
    public static ArrayList<String> SFDC_ValidationRuleLV_ItemsList = null;
    public static ArrayList<String> SFDC_WebLinkLV_ItemsList = null;
    
    //*************************************************************************************************************************************
    //******************************************************DATA LOADER SETTINGS**********************************************************
    //*************************************************************************************************************************************
    public static final String STRING_DEFAULT = ""; //$NON-NLS-1$
    public static final Map<String, String> MAP_STRING_DEFAULT = new LinkedHashMap<>();
    private static final Charset UTF8 = Charset.forName("UTF-8");
    // Delimiter settings
    public static final String CSV_DELIMETER_COMMA = "loader.csvComma";
    public static final String CSV_DELIMETER_TAB = "loader.csvTab";
    public static final String CSV_DELIMETER_OTHER = "loader.csvOther";
    public static final String CSV_DELIMETER_OTHER_VALUE = "loader.csvOtherValue";
    //Salesforce dataloader settings
    public static final String BULK_API_ENABLED = "sfdc.useBulkApi";
    public static final String BULK_API_SERIAL_MODE = "sfdc.bulkApiSerialMode";
    public static final String BULK_API_CHECK_STATUS_INTERVAL = "sfdc.bulkApiCheckStatusInterval";
    public static final String BULK_API_ZIP_CONTENT = "sfdc.bulkApiZipContent";
    public static final String TIMEZONE = "sfdc.timezone";
    // process configuration
    public static final String OUTPUT_STATUS_DIR = "process.statusOutputDirectory"; //$NON-NLS-1$
    public static final String OUTPUT_SUCCESS = "process.outputSuccess"; //$NON-NLS-1$
    public static final String ENABLE_EXTRACT_STATUS_OUTPUT = "process.enableExtractStatusOutput"; //$NON-NLS-1$
    public static final String ENABLE_LAST_RUN_OUTPUT = "process.enableLastRunOutput"; //$NON-NLS-1$
    public static final String LAST_RUN_OUTPUT_DIR = "process.lastRunOutputDirectory"; //$NON-NLS-1$
    public static final String OUTPUT_ERROR = "process.outputError"; //$NON-NLS-1$
    public static final String LOAD_ROW_TO_START_AT = "process.loadRowToStartAt"; //$NON-NLS-1$
    public static final String INITIAL_LAST_RUN_DATE = "process.initialLastRunDate";
    public static final String ENCRYPTION_KEY_FILE = "process.encryptionKeyFile"; //$NON-NLS-1$

    public static final String READ_UTF8 = "dataAccess.readUTF8"; //$NON-NLS-1$
    public static final String WRITE_UTF8 = "dataAccess.writeUTF8"; //$NON-NLS-1$

    private String filename;

    private boolean isBatchMode = false;

    public static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public static final String TRUE = "true"; //$NON-NLS-1$
    public static final String FALSE = "false"; //$NON-NLS-1$
    public static final String BULK_API_ENCODING = "UTF-8";


    private boolean useBulkApiByDefault() {
        return false;
    }

    /**
     * This sets the current defaults.
     */
    public void setDefaults() {
    	setValue(CSV_DELIMETER_COMMA, true);
        setValue(CSV_DELIMETER_TAB, true);
        setValue(CSV_DELIMETER_OTHER, false);
        setValue(CSV_DELIMETER_OTHER_VALUE, "-");

        setValue(ENDPOINT, DEFAULT_ENDPOINT_URL);
        setValue(ENABLE_EXTRACT_STATUS_OUTPUT, false);
        setValue(ENABLE_LAST_RUN_OUTPUT, true);
        // TODO: When we're ready, make Bulk API turned on by default.
        setValue(BULK_API_ENABLED, useBulkApiByDefault());
        setValue(BULK_API_SERIAL_MODE, false);
        setValue(BULK_API_ZIP_CONTENT, false);
        setValue(TIMEZONE, TimeZone.getDefault().getID());
        //oauth settings
        setValue(OAUTH_SERVER, DEFAULT_ENDPOINT_URL);
        setValue(OAUTH_CUSTOM_LOGIN_URL,DEFAULT_ENDPOINT_URL);
        setValue(OAUTH_SERVER ,OAUTH_PREFIX + OAUTH_PARTIAL_SERVER);
        setValue(OAUTH_SINGLE_CLIENTSECRET, OAUTH_PREFIX + OAUTH_PARTIAL_CLIENTSECRET);
        setValue(OAUTH_SINGLE_CLIENTID, OAUTH_PREFIX + OAUTH_PARTIAL_CLIENTID);
        setValue(OAUTH_SINGLE_REDIRECTURI, OAUTH_PREFIX + OAUTH_PARTIAL_REDIRECTURI);
        setValue(OAUTH_MULTI_CLIENTSECRET, OAUTH_PREFIX + OAUTH_PARTIAL_CLIENTSECRET);
        setValue(OAUTH_MULTI_CLIENTID, OAUTH_PREFIX + OAUTH_PARTIAL_CLIENTID);
        setValue(OAUTH_MULTI_REDIRECTURI, OAUTH_PREFIX + OAUTH_PARTIAL_REDIRECTURI);
        setValue(OAUTH_ENVIRONMENT_SINGLE, OAUTH_PREFIX + "environment.single");
        setValue(OAUTH_API_VERSION_SINGLE, OAUTH_PREFIX + "api.single");
        setValue(OAUTH_ACCESSTOKEN_SINGLE, OAUTH_PREFIX + "single.accesstoken");
        setValue(OAUTH_REFRESHTOKEN_SINGLE, OAUTH_PREFIX + "single.refreshtoken");
        setValue(OAUTH_INSTANCEURL_SINGLE, OAUTH_PREFIX + "single.instanceurl");
        setValue(OAUTH_ENVIRONMENT_MULTI_SOURCE, OAUTH_PREFIX + "environment.multi.source");
        setValue(OAUTH_API_VERSION_MULTI_SOURCE, OAUTH_PREFIX + "api.multi.source");
        setValue(OAUTH_ACCESSTOKEN_MULTI_SOURCE, OAUTH_PREFIX + "multi.source.accesstoken");
        setValue(OAUTH_REFRESHTOKEN_MULTI_SOURCE, OAUTH_PREFIX + "multi.source.refreshtoken");
        setValue(OAUTH_INSTANCEURL_MULTI_SOURCE, OAUTH_PREFIX + "multi.source.instanceurl");
        setValue(OAUTH_ENVIRONMENT_MULTI_TARGET, OAUTH_PREFIX + "environment.multi.target");
        setValue(OAUTH_API_VERSION_MULTI_TARGET, OAUTH_PREFIX + "api.multi.target");
        setValue(OAUTH_ACCESSTOKEN_MULTI_TARGET, OAUTH_PREFIX + "multi.target.accesstoken");
        setValue(OAUTH_REFRESHTOKEN_MULTI_TARGET, OAUTH_PREFIX + "multi.target.refreshtoken");
        setValue(OAUTH_INSTANCEURL_MULTI_TARGET, OAUTH_PREFIX + "multi.target.instanceurl");
        /* production server and redirecturi, sandbox server and redirecturi */
        setValue(OAUTH_PREFIX + OAUTH_PROD_ENVIRONMENT_VAL + "." + OAUTH_PARTIAL_SERVER, OAUTH_PROD_SERVER_VAL);
        setValue(OAUTH_PREFIX + OAUTH_PROD_ENVIRONMENT_VAL + "." + OAUTH_PARTIAL_REDIRECTURI, OAUTH_PROD_REDIRECTURI_VAL);

        setValue(OAUTH_PREFIX + OAUTH_SB_ENVIRONMENT_VAL + "." + OAUTH_PARTIAL_SERVER, OAUTH_SB_SERVER_VAL);
        setValue(OAUTH_PREFIX + OAUTH_SB_ENVIRONMENT_VAL + "." + OAUTH_PARTIAL_REDIRECTURI, OAUTH_SB_REDIRECTURI_VAL);
        setValue(SFDC_SINGLE_SECURITY_HEALTHCHECK_SCORE,"sfdc.single.sechealthcheck_score");
        setValue(SFDC_MULTI_SOURCE_SECURITY_HEALTHCHECK_SCORE,"sfdc.multi.source.sechealthcheck_score");
        setValue(SFDC_MULTI_TARGET_SECURITY_HEALTHCHECK_SCORE,"sfdc.multi.target.sechealthcheck_score");
        
        setValue(SFDC_SINGLE_APEXORGWIDECODECOVERAGE_SCORE,"sfdc.single.apexorgwidecodecoverage_score");
        setValue(SFDC_MULTI_SOURCE_APEXORGWIDECODECOVERAGE_SCORE,"sfdc.multi.source.apexorgwidecodecoverage_score");
        setValue(SFDC_MULTI_TARGET_APEXORGWIDECODECOVERAGE_SCORE,"sfdc.multi.target.apexorgwidecodecoverage_score");
        
        setValue(SFDC_SINGLE_ORGID,"sfdc.single.orgid");
        setValue(SFDC_SINGLE_USERNAME,"sfdc.single.username");
        setValue(SFDC_SINGLE_ORGTYPE,"sfdc.single.orgtype");
        setValue(SFDC_SINGLE_ORGNAME,"sfdc.single.orgname");
        setValue(SFDC_SINGLE_ISSANDBOX,"sfdc.single.issandbox");
        
        setValue(SFDC_MULTI_SOURCE_ORGID,"sfdc.multi.source.orgid");
        setValue(SFDC_MULTI_SOURCE_USERNAME,"sfdc.multi.source.username");
        setValue(SFDC_MULTI_SOURCE_ORGTYPE,"sfdc.multi.source.orgtype");
        setValue(SFDC_MULTI_SOURCE_ORGNAME,"sfdc.multi.source.orgname");
        setValue(SFDC_MULTI_SOURCE_ISSANDBOX,"sfdc.multi.source.issandbox");
        
        setValue(SFDC_MULTI_TARGET_ORGID,"sfdc.multi.target.orgid");
        setValue(SFDC_MULTI_TARGET_USERNAME,"sfdc.multi.target.username");
        setValue(SFDC_MULTI_TARGET_ORGTYPE,"sfdc.multi.target.orgtype");
        setValue(SFDC_MULTI_TARGET_ORGNAME,"sfdc.multi.target.orgname");
        setValue(SFDC_MULTI_TARGET_ISSANDBOX,"sfdc.multi.target.issandbox");
        setValue(SFDC_EXISTING_MAPPING_FILE_PATH, "sfdc.mappingfilepath");
        
        SFDC_MAPPING_INSCOPE_ITEMS = null;
        SFDC_SINGLE_INSCOPE_ITEMS = null;
        SFDC_SINGLE_OUTSCOPE_ITEMS = null;
        SFDC_MULTI_SOURCE_INSCOPE_ITEMS = null;
        SFDC_MULTI_SOURCE_OUTSCOPE_ITEMS = null;
        SFDC_MULTI_TARGET_INSCOPE_ITEMS = null;
        SFDC_MULTI_TARGET_OUTSCOPE_ITEMS = null;
        loginSuccessful = false;
        SFDC_SINGLE_SOBJECTS_ITEMS = null;
        SFDC_SINGLE_OBJECTS_ITEMS = null;
        SFDC_MULTI_TARGET_SOBJECTS_ITEMS = null;
        SFDC_MULTI_SOURCE_SOBJECTS_ITEMS = null;
    }

    public TimeZone getTimeZone() {
        return TimeZone.getTimeZone(TIMEZONE);
    }


    /**
     * Puts a set of values from a map into config
     * @param values Map of overriding values
     */
    public void putValue(Map<String, String> values){
        for (String key : values.keySet()) {
            putValue(key, values.get(key));
        }
    }

    /**
     * Puts a value into the config
     */
    public void putValue(String name, String value) {
        String oldValue = name;
        if (oldValue == null || !oldValue.equals(value)) {
            setValue(name, value);
        }
    }


    public void setValue(String name, Map<String, String> valueMap) {
        StringBuilder sb = new StringBuilder();
        for (String key : valueMap.keySet()) {
            // add comma for subsequent entries
            if (sb.length() != 0) {
                sb.append(",");
            }
            sb.append(key + "=" + valueMap.get(key));
        }
        putValue(name, sb.toString());
    }


    /**
     * Sets a string
     */
    public void setValue(String name, String... values) {
        if (values != null && values.length > 1) {
            StringJoiner joiner = new StringJoiner(",");
            for (String value : values) {
                joiner.add(value);
            }
            //setProperty(name, joiner.toString());
        } else if (values != null && values.length > 0) {
            //setProperty(name, values[0]);
        } else {
            //setProperty(name, null);
        }

    }

    /**
     * Sets a boolean
     */
    public void setValue(String name, boolean value) {
        name=Boolean.toString(value);
    }

    public void setValue(String name, Date value) {
        name=DATE_FORMATTER.format(value);
    }

}

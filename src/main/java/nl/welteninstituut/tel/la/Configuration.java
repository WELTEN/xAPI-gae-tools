/*
 * Copyright (C) 2015 Open Universiteit Nederland
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.welteninstituut.tel.la;

import com.google.appengine.api.appidentity.AppIdentityServiceFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

/**
 * @author Stefaan Ternier
*
 */
public class Configuration {

    public static final String XAPITARGET = "xapiTarget";
    public static final String BQProject = "bigQueryProject";
    public static final String BQDataSet = "bigQueryDataSet";
    public static final String BQTableId = "bigQueryTableId";

    public static final String EXPORTERS = "exporters";
    public static final String IMPORTERS = "importers";
    private static Properties props = new Properties();
    private static String serviceAccountName = AppIdentityServiceFactory.getAppIdentityService().getServiceAccountName();

    public static final String METAACCOUNT = "metaAccount";
    public static final String SECONDARY_ACCOUNT = "secondaryAccounts";
    public static final String AUTHORIZATION = "authorization";
    public static final String STARTDATE = "import.startdate";
    public static final String START_TIME = "import.starttime";
    public static final String END_TIME = "import.endtime";
    public static final String WORKING_DAYS = "import.workingdays";

    static {
        InputStream is = Configuration.class.getClassLoader().getResourceAsStream("./META-INF/configuration.properties");

        try {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	try {
        		// InputStream is still open after call to props.load
				is.close();
			} catch (IOException e) {
			}
        }
    }

    public static String get(String key) {
        return StringUtils.trim(props.getProperty(serviceAccountName + "." + key));
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }
    
    public static boolean getAsBoolean(final String key) {
    	return Boolean.parseBoolean(get(key));
    }
    
    public static boolean getAsBoolean(final String key, final boolean defaultValue) {
    	String value = get(key);
    	return value != null ?  Boolean.parseBoolean(value) : defaultValue;
    }

    public static boolean listContains(String property, int value) {
        if (get(property) == null) return false;
        for (String intString :get(property).split(";")){
            if (Integer.parseInt(intString)==value) return true;
        }
        return false;
    }

    public static String getAppId() {
        return serviceAccountName;
    }
}

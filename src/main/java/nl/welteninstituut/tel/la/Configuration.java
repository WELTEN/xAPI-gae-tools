package nl.welteninstituut.tel.la;

import com.google.appengine.api.appidentity.AppIdentityServiceFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by str on 22/05/15.
 */
public class Configuration {

    public static final String XAPITARGET = "xapiTarget";
    public static final String BQProject = "bigQueryProject";
    public static final String BQDataSet = "bigQueryDataSet";
    public static final String BQTableId = "bigQueryTableId";

    public static final String EXPORTERS = "exporters";
    private static Properties props = new Properties();
    private static String serviceAccountName = AppIdentityServiceFactory.getAppIdentityService().getServiceAccountName();


    static {
        InputStream is = Configuration.class.getClassLoader().getResourceAsStream("./META-INF/configuration.properties");

        try {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String property) {
        return props.getProperty(serviceAccountName+"."+property);
    }

    public static String getAppId() {
        return serviceAccountName;
    }
}

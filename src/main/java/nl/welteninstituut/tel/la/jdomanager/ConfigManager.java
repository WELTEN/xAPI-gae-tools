package nl.welteninstituut.tel.la.jdomanager;

import com.google.appengine.api.datastore.Text;
import nl.welteninstituut.tel.la.jdo.Config;

import javax.cache.CacheManager;
import javax.jdo.PersistenceManager;

/**
 * Created by str on 24/04/15.
 */
public class ConfigManager {

    public static String addKey(String key, String value) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Config gi = new Config();
        gi.setKey(key);
        gi.setValue(value);
        try {
            pm.makePersistent(gi);
            return gi.getKey();
        } finally {
            pm.close();
        }
    }

//    public static String getValue(String key) {
//        PersistenceManager pm = PMF.get().getPersistenceManager();
//        String value = (String) GenericCache.getInstance().getCache().get(key);
//        if (value == null) {
//            try {
//                value = pm.getObjectById(Config.class,  key).getValue();
//                GenericCache.getInstance().getCache().put(key, value);
//
//            } finally {
//                pm.close();
//            }
//        }
//        return value;
//    }
}

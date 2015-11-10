package nl.welteninstituut.tel.la.jdomanager;

import javax.jdo.PersistenceManager;

import nl.welteninstituut.tel.la.jdo.QueryCache;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

/**
 * @author Stefaan Ternier
 * 
 */
public class QueryCacheManager {


    public static void addQueryResult(String id, String result) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        QueryCache queryCache = new QueryCache();
        queryCache.setId(id);
        queryCache.setQueryResult(new Text(result));
        queryCache.setLastModificationDate(System.currentTimeMillis());
        try {
            pm.makePersistent(queryCache);
        } finally {
            pm.close();
        }

    }

    public static String getQueryResult(String s) {
        try {
            PersistenceManager pm = PMF.get().getPersistenceManager();
            Key k = KeyFactory.createKey(QueryCache.class.getSimpleName(), s);
            QueryCache e = pm.getObjectById(QueryCache.class, k);
            return e.getQueryResult().getValue();
        } catch (Exception e ){
            return "{}";
        }

    }

    public static Long getLastModificationDate(String s) {
        try {
            PersistenceManager pm = PMF.get().getPersistenceManager();
            Key k = KeyFactory.createKey(QueryCache.class.getSimpleName(), s);
            QueryCache e = pm.getObjectById(QueryCache.class, k);
            return e.getLastModificationDate();
        } catch (Exception e ){
            return 0l;
        }
    }
}

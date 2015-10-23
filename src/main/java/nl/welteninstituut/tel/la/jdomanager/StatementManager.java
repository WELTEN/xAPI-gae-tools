package nl.welteninstituut.tel.la.jdomanager;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import nl.welteninstituut.tel.la.jdo.Statement;

import javax.jdo.PersistenceManager;

/**
 * Created by str on 18/02/15.
 */
public class StatementManager {

    public static long addStatement(String statement, String authorizationData, String laId) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Statement gi = new Statement();
        gi.setLastModificationDate(System.currentTimeMillis());
        gi.setStatementPayload(new Text(statement));
        gi.setAuthorizationData(authorizationData);
        gi.setSynchronized(true);
        gi.setLearningLockerId(laId);
        try {
            pm.makePersistent(gi);
            return gi.getIdentifier();
        } finally {
            pm.close();
        }
    }

    public static void addStatementAsync(String statement) {
        Entity entity = new Entity("Statement");
        entity.setProperty("statementPayload", new Text(statement));
        entity.setProperty("lastModificationDate", System.currentTimeMillis());
        entity.setProperty("synchronized", false);
        
    }

    public static long addStatementWithError(String statement, String authorizationData, String errorMessage) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Statement gi = new Statement();
        gi.setLastModificationDate(System.currentTimeMillis());
        gi.setStatementPayload(new Text(statement));
        gi.setAuthorizationData(authorizationData);
        gi.setSynchronized(false);
        gi.setErrorMessage(new Text(errorMessage));
        try {
            pm.makePersistent(gi);
            System.out.println(gi.getIdentifier());
            return gi.getIdentifier();
        } finally {
            pm.close();
        }
    }

    public static String getStatementError(String id) {
        PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
//            return ((Statement) pm.getObjectById()).getErrorMessage().getValue();
            return pm.getObjectById(Statement.class,  Long.parseLong(id)).getErrorMessage().getValue();
//            return ((Statement) pm.getObjectById(KeyFactory.createKey(Statement.class.getSimpleName(), id))).getErrorMessage().getValue();
        } finally {
            pm.close();
        }
    }

    public static String getStatementJson(String id) {
        PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
//            return ((Statement) pm.getObjectById()).getErrorMessage().getValue();
            return pm.getObjectById(Statement.class,  Long.parseLong(id)).getStatementPayload().getValue();
//            return ((Statement) pm.getObjectById(KeyFactory.createKey(Statement.class.getSimpleName(), id))).getErrorMessage().getValue();
        } finally {
            pm.close();
        }
    }
}

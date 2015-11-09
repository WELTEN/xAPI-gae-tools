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
package nl.welteninstituut.tel.la.jdomanager;

import com.google.appengine.api.datastore.*;
import nl.welteninstituut.tel.la.jdo.Statement;

import javax.jdo.PersistenceManager;
import java.util.UUID;

/**
 * @author Stefaan Ternier
 * 
 */
public class StatementManager {

    static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();


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

    public static long addStatement(String statement, String origin) {
        Entity entity = new Entity(KeyFactory.createKey("Statement", UUID.randomUUID().toString()));
        entity.setProperty("statementPayload", new Text(statement));
        entity.setProperty("lastModificationDate", System.currentTimeMillis());
        entity.setProperty("syncronisationState", Statement.UNSYNCED);
        entity.setProperty("origin", origin);
        return datastore.put(entity).getId();
    }

    public static void addStatementAsync(String statement, String origin) {
        Entity entity = new Entity("Statement");
        entity.setProperty("statementPayload", new Text(statement));
        entity.setProperty("lastModificationDate", System.currentTimeMillis());
        entity.setProperty("syncronisationState", Statement.UNSYNCED);
        entity.setProperty("origin", origin);
        datastore.put(entity);
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

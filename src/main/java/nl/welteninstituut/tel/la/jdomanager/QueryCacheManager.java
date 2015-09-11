package nl.welteninstituut.tel.la.jdomanager;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import nl.welteninstituut.tel.la.jdo.QueryCache;
import nl.welteninstituut.tel.la.jdo.Statement;

import javax.jdo.PersistenceManager;

/**
 * ****************************************************************************
 * Copyright (C) 2013 Open Universiteit Nederland
 * <p/>
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contributors: Stefaan Ternier
 * ****************************************************************************
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

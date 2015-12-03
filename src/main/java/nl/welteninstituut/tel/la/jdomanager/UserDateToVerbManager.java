package nl.welteninstituut.tel.la.jdomanager;

import com.google.appengine.api.datastore.*;
import nl.welteninstituut.tel.la.jdo.Statement;
import nl.welteninstituut.tel.la.jdo.UserDateToVerb;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

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
public class UserDateToVerbManager {

    public static JSONObject getUserActivities(String userId) {
        JSONArray array = new JSONArray();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query q = new Query(UserDateToVerb.TABLE_NAME)
                .setFilter(new Query.FilterPredicate(UserDateToVerb.ACTORID_COLUMN,
                        Query.FilterOperator.EQUAL,
                        userId));

        PreparedQuery pq = datastore.prepare(q);
        for (Entity result : pq.asIterable()) {
            try {
                JSONObject entry = new JSONObject(((Text) result.getProperty(UserDateToVerb.VALUE_COLUMN)).getValue());
                String actorId = (String) result.getProperty(UserDateToVerb.ACTORID_COLUMN);
                String key = result.getKey().getName();
                entry.put("date", key.substring(key.indexOf(actorId)+actorId.length()+1) );
                entry.put("actorId", actorId);
                array.put(entry);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject result = new JSONObject();
        try {
            result.put("results", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public static String getUserActivitiesGData(String userId, String activityId) {
        String resultString = "{\"cols\": [{\"id\": \"Date\",\"label\": \"Date\",\n" +
                "\t\t\"type\": \"date\"\n" +
                "\t}, {\n" +
                "\t\t\"id\": \"Logins\",\n" +
                "\t\t\"label\": \"Logins\",\n" +
                "\t\t\"type\": \"number\"\n" +
                "\t}],\n" +
                "\t\"rows\": [";
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        boolean first = true;
        Query q = new Query(UserDateToVerb.TABLE_NAME)
                .setFilter(new Query.FilterPredicate(UserDateToVerb.ACTORID_COLUMN,
                        Query.FilterOperator.EQUAL,
                        userId));

        PreparedQuery pq = datastore.prepare(q);
        for (Entity result : pq.asIterable()) {
            try {
                JSONObject entry = new JSONObject(((Text) result.getProperty(UserDateToVerb.VALUE_COLUMN)).getValue());
                String actorId = (String) result.getProperty(UserDateToVerb.ACTORID_COLUMN);
                String key = result.getKey().getName();
                String date = key.substring(key.indexOf(actorId) + actorId.length() + 1);
                Iterator<String> keyIter = entry.keys();
                int resultInt = 0;
                while (keyIter.hasNext()){
                    String activityKey = keyIter.next();
                    if (activityKey.contains(activityId) || activityId.equals("all")){
                        resultInt += entry.getInt(activityKey);
                    }
                }


                String toDate = null;
                try {
                    Date d =format.parse(date);
                    toDate = ""+(d.getYear()+1900)+","+d.getMonth()+","+d.getDate();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                System.out.println(entry.toString());
                if (first == false) {
                    resultString +=",";

                } else {
                    first = false;
                }
                resultString += "{\n" +
                        "\t\t\"c\": [{\n" +
                        "\t\t\t\"v\": \"Date("+toDate+")\"\n" +
                        "\t\t}, {\n" +
                        "\t\t\t\"v\": \""+resultInt+"\"\n" +
                        "\t\t}]\n" +
                        "\t}";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        resultString += "]}";
        return resultString;
    }
}

package nl.welteninstituut.tel.la.jdomanager;

import com.google.appengine.api.datastore.*;
import nl.welteninstituut.tel.la.jdo.CourseUserDateToObject;
import nl.welteninstituut.tel.util.StringPool;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
public class CourseDateToObjectManager {

    public static String getResourcesCommittedGData(String courseId, String resourceId) {
        String resultsString = "{\"cols\":[{\"id\":\"Average all learners in the course\"," +
                "\"label\":\"Average all learners in the course\",\"type\":\"string\"}," +
                "{\"id\":\"Videos\",\"label\":\"Videos\",\"type\":\"number\"}," +
                "{\"id\":\"Slides\",\"label\":\"Slides\",\"type\":\"number\"}," +
                "{\"id\":\"Pages\",\"label\":\"Pages\",\"type\":\"number\"}]," +
                "\"rows\":[{\"c\":[{\"v\":\"Peers vs you\"},{\"v\":\"12\"},{\"v\":\"2\"},{\"v\":\"4\"}]}]}";
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query q = new Query(CourseUserDateToObject.TABLE_NAME)
                .setFilter(new Query.FilterPredicate(CourseUserDateToObject.COURSEID_COLUMN,
                        Query.FilterOperator.EQUAL,
                        courseId));

        PreparedQuery pq = datastore.prepare(q);
        HashMap<String, Long> keyToCount = new HashMap<>();
        for (Entity result : pq.asIterable()) {
            try {
                JSONObject entry = new JSONObject(((Text) result.getProperty(CourseUserDateToObject.VALUE_COLUMN)).getValue());
                String key = result.getKey().getName();
                String date = key.substring(key.indexOf(courseId) + courseId.length() + 1);
                Iterator<String> keyIter = entry.keys();
                int resultInt = 0;

                while (keyIter.hasNext()){
                    String activityKey = keyIter.next();
                    if (activityKey.contains(resourceId) || resourceId.equals("all")){
                        resultInt += entry.getInt(activityKey);
                        if (!keyToCount.containsKey(activityKey)) {
                            keyToCount.put(activityKey, (long) entry.getInt(activityKey));
                        } else {
                            keyToCount.put(activityKey, keyToCount.get(activityKey) + ((long) entry.getInt(activityKey)));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        System.out.println(keyToCount);
        boolean first = true;
        String labels = StringPool.BLANK;
        String values = StringPool.BLANK;
        for (String key:keyToCount.keySet()){
            if (first == false) {
                values +=",";
                labels +=",";
            } else {
                first = false;
            }
            labels += "{\"id\":\""+key+"\",\"label\":\""+key+"\",\"type\":\"number\"}";
            values += "{\"v\":\""+keyToCount.get(key)+"\"}";
        }

        return "{\"cols\":[{\"id\":\"Average all learners in the course\"," +
                "\"label\":\"Resources committed\",\"type\":\"string\"}," +
                labels+
                "]," +
                "\"rows\":[{\"c\":[{\"v\":\"Resources\"},"+values+"]}]}";
    }

}

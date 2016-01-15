package nl.welteninstituut.tel.la.jdomanager;

import com.google.appengine.api.datastore.*;
import nl.welteninstituut.tel.la.jdo.CourseUserDateToObjectDefinition;
import nl.welteninstituut.tel.util.StringPool;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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
public class CourseDateToObjectDefinitionManager {

    public static String getResourcesCommittedD3(String courseId, String userId, String resourceId) {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query.Filter courseFilter =
                new Query.FilterPredicate(CourseUserDateToObjectDefinition.COURSEID_COLUMN,
                        Query.FilterOperator.EQUAL,
                        courseId);

        Query.Filter actorFilter =
                new Query.FilterPredicate(CourseUserDateToObjectDefinition.ACTORID_COLUMN,
                        Query.FilterOperator.EQUAL,
                        userId);

        Query.Filter compositeFilter =
                Query.CompositeFilterOperator.and(courseFilter, actorFilter);


        Query q = new Query(CourseUserDateToObjectDefinition.TABLE_NAME)
                .setFilter(compositeFilter);

        PreparedQuery pq = datastore.prepare(q);
        HashMap<String, Long> keyToCount = new HashMap<>();
        for (Entity result : pq.asIterable()) {
            try {
                JSONObject entry = new JSONObject(((Text) result.getProperty(CourseUserDateToObjectDefinition.VALUE_COLUMN)).getValue());
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
        boolean first = true;
        String resultString = "[";
        for (String key:keyToCount.keySet()){
            if (first == false) {
                resultString +=",";
            } else {
                first = false;
            }
            resultString += "{\"label\":\""+keyMapper(key)+"\",\"rate\":"+keyToCount.get(key)+"}";

        }

        return resultString + "]";
    }

    public static String getResourcesCommittedGData(String courseId, String userId, String resourceId) {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query.Filter courseFilter =
                new Query.FilterPredicate(CourseUserDateToObjectDefinition.COURSEID_COLUMN,
                        Query.FilterOperator.EQUAL,
                        courseId);

        Query.Filter actorFilter =
                new Query.FilterPredicate(CourseUserDateToObjectDefinition.ACTORID_COLUMN,
                Query.FilterOperator.EQUAL,
                userId);

        Query.Filter compositeFilter =
                Query.CompositeFilterOperator.and(courseFilter, actorFilter);


        Query q = new Query(CourseUserDateToObjectDefinition.TABLE_NAME)
                .setFilter(compositeFilter);

        PreparedQuery pq = datastore.prepare(q);
        HashMap<String, Long> keyToCount = new HashMap<>();
        for (Entity result : pq.asIterable()) {
            try {
                JSONObject entry = new JSONObject(((Text) result.getProperty(CourseUserDateToObjectDefinition.VALUE_COLUMN)).getValue());
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
            labels += "{\"id\":\""+key+"\",\"label\":\""+keyMapper(key)+"\",\"type\":\"number\"}";
            values += "{\"v\":\""+keyToCount.get(key)+"\"}";
        }

        return "{\"cols\":[{\"id\":\"Average all learners in the course\"," +
                "\"label\":\"Resources committed\",\"type\":\"string\"}," +
                labels+
                "]," +
                "\"rows\":[{\"c\":[{\"v\":\"Resources\"},"+values+"]}]}";
    }


    public static  String keyMapper(String key) {
        key = key.toLowerCase();
        if (key.contains("article")) {
            return "article";
        } else if (key.contains("course")) {
            return "course";
        } else if (key.contains("discussion")) {
            return "discussion";
        } else if (key.contains("task")) {
            return "task";
        } else if (key.contains("peerassessment")) {
            return "peerassessment";
        } else if (key.contains("assessment")) {
            return "assessment";
        } if (key.contains("page")) {
            return "page";
        } else if (key.contains("module")) {
            return "module";
        } else if (key.contains("video")) {
            return "video";
        }  else if (key.contains("peerfeedback")) {
            return "peerfeedback";
        } else if (key.contains("forummessage")) {
            return "F" +
                    "orum Message";
        } else if (key.contains("book")) {
            return "book";
        } else if (key.contains("syllabus")) {
            return "syllabus";
        } else if (key.contains("discussionthread")) {
            return "discussionthread";
        } else if (key.contains("activitystream")) {
            return "activitystream";
        } else if (key.contains("blog")) {
            return "blog";
        }

        return key;
    }
}

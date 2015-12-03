package nl.welteninstituut.tel.la.mapreduce;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.tools.mapreduce.*;
import com.google.appengine.tools.mapreduce.inputs.DatastoreInput;
import com.google.appengine.tools.mapreduce.outputs.DatastoreOutput;
import nl.welteninstituut.tel.la.jdo.CourseDateToVerb;
import nl.welteninstituut.tel.util.StringPool;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

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
public class CourseDateToVerbJob extends Job {

    private static final Logger LOG = Logger.getLogger(CalendarActivitiesJob.class.getName());
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    private static DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private Long date;

    public CourseDateToVerbJob(long date) {
        this.date =date;
    }

    public void start() {
        MapReduceJob.start(getMapReduceSpecification(5), getMapReduceSettings());

    }

    private MapReduceSpecification getMapReduceSpecification(int mapShardCount){

        Query q = new Query("Statement");
        if (date != null){
            Query.Filter propertyFilter =
                    new Query.FilterPredicate("lastModificationDate",
                            Query.FilterOperator.GREATER_THAN,
                            date);
            q = q.setFilter(propertyFilter);
        }

        DatastoreInput input = new DatastoreInput(q, mapShardCount);
        CalendarMapper mapper = new CalendarMapper();
        CalendarReducer reducer = new CalendarReducer(); //String, Entity, KeyValue<String, Entity> (K V O)
//        Output<KeyValue<String, UserDateVerb>, Void> output = new CalendarOutput(); //O R
        DatastoreOutput output = new DatastoreOutput(); //O R Enity void
        //i k v o r  Enity string entity
        MapReduceSpecification<Entity, String, Entity, Entity, Void> spec;
        spec = new MapReduceSpecification.Builder<>(input, mapper, reducer, output)
                .build();
        return spec;
    }


    class CalendarMapper extends Mapper<Entity, String, Entity> {

        @Override
        public void map(Entity entity) {
            JSONObject statementPayload = null;
            try {
                statementPayload = new JSONObject(((Text)entity.getProperty("statementPayload")).getValue());
                JSONObject actorObject = statementPayload.getJSONObject("actor");
                long timestampLong = 0l;
                String timestamp = null;
                if (statementPayload.has("timestamp")) {
                    timestamp = statementPayload.getString("timestamp");
                    try {
                        timestampLong = df.parse(timestamp).getTime();
                    } catch (ParseException e) {
                        timestampLong = df2.parse(timestamp).getTime();
                    }
                }
                if (timestampLong > date) {
                    String actorId;
                    if (actorObject.has("account")) {
                        actorId = actorObject.getJSONObject("account").getString("name");
                    } else {
                        actorId = actorObject.getString("mbox");
                    }
                    String verbId = statementPayload.getJSONObject("verb").getString("id");
                    String courseId = "todo";
                    if (statementPayload.has("context")) {
                        JSONObject context = statementPayload.getJSONObject("context");
                        if (context.has("contextActivities")) {
                            JSONObject contextActivities = context.getJSONObject("contextActivities");
                            if (contextActivities.has("parent")) {
                                JSONObject parent = contextActivities.getJSONArray("parent").getJSONObject(0);
                                if (
                                        parent.has("id") &&
                                                parent.has("definition") &&
                                                parent.getJSONObject("definition").has("type") &&
                                                parent.getJSONObject("definition").getString("type").equals("http://adlnet.gov/expapi/activities/course")) {
                                    courseId = parent.getString("id");
                                }

                            }
                        }
                    }

                    UserDateVerb userDateVerb = new UserDateVerb();
                    userDateVerb.addVerb(verbId);


                    String key = courseId+ ":" + format.format(new Date(timestampLong));
                    Entity entityOut = new Entity(KeyFactory.createKey(CourseDateToVerb.TABLE_NAME, key));
                    entityOut.setProperty(CourseDateToVerb.VALUE_COLUMN, new Text(userDateVerb.toJson().toString()));

                    entityOut.setProperty(CourseDateToVerb.COURSEID_COLUMN, courseId);
                    emit(key, entityOut);

                } else {
                    LOG.severe("Statement "+entity.getKey().getName()+"  has a date that is too old: "+timestamp);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(statementPayload);
            }

        }
    }

    class CalendarReducer extends Reducer<String, Entity,  Entity> { //K V O

        @Override
        public void reduce(String actorIdDate, ReducerInput<Entity> reducerInput) { //K  ReducerInput<V>
            Entity resultEntity = null;
            UserDateVerb resultUserDateVerb = null;
            while (reducerInput.hasNext()) {
                if (resultEntity == null) {
                    resultEntity = reducerInput.next();
                    try {
                        resultUserDateVerb = new UserDateVerb(((Text)resultEntity.getProperty("value")).getValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Entity toProcess = reducerInput.next();
                    try {
                        UserDateVerb newUserDateVerb = new UserDateVerb(((Text) toProcess.getProperty("value")).getValue());
                        resultUserDateVerb.add(newUserDateVerb);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            resultEntity.setProperty(CourseDateToVerb.VALUE_COLUMN, new Text(resultUserDateVerb.toJson().toString()));

            emit(resultEntity);
        }
    }

    private class UserDateVerb implements Serializable {

        private HashMap<String, Integer> verbMap = new HashMap<>();

        public UserDateVerb(){}

        public UserDateVerb(String jsonAsString) throws JSONException {
            this(new JSONObject(jsonAsString));
        }

        public UserDateVerb(JSONObject json){
            Iterator it = json.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                try {
                    addVerb(key, json.getInt(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        public void addVerb(String verbId) {
            addVerb(verbId, 1);
        }
        public void addVerb(String verbId, int i) {
            if (verbMap.containsKey(verbId)) {
                verbMap.put(verbId, verbMap.get(verbId)+i);
            } else {
                verbMap.put(verbId, i);
            }
        }

        public void add(UserDateVerb verbsToAdd) {
            for(String key: verbsToAdd.verbMap.keySet()) {
                addVerb(key, verbsToAdd.verbMap.get(key));
            }
        }

        public JSONObject toJson() {
            JSONObject result = new JSONObject();
            for (String key: verbMap.keySet()) {
                try {
                    result.put(key, verbMap.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return  result;
        }

        public String toString() {
            String result = StringPool.BLANK;
            for(String key: verbMap.keySet()) {
                result += key + " " + verbMap.get(key)+ "/n";
            }
            return result;
        }
    }
}
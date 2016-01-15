package nl.welteninstituut.tel.la.tasks;

import com.google.api.services.bigquery.model.TableDataInsertAllRequest;
import com.google.api.services.bigquery.model.TableDataInsertAllResponse;
import com.google.api.services.bigquery.model.TableRow;
import com.google.appengine.api.datastore.*;
import nl.welteninstituut.tel.la.Configuration;
import nl.welteninstituut.tel.la.bigquery.InsertAPI;
import nl.welteninstituut.tel.la.jdo.Statement;
import nl.welteninstituut.tel.la.jdomanager.StatementManager;
import nl.welteninstituut.tel.util.StringPool;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.Duration;


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
public class BigQuerySyncTask extends GenericBean {

    private String startCursor;
    private static final Logger log = Logger.getLogger(BigQuerySyncTask.class.getName());
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    private static DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public BigQuerySyncTask() {

    }

    public BigQuerySyncTask(String cursor) {
        this.startCursor = cursor;
    }

    public String getStartCursor() {
        return startCursor;
    }

    public void setStartCursor(String startCursor) {
        this.startCursor = startCursor;
    }

    public String getQueueName() {
        return "syncqueue";
    }

    public void run() {
        ReadPolicy policy = new ReadPolicy(ReadPolicy.Consistency.STRONG);
        DatastoreServiceConfig datastoreConfig = DatastoreServiceConfig.Builder.withReadPolicy(policy);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(datastoreConfig);
        int pageSize = 20;
        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(pageSize);
        if (startCursor != null) {
            fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor));
        }

        Query q = new Query(StatementManager.STATEMENT)
                .setFilter(new Query.FilterPredicate(StatementManager.BIGQUERYSYNCSTATE,
                        Query.FilterOperator.EQUAL,
                        Statement.UNSYNCED));

        PreparedQuery pq = datastore.prepare(q);

        QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
        if (results.isEmpty()) return;

        List rowList =
                new ArrayList();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);
        try {
            for (Entity entity : results) {
                try {
                    TableDataInsertAllRequest.Rows rows = new TableDataInsertAllRequest.Rows();
                    rows.setInsertId(entity.getKey().getName());
                    TableRow tableRow = xAPItoRow(((Text) entity.getProperty("statementPayload")).getValue(), entity.getKey().getName(), (String) entity.getProperty("origin"));
                    if (tableRow!=null) {
                        rows.setJson(tableRow);
                        rowList.add(rows);
                        entity.setProperty(StatementManager.BIGQUERYSYNCSTATE, Statement.SYNCED);
                    } else {
                        entity.setProperty(StatementManager.BIGQUERYSYNCSTATE, Statement.ERROR);
                    }
                } catch (Exception e) {
                    entity.setProperty(StatementManager.BIGQUERYSYNCSTATE, Statement.ERROR);
                }

                datastore.put(entity);
            }
            TableDataInsertAllResponse response = InsertAPI.getInstance().insertRowList(rowList, Configuration.get(Configuration.BQTableId));
            txn.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            txn.rollback();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }



        String cursorString = results.getCursor().toWebSafeString();

        new BigQuerySyncTask(cursorString).scheduleTask();
    }



    public TableRow xAPItoRow(String postData, String uuid, String origin) {
        try {
            JSONObject jsonObject = new JSONObject(postData);

            JSONObject actorObject = jsonObject.getJSONObject("actor");
            long timestampLong = 0l;
            if (jsonObject.has("timestamp")) {
//                System.out.println("date from gae: "+jsonObject.getString("timestamp"));
                String timestamp = jsonObject.getString("timestamp");
                try {
                    timestampLong = df.parse(timestamp).getTime();
                } catch (ParseException e) {
                    timestampLong = df2.parse(timestamp).getTime();
                }
            }
            String actorType = actorObject.getString("objectType");
            String actorId = StringPool.BLANK;
            if (actorObject.has("account")) {
                 actorId = actorObject.getJSONObject("account").getString("name");
            } else {
                actorId = actorObject.getString("mbox");
            }

            String verbId = jsonObject.getJSONObject("verb").getString("id");
            String objectType = jsonObject.getJSONObject("object").getString("objectType");
            String objectId = jsonObject.getJSONObject("object").getString("id");
            String objectDefinition = "";
            String courseId = "todo";
            String resultResponse = StringPool.BLANK;
            double resultDuration = 0l;
            if (jsonObject.has("result")) {
                JSONObject resultObject = jsonObject.getJSONObject("result");
                if (resultObject.has("response")) {

                    resultResponse = resultObject.getString("response");

                } else if (resultObject.has("duration")) {

                    resultDuration = (double )new Duration(resultObject.getString("duration")).getStandardSeconds();

                }
            }
            Double lat = -1d;
            Double lng = -1d;
            if (jsonObject.has("context")) {
                JSONObject context = jsonObject.getJSONObject("context");
                if (context.has("extensions")) {
                    JSONObject extensions = context.getJSONObject("extensions");
                    if (extensions.has("http://activitystrea.ms/schema/1.0/place")) {
                        JSONObject pace = extensions.getJSONObject("http://activitystrea.ms/schema/1.0/place");
                        if (pace.has("geojson")) {
                            JSONObject geojson = pace.getJSONObject("geojson");
                            if (geojson.has("features")) {

                                JSONObject features = geojson.getJSONArray("features").getJSONObject(0);
                                if (features.has("geometry")) {
                                    JSONObject geometry = features.getJSONObject("geometry");
                                    if (geometry.has("coordinates")) {
                                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                                        if (coordinates.length() >= 2) {
                                            lng = Double.parseDouble("" + coordinates.get(0));
                                            lat = Double.parseDouble("" + coordinates.get(1));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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
            try {
                objectDefinition = jsonObject.getJSONObject("object").getJSONObject("definition").getString("type");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return createTableRow(uuid, timestampLong, actorType, actorId, verbId, objectType, objectId, objectDefinition, lat, lng, courseId, origin, resultResponse, resultDuration);
        } catch (JSONException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        } catch (ParseException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
        }

        System.out.println("uuid "+uuid + " failed ");

        return null;
    }

    public TableRow createTableRow(String id, long timestamp, String actorType, String actorId, String verbId, String objectType, String objectId, String objectDefinition, Double lat, Double lng, String courseId, String origin, String resultResponse, double resultDuration) throws IOException {
        TableRow row = new TableRow();
        row.set("activityId", id);
//        System.out.println("date to bigquery: "+format.format(timestamp));
        row.set("timestamp", format.format(timestamp));
        row.set("actorType", actorType);
        row.set("actorId", actorId);
        row.set("verbId", verbId);
        row.set("objectType", objectType);
        row.set("objectId", objectId);
        row.set("objectDefinition", objectDefinition);
        row.set("lat", lat);
        row.set("lng", lng);
        row.set("courseId", courseId);
        row.set("origin", origin);
        row.set("resultResponse", resultResponse);
        row.set("resultDuration", resultDuration);
        return row;
    }
}

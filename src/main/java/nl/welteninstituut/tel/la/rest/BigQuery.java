package nl.welteninstituut.tel.la.rest;


import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.AppIdentityCredential;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.model.*;
import nl.welteninstituut.tel.la.Configuration;
import nl.welteninstituut.tel.la.tasks.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by str on 28/04/15.
 */
@Path("/bigQuery")
public class BigQuery {
    private static final String SCOPE = "https://www.googleapis.com/auth/bigquery";

    private static FileDataStoreFactory dataStoreFactory;



    private static GoogleAuthorizationCodeFlow flow = null;

    private static final HttpTransport TRANSPORT = new UrlFetchTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    // @VisibleForTesting
    static Bigquery bigquery = new Bigquery(TRANSPORT, JSON_FACTORY, getRequestInitializer());



    public static Bigquery getBigQuery() {
        return bigquery;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/newTable/{tableId}")
    public String createTable(@PathParam("tableId") String tableId) throws IOException {
        TableSchema schema = new TableSchema();
        List<TableFieldSchema> tableFieldSchema = new ArrayList<TableFieldSchema>();
        TableFieldSchema activityIdSchemaEntry = new TableFieldSchema();
        activityIdSchemaEntry.setName("activityId");
        activityIdSchemaEntry.setType("STRING");

        TableFieldSchema timestampSchemaEntry = new TableFieldSchema();
        timestampSchemaEntry.setName("timestamp");
        timestampSchemaEntry.setType("TIMESTAMP");

        TableFieldSchema actorTypeSchemaEntry = new TableFieldSchema();
        actorTypeSchemaEntry.setName("actorType");
        actorTypeSchemaEntry.setType("STRING");

        TableFieldSchema actorIdSchemaEntry = new TableFieldSchema();
        actorIdSchemaEntry.setName("actorId");
        actorIdSchemaEntry.setType("STRING");

        TableFieldSchema verbIdSchemaEntry = new TableFieldSchema();
        verbIdSchemaEntry.setName("verbId");
        verbIdSchemaEntry.setType("STRING");

        TableFieldSchema objectTypeSchemaEntry = new TableFieldSchema();
        objectTypeSchemaEntry.setName("objectType");
        objectTypeSchemaEntry.setType("STRING");

        TableFieldSchema objectIdSchemaEntry = new TableFieldSchema();
        objectIdSchemaEntry.setName("objectId");
        objectIdSchemaEntry.setType("STRING");

        TableFieldSchema objectDefinitionSchemaEntry = new TableFieldSchema();
        objectDefinitionSchemaEntry.setName("objectDefinition");
        objectDefinitionSchemaEntry.setType("STRING");

        TableFieldSchema latSchemaEntry = new TableFieldSchema();
        latSchemaEntry.setName("lat");
        latSchemaEntry.setType("FLOAT");

        TableFieldSchema lngSchemaEntry = new TableFieldSchema();
        lngSchemaEntry.setName("lng");
        lngSchemaEntry.setType("FLOAT   ");

        TableFieldSchema courseIdSchemaEntry = new TableFieldSchema();
        courseIdSchemaEntry.setName("objectId");
        courseIdSchemaEntry.setType("STRING");

        tableFieldSchema.add(activityIdSchemaEntry);
        tableFieldSchema.add(timestampSchemaEntry);
        tableFieldSchema.add(actorTypeSchemaEntry);
        tableFieldSchema.add(actorIdSchemaEntry);
        tableFieldSchema.add(verbIdSchemaEntry);
        tableFieldSchema.add(objectTypeSchemaEntry);
        tableFieldSchema.add(objectIdSchemaEntry);
        tableFieldSchema.add(objectDefinitionSchemaEntry);
        schema.setFields(tableFieldSchema);

        Table table = new Table();
        table.setSchema(schema);

        TableReference tableRef = new TableReference();
        tableRef.setDatasetId(Configuration.get(Configuration.BQDataSet));
        tableRef.setProjectId(Configuration.get(Configuration.BQProject));
        tableRef.setTableId(tableId);
        table.setTableReference(tableRef);

        Bigquery.Tables.Insert insertresponse = bigquery.tables().insert(Configuration.get(Configuration.BQProject), Configuration.get(Configuration.BQDataSet), table);
//        insertStatement(tableId, "12345", System.currentTimeMillis(), "Agent" , "stefaan.ternier", "read", "object", "oId", "def");


        return "{'ok':'"+insertresponse.getDatasetId()+"'}";
    }

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/{tableId}/{id}")
    public String testInsert(@PathParam("tableId") String tableId, @PathParam("id") String id) throws IOException, InterruptedException {
        insertStatement(tableId, id, System.currentTimeMillis(), "Agent" , "stefaan.ternier", "read", "object", "oId", "def", 52.23d,13.29d, "courseId");

//        for (TableDataInsertAllResponse.InsertErrors error: response.getInsertErrors()){
//            System.out.println(error);
//        }
//        bigquery.datasets().
//        for (Bigquery.Datasets dataSets: bigquery.datasets()){
//
//
//        }
        return "{'ok':'true'}";

    }

    public void insertStatement(String tableId, String id, long timestamp, String actorType, String actorId, String verbId, String objectType, String objectId, String objectDefinition, Double lat, Double lng, String courseId) throws IOException {
        TableRow row = new TableRow();
        row.set("activityId", id);

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

        TableDataInsertAllRequest.Rows rows = new TableDataInsertAllRequest.Rows();
        rows.setInsertId(id);
        rows.setJson(row);
        List rowList =
                new ArrayList();
        rowList.add(rows);
        TableDataInsertAllRequest content =
                new TableDataInsertAllRequest().setRows(rowList);
        TableDataInsertAllResponse response =
                bigquery.tabledata().insertAll(
                        Configuration.get(Configuration.BQProject),
                        Configuration.get(Configuration.BQDataSet),
                        tableId, content).execute();
//        System.out.println(response.getInsertErrors());

    }

//    public static Bigquery createAuthorizedClient() throws IOException {
//
//        Credential credential = authorize();
//        return new Bigquery(TRANSPORT, JSON_FACTORY, credential);
//    }



    private static HttpRequestInitializer getRequestInitializer() {
        if (System.getProperty("OAUTH_ACCESS_TOKEN") != null) {
            return new GoogleCredential().setAccessToken(System.getProperty("OAUTH_ACCESS_TOKEN"));
        }
        return new AppIdentityCredential(Arrays.asList(new String[] {SCOPE}));
    }


    public void query(String userId) {
        String query = "SELECT timestamp FROM [" + Configuration.get(Configuration.BQDataSet) + "." + Configuration.get(Configuration.BQTableId) + "] where actorId = '" + userId + "'";
        new QueryTask(genericQuery(query), userId).scheduleTask();

    }

    public void queryCourse(String courseId) {
        String query = "SELECT timestamp FROM [" + Configuration.get(Configuration.BQDataSet) + "." + Configuration.get(Configuration.BQTableId) + "] where objectId contains  '" + courseId + "'";
        new CourseQueryTask(genericQuery(query), courseId).scheduleTask();
    }

    public String genericQuery(String query) {
        try {
            Job job = new Job();
            JobConfiguration config = new JobConfiguration();
            JobConfigurationQuery queryConfig = new JobConfigurationQuery();
            config.setQuery(queryConfig);

            job.setConfiguration(config);
            queryConfig.setQuery(query);
            Bigquery.Jobs.Insert insert = bigquery.jobs().insert(Configuration.get(Configuration.BQProject), job);
            insert.setProjectId(Configuration.get(Configuration.BQProject));
            return insert.execute().getJobReference().getJobId();
        } catch (IOException e) {

        }
        return null;
    }

    public void averageActivityPerLearner(String courseId) {
        String query = "SELECT actorId, count(*)  FROM [" + Configuration.get(Configuration.BQDataSet) + "." + Configuration.get(Configuration.BQTableId) + "] where objectId contains  '" + courseId + "' group by actorId";
        new ActivityPerLearnerQueryTask(genericQuery(query), courseId).scheduleTask();
    }

    public void courseLoginOverview() {
        String query = "SELECT objectId , count(*) FROM " +getTable()+"   where verbId = \"http://adlnet.gov/expapi/verbs/launched\" group by objectId";
        new CourseLoginOverviewQueryTask(genericQuery(query)).scheduleTask();
    }

    public void courseActivitiesOverview(String courseId){
        String query = "SELECT verbId, count(*) as count  FROM "+getTable()+"  where objectId contains \""+courseId+"\" group by verbId";
        new CourseActivitiesOverviewQueryTask(genericQuery(query), courseId).scheduleTask();


    }

    private String getTable() {
        return "[" + Configuration.get(Configuration.BQDataSet) + "." + Configuration.get(Configuration.BQTableId) + "]";
    }
}

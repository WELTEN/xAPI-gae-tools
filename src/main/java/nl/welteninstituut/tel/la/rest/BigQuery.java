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
import nl.welteninstituut.tel.la.bigquery.InsertAPI;
import nl.welteninstituut.tel.la.bigquery.QueryAPI;
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

        Bigquery.Tables.Insert insertresponse = InsertAPI.getInstance().insertTable(table);
//        insertStatement(tableId, "12345", System.currentTimeMillis(), "Agent" , "stefaan.ternier", "read", "object", "oId", "def");


        return "{'ok':'"+insertresponse.getDatasetId()+"'}";
    }

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/{tableId}/{id}")
    public String testInsert(@PathParam("tableId") String tableId, @PathParam("id") String id) throws IOException, InterruptedException {
        insertStatement(tableId, id, System.currentTimeMillis(), "Agent", "stefaan.ternier", "read", "object", "oId", "def", 52.23d, 13.29d, "courseId");
        return "{'ok':'true'}";

    }

    public void insertStatement(String tableId, String id, long timestamp, String actorType, String actorId, String verbId, String objectType, String objectId, String objectDefinition, Double lat, Double lng, String courseId) throws IOException {
        TableRow row = createTableRow(id,timestamp, actorType, actorId, verbId, objectType, objectId, objectDefinition, lat, lng, courseId);

        TableDataInsertAllRequest.Rows rows = new TableDataInsertAllRequest.Rows();
        rows.setInsertId(id);
        rows.setJson(row);
        List rowList =
                new ArrayList();
        rowList.add(rows);
        InsertAPI.getInstance().insertRowList(rowList, tableId);
    }

    public TableRow createTableRow(String id, long timestamp, String actorType, String actorId, String verbId, String objectType, String objectId, String objectDefinition, Double lat, Double lng, String courseId) throws IOException {
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

        return row;
    }

    public void query(String userId) {
        String query = "SELECT timestamp FROM [" + Configuration.get(Configuration.BQDataSet) + "." + Configuration.get(Configuration.BQTableId) + "] where actorId = '" + userId + "'";
        new QueryTask(QueryAPI.getInstance().createQueryJob(query), userId).scheduleTask();

    }

//    public void queryCourse(String courseId) {
//        String query = "SELECT timestamp FROM [" + Configuration.get(Configuration.BQDataSet) + "." + Configuration.get(Configuration.BQTableId) + "] where courseId =  '" + courseId + "'";
//        new CourseQueryTask(QueryAPI.getInstance().createQueryJob(query), courseId).scheduleTask();
//    }

    public void averageActivityPerLearner(String courseId) {
        String query = "SELECT actorId, count(*)  FROM [" + Configuration.get(Configuration.BQDataSet) + "." + Configuration.get(Configuration.BQTableId) + "] where courseId =  '" + courseId + "' group by actorId";
        new ActivityPerLearnerQueryTask(QueryAPI.getInstance().createQueryJob(query), courseId).scheduleTask();
    }

    public void courseLoginOverview() {
        String query = "SELECT objectId , count(*) FROM " +getTable()+"   where verbId = \"http://adlnet.gov/expapi/verbs/launched\" group by objectId";
        new CourseLoginOverviewQueryTask(QueryAPI.getInstance().createQueryJob(query)).scheduleTask();
    }


    public void dropoutMonitor(String cacheKey) {
        String query = "SELECT a.courseId, a.statements, b.launched, b.users\n" +
                "FROM\n" +
                " (SELECT courseId, count(*) as statements\n" +
                " FROM "+getTable()+" where courseId != 'todo' and courseId != ''\n" +
                " GROUP BY courseId\n" +
                " ) a \n" +
                " JOIN (\n" +
                "   SELECT c.objectId as object, c.launched as launched, d.users as users FROM (\n" +
                "    SELECT f.objectId, count(*) as launched\n" +
                "    FROM "+getTable()+" f where verbId = 'http://adlnet.gov/expapi/verbs/launched' group by f.objectId\n" +
                "   ) c JOIN (\n" +
                "     SELECT courseId, count(distinct actorId) as users\n" +
                "     FROM "+getTable()+"  group by courseId\n" +
                "   ) d on c.objectId = d.courseId\n" +
                "    \n" +
                " ) b ON a.courseId = b.object";
        System.out.println("query "+query);
        new DropoutMonitorTask(QueryAPI.getInstance().createQueryJob(query),  cacheKey).scheduleTask();

    }
    public void langDistribution() {
        String query = "SELECT lang, amount FROM (SELECT count(*) AS amount, \"de\" AS lang FROM [xAPIStatements.xapiTable] where (courseId contains \"de.humance.education:80286\") OR (courseId contains \"de.humance.education:50505\") OR (courseId contains \"eu.ecolearning.hub0:11\") OR (courseId contains \"eu.ecolearning.hub11:7\")),(SELECT count(*) AS amount, \"it\" AS lang FROM [xAPIStatements.xapiTable] where (courseId contains \"it.polimi.pok:course-v1:Polimi+MAT101+2016_M8\") OR (courseId contains \"eu.ecolearning.hub0:11\") OR (courseId contains \"eu.ecolearning.hub11:7\") OR (courseId contains \"eu.ecolearning.hub10b:13\")),(SELECT count(*) AS amount, \"pt\" AS lang FROM [xAPIStatements.xapiTable] where (courseId contains \"eu.ecolearning.hub4:12\") OR (courseId contains \"pt.uab.imooc.eco:course_15_e-skills-E\") OR (courseId contains \"eu.ecolearning.hub11:22\") OR (courseId contains \"eu.ecolearning.hub0:11\") OR (courseId contains \"eu.ecolearning.hub4:14\") OR (courseId contains \"pt.uab.imooc.eco:course_14_eteach002\") OR (courseId contains \"pt.uab.imooc.eco:course_13_eteach001\") OR (courseId contains \"eu.ecolearning.hub11:7\")),(SELECT count(*) AS amount, \"fr\" AS lang FROM [xAPIStatements.xapiTable] where (courseId contains \"eu.ecolearning.hub5:2\") OR (courseId contains \"eu.ecolearning.hub5:1\") OR (courseId contains \"eu.ecolearning.hub0:11\") OR (courseId contains \"eu.ecolearning.hub11:27\") OR (courseId contains \"eu.ecolearning.hub11:7\") OR (courseId contains \"eu.ecolearning.hub11:24\") OR (courseId contains \"eu.ecolearning.hub11:8\")),(SELECT count(*) AS amount, \"en\" AS lang FROM [xAPIStatements.xapiTable] where (courseId contains \"it.polimi.pok:course-v1:Polimi+FC101+2016_M9\") OR (courseId contains \"eu.ecolearning.hub5:1\") OR (courseId contains \"it.polimi.pok:course-v1:Polimi+WMT101+2016_M9\") OR (courseId contains \"eu.ecolearning.hub8:58\") OR (courseId contains \"eu.ecolearning.hub0:11\") OR (courseId contains \"eu.ecolearning.hub2:12\") OR (courseId contains \"eu.ecolearning.hub11:7\") OR (courseId contains \"eu.ecolearning.hub11:65\")),(SELECT count(*) AS amount, \"es\" AS lang FROM [xAPIStatements.xapiTable] where (courseId contains \"eu.ecolearning.hub8:7\") OR (courseId contains \"eu.ecolearning.hub8:8\") OR (courseId contains \"eu.ecolearning.hub8:10\") OR (courseId contains \"eu.ecolearning.hub8:20\") OR (courseId contains \"eu.ecolearning.hub8:59\") OR (courseId contains \"eu.ecolearning.hub11:3\") OR (courseId contains \"eu.ecolearning.hub11:9\") OR (courseId contains \"eu.ecolearning.hub11:14\") OR (courseId contains \"eu.ecolearning.hub8:57\") OR (courseId contains \"eu.ecolearning.hub8:62\") OR (courseId contains \"eu.ecolearning.hub11:1\") OR (courseId contains \"eu.ecolearning.hub11:21\") OR (courseId contains \"eu.ecolearning.hub8:9\") OR (courseId contains \"eu.ecolearning.hub0:11\") OR (courseId contains \"eu.ecolearning.hub8:61\") OR (courseId contains \"eu.ecolearning.hub11:25\") OR (courseId contains \"eu.ecolearning.hub11:5\") OR (courseId contains \"eu.ecolearning.hub11:12\") OR (courseId contains \"eu.ecolearning.hub11:34\") OR (courseId contains \"eu.ecolearning.hub11:36\") OR (courseId contains \"eu.ecolearning.hub11:37\") OR (courseId contains \"eu.ecolearning.hub11:32\") OR (courseId contains \"eu.ecolearning.hub11:7\") OR (courseId contains \"eu.ecolearning.hub1:27\") OR (courseId contains \"eu.ecolearning.hub11:35\") OR (courseId contains \"eu.ecolearning.hub11:38\") OR (courseId contains \"eu.ecolearning.hub11:39\") OR (courseId contains \"eu.ecolearning.hub7:8\") OR (courseId contains \"eu.ecolearning.hub11:54\") OR (courseId contains \"eu.ecolearning.hub11:45\") OR (courseId contains \"eu.ecolearning.hub11:53\") OR (courseId contains \"eu.ecolearning.hub11:48\") OR (courseId contains \"eu.ecolearning.hub11:50\") OR (courseId contains \"eu.ecolearning.hub11:47\") OR (courseId contains \"eu.ecolearning.hub11:49\") OR (courseId contains \"eu.ecolearning.hub11:41\") OR (courseId contains \"eu.ecolearning.hub11:43\") OR (courseId contains \"eu.ecolearning.hub11:59\") OR (courseId contains \"eu.ecolearning.hub11:63\") OR (courseId contains \"eu.ecolearning.hub11:61\") OR (courseId contains \"eu.ecolearning.hub11:62\") OR (courseId contains \"eu.ecolearning.hub11:55\") OR (courseId contains \"eu.ecolearning.hub11:60\") OR (courseId contains \"eu.ecolearning.hub11:67\") OR (courseId contains \"eu.ecolearning.hub11:56\") OR (courseId contains \"eu.ecolearning.hub11:18\") OR (courseId contains \"eu.ecolearning.hub11:57\") OR (courseId contains \"eu.ecolearning.hub11:64\") OR (courseId contains \"eu.ecolearning.hub11:65\") OR (courseId contains \"eu.ecolearning.hub11:51\") OR (courseId contains \"eu.ecolearning.hub11:58\"));";
        new LangDistributionTask(QueryAPI.getInstance().createQueryJob(query), "langDistribution").scheduleTask();

    }


    public void courseActivitiesOverview(String courseId){
        String query = "SELECT verbId, count(*) as count  FROM "+getTable()+"  where courseId = \""+courseId+"\" group by verbId";
        new CourseActivitiesOverviewQueryTask(QueryAPI.getInstance().createQueryJob(query), courseId).scheduleTask();
    }

    public void mesoResourceTypesCourse(String courseId, String cacheKey){
        String query = "SELECT objectDefinition, count(*) as count  FROM "+getTable()+"  where courseId = \""+courseId+"\" group by objectDefinition";
        new MesoResourceTypesCourseTask(QueryAPI.getInstance().createQueryJob(query), cacheKey).scheduleTask();
    }

    public void resourceTypesCourse(String courseId, String userId, String cacheKey){
        String query = "SELECT objectDefinition, count(*) as count  FROM "+getTable()+"  where actorId = \""+userId+"\" and courseId = \""+courseId+"\" group by objectDefinition";
        new MesoResourceTypesCourseTask(QueryAPI.getInstance().createQueryJob(query), cacheKey).scheduleTask();
    }

    public void queryLogins(String userId) {
        String query = null;
        if (userId == null){
            query = "SELECT  STRFTIME_UTC_USEC(timestamp(timestamp) , '%Y,%m,%d') as day, count(*) as amount FROM [" + Configuration.get(Configuration.BQDataSet) + "." + Configuration.get(Configuration.BQTableId) + "] where verbId = 'https://brindlewaye.com/xAPITerms/verbs/loggedin' GROUP BY day ORDER BY day DESC";
        } else {
            query = "SELECT  STRFTIME_UTC_USEC(timestamp(timestamp) , '%Y,%m,%d') as day, count(*) as amount FROM [" + Configuration.get(Configuration.BQDataSet) + "." + Configuration.get(Configuration.BQTableId) + "] where verbId = 'https://brindlewaye.com/xAPITerms/verbs/loggedin' and actorId = '"+userId+"' GROUP BY day ORDER BY day DESC";
        }
        new CalendarLoginQT(QueryAPI.getInstance().createQueryJob(query),userId).scheduleTask();
    }

    public void queryCourseProgress(String courseId){
        String query = "SELECT  objectId, count(*) as count, FORMAT_UTC_USEC((integer(avg(timestamp)))) as date " +
                "FROM " +getTable()+"   where " +
                "courseId = '"+courseId+"' and not  objectId  contains \"?\" and not objectDefinition  contains \"discussion\" and not objectDefinition  contains \"forummessage\" group by objectId order by date asc";
        new ProgressQT(QueryAPI.getInstance().createQueryJob(query),null,courseId).scheduleTask();

    }

    public void queryCourseProgressUser(String courseId, String userId){

        String query = "SELECT  objectId, count(*) as count, FORMAT_UTC_USEC((integer(avg(timestamp)))) as date " +
                "FROM " +getTable()+"   where " +
                "actorId = '"+userId+"' and courseId = '"+courseId+"' and not  objectId  contains \"?\" and not objectDefinition  contains \"discussion\" and not objectDefinition  contains \"forummessage\" group by objectId order by date asc";
        new ProgressQT(QueryAPI.getInstance().createQueryJob(query),userId,courseId).scheduleTask();
    }

    public void queryCourseActivities(String courseId){

        String query = "SELECT STRFTIME_UTC_USEC(timestamp(timestamp), '%Y,%m,%d') as day, count(*)  " +
                "FROM " +getTable()+"   where " +
                " courseId = '"+courseId+"' group by day order by day";
        System.out.println("query: "+query);
        new CourseQueryTask(QueryAPI.getInstance().createQueryJob(query), courseId).scheduleTask();
    }

    public void queryCourseThreadOpeningMessages(String courseId){

        String query = "SELECT STRFTIME_UTC_USEC(timestamp(timestamp), '%Y,%m,%d') as day, count(*)  " +
                "FROM " +getTable()+"   where " +
                " courseId = '"+courseId+"' and objectDescription = 'This is a thread-opening forum message'  group by day order by day";
        new CourseQueryTask(QueryAPI.getInstance().createQueryJob(query), courseId).scheduleTask();
    }

    public void interactivitySort(String courseId,String cacheKey){
        String query = "SELECT count(*) as count  FROM "+getTable()+"  where courseId = \""+courseId+"\" group by actorId order by count asc";
        new InteractivitySortQT(QueryAPI.getInstance().createQueryJob(query), cacheKey).scheduleTask();
    }

    public void interactivitySort(String courseId,String cacheKey, String userId){
        String query = "SELECT count(*) as count  FROM "+getTable()+"  where courseId = \""+courseId+"\" and actorId = \""+userId+"\" group by actorId order by count asc";
        new InteractivitySortQT(QueryAPI.getInstance().createQueryJob(query), cacheKey).scheduleTask();
    }

    public void studentPaths(String courseId,String cacheKey){ //(actorid='5708559163d1117f4a0c9b54'|| actorId="56655a9906666ea27fd4b205") and
        String query = "SELECT a.actorId as actorId, a.objectId as objectId,a.objectDefinition as objectDefinition, a.verbId as verbId, ROUND((( a.firstAccess - b.firstAccess)+1)/(7*24 * 3600 * 1000000))  as RelativeTime " +
                "FROM (SELECT actorId, objectId, objectDefinition, verbId, min(timestamp) as firstAccess,     FROM  "+getTable()+" where      " +
                "courseId=\""+courseId+"\"  group by actorId, objectId, objectDefinition, verbId order by firstAccess asc) a cross join (SELECT actorId, min(timestamp) as firstAccess " +
                "FROM  "+getTable()+" where courseId=\""+courseId+"\" group by actorId)  b WHERE a.actorId = b.actorId ORDER BY a.actorId, RelativeTime";
        System.out.println(query);
        new StudentPathsQT(QueryAPI.getInstance().createQueryJob(query), cacheKey).scheduleTask();
    }

    public void socialFollows(String cacheKey){
        String query = "SELECT actorId, objectAccountId FROM "+getTable()+" where not( objectAccountId == '') and verbId = 'http://activitystrea.ms/schema/1.0/follow'";
        new SocialFollowsQT(QueryAPI.getInstance().createQueryJob(query), cacheKey).scheduleTask();
    }

    public void socialFollows(String cacheKey, String courseId){
        String query = "SELECT actorId, objectAccountId FROM "+getTable()+" where not( objectAccountId == '') and verbId = 'http://activitystrea.ms/schema/1.0/follow' and actorId in (select actorId  from [xAPIStatements.xapiTable] where courseId = '"+courseId+"' and objectAccountId in (select actorId  from [xAPIStatements.xapiTable] where courseId = '"+courseId+"'))";
        new SocialFollowsQT(QueryAPI.getInstance().createQueryJob(query), cacheKey).scheduleTask();
    }

    private String getTable() {
        return "[" + Configuration.get(Configuration.BQDataSet) + "." + Configuration.get(Configuration.BQTableId) + "]";
    }

    public void timeline(String cacheKey, String userId){
//        String query = "SELECT  verbId, objectDefinition, courseId, objectDefinitionName, objectDescription, FORMAT_UTC_USEC(timestamp) as timestamp , objectId, origin, objectAccountId, activityId FROM "+getTable()+" where  origin = 'OpenMOOC' and not (verbId = 'https://brindlewaye.com/xAPITerms/verbs/loggedin') order by timestamp desc limit 200";

        String query = "SELECT  verbId, objectDefinition, courseId, objectDefinitionName, objectDescription, FORMAT_UTC_USEC(timestamp) as timestamp , objectId, origin, objectAccountId, activityId  FROM "+getTable()+" where actorId = '"+userId+"' and (origin = 'OpenMOOC' or origin = 'Portal') and not (verbId = 'https://brindlewaye.com/xAPITerms/verbs/loggedin') order by timestamp desc limit 50";
        new TimeLineQT(QueryAPI.getInstance().createQueryJob(query), cacheKey).scheduleTask();
    }

    public void allCourseActivities(String cacheKey){

        String query = "SELECT STRFTIME_UTC_USEC(timestamp(timestamp), '%Y,%m,%d') as day, courseId, count(*)  " +
                "FROM " +getTable()+"   where courseId != 'todo' and courseId != ''" +
                " group by day, courseId order by day";
        System.out.println("query: "+query);
        new CalendarAnnotationChartTask(QueryAPI.getInstance().createQueryJob(query), cacheKey).scheduleTask();
//        new CourseQueryTask(QueryAPI.getInstance().createQueryJob(query), courseId).scheduleTask();
    }

    public void rob(String cacheKey, String courseId){

        String query = "SELECT STRFTIME_UTC_USEC(timestamp(date), '%Y,%m,%d') as day, count(*)  " +
                "FROM " +getTable()+"   where " +
                " OBJECT_ID = '"+courseId+"' group by day order by day";
        new CourseQueryTask(QueryAPI.getInstance().createQueryJob(query), courseId).scheduleTask();
    }

    public void rob(String cacheKey){

        String query = "SELECT STRFTIME_UTC_USEC(timestamp(date), '%Y,%m,%d') as day, count(*)  " +
                "FROM " +getTable()+"   group by day order by day";
        new CourseQueryTask(QueryAPI.getInstance().createQueryJob(query), "all").scheduleTask();
    }
}

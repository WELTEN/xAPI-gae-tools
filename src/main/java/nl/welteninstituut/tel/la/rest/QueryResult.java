package nl.welteninstituut.tel.la.rest;

import nl.welteninstituut.tel.la.chartobjects.DropOutObject;
import nl.welteninstituut.tel.la.chartobjects.LearnerAverageActivities;
import nl.welteninstituut.tel.la.jdomanager.*;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

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
@Path("/query/result")
public class QueryResult extends Service {

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/calendar/user/{userId}")
    public String getResult(@HeaderParam("Authorization") String token,
                            @PathParam("userId") String userId) throws IOException {
//        if (!validCredentials(token))
//            return getInvalidCredentialsBean();
//        userId = "563a25f842e5f1740c5ff68d";
        return UserDateToVerbManager.getUserActivities(userId).toString();
//        return QueryCacheManager.getQueryResult("calendar_"+userId);

    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/calendar/user/{userId}/gdata")
    public String getResultGdataUserParam(@HeaderParam("Authorization") String token,
                            @PathParam("userId") String userId) throws IOException {
        if (!validCredentials(token))
            return getInvalidCredentialsBean();
//        userId = "563a25f842e5f1740c5ff68d";
        return UserDateToVerbManager.getUserActivitiesGData(userId, "all").toString();
//        return QueryCacheManager.getQueryResult("calendar_"+userId);

    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/calendar/user/gdata")
    public String getResultGdata(@HeaderParam("Authorization") String token) throws IOException {
        if (!validCredentials(token))
            return getInvalidCredentialsBean();
        return UserDateToVerbManager.getUserActivitiesGData(userId, "all").toString();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/calendar/course/{courseId}")
    public String getCalendarCourse(@PathParam("courseId") String courseId) throws IOException {

        return CourseDateToVerbManager.getCourseDateGData(courseId, "all");
//        return "{\"cols\": [{\"id\": \"Date\",\"label\": \"Date\",\n" +
//                "\t\t\"type\": \"date\"\n" +
//                "\t}, {\n" +
//                "\t\t\"id\": \"Logins\",\n" +
//                "\t\t\"label\": \"Logins\",\n" +
//                "\t\t\"type\": \"number\"\n" +
//                "\t}],\n" +
//                "\t\"rows\": []}";
    }






    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/averageLearnerActivities/{courseId}")
    public String averageActivityPerLearnerResult(@HeaderParam("Authorization") String token,
                                                  @PathParam("courseId") String courseId) throws IOException {
        if (!validCredentials(token))
            return getInvalidCredentialsBean();
        try {
            return new LearnerAverageActivities(new JSONObject(QueryCacheManager.getQueryResult("activity_per_learner_"+courseId))).getBarChartObject(userId).toJsonObject().toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";


    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/resourceTypes/course/{courseId}/gdata")
    public String getResoucesUsed(@HeaderParam("Authorization") String token,
                                  @PathParam("courseId") String courseId) throws IOException {
//        if (!validCredentials(token))
//            return getInvalidCredentialsBean();
        userId = "5458df628a24efd87e90ad00";
        System.out.println("userId "+userId);
        return CourseDateToObjectDefinitionManager.getResourcesCommittedGData(courseId, userId,  "all");
//        return "{\n" +
//                "\t\"cols\": [{\n" +
//                "\t\t\"id\": \"Average all learners in the course\",\n" +
//                "\t\t\"label\": \"Resources committed\",\n" +
//                "\t\t\"type\": \"string\"\n" +
//                "\t}, {\n" +
//                "\t\t\"id\": \"http://activitystrea.ms/schema/1.0/task\",\n" +
//                "\t\t\"label\": \"task\",\n" +
//                "\t\t\"type\": \"number\"\n" +
//                "\t}, {\n" +
//                "\t\t\"id\": \"http://activitystrea.ms/schema/1.0/video\",\n" +
//                "\t\t\"label\": \"video\",\n" +
//                "\t\t\"type\": \"number\"\n" +
//                "\t}, {\n" +
//                "\t\t\"id\": \"http://www.ecolearning.eu/expapi/activitytype/syllabus\",\n" +
//                "\t\t\"label\": \"syllabus\",\n" +
//                "\t\t\"type\": \"number\"\n" +
//                "\t}],\n" +
//                "\t\"rows\": [{\n" +
//                "\t\t\"c\": [{\n" +
//                "\t\t\t\"v\": \"Resources\"\n" +
//                "\t\t}, {\n" +
//                "\t\t\t\"v\": \"1\"\n" +
//                "\t\t}, {\n" +
//                "\t\t\t\"v\": \"1\"\n" +
//                "\t\t}, {\n" +
//                "\t\t\t\"v\": \"1\"\n" +
//                "\t\t}]\n" +
//                "\t}]\n" +
//                "}";
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/resourceTypes/course/{courseId}/{userId}/gdata")
    public String getResoucesUsed(@HeaderParam("Authorization") String token,
                                  @PathParam("courseId") String courseId,@PathParam("userId") String actor) throws IOException {

        return CourseDateToObjectDefinitionManager.getResourcesCommittedGData(courseId, actor,  "all");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/dropoutMonitor")
    public String getDropoutMonitor() throws IOException {

            return QueryCacheManager.getQueryResult("dropoutMonitor");

//        return QueryCacheManager.getQueryResult("course_login_overview");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/dropoutMonitor/{courseId}")
    public String dropoutMonitorForCourse(@PathParam("courseId") final String courseId) throws IOException {
        return QueryCacheManager.getQueryResult("course_activities_"+courseId);
    }
}

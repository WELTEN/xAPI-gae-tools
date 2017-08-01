package nl.welteninstituut.tel.la.rest;

import nl.welteninstituut.tel.la.chartobjects.DropOutObject;
import nl.welteninstituut.tel.la.chartobjects.LearnerAverageActivities;
import nl.welteninstituut.tel.la.jdomanager.*;
import org.codehaus.jettison.json.JSONArray;
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
        return QueryCacheManager.getQueryResult("calendar_course_"+courseId);
        //return CourseDateToVerbManager.getCourseDateGData(courseId, "all");
    }

    @GET
    @Produces({"application/csv" })
    @Path("/calendar/course/{courseId}/csv")
    public String getCalendarCourseCSV(@PathParam("courseId") String courseId) throws IOException {
        return QueryCacheManager.getQueryResult("calendar_course_"+courseId+"_csv");

    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/calendar/course/threadopening/{courseId}")
    public String getCalendarCourseThreadOpening(@PathParam("courseId") String courseId) throws IOException {
        return QueryCacheManager.getQueryResult("calendar_course_threadopening_"+courseId);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/calendar/logins/gdata")
    public String getCalendarLogins() throws IOException {
        return QueryCacheManager.getQueryResult("calendar_logins");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/calendar/logins/user/gdata")
    public String getCalendarLogins(@HeaderParam("Authorization") String token) throws IOException {
        if (!validCredentials(token))
            return getInvalidCredentialsBean();
        return QueryCacheManager.getQueryResult("calendar_logins_"+userId);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/calendar/course/{courseId}/d3")
    public String getCalendarCourseD3(@PathParam("courseId") String courseId) throws IOException {
        //return CourseDateToVerbManager.getCourseDateGData(courseId, "all");
        //todo
        return "";
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
    @Path("/resourceTypes/course/{courseId}/d3")
    public String getResoucesUsed(@HeaderParam("Authorization") String token,
                                  @PathParam("courseId") String courseId) throws IOException {
        if (!validCredentials(token))
            return getInvalidCredentialsBean();
//        userId = "549986a4cd35f8064e81cf12";
//        userId = "562bea850213f05409423ea8";
        final String cacheKey = "resourceTypes_"+courseId+"_"+userId;
        return QueryCacheManager.getQueryResult(cacheKey);

//        return CourseDateToObjectDefinitionManager.getResourcesCommittedD3(courseId, userId, "all");

    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/resourceTypes/course/{courseId}/gdata")
    public String getResoucesUsedGdata(@HeaderParam("Authorization") String token,
                                  @PathParam("courseId") String courseId) throws IOException {
        if (!validCredentials(token))
            return getInvalidCredentialsBean();
//        userId = "5561fcf53a8538f429d6aba2";
        return CourseDateToObjectDefinitionManager.getResourcesCommittedGData(courseId, userId,  "all");
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
    @Path("/dropoutMonitorLang")
    public String getDropoutMonitorLang() throws IOException {

        return QueryCacheManager.getQueryResult("dropoutMonitor_lang");

//        return QueryCacheManager.getQueryResult("course_login_overview");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/langDistribution")
    public String languageDistribution(@HeaderParam("Authorization") String token) throws IOException {
        return QueryCacheManager.getQueryResult("langDistribution");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/dropoutMonitor/{courseId}")
    public String dropoutMonitorForCourse(@PathParam("courseId") final String courseId) throws IOException {
        return QueryCacheManager.getQueryResult("course_activities_"+courseId);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/resourceTypes/meso/course/{courseId}/d3")
    public String queryResoucesUsed(@PathParam("courseId") final String courseId) throws IOException {
        return QueryCacheManager.getQueryResult("resourceTypes_meso_"+courseId);
    }

    @GET
    @Produces({"application/csv"})
    @Path("/resourceTypes/meso/course/{courseId}/d3/csv")
    public String queryResoucesUsedCSV(@PathParam("courseId") final String courseId) throws IOException {
        return QueryCacheManager.getQueryResult("resourceTypes_meso_"+courseId+"_csv");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/progress/{courseId}/gdata")
    public String getProgress(@HeaderParam("Authorization") String token,
                              @PathParam("courseId") String courseId) throws IOException {
        if (!validCredentials(token))
            return getInvalidCredentialsBean();
        String course = QueryCacheManager.getQueryResult("progress_"+courseId);
        if (course.equals("{}")) return course;
        String user = QueryCacheManager.getQueryResult("progress_"+courseId+"_"+userId);
        if (user.equals("{}")) return course;
        try {
            System.out.println(course);
            JSONArray courseArray = new JSONArray(course);
            JSONArray userArray = new JSONArray(user);
            int progress = 0;
            if (courseArray.length()!=0) {
                progress=  ( userArray.length()  *100)  /courseArray.length();
            }
            return "{\"cols\":[{\"label\":\"City\",\"type\":\"string\"},{\"label\":\"Progress\",\"type\":\"number\"}],\"rows\":[{\"c\":[{\"v\":\"You\"},{\"v\":"+progress+"}]}]}";
        } catch (JSONException e) {

    e.printStackTrace();
        }
        return "{\"cols\":[{\"label\":\"City\",\"type\":\"string\"},{\"label\":\"Progress\",\"type\":\"number\"}],\"rows\":[{\"c\":[{\"v\":\"You\"},{\"v\":66}]}]}";
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/interactivitySort/{courseId}/gdata")
    public String interactivitySort(@PathParam("courseId") final String courseId) throws IOException {
        final String cacheKey = "interactivitySort_"+courseId;
        return QueryCacheManager.getQueryResult(cacheKey);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/interactivitySort/{courseId}/me/gdata")
    public String interactivitySortMe(@HeaderParam("Authorization") String token,
                                      @PathParam("courseId") final String courseId) throws IOException {
        if (!validCredentials(token))
            return getInvalidCredentialsBean();
        final String cacheKey = "interactivitySort_"+courseId+"_"+userId;
        return QueryCacheManager.getQueryResult(cacheKey);
    }


    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/studentPaths/{courseId}/{detail}")
    public String studentPaths(@HeaderParam("Authorization") String token,
                               @PathParam("courseId") final String courseId,
                               @PathParam("detail") final String detail) throws IOException {
        final String cacheKey = "studentPaths_"+courseId;
        return QueryCacheManager.getQueryResult(cacheKey+"_"+detail);

    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/studentPaths/{courseId}/me")
    public String studentPaths(@HeaderParam("Authorization") String token,
                               @PathParam("courseId") final String courseId
                               ) throws IOException {
        if (!validCredentials(token))
            return getInvalidCredentialsBean();
        final String cacheKey = "studentPaths_"+courseId;
        return QueryCacheManager.getQueryResult(cacheKey+"_"+userId);

    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/social/follows")
    public String followers() throws IOException {
        final String cacheKey = "socialFollows";
        return QueryCacheManager.getQueryResult(cacheKey);
    }


    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/social/{courseId}/follows")
    public String courseFollowers(@PathParam("courseId") final String courseId) throws IOException {
        final String cacheKey = "socialFollows_"+courseId;
        return QueryCacheManager.getQueryResult(cacheKey);
    }

    @GET
    @Produces({"application/csv"})
    @Path("/social/{courseId}/follows/csv")
    public String courseFollowersCSV(@PathParam("courseId") final String courseId) throws IOException {
        final String cacheKey = "socialFollows_"+courseId+"_csv";
        return QueryCacheManager.getQueryResult(cacheKey);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/timeline")
    public String timeLineUser(@HeaderParam("Authorization") String token) throws IOException {
        if (!validCredentials(token))
            return getInvalidCredentialsBean();
        final String cacheKey = "timeline_"+userId;
        return QueryCacheManager.getQueryResult(cacheKey);
    }
    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/allCourseActivities")
    public String allCourseActivities(String cacheKey){

        return QueryCacheManager.getQueryResult("allCourseActivities");
    }


    }

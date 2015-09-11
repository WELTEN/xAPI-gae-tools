package nl.welteninstituut.tel.la.rest;

import nl.welteninstituut.tel.la.chartobjects.DropOutObject;
import nl.welteninstituut.tel.la.chartobjects.LearnerAverageActivities;
import nl.welteninstituut.tel.la.jdomanager.QueryCacheManager;
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
    @Path("/calendar/user/")
    public String getResult(@HeaderParam("Authorization") String token) throws IOException {
        if (!validCredentials(token))
            return getInvalidCredentialsBean();
        return QueryCacheManager.getQueryResult("calendar_"+userId);

    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/calendar/course/{courseId}")
    public String getCalendarCourse(@PathParam("courseId") String courseId) throws IOException {
        return QueryCacheManager.getQueryResult("calendar_course_" + courseId);

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
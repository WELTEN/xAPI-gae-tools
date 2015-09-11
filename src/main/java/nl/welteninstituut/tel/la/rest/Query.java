package nl.welteninstituut.tel.la.rest;

import nl.welteninstituut.tel.la.chartobjects.LearnerAverageActivities;
import nl.welteninstituut.tel.la.jdomanager.QueryCacheManager;
import nl.welteninstituut.tel.la.tasks.DropoutMonitorAggregationTask;
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
@Path("/query")
public class Query extends Service {

    public static BigQuery bigQuery = new BigQuery();
    protected static final long DAY = 86400000l;
    protected static final long HOUR = 3600000l;

    private String executeOnlyIfResultIsOutDated(long time, String queryId, Runnable runnable) {
        long lastModificationDate = QueryCacheManager.getLastModificationDate(queryId);
        if (lastModificationDate + time < System.currentTimeMillis()){
            runnable.run();
            if (lastModificationDate == 0l) QueryCacheManager.addQueryResult(queryId, "{}");
            return "{'computing':true}";
        }
        return "{'computing':false, 'reason':'result less then "+(time/60000)+"minutes old'}";
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/calendar/user")
    public String activities(@HeaderParam("Authorization") String token) throws IOException {
        if (!validCredentials(token))
            return getInvalidCredentialsBean();
        return executeOnlyIfResultIsOutDated(HOUR, "calendar_"+userId, new Runnable() {
            public void run() {
                bigQuery.query(userId);
            }
        });
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/calendar/course/{courseId}")
    public String queryCourse(@PathParam("courseId") final String courseId) throws IOException {
        long lastModificationDate = QueryCacheManager.getLastModificationDate("calendar_course_"+courseId);
        return executeOnlyIfResultIsOutDated(DAY, "calendar_course_"+courseId, new Runnable() {

            public void run() {
                bigQuery.queryCourse(courseId);
            }
        });
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/averageLearnerActivities/{courseId}")
    public String averageActivityPerLearner(@HeaderParam("Authorization") String token,
                              @PathParam("courseId") final String courseId) throws IOException {
        return executeOnlyIfResultIsOutDated(DAY, "activity_per_learner_"+courseId, new Runnable() {

            public void run() {
                bigQuery.averageActivityPerLearner(courseId);
            }
        });
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/dropoutMonitor")
    public String dropoutMonitor(@HeaderParam("Authorization") String token) throws IOException {
        return executeOnlyIfResultIsOutDated(DAY, "course_login_overview", new Runnable() {

            public void run() {
                bigQuery.courseLoginOverview();
            }
        });
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/dropoutMonitor/{courseId}")
    public String dropoutMonitorForCourse(@HeaderParam("Authorization") String token,
                                 @PathParam("courseId") final String courseId) throws IOException {
        return executeOnlyIfResultIsOutDated(DAY, "course_activities_"+courseId, new Runnable() {

            public void run() {
                bigQuery.courseActivitiesOverview(courseId);
            }
        });
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/dropoutMonitorAggregate")
    public String dropoutMonitorAggregate(@HeaderParam("Authorization") String token) throws IOException {
        return executeOnlyIfResultIsOutDated(DAY, "dropoutMonitor", new Runnable() {

            public void run() {
                new DropoutMonitorAggregationTask("dropoutMonitor").scheduleTask();
            }
        });
    }

}

package nl.welteninstituut.tel.la.rest;

import nl.welteninstituut.tel.la.Configuration;
import nl.welteninstituut.tel.la.mapreduce.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by str on 18/05/15.
 */
@Path("/mapreduce")
public class MapReduce implements Serializable {

    private static final long serialVersionUID = 1L;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/init")
    public String init() throws IOException {
//        String id = MapReduceJob.start(getCreationJobSpec(50, 5, 5), new MapReduceSettings.Builder(getSettings()).setBucketName("xapi-proxy-dev").build());
//        String id = MapJob.start(getMapCreationJobSpec(50, 5, 5), new MapSettings.Builder(getSettings()).build());
        new ResetBigquerySyncJob(0, null).start();
        return "{'ok':'true'}";
    }


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/setSyncState/{state}")
    public String syncState(@PathParam("state") int state) throws IOException {
        new ResetBigquerySyncJob(state, null).start();
        return "{'ok':'true'}";
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/setSyncState/{state}/{origin}")
    public String syncState(@PathParam("state") int state,@PathParam("origin") String origin) throws IOException {
        new ResetBigquerySyncJob(state, origin).start();
        return "{'ok':'true'}";
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/computeCalendarActivities")
    public String calendarActivities(@PathParam("state") int state) throws IOException {
        new CalendarActivitiesJob(0).start();
        return "{'ok':'true'}";
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/computeCalendarActivities/{date}")
    public String calendarActivitiesSince(@PathParam("date") String date) throws IOException {
        long longDate = parseDate(date);
        if (longDate != 0) {
            new CalendarActivitiesJob(longDate
            ).start();
            return "{'ok':'true'}";
        } else {
            return "{'ok':'false', 'error':'could not parse" + date + "'}";
        }

    }
    private long parseDate(String date) {
        long longDate = 0l;
        try {
            longDate = Long.parseLong(date);
        } catch (NumberFormatException e) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                longDate = df.parse(date).getTime();
            } catch (ParseException pe  ) {

            }
        }
        return longDate;
    }


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/computeCourseUserDateToVerb")
    public String computeCourseUserDateToVerb(@PathParam("state") int state) throws IOException {
        new CourseUserDateToVerbJob(0).start();
        return "{'ok':'true'}";
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/computeCourseUserDateToVerb/{date}")
    public String computeCourseUserDateToVerbSince(@PathParam("date") String date) throws IOException {
        long longDate = parseDate(date);
        if (longDate != 0) {
            new CourseUserDateToVerbJob(longDate).start();
            return "{'ok':'true'}";
        } else {
            return "{'ok':'false', 'error':'could not parse" + date + "'}";
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/computeCourseDateToVerb")
    public String computeCourseDateToVerb() throws IOException {
        new CourseDateToVerbJob(0).start();
        return "{'ok':'true'}";
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/computeCourseDateToVerb/{date}")
    public String computeCourseDateToVerbSince(@PathParam("date") String date) throws IOException {
        long longDate = parseDate(date);
        if (longDate != 0) {
            new CourseDateToVerbJob(longDate).start();
            return "{'ok':'true'}";
        } else {
            return "{'ok':'false', 'error':'could not parse" + date + "'}";
        }
    }


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/computeCourseUserDateToObject")
    public String computeCourseUserDateToObject(@PathParam("state") int state) throws IOException {
        new CourseUserDateToObjectJob(0).start();
        return "{'ok':'true'}";
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/computeCourseUserDateToObject/{date}")
    public String computeCourseUserDateToObjectSince(@PathParam("date") String date) throws IOException {
        long longDate = parseDate(date);
        if (longDate != 0) {
            new CourseUserDateToObjectJob(longDate).start();
            return "{'ok':'true'}";
        } else {
            return "{'ok':'false', 'error':'could not parse" + date + "'}";
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/computeCourseUserDateToObjectDefinition/{date}")
    public String computeCourseUserDateToObjectDefinitionSince(@PathParam("date") String date) throws IOException {
        long longDate = parseDate(date);
        if (longDate != 0) {
            new CourseUserDateToObjectDefinitionJob(longDate).start();
            return "{'ok':'true'}";
        } else {
            return "{'ok':'false', 'error':'could not parse" + date + "'}";
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/delete/{origin}")
    public String syncState(@PathParam("origin") String origin) throws IOException {
        if (Configuration.get("eraseAllowed").equals("true")) {
            if ("all".equals(origin)) {
                new DeleteStatementsJob(null).start();
            } else {
                new DeleteStatementsJob(origin).start();
            }
            return "{'ok':'true'}";
        } else {
            return "{'ok':'false','comment':'erase not allowed'}";
        }

    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/delete/table/{table}")
    public String deleteTable(@PathParam("table") String table) throws IOException {
        if (Configuration.get("eraseAllowed").equals("true")) {
            new DeleteStatementsJob(null, table).start();
            return "{'ok':'true'}";
        } else {
            return "{'ok':'false','comment':'erase not allowed'}";
        }

    }


}



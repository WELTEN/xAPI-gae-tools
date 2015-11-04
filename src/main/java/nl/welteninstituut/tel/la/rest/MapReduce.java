package nl.welteninstituut.tel.la.rest;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.*;
import com.google.appengine.tools.mapreduce.inputs.DatastoreInput;
import com.google.appengine.tools.mapreduce.outputs.DatastoreOutput;
import com.google.appengine.tools.mapreduce.outputs.InMemoryOutput;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import com.google.appengine.api.datastore.Text;
import nl.welteninstituut.tel.la.mapreduce.DeleteStatementsJob;
import nl.welteninstituut.tel.la.mapreduce.ResetBigquerySyncJob;

/**
 * Created by str on 18/05/15.
 */
@Path("/mapreduce")
public class MapReduce implements Serializable {


    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/init")
    public String init() throws IOException {
//        String id = MapReduceJob.start(getCreationJobSpec(50, 5, 5), new MapReduceSettings.Builder(getSettings()).setBucketName("xapi-proxy-dev").build());
//        String id = MapJob.start(getMapCreationJobSpec(50, 5, 5), new MapSettings.Builder(getSettings()).build());
        new ResetBigquerySyncJob(0).start();
        return "{'ok':'true'}";
    }



    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/setSyncState/{state}")
    public String syncState(@PathParam("state") int state) throws IOException {
        new ResetBigquerySyncJob(state).start();
        return "{'ok':'true'}";
    }


    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/delete/{origin}")
    public String syncState(@PathParam("origin") String origin) throws IOException {
        new DeleteStatementsJob(origin).start();
        return "{'ok':'true'}";
    }

}



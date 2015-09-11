package nl.welteninstituut.tel.la.rest;

import nl.welteninstituut.tel.la.Configuration;
import nl.welteninstituut.tel.la.jdomanager.ConfigManager;
import nl.welteninstituut.tel.la.jdomanager.StatementManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by str on 24/04/15.
 */
@Path("/config")
public class Config {

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/setTarget/{url}")
    public String setTarget(@PathParam("url") String url) {
        ConfigManager.addKey("targetUrl", url);
        return "{}";
    }

    @GET
    @Produces({MediaType.TEXT_PLAIN })
    @Path("/getTarget")
    public String getTarget() {
        return Configuration.get("xapiTarget");
//        return ConfigManager.getValue("targetUrl")+Configuration.getAppId();
    }

    @GET
    @Produces({MediaType.TEXT_PLAIN })
    @Path("/getProp/{prop}")
    public String getProp(@PathParam("prop") String prop) {
        return Configuration.get(prop);
    }

    @GET
    @Produces({MediaType.TEXT_PLAIN })
    @Path("/getId")
    public String getId() {
        return Configuration.getAppId();
    }
}

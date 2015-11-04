package nl.welteninstituut.tel.la.rest;

import com.google.api.client.json.Json;
import com.google.apphosting.api.ApiProxy;
import nl.welteninstituut.tel.la.Configuration;
import nl.welteninstituut.tel.la.export.Export;
import nl.welteninstituut.tel.la.export.ExportException;
import nl.welteninstituut.tel.la.jdo.Statement;
import nl.welteninstituut.tel.la.jdomanager.ConfigManager;
import nl.welteninstituut.tel.la.jdomanager.StatementManager;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/*******************************************************************************
 * Copyright (C) 2013 Open Universiteit Nederland
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors: Stefaan Ternier
 ******************************************************************************/
@Path("/xAPI")
public class Submit {

    static final Export[] exports = Export.getExports();
    private static final Logger log = Logger.getLogger(Submit.class.getName());


    @POST
    @Consumes({MediaType.APPLICATION_JSON })
    @Path("/statement_test")
    public String testAddStatement(String postData, @HeaderParam("Authorization") String authorization) {
        String identifier = null;
        boolean syncSucceed = false;
        for (Export exporter: exports) {
            if (exporter.synchronous()) {
                try {
                    identifier = exporter.exportMetadata(authorization,postData);
                    syncSucceed = true;
                } catch (ExportException e) {
                    long proxyId = StatementManager.addStatementWithError(postData, authorization, e.getResult());
                    return "{\"result\":\"not ok\", \"proxyId\":"+proxyId+"}";
                }
            }
        }
        if (syncSucceed)
            for (Export exporter: exports) {
                if (!exporter.synchronous()) {
                    try {
                        exporter.exportMetadata(authorization,postData,identifier);
                    } catch (ExportException e) {
                        log.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            }
        long proxyId = StatementManager.addStatement(postData, authorization, identifier);
        return "{\"result\":\"ok\", \"proxyId\":"+proxyId+", \"learningLockerResult\":[\""+identifier+"\"]}";
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON })
    @Path("/statement/origin/{origin}")
    public String postStatement(String postData,
                                @PathParam("origin") String origin,
                                @HeaderParam("Authorization") String authorization) {
        if (!authorization.equals(Configuration.get(Configuration.AUTHORIZATION)))
            return "{\"result\":\"not ok\", \"error\":\"Authorization error\"}";
        long proxyId = StatementManager.addStatement(postData, origin);
        return "{\"result\":\"ok\", \"proxyId\":"+proxyId+"}";
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON })
    @Path("/statement_async/origin/{origin}")
    public String postStatementAsync(String postData,
                                     @PathParam("origin") String origin,
                                     @HeaderParam("Authorization") String authorization) {
        StatementManager.addStatementAsync(postData, origin);
        return "{\"result\":\"ok\"}";
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON })
    @Path("/statements")
    public String postStatements(String postData, @HeaderParam("Authorization") String authorization) {
        try {


            URL url = new URL(Configuration.get(Configuration.XAPITARGET));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", authorization);
            connection.setRequestProperty("X-Experience-API-Version", "1.0.0");
            connection.setRequestProperty("Content-Type", "application/json");


            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(postData);
            writer.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String result = "";
                String line = null;

                while ((line = reader.readLine()) != null) {
                    result += line;
                }
                String laId = getLearningLockerId(result);
                long proxyId = StatementManager.addStatement(postData, authorization, laId);
                reader.close();
                connection.disconnect();

                toBigQuery(postData, laId);
                return "{\"result\":\"ok\", \"proxyId\":"+proxyId+", \"learningLockerResult\":"+result+"}";
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String result = "";
                String line = null;

                while ((line = reader.readLine()) != null) {
                    result += line;
                }
                long proxyId = StatementManager.addStatementWithError(postData, authorization, result);
                reader.close();
                connection.disconnect();
                return "{\"result\":\"not ok\", \"proxyId\":"+proxyId+"}";
            }

        } catch (Exception e) {
            long proxyId = StatementManager.addStatementWithError(postData, authorization, e.toString());
            return "{\"result\":\"not ok\", \"proxyId\":"+proxyId+"}";
        }

    }


    private static BigQuery bigQuery = new BigQuery();
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    private static DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

    private static String getLearningLockerId( String laResult){
        JSONArray laResultObject = null;
        try {
            laResultObject = new JSONArray(laResult);
            String id = laResultObject.getString(0);
            return id;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }
    public static void toBigQuery(String postData, String id){
        try {
            JSONObject jsonObject = new JSONObject(postData);

            JSONObject actorObject = jsonObject.getJSONObject("actor");
            long timestampLong =0l;
            if (jsonObject.has("timestamp")) {
                String timestamp = jsonObject.getString("timestamp");
                try {
                    timestampLong = df.parse(timestamp).getTime();
                } catch (ParseException e){
                    timestampLong = df2.parse(timestamp).getTime();
                }
            }
            String actorType = actorObject.getString("objectType");
            String actorId = actorObject.getJSONObject("account").getString("name");
            String verbId = jsonObject.getJSONObject("verb").getString("id");
            String objectType = jsonObject.getJSONObject("object").getString("objectType");
            String objectId = jsonObject.getJSONObject("object").getString("id");
            String objectDefinition ="";
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
                                            if (coordinates.length()>=2) {
                                                lng = Double.parseDouble(""+ coordinates.get(0));
                                                lat = Double.parseDouble(""+ coordinates.get(1));
                                            }
                                        }
                                    }
                            }
                        }
                    }
                }
            }
            System.out.println(lat + " "+lng);

            try {
                objectDefinition = jsonObject.getJSONObject("object").getJSONObject("definition").getString("type");
            } catch (Exception e) {

            }
            bigQuery.insertStatement(Configuration.get(Configuration.BQTableId), id, timestampLong, actorType,actorId,verbId,objectType,objectId,objectDefinition, lat, lng, "todo");
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


    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/statements/json/{id}")
    public String getJson(@PathParam("id") String id) {
        return StatementManager.getStatementJson(id);

    }

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    @Path("/statements/error/{id}")
    public String getError(@PathParam("id") String id) {
        return StatementManager.getStatementError(id);

    }
}

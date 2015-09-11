package nl.welteninstituut.tel.la.export;

import nl.welteninstituut.tel.la.Configuration;
import nl.welteninstituut.tel.la.export.tasks.BigQueryExportTask;
import nl.welteninstituut.tel.la.rest.BigQuery;
import nl.welteninstituut.tel.la.tasks.GenericBean;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class BigQueryExport extends Export {
    private static final Logger log = Logger.getLogger(BigQueryExport.class.getName());


    @Override
    public boolean synchronous() {
        return false;
    }

    @Override
    public String exportMetadata(String authorization, String metadata) throws ExportException {
        return exportMetadata(authorization,metadata, null);
    }

    @Override
    public String exportMetadata(String authorization, String postData, String identifier) throws ExportException {
       new BigQueryExportTask(authorization,postData,identifier).scheduleTask();
        return null;
    }


//    private class BigQueryExportTask  extends GenericBean {
//
//        private String authorization;
//        private String postData;
//        private String identifier;
//
//        public BigQueryExportTask() {
//
//        }
//
//        public BigQueryExportTask(String authorization, String postData, String identifier) {
//            this.authorization = authorization;
//            this.postData = postData;
//            this.identifier = identifier;
//        }
//
//        public String getAuthorization() {
//            return authorization;
//        }
//
//        public void setAuthorization(String authorization) {
//            this.authorization = authorization;
//        }
//
//        public String getPostData() {
//            return postData;
//        }
//
//        public void setPostData(String postData) {
//            this.postData = postData;
//        }
//
//        public String getIdentifier() {
//            return identifier;
//        }
//
//        public void setIdentifier(String identifier) {
//            this.identifier = identifier;
//        }
//
//        @Override
//        public void run() {
//            try {
//                JSONObject jsonObject = new JSONObject(postData);
//
//                JSONObject actorObject = jsonObject.getJSONObject("actor");
//                long timestampLong =0l;
//                if (jsonObject.has("timestamp")) {
//                    String timestamp = jsonObject.getString("timestamp");
//                    try {
//                        timestampLong = df.parse(timestamp).getTime();
//                    } catch (ParseException e){
//                        timestampLong = df2.parse(timestamp).getTime();
//                    }
//                }
//                String actorType = actorObject.getString("objectType");
//                String actorId = actorObject.getJSONObject("account").getString("name");
//                String verbId = jsonObject.getJSONObject("verb").getString("id");
//                String objectType = jsonObject.getJSONObject("object").getString("objectType");
//                String objectId = jsonObject.getJSONObject("object").getString("id");
//                String objectDefinition ="";
//                Double lat = -1d;
//                Double lng = -1d;
//                if (jsonObject.has("context")) {
//                    JSONObject context = jsonObject.getJSONObject("context");
//                    if (context.has("extensions")) {
//                        JSONObject extensions = context.getJSONObject("extensions");
//                        if (extensions.has("http://activitystrea.ms/schema/1.0/place")) {
//                            JSONObject pace = extensions.getJSONObject("http://activitystrea.ms/schema/1.0/place");
//                            if (pace.has("geojson")) {
//                                JSONObject geojson = pace.getJSONObject("geojson");
//                                if (geojson.has("features")) {
//
//                                    JSONObject features = geojson.getJSONArray("features").getJSONObject(0);
//                                    if (features.has("geometry")) {
//                                        JSONObject geometry = features.getJSONObject("geometry");
//                                        if (geometry.has("coordinates")) {
//                                            JSONArray coordinates = geometry.getJSONArray("coordinates");
//                                            if (coordinates.length()>=2) {
//                                                lng = Double.parseDouble(""+ coordinates.get(0));
//                                                lat = Double.parseDouble(""+ coordinates.get(1));
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                try {
//                    objectDefinition = jsonObject.getJSONObject("object").getJSONObject("definition").getString("type");
//                } catch (Exception e) {
//
//                }
//                bigQuery.insertStatement(Configuration.get(Configuration.BQTableId), identifier, timestampLong, actorType,actorId,verbId,objectType,objectId,objectDefinition, lat, lng, "todo");
//            } catch (JSONException e) {
//                log.log(Level.SEVERE, e.getMessage(), e);
//            } catch (IOException e) {
//                log.log(Level.SEVERE, e.getMessage(), e);
//            } catch (ParseException e) {
//                log.log(Level.SEVERE, e.getMessage(), e);
//            }
//        }
//
//    }

}

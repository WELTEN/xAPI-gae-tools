package nl.welteninstituut.tel.la.export;

import nl.welteninstituut.tel.la.Configuration;
import nl.welteninstituut.tel.la.jdomanager.StatementManager;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

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
public class LearningLockerExport extends Export {

    @Override
    public boolean synchronous() {
        return true;
    }

    @Override
    public String exportMetadata(String authorization, String metadata) throws ExportException {
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
            writer.write(metadata);
            writer.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String result = "";
                String line = null;

                while ((line = reader.readLine()) != null) {
                    result += line;
                }
                reader.close();
                connection.disconnect();
                return getLearningLockerId(result);
//                long proxyId = StatementManager.addStatement(postData, authorization, laId);
//
//                toBigQuery(postData, laId);
//                return "{\"result\":\"ok\", \"proxyId\":"+proxyId+", \"learningLockerResult\":"+result+"}";
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String result = "";
                String line = null;

                while ((line = reader.readLine()) != null) {
                    result += line;
                }
                ExportException e = new ExportException();
                e.setResult(result);
                reader.close();
                connection.disconnect();
                throw e;
//                long proxyId = StatementManager.addStatementWithError(postData, authorization, result);
//                reader.close();
//                connection.disconnect();
//                return "{\"result\":\"not ok\", \"proxyId\":"+proxyId+"}";
            }

        } catch (Exception e) {
            ExportException exportException = new ExportException();
            exportException.setResult(e.getMessage());

            throw exportException;
//            long proxyId = StatementManager.addStatementWithError(postData, authorization, e.toString());
//            return "{\"result\":\"not ok\", \"proxyId\":"+proxyId+"}";
        }

    }

    @Override
    public String exportMetadata(String authorization, String metadata, String identifier) throws ExportException {
        return null;
    }

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
}

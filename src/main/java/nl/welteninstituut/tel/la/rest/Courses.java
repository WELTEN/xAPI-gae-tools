package nl.welteninstituut.tel.la.rest;

import nl.welteninstituut.tel.la.jdomanager.StatementManager;
import org.json.JSONException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
@Path("/courses")
public class Courses extends Service {

    @GET
    @Produces({MediaType.APPLICATION_JSON })
    public String getCourses(@HeaderParam("Authorization") String token) throws IOException {
        if (!validCredentials(token))
            return getInvalidCredentialsBean();

        try {
            return readURL(new URL("https://backend.ecolearning.eu/courseprogress/"+userId+"?cached=1"),getBearer(token));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "[]";
    }

    private String readURL(final URL url, String accessToken) throws IOException, JSONException {
        System.out.println("url "+url);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        System.out.println("using token:  "+accessToken);
        //b16b5d3caf414e812c5fe82c024bd891
        connection.setRequestProperty("Authorization", "Bearer "+accessToken );

        InputStream is = connection.getInputStream();
        int r;
        while ((r = is.read()) != -1) {
            baos.write(r);
        }

        int responseCode = connection.getResponseCode();
//        if (responseCode == 401) {
//            // refresh access token using refresh token
//
//
//            baos = new ByteArrayOutputStream();
//            // InputStream is = url.openStream();
//
//            connection = (HttpURLConnection) url.openConnection();
////            connection.setRequestProperty("Authorization", "Bearer " + account.getAccessToken());
//
//            is = connection.getInputStream();
//            while ((r = is.read()) != -1) {
//                baos.write(r);
//            }
//
//        }

        return new String(baos.toByteArray());
    }
}

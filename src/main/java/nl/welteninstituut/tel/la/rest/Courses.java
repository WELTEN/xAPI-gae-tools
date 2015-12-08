package nl.welteninstituut.tel.la.rest;

import org.json.JSONException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
//        if (!validCredentials(token))
//            return getInvalidCredentialsBean();
//        try {
//            return "{\n" +
//                    "  \"courses\":" +
//                    readURL(new URL("https://backend.ecolearning.eu/courseprogress/"+userId+"?cached=1"),getBearer(token))
//                    +"}";
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return "{ \"courses\":[{\"_id\":\"5539f101c72a368579572b7f\",\"title\":[{\"language\":\"it\",\"string\":\"M'appare il mondo: dalle carte alla Terra digitale partecipata Terza edizione\",\"_id\":\"56013bd0066489097cf6695c\"}],\"courseUrl\":\"http://hub10b.ecolearning.eu/course/mappare-il-mondodalle-carte-alla-terra-digitale-3/\",\"courseImageUrl\":\"./img/courses/5539f101c72a368579572b7f.jpg\",\"platformInfo\":{\"platformId\":\"54633600f8ecd1a8dfd05e72\",\"platformName\":\"OpenMooc\",\"logoImageUrl\":\"./img/openmooclogo.jpg\"},\"progressPercentage\":null,\"firstViewDate\":\"2015-05-26T06:50:58.000Z\",\"lastViewDate\":null,\"completedDate\":null,\"oaiPmhIdentifier\":\"eu.ecolearning.hub10b:9\"},{\"_id\":\"552fffae67c196687850df8b\",\"title\":[{\"language\":\"es\",\"string\":\"Flipped Classroom Convocatoria 2\",\"_id\":\"56026315fe7815cc58e73fd9\"}],\"courseUrl\":\"http://ecoportal.telefonicalearningservices.com/web/flipped-classroom-convocatoria-2\",\"courseImageUrl\":\"./img/courses/552fffae67c196687850df8b.jpg\",\"platformInfo\":{\"platformId\":\"5465be5bf8ecd1a8dfd05e7a\",\"platformName\":\"weMOOC\",\"logoImageUrl\":\"./img/wemooc_logo.png\"},\"progressPercentage\":null,\"firstViewDate\":\"2015-05-20T14:54:00.000Z\",\"lastViewDate\":\"2015-05-20T14:54:00.000Z\",\"completedDate\":null,\"oaiPmhIdentifier\":\"oai:eu.ecolearning.hub6:201\"},{\"_id\":\"5473bf3ff3a442d44c4deb0c\",\"title\":[{\"language\":\"pt\",\"string\":\"Necessidades Educativas Especiais. Como ensinar, como aprender\",\"_id\":\"5601c85d7d686e586766798d\"}],\"courseUrl\":\"http://hub4.ecolearning.eu/course/necessidades-educativas-especiais-como-ensinar-com/\",\"courseImageUrl\":\"./img/courses/5473bf3ff3a442d44c4deb0c.jpg\",\"platformInfo\":{\"platformId\":\"54575665f8ecd1a8dfd05e6c\",\"platformName\":\"OpenMooc\",\"logoImageUrl\":\"./img/openmooclogo.jpg\"},\"progressPercentage\":null,\"firstViewDate\":\"2015-05-20T13:02:51.000Z\",\"lastViewDate\":\"2015-06-24T11:27:54.856Z\",\"completedDate\":null,\"oaiPmhIdentifier\":\"eu.ecolearning.hub4:2\"},{\"_id\":\"56153a7b2125a8a56262bfeb\",\"title\":[{\"language\":\"de\",\"string\":\"E-Learning-Projektmanagement an Schulen\",\"_id\":\"5661a36cb6717a93068048f3\"}],\"courseUrl\":\"http://eco.humance.de/group/guest/course-assignment?courseId=80286\",\"courseImageUrl\":\"./img/courses/56153a7b2125a8a56262bfeb.jpg\",\"platformInfo\":{\"platformId\":\"544e4356c5a90837a18e84aa\",\"platformName\":\"logiassist\",\"logoImageUrl\":\"./img/logiassist.gif\"},\"progressPercentage\":null,\"firstViewDate\":null,\"lastViewDate\":null,\"completedDate\":null,\"oaiPmhIdentifier\":\"oai:de.humance.education:80286\"}]}";
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

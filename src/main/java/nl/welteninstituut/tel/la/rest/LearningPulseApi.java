package nl.welteninstituut.tel.la.rest;

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
@Path("/learningpulse")
public class LearningPulseApi {


    @GET
    @Consumes({MediaType.APPLICATION_JSON })
    @Path("/steps")
    public String getSteps(@HeaderParam("Authorization") String authorization) {
        System.out.println("token is "+authorization);
        try {
            System.out.println(readURL(new URL("https://api.fitbit.com/1/user/-/activities/steps/date/today/1m.json"), authorization));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{}";
    }


    protected String readURL(URL url, String token ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        InputStream is = url.openStream();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Bearer "+token);

        InputStream is = connection.getInputStream();
        int r;
        while ((r = is.read()) != -1) {
            baos.write(r);
        }
        return new String(baos.toByteArray());
    }
}

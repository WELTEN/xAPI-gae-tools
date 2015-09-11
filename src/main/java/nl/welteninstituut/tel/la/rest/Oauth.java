package nl.welteninstituut.tel.la.rest;

import nl.welteninstituut.tel.oauth.jdo.OauthKeyManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

@Path("/oauth")
public class Oauth {


    @GET
    @Path("/addkey")
    public String addKey(
            @QueryParam("oauthProviderId") int oauthProviderId,
            @QueryParam("client_id") String client_id,
            @QueryParam("client_secret") String client_secret,
            @QueryParam("redirect_uri") String redirect_uri
    ) {
        OauthKeyManager.addKey(oauthProviderId, client_id, client_secret, redirect_uri);

        return "{}";
    }






    public String getAuthURL(String authCode) {

        return "https://graph.facebook.com/oauth/access_token?client_id=";// + client_id + "&redirect_uri=" + redirect_uri + "&client_secret=" + secret + "&code=" + authCode;

    }

    private String readURL(URL url) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = url.openStream();
        int r;
        while ((r = is.read()) != -1) {
            baos.write(r);
        }
        return new String(baos.toByteArray());
    }



}


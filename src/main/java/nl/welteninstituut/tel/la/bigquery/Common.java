package nl.welteninstituut.tel.la.bigquery;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.AppIdentityCredential;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.bigquery.Bigquery;

import java.util.Arrays;

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
public class Common {

    private static final String SCOPE = "https://www.googleapis.com/auth/bigquery";
    private static final HttpTransport TRANSPORT = new UrlFetchTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    public final static String DONE = "DONE";
    // @VisibleForTesting
    static Bigquery bigquery = new Bigquery(TRANSPORT, JSON_FACTORY, getRequestInitializer());

    private static HttpRequestInitializer getRequestInitializer() {
        if (System.getProperty("OAUTH_ACCESS_TOKEN") != null) {
            return new GoogleCredential().setAccessToken(System.getProperty("OAUTH_ACCESS_TOKEN"));
        }
        return new AppIdentityCredential(Arrays.asList(new String[]{SCOPE}));
    }
}

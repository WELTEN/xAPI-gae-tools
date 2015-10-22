/*
 * Copyright (C) 2015 Open Universiteit Nederland
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
 */
package nl.welteninstituut.tel.oauth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import nl.welteninstituut.tel.oauth.jdo.AccountJDO;
import nl.welteninstituut.tel.oauth.jdo.AccountManager;
import nl.welteninstituut.tel.oauth.jdo.OauthConfigurationJDO;
import nl.welteninstituut.tel.oauth.jdo.OauthKeyManager;

import org.codehaus.jettison.json.JSONObject;

/**
 * @author Stefaan Ternier
 * @author Harrie Martens
 *
 */
public class OauthEcoWorker extends OauthWorker {

    private static String client_secret;
    private static String client_id;
    private static String redirect_uri;

    static {
        OauthConfigurationJDO jdo = OauthKeyManager.getConfigurationObject(AccountJDO.ECOCLIENT);
        client_id = jdo.getClient_id();
        redirect_uri = jdo.getRedirect_uri();
        client_secret = jdo.getClient_secret();
    }

    @Override
    protected String getAuthUrl(String authCode) {
        return "https://idp.ecolearning.eu/token";
    }

    public void exchangeCodeForAccessToken() {
        RequestAccessToken request = new RequestAccessToken();
        request.postUrl(getAuthUrl(code), "code=" + code + "&" + "client_id=" + client_id + "&" + "client_secret=" + client_secret + "&" + "redirect_uri=" + redirect_uri + "&" + "grant_type=authorization_code");
        System.out.println("accessToken= "+request.getAccessToken());
        if (request.getAccessToken() !=  null) {
            saveAccount(request.getAccessToken());

//
            sendRedirect(request.getAccessToken(), ""+request.getExpires_in(), AccountJDO.ECOCLIENT);
        } else {
            error("The google authentication servers are currently not functional. Please retry later. <br> The service usually works again after 15:00 CEST. Find more (technical) information about this problem on. <ul> " +
                    "<li ><a href=\"https://code.google.com/p/google-glass-api/issues/detail?id=99\">oauth2 java.net.SocketTimeoutException on AppEngine</a>" +
                    "<li ><a href=\"https://groups.google.com/forum/?fromgroups#!topic/google-appengine-downtime-notify/TqKVL9TNq2A\">Google groups downtime</a></ul> ");
        }
    }

    public void saveAccount(String accessToken) {
        try {
            JSONObject profileJson = new JSONObject(readURL(new URL("http://idp.ecolearning.eu/userinfo"), accessToken));
            String id = "";
            String picture = "";
            String email = "";
            String given_name = "";
            String family_name = "";
            String name = "";
//            if (profileJson.has("picture")) picture = profileJson.getString("picture");
            if (profileJson.has("sub")) id = profileJson.getString("sub");
            if (profileJson.has("email")) email =  profileJson.getString("email");
            if (profileJson.has("given_name")) given_name = profileJson.getString("given_name");
            if (profileJson.has("family_name")) family_name = profileJson.getString("family_name");
            if (profileJson.has("name")) name = profileJson.getString("name");
            AccountJDO account = AccountManager.addAccount(id, AccountJDO.ECOCLIENT, email, given_name, family_name, name, picture, false);
            saveAccessToken(account.getUniqueId(), accessToken);

        } catch (Throwable ex) {
            throw new RuntimeException("failed login", ex);
        }
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

	@Override
	public int getServiceId() {
		return AccountJDO.ECOCLIENT;
	}
}

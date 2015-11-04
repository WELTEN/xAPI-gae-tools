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

import nl.welteninstituut.tel.oauth.jdo.AccountJDO;
import nl.welteninstituut.tel.oauth.jdo.AccountManager;
import nl.welteninstituut.tel.oauth.jdo.OauthConfigurationJDO;
import nl.welteninstituut.tel.oauth.jdo.OauthKeyManager;
import nl.welteninstituut.tel.util.StringPool;

import org.codehaus.jettison.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Stefaan Ternier
 * @author Harrie Martens
 *
 */
public class OauthFitbitWorker extends OauthWorker {

	private static String client_secret;
	private static String client_id;
	private static String redirect_uri;

	static {
		OauthConfigurationJDO jdo = OauthKeyManager.getConfigurationObject(AccountJDO.FITBITCLIENT);
		client_id = jdo.getClient_id();
		redirect_uri = jdo.getRedirect_uri();
		client_secret = jdo.getClient_secret();
	}

	@Override
	protected String getAuthUrl(String authCode) {
		return "https://api.fitbit.com/oauth2/token";
	}

	public void exchangeCodeForAccessToken() {
		RequestAccessToken request = new RequestAccessToken();
		request.postUrl(getAuthUrl(code), "code=" + code + "&" + "client_id=" + client_id + "&" + "client_secret="
				+ client_secret + "&" + "redirect_uri=" + redirect_uri + "&" + "grant_type=authorization_code",
				client_id + ":" + client_secret);
		if (request.getAccessToken() != null) {
			processRequest(request);

		} else {
			error("The Fitbit authentication servers are currently not functional. Please retry later.");
		}
	}

	@Override
	protected int getClientType() {
		return AccountJDO.FITBITCLIENT;
	}

	@Override
	protected void processLoginAsMetaAccount(RequestAccessToken request) {
		saveAccount(request.getAccessToken(), request.getRefreshToken());
		sendRedirect(request.getAccessToken(), String.valueOf(request.getExpires_in()), AccountJDO.FITBITCLIENT);
	}

	@Override
	protected void processLoginAsSecondaryAccount(RequestAccessToken accessToken) {

	}

	public void saveAccount(String accessToken, String refreshToken) {
		try {
			JSONObject profileJson = new JSONObject(readURL(new URL("https://api.fitbit.com/1/user/-/profile.json"),
					accessToken)).getJSONObject("user");

			String id = profileJson.has("encodedId") ? profileJson.getString("encodedId") : StringPool.BLANK;
			String picture = profileJson.has("avatar150") ? profileJson.getString("avatar150") : StringPool.BLANK;
			String email = StringPool.BLANK;
			String given_name =  profileJson.has("displayName") ? profileJson.getString("displayName") : StringPool.BLANK;
			String family_name = StringPool.BLANK;
			String name = profileJson.has("fullName") ? profileJson.getString("fullName") : StringPool.BLANK;

			AccountJDO account = AccountManager.addAccount(id, AccountJDO.FITBITCLIENT, email, given_name, family_name,
					name, picture, false);
			
			saveAccessToken(account, accessToken, refreshToken);
			
			System.out.println("heartrate2 -> " + readURL(new URL("https://api.fitbit.com/1/user/-/activities/heart/date/2015-10-21/1d/1sec/time/10:00/10:05.json"),
					accessToken));
		} catch (Throwable ex) {
			throw new RuntimeException("failed login", ex);
		}
	}

	protected String readURL(URL url, String token) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// InputStream is = url.openStream();

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		System.out.println(connection.getClass().getName());
		connection.setRequestProperty("Authorization", "Bearer " + token);

		InputStream is = connection.getInputStream();
		int r;
		while ((r = is.read()) != -1) {
			baos.write(r);
		}
		return new String(baos.toByteArray());
	}

	@Override
	public int getServiceId() {
		return AccountJDO.FITBITCLIENT;
	}
}

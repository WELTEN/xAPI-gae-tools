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

import java.net.URL;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import nl.welteninstituut.tel.oauth.jdo.AccountJDO;
import nl.welteninstituut.tel.oauth.jdo.AccountManager;
import nl.welteninstituut.tel.oauth.jdo.OauthConfigurationJDO;
import nl.welteninstituut.tel.oauth.jdo.OauthKeyManager;
import nl.welteninstituut.tel.util.StringPool;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * @author Stefaan Ternier
 * @author Harrie Martens
 *
 */
public class OauthGoogleWorker extends OauthWorker {

	private static String client_secret;
	private static String client_id;
	private static String redirect_uri;

	static {
		OauthConfigurationJDO jdo = OauthKeyManager.getConfigurationObject(AccountJDO.GOOGLECLIENT);
		client_id = jdo.getClient_id();
		redirect_uri = jdo.getRedirect_uri();
		client_secret = jdo.getClient_secret().trim();
	}

	@Override
	protected String getAuthUrl(String authCode) {
		return "https://accounts.google.com/o/oauth2/token?code=" + authCode + "&redirect_uri=" + redirect_uri
				+ "&client_id=" + client_id + "&client_secret=" + client_secret + "&grant_type=authorization_code";
	}

	@Override
	protected int getClientType() {
		return AccountJDO.GOOGLECLIENT;
	}

	@Override
	protected void processLoginAsMetaAccount(RequestAccessToken accessToken) {
		AccountJDO account = saveAccount(accessToken.getAccessToken());

		// reset session after login to prevent session hijacking
		HttpSession session = getRequest().getSession(false);
		if (session != null) {
			session.invalidate();
		}
		
		// fill a user session object
		session = getRequest().getSession(true);
		session.setAttribute("accesstoken", accessToken.getAccessToken());
		session.setAttribute("type", (Integer) AccountJDO.GOOGLECLIENT);
		session.setAttribute("expires-in", (Long) accessToken.getExpires_in());
		session.setAttribute("accountid", account.getUniqueId());
		session.setAttribute("CSRF-token", UUID.randomUUID().toString());

		sendRedirect(accessToken.getAccessToken(), Long.toString(accessToken.getExpires_in()), AccountJDO.GOOGLECLIENT);
	}

	@Override
	protected void processLoginAsSecondaryAccount(RequestAccessToken accessToken) {

	}

	public void exchangeCodeForAccessToken() {
		RequestAccessToken request = new RequestAccessToken();
		request.postUrl(getAuthUrl(code), "code=" + code + "&" + "client_id=" + client_id + "&" + "client_secret="
				+ client_secret + "&" + "redirect_uri=" + redirect_uri + "&" + "grant_type=authorization_code");

		if (request.getAccessToken() != null) {
			processRequest(request);

		} else {
			error("The google authentication servers are currently not functional. Please retry later. <br> The service usually works again after 15:00 CEST. Find more (technical) information about this problem on. <ul> "
					+ "<li ><a href=\"https://code.google.com/p/google-glass-api/issues/detail?id=99\">oauth2 java.net.SocketTimeoutException on AppEngine</a>"
					+ "<li ><a href=\"https://groups.google.com/forum/?fromgroups#!topic/google-appengine-downtime-notify/TqKVL9TNq2A\">Google groups downtime</a></ul> ");
		}
	}

	public AccountJDO saveAccount(String accessToken) {
		AccountJDO account = null;
		try {
			JSONObject profileJson = new JSONObject(readURL(new URL(
					"https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken)));
			String picture = getJSONString(profileJson, "picture");
			String id = getJSONString(profileJson, "id");
			String email = getJSONString(profileJson, "email");
			String given_name = getJSONString(profileJson, "given_name");
			String family_name = getJSONString(profileJson, "family_name");
			String name = getJSONString(profileJson, "name");
			account = AccountManager.addAccount(id, AccountJDO.GOOGLECLIENT, email, given_name, family_name, name,
					picture, false);
			saveAccessToken(account.getUniqueId(), accessToken);

		} catch (Throwable ex) {
			throw new RuntimeException("failed login", ex);
		}

		return account;
	}

	@Override
	public int getServiceId() {
		return AccountJDO.GOOGLECLIENT;
	}

	protected String getJSONString(final JSONObject json, final String key) throws JSONException {
		return json.has(key) ? json.getString(key) : StringPool.BLANK;

	}

}

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
package nl.welteninstituut.tel.la.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.welteninstituut.tel.oauth.jdo.AccountJDO;
import nl.welteninstituut.tel.oauth.jdo.AccountManager;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccount;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccountManager;

;

/**
 * This servlet handles the grant services form. For now only the RescueTime
 * service is supported.
 * 
 * @author Harrie Martens
 *
 */
public class ServicesServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws java.io.IOException {
		
		String apiKey = request.getParameter("rt-key");
		String csrfToken = request.getParameter("CSRF-token");

		HttpSession session = request.getSession(false);
		if (session != null) {

			String accountId = (String) session.getAttribute("accountid");
			AccountJDO pa = AccountManager.getAccount(accountId);
			
			if (pa != null && apiKey != null && Objects.equals(csrfToken, session.getAttribute("CSRF-token"))) {
				if (isValidAPIKey(apiKey)) {
					
					// Valid api key, now get rescuetime account
					OauthServiceAccount account = OauthServiceAccountManager.getAccount(AccountJDO.RESCUETIMECLIENT,
							pa.getLocalId());
					if (account == null) {
						OauthServiceAccountManager.addOauthServiceAccount(AccountJDO.RESCUETIMECLIENT, pa.getLocalId(),
								apiKey, null, null, accountId);
					} else {
						if (!apiKey.equals(account.getAccessToken())) {
							account.setAccessToken(apiKey);
							OauthServiceAccountManager.updateOauthServiceAccount(account);
						}
					}
				}
			}
		}

		response.sendRedirect("/services.jsp");
	}

	private boolean isValidAPIKey(final String key) {

		String url = "https://www.rescuetime.com/anapi/daily_summary_feed?key=" + key;
		try {
			String data = readURL(new URL(url));
			return !data.startsWith("{\"error\":");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;

	}

	protected String readURL(URL url) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		InputStream is = connection.getInputStream();
		int r;
		while ((r = is.read()) != -1) {
			baos.write(r);
		}
		return new String(baos.toByteArray());
	}

}

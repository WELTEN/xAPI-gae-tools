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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Stefaan Ternier
 *
 */
public class OauthServlet extends HttpServlet {

	private static final long serialVersionUID = 42L;

	private static final String FACEBOOK = "facebook";
	private static final String GOOGLE = "google";
	private static final String LINKEDIN = "linkedin";
	private static final String WESPOT = "wespot";
	private static final String ECO = "eco";
	private static final String FITBIT = "fitbit";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("doPost");
		String baseUrl = "http://" + req.getServerName();
		if (req.getServerPort() != 80)
			baseUrl += ":" + req.getServerPort();
		if (req.getParameter("error") != null || req.getParameter("denied") != null) {
			System.out.println("error");
			String line = null;
			while ((line = req.getReader().readLine()) != null) {
				System.out.println(line);
			}

			resp.sendRedirect(baseUrl + "/oauth.html");
			return;

		}
		OauthWorker worker = null;
		if (req.getPathInfo().contains(FACEBOOK)) {
			worker = new OauthFbWorker();
		} else if (req.getPathInfo().contains(GOOGLE)) {
			worker = new OauthGoogleWorker();
		} else if (req.getPathInfo().contains(LINKEDIN)) {
			worker = new OauthLinkedInWorker();
			// } else if (req.getPathInfo().contains(TWITTER)) {
			// OauthTwitterWorker twitterWorker = new OauthTwitterWorker();
			// String accessToken =
			// twitterWorker.afterSuccesfullAuthentication(req);
			// if (accessToken != null) {
			// long expiresLong = 3600*24*7l;
			// resp.sendRedirect(baseUrl+"/oauth.html?accessToken=" +
			// accessToken + "&type=" + AccountJDO.LINKEDINCLIENT + "&exp=" +
			// expiresLong);
			// }
		} else if (req.getPathInfo().contains(WESPOT)) {
			worker = new OauthWespotWorker();
		} else if (req.getPathInfo().contains(ECO)) {
			worker = new OauthEcoWorker();
		} else if (req.getPathInfo().contains(FITBIT)) {
			worker = new OauthFitbitWorker();
		}
		if (worker != null) {
			worker.setBaseUrl(baseUrl);
			worker.setCode(req.getParameter("code"));
			worker.setResponse(resp);
			worker.exchangeCodeForAccessToken();
		}
	}

}

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
package nl.welteninstituut.tel.la.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import nl.welteninstituut.tel.oauth.jdo.OauthKeyManager;

/**
 * Use this rest service to store the OAUTH connection information in the system.
 * 
 * @author Stefaan Ternier
 *
 */
@Path("/oauth")
public class Oauth {

	@GET
	@Path("/addkey")
	public String addKey(@QueryParam("oauthProviderId") int oauthProviderId,
			@QueryParam("client_id") String client_id,
			@QueryParam("client_secret") String client_secret,
			@QueryParam("redirect_uri") String redirect_uri) {

		OauthKeyManager.addKey(oauthProviderId, client_id, client_secret,
				redirect_uri);

		return "{}";
	}

}

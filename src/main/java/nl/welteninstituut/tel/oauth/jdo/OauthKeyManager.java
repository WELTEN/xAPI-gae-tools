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
package nl.welteninstituut.tel.oauth.jdo;

import javax.jdo.PersistenceManager;

import nl.welteninstituut.tel.la.jdomanager.PMF;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * @author Stefaan Ternier
 *
 */
public class OauthKeyManager {

	public static void addKey(int oauthProviderId, String client_id, String client_secret, String redirect_uri) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		OauthConfigurationJDO conf = new OauthConfigurationJDO();
		conf.setOauthProviderId(oauthProviderId);
		conf.setClient_id(client_id);
		conf.setClient_secret(client_secret);
		conf.setRedirect_uri(redirect_uri);
		try {
			pm.makePersistent(conf);
		} finally {
			pm.close();
		}
	}

	public static OauthConfigurationJDO getConfigurationObject(int authProviderId) {
		Key key = KeyFactory.createKey(OauthConfigurationJDO.class.getSimpleName(), authProviderId);
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			OauthConfigurationJDO confJDO = pm.getObjectById(OauthConfigurationJDO.class, key);
			return confJDO;
		} finally {
			pm.close();
		}
	}

}

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

import java.util.Date;

import javax.jdo.PersistenceManager;

import nl.welteninstituut.tel.la.jdomanager.PMF;

import com.google.appengine.api.datastore.KeyFactory;

/**
 * @author Harrie Martens
 *
 */
public class OauthServiceAccountManager {

	public static void addOauthServiceAccount(final int serviceId, final String accountId, final String accessToken,
			final String refreshToken, final Date lastSynced, final String primaryAccount) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			OauthServiceAccount account = new OauthServiceAccount();
			account.setServiceId(serviceId);
			account.setAccountId(accountId);
			account.setAccessToken(accessToken);
			account.setRefreshToken(refreshToken);
			account.setLastSynced(lastSynced);
			account.setPrimaryAccount(primaryAccount);
			account.setKey();
			pm.makePersistent(account);
		} finally {
			pm.close();
		}
	}

	public static void updateOauthServiceAccount(final OauthServiceAccount account) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			OauthServiceAccount dbAccount = pm.getObjectById(OauthServiceAccount.class,
					KeyFactory.createKey(OauthServiceAccount.class.getSimpleName(), account.getKey()));
			dbAccount.setAccessToken(account.getAccessToken());
			dbAccount.setRefreshToken(account.getRefreshToken());
			dbAccount.setLastSynced(account.getLastSynced());
			pm.makePersistent(dbAccount);
		} finally {
			pm.close();
		}
	}

}

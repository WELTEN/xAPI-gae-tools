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
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.joda.time.DateTime;

import nl.welteninstituut.tel.la.importers.fitbit.FitbitTask;
import nl.welteninstituut.tel.la.jdomanager.PMF;
import nl.welteninstituut.tel.util.StringPool;

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

	public static OauthServiceAccount getAccount(final int serviceId, final String accountId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			return pm.getObjectById(
					OauthServiceAccount.class,
					KeyFactory.createKey(OauthServiceAccount.class.getSimpleName(), Integer.toString(serviceId)
							+ StringPool.COLON + accountId));
		} catch (JDOObjectNotFoundException ex) {
			return null;
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static List<OauthServiceAccount> getAccountsForService(final int serviceId) {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(OauthServiceAccount.class);
		q.setFilter("serviceId == serviceIdParam");
		q.declareParameters("Integer serviceIdParam");

		try {
			return (List<OauthServiceAccount>) q.execute(serviceId);
		} finally {
			pm.close();
		}

	}

}

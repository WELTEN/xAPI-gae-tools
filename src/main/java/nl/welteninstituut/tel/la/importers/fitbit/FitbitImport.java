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
package nl.welteninstituut.tel.la.importers.fitbit;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import nl.welteninstituut.tel.la.importers.Importer;
import nl.welteninstituut.tel.la.jdomanager.PMF;
import nl.welteninstituut.tel.oauth.jdo.AccountJDO;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccount;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccountManager;

import org.joda.time.DateTime;

/**
 * @author Stefaan Ternier
 * @author Harrie Martens
 *
 */
public class FitbitImport extends Importer {

	@Override
	public void startImport() {
		System.out.println("FITBIT importer");

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(OauthServiceAccount.class);
		q.setFilter("serviceId == serviceIdParam");
		q.declareParameters("Integer serviceIdParam");

		try {
			@SuppressWarnings("unchecked")
			List<OauthServiceAccount> result = (List<OauthServiceAccount>) q.execute(AccountJDO.FITBITCLIENT);

			for (OauthServiceAccount account : result) {
				
				// TODO remove reset of lastSynced
				// reset lastSynced so fitbit connection can be tested.
				account.setLastSynced(new DateTime("2015-10-30T08:08").toDate());
				OauthServiceAccountManager.updateOauthServiceAccount(account);
				// TODO remove till here
				
				// create a fitbit task per user
				FitbitTask.scheduleTask(new FitbitTask(account.getAccountId()));
			}
		} finally {
			pm.close();
		}

	}
}

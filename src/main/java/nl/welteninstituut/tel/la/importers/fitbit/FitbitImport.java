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

import nl.welteninstituut.tel.la.importers.Importer;
import nl.welteninstituut.tel.oauth.jdo.AccountJDO;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccount;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccountManager;

/**
 * The FitbitImport imports heartrate and stepcount data for all registered Fitbit
 * users. The actual import is done per user in a scheduled task.
 * 
 * @author Stefaan Ternier
 * @author Harrie Martens
 *
 */
public class FitbitImport extends Importer {

	@Override
	public void startImport() {
		for (OauthServiceAccount account : OauthServiceAccountManager.getAccountsForService(AccountJDO.FITBITCLIENT)) {

			// create a fitbit task per user
			FitbitTask.scheduleTask(new FitbitTask(account.getAccountId()));
		}

	}
}

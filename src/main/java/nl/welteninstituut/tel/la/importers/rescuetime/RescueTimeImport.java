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
package nl.welteninstituut.tel.la.importers.rescuetime;

import nl.welteninstituut.tel.la.importers.Importer;
import nl.welteninstituut.tel.oauth.jdo.AccountJDO;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccount;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccountManager;

import org.joda.time.DateTime;

/**
 * @author Stefaan Ternier
 * @author Harrie Martens
 *
 */
public class RescueTimeImport extends Importer {

    public void startImport(){
        System.out.println("start heavy lifting");
        
		for (OauthServiceAccount account : OauthServiceAccountManager.getAccountsForService(AccountJDO.RESCUETIMECLIENT)) {

			// TODO remove reset of lastSynced
			// reset lastSynced so fitbit connection can be tested.
			account.setLastSynced(new DateTime("2015-11-05T08:08").toDate());
			OauthServiceAccountManager.updateOauthServiceAccount(account);
			// TODO remove till here
			
			System.out.println("rescue time user: " + account.getAccountId());

			// create a fitbit task per user
			// FitbitTask.scheduleTask(new FitbitTask(account.getAccountId()));
		}

	}

}

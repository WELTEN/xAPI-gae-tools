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

import java.util.logging.Logger;

import nl.welteninstituut.tel.la.importers.ImportTask;
import nl.welteninstituut.tel.la.importers.Importer;
import nl.welteninstituut.tel.oauth.jdo.AccountJDO;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccount;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccountManager;

/**
 * The RescueTimeImport imports RescueTime data for all registered RescueTime
 * users. The actual import is done per user in a scheduled task.
 * 
 * @author Stefaan Ternier
 * @author Harrie Martens
 *
 */
public class RescueTimeImport extends Importer {
	
    private static final Logger LOG = Logger.getLogger(RescueTimeImport.class.getName());

    public void startImport() {
    	LOG.info("Importing RescueTime data");
    	
		for (OauthServiceAccount account : OauthServiceAccountManager.getAccountsForService(AccountJDO.RESCUETIMECLIENT)) {

	    	LOG.info("Importing RescueTime data for " + account.getAccountId());
	    	
			// create a RescueTime task per user
			ImportTask.scheduleTask(new RescueTimeTask(account.getAccountId()));
		}

	}

}

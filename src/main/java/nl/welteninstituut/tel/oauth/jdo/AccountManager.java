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

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import nl.welteninstituut.tel.la.jdomanager.PMF;
//import org.celstec.arlearn2.beans.account.Account;

import com.google.appengine.api.datastore.KeyFactory;

/**
 * @author Stefaan Ternier
 * @author Harrie Martens
 *
 */
public class AccountManager {

	public static AccountJDO addAccount(String localID, int accountType, String email, String given_name,
			String family_name, String name, String picture, boolean allowTrackLocation) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			boolean isNew = false;
			AccountJDO account = null;

			try {
				account = pm.getObjectById(AccountJDO.class,
						KeyFactory.createKey(AccountJDO.class.getSimpleName(), accountType + ":" + localID));
			} catch (JDOObjectNotFoundException ex) {
				account = new AccountJDO();
				isNew = true;
			}

			account.setLocalId(localID);
			account.setAccountType(accountType);
			account.setUniqueId();
			account.setEmail(email);
			account.setGiven_name(given_name);
			account.setFamily_name(family_name);
			account.setName(name);
			account.setPicture(picture);
			account.setLastModificationDate(System.currentTimeMillis());
			account.setAllowTrackLocation(allowTrackLocation);
			if (isNew) {
				account.setAccountLevel(AccountJDO.USER);
			}
			pm.makePersistent(account);
			return account;
		} finally {
			pm.close();
		}
	}

	public static AccountJDO getAccount(final String userName) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			return pm.getObjectById(AccountJDO.class,
					KeyFactory.createKey(AccountJDO.class.getSimpleName(), userName));
		} catch (JDOObjectNotFoundException ex) {
			return null;
		} finally {
			pm.close();
		}

	}

}

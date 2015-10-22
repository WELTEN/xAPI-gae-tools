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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import nl.welteninstituut.tel.la.importers.Importer;
import nl.welteninstituut.tel.la.jdomanager.PMF;
import nl.welteninstituut.tel.oauth.jdo.AccountJDO;
import nl.welteninstituut.tel.oauth.jdo.OauthServiceAccount;

/**
 * @author Stefaan Ternier
 * @author Harrie Martens
 *
 */
public class FitbitImport  extends Importer {

	@Override
	public void startImport() {
		System.out.println("FITBIT importer");
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(OauthServiceAccount.class);
		q.setFilter("serviceId == serviceIdParam");
		q.declareParameters("Integer serviceIdParam");
		
		try {
			@SuppressWarnings("unchecked")
			List<OauthServiceAccount> result = (List<OauthServiceAccount>)q.execute(AccountJDO.FITBITCLIENT);
			
			for (OauthServiceAccount account : result) {
				System.out.println("---->>>> " + account.getAccountId());
				fetchData(account);
			}
		} finally {
			q.closeAll();
		}
		
	}
	
	private void fetchData(final OauthServiceAccount account) {
		
		String result;
		try {
			result = readURL(new URL("https://api.fitbit.com/1/user/" + account.getAccountId() + "/activities/heart/date/2015-10-21/1d/1sec/time/10:00/10:05.json"),
					account.getAccessToken());
			System.out.println("** -> " + result);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		
	}
	
	protected String readURL(URL url, String token) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// InputStream is = url.openStream();

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Authorization", "Bearer " + token);

		InputStream is = connection.getInputStream();
		int r;
		while ((r = is.read()) != -1) {
			baos.write(r);
		}
		return new String(baos.toByteArray());
	}

	
	
	
	
	
	
}

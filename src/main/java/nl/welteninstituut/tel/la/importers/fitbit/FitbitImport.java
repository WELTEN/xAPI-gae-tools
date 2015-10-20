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

import org.codehaus.jettison.json.JSONObject;

import nl.welteninstituut.tel.la.importers.Importer;
import nl.welteninstituut.tel.la.jdomanager.PMF;
import nl.welteninstituut.tel.oauth.jdo.AccountJDO;
import nl.welteninstituut.tel.oauth.jdo.AccountManager;
import nl.welteninstituut.tel.oauth.jdo.UsersLoggedIn;
import nl.welteninstituut.tel.util.StringPool;

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
		Query q = pm.newQuery(UsersLoggedIn.class);
		q.setFilter("username >= \"7:\"");
		q.setFilter("username < \"7:\uFFFD\"");
		
		try {
			List<UsersLoggedIn> result = (List<UsersLoggedIn>)q.execute();
			
			for (UsersLoggedIn uli : result) {
				System.out.println("---->>>> " + uli.getUsername());
				fetchData(uli);
			}
		} finally {
			q.closeAll();
		}
		
	}
	
	private void fetchData(final UsersLoggedIn uli) {
		
		String userid = uli.getUsername().substring(2);
		
		String result;
		try {
			result = readURL(new URL("https://api.fitbit.com/1/user/" + userid + "/activities/heart/date/today/1d.json"),
					uli.getAuthToken());
			System.out.println("** -> " + result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
